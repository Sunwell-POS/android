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
import com.sunwell.pos.mobile.model.SalesInvoiceLine;
import com.sunwell.pos.mobile.model.SalesPayment;
import com.sunwell.pos.mobile.service.LoginService;
import com.sunwell.pos.mobile.service.ProductService;
import com.sunwell.pos.mobile.util.ImageDownloader;
import com.sunwell.pos.mobile.util.RecycleViewAdapter;
import com.sunwell.pos.mobile.util.ResultListener;
import com.sunwell.pos.mobile.util.ResultWatcher;
import com.sunwell.pos.mobile.util.Util;

import java.io.Serializable;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * Created by sunwell on 10/18/17.
 */

public class InvoiceFragment extends Fragment
{

    private static final String PROGRESS_FETCH_PRODUCT = "progressFetchProduct";
    private SalesInvoice argSI;
    private PListener pListener = new PListener();;
    private TBListener tbListener = new TBListener();;
    private ImageDownloader<ProductHolder> downloader;
    private ResultListener<SalesPayment> dialogListener;
    private InvoiceDetailFragment detailFragment;
    private InvoicePagerFragment invPagerFragment;
//    private PagerFragment pagerFragment;
//    private Button btnTotal ;

    private static String SALES_INVOICE = "sales_invoice";

    public static InvoiceFragment newInstance(SalesInvoice _si) {
        Log.d(Util.APP_TAG, "CALL NEW INST");
        Bundle bundle = new Bundle();
        bundle.putSerializable(InvoiceFragment.SALES_INVOICE, _si);
        InvoiceFragment fragment = new InvoiceFragment();
        fragment.setArguments(bundle);
        return fragment;
    }


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        try {
            super.onCreate(savedInstanceState);
            Bundle arguments = getArguments();
//            pListener = new PListener();
//            tbListener = new TBListener();
            argSI = arguments != null ? (SalesInvoice) arguments.get(InvoiceFragment.SALES_INVOICE) : null ;

            if(!Util.isSmallScreen(getActivity())) {
                if (ProductService.categories == null) {
                    ProductService.fetchCategories(null);
                }

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
                                if(!isAdded() || getActivity() == null)
                                    return;
                                _holder.bindDrawable(_drawable);
                            }
                        }
                );
                downloader.start();
                downloader.getLooper();
            }

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
//            Log.e(Util.APP_TAG, "OCV CALLED IN IF");
            View v = _inflater.inflate(R.layout.invoice_new, container, false);


            if(Util.isSmallScreen(getActivity())) {
                invPagerFragment = InvoicePagerFragment.newInstance(argSI);
//                  invPagerFragment.setTargetFragment(this, Util.REQUEST_CODE_READ);
                invPagerFragment.setButtonListener(tbListener);
//                getFragmentManager().beginTransaction()
//                        .replace(R.id.layout_invoice_content, invPagerFragment)
//                        .commit();
                getChildFragmentManager().beginTransaction()
                        .replace(R.id.layout_invoice_content, invPagerFragment)
                        .commit();
            }
            else {
                detailFragment = InvoiceDetailFragment.newInstance(argSI);
                detailFragment.setPaymentListener(pListener);
//                detailFragment.setTargetFragment(this, Util.REQUEST_CODE_CREATE);
                getChildFragmentManager().beginTransaction()
                        .replace(R.id.layout_payment_detail, detailFragment)
                        .commit();
//                getFragmentManager().beginTransaction()
//                        .replace(R.id.layout_payment_detail, detailFragment)
//                        .commit();

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

//                            PagerFragment pagerFragment = PagerFragment.newInstance(adapter, objPerPage);
//                            Log.e(Util.APP_TAG, "CALLING PPF NEW INSTANCE");
//                            PagerFragmentNew pagerFragment = ProductPagerFragment.newInstance(adapter, objPerPage);
//                            ProductPagerFragmentNew pagerFragment = ProductPagerFragmentNew.newInstance(adapter, objPerPage);
                            ProductPagerFragmentNew pagerFragment = ProductPagerFragmentNew.newInstance(objPerPage);
                            pagerFragment.setAdapter(adapter);
                            getChildFragmentManager().beginTransaction()
                                    .replace(R.id.layout_pager, pagerFragment)
                                    .commit();
//                            getFragmentManager().beginTransaction()
//                                    .replace(R.id.layout_pager, pagerFragment)
//                                    .commit();
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
                    ProductAdapter adapter = new ProductAdapter(ProductService.products);
                    int objPerPage = 16;
                    if(Util.isSmallScreen(getActivity()))
                        objPerPage = 8;
//                    PagerFragmentNew pagerFragment = ProductPagerFragment.newInstance(adapter, objPerPage);
//                    ProductPagerFragmentNew pagerFragment = ProductPagerFragmentNew.newInstance(adapter, objPerPage);
                    ProductPagerFragmentNew pagerFragment = ProductPagerFragmentNew.newInstance(objPerPage);
                    pagerFragment.setAdapter(adapter);
                    getChildFragmentManager().beginTransaction()
                            .replace(R.id.layout_pager, pagerFragment)
                            .commit();
//                    getFragmentManager().beginTransaction()
//                            .replace(R.id.layout_pager, pagerFragment)
//                            .commit();
                }
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
                    SalesInvoiceLine sil = (SalesInvoiceLine) data.getSerializableExtra("salesInvoiceLine");
                    detailFragment.addInvoiceLine(sil);
                    Toast.makeText(getActivity(), R.string.success_add_invoice_line, Toast.LENGTH_SHORT).show();
                } else if (_resultCode == Util.RESULT_CODE_FAILED){
                    int errCode = (Integer) data.getIntExtra("errorCode", -3);
                    int errMessageId = -3;
                    if(errCode == Util.ERROR_NOT_ENOUGH_STOCK)
                        errMessageId = R.string.not_enough_stock;
                    else
                        errMessageId = R.string.internal_server_error;
                    Toast.makeText(getActivity(), errMessageId, Toast.LENGTH_SHORT).show();
                }
            }
//            else if (_requestCode == Util.REQUEST_CODE_READ) {
//                if (_resultCode == Util.RESULT_CODE_SUCCESS) {
//
//                    detailFragment = InvoiceDetailFragment.newInstance(argSI);
//                    detailFragment.setTargetFragment(this, Util.REQUEST_CODE_CREATE);
//
//                    getFragmentManager().beginTransaction().replace(R.id.layout_invoice_content, detailFragment).commit();
//                }
//            }
//            else if (_requestCode == Util.REQUEST_CODE_CREATE) {
//                if (_resultCode == Util.RESULT_CODE_SUCCESS) {
//                    SalesPayment salesPayment = (SalesPayment) data.getSerializableExtra("salesPayment");
//                    if (dialogListener != null)
//                        dialogListener.onResult(InvoiceFragment.this, salesPayment);
//                    else
//                        Toast.makeText(getActivity(), R.string.success_create_sales_payment, Toast.LENGTH_SHORT).show();
//                }
//                else if(_resultCode == Util.RESULT_CODE_CANCELED) {
//                    invPagerFragment = InvoicePagerFragment.newInstance(argSI);
//                    invPagerFragment.setTargetFragment(this, Util.REQUEST_CODE_READ);
//                    getFragmentManager().beginTransaction().replace(R.id.layout_invoice_content, invPagerFragment).commit();
//                }
//                else {
//                    int errCode = data.getIntExtra("errorCode", 999);
//                    Log.d(Util.APP_TAG, "ERROR CODE: " + errCode);
//                    Toast.makeText(getActivity(), R.string.fail_create_sales_payment, Toast.LENGTH_SHORT).show();
//                }
//            }
        }
        catch(Exception e) {
            Log.d(Util.APP_TAG, "Error: " + e.getMessage());
            e.printStackTrace();
            Toast.makeText(getActivity(), R.string.internal_server_error, Toast.LENGTH_SHORT).show();
        }
    }

    public void setDialogListener(ResultListener<SalesPayment> _listener) {
        dialogListener = _listener;
    }

    private class TBListener implements InvoicePagerFragment.TotalButtonListener {
        @Override
        public void onButtonClicked()
        {
            detailFragment = InvoiceDetailFragment.newInstance(argSI);
//                                detailFragment.setTargetFragment(this, Util.REQUEST_CODE_CREATE);
            detailFragment.setPaymentListener(pListener);
            getChildFragmentManager().beginTransaction().replace(R.id.layout_invoice_content, detailFragment).commit();
//                                  getFragmentManager().beginTransaction().replace(R.id.layout_invoice_content, detailFragment).commit();
        }

        @Override
        public void onError(int _errCode)
        {

        }
    }

    private class PListener implements InvoiceDetailFragment.PaymentListener {
        @Override
        public void onPaymentMade(SalesPayment _payment) throws Exception
        {
            if(_payment != null) {
                if (dialogListener != null)
                    dialogListener.onResult(InvoiceFragment.this, _payment);
                else
                    Toast.makeText(getActivity(), R.string.success_create_sales_payment, Toast.LENGTH_SHORT).show();
            }
            else {
                if(Util.isSmallScreen(getActivity())) {
                    invPagerFragment = InvoicePagerFragment.newInstance(argSI);
                    invPagerFragment.setButtonListener(tbListener);
//                invPagerFragment.setTargetFragment(this, Util.REQUEST_CODE_READ);
                    getChildFragmentManager().beginTransaction().replace(R.id.layout_invoice_content, invPagerFragment).commit();
//                getFragmentManager().beginTransaction().replace(R.id.layout_invoice_content, invPagerFragment).commit();
                }
            }
        }

        @Override
        public void onError(int _errCode) throws Exception
        {
            Toast.makeText(getActivity(), R.string.fail_create_sales_payment, Toast.LENGTH_SHORT).show();
        }
    }

    private static class ProductHolder extends RecyclerView.ViewHolder {

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

    private  class ProductAdapter extends RecycleViewAdapter<Product, ProductHolder>
    {
        public ProductAdapter(List<Product> _products) {
            super(_products, RecycleViewAdapter.PLAIN);
//            this.products = _products;
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
                                dialog.setTargetFragment(InvoiceFragment.this, Util.REQUEST_CODE_ADD);
                                dialog.show(getFragmentManager(), "salesinvoiceline");
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
            catch(Exception e) {
                Log.d(Util.APP_TAG, "Error: " + e.getMessage());
                e.printStackTrace();
                Toast.makeText(getActivity(), R.string.internal_server_error, Toast.LENGTH_SHORT).show();
            }
        }
    }

}
