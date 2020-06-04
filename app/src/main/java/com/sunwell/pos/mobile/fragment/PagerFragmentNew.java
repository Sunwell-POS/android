package com.sunwell.pos.mobile.fragment;

import android.graphics.Color;
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
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.sunwell.pos_mobile.R;
import com.sunwell.pos.mobile.model.ProdCategory;
import com.sunwell.pos.mobile.service.ProductService;
import com.sunwell.pos.mobile.util.RecycleViewAdapter;
import com.sunwell.pos.mobile.util.Util;

import java.util.LinkedList;
import java.util.List;

/**
 * Created by sunwell on 11/13/17.
 */

public abstract class PagerFragmentNew<T> extends Fragment
{
    protected static final String ADAPTER = "adapter";
    protected static final String OBJ_PER_PAGE = "objPerPage";
    protected int numberOfPages = 0;
    protected int objectPerPages = 1;
    protected int currentPage = -1;
//    private NamedObjectTranslator<T> translator;
    protected List<T> fullListObjects;
    protected List<T> listObjects;
    protected RecycleViewAdapter<T, RecyclerView.ViewHolder> adapter;
    protected RecyclerView listViewObjects;
    protected Button btn1;
    protected Button btn2;
    protected Button btn3;
    protected Button btn4;
    protected Button btn5;
    protected ImageButton btnNext;
    protected ImageButton btnBack;
    protected List<Button> listButton = new LinkedList<>();

//    public static <V> PagerFragmentNew newInstance(RecycleViewAdapter<V, ? extends RecyclerView.ViewHolder> _adapter, int _objPerPage) {
//        Log.d(Util.APP_TAG, "NEW INSTANCe CALLED IN PF");
//        Bundle bundle = new Bundle();
//        bundle.putSerializable(ADAPTER, _adapter);
//        bundle.putSerializable(OBJ_PER_PAGE, _objPerPage);
//        PagerFragmentNew<V> fragment = new PagerFragmentNew<>();
//        fragment.setArguments(bundle);
//        return fragment;
//    }



    public void onCreate(@Nullable Bundle savedInstanceState)
    {
        try {
            super.onCreate(savedInstanceState);
            Log.d(Util.APP_TAG, "ON CREATE CALLED IN PF");
            if(savedInstanceState != null)
                currentPage = (Integer)savedInstanceState.getSerializable("currentPage");
            adapter = (RecycleViewAdapter<T, RecyclerView.ViewHolder>) getArguments().get(ADAPTER);
            fullListObjects = adapter.getItems();
            listObjects = adapter.getItems();
            objectPerPages = (Integer) getArguments().get(OBJ_PER_PAGE);

            recalculateNumberOfPages();

//            if (listObjects != null && listObjects.size() > 0) {
//                numberOfPages = (listObjects.size() / objectPerPages);
//                if ((listObjects.size() % objectPerPages) > 0)
//                    numberOfPages += 1;
//            }
            Log.d(Util.APP_TAG, "Num of pages: " + numberOfPages);

        }
        catch(Exception e) {
            Log.d(Util.APP_TAG, "Error: " + e.getMessage());
            e.printStackTrace();
            Toast.makeText(getActivity(), R.string.error_product_list, Toast.LENGTH_SHORT).show();
        }
    }

//    public View onCreateView(LayoutInflater _inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState)
//    {
//        try {
//            Log.d(Util.APP_TAG, "ON CREATE VIEW CALLED IN PF");
//            LinearLayout root = (LinearLayout) _inflater.inflate(R.layout.pager, container, false);
//
//            final EditText inputProduct = (EditText) root.findViewById(R.id.input_product);
//            final Spinner spinnerCategory = (Spinner) root.findViewById(R.id.spinner_category);
//            Button btnSearch = (Button) root.findViewById(R.id.button_search);
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
////            adapter.setItems();
//            listViewObjects.setAdapter(adapter);
//
//
////        LayoutParams params = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT) ;
////        this.setLayoutParams(params);
////        this.addView(root);
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
////            Log.d(Util.APP_TAG, "NUP: " + numberOfPages);
//            if (numberOfPages > 0) {
////                adapter
//                moveToPage(currentPage);
//            }
//
//            ProductService.fillCategorySpinner(spinnerCategory, getActivity(), getFragmentManager());
//
//            btnSearch.setOnClickListener(
//                    new View.OnClickListener()
//                    {
//                        @Override
//                        public void onClick(View v)
//                        {
//                            if((inputProduct.getText() != null && inputProduct.getText().length() > 0) || spinnerCategory.getSelectedItemPosition() > 0) {
//                                String name = inputProduct.getText().toString();
//                                ProdCategory ctgr = (ProdCategory)spinnerCategory.getSelectedItem();
//
//                            }
//                        }
//                    }
//            );
//
//
//            return root;
//        }
//        catch(Exception e) {
//            Log.d(Util.APP_TAG, "Error: " + e.getMessage());
//            e.printStackTrace();
//            Toast.makeText(getActivity(), R.string.error_product_list, Toast.LENGTH_SHORT).show();
//            return null;
//        }
//    }

    @Override
    public void onSaveInstanceState(Bundle outState)
    {
        super.onSaveInstanceState(outState);
        outState.putSerializable("currentPage", currentPage);
    }

    public int getObjectPerPages()
    {
        return objectPerPages;
    }

    public void setObjectPerPages(int _objectPerPages)
    {
        objectPerPages = _objectPerPages;
    }

    protected void moveToPage(int _page) {
        if(currentPage == _page)
            return;

        int pageLeft = numberOfPages - _page;
        int location = numberOfPages < 5 ? numberOfPages - pageLeft : 5 - pageLeft;
        if(location < 1)
            location = 1;

        location = location - 1;
        Button currentButton = listButton.get(location);
        currentButton.setText(String.valueOf(_page));
        Button btnDef = new Button(getActivity());
        for(int l = location - 1, p = _page - 1 ; l >= 0 ; l--, p--) {
            Button button = listButton.get(l);
            button.setText(String.valueOf(p));
            button.setTextColor(btnDef.getTextColors());
            button.setBackgroundResource(android.R.drawable.btn_default);
        }
        for(int l = location + 1, p = _page + 1 ; l < 5 && p <= numberOfPages ; l++, p++) {
            Button button = listButton.get(l);
            button.setText(String.valueOf(p));
            button.setTextColor(btnDef.getTextColors());
            button.setBackgroundResource(android.R.drawable.btn_default);
        }

        currentButton.setTextColor(Color.parseColor("#f6f7f7"));
        currentButton.setBackgroundColor(Color.parseColor("#0f7858"));

        int toIndex = (_page * objectPerPages) >= listObjects.size()  ? listObjects.size() : _page * objectPerPages;
        List<T> items = listObjects.subList((_page - 1) * objectPerPages, toIndex);
        Log.d(Util.APP_TAG, "CALL SET ITEMS");
        adapter.setItems(items);
        currentPage = _page;
//        adapter.notifyDataSetChanged();
    }

    protected void recalculateNumberOfPages() {
        numberOfPages = 0;
        if (listObjects != null && listObjects.size() > 0) {
            numberOfPages = (listObjects.size() / objectPerPages);
            if ((listObjects.size() % objectPerPages) > 0)
                numberOfPages += 1;
        }
    }

    protected class ButtonListener implements View.OnClickListener
    {

        @Override
        public void onClick(View v)
        {
            Button b = (Button)v;
            int page = Integer.parseInt((String) b.getText());
            moveToPage(page);
        }
    }

//    private class StockCardHolder extends RecyclerView.ViewHolder {
//
//        public TextView textName;
//        public ImageView imageProduct;
//
//
//        public StockCardHolder(View itemView) {
//            super(itemView);
//            textName = (TextView) itemView.findViewById(R.id.text_name);
//            imageProduct = (ImageView) itemView.findViewById(R.id.image_product);
//        }
//
//        public void bindDrawable(Drawable drawable) {
//            imageProduct.setImageDrawable(drawable);
//        }
//
//    }
//
//    private class StockCardAdapter extends RecyclerView.Adapter<StockCardHolder> {
//        private List<T> products;
//        private static final int HEADER = -1;
//        private int editedPosition;
//
//        public StockCardAdapter(List<T> _products) {
//            this.products = _products;
//        }
//
//        public void addItem(Product _prod) {
//            if(this.products == null)
//                this.products = new LinkedList<>();
//
//            this.products.add(_prod);
//            Log.d(Util.APP_TAG, "ADD : " + _prod.getName());
//            notifyItemInserted(this.products.size());
//        }
//
//        public void notifyItemAdded() {
//            notifyItemInserted(products.size());
//        }
//        public void notifyItemUpdated() {
//            listViewObjects.getAdapter().notifyItemChanged(editedPosition);
//        }
//
//        @Override
//        public StockCardHolder onCreateViewHolder(ViewGroup parent, int viewType) {
//            try {
//                LayoutInflater layoutInflater = LayoutInflater.from(getContext());
//
//                View view = layoutInflater.inflate(R.layout.invoice_item, parent, false);
//                return new StockCardHolder(view);
//            }
//            catch(Exception e) {
//                Log.d(Util.APP_TAG, "Error: " + e.getMessage());
//                e.printStackTrace();
//                Toast.makeText(getContext(), R.string.internal_server_error, Toast.LENGTH_SHORT).show();
//                return null;
//            }
//        }
//
//        @Override
//        public void onBindViewHolder(StockCardHolder holder, final int position) {
//            try {
//                final T prod = this.products.get(position);
//                holder.textName.setText(translator.getName(prod));
//
//                holder.imageProduct.setOnClickListener(
//                        new View.OnClickListener()
//                        {
//                            @Override
//                            public void onClick(View v)
//                            {
//                                InvoiceLineDialogFragment dialog = InvoiceLineDialogFragment.newInstance(argSI, prod);
//                                dialog.setTargetFragment(InvoiceFragment_old.this, Util.REQUEST_CODE_ADD);
//                                dialog.show(getFragmentManager(), "salesinvoiceline");
//                            }
//                        }
//                );
//
//                if (prod.getImg() == null)
//                    downloader.queueImage(holder, null);
//                else {
//                    Map<String, String> params = new HashMap<String, String>();
//                    params.put("sessionString", LoginService.sessionString);
//                    params.put("image", prod.getImg());
//                    downloader.queueImage(holder, Util.getImageURL(Util.PRODUCT_URL, params));
//                    Log.d(Util.APP_TAG, "Prod: " + prod.getName() + " pos: " + position + " url: " + Util.getImageURL(Util.PRODUCT_URL, params));
//                }
//            }
//            catch(Exception e) {
//                Log.d(Util.APP_TAG, "Error: " + e.getMessage());
//                e.printStackTrace();
//                Toast.makeText(getActivity(), R.string.internal_server_error, Toast.LENGTH_SHORT).show();
//            }
//        }
//
//        @Override
//        public int getItemCount() {
//
//            return this.products != null ? this.products.size() : 0;
//        }
//
//        @Override
//        public int getItemViewType(int position) {
//            if (position == 0) {
//                return HEADER;
//            }
//
//            return super.getItemViewType(position);
//        }
//    }
//
//    public static interface NamedObjectTranslator<T> {
//        public String getName(T object);
//    }

}
