package com.sunwell.pos.mobile.fragment;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.sunwell.pos_mobile.R;
import com.sunwell.pos.mobile.dialog.ChoosePaymentMethodDialogFragment;
import com.sunwell.pos.mobile.dialog.InvoiceLineDialogFragment;
import com.sunwell.pos.mobile.dialog.PaymentDialogFragment;
import com.sunwell.pos.mobile.dialog.SplitBillDialogFragment;
import com.sunwell.pos.mobile.model.PaymentMethodObj;
import com.sunwell.pos.mobile.model.Product;
import com.sunwell.pos.mobile.model.SalesInvoice;
import com.sunwell.pos.mobile.model.SalesInvoiceLine;
import com.sunwell.pos.mobile.model.SalesPayment;
import com.sunwell.pos.mobile.service.InvoiceService;
import com.sunwell.pos.mobile.service.LoginService;
import com.sunwell.pos.mobile.service.ProductService;
import com.sunwell.pos.mobile.util.ImageDownloader;
import com.sunwell.pos.mobile.util.RecycleViewAdapter;
import com.sunwell.pos.mobile.util.ResultListener;
import com.sunwell.pos.mobile.util.ResultWatcher;
import com.sunwell.pos.mobile.util.Util;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * Created by sunwell on 10/18/17.
 */

public class InvoiceDetailFragment extends Fragment {
    private static final String PROGRESS_FETCH_PMO = "progressFetchPMO";
    private SalesInvoice argSI;
    private PaymentMethodObj paymentMethod;
    private PaymentListener paymentListener;
    private LayoutInflater layoutInflater ;
    private LinearLayout panelItem;
    private TextView textTotal ;
    private TextView textDisc ;
    private TextView textService ;
    private TextView textTax ;
    private TextView textSubTotal ;
    private TextView textPaymentMethod ;
    private Button btnClose ;

    private static String SALES_INVOICE = "sales_invoice";

    public static InvoiceDetailFragment newInstance(SalesInvoice _si) {
        Bundle bundle = new Bundle();
        bundle.putSerializable(InvoiceDetailFragment.SALES_INVOICE, _si);
        InvoiceDetailFragment fragment = new InvoiceDetailFragment();
        fragment.setArguments(bundle);
        return fragment;
    }


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        try {
            super.onCreate(savedInstanceState);
            Bundle arguments = getArguments();
            if(savedInstanceState != null)
                paymentMethod = (PaymentMethodObj)savedInstanceState.getSerializable("paymentMethod");
            argSI = arguments != null ? (SalesInvoice) arguments.get(InvoiceDetailFragment.SALES_INVOICE) : null ;
            Log.d(Util.APP_TAG, "SI: " + argSI);
        }
        catch(Exception e) {
            Log.d(Util.APP_TAG, "Error: " + e.getMessage());
            e.printStackTrace();
            Toast.makeText(getActivity(), R.string.error_product_list, Toast.LENGTH_SHORT).show();
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater _inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        try {
            super.onCreateView(_inflater, container, savedInstanceState);
            Log.d(Util.APP_TAG, "OCV CALLED");
            View v = _inflater.inflate(R.layout.invoice_detail, container, false);
            layoutInflater = _inflater;
            TextView textInv = (TextView) v.findViewById(R.id.text_no_invoice);
            Button btnPaymentMethod = (Button) v.findViewById(R.id.button_payment_method);
            Button btnMakePayment = (Button) v.findViewById(R.id.button_make_payment);
            Button btnSplitBill = (Button) v.findViewById(R.id.button_split_bill);
            btnClose = (Button) v.findViewById(R.id.button_close);
            textSubTotal = (TextView) v.findViewById(R.id.text_subtotal_amount);
            textDisc = (TextView) v.findViewById(R.id.text_discount_amount);
            textService = (TextView) v.findViewById(R.id.text_service_amount);
            textTax = (TextView) v.findViewById(R.id.text_tax_amount);
            textTotal = (TextView) v.findViewById(R.id.text_total_amount);
            textPaymentMethod = (TextView) v.findViewById(R.id.text_payment_method);
            panelItem = (LinearLayout)v.findViewById(R.id.panel_item);

            textInv.setText(argSI.getNoInvoice());
            if(argSI.getSalesInvoiceLines() != null && argSI.getSalesInvoiceLines().size() > 0) {
                for(SalesInvoiceLine sil : argSI.getSalesInvoiceLines()) {
                    addInvoiceLineRow(sil);
                }
                calculateTotal();
            }

            btnPaymentMethod.setOnClickListener(
                    new View.OnClickListener()
                    {
                        @Override
                        public void onClick(View v)
                        {
                            try {
                                ChoosePaymentMethodDialogFragment dialog = new ChoosePaymentMethodDialogFragment();
                                dialog.setTargetFragment(InvoiceDetailFragment.this, Util.REQUEST_CODE_PICK);
                                dialog.show(getFragmentManager(), "payment_method");
                            }
                            catch(Exception e) {
                                Log.d(Util.APP_TAG, "Error: " + e.getMessage());
                                e.printStackTrace();
                                Toast.makeText(getActivity(), R.string.internal_server_error, Toast.LENGTH_SHORT).show();
                            }
                        }
                    }
            );

            btnMakePayment.setOnClickListener(
                    new View.OnClickListener()
                    {
                        @Override
                        public void onClick(View v)
                        {
                            try {
                                PaymentDialogFragment dialog = PaymentDialogFragment.newInstance(argSI, paymentMethod);
                                dialog.setTargetFragment(InvoiceDetailFragment.this, Util.REQUEST_CODE_CREATE);
                                dialog.show(getFragmentManager(), "payment");
                            }
                            catch(Exception e) {
                                Log.d(Util.APP_TAG, "Error: " + e.getMessage());
                                e.printStackTrace();
                                Toast.makeText(getActivity(), R.string.internal_server_error, Toast.LENGTH_SHORT).show();
                            }
                        }
                    }
            );

            btnSplitBill.setOnClickListener(
                    new View.OnClickListener()
                    {
                        @Override
                        public void onClick(View v)
                        {
                            try {
                                SplitBillDialogFragment dialog = SplitBillDialogFragment.newInstance(argSI);
                                dialog.setTargetFragment(InvoiceDetailFragment.this, Util.REQUEST_CODE_ADD);
                                dialog.show(getFragmentManager(), "splitBill");
                            }
                            catch(Exception e) {
                                Log.d(Util.APP_TAG, "Error: " + e.getMessage());
                                e.printStackTrace();
                                Toast.makeText(getActivity(), R.string.internal_server_error, Toast.LENGTH_SHORT).show();
                            }
                        }
                    }
            );

            btnClose.setOnClickListener(
                    new View.OnClickListener()
                    {
                        @Override
                        public void onClick(View v)
                        {
//                            if (getTargetFragment() != null) {
//                                Fragment targetFragment = getTargetFragment();
////                                Intent intent = new Intent();
////                                intent.putExtra("salesPayment", salesPayment);
//                                targetFragment.onActivityResult(getTargetRequestCode(), Util.RESULT_CODE_CANCELED, null);
//                            }

                            if(paymentListener != null) {
                                try {
                                    paymentListener.onPaymentMade(null);
                                }
                                catch(Exception e) {
                                    Log.d(Util.APP_TAG,"Error: " + e.getMessage());
                                    Toast.makeText(getActivity(), R.string.internal_server_error, Toast.LENGTH_SHORT).show();
                                }
                            }
                        }
                    }
            );

            if(InvoiceService.paymentMethods == null) {
                InvoiceService.fetchPaymentMethods(
                        new ResultWatcher<List<PaymentMethodObj>>()
                        {
                            @Override
                            public void onResult(Object source, List<PaymentMethodObj> result) throws Exception
                            {
                                Util.stopDialog(PROGRESS_FETCH_PMO);
                                if (result != null && result.size() > 0) {
                                    paymentMethod = result.get(0);
                                    textPaymentMethod.setText(paymentMethod.getName());
                                }
                            }

                            @Override
                            public void onError(Object source, int errCode) throws Exception
                            {
                                Util.stopDialog(PROGRESS_FETCH_PMO);
                                Toast.makeText(getActivity(), R.string.internal_server_error, Toast.LENGTH_SHORT).show();
                            }
                        }
                );
                Util.showDialog(getFragmentManager(), PROGRESS_FETCH_PMO);
            }
            else {
                paymentMethod = InvoiceService.paymentMethods.get(0);
                textPaymentMethod.setText(paymentMethod.getName());
            }
            return v;
        }
        catch(Exception e) {
            Log.d(Util.APP_TAG, "Error: " + e.getMessage());
            e.printStackTrace();
            Toast.makeText(getActivity(), R.string.internal_server_error, Toast.LENGTH_SHORT).show();
            return null;
        }
    }

    @Override
    public void onActivityResult(int _requestCode, int _resultCode, Intent data) {
        try {
            if (_requestCode == Util.REQUEST_CODE_PICK) {
                paymentMethod = (PaymentMethodObj) data.getSerializableExtra("paymentMethod");
                textPaymentMethod.setText(paymentMethod.getName());
            }
            else if (_requestCode == Util.REQUEST_CODE_ADD) {
                if (_resultCode == Util.RESULT_CODE_SUCCESS) {
                    if(argSI.getSalesInvoiceLines().size() > 0) {
                        for( ; panelItem.getChildCount() > 1; ) {
//                            Log.d(Util.APP_TAG, " REMOVING SIL: " + i);
                            panelItem.removeViewAt(1);
                        }
                        for(SalesInvoiceLine sil : argSI.getSalesInvoiceLines()) {
                            Log.d(Util.APP_TAG, "ADDING SIL: " + sil.getProduct().getName());
                            addInvoiceLineRow(sil);
                        }
                        calculateTotal();
                    }
                    Toast.makeText(getActivity(), R.string.success_split_bill, Toast.LENGTH_SHORT).show();
                }
                else {
                    int errCode = data.getIntExtra("errorCode", 999);
                    Log.d(Util.APP_TAG, "ERROR CODE: " + errCode);
                    Toast.makeText(getActivity(), R.string.fail_split_bill, Toast.LENGTH_SHORT).show();
                }
            }
            else if (_requestCode == Util.REQUEST_CODE_CREATE) {
                if (_resultCode == Util.RESULT_CODE_SUCCESS) {
                    SalesPayment salesPayment = (SalesPayment) data.getSerializableExtra("salesPayment");
//                    if (getTargetFragment() != null) {
//                        Fragment targetFragment = getTargetFragment();
//                        Intent intent = new Intent();
//                        intent.putExtra("salesPayment", salesPayment);
//                        targetFragment.onActivityResult(getTargetRequestCode(), Util.RESULT_CODE_SUCCESS, intent);
//                    }
                    if (paymentListener != null) {
                        paymentListener.onPaymentMade(salesPayment);
                    }
                    else
                        Toast.makeText(getActivity(), R.string.success_create_sales_payment, Toast.LENGTH_SHORT).show();
                }
                else {
                    int errCode = data.getIntExtra("errorCode", 999);
                    Log.d(Util.APP_TAG, "ERROR CODE: " + errCode);
                    Toast.makeText(getActivity(), R.string.fail_create_sales_payment, Toast.LENGTH_SHORT).show();
                }
            }
        }
        catch(Exception e) {
            Log.d(Util.APP_TAG, "Error: " + e.getMessage());
            e.printStackTrace();
            Toast.makeText(getActivity(), R.string.internal_server_error, Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState)
    {
        super.onSaveInstanceState(outState);

        if(paymentMethod != null)
            outState.putSerializable("paymentMethod", paymentMethod);
    }

    public void setPaymentListener(PaymentListener _list) {
        paymentListener = _list;
    }


    public void addInvoiceLine(SalesInvoiceLine _sil)
    {
        addInvoiceLineRow(_sil);
        calculateTotal();
    }

    private void addInvoiceLineRow(SalesInvoiceLine _sil) {
        View invoiceLine = layoutInflater.inflate(R.layout.invoice_line, panelItem, false);
        TextView textName = (TextView)invoiceLine.findViewById(R.id.text_name);
        TextView textQty = (TextView)invoiceLine.findViewById(R.id.text_qty);
        TextView textDisc = (TextView)invoiceLine.findViewById(R.id.text_disc);
        TextView textTotal = (TextView)invoiceLine.findViewById(R.id.text_total);
        textName.setText(_sil.getProduct().getName());
        textQty.setText(String.valueOf(_sil.getQty()));
        textDisc.setText(String.valueOf(_sil.getDiscValue()));
        textTotal.setText(String.valueOf(_sil.getSubTotal()));
        panelItem.addView(invoiceLine);
    }

    private void calculateTotal() {
        double subTotal = 0;
        double total = 0;
        double disc = 0;
        double tax = 0;
        double svc = 0;

        if(argSI.getSalesInvoiceLines() != null && argSI.getSalesInvoiceLines().size() > 0) {
            for (SalesInvoiceLine sil : argSI.getSalesInvoiceLines()) {
                subTotal += sil.getSubTotal();
                disc += sil.getRealDiscValue();
                Log.d(Util.APP_TAG, "ST: " + subTotal + "D: " + disc);
            }
        }
//        subTotal =
        total = subTotal - disc;
        svc = 0.05 * total;
        total = total + svc ;
        tax = 0.10 * total;
        total = Math.round((total + svc + tax) * 100.0) / 100;
        subTotal = Math.round(subTotal * 100.0) / 100;
        disc = Math.round(disc * 100.0) / 100;
        svc = Math.round(svc * 100) / 100;
        tax = Math.round( tax * 100) / 100;

        textTotal.setText(String.valueOf(total));
        textDisc.setText(String.valueOf(disc));
        textSubTotal.setText(String.valueOf(subTotal));
        textService.setText(String.valueOf(svc));
        textTax.setText(String.valueOf(tax));
    }

    public static interface PaymentListener {
        public void onPaymentMade(SalesPayment _payment) throws Exception;
        public void onError(int _errCode) throws Exception;
    }


}
