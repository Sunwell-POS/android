package com.sunwell.pos.mobile.fragment;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.example.sunwell.pos_mobile.R;
import com.sunwell.pos.mobile.model.SalesInvoice;
import com.sunwell.pos.mobile.model.SalesPayment;
import com.sunwell.pos.mobile.service.ProductService;
import com.sunwell.pos.mobile.util.ImageDownloader;
import com.sunwell.pos.mobile.util.ResultListener;
import com.sunwell.pos.mobile.util.Util;

/**
 * Created by sunwell on 10/18/17.
 */

public class InvoiceFragmentSmallScreen extends Fragment {

    private static final String PROGRESS_FETCH_PRODUCT = "progressFetchProduct";
    private SalesInvoice argSI;
//    private ImageDownloader<StockCardHolder> downloader;
    private ResultListener<SalesPayment> dialogListener;
    private InvoiceDetailFragment detailFragment;
    private InvoicePagerFragment invPagerFragment;
    private PagerFragment pagerFragment;
    private Button btnTotal ;

    private static String SALES_INVOICE = "sales_invoice";

    public static InvoiceFragmentSmallScreen newInstance(SalesInvoice _si) {
        Log.d(Util.APP_TAG, "CALL NEW INST");
        Bundle bundle = new Bundle();
        bundle.putSerializable(InvoiceFragmentSmallScreen.SALES_INVOICE, _si);
        InvoiceFragmentSmallScreen fragment = new InvoiceFragmentSmallScreen();
        fragment.setArguments(bundle);
        return fragment;
    }


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        try {
            super.onCreate(savedInstanceState);
            Bundle arguments = getArguments();
            argSI = arguments != null ? (SalesInvoice) arguments.get(InvoiceFragmentSmallScreen.SALES_INVOICE) : null ;

//            if(!Util.isSmallScreen(getActivity())) {
//                if (ProductService.categories == null) {
//                    ProductService.fetchCategories(null);
//                }
//
//                Handler responseHandler = new Handler();
//                Drawable defDrawable = getResources().getDrawable(R.mipmap.ic_launcher);
//                int imagePxSize = 0;
//                if(Util.isSmallScreen(getActivity()))
//                    imagePxSize = Util.dpToPx(R.dimen.small_image, getActivity());
//                else
//                    imagePxSize = Util.dpToPx(R.dimen.big_image, getActivity());
//                downloader = new ImageDownloader<>(responseHandler, defDrawable, imagePxSize, imagePxSize);
//                downloader.setThumbnailDownloadListener(
//                        new ImageDownloader.DownloadListener<InvoiceFragment_old3.ProductHolder>() {
//
//                            @Override
//                            public void onImageDownloaded(InvoiceFragment_old3.ProductHolder _holder, Bitmap _bitmap) throws Exception {
//                                Log.d(Util.APP_TAG, " ON IMAGEDOWNLOADED CALLED: " + InvoiceFragment_old3.this);
//                                if(!isAdded() || getActivity() == null)
//                                    return;
//                                Drawable drawable = new BitmapDrawable(getResources(), _bitmap);
//                                _holder.bindDrawable(drawable);
//                            }
//
//                            @Override
//                            public void onImageDownloaded(InvoiceFragment_old3.ProductHolder _holder, Drawable _drawable) throws Exception {
//                                Log.d(Util.APP_TAG, " ON IMAGEDOWNLOADED2 CALLED: " + this);
//                                if(!isAdded() || getActivity() == null)
//                                    return;
//                                _holder.bindDrawable(_drawable);
//                            }
//                        }
//                );
//                downloader.start();
//                downloader.getLooper();
//            }

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
            View v = _inflater.inflate(R.layout.invoice_small_screen, container, false);

            invPagerFragment = InvoicePagerFragment.newInstance(argSI);
            invPagerFragment.setTargetFragment(this, Util.REQUEST_CODE_READ);
            getFragmentManager().beginTransaction()
                    .replace(R.id.layout_invoice_content, invPagerFragment)
                    .commit();

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
            if (_requestCode == Util.REQUEST_CODE_READ) {
                if (_resultCode == Util.RESULT_CODE_SUCCESS) {

                    detailFragment = InvoiceDetailFragment.newInstance(argSI);
                    detailFragment.setTargetFragment(this, Util.REQUEST_CODE_CREATE);

                    getFragmentManager().beginTransaction().replace(R.id.layout_invoice_content, detailFragment).commit();
                }
            }
            else if (_requestCode == Util.REQUEST_CODE_CREATE) {
                if (_resultCode == Util.RESULT_CODE_SUCCESS) {
                    SalesPayment salesPayment = (SalesPayment) data.getSerializableExtra("salesPayment");
                    if (dialogListener != null)
                        dialogListener.onResult(InvoiceFragmentSmallScreen.this, salesPayment);
                    else
                        Toast.makeText(getActivity(), R.string.success_create_sales_payment, Toast.LENGTH_SHORT).show();
                }
                else if(_resultCode == Util.RESULT_CODE_CANCELED) {
                    invPagerFragment = InvoicePagerFragment.newInstance(argSI);
                    invPagerFragment.setTargetFragment(this, Util.REQUEST_CODE_READ);
//                    FragmentManager fmg = getFragmentManager() ;
//                    if(fmg == null)
//                        fmg = getActivity().getSupportFragmentManager();

                    getFragmentManager().beginTransaction().replace(R.id.layout_invoice_content, invPagerFragment).commit();
//                    getFragmentManager().beginTransaction()
//                            .replace(R.id.layout_content, invPagerFragment)
//                            .commit();
                }
                else {
                    int errCode = data.getIntExtra("errorCode", 999);
                    Log.d(Util.APP_TAG, "ERROR CODE: " + errCode);
                    Toast.makeText(getActivity(), R.string.fail_create_sales_payment, Toast.LENGTH_SHORT).show();
                }
            }
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

}
