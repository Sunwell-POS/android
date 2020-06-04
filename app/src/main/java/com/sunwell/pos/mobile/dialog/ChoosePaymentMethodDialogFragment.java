package com.sunwell.pos.mobile.dialog;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.Toast;

import com.example.sunwell.pos_mobile.R;
import com.sunwell.pos.mobile.model.PaymentMethodObj;
import com.sunwell.pos.mobile.model.SalesInvoice;
import com.sunwell.pos.mobile.service.InvoiceService;
import com.sunwell.pos.mobile.util.ResultListener;
import com.sunwell.pos.mobile.util.ResultWatcher;
import com.sunwell.pos.mobile.util.Util;

import java.util.ArrayList;
import java.util.EmptyStackException;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by sunwell on 10/19/17.
 */

public class ChoosePaymentMethodDialogFragment extends DialogFragment {

    private static final String PROGRESS_FETCH_PAYMENT_METHOD = "progressFetchPaymentMethod";
//    private static String CATEGORY = "category";
//    private ResultListener<SalesInvoice> dialogListener ;
    private TableLayout rootView ;
//    private List<TableRow> tableRows = new LinkedList<>();

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        try {
            final Dialog dialog = new Dialog(getActivity());
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            dialog.setContentView(R.layout.choose_payment_method_dialog);
            rootView = (TableLayout) dialog.findViewById(R.id.root);

            final List<PaymentMethodObj> list = new ArrayList<>();
            if (InvoiceService.paymentMethods != null) {
                for (PaymentMethodObj pm : InvoiceService.paymentMethods) {
                    list.add(pm);
                }
                addPaymentMethods(list);
            } else {
                    InvoiceService.fetchPaymentMethods(
                            new ResultWatcher<List<PaymentMethodObj>>()
                            {
                                @Override
                                public void onResult(Object source, List<PaymentMethodObj> result) throws Exception
                                {
                                    Util.stopDialog(PROGRESS_FETCH_PAYMENT_METHOD);
                                    if (result != null) {
                                        for (PaymentMethodObj pm : result) {
                                            list.add(pm);
                                        }
                                        addPaymentMethods(list);
                                    }
                                }

                                @Override
                                public void onError(Object source, int errCode) throws Exception
                                {
                                    Util.stopDialog(PROGRESS_FETCH_PAYMENT_METHOD);
                                    Toast.makeText(getActivity(), R.string.internal_server_error, Toast.LENGTH_SHORT).show();
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
            return  null;
        }
    }

    private void addPaymentMethods(List<PaymentMethodObj> _list) {
        if(_list.size() > 0) {
            TableRow tr = null;
            for (int i = 1; i < _list.size(); i++) {
                final PaymentMethodObj pm = _list.get(i - 1);
                if( i % 5 == 1) {
                    tr = new TableRow(getActivity());
                    rootView.addView(tr, rootView.getChildCount() - 1);
                }
                Button button = new Button(getActivity());
                button.setText(pm.getName());
                button.setOnClickListener(
                        new View.OnClickListener()
                        {
                            @Override
                            public void onClick(View v)
                            {
                                if(getTargetFragment() != null) {
                                    Intent intent = new Intent();
                                    intent.putExtra("paymentMethod", pm);
                                    getTargetFragment().onActivityResult(getTargetRequestCode(), Util.RESULT_CODE_SUCCESS, intent);
                                    ChoosePaymentMethodDialogFragment.this.dismiss();
                                }
                            }
                        }
                );
                TableLayout.LayoutParams p = new TableLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                p.leftMargin=Util.dpToPx(6, getActivity());
                tr.addView(button);
            }
        }
    }
}
