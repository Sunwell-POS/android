package com.sunwell.pos.mobile.dialog;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TableLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.sunwell.pos_mobile.R;
import com.sunwell.pos.mobile.fragment.PagerFragment;
import com.sunwell.pos.mobile.model.ProdCategory;
import com.sunwell.pos.mobile.model.Product;
import com.sunwell.pos.mobile.service.LoginService;
import com.sunwell.pos.mobile.service.ProductService;
import com.sunwell.pos.mobile.util.ImageDownloader;
import com.sunwell.pos.mobile.util.RecycleViewAdapter;
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

public class ChooseProductDialogFragment2 extends DialogFragment {

    private static final String PROGRESS_FETCH_PRODUCT = "progressFetchProduct";

    private Product product ;
    private ProdCategory category ;
    private List<ProdCategory> categories = new LinkedList<>();
    private int numberOfPages = 0;
    private int objectPerPages = 1;
    private int currentPage = -1;
    private List<Product> fullListObjects;
    private List<Product> listObjects;
    private ImageDownloader<ProductHolder> downloader;
    private RecycleViewAdapter<Product, ProductHolder> adapter;
    private RecyclerView listViewObjects;
    private Button btn1;
    private Button btn2;
    private Button btn3;
    private Button btn4;
    private Button btn5;
    private ImageButton btnNext;
    private ImageButton btnBack;
    private List<Button> listButton ;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        if(savedInstanceState != null)
            currentPage = (Integer)savedInstanceState.getSerializable("currentPage");
        listButton = new LinkedList<>();
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        try {
            final Dialog dialog = new Dialog(getActivity());
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            dialog.setContentView(R.layout.pager);

            Handler responseHandler = new Handler();
            Drawable defDrawable = getResources().getDrawable(R.mipmap.ic_launcher);
            int imagePxSize = 0;
            if(Util.isSmallScreen(getActivity()))
                imagePxSize = Util.dpToPx(R.dimen.small_image, getActivity());
            else
                imagePxSize = Util.dpToPx(R.dimen.big_image, getActivity());
            downloader = new ImageDownloader<>(responseHandler, defDrawable, imagePxSize, imagePxSize);
            downloader.setThumbnailDownloadListener(
                    new ImageDownloader.DownloadListener<ProductHolder>() {

                        @Override
                        public void onImageDownloaded(ProductHolder _holder, Bitmap _bitmap) throws Exception {
                            if(!isAdded() || getActivity() == null)
                                return;
                            Drawable drawable = new BitmapDrawable(getResources(), _bitmap);
                            _holder.bindDrawable(drawable);
                        }

                        @Override
                        public void onImageDownloaded(ProductHolder _holder, Drawable _drawable) throws Exception {
                            Log.d(Util.APP_TAG, " ON IMAGEDOWNLOADED2 CALLED: " + this);
                            if(!isAdded() || getActivity() == null)
                                return;
                            _holder.bindDrawable(_drawable);
                        }
                    }
            );
            downloader.start();
            downloader.getLooper();

            final LinearLayout panelItem = (LinearLayout) dialog.findViewById(R.id.panel_filter);
            final AutoCompleteTextView inputProduct = (AutoCompleteTextView) dialog.findViewById(R.id.atc_product);
//            final AutoCompleteTextView inputCategory = (AutoCompleteTextView) dialog.findViewById(R.id.atc_category);
            final AutoCompleteTextView inputCategory = null;
            final ImageButton btnSearch = (ImageButton) dialog.findViewById(R.id.button_search);
            final ImageButton btnClear = (ImageButton) dialog.findViewById(R.id.button_clear);


            btn1 = (Button) dialog.findViewById(R.id.button_1);
            btn2 = (Button) dialog.findViewById(R.id.button_2);
            btn3 = (Button) dialog.findViewById(R.id.button_3);
            btn4 = (Button) dialog.findViewById(R.id.button_4);
            btn5 = (Button) dialog.findViewById(R.id.button_5);
            listButton.add(btn1);
            listButton.add(btn2);
            listButton.add(btn3);
            listButton.add(btn4);
            listButton.add(btn5);
            listViewObjects = (RecyclerView) dialog.findViewById(R.id.list_object);
            btnNext = (ImageButton) dialog.findViewById(R.id.button_next);
            btnBack = (ImageButton) dialog.findViewById(R.id.button_back);

            DisplayMetrics displayMetrics = this.getResources().getDisplayMetrics();
            if(!Util.isSmallScreen(getActivity()))
                listViewObjects.setLayoutManager(new GridLayoutManager(getActivity(), 4));
            else
                listViewObjects.setLayoutManager(new LinearLayoutManager(getActivity()));

            ButtonListener btnListener = new ButtonListener();

            for(int i = 0 ; i < 5 ; i++) {
                Button button = listButton.get(i);
                button.setOnClickListener(btnListener);
            }

            btnClear.setOnClickListener(
                    new View.OnClickListener()
                    {
                        @Override
                        public void onClick(View v)
                        {
                            product = null;
                            inputProduct.setText("");
                            btnSearch.performClick();
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
                            btnSearch.performClick();
                        }
                    }
            );

            inputCategory.setOnItemClickListener(
                    new AdapterView.OnItemClickListener()
                    {

                        @Override
                        public void onItemClick(AdapterView<?> parent, View view, int position, long id)
                        {
                            final ProdCategory ctgr = (ProdCategory)parent.getItemAtPosition(position);
                            Log.d(Util.APP_TAG, "CTGR IS: " + ctgr);
                            for(ProdCategory c : categories) {
                                if(c.equals(ctgr))
                                    return;
                            }

                            final View ctgrItem = LayoutInflater.from(getActivity()).inflate(R.layout.category_item, panelItem, false);
                            TextView txtCtgr = (TextView)ctgrItem.findViewById(R.id.text_category);
                            txtCtgr.setText(ctgr.getName());
                            txtCtgr.setOnClickListener(
                                    new View.OnClickListener()
                                    {
                                        @Override
                                        public void onClick(View v)
                                        {
                                            panelItem.removeView(ctgrItem);
                                            categories.remove(ctgr);
                                            btnSearch.performClick();
                                        }
                                    }
                            );
                            panelItem.addView(ctgrItem, panelItem.getChildCount() - 2);
                            categories.add(ctgr);
                            btnSearch.performClick();
                            inputCategory.setText("");
                        }
                    }
            );

            btnSearch.setOnClickListener(
                    new View.OnClickListener()
                    {
                        @Override
                        public void onClick(View v)
                        {
                            String name = inputProduct.getText() != null ? inputProduct.getText().toString() : null;
                            Log.d(Util.APP_TAG, "text name: " + name);
                            if(fullListObjects.size() > 0) {
                                listObjects = new LinkedList<>();
                                for (Product p : fullListObjects) {
                                    if(name != null && name.length() > 0)
                                        if(!p.getName().toLowerCase().contains(name.toLowerCase()))
                                            continue;

                                    if(categories.size() > 0) {
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

                                        if(filterCtgr.size() > 0)
                                            continue;;
                                    }
                                    listObjects.add(p);
                                }
                                refreshPaging();
                                adapter.setItems(listObjects);
                                if(numberOfPages > 0)
                                    moveToPage(1);
                            }
                        }
                    }
            );

            ProductService.fillProductAutoComplete(inputProduct, getActivity(), getFragmentManager());
            ProductService.fillCategoryAutoComplete(inputCategory, getActivity(), getFragmentManager());

            if(ProductService.products == null) {
                Log.d(Util.APP_TAG, "FRAGMENT: " + getFragmentManager().findFragmentById(R.id.layout_pager));
                ResultWatcher<List<Product>> listener = new ResultWatcher<List<Product>>() {
                    @Override
                    public void onResult(Object source, List<Product> _products) throws Exception {
                        Util.stopDialog(PROGRESS_FETCH_PRODUCT);
                        adapter = new ProductAdapter(_products);
                        fullListObjects = adapter.getItems();
                        listObjects = adapter.getItems();


                        if(Util.isSmallScreen(getActivity()))
                            objectPerPages = 8;
                        else
                            objectPerPages = 16;

//                        if (listObjects != null && listObjects.size() > 0) {
//                            numberOfPages = (listObjects.size() / objectPerPages);
//                            if ((listObjects.size() % objectPerPages) > 0)
//                                numberOfPages += 1;
//                        }
//
//                        for(int l = numberOfPages ; l < 5 ; l++) {
//                            Button button = listButton.get(l);
//                            button.setVisibility(View.INVISIBLE);
//                        }

                        refreshPaging();

                        listViewObjects.setAdapter(adapter);

                        if (numberOfPages > 0) {
                            moveToPage(1);
                        }
                    }

                    @Override
                    public void onError(Object source, int errCode) throws Exception {
                        Util.stopDialog(PROGRESS_FETCH_PRODUCT);
                        Toast.makeText(getActivity(), R.string.internal_server_error, Toast.LENGTH_SHORT).show();
                    }
                };

                ProductService.fetchProducts(listener);
                Util.showDialog(getFragmentManager(), PROGRESS_FETCH_PRODUCT);
            }
            else {
                Log.d(Util.APP_TAG, "FRAGMENT: " + getFragmentManager().findFragmentById(R.id.layout_pager));
                adapter = new ProductAdapter(ProductService.products);
                fullListObjects = adapter.getItems();
                listObjects = adapter.getItems();

                if(Util.isSmallScreen(getActivity()))
                    objectPerPages = 8;
                else
                    objectPerPages = 16;

//                if (listObjects != null && listObjects.size() > 0) {
//                    numberOfPages = (listObjects.size() / objectPerPages);
//                    if ((listObjects.size() % objectPerPages) > 0)
//                        numberOfPages += 1;
//                }
//
//                for(int l = numberOfPages ; l < 5 ; l++) {
//                    Button button = listButton.get(l);
//                    button.setVisibility(View.INVISIBLE);
//                }

                refreshPaging();

                listViewObjects.setAdapter(adapter);

                if (numberOfPages > 0) {
                    moveToPage(1);
                }
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

    @Override
    public void onSaveInstanceState(Bundle outState)
    {
        super.onSaveInstanceState(outState);
        outState.putSerializable(Util.APP_TAG, "currentPage");
    }

    public int getObjectPerPages()
    {
        return objectPerPages;
    }

    public void setObjectPerPages(int _objectPerPages)
    {
        objectPerPages = _objectPerPages;
    }

    private void recalculateNumberOfPages() {
        numberOfPages = 0;
        if (listObjects != null && listObjects.size() > 0) {
            numberOfPages = (listObjects.size() / objectPerPages);
            if ((listObjects.size() % objectPerPages) > 0)
                numberOfPages += 1;
        }
    }

    private void refreshPaging() {
        recalculateNumberOfPages();

        for (Button b:listButton) {
            b.setVisibility(View.VISIBLE);
        }

        for(int l = numberOfPages ; l < 5 ; l++) {
            Button button = listButton.get(l);
            button.setVisibility(View.INVISIBLE);
        }
    }

    private void moveToPage(int _page) {
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
        List<Product> items = listObjects.subList((_page - 1) * objectPerPages, toIndex);
        Log.d(Util.APP_TAG, "CALL SET ITEMS");
        adapter.setItems(items);
        currentPage = _page;
//        adapter.notifyDataSetChanged();
    }

    private class ButtonListener implements View.OnClickListener
    {

        @Override
        public void onClick(View v)
        {
            Button b = (Button)v;
            int page = Integer.parseInt((String) b.getText());
            moveToPage(page);
        }
    }

    private class ProductHolder extends RecyclerView.ViewHolder {

        public TextView textName;
        public ImageView imageProduct;


        public ProductHolder(View itemView) {
            super(itemView);
            textName = (TextView) itemView.findViewById(R.id.text_name);
            imageProduct = (ImageView) itemView.findViewById(R.id.image_product);
        }

        public void bindDrawable(Drawable drawable) {
            imageProduct.setImageDrawable(drawable);
        }

    }

    private class ProductAdapter extends RecycleViewAdapter<Product, ProductHolder>
    {
        public ProductAdapter(List<Product> _products) {
            super(new LinkedList<>(_products), RecycleViewAdapter.PLAIN);
            this.products = _products;
        }

        @Override
        public ProductHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            try {
                LayoutInflater layoutInflater = LayoutInflater.from(getActivity());
                View view = layoutInflater.inflate(R.layout.invoice_item, parent, false);
                return new ProductHolder(view);
            }
            catch(Exception e) {
                Log.d(Util.APP_TAG, "Error: " + e.getMessage());
                e.printStackTrace();
                Toast.makeText(getActivity(), R.string.internal_server_error, Toast.LENGTH_SHORT).show();
                return null;
            }
        }

        @Override
        public void onBindViewHolder(ProductHolder holder, final int position) {
            try {
                final Product prod = this.products.get(position);
                holder.textName.setText(prod.getName());

                holder.imageProduct.setOnClickListener(
                        new View.OnClickListener()
                        {
                            @Override
                            public void onClick(View v)
                            {
                                if(getTargetFragment() != null) {
                                    Intent intent = new Intent();
                                    Fragment targetFragment = getTargetFragment();
                                    intent.putExtra("product", prod);
                                    targetFragment.onActivityResult(getTargetRequestCode(), Util.RESULT_CODE_SUCCESS, intent );
                                    dismiss();
                                }
                            }
                        }
                );

                if (prod.getImg() == null)
                    downloader.queueImage(holder, null);
                else {
                    Map<String, String> params = new HashMap<String, String>();
                    params.put("sessionString", LoginService.sessionString);
                    params.put("image", prod.getImg());
                    downloader.queueImage(holder, Util.getImageURL(Util.PRODUCT_URL, params));
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
