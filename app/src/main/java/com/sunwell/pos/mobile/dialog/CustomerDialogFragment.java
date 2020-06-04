package com.sunwell.pos.mobile.dialog;

import android.app.Dialog;
import android.content.Intent;
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
import com.sunwell.pos.mobile.service.LoginService;
import com.sunwell.pos.mobile.util.ResultWatcher;
import com.sunwell.pos.mobile.util.Util;

/**
 * Created by sunwell on 10/19/17.
 */

public class CustomerDialogFragment extends DialogFragment {

    private static final String PROGRESS_ADD_CUSTOMER = "progressAddCustomer";
    private static String CUSTOMER = "customer";
    private EditText inputName ;
    private EditText inputMemberNo ;
    private EditText inputPhone ;
    private EditText inputAddress ;
    private EditText inputEmail ;
    private EditText inputMemo ;
    private Spinner spinnerGender ;

    public static CustomerDialogFragment newInstance(Customer _customer) {
        Bundle bundle = new Bundle();
        bundle.putSerializable(CustomerDialogFragment.CUSTOMER, _customer);
        CustomerDialogFragment dialog = new CustomerDialogFragment();
        dialog.setArguments(bundle);
        return dialog;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        try {
            Log.d(Util.APP_TAG, "On CREATE DIALOG");
            final Dialog dialog = new Dialog(getActivity());
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            dialog.setContentView(R.layout.customer_dialog);
            inputName = (EditText) dialog.findViewById(R.id.input_name);
            inputMemberNo = (EditText) dialog.findViewById(R.id.input_member_no);
            inputPhone = (EditText) dialog.findViewById(R.id.input_phone);
            inputAddress = (EditText) dialog.findViewById(R.id.input_address);
            inputEmail = (EditText) dialog.findViewById(R.id.input_email);
            inputMemo = (EditText) dialog.findViewById(R.id.input_memo);
            spinnerGender = (Spinner) dialog.findViewById(R.id.spinner_gender);
            Button btnClose = (Button) dialog.findViewById(R.id.button_close);
            final Button btnAdd = (Button) dialog.findViewById(R.id.button_add_customer);

            Bundle arguments = getArguments();
            final Customer argCust = arguments != null ? (Customer) arguments.get(CustomerDialogFragment.CUSTOMER) : null;

            if (argCust != null) {
                inputName.setText(argCust.getName());
                inputMemberNo.setText(argCust.getMemberNo());
                inputPhone.setText(argCust.getPhone());
                inputAddress.setText(argCust.getAddress());
                inputEmail.setText(argCust.getEmail());
                inputMemo.setText(argCust.getMemo());
                spinnerGender.setSelection(argCust.getIsMale() ? 1 : 2);
                btnAdd.setText(R.string.edit_customer);
                dialog.setTitle(R.string.edit_customer);
            }

//            dialog.setTitle(R.string.add_customer);


            btnClose.setOnClickListener(
                    new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            CustomerDialogFragment.this.dismiss();
                        }
                    }
            );

            btnAdd.setOnClickListener(
                    new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            try {
                                if(!validateInput())
                                    return;

                                String name = inputName.getText().toString();
                                String memberNo = inputMemberNo.getText().toString();
                                String phone = inputPhone.getText().toString();
                                String email = inputEmail.getText().toString();
                                String address = inputAddress.getText().toString();
                                String memo = inputMemo.getText().toString();
                                Boolean isMale = spinnerGender.getSelectedItemPosition() == 1 ? true : false;

                                Customer cust = new Customer();
                                cust.setName(name);
                                cust.setPhone(phone);
                                cust.setMemberNo(memberNo);
                                cust.setEmail(email);
                                cust.setAddress(address);
                                cust.setMemo(memo);
                                cust.setIsMale(isMale);

                                if (argCust != null)
                                    cust.setSystemId(argCust.getSystemId());

                                ResultWatcher<Customer> custListener = new ResultWatcher<Customer>() {
                                    @Override
                                    public void onResult(Object source, Customer cust) throws Exception {
                                        Util.stopDialog(PROGRESS_ADD_CUSTOMER );
                                        Intent intent = new Intent();
                                        intent.putExtra("customer", cust);
                                        if (getTargetFragment() != null)
                                            getTargetFragment().onActivityResult(getTargetRequestCode(), Util.RESULT_CODE_SUCCESS, intent);
                                        CustomerDialogFragment.this.dismiss();
                                    }

                                    @Override
                                    public void onError(Object source, int _errCode) throws Exception {
                                        Util.stopDialog(PROGRESS_ADD_CUSTOMER );
                                        Intent intent = new Intent();
                                        intent.putExtra("errorCode", _errCode);
                                        if (getTargetFragment() != null)
                                            getTargetFragment().onActivityResult(getTargetRequestCode(), Util.RESULT_CODE_FAILED, intent);
                                        CustomerDialogFragment.this.dismiss();
                                    }
                                };

                                if (argCust == null)
                                    LoginService.addCustomer(custListener, cust);
                                else
                                    LoginService.editCustomer(custListener, cust);

                                Util.showDialog(getFragmentManager(), PROGRESS_ADD_CUSTOMER);
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
        if(inputName.getText() == null || inputName.getText().toString().length() <= 0) {
            Toast.makeText(getActivity(), R.string.name_field_cannot_be_empty, Toast.LENGTH_SHORT).show();
            return false;
        }

        if(spinnerGender.getSelectedItemPosition() <= 0) {
            Toast.makeText(getActivity(), R.string.gender_field_cannot_be_empty, Toast.LENGTH_SHORT).show();
            return false;
        }

        return true;
    }
}
