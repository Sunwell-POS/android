package com.sunwell.pos.mobile.dialog;

import android.app.Dialog;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.sunwell.pos_mobile.R;
import com.sunwell.pos.mobile.model.Customer;
import com.sunwell.pos.mobile.model.SalesInvoice;
import com.sunwell.pos.mobile.service.InvoiceService;
import com.sunwell.pos.mobile.service.LoginService;
import com.sunwell.pos.mobile.util.ResultListener;
import com.sunwell.pos.mobile.util.ResultWatcher;
import com.sunwell.pos.mobile.util.Util;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by sunwell on 10/19/17.
 */

public class InvoiceDialogFragment extends DialogFragment {

    private static String CATEGORY = "category";
    private ResultListener<SalesInvoice> dialogListener ;

    private EditText inputName ;
    private Spinner spinnerCustomer ;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        try {
            final Dialog dialog = new Dialog(getActivity());
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            dialog.setContentView(R.layout.invoice_dialog);
            inputName = (EditText) dialog.findViewById(R.id.input_name);
            spinnerCustomer = (Spinner) dialog.findViewById(R.id.spinner_customer);
            Button btnClose = (Button) dialog.findViewById(R.id.button_close);
            final Button btnAdd = (Button) dialog.findViewById(R.id.button_create);

//            dialog.setTitle(R.string.new_invoice);

//            final List<Customer> list = new ArrayList<>();
//            list.add(new Customer());
//            if (LoginService.customers != null) {
//                for (Customer cust : LoginService.customers) {
//                    list.add(cust);
//                }
//                Util.fillSpinner(spinnerCustomer, list, Customer.class, getActivity());
//            } else {
//                    LoginService.fetchCustomers(
//                            new ResultWatcher<List<Customer>>()
//                            {
//                                @Override
//                                public void onResult(Object source, List<Customer> result) throws Exception
//                                {
//                                    if (result != null) {
//                                        for (Customer cust : result) {
//                                            list.add(cust);
//                                        }
//                                        Util.fillSpinner(spinnerCustomer, list, Customer.class, getActivity());
//                                    }
//                                }
//
//                                @Override
//                                public void onError(Object source, int errCode) throws Exception
//                                {
//                                    super.onError(source, errCode);
//                                }
//                            }
//                    );
//            }
            btnClose.setOnClickListener(
                    new View.OnClickListener()
                    {
                        @Override
                        public void onClick(View v)
                        {
                            InvoiceDialogFragment.this.dismiss();
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

                                SalesInvoice si = new SalesInvoice();
                                if (spinnerCustomer.getSelectedItemPosition() > 0) {
                                    Customer cust = (Customer) spinnerCustomer.getSelectedItem();
                                    si.setCustomer(new Customer());
                                    si.getCustomer().setSystemId(cust.getSystemId());
                                } else if (inputName.getText() != null && inputName.getText().toString().length() > 0) {
                                    String name = inputName.getText().toString();
                                    si.setName(name);
                                }

                                ResultWatcher<SalesInvoice> invoiceListener = new ResultWatcher<SalesInvoice>()
                                {
                                    @Override
                                    public void onResult(Object source, SalesInvoice inv) throws Exception
                                    {
                                        if (dialogListener != null)
                                            dialogListener.onResult(InvoiceDialogFragment.this, inv);
                                        InvoiceDialogFragment.this.dismiss();
                                    }

                                    @Override
                                    public void onError(Object source, int _errCode) throws Exception
                                    {
                                        if (dialogListener != null)
                                            dialogListener.onError(InvoiceDialogFragment.this, _errCode);
                                        InvoiceDialogFragment.this.dismiss();
                                    }
                                };

                                InvoiceService.addSalesInvoice(invoiceListener, si);
                            }
                            catch(Exception e) {
                                Log.d(Util.APP_TAG, "Error: " + e.getMessage());
                                e.printStackTrace();
                                Toast.makeText(getActivity(), R.string.internal_server_error, Toast.LENGTH_SHORT).show();
                            }
                        }
                    }
            );

            LoginService.fillCustomerSpinner(spinnerCustomer, getActivity(), getFragmentManager());

            return dialog;
        }
        catch(Exception e) {
            Log.d(Util.APP_TAG, "Error: " + e.getMessage());
            e.printStackTrace();
            Toast.makeText(getActivity(), R.string.internal_server_error, Toast.LENGTH_SHORT).show();
            return null;
        }
    }

    public void setDialogListener(ResultListener<SalesInvoice> _listener) {
        dialogListener = _listener;
    }

    private Boolean validateInput() {
        if(inputName.getText() == null || inputName.getText().toString().length() > 0 || spinnerCustomer.getSelectedItemPosition() <= 0) {
            Toast.makeText(getActivity(), R.string.either_name_or_customer_must_be_filled, Toast.LENGTH_SHORT).show();
            return false;
        }

//        if(spinnerCustomer.getSelectedItemPosition() <= 0) {
//            Toast.makeText(getActivity(), R.string.customer_field_cannot_be_empty, Toast.LENGTH_SHORT).show();
//            return false;
//        }

        return true;
    }
}
