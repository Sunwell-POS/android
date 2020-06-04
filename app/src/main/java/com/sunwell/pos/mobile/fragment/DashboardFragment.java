package com.sunwell.pos.mobile.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.example.sunwell.pos_mobile.R;
import com.sunwell.pos.mobile.model.ProdCategory;
import com.sunwell.pos.mobile.model.SalesInvoice;
import com.sunwell.pos.mobile.util.RecycleViewAdapter;
import com.sunwell.pos.mobile.util.ResultWatcher;
import com.sunwell.pos.mobile.util.Util;

import java.util.LinkedList;
import java.util.List;

/**
 * Created by sunwell on 10/18/17.
 */

public class DashboardFragment extends Fragment {

    private static final String PROGRESS_FETCH_CATEGORY = "progressFetchCustomer";
    private static final String PROGRESS_DELETE_CATEGORY = "progressDeleteCategory";

    private RecyclerView listInvoice;
    private ResultWatcher<String> invoiceItemListener;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        try {
            Log.d(Util.APP_TAG, "OCV ON DF CALLED");
            View v = inflater.inflate(R.layout.layout_dashboard, container, false);

            listInvoice = (RecyclerView) v.findViewById(R.id.list_invoice);
            listInvoice.setLayoutManager(new LinearLayoutManager(getActivity()));
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

    }

    public void setSalesInvoices(List<SalesInvoice> _invoices) {
        listInvoice.setAdapter(new InvoiceAdapter(_invoices));
    }

    public void setInvoiceItemListener(ResultWatcher<String> _listener) {
        invoiceItemListener = _listener;
    }

    private class InvoiceHolder extends RecyclerView.ViewHolder {
        public TextView textNumber;

        public InvoiceHolder(View itemView) {
            super(itemView);
            textNumber = (TextView) itemView.findViewById(R.id.text_invoice);
        }
    }

    private class InvoiceAdapter extends RecycleViewAdapter<SalesInvoice, InvoiceHolder>
    {
        public InvoiceAdapter(List<SalesInvoice> _invoices) {
            super(new LinkedList<>(_invoices), RecycleViewAdapter.USE_HEADER);
        }

        @Override
        public InvoiceHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            try {
                LayoutInflater layoutInflater = LayoutInflater.from(getActivity());
                View view;
                if (viewType == HEADER)
                    view = layoutInflater.inflate(R.layout.invoice_number_header, parent, false);
                else
                    view = layoutInflater.inflate(R.layout.invoice_number, parent, false);
                return new InvoiceHolder(view);
            }
            catch(Exception e) {
                Log.d(Util.APP_TAG, "Error: " + e.getMessage());
                e.printStackTrace();
                Toast.makeText(getActivity(), R.string.internal_server_error, Toast.LENGTH_SHORT).show();
                return null;
            }
        }

        @Override
        public void onBindViewHolder(final InvoiceHolder holder, final int position) {
            try {
                if (position == 0 || products == null)
                    return;

                else {
                    final SalesInvoice si = this.products.get(position - 1);
                    holder.textNumber.setText(si.getNoInvoice());
                    holder.textNumber.setOnClickListener(
                            new View.OnClickListener()
                            {
                                @Override
                                public void onClick(View v)
                                {
                                    try {
                                        if (invoiceItemListener != null) {
                                            invoiceItemListener.onResult(DashboardFragment.this, si.getNoInvoice());
                                        }
                                    }
                                    catch(Exception e) {
                                        Log.d(Util.APP_TAG, "Error: " + e.getMessage());
                                        e.printStackTrace();
                                        Toast.makeText(getActivity(), R.string.internal_server_error, Toast.LENGTH_SHORT).show();
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

//    @Override
//    public void onSaveInstanceState(Bundle outState)
//    {
//        super.onSaveInstanceState(outState);
//        Log.d(Util.APP_TAG, " BEFORE CONF CHANGE: " + invoiceItemListener);
//        outState.putSerializable("invoiceItemListener", invoiceItemListener);
//    }
//
//    @Override
//    public void onActivityCreated(@Nullable Bundle savedInstanceState)
//    {
//        super.onActivityCreated(savedInstanceState);
//        if(savedInstanceState != null)
//            invoiceItemListener = (ResultWatcher<String>)savedInstanceState.getSerializable("invoiceItemListener");
//        Log.d(Util.APP_TAG, " AFTER CONF CHANGE: " + invoiceItemListener);
//    }
}
