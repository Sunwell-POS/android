package com.sunwell.pos.mobile.service;

import android.content.Context;
import android.os.AsyncTask;
import android.support.v4.app.FragmentManager;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.sunwell.pos_mobile.R;
import com.sunwell.pos.mobile.model.ProdCategory;
import com.sunwell.pos.mobile.model.Product;
import com.sunwell.pos.mobile.model.User;
import com.sunwell.pos.mobile.util.ResultListener;
import com.sunwell.pos.mobile.util.ResultWatcher;
import com.sunwell.pos.mobile.util.Util;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * Created by sunwell on 10/17/17.
 */

public class ProductService {

    private static final String PROGRESS_FETCH_PRODUCT = "progressFetchProduct";
    private static final String PROGRESS_FETCH_CATEGORY = "progressFetchCategory";
    public static List<Product> products;
    public static List<ProdCategory> categories;

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

    private ProductService() {

    }

    public static void clear() {
        products = null;
        categories = null;
    }

    public static void fetchProducts(ResultListener<List<Product>> _listener) throws Exception {
        JSONObject json = new JSONObject();
        json.put("sessionString", LoginService.sessionString);
        GeneralTask<List<Product>> gt = new GeneralTask<>(_listener, Util.PRODUCT_URL, null);
        gt.setParser(
                new GeneralTask.ResponseParser<List<Product>>() {

                    @Override
                    public List<Product> parse(JSONObject response) throws Exception {
                        products = null ;
                        if (!response.has("listProduct"))
                            return null;

                        JSONArray jArrProduct = response.getJSONArray("listProduct");
                        if (jArrProduct == null || jArrProduct.length() <= 0)
                            return null;

                        Product[] arrProduct = Util.parseJSONData(jArrProduct.toString(), Product[].class);
                        if (arrProduct != null && arrProduct.length > 0) {
                            products = new LinkedList<>(Arrays.asList(arrProduct));
                        }
                        Log.d(Util.APP_TAG, "PROD SIZE: " + products.size());
                        return products;
                    }
                }
        );
        gt.get(json);
    }

    public static void fetchCategories(final ResultWatcher<List<ProdCategory>> _listener) throws Exception {

//        if(1 == 1)
//            throw new Exception("TEST CTGR");

        JSONObject json = new JSONObject();
        json.put("sessionString", LoginService.sessionString);
        GeneralTask<List<ProdCategory>> gt = new GeneralTask<>(_listener, Util.CATEGORY_URL, null);
        gt.setParser(
                new GeneralTask.ResponseParser<List<ProdCategory>>() {

                    @Override
                    public List<ProdCategory> parse(JSONObject response) throws Exception {
                        categories = null ;
                        if (!response.has("listCategory"))
                            return null;

                        JSONArray jArrCategory = response.getJSONArray("listCategory");
                        if (jArrCategory == null || jArrCategory.length() <= 0)
                            return null;

                        ProdCategory[] arrCategories = Util.parseJSONData(jArrCategory.toString(), ProdCategory[].class);
                        if (arrCategories != null && arrCategories.length > 0) {
                            categories = new LinkedList<>(Arrays.asList(arrCategories));
                        }
                        Log.d(Util.APP_TAG, "CTGR SIZE: " + categories.size());
                        return categories;
                    }
                }
        );
        gt.get(json);
    }

    public static void addProduct(final ResultWatcher<Product> _listener, Product _prod) throws Exception {
        List<ProdCategory> categories = _prod.getCategories();
        String strCtgr = "";
        if(categories != null && categories.size() > 0) {
            for (ProdCategory ctgr : categories) {
                strCtgr += ctgr.getSystemId() + ";";
            }
            strCtgr = strCtgr.substring(0, strCtgr.length() - 1);
        }
        else
            strCtgr = null;

        JSONObject jsonObject = Util.toJSONObject(_prod, Product.class);
        jsonObject.put("sessionString", LoginService.sessionString);
        jsonObject.remove("categories");
        if(strCtgr != null)
            jsonObject.put("categoriesString", strCtgr);
        ResultWatcher<Product> listener = new ResultWatcher<Product>() {
            @Override
            public void onResult(Object source, Product result) throws Exception {
                if(products != null)
                    products.add(result);

                if(_listener != null)
                    _listener.onResult(null, result);
            }

            @Override
            public void onError(Object source, int _errCode) throws Exception {
                if(_listener != null)
                    _listener.onError(null, _errCode);
            }
        };
        new GeneralTask<>(listener, Util.PRODUCT_URL, Product.class).post(jsonObject);
    }

    public static void addCategory(final ResultListener<ProdCategory> _listener, ProdCategory _ctgr) throws Exception {
        JSONObject jsonObj = Util.toJSONObject(_ctgr, ProdCategory.class);
        jsonObj.put("sessionString", LoginService.sessionString);
        ResultWatcher<ProdCategory> listener = new ResultWatcher<ProdCategory>() {
            @Override
            public void onResult(Object source, ProdCategory result) throws Exception {
                if(categories != null) {
                    Log.d(Util.APP_TAG, "Adding now...");
                    categories.add(result);
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
        new GeneralTask<>(listener, Util.CATEGORY_URL, ProdCategory.class).post(jsonObj);
    }

    public static void editProduct(final ResultWatcher<Product> _listener, final Product _prod) throws Exception {
        List<ProdCategory> categories = _prod.getCategories();
        String strCtgr = "";
        if(categories != null && categories.size() > 0) {
            for (ProdCategory ctgr : categories) {
                strCtgr += ctgr.getSystemId() + ";";
            }
            strCtgr = strCtgr.substring(0, strCtgr.length() - 1);
        }
        else
            strCtgr = null;

        JSONObject jsonObject = Util.toJSONObject(_prod, Product.class);
        jsonObject.put("sessionString", LoginService.sessionString);
        jsonObject.remove("categories");
        if(strCtgr != null)
            jsonObject.put("categoriesString", strCtgr);

        ResultWatcher<Product> listener = new ResultWatcher<Product>() {
            @Override
            public void onResult(Object source, Product result) throws Exception {
                if(products != null) {
                    for (int i = 0 ; i < products.size() ; i++) {
                        Product p = products.get(i);
                        if (p.getSystemId().equals(_prod.getSystemId())) {
                            products.set(i, result);
                            break;
                        }
                    }
                }

                if(_listener != null)
                    _listener.onResult(null, result);
            }

            @Override
            public void onError(Object source, int _errCode) throws Exception {
                if(_listener != null)
                    _listener.onError(null, _errCode);
            }
        };
        new GeneralTask<>(listener, Util.PRODUCT_URL, Product.class).put(jsonObject);
    }

    public static void editCategory(final ResultListener<ProdCategory> _listener, final ProdCategory _ctgr) throws Exception {
        JSONObject jsonObject = Util.toJSONObject(_ctgr, ProdCategory.class);
        jsonObject.put("sessionString", LoginService.sessionString);
        ResultWatcher<ProdCategory> listener = new ResultWatcher<ProdCategory>() {
            @Override
            public void onResult(Object source, ProdCategory result) throws Exception  {
                if(categories != null) {
                    for (int i = 0 ; i < categories.size() ; i++) {
                        ProdCategory c = categories.get(i);
                        if (c.getSystemId().equals(_ctgr.getSystemId())) {
                            categories.set(i, result);
                            break;
                        }
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
        new GeneralTask<>(listener, Util.CATEGORY_URL, ProdCategory.class).put(jsonObject);
    }

    public static void deleteProduct(final ResultWatcher<Boolean> _listener, final String _id) throws Exception {
        JSONObject param = new JSONObject();
        param.put("sessionString", LoginService.sessionString);
        param.put("systemId", _id);
        ResultWatcher<Boolean> listener = new ResultWatcher<Boolean>() {
            @Override
            public void onResult(Object source, Boolean result) throws Exception {
                if(products != null) {
                    Product p = null;
                    for (int i = 0 ; i < products.size() ; i++) {
                        if (products.get(i).getSystemId().equals(_id)) {
                            p = products.get(i);
                            break;
                        }
                    }
                    products.remove(p);
                }

                if(_listener != null)
                    _listener.onResult(source, result);
            }

            @Override
            public void onError(Object source, int _errCode) throws Exception{
                if(_listener != null)
                    _listener.onError(source, _errCode);
            }
        };
        new GeneralTask<>(listener, Util.PRODUCT_URL, null).delete(param);
    }

    public static void deleteCategory(final ResultListener<Boolean> _listener, final String _id) throws Exception{
        JSONObject param = new JSONObject();
        param.put("sessionString", LoginService.sessionString);
        param.put("systemId", _id);
        ResultWatcher<Boolean> listener = new ResultWatcher<Boolean>() {
            @Override
            public void onResult(Object source, Boolean result) throws Exception {
                if(categories != null) {
                    ProdCategory c = null;
                    for (int i = 0 ; i < categories.size() ; i++) {
                        if (categories.get(i).getSystemId().equals(_id)) {
                            c = categories.get(i);
                            break;
                        }
                    }
                    categories.remove(c);
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
        new GeneralTask<>(listener, Util.CATEGORY_URL, null).delete(param);
    }

    public static void fillProductSpinner(final Spinner _spinner, final Context _ctx, final FragmentManager _fm) throws Exception {
        final List<Product> list = new ArrayList<>();
        list.add(new Product());
        if (ProductService.products != null) {
            for (Product prod : ProductService.products) {
                list.add(prod);
            }
            Util.fillSpinner(_spinner, list, Product.class, _ctx);
        } else {
            Util.showDialog(_fm, PROGRESS_FETCH_PRODUCT);
            ProductService.fetchProducts(
                    new ResultWatcher<List<Product>>()
                    {
                        @Override
                        public void onResult(Object source, List<Product> result)
                        {
                            Util.stopDialog(PROGRESS_FETCH_PRODUCT);
                            if (result != null) {
                                for (Product prod : result) {
                                    list.add(prod);
                                }
                                Util.fillSpinner(_spinner, list, Product.class, _ctx);
                            }
                        }

                        @Override
                        public void onError(Object source, int errCode) throws Exception {
                            Util.stopDialog(PROGRESS_FETCH_PRODUCT);
                            Toast.makeText(_ctx, R.string.internal_server_error, Toast.LENGTH_SHORT).show();
                        }
                    }
            );
        }
    }

    public static void fillCategorySpinner(final Spinner _spinner, final Context _ctx, final FragmentManager _fm) throws Exception {
        final List<ProdCategory> list = new ArrayList<>();
        list.add(new ProdCategory());
        if (ProductService.categories != null) {
            for (ProdCategory ctgr : ProductService.categories) {
                list.add(ctgr);
            }
            Util.fillSpinner(_spinner, list, ProdCategory.class, _ctx);
        } else {
            Util.showDialog(_fm, PROGRESS_FETCH_CATEGORY);
            ProductService.fetchCategories(
                    new ResultWatcher<List<ProdCategory>>()
                    {
                        @Override
                        public void onResult(Object source, List<ProdCategory> result)
                        {
                            Util.stopDialog(PROGRESS_FETCH_CATEGORY);
                            if (result != null) {
                                for (ProdCategory ctgr : result) {
                                    list.add(ctgr);
                                }
                                Util.fillSpinner(_spinner, list, ProdCategory.class, _ctx);
                            }
                        }

                        @Override
                        public void onError(Object source, int errCode) throws Exception {
                            Util.stopDialog(PROGRESS_FETCH_CATEGORY);
                            Toast.makeText(_ctx, R.string.internal_server_error, Toast.LENGTH_SHORT).show();
                        }
                    }
            );
        }
    }

    public static void fillProductAutoComplete(final AutoCompleteTextView _input, final Context _ctx, final FragmentManager _fm) throws Exception {
//        Log.e(Util.APP_TAG, "FILL ATC CALLED");
        if(ProductService.products != null) {
            ArrayAdapter<Product> adapter = new ArrayAdapter<Product>(_ctx,
                    android.R.layout.simple_dropdown_item_1line, ProductService.products);
            _input.setAdapter(adapter);
//            Log.e(Util.APP_TAG, "PROD SIZE: " + products.size());
        }
        else {
            ProductService.fetchProducts(
                    new ResultWatcher<List<Product>>()
                    {
                        @Override
                        public void onResult(Object source, List<Product> result) throws Exception
                        {
                            Util.stopDialog(PROGRESS_FETCH_PRODUCT);

                            if(result == null || result.size() <= 0)
                                return;

                            ArrayAdapter<Product> adapter = new ArrayAdapter<Product>(_ctx,
                                    android.R.layout.simple_dropdown_item_1line, result);
                            _input.setAdapter(adapter);
                        }

                        @Override
                        public void onError(Object source, int errCode) throws Exception
                        {
                            Util.stopDialog(PROGRESS_FETCH_PRODUCT);
                            Toast.makeText(_ctx, R.string.internal_server_error, Toast.LENGTH_SHORT).show();
                        }
                    }
            );

            Util.showDialog(_fm, PROGRESS_FETCH_PRODUCT);
        }
    };

    public static void fillCategoryAutoComplete(final AutoCompleteTextView _input, final Context _ctx, final FragmentManager _fm) throws Exception {
        if(ProductService.categories != null) {
            ArrayAdapter<ProdCategory> adapter = new ArrayAdapter<ProdCategory>(_ctx,
                    android.R.layout.simple_dropdown_item_1line, ProductService.categories);
            _input.setAdapter(adapter);
        }
        else {
            ProductService.fetchCategories(
                    new ResultWatcher<List<ProdCategory>>()
                    {
                        @Override
                        public void onResult(Object source, List<ProdCategory> result) throws Exception
                        {
                            Util.stopDialog(PROGRESS_FETCH_CATEGORY);

                            if(result == null || result.size() <= 0)
                                return;

                            ArrayAdapter<ProdCategory> adapter = new ArrayAdapter<ProdCategory>(_ctx,
                                    android.R.layout.simple_dropdown_item_1line, result);
                            _input.setAdapter(adapter);
                        }

                        @Override
                        public void onError(Object source, int errCode) throws Exception
                        {
                            Util.stopDialog(PROGRESS_FETCH_CATEGORY);
                            Toast.makeText(_ctx, R.string.internal_server_error, Toast.LENGTH_SHORT).show();
                        }
                    }
            );

            Util.showDialog(_fm, PROGRESS_FETCH_CATEGORY);
        }
    };

    public static Product findProductById(String _id) {
        for(Product p : products) {
            if(p.getSystemId().equals(_id))
                return p;
        }

        return null;
    }

    public static Product findProductByName(String _name) {
        for(Product p : products) {
            if(p.getName().equals(_name))
                return p;
        }

        return null;
    }





//    public interface ProductListListener extends ResultWatcher<List<Product>> {
//    }
//
//    public interface CategoryListListener extends ResultWatcher<List<ProdCategory>> {
//    }
//
//    public interface CategoryListener extends ResultWatcher<ProdCategory> {
//    }
//
//    public interface DeleteCategoryListener extends ResultWatcher<Boolean> {
//    }

//    -------------------------------------------------------------- old code ----------------------------------------------------------------

//    private static class FetchProductTask extends AsyncTask<String, Void, List<Product>> {
//        private ResultListener<List<Product>> listener;
//        private int errCode = -1;
//
//        public FetchProductTask(ResultListener<List<Product>> _listener) {
//            listener = _listener;
//        }
//
//        @Override
//        protected List<Product> doInBackground(String... params) {
//            try {
//                String sessionString = LoginService.sessionString;
//                Map<String, String> p = new HashMap<>();
//                p.put("sessionString", sessionString);
//                String responseString = Util.getRequest(Util.PRODUCT_URL, p);
//                Log.d(Util.APP_TAG, "Response: " + new JSONObject(responseString).toString(4));
//                JSONObject json = new JSONObject(responseString);
//                if (json.has("errorCode")) {
//                    errCode = json.getInt("errorCode");
//                    return null;
//                }
//
//                if(!json.has("listProduct"))
//                    return null;
//
//                JSONArray jArrCategory = json.getJSONArray("listProduct");
//                if (jArrCategory == null || jArrCategory.length() <= 0)
//                    return null;
//
//                Product[] arrProd = Util.parseJSONData(jArrCategory.toString(), Product[].class);
//                if (arrProd != null && arrProd.length > 0) {
//                    products = new LinkedList<>(Arrays.asList(arrProd));
//                }
//
//                return products;
//            } catch (Exception e) {
//                Log.d(Util.APP_TAG, "Exception: " + e.getMessage());
//                e.printStackTrace();
//                errCode = -2;
//                return null;
//
//            }
//        }
//
//        @Override
//        protected void onPostExecute(List<Product> _products) {
//            if (listener != null) {
//                if (errCode != -1)
//                    listener.onError(errCode);
//                else
//                    listener.onResult(_products);
//            }
//        }
//    }
//
//
//    private static class FetchCategoryTask extends AsyncTask<String, Void, List<ProdCategory>> {
//        private ResultListener<List<ProdCategory>> listener;
//        private int errCode = -1;
//
//        public FetchCategoryTask(ResultListener<List<ProdCategory>> _listener) {
//            listener = _listener;
//        }
//
//        @Override
//        protected List<ProdCategory> doInBackground(String... params) {
//            try {
//                String sessionString = LoginService.sessionString;
//                Map<String, String> p = new HashMap<>();
//                p.put("sessionString", sessionString);
//                String responseString = Util.getRequest(Util.CATEGORY_URL, p);
//                Log.d(Util.APP_TAG, "Response: " + new JSONObject(responseString).toString(4));
//                JSONObject json = new JSONObject(responseString);
//                if (json.has("errorCode")) {
//                    errCode = json.getInt("errorCode");
//                    return null;
//                }
//
//                if(!json.has("listCategory"))
//                    return null;
//
//                JSONArray jArrCategory = json.getJSONArray("listCategory");
//                if (jArrCategory == null || jArrCategory.length() <= 0)
//                    return null;
//
//                ProdCategory[] arrCategories = Util.parseJSONData(jArrCategory.toString(), ProdCategory[].class);
//                if (arrCategories != null && arrCategories.length > 0) {
//                    categories = new LinkedList<>(Arrays.asList(arrCategories));
//                }
//
//                return categories;
//            } catch (Exception e) {
//                Log.d(Util.APP_TAG, "Exception: " + e.getMessage());
//                e.printStackTrace();
//                errCode = -2;
//                return null;
//
//            }
//        }
//
//        @Override
//        protected void onPostExecute(List<ProdCategory> _categories) {
//            if (listener != null) {
//                if (errCode != -1)
//                    listener.onError(errCode);
//                else
//                    listener.onResult(_categories);
//            }
//        }
//    }
//
//    private static class DeleteCategoryTask extends AsyncTask<String, Void, Boolean> {
//        private ResultListener<Boolean> listener;
//        private int errCode = -1;
//
//        public DeleteCategoryTask(ResultListener<Boolean> _listener) {
//            listener = _listener;
//        }
//
//        @Override
//        protected Boolean doInBackground(String... params) {
//            try {
//                String systemId = params[0];
//                String sessionString = LoginService.sessionString;
//                Map<String, String> p = new HashMap<>();
//                p.put("sessionString", sessionString);
//                p.put("systemId", systemId);
//                String responseString = Util.deleteRequest(Util.CATEGORY_URL, p);
//                Log.d(Util.APP_TAG, "Response: " + new JSONObject(responseString).toString(4));
//                JSONObject json = new JSONObject(responseString);
//                if (json.has("errorCode")) {
//                    errCode = json.getInt("errorCode");
//                    return false;
//                }
//
//                ProdCategory ctgr = null;
//                for (ProdCategory c : categories) {
//                    if (c.getSystemId().equals(systemId)) {
//                        ctgr = c;
//                        break;
//                    }
//                }
//                categories.remove(ctgr);
//
//                return true;
//            } catch (Exception e) {
//                Log.d(Util.APP_TAG, "Exception: " + e.getMessage());
//                e.printStackTrace();
//                errCode = -2;
//                return false;
//            }
//        }
//
//        @Override
//        protected void onPostExecute(Boolean success) {
//
//            if (listener != null) {
//                if (errCode != -1)
//                    listener.onError(errCode);
//                else
//                    listener.onResult(success);
//            }
//        }
//    }
//
//    private static class SendCategoryTask extends AsyncTask<ProdCategory, Void, ProdCategory> {
//        private ResultListener<ProdCategory> listener;
//        private int errCode = -1;
//        private int type = -1;
//        public static final int TASK_ADD_NEW = 0;
//        public static final int TASK_EDIT = 1;
//
//
//        public SendCategoryTask(ResultListener<ProdCategory> _listener, int _type) {
//            listener = _listener;
//            type = _type;
//        }
//
//        @Override
//        protected ProdCategory doInBackground(ProdCategory... params) {
//            try {
//                ProdCategory ctgr = params[0];
//                String name = ctgr.getName();
//                boolean dflt = ctgr.isDefault1();
//                JSONObject jsonObj = new JSONObject();
//                Log.d(Util.APP_TAG, "DFLT: " + dflt);
//                jsonObj.put("name", name);
//                jsonObj.put("default1", dflt);
//                jsonObj.put("sessionString", LoginService.sessionString);
//                String responseString;
//                if (type == TASK_ADD_NEW)
//                    responseString = Util.postRequest(Util.CATEGORY_URL, jsonObj);
//                else {
//                    jsonObj.put("systemId", ctgr.getSystemId());
//                    responseString = Util.putRequest(Util.CATEGORY_URL, jsonObj);
//                }
//
//                JSONObject responseJSon = new JSONObject(responseString);
//                if (responseJSon.has("errorCode")) {
//                    errCode = responseJSon.getInt("errorCode");
//                    return null;
//                }
//                ctgr = Util.parseJSONData(responseString, ProdCategory.class);
//                if (categories == null)
//                    categories = new LinkedList<>();
//
//                if (type == TASK_ADD_NEW)
//                    categories.add(ctgr);
//                else {
//                    for (ProdCategory c : categories) {
//                        if (c.getSystemId().equals(ctgr.getSystemId())) {
//                            Log.d(Util.APP_TAG, "Modifying now..., c: " + c.getName());
//                            c.setName(ctgr.getName());
//                            c.setDefault1(ctgr.isDefault1());
//                            break;
//                        }
//
//                    }
//                }
//                Log.d(Util.APP_TAG, responseJSon.toString(4));
//                return ctgr;
//            } catch (Exception e) {
//                Log.d(Util.APP_TAG, "Exception: " + e.getMessage());
//                e.printStackTrace();
//                errCode = -2;
//                return null;
//
//            }
//        }
//
//        @Override
//        protected void onPostExecute(ProdCategory _ctgr) {
//            if (listener != null) {
//                if (errCode != -1)
//                    listener.onError(errCode);
//                else
//                    listener.onResult(_ctgr);
//            }
//        }
//    }
//
//    private static class SendProductTask extends AsyncTask<Product, Void, Product> {
//        private ResultListener<Product> listener;
//        private int errCode = -1;
//        private int type = -1;
//        public static final int TASK_ADD_NEW = 0;
//        public static final int TASK_EDIT = 1;
//
//
//        public SendProductTask(ResultListener<Product> _listener, int _type) {
//            listener = _listener;
//            type = _type;
//        }
//
//        @Override
//        protected Product doInBackground(Product... params) {
//            try {
//                Product prod = params[0];
//                List<ProdCategory> categories = prod.fetchCategories();
//                String strCtgr = "";
//                if(categories != null && categories.size() > 0) {
//                    for (ProdCategory ctgr : categories) {
//                        strCtgr += ctgr.getSystemId() + ";";
//                    }
//                    strCtgr = strCtgr.substring(0, strCtgr.length() - 1);
//                }
//                else
//                    strCtgr = null;
//
//                String responseString;
//                JSONObject jsonObject = Util.toJSONObject(prod, Product.class);
//                jsonObject.put("sessionString", LoginService.sessionString);
//                jsonObject.remove("categories");
//                if(strCtgr != null)
//                    jsonObject.put("categoriesString", strCtgr);
//
//                if (type == TASK_ADD_NEW)
//                    responseString = Util.postRequest(Util.PRODUCT_URL, jsonObject);
//                else {
////                    jsonObj.put("systemId", ctgr.getSystemId());
//                    responseString = Util.putRequest(Util.PRODUCT_URL, jsonObject);
//                }
//
//                JSONObject responseJSon = new JSONObject(responseString);
//                Log.d(Util.APP_TAG, "Response: " + responseJSon.toString(4));
//
//                if (responseJSon.has("errorCode")) {
//                    errCode = responseJSon.getInt("errorCode");
//                    return null;
//                }
//
//                prod = Util.parseJSONData(responseString, Product.class);
//                if (ProductService.products == null)
//                    ProductService.products = new LinkedList<>();
//
//                if (type == TASK_ADD_NEW)
//                    ProductService.products.add(prod);
//                else {
//                    for (int i = 0 ; i < ProductService.products.size() ; i++) {
//                        Product p = ProductService.products.get(i);
//                        if (p.getSystemId().equals(prod.getSystemId())) {
//                            ProductService.products.set(i, prod);
//                            break;
//                        }
//                    }
//                }
//                return prod;
//            } catch (Exception e) {
//                Log.d(Util.APP_TAG, "Exception: " + e.getMessage());
//                e.printStackTrace();
//                errCode = -2;
//                return null;
//
//            }
//        }
//
//        @Override
//        protected void onPostExecute(Product _prod) {
//            if (listener != null) {
//                if (errCode != -1)
//                    listener.onError(errCode);
//                else
//                    listener.onResult(_prod);
//            }
//        }
//    }
//
//    private static class DeleteProductTask extends AsyncTask<String, Void, Boolean> {
//        private ResultListener<Boolean> listener;
//        private int errCode = -1;
//
//        public DeleteProductTask(ResultListener<Boolean> _listener) {
//            listener = _listener;
//        }
//
//        @Override
//        protected Boolean doInBackground(String... params) {
//            try {
//                String systemId = params[0];
//                String sessionString = LoginService.sessionString;
//                Map<String, String> param = new HashMap<>();
//                param.put("sessionString", sessionString);
//                param.put("systemId", systemId);
//                String responseString = Util.deleteRequest(Util.PRODUCT_URL, param);
//                Log.d(Util.APP_TAG, "Response: " + new JSONObject(responseString).toString(4));
//                JSONObject json = new JSONObject(responseString);
//                if (json.has("errorCode")) {
//                    errCode = json.getInt("errorCode");
//                    return false;
//                }
//
//                Product prod = null;
//                for (Product p : products) {
//                    if (p.getSystemId().equals(systemId)) {
//                        prod = p;
//                        break;
//                    }
//                }
//                products.remove(prod);
//
//                return true;
//            } catch (Exception e) {
//                Log.d(Util.APP_TAG, "Exception: " + e.getMessage());
//                e.printStackTrace();
//                errCode = -2;
//                return false;
//            }
//        }
//
//        @Override
//        protected void onPostExecute(Boolean success) {
//
//            if (listener != null) {
//                if (errCode != -1)
//                    listener.onError(errCode);
//                else
//                    listener.onResult(success);
//            }
//        }
//    }
}

