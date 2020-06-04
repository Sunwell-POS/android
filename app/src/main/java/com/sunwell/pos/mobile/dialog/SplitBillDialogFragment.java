package com.sunwell.pos.mobile.dialog;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.sunwell.pos_mobile.R;
import com.sunwell.pos.mobile.model.Product;
import com.sunwell.pos.mobile.model.SalesInvoice;
import com.sunwell.pos.mobile.model.SalesInvoiceLine;
import com.sunwell.pos.mobile.service.InvoiceService;
import com.sunwell.pos.mobile.util.ResultWatcher;
import com.sunwell.pos.mobile.util.Util;

import java.util.LinkedList;
import java.util.List;

/**
 * Created by sunwell on 10/19/17.
 */

public class SplitBillDialogFragment extends DialogFragment {

    private static final String PROGRESS_SPLIT_BILL = "progressSplitBill";
    private static final String SALES_INVOICE = "sales_invoice";
    private SalesInvoice argInv ;
    private LinearLayout panelItem ;

    public static SplitBillDialogFragment newInstance(SalesInvoice _si) {
        Bundle bundle = new Bundle();
        bundle.putSerializable(SplitBillDialogFragment.SALES_INVOICE, _si);
        SplitBillDialogFragment dialog = new SplitBillDialogFragment();
        dialog.setArguments(bundle);
        return dialog;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        try {
            final Dialog dialog = new Dialog(getActivity());
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            dialog.setContentView(R.layout.split_bill_dialog);
            panelItem = (LinearLayout) dialog.findViewById(R.id.panel_item);
            Button btnSplitBill = (Button) dialog.findViewById(R.id.button_split_bill);
            Button btnClose = (Button) dialog.findViewById(R.id.button_close);
            Bundle arguments = getArguments();
            argInv = arguments != null ? (SalesInvoice) arguments.get(SplitBillDialogFragment.SALES_INVOICE) : null;

            for(SalesInvoiceLine sil : argInv.getSalesInvoiceLines()) {
                addNewItemRow(sil);
            }

            btnSplitBill.setOnClickListener(
                    new View.OnClickListener()
                    {
                        @Override
                        public void onClick(View v)
                        {
                            try {
                                SalesInvoice si = new SalesInvoice();
                                si.setSystemId(argInv.getSystemId());

                                List<SalesInvoiceLine> lines = new LinkedList<>();

                                for (int i = 1; i < panelItem.getChildCount(); i++) {
                                    View row = panelItem.getChildAt(i);
                                    EditText inputQty = (EditText) row.findViewById(R.id.input_qty);
                                    double val = -1;
                                    if (inputQty.getText() != null && inputQty.getText().toString().length() > 0)
                                        val = Double.valueOf(inputQty.getText().toString());
                                    else
                                        continue;

                                    if (val > 0) {
                                        SalesInvoiceLine line = new SalesInvoiceLine();
                                        line.setProduct(argInv.getSalesInvoiceLines().get(i - 1).getProduct());
                                        line.setSystemId(argInv.getSalesInvoiceLines().get(i - 1).getSystemId());
                                        line.setQty(val);
                                        lines.add(line);
                                    }
                                }

                                if (!(lines.size() > 0)) {
                                    Toast.makeText(getActivity(), R.string.no_item_is_specified, Toast.LENGTH_SHORT).show();
                                    return;
                                }

                                si.setSalesInvoiceLines(lines);
                                InvoiceService.splitBill(
                                        new ResultWatcher<SalesInvoice>()
                                        {
                                            @Override
                                            public void onResult(Object source, SalesInvoice result) throws Exception
                                            {
                                                Util.stopDialog(PROGRESS_SPLIT_BILL);
                                                Intent intent = new Intent();
                                                intent.putExtra("salesInvoice", result);
                                                if (getTargetFragment() != null)
                                                    getTargetFragment().onActivityResult(getTargetRequestCode(), Util.RESULT_CODE_SUCCESS, intent);
                                                SplitBillDialogFragment.this.dismiss();
                                            }

                                            @Override
                                            public void onError(Object source, int errCode)
                                            {
                                                Util.stopDialog(PROGRESS_SPLIT_BILL);
                                                Intent intent = new Intent();
                                                intent.putExtra("errorCode", errCode);
                                                if (getTargetFragment() != null)
                                                    getTargetFragment().onActivityResult(getTargetRequestCode(), Util.RESULT_CODE_FAILED, intent);
                                                SplitBillDialogFragment.this.dismiss();
                                            }
                                        }, si
                                );
                                Util.showDialog(getFragmentManager(), PROGRESS_SPLIT_BILL);
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
                            SplitBillDialogFragment.this.dismiss();
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

    private void addNewItemRow(final SalesInvoiceLine _line) {
        LayoutInflater inflater = LayoutInflater.from(getActivity());
        View row = inflater.inflate(R.layout.split_bill_row, panelItem, false);
        TextView textProduct = (TextView) row.findViewById(R.id.text_product);
        TextView textTotalQty = (TextView) row.findViewById(R.id.text_total_qty);
        final TextView textBill1Qty = (TextView) row.findViewById(R.id.text_bill1_qty);
        final EditText inputQty = (EditText) row.findViewById(R.id.input_qty);
        Button btnNeg = (Button) row.findViewById(R.id.button_neg_qty);
        Button btnPlus = (Button) row.findViewById(R.id.button_plus_qty);
        textProduct.setText(_line.getProduct().getName());
        textTotalQty.setText(String.valueOf(_line.getQty()));
        textBill1Qty.setText(String.valueOf(_line.getQty()));
        inputQty.setText(String.valueOf(0));

        btnPlus.setOnClickListener(
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
                            if(val > _line.getQty())
                                return;

                            inputQty.setText(String.valueOf(val));
//                            textBill1Qty.setText(String.valueOf(_line.getQty() - val));
                        }
                        catch(Exception e) {
                            Log.d(Util.APP_TAG, "Error: " + e.getMessage());
                            e.printStackTrace();
                            Toast.makeText(getActivity(), R.string.internal_server_error, Toast.LENGTH_SHORT).show();
                        }
                    }
                }
        );

        btnNeg.setOnClickListener(
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
                            if (val < 0)
                                return;

                            inputQty.setText(String.valueOf(val));
//                            textBill1Qty.setText(String.valueOf(_line.getQty() - val));
                        }
                        catch(Exception e) {
                            Log.d(Util.APP_TAG, "Error: " + e.getMessage());
                            e.printStackTrace();
                            Toast.makeText(getActivity(), R.string.internal_server_error, Toast.LENGTH_SHORT).show();
                        }
                    }
                }
        );

        inputQty.addTextChangedListener(
                new TextWatcher()
                {
                    @Override
                    public void beforeTextChanged(CharSequence s, int start, int count, int after)
                    {

                    }

                    @Override
                    public void onTextChanged(CharSequence s, int start, int before, int count)
                    {

                    }

                    @Override
                    public void afterTextChanged(Editable s)
                    {
                        if (inputQty.getText() == null || inputQty.getText().toString().length() <= 0)
                            return;

                        Double val = Double.valueOf(inputQty.getText().toString());
                        if (val < 0 || val > _line.getQty())
                            return;

                        textBill1Qty.setText(String.valueOf(_line.getQty() - val));
                    }
                }
        );

        panelItem.addView(row);
    }

//    private Boolean validateInput() {
//        if(inputPrice.getText() == null || inputPrice.getText().toString().length() <= 0) {
//            Toast.makeText(getActivity(), R.string.price_field_cannot_be_empty, Toast.LENGTH_SHORT).show();
//            return false;
//        }
//
//        if(inputQty.getText() == null || inputQty.getText().toString().length() <= 0) {
//            Toast.makeText(getActivity(), R.string.qty_field_cannot_be_empty, Toast.LENGTH_SHORT).show();
//            return false;
//        }
//
//        if(spinnerMetric.getSelectedItemPosition() <= 0) {
//            Toast.makeText(getActivity(), R.string.metric_field_cannot_be_empty, Toast.LENGTH_SHORT).show();
//            return false;
//        }
//
//        return true;
//    }
}
