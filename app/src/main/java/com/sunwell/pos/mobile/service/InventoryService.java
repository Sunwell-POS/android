package com.sunwell.pos.mobile.service;

import android.content.Context;
import android.support.v4.app.FragmentManager;
import android.util.Log;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.sunwell.pos_mobile.R;
import com.sunwell.pos.mobile.model.IncomingGood;
import com.sunwell.pos.mobile.model.OnHandStock;
import com.sunwell.pos.mobile.model.OutcomingGood;
import com.sunwell.pos.mobile.model.Product;
import com.sunwell.pos.mobile.model.StockMutationItem;
import com.sunwell.pos.mobile.model.User;
import com.sunwell.pos.mobile.model.Warehouse;
import com.sunwell.pos.mobile.util.ResultListener;
import com.sunwell.pos.mobile.util.ResultWatcher;
import com.sunwell.pos.mobile.util.Util;

import org.json.JSONArray;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * Created by sunwell on 10/17/17.
 */

public class InventoryService
{
    private static final String PROGRESS_FETCH_WAREHOUSE = "progressFetchWarehouse";
    public static List<OnHandStock> stocks;
    public static List<Warehouse> warehouses;
    public static List<IncomingGood> tempGoods;
    private static boolean goodCheckingError = false;
    private static ResultListener<Boolean> stockAvailableListener ;
//    public static List<ProdCategory> categories;

    static {
        LoginService.addLogoutListener(
                new ResultWatcher<User>()
                {
                    @Override
                    public void onResult(Object source, User result) throws Exception
                    {
                        clear();
                    }
                }
        );
    }

    private InventoryService() {

    }

    public static void clear() {
        stocks = null;
        warehouses = null;
        tempGoods = null;
        stockAvailableListener = null;

    }

    public static void fetchOnHandStocks(ResultListener<List<OnHandStock>> _listener) throws Exception {
        JSONObject json = new JSONObject();
        json.put("sessionString", LoginService.sessionString);
        GeneralTask<List<OnHandStock>> gt = new GeneralTask<>(_listener, Util.STOCK_URL, null);
        gt.setParser(
                new GeneralTask.ResponseParser<List<OnHandStock>>() {

                    @Override
                    public List<OnHandStock> parse(JSONObject response) throws Exception {
                        stocks = null ;
                        if (!response.has("listOnHandStock"))
                            return null;

                        JSONArray jArrStock = response.getJSONArray("listOnHandStock");
                        if (jArrStock == null || jArrStock.length() <= 0)
                            return null;

                        OnHandStock[] arrStocks = Util.parseJSONData(jArrStock.toString(), OnHandStock[].class);
                        if (arrStocks != null && arrStocks.length > 0) {
                            stocks = new LinkedList<>(Arrays.asList(arrStocks));
                        }
                        return stocks;
                    }
                }
        );
        gt.get(json);
    }

    public static void fetchStockMutation(ResultListener<List<StockMutationItem>> _listener, Map<String, String> _params) throws Exception {
        JSONObject json = new JSONObject();
        json.put("sessionString", LoginService.sessionString);
        if(_params.get("prodCategoryId") != null)
            json.put("prodCategoryId", _params.get("prodCategoryId"));

        json.put("warehouseId", _params.get("warehouseId"));
        json.put("startDate", _params.get("startDate"));
        json.put("endDate", _params.get("endDate"));

        GeneralTask<List<StockMutationItem>> gt = new GeneralTask<>(_listener, Util.STOCK_MUTATION_URL, null);
        gt.setParser(
                new GeneralTask.ResponseParser<List<StockMutationItem>>() {

                    @Override
                    public List<StockMutationItem> parse(JSONObject response) throws Exception {
                        stocks = null ;
                        if (!response.has("listStockMutation"))
                            return null;

                        JSONArray jArrStock = response.getJSONArray("listStockMutation");
                        if (jArrStock == null || jArrStock.length() <= 0)
                            return null;

                        StockMutationItem[] arrStocks = Util.parseJSONData(jArrStock.toString(), StockMutationItem[].class);
                        return Arrays.asList(arrStocks);
                    }
                }
        );
        gt.get(json);
    }

    public static void fetchOnHandStocks(ResultListener<List<OnHandStock>> _listener, Map<String, String> _params) throws Exception {
        JSONObject json = new JSONObject();
        json.put("sessionString", LoginService.sessionString);
        if(_params.get("productId") != null)
            json.put("productId", _params.get("productId"));
        if(_params.get("warehouseId") != null)
            json.put("warehouseId", _params.get("warehouseId"));
        GeneralTask<List<OnHandStock>> gt = new GeneralTask<>(_listener, Util.STOCK_URL, null);
        gt.setParser(
                new GeneralTask.ResponseParser<List<OnHandStock>>() {

                    @Override
                    public List<OnHandStock> parse(JSONObject response) throws Exception {
                        stocks = null ;
                        if (!response.has("listOnHandStock"))
                            return null;

                        JSONArray jArrStock = response.getJSONArray("listOnHandStock");
                        if (jArrStock == null || jArrStock.length() <= 0)
                            return null;

                        OnHandStock[] arrStocks = Util.parseJSONData(jArrStock.toString(), OnHandStock[].class);
                        return Arrays.asList(arrStocks);
                    }
                }
        );
        gt.get(json);
    }

    public static void fetchIncomingGoods(ResultListener<List<IncomingGood>> _listener, Map<String, String> _params) throws Exception {
        JSONObject json = new JSONObject();
        json.put("sessionString", LoginService.sessionString);
        json.put("productId", _params.get("productId"));
        json.put("warehouseId", _params.get("warehouseId"));
        json.put("startDate", _params.get("startDate"));
        json.put("endDate", _params.get("endDate"));
        json.put("withPrevSummary", true);

        GeneralTask<List<IncomingGood>> gt = new GeneralTask<>(_listener, Util.INCOMING_GOOD_URL, null);
        gt.setParser(
                new GeneralTask.ResponseParser<List<IncomingGood>>() {

                    @Override
                    public List<IncomingGood> parse(JSONObject response) throws Exception {
                        if (!response.has("listIncomingGood"))
                            return null;

                        JSONArray jArrStock = response.getJSONArray("listIncomingGood");
                        if (jArrStock == null || jArrStock.length() <= 0)
                            return null;

                        IncomingGood[] arrIncomingGood = Util.parseJSONData(jArrStock.toString(), IncomingGood[].class);
                        return Arrays.asList(arrIncomingGood);
                    }
                }
        );
        gt.get(json);
    }

    public static void fetchOutcomingGoods(ResultListener<List<OutcomingGood>> _listener, Map<String, String> _params) throws Exception {
        JSONObject json = new JSONObject();
        json.put("sessionString", LoginService.sessionString);
        json.put("productId", _params.get("productId"));
        json.put("warehouseId", _params.get("warehouseId"));
        json.put("startDate", _params.get("startDate"));
        json.put("endDate", _params.get("endDate"));
        json.put("withPrevSummary", true);

        GeneralTask<List<OutcomingGood>> gt = new GeneralTask<>(_listener, Util.OUTCOMING_GOOD_URL, null);
        gt.setParser(
                new GeneralTask.ResponseParser<List<OutcomingGood>>() {

                    @Override
                    public List<OutcomingGood> parse(JSONObject response) throws Exception {
                        if (!response.has("listOutcomingGood"))
                            return null;

                        JSONArray jArrStock = response.getJSONArray("listOutcomingGood");
                        if (jArrStock == null || jArrStock.length() <= 0)
                            return null;

                        OutcomingGood[] outcomingGood = Util.parseJSONData(jArrStock.toString(), OutcomingGood[].class);
                        return Arrays.asList(outcomingGood);
                    }
                }
        );
        gt.get(json);
    }


    public static void fetchWarehouses(ResultListener<List<Warehouse>> _listener) throws Exception {
        JSONObject json = new JSONObject();
        json.put("sessionString", LoginService.sessionString);
        GeneralTask<List<Warehouse>> gt = new GeneralTask<>(_listener, Util.WAREHOUSE_URL, null);
        gt.setParser(
                new GeneralTask.ResponseParser<List<Warehouse>>() {

                    @Override
                    public List<Warehouse> parse(JSONObject response) throws Exception {
                        warehouses = null ;
                        if (!response.has("listWarehouse"))
                            return null;

                        JSONArray jArrWarehouses = response.getJSONArray("listWarehouse");
                        if (jArrWarehouses == null || jArrWarehouses.length() <= 0)
                            return null;

                        Warehouse[] arrWarehouses = Util.parseJSONData(jArrWarehouses.toString(), Warehouse[].class);
                        if (arrWarehouses != null && arrWarehouses.length > 0) {
                            warehouses = new LinkedList<>(Arrays.asList(arrWarehouses));
                        }
                        return warehouses;
                    }
                }
        );
        gt.get(json);
    }

    public static void addIncomingGoods(final ResultListener<List<IncomingGood>> _listener, List<IncomingGood> _listIC) throws Exception {

        Log.d(Util.APP_TAG, " ADD INCOMINg GOOD");
        String warehouseId = _listIC.get(0).getWarehouse().getSystemId();
        Date incomingDate = _listIC.get(0).getIncomingDate();
        String memo = _listIC.get(0).getMemo();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

        goodCheckingError = false;

        Warehouse warehouse = new Warehouse();
        warehouse.setSystemId(warehouseId);

        for(IncomingGood ic : _listIC) {
            ic.setProduct( new Product(ic.getProduct().getSystemId()));
            ic.setWarehouse(null);
            ic.setIncomingDate(null);
            ic.setMemo(null);
        }
         _listIC.toArray(new IncomingGood[]{});

        JSONArray jArray = Util.toJSONArray(_listIC.toArray(new IncomingGood[]{}), IncomingGood[].class);
        JSONObject registerItem = new JSONObject();
        JSONObject warehouseObj = Util.toJSONObject(warehouse, Warehouse.class);

        registerItem.put("sessionString", LoginService.sessionString);
        registerItem.put("warehouse", warehouseObj);
        registerItem.put("incomingDate", sdf.format(incomingDate));
        registerItem.put("memo", memo);
        registerItem.put("listIncomingGood", jArray);

        GeneralTask<List<IncomingGood>> gt = new GeneralTask<>(
                new ResultWatcher<List<IncomingGood>>()
                {
                    @Override
                    public void onResult(final Object source, final List<IncomingGood> result) throws Exception
                    {
                        if(tempGoods.size() <= 0 ) {
                            if(_listener != null)
                                _listener.onResult(source, result);

                            return;
                        }

                        stockAvailableListener = new ResultWatcher<Boolean>()
                        {
                            @Override
                            public void onResult(Object src, Boolean available) throws Exception
                            {
                                if(_listener != null)
                                    _listener.onResult(source, result);
                            }

                            @Override
                            public void onError(Object src, int ec) throws Exception
                            {
                                if(_listener != null)
                                    _listener.onError(source, ec);
                            }
                        };
                    }

                    @Override
                    public void onError(Object source, int errCode) throws Exception
                    {
                        if(_listener != null)
                            _listener.onError(source, errCode);
                    }

        }, Util.INCOMING_GOOD_URL, null);

        gt.setParser(
                new GeneralTask.ResponseParser<List<IncomingGood>>() {

                    @Override
                    public List<IncomingGood> parse(JSONObject response) throws Exception {
                        Log.d(Util.APP_TAG, " PARSE CALLED");
                        if (!response.has("listIncomingGood")) {
                            Log.d(Util.APP_TAG, "No LIST INCOMING GOOD");
                            return null;
                        }

                        JSONArray jArrIncomingGood = response.getJSONArray("listIncomingGood");
                        if (jArrIncomingGood == null || jArrIncomingGood.length() <= 0) {
                            Log.d(Util.APP_TAG, "No ARRAY INCOMING GOOD");
                            return null;
                        }

                        IncomingGood[] arrIncomingGood = Util.parseJSONData(jArrIncomingGood.toString(), IncomingGood[].class);
                        List<IncomingGood> listGoods = Arrays.asList(arrIncomingGood);
                        tempGoods = new LinkedList<IncomingGood>(listGoods);
                        Log.d(Util.APP_TAG, "SIZE: " + listGoods.size());
                        if (arrIncomingGood != null && arrIncomingGood.length > 0) {
                            for(IncomingGood ic : arrIncomingGood) {
                                if(stocks != null) {
                                    OnHandStock ohs = null;
                                    for(OnHandStock o : stocks) {
                                        if(o.getProduct().equals(ic.getProduct()) && o.getWarehouse().equals(ic.getWarehouse())) {
                                            o.setQty(o.getQty() + ic.getQty());
                                            ohs = o;
                                            tempGoods.remove(ic);
                                            break;
                                        }
                                    }
                                    if(ohs == null) {
                                        Map<String, String> params = new HashMap<String, String>();
                                        params.put("productId", ic.getProduct().getSystemId());
                                        params.put("warehouseId", ic.getWarehouse().getSystemId());

                                        fetchOnHandStocks(
                                                new ResultWatcher<List<OnHandStock>>()
                                                {
                                                    @Override
                                                    public void onResult(Object source, List<OnHandStock> result) throws Exception
                                                    {
                                                        if(result == null || result.size() <= 0)
                                                            checkIncomingGood(null);

                                                        stocks.add(result.get(0));
                                                        checkIncomingGood(result.get(0));
                                                    }

                                                    @Override
                                                    public void onError(Object source, int errCode) throws Exception {
                                                        checkIncomingGood(null);
                                                    }

                                                }, params
                                        );
//                                        OnHandStock onhand = new OnHandStock();
//                                        onhand.setProduct(ic.getProduct());
//                                        onhand.setWarehouse(ic.getWarehouse());
//                                        onhand.setQty(ic.getQty());
//                                        onhand.setLastInputDate(onhand.getLastInputDate().before(ic.getOutcomingDate()) ? ic.getOutcomingDate() : onhand.getLastInputDate());
//                                        stocks.add(onhand);
                                    }
                                }
//                                else {
//                                    Map<String, String> params = new HashMap<String, String>();
//                                    params.put("productId", ic.getProduct().getSystemId());
//                                    params.put("warehouseId", ic.getWarehouse().getSystemId());
//
//                                    stocks = new LinkedList<OnHandStock>();
//
//                                    fetchOnHandStocks(
//                                            new ResultWatcher<List<OnHandStock>>()
//                                            {
//                                                @Override
//                                                public void onResult(Object source, List<OnHandStock> result) throws Exception
//                                                {
//                                                    if(result == null || result.size() <= 0)
//                                                        checkIncomingGood(null);
//
//                                                    stocks.add(result.get(0));
//                                                    checkIncomingGood(result.get(0));
//                                                }
//
//                                                @Override
//                                                public void onError(Object source, int errCode) throws Exception {
//                                                    checkIncomingGood(null);
//                                                }
//
//                                            }, params
//                                    );
////                                    OnHandStock onhand = new OnHandStock();
////                                    onhand.setProduct(ic.getProduct());
////                                    onhand.setWarehouse(ic.getWarehouse());
////                                    onhand.setQty(ic.getQty());
////                                    onhand.setLastInputDate(onhand.getLastInputDate().before(ic.getOutcomingDate()) ? ic.getOutcomingDate() : onhand.getLastInputDate());
////                                    stocks.add(onhand);
//                                }
                            }
                        }

                        return listGoods;
                    }
                }
        );
        gt.post(registerItem);
    }

    public static void addOnHandStocks(final ResultListener<List<OnHandStock>> _listener, List<OnHandStock> _listOH) throws Exception {
        JSONArray jArray = Util.toJSONArray(_listOH, OnHandStock[].class);
        JSONObject jsonObj = new JSONObject();
        jsonObj.put("sessionString", LoginService.sessionString);
        jsonObj.put("listOnHandStock", jArray);
        GeneralTask<List<OnHandStock>> gt = new GeneralTask<>(_listener, Util.STOCK_URL, null);
        gt.setParser(
                new GeneralTask.ResponseParser<List<OnHandStock>>() {

                    @Override
                    public List<OnHandStock> parse(JSONObject response) throws Exception {
                        stocks = null ;
                        if (!response.has("listOnHandStock"))
                            return null;

                        JSONArray jArrStock = response.getJSONArray("listOnHandStock");
                        if (jArrStock == null || jArrStock.length() <= 0)
                            return null;

                        OnHandStock[] arrOnHandStocks = Util.parseJSONData(jArrStock.toString(), OnHandStock[].class);
                        List<OnHandStock> listOnHandStocks = Arrays.asList(arrOnHandStocks);
                        if (arrOnHandStocks != null && arrOnHandStocks.length > 0) {
                            for(OnHandStock ohs : arrOnHandStocks) {
                                if (stocks.contains(ohs)) {
                                    int index = stocks.indexOf(ohs);
                                    stocks.set(index, ohs);
                                } else {
                                    stocks.add(ohs);
                                }
                            }
                        }

                        return listOnHandStocks;
                    }
                }
        );
        new GeneralTask<>(_listener, Util.STOCK_URL, null).post(jsonObj);
    }


    public static void addOnHandStock(final ResultListener<OnHandStock> _listener, OnHandStock _ohs) throws Exception {
        JSONObject jsonObj = Util.toJSONObject(_ohs, OnHandStock.class);
        jsonObj.put("sessionString", LoginService.sessionString);
        ResultWatcher<OnHandStock> listener = new ResultWatcher<OnHandStock>() {
            @Override
            public void onResult(Object source, OnHandStock result) throws Exception {
                if(stocks != null) {
                    if(stocks.contains(result)) {
                        int index = stocks.indexOf(result);
                        stocks.set(index, result);
                    }
                    else {
                        stocks.add(result);
                    }
                }

                if(_listener != null)
                    _listener.onResult(null, result);
            }

            @Override
            public void onError(Object source, int _errCode) throws Exception {
                if(_listener != null)
                    _listener.onError(source, _errCode);
            }
        };
        new GeneralTask<>(listener, Util.STOCK_URL, OnHandStock.class).post(jsonObj);
    }

    public static void fillWarehouseSpinner(final Spinner _spinner, final Context _ctx, final FragmentManager _fm) throws Exception {
        final List<Warehouse> list = new ArrayList<>();
        list.add(new Warehouse());
        if (warehouses != null) {
            for (Warehouse wrh : warehouses) {
                list.add(wrh);
            }
            Util.fillSpinner(_spinner, list, Warehouse.class, _ctx);
        } else {
            Util.showDialog(_fm, PROGRESS_FETCH_WAREHOUSE);
            fetchWarehouses(
                    new ResultWatcher<List<Warehouse>>()
                    {
                        @Override
                        public void onResult(Object source, List<Warehouse> result)
                        {
                            Util.stopDialog(PROGRESS_FETCH_WAREHOUSE);
                            if (result != null) {
                                for (Warehouse wrh : result) {
                                    list.add(wrh);
                                }
                                Util.fillSpinner(_spinner, list, Warehouse.class, _ctx);
                            }
                        }

                        @Override
                        public void onError(Object source, int errCode) throws Exception {
                            Util.stopDialog(PROGRESS_FETCH_WAREHOUSE);
                            Toast.makeText(_ctx, R.string.internal_server_error, Toast.LENGTH_SHORT).show();
                        }
                    }
            );
        }
    }

    public static OnHandStock findOnHandStockByProductAndWarehouse(Product _p, Warehouse _wr) {
        if(stocks == null)
            return null;

        for(OnHandStock ohs : stocks) {
            if(ohs.getProduct().equals(_p) && ohs.getWarehouse().equals(_wr))
                return ohs;
        }

        return null;
    }

    private synchronized static void checkIncomingGood(OnHandStock _ohs) throws Exception {

        if(goodCheckingError) {
            Log.d(Util.APP_TAG, "ERROR In CHECKING");
            return;
        }

        if(_ohs == null) {
            Log.d(Util.APP_TAG, "OHS In NULL");
            goodCheckingError = true;
            stockAvailableListener.onError(null, -3);
            return ;
        }

//        boolean res = tempGoods.remove(new IncomingGood(_ohs.getProduct(), _ohs.getWarehouse()));
        IncomingGood good = null;
        for(IncomingGood ic : tempGoods) {
            if(ic.getProduct().equals(_ohs.getProduct()) && ic.getWarehouse().equals(_ohs.getWarehouse()))
            good = ic;
        }
        tempGoods.remove(good);
        Log.d(Util.APP_TAG, "TEMP GODDS SIZE: " + tempGoods.size());

        if(tempGoods.size() <= 0)
            stockAvailableListener.onResult(null, true);
    }

//    private void getStockFromGood(IncomingGood _good,  Map<String, String> _params, int _count ) throws Exception {
//        fetchOnHandStocks(
//                new ResultWatcher<List<OnHandStock>>()
//                {
//                    @Override
//                    public void onResult(Object source, List<OnHandStock> result) throws Exception
//                    {
//                        if(result == null || result.size() <= 0)
//                            throw new Exception("ERROR, CAN'T GET ON HAND STOCK DATA");
//
//                        stocks.add(result.get(0));
//
//                    }
//
//                    @Override
//                    public void onError(Object source, int errCode) throws Exception {
//                        throw new Exception("ERROr WHEN RETRIEVINg ON HAND STOCK DATA");
//                    }
//
//
//
//                }, _params );
//        );
//    }

//    public static void fillWarehouseSpinner(final Spinner _spinner, final Context _ctx, final FragmentManager _fm) throws Exception {
//        final List<Warehouse> list = new ArrayList<>();
//        list.add(new Warehouse());
//        if (warehouses != null) {
//            for (Warehouse wrh : warehouses) {
//                list.add(wrh);
//            }
//            Util.fillSpinner(_spinner, list, Warehouse.class, _ctx);
//        } else {
//            Util.showDialog(_fm, PROGRESS_FETCH_WAREHOUSE);
//            fetchWarehouses(
//                    new ResultWatcher<List<Warehouse>>()
//                    {
//                        @Override
//                        public void onResult(Object source, List<Warehouse> result)
//                        {
//                            Util.stopDialog(PROGRESS_FETCH_WAREHOUSE);
//                            if (result != null) {
//                                for (Warehouse wrh : result) {
//                                    list.add(wrh);
//                                }
//                                Util.fillSpinner(_spinner, list, Warehouse.class, _ctx);
//                            }
//                        }
//
//                        @Override
//                        public void onError(Object source, int errCode) throws Exception {
//                            Util.stopDialog(PROGRESS_FETCH_WAREHOUSE);
//                            Toast.makeText(_ctx, R.string.internal_server_error, Toast.LENGTH_SHORT).show();
//                        }
//                    }
//            );
//        }
//    }
}

