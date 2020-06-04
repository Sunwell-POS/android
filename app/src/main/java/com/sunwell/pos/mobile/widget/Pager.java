package com.sunwell.pos.mobile.widget;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.sunwell.pos_mobile.R;
import com.sunwell.pos.mobile.model.Product;
import com.sunwell.pos.mobile.model.SalesInvoiceLine;
import com.sunwell.pos.mobile.util.RecycleViewAdapter;
import com.sunwell.pos.mobile.util.Util;

import java.util.LinkedList;
import java.util.List;

/**
 * Created by sunwell on 11/13/17.
 */

public class Pager<T> extends FrameLayout
{
    private int numberOfPages = 0;
    private int objectPerPages = 1;
    private int currentPage = 1;
//    private NamedObjectTranslator<T> translator;
    private List<T> listObjects;
    RecycleViewAdapter<T, ? extends RecyclerView.ViewHolder> adapter;
    private RecyclerView listViewObjects;
    private Button btn1;
    private Button btn2;
    private Button btn3;
    private Button btn4;
    private Button btn5;
    private ImageButton btnNext;
    private ImageButton btnBack;
    private List<Button> listButton;

    public Pager(@NonNull Context context, List<T> _list, RecycleViewAdapter<T, ? extends RecyclerView.ViewHolder> _adapter, int _objPerPage)
    {
        super(context);
        if(_list.size() > 0) {
            numberOfPages = (_list.size() / _objPerPage);
            if((_list.size() % _objPerPage) > 0 )
                numberOfPages += 1;
        }
        LayoutInflater inflater = LayoutInflater.from(context);
        adapter = _adapter;
        listObjects = _list;
        LinearLayout root = (LinearLayout)inflater.inflate(R.layout.pager, this, false);
        btn1 = (Button)root.findViewById(R.id.button_1);
        btn2 = (Button)root.findViewById(R.id.button_2);
        btn3 = (Button)root.findViewById(R.id.button_3);
        btn4 = (Button)root.findViewById(R.id.button_4);
        btn5 = (Button)root.findViewById(R.id.button_5);
        listButton.add(btn1);
        listButton.add(btn2);
        listButton.add(btn3);
        listButton.add(btn4);
        listButton.add(btn5);
        listViewObjects = (RecyclerView) root.findViewById(R.id.list_object);
        btnNext = (ImageButton)root.findViewById(R.id.button_next);
        btnBack = (ImageButton)root.findViewById(R.id.button_back);

//        listViewObjects.setAdapter(adapter);
//        listViewObjects.setLayoutManager(new GridLayoutManager(getActivity(), 4));

        FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT) ;
        this.setLayoutParams(params);
        this.addView(root);

        for(int l = numberOfPages ; l < 5 ; l++) {
            Button button = listButton.get(l);
            button.setVisibility(View.INVISIBLE);
        }

        if(numberOfPages > 0)
            moveToPage(1);
    }


    public int getObjectPerPages()
    {
        return objectPerPages;
    }

    public void setObjectPerPages(int _objectPerPages)
    {
        objectPerPages = _objectPerPages;
    }

    private void moveToPage(int _page) {
        if(currentPage == _page)
            return;

        int pageLeft = numberOfPages - _page;
        int location = numberOfPages < 5 ? numberOfPages - pageLeft : 5 - pageLeft;
//        if(location > numberOfPages)
//            location = numberOfPages;

        if(location < 1)
            location = 1;
        location = location - 1;
        Button currentButton = listButton.get(location);
        currentButton.setText(_page);
        for(int l = location - 1, p = _page - 1 ; l >= 0 ; l--, p--) {
            Button button = listButton.get(l);
            button.setText(String.valueOf(p));
            button.setTextColor(currentButton.getTextColors());
            button.setBackgroundResource(android.R.drawable.btn_default);
        }
        for(int l = location + 1, p = _page + 1 ; l < 5 && p <= numberOfPages ; l++, p++) {
            Button button = listButton.get(l);
            button.setText(String.valueOf(p));
            button.setTextColor(currentButton.getTextColors());
            button.setBackgroundResource(android.R.drawable.btn_default);
        }

//        for(int l = numberOfPages ; l < 5 ; l++) {
//            Button button = listButton.get(l);
//            button.setVisibility(View.INVISIBLE);
//        }

        currentButton.setTextColor(Color.parseColor("#f6f7f7"));
        currentButton.setBackgroundColor(Color.parseColor("#0f7858"));

        List<T> items = listObjects.subList((_page - 1) * objectPerPages, _page * objectPerPages);
        adapter.setItems(items);
        adapter.notifyDataSetChanged();
    }

    private class buttonListener implements OnClickListener {

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
