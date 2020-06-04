package com.sunwell.pos.mobile.fragment;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
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
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.sunwell.pos_mobile.R;
import com.sunwell.pos.mobile.dialog.ChooseProductDialogFragment2;
import com.sunwell.pos.mobile.dialog.ChooseProductDialogFragment3;
import com.sunwell.pos.mobile.dialog.DatePickerDialogFragment;
import com.sunwell.pos.mobile.model.IncomingGood;
import com.sunwell.pos.mobile.model.OutcomingGood;
import com.sunwell.pos.mobile.model.Product;
import com.sunwell.pos.mobile.model.StockCardItem;
import com.sunwell.pos.mobile.model.Warehouse;
import com.sunwell.pos.mobile.service.InventoryService;
import com.sunwell.pos.mobile.util.RecycleViewAdapter;
import com.sunwell.pos.mobile.util.ResultWatcher;
import com.sunwell.pos.mobile.util.Util;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * Created by sunwell on 10/18/17.
 */

public class StockCardFragment extends Fragment {

    private static final String PROGRESS_FETCH_STOCK_CARD = "progressFetchStockCard";
    private static final String PROGRESS_FETCH_WAREHOUSE = "progressFetchWarehouse";

//    private ImageDownloader<StockMutationHolder> downloader;
    private RecyclerView listStockCard;
    private List<IncomingGood> listIncomingGoods;
    private List<OutcomingGood> listOutcomingGoods;
    private ResultWatcher<Boolean> readyListener;
    private Boolean isError ;

    private Product pickedProduct;
    private Date startDate;
    private Date endDate;

    private EditText inputProduct;
    private EditText inputStartDate ;
    private EditText inputEndDate ;
    private Spinner spinnerWarehouse;


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        try {
            super.onCreate(savedInstanceState);
            isError = false;

            if(savedInstanceState != null) {
                pickedProduct = (Product) savedInstanceState.getSerializable("product");
                startDate = (Date) savedInstanceState.getSerializable("startDate");
                endDate = (Date) savedInstanceState.getSerializable("endDate");
            }
//            Handler responseHandler = new Handler();
//            Drawable defDrawable = getResources().getDrawable(R.mipmap.ic_launcher);
//            int imagePxSize = Util.dpToPx(R.dimen.big_image, getActivity());
//            downloader = new ImageDownloader<>(responseHandler, defDrawable, imagePxSize, imagePxSize);
//            downloader.setThumbnailDownloadListener(
//                    new ImageDownloader.DownloadListener<StockMutationHolder>() {
//
//                        @Override
//                        public void onImageDownloaded(StockMutationHolder _holder, Bitmap _bitmap) throws Exception {
//                            Drawable drawable = new BitmapDrawable(getResources(), _bitmap);
//                            _holder.bindDrawable(drawable);
//                        }
//
//                        @Override
//                        public void onImageDownloaded(StockMutationHolder _tenantHolder, Drawable _drawable) throws Exception {
//                            _tenantHolder.bindDrawable(_drawable);
//                        }
//                    }
//            );
//            downloader.start();
//            downloader.getLooper();
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
            View v = inflater.inflate(R.layout.stock_card, container, false);
            inputProduct = (EditText)v.findViewById(R.id.input_product);
            inputStartDate = (EditText)v.findViewById(R.id.input_start_date);
            inputEndDate = (EditText)v.findViewById(R.id.input_end_date);
            spinnerWarehouse = (Spinner) v.findViewById(R.id.spinner_warehouse);
            Button btnRefesh = (Button) v.findViewById(R.id.button_refresh);
            StockCardAdapter listAdapter;
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            endDate = new Date();
            Calendar cal = Calendar.getInstance();
            cal.setTime(endDate);
            cal.add(Calendar.MONTH, -1);
            startDate = cal.getTime();

            inputStartDate.setText(sdf.format(startDate));
            inputEndDate.setText(sdf.format(endDate));

            inputProduct.setOnFocusChangeListener(
                    new View.OnFocusChangeListener()
                    {

                        @Override
                        public void onFocusChange(View v, boolean hasFocus)
                        {
                            if(hasFocus) {
                                FragmentManager fragmentManager = getFragmentManager();
                                ChooseProductDialogFragment3 dialog = new ChooseProductDialogFragment3();
                                dialog.setTargetFragment(StockCardFragment.this, Util.REQUEST_CODE_CHOOSE);
                                dialog.show(fragmentManager, "Pick Product");
                            }

                        }
                    }
            );

            inputProduct.setOnClickListener(
                    new View.OnClickListener()
                    {
                        @Override
                        public void onClick(View v)
                        {
                            FragmentManager fragmentManager = getFragmentManager();
                            ChooseProductDialogFragment3 dialog = new ChooseProductDialogFragment3();
                            dialog.setTargetFragment(StockCardFragment.this, Util.REQUEST_CODE_CHOOSE);
                            dialog.show(fragmentManager, "Pick Product");
                        }
                    }
            );

            View.OnClickListener dateListener = new View.OnClickListener()
            {

                @Override
                public void onClick(View v)
                {
                    DatePickerDialogFragment dlg = new DatePickerDialogFragment();
                    dlg.setTargetFragment(StockCardFragment.this, Util.REQUEST_CODE_PICK);
                    dlg.show(getFragmentManager(), "pickDate");
                }
            };

            inputStartDate.setOnFocusChangeListener(
                    new View.OnFocusChangeListener()
                    {

                        @Override
                        public void onFocusChange(View v, boolean hasFocus)
                        {
                            if(hasFocus) {
                                DatePickerDialogFragment dlg = DatePickerDialogFragment.newInstance(startDate);
                                dlg.setTargetFragment(StockCardFragment.this, Util.REQUEST_CODE_PICK);
                                dlg.show(getFragmentManager(), "pickDate");
                            }

                        }
                    }
            );

            inputEndDate.setOnFocusChangeListener(
                    new View.OnFocusChangeListener()
                    {

                        @Override
                        public void onFocusChange(View v, boolean hasFocus)
                        {
                            if(hasFocus) {
                                DatePickerDialogFragment dlg = DatePickerDialogFragment.newInstance(endDate);
                                dlg.setTargetFragment(StockCardFragment.this, Util.REQUEST_CODE_PICK);
                                dlg.show(getFragmentManager(), "pickDate");
                            }

                        }
                    }
            );

            inputStartDate.setOnClickListener(
                    new View.OnClickListener()
                    {
                        @Override
                        public void onClick(View v)
                        {
                            DatePickerDialogFragment dlg = DatePickerDialogFragment.newInstance(startDate);
                            dlg.setTargetFragment(StockCardFragment.this, Util.REQUEST_CODE_PICK);
                            dlg.show(getFragmentManager(), "pickDate");
                        }
                    }
            );

            inputEndDate.setOnClickListener(
                    new View.OnClickListener()
                    {
                        @Override
                        public void onClick(View v)
                        {
                            DatePickerDialogFragment dlg = DatePickerDialogFragment.newInstance(endDate);
                            dlg.setTargetFragment(StockCardFragment.this, Util.REQUEST_CODE_PICK);
                            dlg.show(getFragmentManager(), "pickDate");
                        }
                    }
            );

            listStockCard = (RecyclerView) v.findViewById(R.id.list_stock);
            listStockCard.setLayoutManager(new LinearLayoutManager(getActivity()));

            readyListener = new ResultWatcher<Boolean>() {
                @Override
                public void onResult(Object source, Boolean _ready) throws Exception {
                    Util.stopDialog(PROGRESS_FETCH_STOCK_CARD);
                    listStockCard.setAdapter(new StockCardAdapter(convertToStockCardItem(listIncomingGoods, listOutcomingGoods)));
                }

                @Override
                public void onError(Object source, int errCode) throws Exception {
                    Util.stopDialog(PROGRESS_FETCH_STOCK_CARD);
                    Toast.makeText(getActivity(), R.string.internal_server_error, Toast.LENGTH_SHORT).show();
                }
            };

            final ResultWatcher<List<IncomingGood>> incomingListener = new ResultWatcher<List<IncomingGood>>() {
                @Override
                public void onResult(Object source, List<IncomingGood> _goods) throws Exception {
                    listIncomingGoods = _goods;
                    checkReady(-1);
                }

                @Override
                public void onError(Object source, int errCode) throws Exception {
//                    Util.stopDialog(PROGRESS_FETCH_STOCK_CARD);
                    Toast.makeText(getActivity(), R.string.internal_server_error, Toast.LENGTH_SHORT).show();
                    checkReady(errCode);
                }
            };

            final ResultWatcher<List<OutcomingGood>> outcomingListener = new ResultWatcher<List<OutcomingGood>>() {
                @Override
                public void onResult(Object source, List<OutcomingGood> _goods) throws Exception {
                    listOutcomingGoods = _goods;
                    checkReady(-1);
                }

                @Override
                public void onError(Object source, int errCode) throws Exception {
//                    Util.stopDialog(PROGRESS_FETCH_STOCK_CARD);
                    Toast.makeText(getActivity(), R.string.internal_server_error, Toast.LENGTH_SHORT).show();
                    checkReady(errCode);
                }
            };

            btnRefesh.setOnClickListener(
                    new View.OnClickListener()
                    {
                        @Override
                        public void onClick(View v)
                        {
                            try {
                                if (!validateInput())
                                    return;

                                listIncomingGoods = null;
                                listOutcomingGoods = null;
                                isError = false;

                                String prodId = pickedProduct.getSystemId();
                                String warehouseId = ((Warehouse)spinnerWarehouse.getSelectedItem()).getSystemId();
                                String startDate = inputStartDate.getText().toString();
                                String endDate = inputEndDate.getText().toString();

                                Map<String, String> params = new HashMap<String, String>();
                                params.put("productId", prodId);
                                params.put("warehouseId", warehouseId);
                                params.put("startDate", startDate + "+00:00:00");
                                params.put("endDate", endDate + "+00:00:00");

                                InventoryService.fetchIncomingGoods(incomingListener, params);
                                InventoryService.fetchOutcomingGoods(outcomingListener, params);
                                Util.showDialog(getFragmentManager(), PROGRESS_FETCH_STOCK_CARD);
                            }
                            catch(Exception _e) {
                                Log.d(Util.APP_TAG, "Error: " + _e.getMessage());
                                Toast.makeText(getActivity(), R.string.internal_server_error, Toast.LENGTH_SHORT).show();
                            }
                        }
                    }
            );

            InventoryService.fillWarehouseSpinner(spinnerWarehouse, getActivity(), getFragmentManager());
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
            super.onActivityResult(_requestCode, _resultCode, data);
            if (_requestCode == Util.REQUEST_CODE_PICK) {
                if (data != null) {
                    if(inputStartDate.hasFocus()) {
                        startDate = (Date) data.getSerializableExtra(DatePickerDialogFragment.DATE);
                        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                        inputStartDate.setText(sdf.format(startDate));
                    }
                    else {
                        endDate = (Date)data.getSerializableExtra(DatePickerDialogFragment.DATE);
                        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                        inputEndDate.setText(sdf.format(endDate));
                    }
                }
            }
            else if(_requestCode == Util.REQUEST_CODE_CHOOSE) {
                if(data != null) {
                    pickedProduct = (Product) data.getSerializableExtra("product");
                    inputProduct.setText(pickedProduct.getName());
                }
            }
        }
        catch(Exception e) {
            Log.d(Util.APP_TAG, "Error: " + e.getMessage());
            e.printStackTrace();
            Toast.makeText(getActivity(), R.string.internal_server_error, Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState)
    {
        super.onSaveInstanceState(outState);

        if(pickedProduct != null)
            outState.putSerializable("product", pickedProduct);

        if(startDate != null)
            outState.putSerializable("startDate", startDate);

        if(endDate != null)
            outState.putSerializable("endDate", endDate);
    }

    private synchronized void checkReady(int errCode) throws Exception {
        if(isError)
            return;

        if(listIncomingGoods != null && listOutcomingGoods != null)
            readyListener.onResult(null, true);
        else {
            if(errCode != -1) {
                isError = true;
                readyListener.onError(null, errCode);
            }
        }
    }

    private Boolean validateInput() {
        if(pickedProduct == null) {
            Toast.makeText(getActivity(), R.string.product_field_cannot_be_empty, Toast.LENGTH_SHORT).show();
            return false;
        }

        if(!(spinnerWarehouse.getSelectedItemPosition() > 0)) {
            Toast.makeText(getActivity(), R.string.warehouse_field_cannot_be_empty, Toast.LENGTH_SHORT).show();
            return false;
        }

        if(startDate == null || endDate == null) {
            Toast.makeText(getActivity(), R.string.date_field_cannot_be_empty, Toast.LENGTH_SHORT).show();
            return false;
        }

        return true;
    }

    private List<StockCardItem> convertToStockCardItem(List<IncomingGood> _iGoods, List<OutcomingGood> _oGoods) {
        List<IncomingGood> iGoods = new LinkedList<>(_iGoods);
        List<OutcomingGood> oGoods = new LinkedList<>(_oGoods);
        List<StockCardItem> listItem = new LinkedList<>();
        List<StockCardItem> sortedList = new LinkedList<>();

        IncomingGood prevIncSum = new IncomingGood();
        OutcomingGood prevOutSum = new OutcomingGood();
        prevIncSum.setQty(0.0);
        prevOutSum.setQty(0.0);
        if(iGoods != null && iGoods.size() > 0) {
            prevIncSum = iGoods.remove(0);
            for (IncomingGood g : iGoods) {
                StockCardItem item = new StockCardItem();
                item.setDate(g.getIncomingDate());
                item.setRegister(g.getQty());
                item.setPicked(0.0);
                listItem.add(item);
            }
        }

        if(oGoods != null && oGoods.size() > 0) {
            prevOutSum = oGoods.remove(0);
            for (OutcomingGood g : oGoods) {
                StockCardItem item = new StockCardItem();
                item.setDate(g.getOutcomingDate());
                item.setRegister(0.0);
                item.setPicked(g.getQty());
                listItem.add(item);
            }
        }

        double balance = prevIncSum.getQty() - prevOutSum.getQty();

        if(listItem.size() > 0) {
            if (Build.VERSION.SDK_INT > Build.VERSION_CODES.N) {
                listItem.sort(
                        new Comparator<StockCardItem>()
                        {
                            @Override
                            public int compare(StockCardItem o1, StockCardItem o2)
                            {
                                return o1.getDate().compareTo(o2.getDate());
                            }
                        }
                );
                sortedList = listItem;
            }
            else {
                while(listItem.size() > 0) {
                    int index = 0;
                    for(int i = 1 ; i < listItem.size() ; i++) {
                        if(listItem.get(i).getDate().before(listItem.get(index).getDate()))
                            index = i;
                    }
                    StockCardItem min = listItem.remove(index);
                    sortedList.add(min);
                }
            }
        }

        StockCardItem stcItem = new StockCardItem();
//        Date minDate ;
//        if(sortedList.size() > 0)
//            minDate = sortedList.get(0).getDate();
//        else
        Calendar cal = Calendar.getInstance();
        cal.setTime(startDate);
        cal.add(Calendar.DATE, -1);
        stcItem.setDate(cal.getTime());
        stcItem.setBalance(balance);

        sortedList.add(0, stcItem);

        for(int i = 1; i < sortedList.size() ; i++) {
            StockCardItem prevItem = sortedList.get(i - 1);
            StockCardItem currItem = sortedList.get(i);
            currItem.setBalance(prevItem.getBalance() + currItem.getRegister() - currItem.getPicked());
        }

        return sortedList;
    }

    private class StockCardHolder extends RecyclerView.ViewHolder {

        public TextView textDate;
        public TextView textRegister;
        public TextView textPicked;
        public TextView textBalance;


        public StockCardHolder(View itemView) {
            super(itemView);
            textDate = (TextView) itemView.findViewById(R.id.text_date);
            textRegister = (TextView) itemView.findViewById(R.id.text_register);
            textPicked = (TextView) itemView.findViewById(R.id.text_picked);
            textBalance = (TextView) itemView.findViewById(R.id.text_balance);
        }
    }

    private class StockCardAdapter extends RecycleViewAdapter<StockCardItem, StockCardHolder>
    {
        private List<StockCardItem> fullItems;

        public StockCardAdapter(List<StockCardItem> _stocks) {
            super(new LinkedList<StockCardItem>((_stocks != null && _stocks.size() > 0) ? _stocks : new LinkedList<StockCardItem>()), RecycleViewAdapter.USE_HEADER);
            fullItems = _stocks;
        }

        @Override
        public StockCardHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            try {
                LayoutInflater layoutInflater = LayoutInflater.from(getActivity());

                View view;

                if(viewType == HEADER) {
                    view = layoutInflater.inflate(R.layout.stock_card_header, parent, false);
                }
                else {
                    view = layoutInflater.inflate(R.layout.stock_card_row, parent, false);
                }
                return new StockCardHolder(view);
            }
            catch(Exception e) {
                Log.d(Util.APP_TAG, "Error: " + e.getMessage());
                e.printStackTrace();
                Toast.makeText(getActivity(), R.string.internal_server_error, Toast.LENGTH_SHORT).show();
                return null;
            }
        }

        @Override
        public void onBindViewHolder(final StockCardHolder holder, final int position) {
            try {
                if (position == 0 || products == null)
                    return;

//                if(position > 0) {

                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                final StockCardItem sci = this.products.get(position - 1);
                holder.textDate.setText(sdf.format(sci.getDate()));
                holder.textBalance.setText(String.valueOf(sci.getBalance()));

                if(position > 1) {
                    holder.textRegister.setText(String.valueOf(sci.getRegister()));
                    holder.textPicked.setText(String.valueOf(sci.getPicked()));
                }
//                if(ohs.getLastInputDate() != null)
//                    holder.textLastinputtedDate.setText(sdf.format(ohs.getLastInputDate()));
//
//                if (ohs.getProduct().getImg() == null)
//                    downloader.queueImage(holder, null);
//                else {
//                    Map<String, String> params = new HashMap<String, String>();
//                    params.put("sessionString", LoginService.sessionString);
//                    params.put("image", ohs.getProduct().getImg());
//                    downloader.queueImage(holder, Util.getImageURL(Util.PRODUCT_URL, params));
//                }
//                }
//                else if(position == 1) {
//                    ProductService.fillProductSpinner(holder.spinnerProduct, getActivity(), getFragmentManager());
//                    InventoryService.fillWarehouseSpinner(holder.spinnerWarehouse, getActivity(), getFragmentManager());
//                    holder.btnSearch.setOnClickListener(
//                            new View.OnClickListener()
//                            {
//                                @Override
//                                public void onClick(View v)
//                                {
//                                    Warehouse wr = null;
//                                    Product p = null;
//                                    if(holder.spinnerWarehouse.getSelectedItemPosition() > 0)
//                                        wr = (Warehouse ) holder.spinnerWarehouse.getSelectedItem();
//                                    if(holder.spinnerProduct.getSelectedItemPosition() > 0)
//                                        p = (Product ) holder.spinnerProduct.getSelectedItem();
//                                    List<OnHandStock> filteredStocks = new LinkedList<>();
//                                    if(fullItems != null) {
//                                        for (OnHandStock ohs : fullItems) {
//                                            if(p != null)
//                                                if(!ohs.getProduct().equals(p))
//                                                    continue;
//
//                                            if(wr != null )
//                                                if(!ohs.getWarehouse().equals(wr))
//                                                    continue;
//
//                                            filteredStocks.add(ohs);
//                                        }
//                                        setItems(filteredStocks);
//                                    }
//                                }
//                            }
//                    );
//                }
            }
            catch(Exception e) {
                Log.d(Util.APP_TAG, "Error: " + e.getMessage());
                e.printStackTrace();
                Toast.makeText(getActivity(), R.string.internal_server_error, Toast.LENGTH_SHORT).show();
            }
        }
    }


}
