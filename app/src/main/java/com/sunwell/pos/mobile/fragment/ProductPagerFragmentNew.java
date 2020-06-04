package com.sunwell.pos.mobile.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.sunwell.pos_mobile.R;
import com.sunwell.pos.mobile.controller.PagerController;
import com.sunwell.pos.mobile.controller.PagerProductController;
import com.sunwell.pos.mobile.dialog.ChooseCategoryDialogFragment;
import com.sunwell.pos.mobile.model.ProdCategory;
import com.sunwell.pos.mobile.model.Product;
import com.sunwell.pos.mobile.service.ProductService;
import com.sunwell.pos.mobile.util.RecycleViewAdapter;
import com.sunwell.pos.mobile.util.Util;

import java.io.Serializable;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by sunwell on 1/16/18.
 */

public class ProductPagerFragmentNew extends Fragment
{

//    private Product product ;
//    private ProdCategory category ;
//    private List<ProdCategory> categories = new LinkedList<>();
//    private LinearLayout panelItem;
//    private AutoCompleteTextView inputProduct ;
//    private AutoCompleteTextView inputCategory ;
//    private ImageButton btnSearch ;
    RecycleViewAdapter<Product, RecyclerView.ViewHolder> adapter ;

    private PagerProductController controller ;

//    public static ProductPagerFragmentNew newInstance(RecycleViewAdapter<Product, ? extends RecyclerView.ViewHolder> _adapter, int _objPerPage) {
////        Log.e(Util.APP_TAG, "NEW INSTANCe CALLED IN PPF");
//        Bundle bundle = new Bundle();
//        bundle.putSerializable(PagerController.ADAPTER, _adapter);
//        bundle.putInt(PagerController.OBJ_PER_PAGE, _objPerPage);
//        ProductPagerFragmentNew fragment = new ProductPagerFragmentNew();
//        fragment.setArguments(bundle);
//        return fragment;
//    }

    public static ProductPagerFragmentNew newInstance(int _objPerPage) {
//        Log.e(Util.APP_TAG, "NEW INSTANCe CALLED IN PPF");
//        bundle.putSerializable(PagerController.ADAPTER, _adapter);
        Bundle bundle = new Bundle();
        bundle.putInt(PagerController.OBJ_PER_PAGE, _objPerPage);
        ProductPagerFragmentNew fragment = new ProductPagerFragmentNew();
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater _inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState)
    {
        try {
            LinearLayout root = (LinearLayout) _inflater.inflate(R.layout.pager, container, false);
//            RecycleViewAdapter<Product, RecyclerView.ViewHolder> adapter = (RecycleViewAdapter<Product, RecyclerView.ViewHolder>) getArguments().get(PagerController.ADAPTER);
            Integer objectPerPages = (Integer) getArguments().get(PagerController.OBJ_PER_PAGE);
            controller = new PagerProductController(getActivity(), getFragmentManager(), root, adapter, objectPerPages);
//            panelItem = (LinearLayout) root.findViewById(R.id.panel_filter);
//            inputProduct = (AutoCompleteTextView) root.findViewById(R.id.atc_product);
//            final ImageButton btnClear = (ImageButton) root.findViewById(R.id.button_clear);
//            final ImageButton btnCtgr = (ImageButton) root.findViewById(R.id.button_category);
//
//            btn1 = (Button) root.findViewById(R.id.button_1);
//            btn2 = (Button) root.findViewById(R.id.button_2);
//            btn3 = (Button) root.findViewById(R.id.button_3);
//            btn4 = (Button) root.findViewById(R.id.button_4);
//            btn5 = (Button) root.findViewById(R.id.button_5);
//            listButton.add(btn1);
//            listButton.add(btn2);
//            listButton.add(btn3);
//            listButton.add(btn4);
//            listButton.add(btn5);
//            listViewObjects = (RecyclerView) root.findViewById(R.id.list_object);
//            btnNext = (ImageButton) root.findViewById(R.id.button_next);
//            btnBack = (ImageButton) root.findViewById(R.id.button_back);
//
//            DisplayMetrics displayMetrics = this.getResources().getDisplayMetrics();
//            if(!Util.isSmallScreen(getActivity()))
//                listViewObjects.setLayoutManager(new GridLayoutManager(getActivity(), 4));
//            else
//                listViewObjects.setLayoutManager(new LinearLayoutManager(getActivity()));
//            listViewObjects.setAdapter(adapter);
//
//            ButtonListener listener = new ButtonListener();
//
//            for(int i = 0 ; i < 5 ; i++) {
//                Button button = listButton.get(i);
//                button.setOnClickListener(listener);
//            }
//
//            for(int l = numberOfPages ; l < 5 ; l++) {
//                Button button = listButton.get(l);
//                button.setVisibility(View.INVISIBLE);
//            }
//
//            if (numberOfPages > 0) {
//                moveToPage(1);
//            }
//
//            Log.e(Util.APP_TAG, "FILLINg AC");
//            ProductService.fillProductAutoComplete(inputProduct, getActivity(), getFragmentManager());
//
//            btnClear.setOnClickListener(
//                    new View.OnClickListener()
//                    {
//                        @Override
//                        public void onClick(View v)
//                        {
//                            product = null;
//                            inputProduct.setText("");
//                            refresh();
//                        }
//                    }
//            );
//
//            inputProduct.setOnItemClickListener(
//                    new AdapterView.OnItemClickListener()
//                    {
//                        @Override
//                        public void onItemClick(AdapterView<?> parent, View view, int position, long id)
//                        {
//                            product = (Product)parent.getItemAtPosition(position);
//                            refresh();
//                        }
//                    }
//            );
//
//            btnCtgr.setOnClickListener(
//                    new View.OnClickListener()
//                    {
//                        @Override
//                        public void onClick(View view)
//                        {
//                            ChooseCategoryDialogFragment dlg = new ChooseCategoryDialogFragment();
//                            dlg.setTargetFragment(ProductPagerFragmentNew.this, Util.REQUEST_CODE_PICK);
//                            dlg.show(getFragmentManager(), "pickCategories");
//                        }
//                    }
//            );
//
            return root;
        }
        catch(Exception e) {
            Log.d(Util.APP_TAG, "Error: " + e.getMessage());
            e.printStackTrace();
            Toast.makeText(getActivity(), R.string.error_product_list, Toast.LENGTH_SHORT).show();
            return null;
        }
    }

    @Override
    public void onActivityResult(int _requestCode, int _resultCode, Intent data) {
        try {
            controller.onActivityResult(_requestCode, _resultCode, data);
//            if (_requestCode == Util.REQUEST_CODE_PICK) {
//                if (_resultCode == Util.RESULT_CODE_SUCCESS) {
//                    Log.d(Util.APP_TAG, "RESULT CODe SUCCESS");
//                    Serializable result = data.getSerializableExtra("listCategory");
//                    if(result != null) {
//                        categories = (List<ProdCategory>) result;
//                        for(ProdCategory ctg : categories) {
//                            addCategoryItem(ctg);
//                        }
//                        refresh();
//                    }
//                }
//            }
        }
        catch(Exception e) {
            Log.d(Util.APP_TAG, "Error: " + e.getMessage());
            e.printStackTrace();
            Toast.makeText(getActivity(), R.string.internal_server_error, Toast.LENGTH_SHORT).show();
        }
    }

    // harus diset sebelum fragment ini di attach karena nggak bisa ditaruh di method static newInstnace
    // karena adapternya nggak bisa diserialisasi karena adapternya adalah inner class dari fragment
    // dan fragment nggak bisa diserialisasi dan adapter tersebut nggak bisa jadi static inner classs
    // karena adapter tersebut membutuhkan context yang hanya bisa didapat bila tidak menjadi sattic inner class

    public void setAdapter(RecycleViewAdapter<Product, ? extends RecyclerView.ViewHolder> _adapter) {
        adapter = (RecycleViewAdapter<Product, RecyclerView.ViewHolder>)_adapter;
    }

//    private void addCategoryItem(final ProdCategory _ctgr) {
//        Log.d(Util.APP_TAG, "CTGR IS: " + _ctgr);
//        final View ctgrItem = LayoutInflater.from(getActivity()).inflate(R.layout.category_item, panelItem, false);
//        TextView txtCtgr = (TextView)ctgrItem.findViewById(R.id.text_category);
//        txtCtgr.setText(_ctgr.getName());
//        txtCtgr.setOnClickListener(
//                new View.OnClickListener()
//                {
//                    @Override
//                    public void onClick(View v)
//                    {
//                        panelItem.removeView(ctgrItem);
//                        categories.remove(_ctgr);
//                        refresh();
//                    }
//                }
//        );
//        panelItem.addView(ctgrItem, panelItem.getChildCount() - 2);
//        refresh();
//    }
//
//    private void refresh()
//    {
//        Log.d(Util.APP_TAG, "REFRESH()");
//        String name = inputProduct.getText() != null ? inputProduct.getText().toString() : null;
//        Log.d(Util.APP_TAG, "text name: " + name);
//        if (fullListObjects.size() > 0) {
//            listObjects = new LinkedList<>();
//            for (Product p : fullListObjects) {
//                if (name != null && name.length() > 0)
//                    if (!p.getName().toLowerCase().contains(name.toLowerCase()))
//                        continue;
//
//                Log.d(Util.APP_TAG, "CTGR SIZE: " + categories.size());
//                if (categories.size() > 0) {
//                    List<ProdCategory> filterCtgr = new LinkedList<>(categories);
//                    if (p.getCategories() != null) {
//                        for (ProdCategory c : p.getCategories()) {
//                            for (ProdCategory ctgr : categories) {
//                                if (c.equals(ctgr)) {
//                                    filterCtgr.remove(ctgr);
//                                    break;
//                                }
//                            }
//                        }
//                    }
//
//                    Log.d(Util.APP_TAG, "FILTER SIZE: " + filterCtgr.size());
//                    if (filterCtgr.size() > 0)
//                        continue;
//                    ;
//                }
//                listObjects.add(p);
//            }
//            refreshPaging();
//            adapter.setItems(listObjects);
//            if (numberOfPages > 0)
//                moveToPage(1);
//        }
//    }
//
//    protected void refreshPaging() {
//        recalculateNumberOfPages();
//
//        for (Button b:listButton) {
//            b.setVisibility(View.VISIBLE);
//        }
//
//        for(int l = numberOfPages ; l < 5 ; l++) {
//            Button button = listButton.get(l);
//            button.setVisibility(View.INVISIBLE);
//        }
//    }
}
