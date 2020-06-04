package com.sunwell.pos.mobile.controller;

import android.content.Context;
import android.content.Intent;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.sunwell.pos_mobile.R;
import com.sunwell.pos.mobile.dialog.ChooseCategoryDialogFragment;
import com.sunwell.pos.mobile.fragment.PagerFragmentNew;
import com.sunwell.pos.mobile.fragment.ProductPagerFragment;
import com.sunwell.pos.mobile.model.ProdCategory;
import com.sunwell.pos.mobile.model.Product;
import com.sunwell.pos.mobile.service.ProductService;
import com.sunwell.pos.mobile.util.RecycleViewAdapter;
import com.sunwell.pos.mobile.util.ResultWatcher;
import com.sunwell.pos.mobile.util.Util;

import java.io.Serializable;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by sunwell on 1/19/18.
 */

public class PagerProductController extends PagerController<Product>
{
    private Product product ;
    private ProdCategory category ;
    private List<ProdCategory> categories = new LinkedList<>();
    private LinearLayout panelItem;
    private AutoCompleteTextView inputProduct ;

    public PagerProductController(Context _ctx, final FragmentManager _fm, View _root, RecycleViewAdapter<Product, ? extends RecyclerView.ViewHolder> _adapter, int _objPerPage)
    {
        super(_adapter, _objPerPage);
        try {
            ctx = _ctx;
            panelItem = (LinearLayout) _root.findViewById(R.id.panel_filter);
            inputProduct = (AutoCompleteTextView) _root.findViewById(R.id.atc_product);
            final ImageButton btnClear = (ImageButton) _root.findViewById(R.id.button_clear);
            final ImageButton btnCtgr = (ImageButton) _root.findViewById(R.id.button_category);

            btn1 = (Button) _root.findViewById(R.id.button_1);
            btn2 = (Button) _root.findViewById(R.id.button_2);
            btn3 = (Button) _root.findViewById(R.id.button_3);
            btn4 = (Button) _root.findViewById(R.id.button_4);
            btn5 = (Button) _root.findViewById(R.id.button_5);
            listButton.add(btn1);
            listButton.add(btn2);
            listButton.add(btn3);
            listButton.add(btn4);
            listButton.add(btn5);
            listViewObjects = (RecyclerView) _root.findViewById(R.id.list_object);
            btnNext = (ImageButton) _root.findViewById(R.id.button_next);
            btnBack = (ImageButton) _root.findViewById(R.id.button_back);

            if(!Util.isSmallScreen(ctx))
                listViewObjects.setLayoutManager(new GridLayoutManager(ctx, 4));
            else
                listViewObjects.setLayoutManager(new LinearLayoutManager(ctx));
            listViewObjects.setAdapter(adapter);

            ButtonListener listener = new ButtonListener();

            for(int i = 0 ; i < 5 ; i++) {
                Button button = listButton.get(i);
                button.setOnClickListener(listener);
            }

            for(int l = numberOfPages ; l < 5 ; l++) {
                Button button = listButton.get(l);
                button.setVisibility(View.INVISIBLE);
            }

            if (numberOfPages > 0) {
                moveToPage(1);
            }

            Log.e(Util.APP_TAG, "FILLINg AC");
            ProductService.fillProductAutoComplete(inputProduct, ctx, _fm);

            btnClear.setOnClickListener(
                    new View.OnClickListener()
                    {
                        @Override
                        public void onClick(View v)
                        {
                            product = null;
                            inputProduct.setText("");
                            refresh();
                        }
                    }
            );

            inputProduct.setOnItemClickListener(
                    new AdapterView.OnItemClickListener()
                    {
                        @Override
                        public void onItemClick(AdapterView<?> parent, View view, int position, long id)
                        {
                            product = (Product)parent.getItemAtPosition(position);
                            refresh();
                        }
                    }
            );

            btnCtgr.setOnClickListener(
                    new View.OnClickListener()
                    {
                        @Override
                        public void onClick(View view)
                        {
                            ChooseCategoryDialogFragment dlg = new ChooseCategoryDialogFragment();
//                            dlg.setTargetFragment(_fragment, Util.REQUEST_CODE_PICK);
                            dlg.setCategoriesListener(
                                    new ResultWatcher<List<ProdCategory>>()
                                    {
                                        @Override
                                        public void onResult(Object source, List<ProdCategory> result) throws Exception
                                        {
                                            if(result != null) {
                                                categories = result;
                                                addCategories();
                                                refresh();
                                            }
                                        }
                                    }
                            );
                            dlg.show(_fm, "pickCategories");
                        }
                    }
            );
        }
        catch(Exception e) {
            Log.d(Util.APP_TAG, "Error: " + e.getMessage());
            e.printStackTrace();
            Toast.makeText(ctx, R.string.error_product_list, Toast.LENGTH_SHORT).show();
        }
    }

    public void onActivityResult(int _requestCode, int _resultCode, Intent data) throws Exception{
        if (_requestCode == Util.REQUEST_CODE_PICK) {
            if (_resultCode == Util.RESULT_CODE_SUCCESS) {
                Log.d(Util.APP_TAG, "RESULT CODe SUCCESS");
                Serializable result = data.getSerializableExtra("listCategory");
                if(result != null) {
                    categories = (List<ProdCategory>) result;
                    addCategories();
                    refresh();
                }
            }
        }
    }

    private void addCategories() {
        for(int i = panelItem.getChildCount() - 3 ; i > 0 ; i --) {
            panelItem.removeViewAt(i);
        }

        for(ProdCategory ctg : categories) {
            addCategoryItem(ctg);
        }
    }

    private void addCategoryItem(final ProdCategory _ctgr) {
        Log.d(Util.APP_TAG, "CTGR IS: " + _ctgr);
        final View ctgrItem = LayoutInflater.from(ctx).inflate(R.layout.category_item, panelItem, false);
        TextView txtCtgr = (TextView)ctgrItem.findViewById(R.id.text_category);
        txtCtgr.setText(_ctgr.getName());
        txtCtgr.setOnClickListener(
                new View.OnClickListener()
                {
                    @Override
                    public void onClick(View v)
                    {
                        panelItem.removeView(ctgrItem);
                        categories.remove(_ctgr);
                        refresh();
                    }
                }
        );
        panelItem.addView(ctgrItem, panelItem.getChildCount() - 2);
        refresh();
    }

    private void refresh()
    {
        Log.d(Util.APP_TAG, "REFRESH()");
        String name = inputProduct.getText() != null ? inputProduct.getText().toString() : null;
        Log.d(Util.APP_TAG, "text name: " + name);
        if (fullListObjects.size() > 0) {
            listObjects = new LinkedList<>();
            for (Product p : fullListObjects) {
                if (name != null && name.length() > 0)
                    if (!p.getName().toLowerCase().contains(name.toLowerCase()))
                        continue;

                Log.d(Util.APP_TAG, "CTGR SIZE: " + categories.size());
                if (categories.size() > 0) {
                    List<ProdCategory> filterCtgr = new LinkedList<>(categories);
                    if (p.getCategories() != null) {
                        for (ProdCategory c : p.getCategories()) {
                            for (ProdCategory ctgr : categories) {
                                if (c.equals(ctgr)) {
                                    filterCtgr.remove(ctgr);
                                    break;
                                }
                            }
                        }
                    }

                    Log.d(Util.APP_TAG, "FILTER SIZE: " + filterCtgr.size());
                    if (filterCtgr.size() > 0)
                        continue;
                    ;
                }
                listObjects.add(p);
            }
            refreshPaging();
            adapter.setItems(listObjects); // mesti dikasih linked list agar bisa serializable
            if (numberOfPages > 0)
                moveToPage(1);
        }
    }

    protected void refreshPaging() {
        recalculateNumberOfPages();

        for (Button b:listButton) {
            b.setVisibility(View.VISIBLE);
        }

        for(int l = numberOfPages ; l < 5 ; l++) {
            Button button = listButton.get(l);
            button.setVisibility(View.INVISIBLE);
        }
    }
}
