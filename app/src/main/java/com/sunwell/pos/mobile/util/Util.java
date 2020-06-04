package com.sunwell.pos.mobile.util;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.util.Base64;
import android.util.DisplayMetrics;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.sunwell.pos.mobile.dialog.ProgressDialogFragment;
import com.sunwell.pos.mobile.model.Customer;
import com.sunwell.pos.mobile.model.Tenant;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Created by sunwell on 10/3/17.
 */
public class Util
{

    public static final String APP_TAG = "POS";
    public static final int REQUEST_CODE_READ = 0;
    public static final int REQUEST_CODE_ADD = 1;
    public static final int REQUEST_CODE_EDIT = 2;
    public static final int REQUEST_CODE_PICK = 3;
    public static final int REQUEST_CODE_CHOOSE = 4;
    public static final int REQUEST_CODE_CREATE = 5;

    public static final int RESULT_CODE_SUCCESS = 0;
    public static final int RESULT_CODE_FAILED = 1;
    public static final int RESULT_CODE_CANCELED = 2;

    public static final int ERROR_NOT_ENOUGH_STOCK = 8;

//    public static final String BASE_URL = "http://192.168.1.13:8080/pos/";
    public static final String BASE_URL = "http://139.59.101.119:8080/pos/";

    public static final String LOGIN_URL = BASE_URL + "resources/login/";
    public static final String LOGOUT_URL = BASE_URL + "resources/logout/";
    public static final String USER_URL = BASE_URL + "resources/users/";
    public static final String USER_GROUP_URL = BASE_URL + "resources/usergroups/";
    public static final String CUSTOMER_URL = BASE_URL + "resources/customers/";
    public static final String COMPANY_URL = BASE_URL + "resources/tenantinfo/";
    public static final String IMAGES_PATH = "images";
    public static final String CATEGORY_URL = BASE_URL + "resources/category/";
    public static final String SALES_INVOICE_URL = BASE_URL + "resources/salesinvoices/";
    public static final String SALES_PAYMENT_URL = BASE_URL + "resources/salespayments/";
    public static final String PAYMENT_METHOD_URL = BASE_URL + "resources/paymentmethods/";
    public static final String PAYMENT_METHOD_OBJ_URL = BASE_URL + "resources/paymentmethodobjs/";
    public static final String SALES_INVOICE_LINE_URL = BASE_URL + "resources/salesinvoicelines/";
    public static final String PRODUCT_URL = BASE_URL + "resources/products/";
    public static final String STOCK_URL = BASE_URL + "resources/stocks/";
    public static final String WAREHOUSE_URL = BASE_URL + "resources/warehouses/";
    public static final String INCOMING_GOOD_URL = BASE_URL + "resources/incominggoods/";
    public static final String OUTCOMING_GOOD_URL = BASE_URL + "resources/outcominggoods/";
    public static final String STOCK_MUTATION_URL = BASE_URL + "resources/stockmutation/";
    public static final String SPLIT_BILL_URL = BASE_URL + "resources/splitbill/";
    public static String SESSION_STRING = "";
    private static String dialogTag = "";
    private static List<String> dialogTags = new LinkedList<>();
    private static ProgressDialogFragment dialogFragment = new ProgressDialogFragment();
    private static FragmentManager dialogFM;

    public static String getRequest(String urlSpec, Map<String, String> _params) throws Exception
    {
        return new String(sendGetRequest(urlSpec, _params));
    }

    public static String getRequest(String urlSpec, JSONObject _json) throws Exception
    {
        return new String(sendGetRequest(urlSpec, _json));
    }

    public static String postRequest(String urlSpec, JSONObject json) throws Exception
    {
        return new String(sendPostRequest(urlSpec, json));
    }

    public static String putRequest(String urlSpec, JSONObject json) throws Exception
    {
        return new String(sendPutRequest(urlSpec, json));
    }

    public static String deleteRequest(String urlSpec, Map<String, String> _params) throws Exception
    {
        return new String(sendDeleteRequest(urlSpec, _params));
    }

    public static String deleteRequest(String urlSpec, JSONObject _json) throws Exception
    {
        return new String(sendDeleteRequest(urlSpec, _json));
    }

    public static byte[] sendGetRequest(String urlSpec, Map<String, String> _params) throws Exception
    {
        return getData("GET", urlSpec, _params);
    }

    public static byte[] sendGetRequest(String urlSpec, JSONObject _json) throws Exception
    {
        return getData("GET", urlSpec, Util.jsonToMap(_json));
    }

    public static byte[] sendPostRequest(String urlSpec, JSONObject json) throws Exception
    {
        return sendData("POST", urlSpec, json);
    }

    public static byte[] sendPutRequest(String urlSpec, JSONObject json) throws Exception
    {
        return sendData("PUT", urlSpec, json);
    }

    public static byte[] sendDeleteRequest(String urlSpec, Map<String, String> _params) throws Exception
    {
        return getData("DELETE", urlSpec, _params);
    }


    public static byte[] sendDeleteRequest(String urlSpec, JSONObject _json) throws Exception
    {
        return getData("DELETE", urlSpec, Util.jsonToMap(_json));
    }

    public static String getImageURL(String _type, Map<String, String> _params)
    {
        String urlSpec = _type + IMAGES_PATH;
        if (_params != null) {
            Set<String> keys = _params.keySet();
            if (keys != null && keys.size() > 0) {
                urlSpec += "?";
                for (String key : keys) {
                    String value = _params.get(key);
                    urlSpec += key + "=" + value + "&";
                }
                urlSpec = urlSpec.substring(0, urlSpec.length() - 1);
            }
        }
        return urlSpec;
    }

    public static <T> T parseJSONData(String _json, Class<T> type)
    {
//        Gson gson = new Gson();
        Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd HH:mm:ss").create();
        if (_json != null && _json.length() > 0) {
            T obj = gson.fromJson(_json, type);
            return obj;
        }
        return null;
    }

    public static JSONObject toJSONObject(Object _obj, Class type) throws Exception
    {
        Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd HH:mm:ss").create();
        if (_obj != null) {
            String json = gson.toJson(_obj, type);
            JSONObject jsonObj = new JSONObject(json);
            return jsonObj;
        }
        return null;
    }

    public static JSONArray toJSONArray(Object _obj, Class type) throws Exception
    {
        Gson gson = new Gson();
        if (_obj != null) {
            String json = gson.toJson(_obj, type);
            JSONArray jsonArr = new JSONArray(json);
            return jsonArr;
        }
        return null;
    }

    public static double round(double _amount) {
        return Math.round(_amount * 100 ) / 100 ;
    }



    public static int pxToDp(int px, Context _ctx)
    {
        DisplayMetrics displayMetrics = _ctx.getResources().getDisplayMetrics();
        int dp = Math.round(px / (displayMetrics.xdpi / DisplayMetrics.DENSITY_DEFAULT));
        return dp;
    }

    public static int dpToPx(int dp, Context _ctx)
    {
        DisplayMetrics displayMetrics = _ctx.getResources().getDisplayMetrics();
        int px = Math.round(dp * (displayMetrics.xdpi / DisplayMetrics.DENSITY_DEFAULT));
        return px;
    }

    public static <T> void fillSpinner(Spinner _spinner, List<T> _list, Class<T> _type, Context _ctx)
    {
        ArrayAdapter<T> dataAdapter = new ArrayAdapter<T>(_ctx,
                android.R.layout.simple_spinner_item, _list);
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        _spinner.setAdapter(dataAdapter);
    }

    public static String encodeToBase64String(String _path) throws Exception
    {
        InputStream inputStream = new FileInputStream(_path);//You can get an inputStream using any IO API
        byte[] bytes;
        byte[] buffer = new byte[8192];
        int bytesRead;
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        try {
            while ((bytesRead = inputStream.read(buffer)) != -1) {
                output.write(buffer, 0, bytesRead);
            }
        }
        catch (IOException e) {
            e.printStackTrace();
        }
        bytes = output.toByteArray();
        String encodedString = Base64.encodeToString(bytes, Base64.NO_WRAP);
        return encodedString;
    }

    public static String encodeToBase64String(InputStream _stream) throws Exception
    {
//        InputStream inputStream = new FileInputStream(_path);//You can get an inputStream using any IO API
        byte[] bytes;
        byte[] buffer = new byte[8192];
        int bytesRead;
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        try {
            while ((bytesRead = _stream.read(buffer)) != -1) {
                output.write(buffer, 0, bytesRead);
            }
        }
        catch (IOException e) {
            e.printStackTrace();
        }
        bytes = output.toByteArray();
        String encodedString = Base64.encodeToString(bytes, Base64.NO_WRAP);
        return encodedString;
    }

    public static Map<String, String> jsonToMap(JSONObject _json) throws Exception
    {
        Iterator<String> keys = _json.keys();
        Map<String, String> retval = new HashMap<>();
        while (keys.hasNext()) {
            String key = keys.next();
            String val = _json.getString(key);
            retval.put(key, val);
        }

        if (retval.isEmpty())
            return null;

        return retval;
    }

    private static byte[] getData(String _type, String urlSpec, Map<String, String> _params) throws Exception
    {
        if (_params != null) {
            Set<String> keys = _params.keySet();
            if (keys != null && keys.size() > 0) {
                urlSpec += "?";
                for (String key : keys) {
                    String value = _params.get(key);
                    urlSpec += key + "=" + value + "&";
                }
                urlSpec = urlSpec.substring(0, urlSpec.length() - 1);
            }
        }
        Log.d(Util.APP_TAG, " URL SPEC: " + urlSpec);
        URL url = new URL(urlSpec);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod(_type);
        return getRawBytes(connection);
    }

    private static byte[] sendData(String _type, String urlSpec, JSONObject json) throws Exception
    {
        URL url = new URL(urlSpec);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setDoOutput(true);
        connection.setRequestMethod(_type);
        connection.setRequestProperty("Content-Type", "application/json");
        connection.setRequestProperty("Accept", "application/json");

        Log.d(Util.APP_TAG, " SEND JSON: " + json.toString(4));

        Writer writer = new BufferedWriter(new OutputStreamWriter(connection.getOutputStream(), "UTF-8"));
        writer.write(json.toString());
        writer.flush();
        writer.close();

        Log.d(Util.APP_TAG, "FINISHED WRITING ");

        return getRawBytes(connection);
    }

    private static byte[] getRawBytes(HttpURLConnection _connection) throws IOException
    {
        try {
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            InputStream in = _connection.getInputStream();
            if (_connection.getResponseCode() != HttpURLConnection.HTTP_OK) {
                throw new IOException(_connection.getResponseMessage());
            }

            int bytesRead = 0;
            byte[] buffer = new byte[1024];
            while ((bytesRead = in.read(buffer)) > 0) {
                out.write(buffer, 0, bytesRead);
            }
            out.close();

            return out.toByteArray();
        }
        finally {
            _connection.disconnect();
        }
    }

    public static Bitmap decodeSampledBitmapFromBytes(byte[] bytes, int reqWidth, int reqHeight) {

        // First decode with inJustDecodeBounds=true to check dimensions
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeByteArray(bytes, 0, bytes.length, options);

        // Calculate inSampleSize
        options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight);

        // Decode bitmap with inSampleSize set
        options.inJustDecodeBounds = false;
        return BitmapFactory.decodeByteArray(bytes, 0, bytes.length, options);
    }

    public static int calculateInSampleSize(BitmapFactory.Options options, int reqWidth, int reqHeight) {
        // Raw height and width of image
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;

        if (height > reqHeight || width > reqWidth) {

            final int halfHeight = height / 2;
            final int halfWidth = width / 2;

            // Calculate the largest inSampleSize value that is a power of 2 and keeps both
            // height and width larger than the requested height and width.
            while ((halfHeight / inSampleSize) >= reqHeight
                    && (halfWidth / inSampleSize) >= reqWidth) {
                inSampleSize *= 2;
            }
        }

        return inSampleSize;
    }

    public static boolean isSmallScreen(Context _ctx) {
        DisplayMetrics displayMetrics = _ctx.getResources().getDisplayMetrics();
//        Log.d(Util.APP_TAG, " PX: " + displayMetrics.widthPixels + " DP: " + Util.pxToDp(displayMetrics.widthPixels, _ctx));
        if(Util.pxToDp(displayMetrics.widthPixels, _ctx) > 800)
            return false;
        else
            return true;
    }

    public synchronized static void showDialog(FragmentManager _fm, String _tag) {
        Log.d(Util.APP_TAG, " CALL SHOW");

        if(dialogFM != null) {
            if(dialogFM == _fm) {
//                dialogTag = _tag;
                if(!dialogTags.contains(_tag));
                    dialogTags.add(_tag);
            }

            return;
        }

        if(!dialogTags.contains(_tag));
            dialogTags.add(_tag);

//        dialogTag = _tag;
        dialogFM = _fm;
        dialogFragment.setCancelable(false);
        dialogFragment.show(_fm, "LOADING");
    }

    public synchronized static void stopDialog(String _tag) {
        Log.d(Util.APP_TAG, " CALL STOP");

        if(dialogFM == null)
            return;

        dialogTags.remove(_tag);

        if(dialogTags.size() > 0)
            return;

        dialogFragment.dismiss();
        Log.d(Util.APP_TAG, " NULLING DFM ");
        dialogFM = null;
    }
}
