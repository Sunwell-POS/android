package com.sunwell.pos.mobile.fragment;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.sunwell.pos_mobile.R;
import com.sunwell.pos.mobile.dialog.InvoiceLineDialogFragment;
import com.sunwell.pos.mobile.model.Product;
import com.sunwell.pos.mobile.model.SalesInvoice;
import com.sunwell.pos.mobile.model.SalesPayment;
import com.sunwell.pos.mobile.service.LoginService;
import com.sunwell.pos.mobile.service.ProductService;
import com.sunwell.pos.mobile.util.ImageDownloader;
import com.sunwell.pos.mobile.util.RecycleViewAdapter;
import com.sunwell.pos.mobile.util.ResultListener;
import com.sunwell.pos.mobile.util.ResultWatcher;
import com.sunwell.pos.mobile.util.Util;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * Created by sunwell on 10/18/17.
 */

public class InvoicePagerFragment extends Fragment {

    private static final String PROGRESS_FETCH_PRODUCT = "progressFetchProduct";
    private SalesInvoice argSI;
    private TotalButtonListener btnListener;
    private ImageDownloader<ProductHolder> downloader;
//    private ResultListener<SalesPayment> dialogListener;
//    private InvoiceDetailFragment detailFragment;
    private ProductPagerFragmentNew pagerFragment;
    private Button btnTotal ;

    private static String SALES_INVOICE = "sales_invoice";

    public static InvoicePagerFragment newInstance(SalesInvoice _si) {
        Log.d(Util.APP_TAG, "CALL NEW INST");
        Bundle bundle = new Bundle();
        bundle.putSerializable(InvoicePagerFragment.SALES_INVOICE, _si);
        InvoicePagerFragment fragment = new InvoicePagerFragment();
        fragment.setArguments(bundle);
        return fragment;
    }


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        try {
            super.onCreate(savedInstanceState);
            Log.d(Util.APP_TAG, " ON CREATE CALLED: " + this);
            if (ProductService.categories == null) {
                ProductService.fetchCategories(null);
            }
            Bundle arguments = getArguments();
            argSI = arguments != null ? (SalesInvoice) arguments.get(InvoicePagerFragment.SALES_INVOICE) : null ;

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
                            Log.d(Util.APP_TAG, " ON IMAGEDOWNLOADED CALLED: " + InvoicePagerFragment.this);
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
        }
        catch(Exception e) {
            Log.d(Util.APP_TAG, "Error: " + e.getMessage());
            e.printStackTrace();
            Toast.makeText(getActivity(), R.string.error_product_list, Toast.LENGTH_SHORT).show();
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater _inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        try {
            super.onCreateView(_inflater, container, savedInstanceState);
            Log.d(Util.APP_TAG, "OCV CALLED");
            View v = _inflater.inflate(R.layout.invoice_pager, container, false);

            Log.d(Util.APP_TAG, "Total: " + argSI.getTotal());
            btnTotal = (Button)v.findViewById(R.id.button_total);
            btnTotal.setText("Total: " + Util.round(argSI.getTotal()));

            btnTotal.setOnClickListener(
                    new View.OnClickListener()
                    {
                        @Override
                        public void onClick(View v)
                        {
//                            if (getTargetFragment() != null) {
//                                Fragment targetFragment = getTargetFragment();
//                                targetFragment.onActivityResult(getTargetRequestCode(), Util.RESULT_CODE_SUCCESS, null);
//                            }

                            if(btnListener != null) {
                                btnListener.onButtonClicked();
                            }
                        }
                    }
            );

            if(ProductService.products == null) {
                Log.d(Util.APP_TAG, "FRAGMENT: " + getFragmentManager().findFragmentById(R.id.layout_pager));
                ResultWatcher<List<Product>> listener = new ResultWatcher<List<Product>>() {
                    @Override
                    public void onResult(Object source, List<Product> _products) throws Exception {
                        Util.stopDialog(PROGRESS_FETCH_PRODUCT);
                        ProductAdapter adapter = new ProductAdapter(_products);
                        int objPerPage = 16;
                        if(Util.isSmallScreen(getActivity()))
                            objPerPage = 8;

//                        pagerFragment = PagerFragment.newInstance(adapter, objPerPage);
//                        pagerFragment = ProductPagerFragmentNew.newInstance(adapter, objPerPage);
                        pagerFragment = ProductPagerFragmentNew.newInstance(objPerPage);
                        pagerFragment.setAdapter(adapter);
                        getChildFragmentManager().beginTransaction()
                                .replace(R.id.layout_pager, pagerFragment)
                                .commit();
//                        getFragmentManager().beginTransaction()
//                                .replace(R.id.layout_pager, pagerFragment)
//                                .commit();
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
                    ProductAdapter adapter = new ProductAdapter(ProductService.products);
                    int objPerPage = 16;
                    if(Util.isSmallScreen(getActivity()))
                        objPerPage = 8;
//                    pagerFragment = ProductPagerFragmentNew.newInstance(adapter, objPerPage);
                    pagerFragment = ProductPagerFragmentNew.newInstance(objPerPage);
                    pagerFragment.setAdapter(adapter);
                    getChildFragmentManager().beginTransaction()
                            .replace(R.id.layout_pager, pagerFragment)
                            .commit();
//                    getFragmentManager().beginTransaction()
//                            .replace(R.id.layout_pager, pagerFragment)
//                            .commit();
            }
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
                    Toast.makeText(getActivity(), R.string.success_add_invoice_line, Toast.LENGTH_SHORT).show();
                    btnTotal.setText("Total: " + Util.round(argSI.getTotal()));
                }
                else if(_requestCode == Util.RESULT_CODE_FAILED) {
                    Log.d(Util.APP_TAG, " ERR CODE: " + data.getIntExtra("errorCode", -3));
                    Toast.makeText(getActivity(), R.string.fail_add_invoice_line, Toast.LENGTH_SHORT).show();
                }
            }
        }
        catch(Exception e) {
            Log.d(Util.APP_TAG, "Error: " + e.getMessage());
            e.printStackTrace();
            Toast.makeText(getActivity(), R.string.internal_server_error, Toast.LENGTH_SHORT).show();
        }
    }

    public void setButtonListener(TotalButtonListener _btn) {
        btnListener = _btn;
    }

    public static interface TotalButtonListener {
        public void onButtonClicked();
        public void onError(int _errCode);
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
                                InvoiceLineDialogFragment dialog = InvoiceLineDialogFragment.newInstance(argSI, prod);
                                dialog.setTargetFragment(InvoicePagerFragment.this, Util.REQUEST_CODE_ADD);
                                dialog.show(getFragmentManager(), "salesinvoiceline");
                            }
                        }
                );

                Log.d(Util.APP_TAG, "QUEUE IMAGe REQUEST: " + InvoicePagerFragment.this + " position: " + position);

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
            catch(Exception e) {
                Log.d(Util.APP_TAG, "Error: " + e.getMessage());
                e.printStackTrace();
                Toast.makeText(getActivity(), R.string.internal_server_error, Toast.LENGTH_SHORT).show();
            }
        }
    }
}
