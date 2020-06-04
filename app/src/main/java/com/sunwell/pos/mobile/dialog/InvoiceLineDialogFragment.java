package com.sunwell.pos.mobile.dialog;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.sunwell.pos_mobile.R;
import com.sunwell.pos.mobile.model.Product;
import com.sunwell.pos.mobile.model.SalesInvoice;
import com.sunwell.pos.mobile.model.SalesInvoiceLine;
import com.sunwell.pos.mobile.service.InventoryService;
import com.sunwell.pos.mobile.service.InvoiceService;
import com.sunwell.pos.mobile.util.ResultWatcher;
import com.sunwell.pos.mobile.util.Util;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by sunwell on 10/19/17.
 */

public class InvoiceLineDialogFragment extends DialogFragment {

    private static final String PROGRESS_ADD_INVOICE_LINE = "progressAddInvoiceLine";
    private static final String PROGRESS_FETCH_WAREHOUSE = "progressFetchWarehouse";
    private static final String SI_LINE = "si_line";
    private static final String SALES_INVOICE = "sales_invoice";
    private static final String PRODUCT = "product";
    private int discType = SalesInvoiceLine.DISC_TYPE_PERCENTAGE;
    private SalesInvoice argInv ;
    private Product argProduct ;

    private EditText inputPrice ;
    private EditText inputQty ;
    private EditText inputDisc ;
    private Spinner spinnerMetric ;
    private Button btnPlusQty ;
    private Button btnNegQty ;
    private Button btnDiscPercentage ;
    private Button btnDiscMoney ;
    private Button btnClose ;
    private Button btnAdd ;

    public static InvoiceLineDialogFragment newInstance(SalesInvoiceLine _line) {
        Bundle bundle = new Bundle();
        bundle.putSerializable(InvoiceLineDialogFragment.SI_LINE, _line);
        InvoiceLineDialogFragment dialog = new InvoiceLineDialogFragment();
        dialog.setArguments(bundle);
        return dialog;
    }

    public static InvoiceLineDialogFragment newInstance(SalesInvoice _inv, Product _p) {
        Bundle bundle = new Bundle();
        bundle.putSerializable(InvoiceLineDialogFragment.SALES_INVOICE, _inv);
        bundle.putSerializable(InvoiceLineDialogFragment.PRODUCT, _p);
        InvoiceLineDialogFragment dialog = new InvoiceLineDialogFragment();
        dialog.setArguments(bundle);
        return dialog;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        if(savedInstanceState != null)
            discType = (Integer) savedInstanceState.getInt("discType");
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        try {
            final Dialog dialog = new Dialog(getActivity());
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            dialog.setContentView(R.layout.invoice_line_dialog);
            inputPrice = (EditText) dialog.findViewById(R.id.input_price);
            inputQty = (EditText) dialog.findViewById(R.id.input_qty);
            inputDisc = (EditText) dialog.findViewById(R.id.input_discount);
            spinnerMetric = (Spinner) dialog.findViewById(R.id.spinner_metric);
            btnPlusQty = (Button) dialog.findViewById(R.id.button_plus_qty);
            btnNegQty = (Button) dialog.findViewById(R.id.button_neg_qty);
            btnDiscPercentage = (Button) dialog.findViewById(R.id.button_percentage);
            btnDiscMoney = (Button) dialog.findViewById(R.id.button_money);
            btnClose = (Button) dialog.findViewById(R.id.button_close);
            btnAdd = (Button) dialog.findViewById(R.id.button_add_line);

            Bundle arguments = getArguments();
            final SalesInvoiceLine argLine = arguments != null ? (SalesInvoiceLine) arguments.get(InvoiceLineDialogFragment.SI_LINE) : null;
            argInv = arguments != null ? (SalesInvoice) arguments.get(InvoiceLineDialogFragment.SALES_INVOICE) : null;
            argProduct = arguments != null ? (Product) arguments.get(InvoiceLineDialogFragment.PRODUCT) : null;

            inputPrice.setText(String.valueOf(argProduct.getPrice()));
            spinnerMetric.setSelection(((ArrayAdapter)spinnerMetric.getAdapter()).getPosition(argProduct.getMetric()));

            if (argLine != null) {
                inputPrice.setText(String.valueOf(argLine.getPrice()));
                inputQty.setText(String.valueOf(argLine.getQty()));
                inputDisc.setText(String.valueOf(argLine.getDiscValue()));
                spinnerMetric.setSelection(((ArrayAdapter)spinnerMetric.getAdapter()).getPosition(argLine.getMetric()));
                btnAdd.setText(R.string.edit_invoice_item);
                dialog.setTitle(R.string.edit_invoice_item);
            }

//            dialog.setTitle(R.string.add_invoice_item);

            btnPlusQty.setOnClickListener(
                    new View.OnClickListener()
                    {
                        @Override
                        public void onClick(View v)
                        {
                            try {
                                if (inputQty.getText() == null || inputQty.getText().toString().length() <= 0)
                                    return;

                                Double val = Double.valueOf(inputQty.getText().toString());
                                val = val + 1;
                                inputQty.setText(String.valueOf(val));
                            }
                            catch(Exception e) {
                                Log.d(Util.APP_TAG, "Error: " + e.getMessage());
                                e.printStackTrace();
                                Toast.makeText(getActivity(), R.string.internal_server_error, Toast.LENGTH_SHORT).show();
                            }
                        }
                    }
            );

            btnNegQty.setOnClickListener(
                    new View.OnClickListener()
                    {
                        @Override
                        public void onClick(View v)
                        {
                            try {
                                if (inputQty.getText() == null || inputQty.getText().toString().length() <= 0)
                                    return;

                                Double val = Double.valueOf(inputQty.getText().toString());
                                val = val - 1;
                                if (val < 1)
                                    return;

                                inputQty.setText(String.valueOf(val));
                            }
                            catch(Exception e) {
                                Log.d(Util.APP_TAG, "Error: " + e.getMessage());
                                e.printStackTrace();
                                Toast.makeText(getActivity(), R.string.internal_server_error, Toast.LENGTH_SHORT).show();
                            }
                        }
                    }
            );

            inputQty.setText("1");

            btnDiscPercentage.setTextColor(Color.parseColor("#f6f7f7"));
            btnDiscPercentage.setBackgroundColor(Color.parseColor("#0f7858"));

            btnDiscMoney.setOnClickListener(
                    new View.OnClickListener()
                    {
                        @Override
                        public void onClick(View v)
                        {
                            try {
                                changeDiscType(SalesInvoiceLine.DISC_TYPE_MONEY);
                            }
                            catch(Exception e) {
                                Log.d(Util.APP_TAG, "Error: " + e.getMessage());
                                e.printStackTrace();
                                Toast.makeText(getActivity(), R.string.internal_server_error, Toast.LENGTH_SHORT).show();
                            }
                        }
                    }
            );

            btnDiscPercentage.setOnClickListener(
                    new View.OnClickListener()
                    {
                        @Override
                        public void onClick(View v)
                        {
                            try {
                                changeDiscType(SalesInvoiceLine.DISC_TYPE_PERCENTAGE);
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
                            InvoiceLineDialogFragment.this.dismiss();
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

                                Double price = Double.valueOf(inputPrice.getText().toString());
                                Double qty = Double.valueOf(inputQty.getText().toString());
                                double discount = 0;  // implicit conversion to 0
                                if(inputDisc.getText() != null && inputDisc.getText().toString().length() > 0)
                                    discount = Double.valueOf(inputDisc.getText().toString());  // implicit conversion to 0
                                String metric = (String) spinnerMetric.getSelectedItem();

                                final SalesInvoiceLine sil = new SalesInvoiceLine();
                                sil.setPrice(price);
                                sil.setQty(qty);
                                sil.setDiscType(discType);
                                sil.setDiscValue(discount);
                                sil.setMetric(metric);
                                sil.setProduct(new Product());
                                sil.getProduct().setSystemId(argProduct.getSystemId());

                                if (argLine != null)
                                    sil.setSystemId(argLine.getSystemId());

                                ResultWatcher<SalesInvoiceLine> listener = new ResultWatcher<SalesInvoiceLine>()
                                {
                                    @Override
                                    public void onResult(Object source, SalesInvoiceLine result) throws Exception
                                    {
                                        Util.stopDialog(PROGRESS_ADD_INVOICE_LINE );
                                        Intent intent = new Intent();
                                        intent.putExtra("salesInvoiceLine", result);
                                        if (getTargetFragment() != null)
                                            getTargetFragment().onActivityResult(getTargetRequestCode(), Util.RESULT_CODE_SUCCESS, intent);
                                        InvoiceLineDialogFragment.this.dismiss();
                                    }

                                    @Override
                                    public void onError(Object source, int _errCode) throws Exception
                                    {
                                        Util.stopDialog(PROGRESS_ADD_INVOICE_LINE );
                                        Intent intent = new Intent();
                                        intent.putExtra("errorCode", _errCode);
                                        if (getTargetFragment() != null)
                                            getTargetFragment().onActivityResult(getTargetRequestCode(), Util.RESULT_CODE_FAILED, intent);
                                        InvoiceLineDialogFragment.this.dismiss();
                                    }
                                };

//                                Map<String, String> params = new HashMap<String, String>();
//                                params.put("productId", argProduct.getSystemId());
//                                params.put("warehouseId", InventoryService.warehouses.get(0).getSystemId());

//                                InventoryService.fetchOnHandStocks(
//                                        new ResultWatcher<List<OnHandStock>>()
//                                        {
//                                            @Override
//                                            public void onResult(Object source, List<OnHandStock> result) throws Exception
//                                            {
//                                                if(result.get(0).getQty() >= )
//                                            }
//
//                                            @Override
//                                            public void onError(Object source, int errCode)
//                                            {
//
//                                            }
//                                        }, params
//                                );

                                if (argLine == null)
                                    InvoiceService.addSalesInvoiceItem(listener, sil, argInv.getSystemId());
                                else
                                    InvoiceService.editSalesInvoiceItem(listener, sil, argInv.getSystemId());

                                Util.showDialog(getFragmentManager(), PROGRESS_ADD_INVOICE_LINE);
                            }
                            catch(Exception e) {
                                Log.d(Util.APP_TAG, "Error: " + e.getMessage());
                                e.printStackTrace();
                                Toast.makeText(getActivity(), R.string.internal_server_error, Toast.LENGTH_SHORT).show();
                            }
                        }
                    }
            );

//            if(InventoryService.warehouses == null) {
//                {
//                    Util.showDialog(getFragmentManager(), PROGRESS_FETCH_WAREHOUSE);
//                    InventoryService.fetchWarehouses(
//                            new ResultWatcher<List<Warehouse>>()
//                            {
//                                @Override
//                                public void onResult(Object source, List<Warehouse> result)
//                                {
//                                    Util.stopDialog(PROGRESS_FETCH_WAREHOUSE);
////                                    if (result != null) {
////                                        for (Warehouse wrh : result) {
////                                            list.add(wrh);
////                                        }
////                                        Util.fillSpinner(_spinner, list, Warehouse.class, _ctx);
////                                    }
//                                }
//
//                                @Override
//                                public void onError(Object source, int errCode) throws Exception {
//                                    Util.stopDialog(PROGRESS_FETCH_WAREHOUSE);
//                                    Toast.makeText(getActivity(), R.string.internal_server_error, Toast.LENGTH_SHORT).show();
//                                }
//                            }
//                    );
//                }
//            }

            return dialog;
        }
        catch(Exception e) {
            Log.d(Util.APP_TAG, "Error: " + e.getMessage());
            e.printStackTrace();
            Toast.makeText(getActivity(), R.string.internal_server_error, Toast.LENGTH_SHORT).show();
            return null;
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState)
    {
        super.onSaveInstanceState(outState);
        outState.putInt("discType", discType);
    }

    private void changeDiscType(int _type) {
        if(_type == SalesInvoiceLine.DISC_TYPE_PERCENTAGE) {
            if(discType == SalesInvoiceLine.DISC_TYPE_PERCENTAGE)
                return;
            discType = SalesInvoiceLine.DISC_TYPE_PERCENTAGE;
            btnDiscMoney.setTextColor(btnDiscPercentage.getTextColors());
            btnDiscMoney.setBackgroundResource(android.R.drawable.btn_default);
            btnDiscPercentage.setTextColor(Color.parseColor("#f6f7f7"));
            btnDiscPercentage.setBackgroundColor(Color.parseColor("#0f7858"));
        }
        else
        {
            if(discType == SalesInvoiceLine.DISC_TYPE_MONEY)
                return;
            discType = SalesInvoiceLine.DISC_TYPE_MONEY;
            Log.d(Util.APP_TAG, "CALLEd HERE");
            btnDiscPercentage.setTextColor(btnDiscMoney.getTextColors());
            btnDiscPercentage.setBackgroundResource(android.R.drawable.btn_default);
            btnDiscMoney.setTextColor(Color.parseColor("#f6f7f7"));
            btnDiscMoney.setBackgroundColor(Color.parseColor("#0f7858"));
        }
    }

    private Boolean validateInput() {
        if(inputPrice.getText() == null || inputPrice.getText().toString().length() <= 0) {
            Toast.makeText(getActivity(), R.string.price_field_cannot_be_empty, Toast.LENGTH_SHORT).show();
            return false;
        }

        if(inputQty.getText() == null || inputQty.getText().toString().length() <= 0) {
            Toast.makeText(getActivity(), R.string.qty_field_cannot_be_empty, Toast.LENGTH_SHORT).show();
            return false;
        }

        if(spinnerMetric.getSelectedItemPosition() <= 0) {
            Toast.makeText(getActivity(), R.string.metric_field_cannot_be_empty, Toast.LENGTH_SHORT).show();
            return false;
        }

        return true;
    }
}
