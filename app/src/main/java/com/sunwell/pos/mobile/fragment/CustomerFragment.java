package com.sunwell.pos.mobile.fragment;

import android.content.Intent;
import android.graphics.drawable.Drawable;
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
import android.widget.TextView;
import android.widget.Toast;

import com.example.sunwell.pos_mobile.R;
import com.sunwell.pos.mobile.dialog.CustomerDialogFragment;
import com.sunwell.pos.mobile.model.Customer;
import com.sunwell.pos.mobile.model.ProdCategory;
import com.sunwell.pos.mobile.service.LoginService;
import com.sunwell.pos.mobile.util.RecycleViewAdapter;
import com.sunwell.pos.mobile.util.ResultWatcher;
import com.sunwell.pos.mobile.util.Util;

import java.util.LinkedList;
import java.util.List;

/**
 * Created by sunwell on 10/18/17.
 */

public class CustomerFragment extends Fragment {
    private static final String PROGRESS_FETCH_CUSTOMER = "progressFetchCustomer";
    private static final String PROGRESS_DELETE_CUSTOMER = "progressDeleteCustomer";
    private RecyclerView listObjects;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        try {
            View v = inflater.inflate(R.layout.standard_list, container, false);
            Button btnAdd = (Button) v.findViewById(R.id.button_add);

            btnAdd.setText(R.string.add_customer);
            btnAdd.setOnClickListener(
                    new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                        try {
                            FragmentManager fragmentManager = getFragmentManager();
                            CustomerDialogFragment dialog = new CustomerDialogFragment();
                            dialog.setTargetFragment(CustomerFragment.this, Util.REQUEST_CODE_ADD);
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
            ResultWatcher<List<Customer>> listener = new ResultWatcher<List<Customer>>() {
                @Override
                public void onResult(Object source, List<Customer> _custs) throws Exception {
                    Util.stopDialog(PROGRESS_FETCH_CUSTOMER);
                    listObjects.setAdapter(new CustomerAdapter(_custs));
                }

                @Override
                public void onError(Object source, int errCode) throws Exception {
                    Util.stopDialog(PROGRESS_FETCH_CUSTOMER);
                    Toast.makeText(getActivity(), R.string.internal_server_error, Toast.LENGTH_SHORT).show();
                }
            };

            LoginService.fetchCustomers(listener);
            Util.showDialog(getFragmentManager(), PROGRESS_FETCH_CUSTOMER);
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
                    Customer cust = (Customer)data.getSerializableExtra("customer");
                    ((CustomerAdapter) listObjects.getAdapter()).addItem(cust);
                    Toast.makeText(getActivity(), R.string.success_add_customer, Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(getActivity(), R.string.fail_add_customer, Toast.LENGTH_SHORT).show();
                }
            } else if (_requestCode == Util.REQUEST_CODE_EDIT) {
                if (_resultCode == Util.RESULT_CODE_SUCCESS) {
                    Customer cust = (Customer)data.getSerializableExtra("customer");
                    ((CustomerAdapter) listObjects.getAdapter()).updateItem(cust);
                    Toast.makeText(getActivity(), R.string.success_edit_customer, Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(getActivity(), R.string.fail_edit_customer, Toast.LENGTH_SHORT).show();
                }
            }
        }
        catch(Exception e) {
            Log.d(Util.APP_TAG, "Error: " + e.getMessage());
            e.printStackTrace();
            Toast.makeText(getActivity(), R.string.internal_server_error, Toast.LENGTH_SHORT).show();
        }
    }

    private class CustomerHolder extends RecyclerView.ViewHolder {

        public TextView textNo;
        public TextView textMemberNo;
        public TextView textName;
        public TextView textPhone;
        public TextView textEmail;
        public ImageButton btnEdit;
        public ImageButton btnDelete;

        public EditText inputMemberNo;
        public EditText inputName;
        public EditText inputPhone;
        public EditText inputEmail;
        public ImageButton btnSearch;

        public CustomerHolder(View itemView) {
            super(itemView);
            textNo = (TextView) itemView.findViewById(R.id.text_no);
            textName = (TextView) itemView.findViewById(R.id.text_name);
            textMemberNo = (TextView) itemView.findViewById(R.id.text_member_no);
            textPhone = (TextView) itemView.findViewById(R.id.text_phone);
            textEmail = (TextView) itemView.findViewById(R.id.text_email);
            btnEdit = (ImageButton) itemView.findViewById(R.id.button_edit);
            btnDelete = (ImageButton) itemView.findViewById(R.id.button_delete);

            inputName = (EditText) itemView.findViewById(R.id.input_name);
            inputMemberNo = (EditText) itemView.findViewById(R.id.input_member_no);
            inputPhone = (EditText) itemView.findViewById(R.id.input_phone);
            inputEmail = (EditText) itemView.findViewById(R.id.input_email);
            btnSearch = (ImageButton) itemView.findViewById(R.id.button_search);
        }
    }

    private class CustomerAdapter extends RecycleViewAdapter<Customer, CustomerHolder> {
        private List<Customer> fullCustomers;

        public CustomerAdapter(List<Customer> _custs) {
            super(new LinkedList<>(_custs), RecycleViewAdapter.USE_FILTER);
            this.fullCustomers = _custs;
        }

        @Override
        public CustomerHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            try {
                LayoutInflater layoutInflater = LayoutInflater.from(getActivity());
                View view;
                if (viewType == HEADER)
                    view = layoutInflater.inflate(R.layout.customer_header, parent, false);
                else if (viewType == FILTER)
                    view = layoutInflater.inflate(R.layout.customer_filter, parent, false);
                else
                    view = layoutInflater.inflate(R.layout.customer_row, parent, false);
                return new CustomerHolder(view);
            }
            catch(Exception e) {
                Log.d(Util.APP_TAG, "Error: " + e.getMessage());
                e.printStackTrace();
                Toast.makeText(getActivity(), R.string.internal_server_error, Toast.LENGTH_SHORT).show();
                return null;
            }
        }

        @Override
        public void onBindViewHolder(final CustomerHolder holder, final int position) {
            try {
                if (position == 0 || products == null)
                    return;

                if(position > 1) {

                    final Customer customer = this.products.get(position - 2);
                    holder.textNo.setText("" + (position -1));
                    holder.textMemberNo.setText(customer.getMemberNo());
                    holder.textName.setText(customer.getName());
                    holder.textPhone.setText(customer.getPhone());
                    holder.textEmail.setText(customer.getEmail());
                    Drawable drawable;
                    holder.btnEdit.setOnClickListener(
                            new View.OnClickListener()
                            {
                                @Override
                                public void onClick(View v)
                                {
                                    try {
                                        CustomerDialogFragment dialog = CustomerDialogFragment.newInstance(customer);
                                        dialog.setTargetFragment(CustomerFragment.this, Util.REQUEST_CODE_EDIT);
                                        editedPosition = position;
                                        dialog.show(getFragmentManager(), "Customer");
                                    }
                                    catch (Exception e) {
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
                                        String systemId = customer.getSystemId();
                                        LoginService.deleteCustomer(
                                                new ResultWatcher<Boolean>()
                                                {
                                                    @Override
                                                    public void onResult(Object source, Boolean result) throws Exception
                                                    {
                                                        Util.stopDialog(PROGRESS_DELETE_CUSTOMER);
                                                        removeAt(position);
                                                        Toast.makeText(getActivity(), R.string.success_delete_customer, Toast.LENGTH_SHORT).show();
//                                                        listObjects.getAdapter().notifyDataSetChanged();
                                                    }

                                                    @Override
                                                    public void onError(Object source, int errCode) throws Exception
                                                    {
                                                        Util.stopDialog(PROGRESS_DELETE_CUSTOMER);
                                                        Toast.makeText(getActivity(), R.string.fail_delete_customer, Toast.LENGTH_SHORT).show();
                                                    }
                                                }
                                                , systemId);
                                        Util.showDialog(getFragmentManager(), PROGRESS_DELETE_CUSTOMER);
                                    }
                                    catch (Exception e) {
//                                        Util.stopDialog(PROGRESS_FETCH_CUSTOMER);
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
                                    String filterMemberNo = holder.inputMemberNo.getText().toString();
                                    String filterPhone = holder.inputPhone.getText().toString();
                                    String filterEmail = holder.inputEmail.getText().toString();
                                    List<Customer> fileteredCustomers = new LinkedList<>();
                                    if(fullCustomers != null && fullCustomers.size() > 0) {
                                        for(Customer cust : fullCustomers) {
//                                            Log.d(Util.APP_TAG, "NAME: " + filterName + " C NAME: " + cust.getName());
                                            if (filterName != null && filterName.length() > 0) {
                                                if(!cust.getName().toLowerCase().contains(filterName.toLowerCase()))
                                                    continue;
                                            }

                                            if (filterMemberNo != null && filterMemberNo.length() > 0) {
                                                if(!cust.getMemberNo().toLowerCase().contains(filterMemberNo.toLowerCase()))
                                                    continue;
                                            }

                                            if (filterPhone != null && filterPhone.length() > 0) {
                                                if(cust.getPhone() != null) {
                                                    if (!cust.getPhone().toLowerCase().contains(filterPhone.toLowerCase()))
                                                        continue;
                                                }
                                                else
                                                    continue;
                                            }

                                            if (filterEmail != null && filterEmail.length() > 0) {
                                                if(cust.getEmail() != null) {
                                                    if (!cust.getEmail().toLowerCase().contains(filterEmail.toLowerCase()))
                                                        continue;
                                                }
                                                else
                                                    continue;
                                            }

                                            fileteredCustomers.add(cust);
                                        }

                                        setItems(fileteredCustomers);
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
