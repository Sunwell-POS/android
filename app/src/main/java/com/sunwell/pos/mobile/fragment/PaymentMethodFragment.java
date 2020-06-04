package com.sunwell.pos.mobile.fragment;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.sunwell.pos_mobile.R;
import com.sunwell.pos.mobile.dialog.PaymentMethodDialogFragment;
import com.sunwell.pos.mobile.model.PaymentMethodObj;
import com.sunwell.pos.mobile.service.InvoiceService;
import com.sunwell.pos.mobile.util.RecycleViewAdapter;
import com.sunwell.pos.mobile.util.ResultWatcher;
import com.sunwell.pos.mobile.util.Util;

import java.util.LinkedList;
import java.util.List;

/**
 * Created by sunwell on 10/18/17.
 */

public class PaymentMethodFragment extends Fragment {

    private static final String PROGRESS_FETCH_PM = "progressFetchPM";
    private static final String PROGRESS_DELETE_PM = "progressDeletePM";

    private  RecyclerView listObjects;

//    @Override
//    public void onCreate(@Nullable Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        try {
            View v = inflater.inflate(R.layout.standard_list, container, false);
            Button btnAdd = (Button) v.findViewById(R.id.button_add);
            MethodAdapter listAdapter;

            btnAdd.setText(R.string.add_payment_method);
            btnAdd.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                    try {
                        FragmentManager fragmentManager = getFragmentManager();
                        PaymentMethodDialogFragment dialog = new PaymentMethodDialogFragment();
                        dialog.setTargetFragment(PaymentMethodFragment.this, Util.REQUEST_CODE_ADD);
                        dialog.show(fragmentManager, "Category");
                    }
                    catch(Exception e) {
                        Log.d(Util.APP_TAG, "Error: " + e.getMessage());
                        e.printStackTrace();
                        Toast.makeText(getActivity(), R.string.internal_server_error, Toast.LENGTH_SHORT).show();
                    }
                    }
                }
            );
            listObjects = (RecyclerView) v.findViewById(R.id.list_objects);
            listObjects.setLayoutManager(new LinearLayoutManager(getActivity()));
            ResultWatcher<List<PaymentMethodObj>> listener = new ResultWatcher<List<PaymentMethodObj>>() {
                @Override
                public void onResult(Object source, List<PaymentMethodObj> _methods) throws Exception {
                    Util.stopDialog(PROGRESS_FETCH_PM);
                    listObjects.setAdapter(new MethodAdapter(_methods));
                }

                @Override
                public void onError(Object source, int errCode) throws Exception {
                    Util.stopDialog(PROGRESS_FETCH_PM);
                    Toast.makeText(getActivity(), R.string.internal_server_error, Toast.LENGTH_SHORT).show();
                }
            };

            InvoiceService.fetchPaymentMethods(listener);
            Util.showDialog(getFragmentManager(), PROGRESS_FETCH_PM);
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
            if (_requestCode == Util.REQUEST_CODE_ADD) {
                if (_resultCode == Util.RESULT_CODE_SUCCESS) {
                    PaymentMethodObj pmo = (PaymentMethodObj)data.getSerializableExtra("paymentMethodObject");
                    ((MethodAdapter) listObjects.getAdapter()).addItem(pmo);
                    Toast.makeText(getActivity(), R.string.success_add_payment_method, Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(getActivity(), R.string.fail_add_payment_method, Toast.LENGTH_SHORT).show();
                }
            } else if (_requestCode == Util.REQUEST_CODE_EDIT) {
                if (_resultCode == Util.RESULT_CODE_SUCCESS) {
                    PaymentMethodObj pmo = (PaymentMethodObj)data.getSerializableExtra("paymentMethodObject");
                    ((MethodAdapter) listObjects.getAdapter()).updateItem(pmo);
                    Toast.makeText(getActivity(), R.string.success_edit_payment_method, Toast.LENGTH_SHORT).show();
//                    ((MethodAdapter) listObjects.getAdapter()).notifyItemUpdated();
                } else {
                    Toast.makeText(getActivity(), R.string.fail_edit_payment_method, Toast.LENGTH_SHORT).show();
                }
            }
        }
        catch(Exception e) {
            Log.d(Util.APP_TAG, "Error: " + e.getMessage());
            e.printStackTrace();
            Toast.makeText(getActivity(), R.string.internal_server_error, Toast.LENGTH_SHORT).show();
        }
    }

    private class MethodHolder extends RecyclerView.ViewHolder {

        public TextView textNo;
        public TextView textName;
        public TextView textMethod;
        public TextView textActive;
        public TextView textDiscount;
        public TextView textMinPayment;
        public TextView textMaxPayment;
        public ImageView imageActive;
        public ImageButton btnEdit;
        public ImageButton btnDelete;

        public EditText inputName;
        public EditText inputMethod;
        public EditText inputDiscount;
        public EditText inputMinPayment;
        public EditText inputMaxPayment;
        public Spinner spinnerActive;
        public ImageButton btnSearch;

        public MethodHolder(View itemView) {
            super(itemView);
            textNo = (TextView) itemView.findViewById(R.id.text_no);
            textName = (TextView) itemView.findViewById(R.id.text_name);
            textMethod = (TextView) itemView.findViewById(R.id.text_method);
            textActive = (TextView) itemView.findViewById(R.id.text_active);
            textDiscount = (TextView) itemView.findViewById(R.id.text_discount);
            textMinPayment = (TextView) itemView.findViewById(R.id.text_min_payment);
            textMaxPayment = (TextView) itemView.findViewById(R.id.text_max_payment);
            imageActive = (ImageView) itemView.findViewById(R.id.image_active);
            btnEdit = (ImageButton) itemView.findViewById(R.id.button_edit);
            btnDelete = (ImageButton) itemView.findViewById(R.id.button_delete);

            inputName = (EditText) itemView.findViewById(R.id.input_name);
            inputMethod = (EditText) itemView.findViewById(R.id.input_method);
            spinnerActive = (Spinner) itemView.findViewById(R.id.spinner_active);
            inputDiscount = (EditText) itemView.findViewById(R.id.input_discount);
            inputMinPayment = (EditText) itemView.findViewById(R.id.input_min_payment);
            inputMaxPayment = (EditText) itemView.findViewById(R.id.input_max_payment);
            btnSearch = (ImageButton) itemView.findViewById(R.id.button_search);
        }
    }

    private class MethodAdapter extends RecycleViewAdapter<PaymentMethodObj, MethodHolder>
    {
        private List<PaymentMethodObj> fullMethods;

        public MethodAdapter(List<PaymentMethodObj> _methods) {
            super(new LinkedList<>(_methods), RecycleViewAdapter.USE_FILTER);
            this.fullMethods = _methods;
        }

        @Override
        public MethodHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            try {
                LayoutInflater layoutInflater = LayoutInflater.from(getActivity());
                View view;
                if (viewType == HEADER)
                    view = layoutInflater.inflate(R.layout.payment_method_header, parent, false);
                else if (viewType == FILTER)
                    view = layoutInflater.inflate(R.layout.payment_method_filter, parent, false);
                else
                    view = layoutInflater.inflate(R.layout.payment_method_row, parent, false);
                return new MethodHolder(view);
            }
            catch(Exception e) {
                Log.d(Util.APP_TAG, "Error: " + e.getMessage());
                e.printStackTrace();
                Toast.makeText(getActivity(), R.string.internal_server_error, Toast.LENGTH_SHORT).show();
                return null;
            }

        }

        @Override
        public void onBindViewHolder(final MethodHolder holder, final int position) {
            try {
                if (position == 0 || products == null)
                    return;

                if(position > 1) {
                    final PaymentMethodObj method = this.products.get(position - 2);
                    holder.textNo.setText("" + (position - 1));
                    holder.textName.setText(method.getName());
                    holder.textMethod.setText(method.getParent().getName());
                    holder.textDiscount.setText(method.getDiscValue() != null ? String.valueOf(method.getDiscValue()) : "");
                    holder.textMinPayment.setText(method.getMinPayment() != null ? String.valueOf(method.getMinPayment()) : "");
                    holder.textMaxPayment.setText(method.getMaxPayment() != null ? String.valueOf(method.getMaxPayment()) : "");
                    Drawable drawable;
                    if (method.getStatus()) {
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
                            drawable = getResources().getDrawable(R.drawable.right, getActivity().getTheme());
                        else
                            drawable = getResources().getDrawable(R.drawable.right);
                    } else {
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
                            drawable = getResources().getDrawable(R.drawable.wrong, getActivity().getTheme());
                        else
                            drawable = getResources().getDrawable(R.drawable.wrong);
                    }

                    holder.imageActive.setImageDrawable(drawable);

                    holder.btnEdit.setOnClickListener(
                        new View.OnClickListener()
                        {
                            @Override
                            public void onClick(View v)
                            {
                                try {
                                    PaymentMethodDialogFragment dialog = PaymentMethodDialogFragment.newInstance(method);
                                    dialog.setTargetFragment(PaymentMethodFragment.this, Util.REQUEST_CODE_EDIT);
                                    editedPosition = position;
                                    dialog.show(getFragmentManager(), "Customer");
                                }
                                catch(Exception e) {
                                    Log.d(Util.APP_TAG, "Error: " + e.getMessage());
                                    e.printStackTrace();
                                    Toast.makeText(getActivity(), R.string.internal_server_error, Toast.LENGTH_SHORT).show();
                                }
                            }
                        }
                    );
    //
                    holder.btnDelete.setOnClickListener(
                            new View.OnClickListener()
                            {
                                @Override
                                public void onClick(View v)
                                {
                                    try {
                                        String systemId = method.getSystemId();
                                        InvoiceService.deletePaymentMethod(
                                                new ResultWatcher<Boolean>()
                                                {
                                                    @Override
                                                    public void onResult(Object source, Boolean result)throws Exception
                                                    {
                                                        Util.stopDialog(PROGRESS_DELETE_PM);
                                                        ((MethodAdapter)listObjects.getAdapter()).removeAt(position);
                                                        Toast.makeText(getActivity(), R.string.success_delete_payment_method, Toast.LENGTH_SHORT).show();
//                                                        listObjects.getAdapter().notifyDataSetChanged();
                                                    }

                                                    @Override
                                                    public void onError(Object source, int errCode) throws Exception
                                                    {
                                                        Util.stopDialog(PROGRESS_DELETE_PM);
                                                        Toast.makeText(getActivity(), R.string.fail_delete_payment_method, Toast.LENGTH_SHORT).show();
                                                    }
                                                }
                                                , systemId);
                                        Util.showDialog(getFragmentManager(), PROGRESS_DELETE_PM);
                                    }
                                    catch (Exception e) {
                                        Log.d(Util.APP_TAG, "Error: " + e.getMessage());
                                        e.printStackTrace();
                                    }
                                }
                            }
                    );
                }
                else {
                    holder.btnSearch.setOnClickListener(
                            new View.OnClickListener()
                            {
                                @Override
                                public void onClick(View v)
                                {
                                    String filterName = holder.inputName.getText().toString();
                                    String filterMethod = holder.inputMethod.getText().toString();
                                    Integer posActive = holder.spinnerActive.getSelectedItemPosition();
                                    List<PaymentMethodObj> filteredMethods = new LinkedList<>();
                                    if(fullMethods != null && fullMethods.size() > 0) {
                                        for(PaymentMethodObj method : fullMethods) {
//                                            Log.d(Util.APP_TAG, "NAME: " + filterName + " C NAME: " + cust.getName());
                                            if (filterName != null && filterName.length() > 0) {
                                                if(!method.getName().toLowerCase().contains(filterName.toLowerCase()))
                                                    continue;
                                            }

                                            if (filterMethod != null && filterMethod.length() > 0) {
                                                if(!method.getParent().getName().toLowerCase().contains(filterMethod.toLowerCase()))
                                                    continue;
                                            }


                                            if(posActive > 0) {
                                                boolean def = posActive == 1 ? true : false;
                                                if(method.getStatus() != null) {
                                                    if (def) {
                                                        if (!method.getStatus())
                                                            continue;
                                                    }
                                                    else {
                                                        if(method.getStatus())
                                                            continue;
                                                    }
                                                }
                                                else {
                                                    if(def)
                                                        continue;
                                                }
                                            }

                                            filteredMethods.add(method);
                                        }

                                        setItems(filteredMethods);
                                    }
                                }
                            }
                    );
                }
            }
            catch(Exception e) {
                Log.d(Util.APP_TAG, "Error: " + e.getMessage());
                e.printStackTrace();
                Toast.makeText(getActivity(), R.string.internal_server_error, Toast.LENGTH_SHORT).show();
            }
        }
    }

}
