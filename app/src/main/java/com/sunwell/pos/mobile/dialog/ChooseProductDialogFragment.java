package com.sunwell.pos.mobile.dialog;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import com.example.sunwell.pos_mobile.R;
import com.sunwell.pos.mobile.fragment.InvoicePagerFragment;
import com.sunwell.pos.mobile.fragment.PagerFragment;
import com.sunwell.pos.mobile.model.PaymentMethodObj;
import com.sunwell.pos.mobile.model.Product;
import com.sunwell.pos.mobile.model.SalesInvoice;
import com.sunwell.pos.mobile.service.InvoiceService;
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

public class ChooseProductDialogFragment extends DialogFragment {

    private static final String PROGRESS_FETCH_PRODUCT = "progressFetchProduct";
    private static String CATEGORY = "category";
    private ImageDownloader<ProductHolder> downloader;
//    private ResultListener<Product> dialogListener ;
//    private TableLayout rootView ;
    private PagerFragment pagerFragment;


    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        try {
            final Dialog dialog = new Dialog(getActivity());
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            dialog.setContentView(R.layout.choose_product_dialog);
            dialog.findViewById(R.id.root);

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

            final List<Product> list = new ArrayList<>();
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

                        pagerFragment = PagerFragment.newInstance(adapter, objPerPage);
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
                pagerFragment = PagerFragment.newInstance(adapter, objPerPage);
                getFragmentManager().beginTransaction()
                        .replace(R.id.layout_pager, pagerFragment)
                        .commit();
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
