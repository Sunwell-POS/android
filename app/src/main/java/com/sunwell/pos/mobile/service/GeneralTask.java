package com.sunwell.pos.mobile.service;

import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import com.example.sunwell.pos_mobile.R;
import com.sunwell.pos.mobile.util.ResultListener;
import com.sunwell.pos.mobile.util.Util;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * Created by sunwell on 10/26/17.
 */
public class GeneralTask<T> extends AsyncTask<JSONObject, Void, T>
{
    public static final int TYPE_GET = 0;
    public static final int TYPE_POST = 1;
    public static final int TYPE_PUT = 2;
    public static final int TYPE_DELETE = 3;

    private int errCode = -1;
    private int type = -1;
    private String url;
    private ResultListener<T> listener;
    private ResponseParser<T> parser;
    private Map<String, Object> requestedAttributes = new HashMap<>();
    private Class<T> classVal;

    public GeneralTask(ResultListener<T> _listener, String _url, Class<T> _class)
    {
        listener = _listener;
        classVal = _class;
        url = _url;
    }

    public void get(JSONObject _jsonObj)
    {
        type = TYPE_GET;
        execute(_jsonObj);
    }

    public void post(JSONObject _jsonObj)
    {
        type = TYPE_POST;
        execute(_jsonObj);
    }

    public void put(JSONObject _jsonObj)
    {
        type = TYPE_PUT;
        execute(_jsonObj);
    }

    public void delete(JSONObject _jsonObj)
    {
        type = TYPE_DELETE;
        execute(_jsonObj);
    }

    public ResponseParser<T> getParser()
    {
        return parser;
    }

    public void setParser(ResponseParser<T> parse)
    {
        Log.d(Util.APP_TAG, " SET PARSe CALLED");
        this.parser = parse;
    }

    public void setRequestedAttributes(String... _attributes)
    {
        if (_attributes != null) {
            for (String attr : _attributes) {
                requestedAttributes.put(attr, null);
            }
        }
    }

    public interface ResponseParser<T>
    {
        public T parse(JSONObject response) throws Exception;
    }

    @Override
    protected T doInBackground(JSONObject... params)
    {
        try {
            JSONObject jsonObj = params[0];
            Log.d(Util.APP_TAG, " INVOICE JSON: " + jsonObj.toString(4));
            T obj = null;

            String responseString = null;

            switch (type) {
                case TYPE_GET:
                    responseString = Util.getRequest(url, jsonObj);
                    break;
                case TYPE_POST:
                    responseString = Util.postRequest(url, jsonObj);
                    break;
                case TYPE_PUT:
                    responseString = Util.putRequest(url, jsonObj);
                    break;
                case TYPE_DELETE:
                    responseString = Util.deleteRequest(url, jsonObj);
                    break;
            }

            JSONObject responseJSon = new JSONObject(responseString);
            Log.d(Util.APP_TAG, "Response: " + responseJSon.toString(4));

            if (responseJSon.has("errorCode")) {
                errCode = responseJSon.getInt("errorCode");
                return null;
            }

            if (parser != null)
                obj = parser.parse(responseJSon);
            else if (classVal != null)
                obj = Util.parseJSONData(responseString, classVal);
            else
                return null;


            if (!requestedAttributes.isEmpty()) {
                Set<String> keys = requestedAttributes.keySet();

                for (String key : keys) {
                    if (responseJSon.has(key)) {
                        requestedAttributes.put(key, responseJSon.get(key));
                    }
                }
            }

            return obj;
        }
        catch (Exception e) {
            Log.d(Util.APP_TAG, "Exception: " + e.getMessage());
            e.printStackTrace();
            errCode = -2;
            return null;

        }
    }

    @Override
    protected void onPostExecute(T _arg)
    {
        try {
            if (listener != null) {
                if (errCode != -1) {
                    listener.onError(GeneralTask.this, errCode);
                } else {
                    listener.onResult(GeneralTask.this, _arg);

                    if (!requestedAttributes.isEmpty()) {
                        listener.onData(GeneralTask.this, requestedAttributes);
                    }
                }
            }
        }
        catch(Exception e) {
            Log.d(Util.APP_TAG, "Error: " + e.getMessage());
            e.printStackTrace();
//            try {
//                listener.onError(GeneralTask.this, errCode);
//            }
//            catch(Exception ex) {
//                Log.d(Util.APP_TAG, " Error: " + ex.getMessage());
//                ex.printStackTrace();
//            }
        }
    }
}