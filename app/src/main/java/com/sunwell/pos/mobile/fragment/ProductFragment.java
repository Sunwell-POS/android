package com.sunwell.pos.mobile.fragment;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.sunwell.pos_mobile.R;
import com.sunwell.pos.mobile.dialog.ProductDialogFragment;
import com.sunwell.pos.mobile.model.PaymentMethodObj;
import com.sunwell.pos.mobile.model.ProdCategory;
import com.sunwell.pos.mobile.model.Product;
import com.sunwell.pos.mobile.service.LoginService;
import com.sunwell.pos.mobile.service.ProductService;
import com.sunwell.pos.mobile.util.ImageDownloader;
import com.sunwell.pos.mobile.util.RecycleViewAdapter;
import com.sunwell.pos.mobile.util.ResultWatcher;
import com.sunwell.pos.mobile.util.Util;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * Created by sunwell on 10/18/17.
 */

public class ProductFragment extends Fragment {

    private static final String PROGRESS_FETCH_PRODUCT = "progressFetchProduct";
    private static final String PROGRESS_DELETE_PRODUCT = "progressDeleteProduct";
//    private static int idLayoutProductRow;
//    private static int idLayoutProductHeader;

    private ImageDownloader<ProductHolder> downloader;
    private RecyclerView listProduct;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        try {
            super.onCreate(savedInstanceState);
//            Util.isSmallScreen(getActivity());
            Handler responseHandler = new Handler();
            Drawable defDrawable = getResources().getDrawable(R.mipmap.ic_launcher);
            int imagePxSize = Util.dpToPx(R.dimen.big_image, getActivity());
            downloader = new ImageDownloader<>(responseHandler, defDrawable, imagePxSize, imagePxSize);
            downloader.setThumbnailDownloadListener(
                    new ImageDownloader.DownloadListener<ProductHolder>() {

                        @Override
                        public void onImageDownloaded(ProductHolder _holder, Bitmap _bitmap) throws Exception {
                            Drawable drawable = new BitmapDrawable(getResources(), _bitmap);
                            _holder.bindDrawable(drawable);
                        }

                        @Override
                        public void onImageDownloaded(ProductHolder _tenantHolder, Drawable _drawable) throws Exception {
                            _tenantHolder.bindDrawable(_drawable);
                        }
                    }
            );
            downloader.start();
            downloader.getLooper();
        }
        catch(Exception e) {
            Log.d(Util.APP_TAG, "Error: " + e.getMessage());
            e.printStackTrace();
            Toast.makeText(getActivity(), R.string.internal_server_error, Toast.LENGTH_SHORT).show();
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        try {
            super.onCreateView(inflater, container, savedInstanceState);
            View v = inflater.inflate(R.layout.layout_product, container, false);
            Button btnAddProduct = (Button) v.findViewById(R.id.button_add_product);
            ProductAdapter listAdapter;
            int width = Util.pxToDp(container.getWidth(), getContext());
            Log.d(Util.APP_TAG, "W: " + width);
//            if (width < 900) {
//                idLayoutProductHeader = R.layout.product_header_short;
//                idLayoutProductRow = R.layout.product_row_short;
//            } else {
//                idLayoutProductHeader = R.layout.product_header;
//                idLayoutProductRow = R.layout.product_row;
//            }

            btnAddProduct.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        try {
                            FragmentManager fragmentManager = getFragmentManager();
                            ProductDialogFragment dialog = new ProductDialogFragment();
                            dialog.setTargetFragment(ProductFragment.this, Util.REQUEST_CODE_ADD);
                            dialog.show(fragmentManager, "Product");
                        }
                        catch(Exception e) {
                            Log.d(Util.APP_TAG, "Error: " + e.getMessage());
                            e.printStackTrace();
                            Toast.makeText(getActivity(), R.string.internal_server_error, Toast.LENGTH_SHORT).show();
                        }
                    }
                }
            );
            listProduct = (RecyclerView) v.findViewById(R.id.list_product);
            listProduct.setLayoutManager(new LinearLayoutManager(getActivity()));
            ResultWatcher<List<Product>> listener = new ResultWatcher<List<Product>>() {
                @Override
                public void onResult(Object source, List<Product> _products) throws Exception {
                    Util.stopDialog(PROGRESS_FETCH_PRODUCT);
                    listProduct.setAdapter(new ProductAdapter(_products));
                }

                @Override
                public void onError(Object source, int errCode) throws Exception {
                    Util.stopDialog(PROGRESS_FETCH_PRODUCT);
                    Toast.makeText(getActivity(), R.string.internal_server_error, Toast.LENGTH_SHORT).show();
                }
            };

            ProductService.fetchProducts(listener);
            Util.showDialog(getFragmentManager(), PROGRESS_FETCH_PRODUCT);
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
        try {
            if (_requestCode == Util.REQUEST_CODE_ADD) {
                if (_resultCode == Util.RESULT_CODE_SUCCESS) {
                    Product prod = (Product)data.getSerializableExtra("product");
                    ((ProductAdapter) listProduct.getAdapter()).addItem(prod);
                    Toast.makeText(getActivity(), R.string.success_add_product, Toast.LENGTH_SHORT).show();
                } else {
                    Log.d(Util.APP_TAG, " ERR CODE: " + data.getIntExtra("errorCode", -3));
                    Toast.makeText(getActivity(), R.string.fail_add_product, Toast.LENGTH_SHORT).show();
                }
            } else if (_requestCode == Util.REQUEST_CODE_EDIT) {
                if (_resultCode == Util.RESULT_CODE_SUCCESS) {
                    Product prod = (Product)data.getSerializableExtra("product");
                    ((ProductAdapter) listProduct.getAdapter()).updateItem(prod);
                    Toast.makeText(getActivity(), R.string.success_edit_product, Toast.LENGTH_SHORT).show();
//                    ((StockCardAdapter) listProduct.getAdapter()).notifyItemUpdated();
                } else {
                    Toast.makeText(getActivity(), R.string.fail_edit_product, Toast.LENGTH_SHORT).show();
                }
            }
        }
        catch(Exception e) {
            Log.d(Util.APP_TAG, "Error: " + e.getMessage());
            e.printStackTrace();
            Toast.makeText(getActivity(), R.string.internal_server_error, Toast.LENGTH_SHORT).show();
        }
    }

    private class ProductHolder extends RecyclerView.ViewHolder {

        public TextView textNo;
        public TextView textName;
        public TextView textMetric;
        public TextView textPrice;
        public TextView textCategory;
        public ImageView imageBarcode;
        public ImageView imageStock;
        public TextView textDisc;
        public TextView textActive;
        public ImageView imageProduct;
        public ImageButton btnEdit;
        public ImageButton btnDelete;
        public ImageButton btnSearch;

        public EditText inputName;
        public Spinner spinnerMetric;
        public EditText inputPrice;
        public Spinner spinnerCategory;
        public Spinner spinnerBarcode;
        public Spinner spinnerStock;
        public CheckBox cbDisc;
        public CheckBox cbActive;;


        public ProductHolder(View itemView) {
            super(itemView);
            textNo = (TextView) itemView.findViewById(R.id.text_no);
            textName = (TextView) itemView.findViewById(R.id.text_name);
            textMetric = (TextView) itemView.findViewById(R.id.text_metric);
            textPrice = (TextView) itemView.findViewById(R.id.text_price);
            textCategory = (TextView) itemView.findViewById(R.id.text_category);
            imageBarcode = (ImageView) itemView.findViewById(R.id.image_barcode);
            imageStock = (ImageView) itemView.findViewById(R.id.image_stock);
            textDisc = (TextView) itemView.findViewById(R.id.text_discount);
            textActive = (TextView) itemView.findViewById(R.id.text_active);
            imageProduct = (ImageView) itemView.findViewById(R.id.image_product);
            btnEdit = (ImageButton) itemView.findViewById(R.id.button_edit);
            btnDelete = (ImageButton) itemView.findViewById(R.id.button_delete);
            btnSearch = (ImageButton) itemView.findViewById(R.id.button_search);

            inputName = (EditText) itemView.findViewById(R.id.input_name);
            spinnerMetric = (Spinner) itemView.findViewById(R.id.spinner_metric);
            inputPrice = (EditText) itemView.findViewById(R.id.input_price);
            spinnerCategory = (Spinner) itemView.findViewById(R.id.spinner_category);
            spinnerBarcode = (Spinner) itemView.findViewById(R.id.spinner_use_barcode);
            spinnerStock = (Spinner) itemView.findViewById(R.id.spinner_use_stock);
//            cbDisc = (CheckBox) itemView.findViewById(R.id.cb_use_discount);
//            cbActive = (CheckBox) itemView.findViewById(R.id.cb_active);
        }

        public void bindDrawable(Drawable drawable) {
            imageProduct.setImageDrawable(drawable);
        }
    }

    private class ProductAdapter extends RecycleViewAdapter<Product, ProductHolder>
    {
        private List<Product> fullProducts;

        public ProductAdapter(List<Product> _products) {
            super(new LinkedList<>(_products), RecycleViewAdapter.USE_FILTER);
            fullProducts = _products;
        }

//        public void addItem(Product _prod) {
//            if(this.products == null)
//                this.products = new LinkedList<>();
//
//            this.products.add(_prod);
//            Log.d(Util.APP_TAG, "ADD : " + _prod.getName());
//            notifyItemInserted(this.products.size());
//        }


        @Override
        public ProductHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            try {
                LayoutInflater layoutInflater = LayoutInflater.from(getActivity());

                View view;
                if (viewType == HEADER) {
                    view = layoutInflater.inflate(R.layout.product_header_short, parent, false);
                }
                else if(viewType == FILTER) {
                    view = layoutInflater.inflate(R.layout.product_filter, parent, false);
                }
                else {
                    view = layoutInflater.inflate(R.layout.product_row_short, parent, false);
                }
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
        public void onBindViewHolder(final ProductHolder holder, final int position) {
            try {
                if (position == 0 || products == null)
                    return;

                if(position > 1) {

                    final Product prod = this.products.get(position - 2);
                    holder.textNo.setText("" + (position - 1));
                    holder.textName.setText(prod.getName());
                    holder.textMetric.setText(prod.getMetric());
                    holder.textPrice.setText(prod.getPrice() + "");
                    if (prod.getCategories() != null && prod.getCategories().size() > 0) {
                        Log.d(Util.APP_TAG, " NAME: " + prod.getName() + " SIZE: " + prod.getCategories().size());
                        holder.textCategory.setText(prod.getCategories().get(0).getName());
                    }
                    Drawable drawable;

                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                        if(prod.getBarCode() != null && prod.getBarCode().length() > 0)
                            drawable = getResources().getDrawable(R.drawable.right, getActivity().getTheme());
                        else
                            drawable = getResources().getDrawable(R.drawable.wrong, getActivity().getTheme());

                        holder.imageBarcode.setImageDrawable(drawable);

                        if(prod.getHasStock())
                            drawable = getResources().getDrawable(R.drawable.right, getActivity().getTheme());
                        else
                            drawable = getResources().getDrawable(R.drawable.wrong, getActivity().getTheme());

                        holder.imageStock.setImageDrawable(drawable);
                    }
                    else {
                        if(prod.getBarCode() != null && prod.getBarCode().length() > 0)
                            drawable = getResources().getDrawable(R.drawable.right);
                        else
                            drawable = getResources().getDrawable(R.drawable.right);

                        holder.imageBarcode.setImageDrawable(drawable);

                        if(prod.getHasStock())
                            drawable = getResources().getDrawable(R.drawable.right);
                        else
                            drawable = getResources().getDrawable(R.drawable.right);

                        holder.imageStock.setImageDrawable(drawable);
                    }

                    holder.btnEdit.setOnClickListener(
                            new View.OnClickListener()
                            {
                                @Override
                                public void onClick(View v)
                                {
                                    try {
                                        ProductDialogFragment dialog = ProductDialogFragment.newInstance(prod);
                                        dialog.setTargetFragment(ProductFragment.this, Util.REQUEST_CODE_EDIT);
                                        editedPosition = position;
                                        dialog.show(getFragmentManager(), "Product");
                                    }
                                    catch (Exception e) {
                                        Log.d(Util.APP_TAG, "Error: " + e.getMessage());
                                        e.printStackTrace();
                                        Toast.makeText(getActivity(), R.string.internal_server_error, Toast.LENGTH_SHORT).show();
                                    }
                                }
                            }
                    );
//
                    holder.btnDelete.setOnClickListener(
                            new View.OnClickListener()
                            {
                                @Override
                                public void onClick(View v)
                                {
                                    try {
                                        String systemId = prod.getSystemId();
                                        ProductService.deleteProduct(
                                                new ResultWatcher<Boolean>()
                                                {
                                                    @Override
                                                    public void onResult(Object source, Boolean result) throws Exception
                                                    {
                                                        Util.stopDialog(PROGRESS_DELETE_PRODUCT);
                                                        ((ProductAdapter)listProduct.getAdapter()).removeAt(position);
                                                        Toast.makeText(getActivity(), R.string.success_delete_product, Toast.LENGTH_SHORT).show();
//                                                        listProduct.getAdapter().notifyDataSetChanged();
                                                    }

                                                    @Override
                                                    public void onError(Object source, int errCode) throws Exception
                                                    {
                                                        Util.stopDialog(PROGRESS_DELETE_PRODUCT);
                                                        Toast.makeText(getActivity(), R.string.fail_delete_product, Toast.LENGTH_SHORT).show();
                                                    }
                                                }
                                                , systemId
                                        );
                                        Util.showDialog(getFragmentManager(), PROGRESS_DELETE_PRODUCT);
                                    }
                                    catch (Exception e) {
                                        Log.d(Util.APP_TAG, "Error: " + e.getMessage());
                                        e.printStackTrace();
                                        Toast.makeText(getActivity(), R.string.internal_server_error, Toast.LENGTH_SHORT).show();
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
                        Log.d(Util.APP_TAG, "Prod: " + prod.getName() + " pos: " + position + " url: " + Util.getImageURL(Util.PRODUCT_URL, params));
                    }
                }
                else if(position == 1) {
                    ProductService.fillCategorySpinner(holder.spinnerCategory, getActivity(), getFragmentManager());
                    holder.btnSearch.setOnClickListener(
                            new View.OnClickListener()
                            {
                                @Override
                                public void onClick(View v)
                                {
                                    String text = holder.inputName.getText().toString();
                                    String metric = (String )holder.spinnerMetric.getSelectedItem();
                                    ProdCategory ctgr = holder.spinnerCategory.getSelectedItemPosition() > 0 ? (ProdCategory) holder.spinnerCategory.getSelectedItem() : null;
                                    Integer posBarcode = holder.spinnerBarcode.getSelectedItemPosition();
                                    Integer posStock = holder.spinnerStock.getSelectedItemPosition();
                                    List<Product> filteredProducts = new LinkedList<>();
                                    if(fullProducts != null) {
                                        for (Product p : fullProducts) {
                                            if(text != null && text.length() > 0)
                                                if(!p.getName().toLowerCase().contains(text.toLowerCase()))
                                                    continue;

                                            if(metric != null && metric.length() > 0)
                                                if(!p.getMetric().contains(metric))
                                                    continue;

                                            if(posBarcode != 0) {
                                                boolean hasBarcode = posBarcode == 1 ? true : false;
                                                if(p.getBarCode() != null) {
                                                    if(hasBarcode) {
                                                        if(p.getBarCode().length() <= 0)
                                                           continue;
                                                    }
                                                    else {
                                                        if(p.getBarCode().length() > 0)
                                                            continue;
                                                    }
                                                }
                                                else {
                                                    if(hasBarcode)
                                                        continue;
                                                }
                                            }

                                            if(posStock != 0) {
                                                boolean useStock = posStock == 1 ? true : false;
                                                if(p.getHasStock() != null) {
                                                    if(useStock) {
                                                        if(!p.getHasStock())
                                                            continue;
                                                    }
                                                    else {
                                                        if(p.getHasStock())
                                                            continue;
                                                    }
                                                }
                                                else {
                                                    if(useStock)
                                                        continue;
                                                }
                                            }

                                            if(ctgr != null) {
                                                Boolean found = false;
                                                if (p.getCategories() != null)
                                                    for (ProdCategory c : p.getCategories()) {
                                                        if (!c.equals(ctgr))
                                                            continue;
                                                        else {
                                                            found = true;
                                                            break;
                                                        }
                                                    }

                                                    if(!found)
                                                        continue;;
                                            }
                                            filteredProducts.add(p);
                                        }
                                        setItems(filteredProducts);
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

//        @Override
//        public int getItemCount() {
//
//            return this.products != null ? this.products.size() + 2 : 2;
//        }

//        @Override
//        public int getItemViewType(int position) {
//            if (position == 0) {
//                return HEADER;
//            }
//            else if (position == 1) {
//                return FILTER;
//            }
//
//            return super.getItemViewType(position);
//        }
    }

//    private class StockCardAdapter extends RecyclerView.Adapter<StockMutationHolder> {
//        private List<Product> products;
//        private static final int HEADER = -1;
//        private int editedPosition;
//
//        public StockCardAdapter(List<Product> _products) {
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
//            listProduct.getAdapter().notifyItemChanged(editedPosition);
//        }
//
//        @Override
//        public StockMutationHolder onCreateViewHolder(ViewGroup parent, int viewType) {
//            try {
//                LayoutInflater layoutInflater = LayoutInflater.from(getActivity());
//
//                View view;
//                if (viewType == HEADER) {
//                    view = layoutInflater.inflate(idLayoutProductHeader, parent, false);
//                } else {
//                    view = layoutInflater.inflate(idLayoutProductRow, parent, false);
//                }
//                return new StockMutationHolder(view);
//            }
//            catch(Exception e) {
//                Log.d(Util.APP_TAG, "Error: " + e.getMessage());
//                e.printStackTrace();
//                Toast.makeText(getActivity(), R.string.internal_server_error, Toast.LENGTH_SHORT).show();
//                return null;
//            }
//        }
//
//        @Override
//        public void onBindViewHolder(StockMutationHolder holder, final int position) {
//            try {
//                if (position == 0 || products == null)
//                    return;
//
//                final Product prod = this.products.get(position - 1);
//                holder.textNo.setText("" + position);
//                holder.textNumber.setText(prod.getName());
//                holder.textMetric.setText(prod.getMetric());
//                holder.textPrice.setText(prod.getPrice() + "");
//                if (prod.getCategories() != null) {
//                    holder.textCategory.setText(prod.getCategories().get(0).getName());
//                }
//                holder.textBarcode.setText(prod.getBarCode());
//                holder.textActive.setText(prod.getStatus() ? "Active" : "Not Active");
//
//                holder.btnEdit.setOnClickListener(
//                    new View.OnClickListener()
//                    {
//                        @Override
//                        public void onClick(View v)
//                        {
//                            try {
//                                ProductDialogFragment dialog = ProductDialogFragment.newInstance(prod);
//                                dialog.setTargetFragment(ProductFragment.this, Util.REQUEST_CODE_EDIT);
//                                editedPosition = position;
//                                dialog.show(getFragmentManager(), "Product");
//                            }
//                            catch(Exception e) {
//                                Log.d(Util.APP_TAG, "Error: " + e.getMessage());
//                                e.printStackTrace();
//                                Toast.makeText(getActivity(), R.string.internal_server_error, Toast.LENGTH_SHORT).show();
//                            }
//                        }
//                    }
//                );
////
//                holder.btnDelete.setOnClickListener(
//                    new View.OnClickListener()
//                    {
//                        @Override
//                        public void onClick(View v)
//                        {
//                            try {
//                                String systemId = products.get(position - 1).getSystemId();
//                                ProductService.deleteProduct(
//                                    new ResultWatcher<Boolean>()
//                                    {
//                                        @Override
//                                        public void onResult(Object source, Boolean result) throws Exception
//                                        {
//                                            Toast.makeText(getActivity(), R.string.success_delete_product, Toast.LENGTH_SHORT).show();
//                                            listProduct.getAdapter().notifyDataSetChanged();
//                                        }
//
//                                        @Override
//                                        public void onError(Object source, int errCode) throws Exception
//                                        {
//                                            Toast.makeText(getActivity(), R.string.fail_delete_product, Toast.LENGTH_SHORT).show();
//                                        }
//                                    }
//                                    , systemId
//                                );
//                            }
//                            catch(Exception e) {
//                                Log.d(Util.APP_TAG, "Error: " + e.getMessage());
//                                e.printStackTrace();
//                                Toast.makeText(getActivity(), R.string.internal_server_error, Toast.LENGTH_SHORT).show();
//                            }
//                        }
//                    }
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
//            return this.products != null ? this.products.size() + 1 : 1;
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

}
