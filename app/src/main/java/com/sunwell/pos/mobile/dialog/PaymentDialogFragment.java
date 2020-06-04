package com.sunwell.pos.mobile.dialog;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.sunwell.pos_mobile.R;
import com.sunwell.pos.mobile.model.PaymentMethod;
import com.sunwell.pos.mobile.model.PaymentMethodObj;
import com.sunwell.pos.mobile.model.SalesInvoice;
import com.sunwell.pos.mobile.model.SalesPayment;
import com.sunwell.pos.mobile.service.InvoiceService;
import com.sunwell.pos.mobile.util.ResultListener;
import com.sunwell.pos.mobile.util.ResultWatcher;
import com.sunwell.pos.mobile.util.Util;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by sunwell on 10/19/17.
 */

public class PaymentDialogFragment extends DialogFragment {

    private static final String PROGRESS_CREATE_PAYMENT = "progressCreatePayment";
    private static String PAYMENT_METHOD = "payment_method";
    private static String SALES_INVOICE = "sales_invoice";
    private SalesInvoice salesInvoice;
    private PaymentMethodObj method;
//    private ResultListener<SalesPayment> dialogListener ;
    private TextView textChangeAmount;
    private TextView textTotal ;
    private TextView textDisc ;
    private TextView textService ;
    private TextView textTax ;
    private TextView textSubTotal ;
    private EditText inputMemo ;
    private EditText inputPayment ;
    private Spinner spinnerPaymentMethod;


    public static PaymentDialogFragment newInstance(SalesInvoice _si, PaymentMethodObj _pm) {
        Bundle bundle = new Bundle();
        bundle.putSerializable(SALES_INVOICE, _si);
        bundle.putSerializable(PAYMENT_METHOD, _pm);
        PaymentDialogFragment dialog = new PaymentDialogFragment();
        dialog.setArguments(bundle);
        return dialog;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        try {
            final Dialog dialog = new Dialog(getActivity());
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            dialog.setContentView(R.layout.payment_dialog);
            Button btnClose = (Button) dialog.findViewById(R.id.button_close);
            final Button btnAdd = (Button) dialog.findViewById(R.id.button_create);
            textChangeAmount = (TextView) dialog.findViewById(R.id.text_change_amount);
            textSubTotal = (TextView) dialog.findViewById(R.id.text_subtotal_amount);
            textDisc = (TextView) dialog.findViewById(R.id.text_discount_amount);
            textService = (TextView) dialog.findViewById(R.id.text_service_amount);
            textTax = (TextView) dialog.findViewById(R.id.text_tax_amount);
            textTotal = (TextView) dialog.findViewById(R.id.text_total_amount);
            inputMemo = (EditText) dialog.findViewById(R.id.input_memo);
            inputPayment = (EditText) dialog.findViewById(R.id.input_payment);
            spinnerPaymentMethod = (Spinner) dialog.findViewById(R.id.spinner_payment);

//            dialog.setTitle(R.string.payment);

            Bundle arguments = getArguments();
            salesInvoice = arguments != null ? (SalesInvoice) arguments.get(SALES_INVOICE) : null;
            method = arguments != null ? (PaymentMethodObj) arguments.get(PAYMENT_METHOD) : null;

            textSubTotal.setText(String.valueOf(Math.round(salesInvoice.getSubTotal() * 100.0) / 100.0));
            textDisc.setText(String.valueOf(Math.round(salesInvoice.getTotalDiscount() * 100.0) / 100.0));
            textService.setText(String.valueOf(Math.round(salesInvoice.getServiceAmount() * 100.0) / 100.0));
            textTax.setText(String.valueOf(Math.round(salesInvoice.getTaxAmount() * 100.0) / 100.0));
            textTotal.setText(String.valueOf(Math.round(salesInvoice.getTotal() * 100.0) / 100.0));

            final List<PaymentMethodObj> list = new ArrayList<>();
            list.add(new PaymentMethodObj());
            spinnerPaymentMethod.setEnabled(false);
            if (InvoiceService.paymentMethods != null) {
                for (PaymentMethodObj pm : InvoiceService.paymentMethods) {
                    list.add(pm);
                }
                Util.fillSpinner(spinnerPaymentMethod, list, PaymentMethodObj.class, getActivity());
                if (method != null)
                    spinnerPaymentMethod.setSelection(((ArrayAdapter) spinnerPaymentMethod.getAdapter()).getPosition(method));
            } else {
                    InvoiceService.fetchPaymentMethods(
                            new ResultWatcher<List<PaymentMethodObj>>()
                            {
                                @Override
                                public void onResult(Object source, List<PaymentMethodObj> result) throws Exception
                                {
                                    if (result != null) {
                                        for (PaymentMethodObj pm : result) {
                                            list.add(pm);
                                        }
                                        Util.fillSpinner(spinnerPaymentMethod, list, PaymentMethodObj.class, getActivity());
                                        if (method != null)
                                            spinnerPaymentMethod.setSelection(((ArrayAdapter) spinnerPaymentMethod.getAdapter()).getPosition(method));
                                    }
                                }

                                @Override
                                public void onError(Object source, int errCode) throws Exception
                                {
                                    super.onError(source, errCode);
                                }
                            }
                    );

            }

            btnClose.setOnClickListener(
                    new View.OnClickListener()
                    {
                        @Override
                        public void onClick(View v)
                        {
                            PaymentDialogFragment.this.dismiss();
                        }
                    }
            );

            btnAdd.setOnClickListener(
                    new View.OnClickListener()
                    {
                        @Override
                        public void onClick(View v)
                        {
                            try {

                                if(!validateInput())
                                    return;

                                double payAmount = Double.valueOf(inputPayment.getText().toString());
                                double invoiceAmount = salesInvoice.getTotal();
                                double changeAmount = payAmount - invoiceAmount;
                                if (changeAmount < 0) {
                                    Toast.makeText(getActivity(), R.string.payment_amount_not_valid, Toast.LENGTH_SHORT).show();
                                }
                                changeAmount = Math.round(changeAmount * 100.0) / 100.0;
                                SalesPayment sp = new SalesPayment();
                                sp.setParent(new SalesInvoice());
                                sp.getParent().setSystemId(salesInvoice.getSystemId());
                                sp.setPaymentMethod(new PaymentMethod());
                                sp.getPaymentMethod().setSystemId(method.getParent().getSystemId());
                                sp.setAmount(payAmount);

                                ResultWatcher<SalesPayment> listener = new ResultWatcher<SalesPayment>()
                                {
                                    @Override
                                    public void onResult(Object source, SalesPayment sp) throws Exception
                                    {
                                        Util.stopDialog(PROGRESS_CREATE_PAYMENT );
                                        if (getTargetFragment() != null) {
                                            Intent intent = new Intent();
                                            intent.putExtra("salesPayment", sp);
                                            getTargetFragment().onActivityResult(getTargetRequestCode(), Util.RESULT_CODE_SUCCESS, intent);
                                        }
                                        PaymentDialogFragment.this.dismiss();
                                    }

                                    @Override
                                    public void onError(Object source, int _errCode) throws Exception
                                    {
                                        Util.stopDialog(PROGRESS_CREATE_PAYMENT );
                                        if (getTargetFragment() != null) {
                                            Intent intent = new Intent();
                                            intent.putExtra("errorCode", _errCode);
                                            Log.d(Util.APP_TAG, " onError: " + _errCode);
                                            getTargetFragment().onActivityResult(getTargetRequestCode(), Util.RESULT_CODE_FAILED, intent);
                                        }
                                        PaymentDialogFragment.this.dismiss();
                                    }
                                };

                                InvoiceService.addSalesPayment(listener, sp);
                                Util.showDialog(getFragmentManager(), PROGRESS_CREATE_PAYMENT);
                            }
                            catch(Exception e) {
                                Log.d(Util.APP_TAG, "Error: " + e.getMessage());
                                e.printStackTrace();
                                Toast.makeText(getActivity(), R.string.internal_server_error, Toast.LENGTH_SHORT).show();
                            }
                        }
                    }
            );

            return dialog;
        }
        catch(Exception e) {
            Log.d(Util.APP_TAG, "Error: " + e.getMessage());
            e.printStackTrace();
            Toast.makeText(getActivity(), R.string.internal_server_error, Toast.LENGTH_SHORT).show();
            return null;
        }
    }

    private Boolean validateInput() {
        if(inputPayment.getText() == null || inputPayment.getText().toString().length() <= 0) {
            Toast.makeText(getActivity(), R.string.amount_field_cannot_be_empty, Toast.LENGTH_SHORT).show();
            return false;
        }

        if(spinnerPaymentMethod.getSelectedItemPosition() <= 0) {
            Toast.makeText(getActivity(), R.string.payment_field_cannot_be_empty, Toast.LENGTH_SHORT).show();
            return false;
        }

        return true;
    }
}
