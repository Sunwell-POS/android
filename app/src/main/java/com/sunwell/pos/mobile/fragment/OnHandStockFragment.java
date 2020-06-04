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
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.sunwell.pos_mobile.R;
import com.sunwell.pos.mobile.dialog.OnHandDialogFragment;
import com.sunwell.pos.mobile.model.IncomingGood;
import com.sunwell.pos.mobile.model.OnHandStock;
import com.sunwell.pos.mobile.model.Product;
import com.sunwell.pos.mobile.model.Warehouse;
import com.sunwell.pos.mobile.service.InventoryService;
import com.sunwell.pos.mobile.service.LoginService;
import com.sunwell.pos.mobile.service.ProductService;
import com.sunwell.pos.mobile.util.ImageDownloader;
import com.sunwell.pos.mobile.util.RecycleViewAdapter;
import com.sunwell.pos.mobile.util.ResultWatcher;
import com.sunwell.pos.mobile.util.Util;

import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * Created by sunwell on 10/18/17.
 */

public class OnHandStockFragment extends Fragment {

    private static final String PROGRESS_FETCH_ONhAND = "progressFetchOnHand";
    private static final String PROGRESS_FETCH_PRODUCT = "progressFetchProduct";
    private static final String PROGRESS_FETCH_WAREHOUSE = "progressFetchWarehouse";

    private ImageDownloader<OnHandHolder> downloader;
    private RecyclerView listOnHandStock;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        try {
            super.onCreate(savedInstanceState);
            Handler responseHandler = new Handler();
            Drawable defDrawable = getResources().getDrawable(R.mipmap.ic_launcher);
            int imagePxSize = Util.dpToPx(R.dimen.big_image, getActivity());
            downloader = new ImageDownloader<>(responseHandler, defDrawable, imagePxSize, imagePxSize);
            downloader.setThumbnailDownloadListener(
                    new ImageDownloader.DownloadListener<OnHandHolder>() {

                        @Override
                        public void onImageDownloaded(OnHandHolder _holder, Bitmap _bitmap) throws Exception {
                            Drawable drawable = new BitmapDrawable(getResources(), _bitmap);
                            _holder.bindDrawable(drawable);
                        }

                        @Override
                        public void onImageDownloaded(OnHandHolder _tenantHolder, Drawable _drawable) throws Exception {
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
            View v = inflater.inflate(R.layout.standard_list, container, false);
            Button btnAddOnHand = (Button) v.findViewById(R.id.button_add);
            btnAddOnHand.setText(R.string.add_incoming_stock);
            OnHandAdapter listAdapter;

            btnAddOnHand.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        try {
                            FragmentManager fragmentManager = getFragmentManager();
                            OnHandDialogFragment dialog = new OnHandDialogFragment();
                            dialog.setTargetFragment(OnHandStockFragment.this, Util.REQUEST_CODE_ADD);
                            dialog.show(fragmentManager, "OnHandStock");
                        }
                        catch(Exception e) {
                            Log.d(Util.APP_TAG, "Error: " + e.getMessage());
                            e.printStackTrace();
                            Toast.makeText(getActivity(), R.string.internal_server_error, Toast.LENGTH_SHORT).show();
                        }
                    }
                }
            );
            listOnHandStock = (RecyclerView) v.findViewById(R.id.list_objects);
            listOnHandStock.setLayoutManager(new LinearLayoutManager(getActivity()));
            ResultWatcher<List<OnHandStock>> listener = new ResultWatcher<List<OnHandStock>>() {
                @Override
                public void onResult(Object source, List<OnHandStock> _stocks) throws Exception {
                    Util.stopDialog(PROGRESS_FETCH_ONhAND);
                    listOnHandStock.setAdapter(new OnHandAdapter(_stocks));
                }

                @Override
                public void onError(Object source, int errCode) throws Exception {
                    Util.stopDialog(PROGRESS_FETCH_ONhAND);
                    Toast.makeText(getActivity(), R.string.internal_server_error, Toast.LENGTH_SHORT).show();
                }
            };

            InventoryService.fetchOnHandStocks(listener);
            Util.showDialog(getFragmentManager(), PROGRESS_FETCH_ONhAND);
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
                    List<IncomingGood> goods = (List<IncomingGood>)data.getSerializableExtra("listIncomingGood");
                    List<OnHandStock> stocks = null;
                    if(goods != null && goods.size() > 0) {
                        stocks = new LinkedList<>();
                        for(IncomingGood ic : goods) {
                            OnHandStock ohs = InventoryService.findOnHandStockByProductAndWarehouse(ic.getProduct(), ic.getWarehouse());
                            stocks.add(ohs);
                        }
                        ((OnHandAdapter) listOnHandStock.getAdapter()).updateItems(stocks);
                    }
                    Toast.makeText(getActivity(), R.string.success_add_product, Toast.LENGTH_SHORT).show();
                } else {
                    Log.d(Util.APP_TAG, " ERR CODE: " + data.getIntExtra("errorCode", -3));
                    Toast.makeText(getActivity(), R.string.fail_add_onhand, Toast.LENGTH_SHORT).show();
                }
            }
        }
        catch(Exception e) {
            Log.d(Util.APP_TAG, "Error: " + e.getMessage());
            e.printStackTrace();
            Toast.makeText(getActivity(), R.string.internal_server_error, Toast.LENGTH_SHORT).show();
        }
    }

    private class OnHandHolder extends RecyclerView.ViewHolder {

        public TextView textNo;
        public TextView textName;
        public TextView textQty;
        public TextView textWarehouse;
        public TextView textLastinputtedDate;
        public ImageView imageProduct;
        public ImageButton btnSearch;

        public EditText inputQty;
        public Spinner spinnerProduct;
        public Spinner spinnerWarehouse;


        public OnHandHolder(View itemView) {
            super(itemView);
            textNo = (TextView) itemView.findViewById(R.id.text_no);
            textName = (TextView) itemView.findViewById(R.id.text_name);
            textQty = (TextView) itemView.findViewById(R.id.text_qty);
            textWarehouse = (TextView) itemView.findViewById(R.id.text_warehouse);
            textLastinputtedDate = (TextView) itemView.findViewById(R.id.text_last_inputted_date);
            imageProduct = (ImageView) itemView.findViewById(R.id.image_product);

            inputQty = (EditText) itemView.findViewById(R.id.input_qty);
            spinnerProduct = (Spinner) itemView.findViewById(R.id.spinner_product);
            spinnerWarehouse = (Spinner) itemView.findViewById(R.id.spinner_warehouse);

            btnSearch = (ImageButton) itemView.findViewById(R.id.button_search);
        }

        public void bindDrawable(Drawable drawable) {
            imageProduct.setImageDrawable(drawable);
        }
    }

    private class OnHandAdapter extends RecycleViewAdapter<OnHandStock, OnHandHolder>
    {
        private List<OnHandStock> fullOnHandStocks;

        public OnHandAdapter(List<OnHandStock> _stocks) {
            super(new LinkedList<OnHandStock>((_stocks != null && _stocks.size() > 0) ? _stocks : new LinkedList<OnHandStock>()), RecycleViewAdapter.USE_FILTER);
            fullOnHandStocks = _stocks;
        }

        @Override
        public OnHandHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            try {
                LayoutInflater layoutInflater = LayoutInflater.from(getActivity());

                View view;
                if (viewType == HEADER) {
                    view = layoutInflater.inflate(R.layout.onhand_header, parent, false);
                }
                else if(viewType == FILTER) {
                    view = layoutInflater.inflate(R.layout.onhand_filter, parent, false);
                }
                else {
                    view = layoutInflater.inflate(R.layout.onhand_row, parent, false);
                }
                return new OnHandHolder(view);
            }
            catch(Exception e) {
                Log.d(Util.APP_TAG, "Error: " + e.getMessage());
                e.printStackTrace();
                Toast.makeText(getActivity(), R.string.internal_server_error, Toast.LENGTH_SHORT).show();
                return null;
            }
        }

        @Override
        public void onBindViewHolder(final OnHandHolder holder, final int position) {
            try {
                if (position == 0 || products == null)
                    return;

                if(position > 1) {

                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                    final OnHandStock ohs = this.products.get(position - 2);
                    holder.textNo.setText("" + (position - 1));
                    holder.textName.setText(ohs.getProduct().getName());
                    holder.textQty.setText(String.valueOf(ohs.getQty()));
                    holder.textWarehouse.setText(ohs.getWarehouse().getName());
                    if(ohs.getLastInputDate() != null)
                        holder.textLastinputtedDate.setText(sdf.format(ohs.getLastInputDate()));

                    if (ohs.getProduct().getImg() == null)
                        downloader.queueImage(holder, null);
                    else {
                        Map<String, String> params = new HashMap<String, String>();
                        params.put("sessionString", LoginService.sessionString);
                        params.put("image", ohs.getProduct().getImg());
                        downloader.queueImage(holder, Util.getImageURL(Util.PRODUCT_URL, params));
                    }
                }
                else if(position == 1) {
                    ProductService.fillProductSpinner(holder.spinnerProduct, getActivity(), getFragmentManager());
                    InventoryService.fillWarehouseSpinner(holder.spinnerWarehouse, getActivity(), getFragmentManager());
                    holder.btnSearch.setOnClickListener(
                            new View.OnClickListener()
                            {
                                @Override
                                public void onClick(View v)
                                {
                                    Warehouse wr = null;
                                    Product p = null;
                                    if(holder.spinnerWarehouse.getSelectedItemPosition() > 0)
                                        wr = (Warehouse ) holder.spinnerWarehouse.getSelectedItem();
                                    if(holder.spinnerProduct.getSelectedItemPosition() > 0)
                                        p = (Product ) holder.spinnerProduct.getSelectedItem();
                                    List<OnHandStock> filteredStocks = new LinkedList<>();
                                    if(fullOnHandStocks != null) {
                                        for (OnHandStock ohs : fullOnHandStocks) {
                                            if(p != null)
                                                if(!ohs.getProduct().equals(p))
                                                    continue;

                                            if(wr != null )
                                                if(!ohs.getWarehouse().equals(wr))
                                                    continue;

                                            filteredStocks.add(ohs);
                                        }
                                        setItems(filteredStocks);
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
    }
}
