package com.sunwell.pos.mobile.fragment;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.sunwell.pos_mobile.R;
import com.sunwell.pos.mobile.dialog.CustomerDialogFragment;
import com.sunwell.pos.mobile.model.Customer;
import com.sunwell.pos.mobile.model.ProdCategory;
import com.sunwell.pos.mobile.model.SalesPayment;
import com.sunwell.pos.mobile.service.InvoiceService;
import com.sunwell.pos.mobile.service.LoginService;
import com.sunwell.pos.mobile.util.RecycleViewAdapter;
import com.sunwell.pos.mobile.util.ResultWatcher;
import com.sunwell.pos.mobile.util.Util;

import java.text.SimpleDateFormat;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by sunwell on 10/18/17.
 */

public class SalesFragment extends Fragment {

    private static final String PROGRESS_FETCH_PAYMENTS = "progressFetchPayment";
//    private List<Customer> userGroups;
    private RecyclerView listObjects;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        try {
            View v = inflater.inflate(R.layout.standard_list, container, false);
            Button btnAdd = (Button) v.findViewById(R.id.button_add);
            SalesAdapter listAdapter;

            btnAdd.setVisibility(View.GONE);
            listObjects = (RecyclerView) v.findViewById(R.id.list_objects);
            listObjects.setLayoutManager(new LinearLayoutManager(getActivity()));
            ResultWatcher<List<SalesPayment>> listener = new ResultWatcher<List<SalesPayment>>() {
                @Override
                public void onResult(Object source, List<SalesPayment> _payments) throws Exception {
                    Util.stopDialog(PROGRESS_FETCH_PAYMENTS);
                    listObjects.setAdapter(new SalesAdapter(_payments));
                }

                @Override
                public void onError(Object source, int errCode) throws Exception {
                    Util.stopDialog(PROGRESS_FETCH_PAYMENTS);
                    Toast.makeText(getActivity(), R.string.internal_server_error, Toast.LENGTH_SHORT).show();
                }
            };

            InvoiceService.fetchSalesPayments(listener);
            Util.showDialog(getFragmentManager(), PROGRESS_FETCH_PAYMENTS);
            return v;
        }
        catch(Exception e) {
            Log.d(Util.APP_TAG, "Error: " + e.getMessage());
            e.printStackTrace();
            Toast.makeText(getActivity(), R.string.internal_server_error, Toast.LENGTH_SHORT).show();
            return null;
        }
    }
//
//    @Override
//    public void onActivityResult(int _requestCode, int _resultCode, Intent data) throws Exception {
//
//    }

    private class SalesHolder extends RecyclerView.ViewHolder {

        public TextView textNo;
        public TextView textInvoiceNo;
        public TextView textInvoiceDate;
        public TextView textCustomer;
        public TextView textTotal;
        public TextView textMethod;
        public TextView textCardNumber;
        public TextView textCashier;
        public TextView textStatus;
        public ImageButton btnEdit;
        public ImageButton btnDelete;

        public EditText inputInvoiceNo;
        public EditText inputInvoiceDate;
        public EditText inputCustomer;
        public EditText inputTotal;
        public EditText inputMethod;
        public EditText inputCardNumber;
        public EditText inputCashier;
        public Spinner spinnerStatus;
        public ImageButton btnSearch;

        public SalesHolder(View itemView) {
            super(itemView);
            textNo = (TextView) itemView.findViewById(R.id.text_no);
            textInvoiceNo = (TextView) itemView.findViewById(R.id.text_invoice_no);
            textInvoiceDate = (TextView) itemView.findViewById(R.id.text_invoice_date);
            textCustomer = (TextView) itemView.findViewById(R.id.text_customer);
            textTotal = (TextView) itemView.findViewById(R.id.text_total_amount);
            textMethod = (TextView) itemView.findViewById(R.id.text_method);
//            textCardNumber = (TextView) itemView.findViewById(R.id.text_card_number);
            textCashier = (TextView) itemView.findViewById(R.id.text_cashier);
            textStatus = (TextView) itemView.findViewById(R.id.text_status);

            inputInvoiceNo = (EditText) itemView.findViewById(R.id.input_invoice_no);
            inputInvoiceDate = (EditText) itemView.findViewById(R.id.input_invoice_date);
            inputCustomer = (EditText) itemView.findViewById(R.id.input_customer);
//            inputTotal = (EditText) itemView.findViewById(R.id.input_total_amount);
            inputMethod = (EditText) itemView.findViewById(R.id.input_method);
//            inputCardNumber = (EditText) itemView.findViewById(R.id.input_card_number);
            inputCashier = (EditText) itemView.findViewById(R.id.input_cashier);
            spinnerStatus = (Spinner) itemView.findViewById(R.id.spinner_status);
            btnSearch = (ImageButton) itemView.findViewById(R.id.button_search);
        }
    }

    private class SalesAdapter extends RecycleViewAdapter<SalesPayment, SalesHolder>
    {
        private List<SalesPayment> fullPayments;
//        private static final int HEADER = -1;
//        private int editedPosition;

        public SalesAdapter(List<SalesPayment> _payments) {
            super(new LinkedList<>(_payments), RecycleViewAdapter.USE_FILTER);
            this.fullPayments = _payments;
        }


        @Override
        public SalesHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            try {
                LayoutInflater layoutInflater = LayoutInflater.from(getActivity());
                View view;
                if (viewType == HEADER)
                    view = layoutInflater.inflate(R.layout.sales_header, parent, false);
                else if(viewType == FILTER)
                    view = layoutInflater.inflate(R.layout.sales_filter, parent, false);
                else
                    view = layoutInflater.inflate(R.layout.sales_row, parent, false);
                return new SalesHolder(view);
            }
            catch(Exception e) {
                Log.d(Util.APP_TAG, "Error: " + e.getMessage());
                e.printStackTrace();
                Toast.makeText(getActivity(), R.string.internal_server_error, Toast.LENGTH_SHORT).show();
                return null;
            }
        }

        @Override
        public void onBindViewHolder(final SalesHolder holder, final int position) {
            try {
                if (position == 0 || products == null)
                    return;

                if(position > 1) {
                    Log.d(Util.APP_TAG, "POS: " + position);

                    final SalesPayment sp = this.products.get(position - 2);
                    holder.textNo.setText("" + (position - 1));
                    holder.textInvoiceNo.setText(sp.getParent().getNoInvoice());
                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd hh:mm:");
                    holder.textInvoiceDate.setText(sdf.format(sp.getParent().getNoInvoiceDate()));
                    holder.textCustomer.setText(sp.getParent().getCustomer().getName());
                    holder.textTotal.setText(String.valueOf(Math.round(sp.getParent().getTotal() * 100) / 100.0));
                    holder.textMethod.setText(sp.getPaymentMethod().getName());
//                    holder.textCardNumber.setText(sp.getCardNumber());
                    holder.textStatus.setText(sp.getParent().getPaid() ? "Paid" : "Unpaid");
                }
                else {
                    holder.btnSearch.setOnClickListener(
                            new View.OnClickListener()
                            {
                                @Override
                                public void onClick(View v)
                                {
                                    String filterInvNo = holder.inputInvoiceNo.getText().toString();
                                    String filterCustomer = holder.inputCustomer.getText().toString();
                                    String filterMethod = holder.inputMethod.getText().toString();
//                                    String filterCardNumber = holder.inputCardNumber.getText().toString();
                                    Integer posStatus = holder.spinnerStatus.getSelectedItemPosition();

                                    List<SalesPayment> filteredPayments = new LinkedList<>();
                                    if(fullPayments != null && fullPayments.size() > 0) {
                                        for(SalesPayment sp : fullPayments) {
//                                            Log.d(Util.APP_TAG, "NAME: " + filterName + " C NAME: " + ctgr.getName());
                                            if (filterInvNo != null && filterInvNo.length() > 0) {
                                                if(!sp.getParent().getNoInvoice().toLowerCase().contains(filterInvNo.toLowerCase()))
                                                    continue;
                                            }

                                            if (filterCustomer != null && filterCustomer.length() > 0) {
                                                if(!sp.getParent().getCustomer().getName().toLowerCase().contains(filterCustomer.toLowerCase()))
                                                    continue;
                                            }

                                            if (filterMethod != null && filterMethod.length() > 0) {
                                                if(!sp.getPaymentMethod().getName().toLowerCase().contains(filterMethod.toLowerCase()))
                                                    continue;
                                            }

//                                            if (filterCardNumber != null && filterCardNumber.length() > 0) {
//                                                if(sp.getCardNumber() != null) {
//                                                    if (!sp.getCardNumber().toLowerCase().contains(filterCardNumber.toLowerCase()))
//                                                        continue;
//                                                }
//                                                else
//                                                    continue;
//                                            }

                                            if(posStatus > 0) {
                                                boolean paid = posStatus == 1 ? true : false;
                                                if(sp.getParent().getPaid() != null) {
                                                    if (paid) {
                                                        if (!sp.getParent().getPaid())
                                                            continue;
                                                    }
                                                    else {
                                                        if(sp.getParent().getPaid())
                                                            continue;
                                                    }
                                                }
                                                else {
                                                    if(paid)
                                                        continue;
                                                }
                                            }

                                            filteredPayments.add(sp);
                                        }

                                        setItems(filteredPayments);
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
