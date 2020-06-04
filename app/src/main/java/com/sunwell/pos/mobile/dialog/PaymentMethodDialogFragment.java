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
    import android.widget.CheckBox;
    import android.widget.EditText;
    import android.widget.Spinner;
    import android.widget.Toast;

    import com.example.sunwell.pos_mobile.R;
    import com.sunwell.pos.mobile.model.PaymentMethod;
    import com.sunwell.pos.mobile.model.PaymentMethodObj;
    import com.sunwell.pos.mobile.service.InvoiceService;
    import com.sunwell.pos.mobile.util.ResultWatcher;
    import com.sunwell.pos.mobile.util.Util;

    import java.util.ArrayList;
    import java.util.List;

    /**
     * Created by sunwell on 10/19/17.
     */

    public class PaymentMethodDialogFragment extends DialogFragment {

        private static final String PROGRESS_ADD_PAYMENT_METHOD_OBJ = "progressAddPaymentMethodObj";
        private static final String PROGRESS_FETCH_PAYMENT_METHOD = "progressFetchPaymentMethod";
        private static String PAYMENT_METHOD = "payment_method";
        private EditText inputName ;
        private EditText inputMemo ;
        private Spinner spinnerPaymentMethod ;
        private CheckBox cbActive ;
        private CheckBox cbDiscount ;

        public static PaymentMethodDialogFragment newInstance(PaymentMethodObj _method) {
            Bundle bundle = new Bundle();
            bundle.putSerializable(PaymentMethodDialogFragment.PAYMENT_METHOD, _method);
            PaymentMethodDialogFragment dialog = new PaymentMethodDialogFragment();
            dialog.setArguments(bundle);
            return dialog;
        }

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            try {
                final Dialog dialog = new Dialog(getActivity());
                dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                dialog.setContentView(R.layout.payment_method_dialog);
                inputName = (EditText) dialog.findViewById(R.id.input_name);
                inputMemo = (EditText) dialog.findViewById(R.id.input_memo);
                spinnerPaymentMethod = (Spinner) dialog.findViewById(R.id.spinner_payment);
                cbActive = (CheckBox) dialog.findViewById(R.id.cb_active);
                cbDiscount = (CheckBox) dialog.findViewById(R.id.cb_discount);
                Button btnClose = (Button) dialog.findViewById(R.id.button_close);
                final Button btnAdd = (Button) dialog.findViewById(R.id.button_add_method);

                Bundle arguments = getArguments();
                final PaymentMethodObj argMethod = arguments != null ? (PaymentMethodObj) arguments.get(PaymentMethodDialogFragment.PAYMENT_METHOD) : null;

//                dialog.setTitle(R.string.add_payment_method);

                if (argMethod != null) {
                    inputName.setText(argMethod.getName());
                    inputMemo.setText(argMethod.getMemo());
                    cbActive.setChecked(argMethod.getStatus());
                    cbDiscount.setChecked(argMethod.getHasDisc());
                    btnAdd.setText(R.string.edit_payment_method);
                    dialog.setTitle(R.string.edit_payment_method);
                }

                btnClose.setOnClickListener(
                        new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                PaymentMethodDialogFragment.this.dismiss();
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

                                    if (!validateInput())
                                        return;

                                    String name = inputName.getText().toString();
                                    String memo = inputMemo.getText().toString();
                                    Boolean isActive = cbActive.isChecked();
                                    Boolean hasDisc = cbDiscount.isChecked();
                                    PaymentMethod pm = (PaymentMethod) spinnerPaymentMethod.getSelectedItem();

                                    PaymentMethodObj pmo = new PaymentMethodObj();
                                    pmo.setName(name);
                                    pmo.setMemo(memo);
                                    pmo.setParent(pm);
                                    pmo.setStatus(isActive);
                                    pmo.setHasDisc(hasDisc);
                                    pmo.setDiscType(PaymentMethodObj.DISC_TYPE_PERCENTAGE);

                                    if (argMethod != null)
                                        pmo.setSystemId(argMethod.getSystemId());

                                    ResultWatcher<PaymentMethodObj> listener = new ResultWatcher<PaymentMethodObj>()
                                    {
                                        @Override
                                        public void onResult(Object source, PaymentMethodObj _pmo)
                                        {
                                            Util.stopDialog(PROGRESS_ADD_PAYMENT_METHOD_OBJ);
                                            Intent intent = new Intent();
                                            intent.putExtra("paymentMethodObject", _pmo);
                                            if (getTargetFragment() != null)
                                                getTargetFragment().onActivityResult(getTargetRequestCode(), Util.RESULT_CODE_SUCCESS, intent);
                                            PaymentMethodDialogFragment.this.dismiss();
                                        }

                                        @Override
                                        public void onError(Object source, int _errCode)
                                        {
                                            Util.stopDialog(PROGRESS_ADD_PAYMENT_METHOD_OBJ);
                                            Intent intent = new Intent();
                                            intent.putExtra("errorCode", _errCode);
                                            if (getTargetFragment() != null)
                                                getTargetFragment().onActivityResult(getTargetRequestCode(), Util.RESULT_CODE_FAILED, intent);
                                            PaymentMethodDialogFragment.this.dismiss();
                                        }
                                    };

                                    if (argMethod == null)
                                        InvoiceService.addPaymentMethod(listener, pmo);
                                    else
                                        InvoiceService.editPaymentMethod(listener, pmo);

                                    Util.showDialog(getFragmentManager(), PROGRESS_ADD_PAYMENT_METHOD_OBJ);
                                }
                                catch (Exception e) {
                                    Log.d(Util.APP_TAG, "Error: " + e.getMessage());
                                    e.printStackTrace();
                                    Toast.makeText(getActivity(), R.string.internal_server_error, Toast.LENGTH_SHORT).show();
                                }
                            }
                        }
                );

                final List<PaymentMethod> list = new ArrayList<>();
                list.add(new PaymentMethod());
                if(InvoiceService.originPaymentMethods != null) {
                    for(PaymentMethod pm : InvoiceService.originPaymentMethods) {
                        list.add(pm);
                    }
                    Util.fillSpinner(spinnerPaymentMethod, list, PaymentMethod.class, getActivity());
                    if(argMethod != null)
                        spinnerPaymentMethod.setSelection(((ArrayAdapter)spinnerPaymentMethod.getAdapter()).getPosition(argMethod.getParent()));
                }
                else {
                    InvoiceService.fetchOriginPaymentMethods(
                            new ResultWatcher<List<PaymentMethod>>() {
                                @Override
                                public void onResult(Object source, List<PaymentMethod> result) throws Exception {
                                    Util.stopDialog(PROGRESS_FETCH_PAYMENT_METHOD);
                                    if(result != null ) {
                                        for(PaymentMethod pm : result) {
                                            list.add(pm);
                                        }
                                        Util.fillSpinner(spinnerPaymentMethod, list, PaymentMethod.class, getActivity());
                                        if(argMethod != null)
                                            spinnerPaymentMethod.setSelection(((ArrayAdapter)spinnerPaymentMethod.getAdapter()).getPosition(argMethod.getParent()));
                                    }
                                }

                                @Override
                                public void onError(Object source, int errCode) throws Exception {
                                    Util.stopDialog(PROGRESS_FETCH_PAYMENT_METHOD);
                                    super.onError(source, errCode);
                                }
                            }
                    );
                    Util.showDialog(getFragmentManager(), PROGRESS_FETCH_PAYMENT_METHOD);
                }

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

            if(spinnerPaymentMethod.getSelectedItemPosition() <= 0) {
                Toast.makeText(getActivity(), R.string.payment_field_cannot_be_empty, Toast.LENGTH_SHORT).show();
                return false;
            }

            return true;
        }
    }
