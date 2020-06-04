package com.sunwell.pos.mobile.dialog;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.Toast;

import com.example.sunwell.pos_mobile.R;
import com.sunwell.pos.mobile.model.PaymentMethodObj;
import com.sunwell.pos.mobile.model.ProdCategory;
import com.sunwell.pos.mobile.service.InvoiceService;
import com.sunwell.pos.mobile.service.ProductService;
import com.sunwell.pos.mobile.util.ResultListener;
import com.sunwell.pos.mobile.util.ResultWatcher;
import com.sunwell.pos.mobile.util.Util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * Created by sunwell on 10/19/17.
 */

public class ChooseCategoryDialogFragment extends DialogFragment {

    private static final String PROGRESS_FETCH_CATEGORY = "progressFetchCategory";
//    private static String CATEGORY = "category";
//    private ResultListener<SalesInvoice> dialogListener ;
    private LinearLayout rootView ;
//    private List<ProdCategory> categories = new LinkedList<>();
    private Map<Button, ProdCategory> categories = new HashMap<>();
//    private List<TableRow> tableRows = new LinkedList<>();

    private ResultListener<List<ProdCategory>> categoriesListener ;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        try {
            final Dialog dialog = new Dialog(getActivity());
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            dialog.setContentView(R.layout.choose_item_dialog);
            rootView = (LinearLayout) dialog.findViewById(R.id.root);
            Button btnCancel = (Button)dialog.findViewById(R.id.button_close);
            Button btnOk = (Button)dialog.findViewById(R.id.button_ok);

            btnCancel.setOnClickListener(
                    new View.OnClickListener()
                    {
                        @Override
                        public void onClick(View v)
                        {
                            ChooseCategoryDialogFragment.this.dismiss();
                        }
                    }
            );

            btnOk.setOnClickListener(
                    new View.OnClickListener()
                    {
                        @Override
                        public void onClick(View v)
                        {
                            try {
                                LinkedList<ProdCategory> listCatgr = null;
                                if (categories.size() > 0) {
                                    Intent intent = new Intent();
                                    listCatgr = new LinkedList<>(categories.values());
                                    intent.putExtra("listCategory", listCatgr);
                                }
                                if (getTargetFragment() != null) {
                                    Intent intent = new Intent();
                                    intent.putExtra("listCategory", listCatgr);
                                    getTargetFragment().onActivityResult(getTargetRequestCode(), Util.RESULT_CODE_SUCCESS, intent);
                                }
                                if (categoriesListener != null) {
                                    categoriesListener.onResult(ChooseCategoryDialogFragment.this, listCatgr);
                                }
                                ChooseCategoryDialogFragment.this.dismiss();
                            }
                            catch (Exception e) {
                                Log.d(Util.APP_TAG, "Error: " + e.getMessage());
                                e.printStackTrace();
                                Toast.makeText(getActivity(), R.string.internal_server_error, Toast.LENGTH_SHORT).show();
                            }
                        }
                    }
            );

            final List<ProdCategory> list = new ArrayList<>();
            if (ProductService.categories != null) {
                for (ProdCategory ctgr : ProductService.categories) {
                    list.add(ctgr);
                }
                addPaymentMethods(list);
            } else {
                    ProductService.fetchCategories(
                            new ResultWatcher<List<ProdCategory>>()
                            {
                                @Override
                                public void onResult(Object source, List<ProdCategory> result) throws Exception
                                {
                                    Util.stopDialog(PROGRESS_FETCH_CATEGORY);
                                    if (result != null) {
                                        for (ProdCategory ctgr : result) {
                                            list.add(ctgr);
                                        }
                                        addPaymentMethods(list);
                                    }
                                }

                                @Override
                                public void onError(Object source, int errCode) throws Exception
                                {
                                    Util.stopDialog(PROGRESS_FETCH_CATEGORY);
                                    Toast.makeText(getActivity(), R.string.internal_server_error, Toast.LENGTH_SHORT).show();
                                }
                            }
                    );
                Util.showDialog(getFragmentManager(), PROGRESS_FETCH_CATEGORY);
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

    public void setCategoriesListener(ResultListener<List<ProdCategory>> _listener) {
        categoriesListener = _listener;
    }

    private void addPaymentMethods(List<ProdCategory> _list) {
        if(_list.size() > 0) {
            LinearLayout row = null;
            LayoutInflater inflater = LayoutInflater.from(getActivity());
            for (int i = 1; i < _list.size(); i++) {
                final ProdCategory ctgr = _list.get(i - 1);
                if( i % 4 == 1) {
                    row = new LinearLayout(getActivity());
                    LinearLayout.LayoutParams p = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                    row.setOrientation(LinearLayout.HORIZONTAL);
                    row.setLayoutParams(p);
                    rootView.addView(row, rootView.getChildCount() - 1);
                }
                final View btnRoot = inflater.inflate(R.layout.button_with_border, row, false);
                final Button button = (Button)btnRoot.findViewById(R.id.button_bordered);
//                btnRoot.getBackground().setAlpha(0);
                btnRoot.setBackgroundColor(Color.parseColor("#ffffff"));
                button.setText(ctgr.getName());
                button.setOnClickListener(
                        new View.OnClickListener()
                        {
                            @Override
                            public void onClick(View v)
                            {
                                if(categories.get(button) == null ) {
                                    Log.d(Util.APP_TAG, "ON CLICK CALLED, GET BUTTOn IS NULL");
//                                    btnRoot.getBackground().setAlpha(255);
                                    btnRoot.setBackgroundColor(Color.parseColor("#0f7858"));
                                    categories.put(button, ctgr);
                                }
                                else {
                                    Log.d(Util.APP_TAG, "ON CLICK CALLED, GET BUTTOn IS NOT NULL");
                                    btnRoot.setBackgroundColor(Color.parseColor("#ffffff"));
//                                    btnRoot.getBackground().setAlpha(0);
                                    categories.remove(button);
                                }

                                if(getTargetFragment() != null) {



//                                    Intent intent = new Intent();
//                                    intent.putExtra("paymentMethod", ctgr);
//                                    getTargetFragment().onActivityResult(getTargetRequestCode(), Util.RESULT_CODE_SUCCESS, intent);
//                                    ChooseCategoryDialogFragment.this.dismiss();
                                }
                            }
                        }
                );
                TableLayout.LayoutParams p = new TableLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                p.leftMargin=Util.dpToPx(6, getActivity());
//                categories.put(button, ctgr);
                row.addView(btnRoot);
            }

//            final Button button = new Button(getActivity());
//            button.getBackground().setAlpha(30);
//            button.setText("TEST");
//            button.setOnClickListener(
//                    new View.OnClickListener()
//                    {
//                        boolean select = false;
//                        @Override
//                        public void onClick(View v)
//                        {
//                            if(!select)
//                                button.getBackground().setAlpha(255);
//                            else
//                                button.getBackground().setAlpha(30);
//
//                            select = !select;
//                        }
//                    }
//            );
//            rootView.addView(button);
        }
    }
}
