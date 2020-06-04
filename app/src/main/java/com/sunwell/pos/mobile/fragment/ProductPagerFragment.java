package com.sunwell.pos.mobile.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
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
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.sunwell.pos_mobile.R;
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

public class ProductPagerFragment extends PagerFragmentNew<Product>
{

    private Product product ;
    private ProdCategory category ;
    private List<ProdCategory> categories = new LinkedList<>();
    private LinearLayout panelItem;
    private AutoCompleteTextView inputProduct ;
//    private AutoCompleteTextView inputCategory ;
//    private ImageButton btnSearch ;

    public static PagerFragmentNew<Product> newInstance(RecycleViewAdapter<Product, ? extends RecyclerView.ViewHolder> _adapter, int _objPerPage) {
//        Log.e(Util.APP_TAG, "NEW INSTANCe CALLED IN PPF");
        Bundle bundle = new Bundle();
        bundle.putSerializable(ADAPTER, _adapter);
        bundle.putSerializable(OBJ_PER_PAGE, _objPerPage);
        PagerFragmentNew<Product> fragment = new ProductPagerFragment();
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater _inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState)
    {
        try {
//            Log.e(Util.APP_TAG, "ON CREATE VIEW CALLED IN PPF");
            LinearLayout root = (LinearLayout) _inflater.inflate(R.layout.pager, container, false);
            panelItem = (LinearLayout) root.findViewById(R.id.panel_filter);
            inputProduct = (AutoCompleteTextView) root.findViewById(R.id.atc_product);
//            inputCategory = (AutoCompleteTextView) root.findViewById(R.id.atc_category);
//            btnSearch = (ImageButton) root.findViewById(R.id.button_search);
            final ImageButton btnClear = (ImageButton) root.findViewById(R.id.button_clear);
            final ImageButton btnCtgr = (ImageButton) root.findViewById(R.id.button_category);

            btn1 = (Button) root.findViewById(R.id.button_1);
            btn2 = (Button) root.findViewById(R.id.button_2);
            btn3 = (Button) root.findViewById(R.id.button_3);
            btn4 = (Button) root.findViewById(R.id.button_4);
            btn5 = (Button) root.findViewById(R.id.button_5);
            listButton.add(btn1);
            listButton.add(btn2);
            listButton.add(btn3);
            listButton.add(btn4);
            listButton.add(btn5);
            listViewObjects = (RecyclerView) root.findViewById(R.id.list_object);
            btnNext = (ImageButton) root.findViewById(R.id.button_next);
            btnBack = (ImageButton) root.findViewById(R.id.button_back);

            DisplayMetrics displayMetrics = this.getResources().getDisplayMetrics();
            if(!Util.isSmallScreen(getActivity()))
                listViewObjects.setLayoutManager(new GridLayoutManager(getActivity(), 4));
            else
                listViewObjects.setLayoutManager(new LinearLayoutManager(getActivity()));
//            adapter.setItems();
            listViewObjects.setAdapter(adapter);


//        LayoutParams params = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT) ;
//        this.setLayoutParams(params);
//        this.addView(root);

            ButtonListener listener = new ButtonListener();

            for(int i = 0 ; i < 5 ; i++) {
                Button button = listButton.get(i);
                button.setOnClickListener(listener);
            }

            for(int l = numberOfPages ; l < 5 ; l++) {
                Button button = listButton.get(l);
                button.setVisibility(View.INVISIBLE);
            }

//            Log.d(Util.APP_TAG, "NUP: " + numberOfPages);
            if (numberOfPages > 0) {
//                adapter
                moveToPage(1);
            }

            Log.e(Util.APP_TAG, "FILLINg AC");
            ProductService.fillProductAutoComplete(inputProduct, getActivity(), getFragmentManager());
//            ProductService.fillCategoryAutoComplete(inputCategory, getActivity(), getFragmentManager());

//            inputProduct.setOnItemSelectedListener(
//                    new AdapterView.OnItemSelectedListener()
//                    {
//
//                        @Override
//                        public void onItemSelected(AdapterView<?> parent, View view, int position, long id)
//                        {
//                            Log.d(Util.APP_TAG, "OIS CALLED");
//                            if(position > 0)
//                                product = (Product)inputProduct.getAdapter().getItem(position);
//                            else
//                                product = null;
//                        }
//
//                        @Override
//                        public void onNothingSelected(AdapterView<?> parent)
//                        {
//
//                        }
//                    }
//            );

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
                            dlg.setTargetFragment(ProductPagerFragment.this, Util.REQUEST_CODE_PICK);
                            dlg.show(getFragmentManager(), "pickCategories");
                        }
                    }
            );

//            inputCategory.setOnItemClickListener(
//                    new AdapterView.OnItemClickListener()
//                    {
//
//                        @Override
//                        public void onItemClick(AdapterView<?> parent, View view, int position, long id)
//                        {
//                            final ProdCategory ctgr = (ProdCategory)parent.getItemAtPosition(position);
//                            Log.d(Util.APP_TAG, "CTGR IS: " + ctgr);
//                            for(ProdCategory c : categories) {
//                                if(c.equals(ctgr))
//                                    return;
//                            }
//
//                            final View ctgrItem = LayoutInflater.from(getActivity()).inflate(R.layout.category_item, panelItem, false);
//                            TextView txtCtgr = (TextView)ctgrItem.findViewById(R.id.text_category);
////                            ImageButton btnDelete = (ImageButton)ctgrItem.findViewById(R.id.button_delete_item);
//                            txtCtgr.setText(ctgr.getName());
//                            txtCtgr.setOnClickListener(
//                                    new View.OnClickListener()
//                                    {
//                                        @Override
//                                        public void onClick(View v)
//                                        {
//                                            panelItem.removeView(ctgrItem);
//                                            categories.remove(ctgr);
//                                            btnSearch.performClick();
//                                        }
//                                    }
//                            );
//                            panelItem.addView(ctgrItem, panelItem.getChildCount() - 2);
//                            categories.add(ctgr);
//                            btnSearch.performClick();
//                            inputCategory.setText("");
//                        }
//                    }
//            );

//            inputCategory.setOnItemSelectedListener(
//                    new AdapterView.OnItemSelectedListener()
//                    {
//
//                        @Override
//                        public void onItemSelected(AdapterView<?> parent, View view, int position, long id)
//                        {
//                            Log.d(Util.APP_TAG, "OIS CALLED");
//                            if(position > 0)
//                                category = (ProdCategory) inputProduct.getAdapter().getItem(position);
//                            else
//                                category = null;
//                        }
//
//                        @Override
//                        public void onNothingSelected(AdapterView<?> parent)
//                        {
//
//                        }
//                    }
//            );

//            btnSearch.setOnClickListener(
//                    new View.OnClickListener()
//                    {
//                        @Override
//                        public void onClick(View v)
//                        {
////                            if((inputProduct.getText() != null && inputProduct.getText().toString().length() > 0) || category != null) {
//                            String name = inputProduct.getText() != null ? inputProduct.getText().toString() : null;
////                                Product prod = null;
////                                if(name != null && name.length() > 0)
////                                   prod = ProductService.findProductByName(name);
//
////                                if(product != null)
////                                    name = product.getName();
//
////                                ProdCategory ctgr = (ProdCategory)spinnerCategory.getSelectedItem();
//                            Log.d(Util.APP_TAG, "text name: " + name);
//                            if(fullListObjects.size() > 0) {
////                                    List<Product> filteredProducts = new LinkedList<>();
//                                listObjects = new LinkedList<>();
//                                for (Product p : fullListObjects) {
//                                    if(name != null && name.length() > 0)
//                                        if(!p.getName().toLowerCase().contains(name.toLowerCase()))
//                                            continue;
//
//                                    if(categories.size() > 0) {
////                                        Boolean found = false;
//                                        List<ProdCategory> filterCtgr = new LinkedList<>(categories);
//                                        if (p.getCategories() != null) {
//                                            for (ProdCategory c : p.getCategories()) {
//                                                for (ProdCategory ctgr : categories) {
//                                                    if (c.equals(ctgr)) {
//                                                        filterCtgr.remove(ctgr);
//                                                        break;
//                                                    }
//                                                }
//                                            }
//                                        }
//
//                                        if(filterCtgr.size() > 0)
//                                            continue;;
//                                    }
//                                    listObjects.add(p);
//                                }
//                                refreshPaging();
//                                adapter.setItems(listObjects);
//                                if(numberOfPages > 0)
//                                    moveToPage(1);
//                            }
////                            }
//                        }
//                    }
//            );

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
            if (_requestCode == Util.REQUEST_CODE_PICK) {
                if (_resultCode == Util.RESULT_CODE_SUCCESS) {
                    Log.d(Util.APP_TAG, "RESULT CODe SUCCESS");
                    Serializable result = data.getSerializableExtra("listCategory");
                    if(result != null) {
                        categories = (List<ProdCategory>) result;
                        for(ProdCategory ctg : categories) {
                            addCategoryItem(ctg);
                        }
                        refresh();
                    }
                }
//                else if(_requestCode == Util.RESULT_CODE_FAILED) {
//                    Log.d(Util.APP_TAG, " ERR CODE: " + data.getIntExtra("errorCode", -3));
//                    Toast.makeText(getActivity(), R.string.fail_add_invoice_line, Toast.LENGTH_SHORT).show();
//                }
            }
        }
        catch(Exception e) {
            Log.d(Util.APP_TAG, "Error: " + e.getMessage());
            e.printStackTrace();
            Toast.makeText(getActivity(), R.string.internal_server_error, Toast.LENGTH_SHORT).show();
        }
    }

    private void addCategoryItem(final ProdCategory _ctgr) {
//        final ProdCategory ctgr = (ProdCategory)parent.getItemAtPosition(position);
        Log.d(Util.APP_TAG, "CTGR IS: " + _ctgr);
//        for(ProdCategory c : categories) {
//            if(c.equals(_ctgr))
//                return;
//        }

        final View ctgrItem = LayoutInflater.from(getActivity()).inflate(R.layout.category_item, panelItem, false);
        TextView txtCtgr = (TextView)ctgrItem.findViewById(R.id.text_category);
//                            ImageButton btnDelete = (ImageButton)ctgrItem.findViewById(R.id.button_delete_item);
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
//        categories.add(_ctgr);
        refresh();
//        inputCategory.setText("");
    }

    private void refresh()
    {
//        if ((inputProduct.getText() != null && inputProduct.getText().toString().length() > 0) || category != null) {
        Log.d(Util.APP_TAG, "REFRESH()");
        String name = inputProduct.getText() != null ? inputProduct.getText().toString() : null;
//                                Product prod = null;
//                                if(name != null && name.length() > 0)
//                                   prod = ProductService.findProductByName(name);

//                                if(product != null)
//                                    name = product.getName();

//                                ProdCategory ctgr = (ProdCategory)spinnerCategory.getSelectedItem();
        Log.d(Util.APP_TAG, "text name: " + name);
        if (fullListObjects.size() > 0) {
//                                    List<Product> filteredProducts = new LinkedList<>();
            listObjects = new LinkedList<>();
            for (Product p : fullListObjects) {
                if (name != null && name.length() > 0)
                    if (!p.getName().toLowerCase().contains(name.toLowerCase()))
                        continue;

                Log.d(Util.APP_TAG, "CTGR SIZE: " + categories.size());
                if (categories.size() > 0) {
//                                        Boolean found = false;
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
            adapter.setItems(listObjects);
            if (numberOfPages > 0)
                moveToPage(1);
        }
    }
//    }

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
