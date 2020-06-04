package com.sunwell.pos.mobile.util;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Message;
import android.util.Log;
import android.widget.Toast;

import com.example.sunwell.pos_mobile.R;
import com.sunwell.pos.mobile.util.Util;

import org.json.JSONObject;

import java.io.IOException;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * Created by sunwell on 10/5/17.
 */

public class ImageDownloader<T> extends HandlerThread
{
    private static final int MESSAGE_DOWNLOAD = 0;
    private static final String TAG = "ImageDownloader";

    private Drawable defDrawable;
    private Handler requestHandler;
    private Handler responseHandler;
    private DownloadListener<T> downloadListener;
    //    private ConcurrentMap<T,String> mRequestMap = new ConcurrentHashMap<>();
    private ConcurrentMap<T, String> mRequestMap = new ConcurrentHashMap<>();
    private int no = 0;
    private int reqHeight = 0;
    private int reqWidth = 0;
    private static int globalNo = 0;

    public ImageDownloader(Handler _responsehandler, Drawable _defDrawable, int _reqHeight, int _reqWidth)
    {
        super(TAG);
        this.responseHandler = _responsehandler;
        this.defDrawable = _defDrawable;
        this.reqHeight = _reqHeight;
        this.reqWidth = _reqWidth;
        globalNo++;
        no = globalNo;
    }

    public void queueImage(T target, String url)
    {
//        if(url ==  null || url.length() <= 0)
//            return;

        mRequestMap.put(target, url != null ? url : "");
        requestHandler.obtainMessage(MESSAGE_DOWNLOAD, target).sendToTarget();
        Log.i(TAG, "Got a URL: " + url);
    }

    public void clearQueue()
    {
        requestHandler.removeMessages(MESSAGE_DOWNLOAD);
    }

    public void setThumbnailDownloadListener(DownloadListener<T> listener)
    {
        downloadListener = listener;
    }

    @Override
    protected void onLooperPrepared()
    {
        try {
            requestHandler = new Handler()
            {
                @Override
                public void handleMessage(Message msg)
                {
                    try {
                        if (msg.what == MESSAGE_DOWNLOAD) {
                            T target = (T) msg.obj;
                            Log.i(TAG, "Got a request for URL: " + mRequestMap.get(target));
                            handleRequest(target);
                        }
                    }
                    catch(Exception e) {
                        Log.d(Util.APP_TAG, "Error: " + e.getMessage());
                        e.printStackTrace();
                        Toast.makeText(null, R.string.internal_server_error, Toast.LENGTH_SHORT).show();
                    }
                }
            };
        }
        catch(Exception e) {
            Log.d(Util.APP_TAG, "Error: " + e.getMessage());
            e.printStackTrace();
            Toast.makeText(null, R.string.internal_server_error, Toast.LENGTH_SHORT).show();
        }
    }

    private void handleRequest(final T target) throws Exception
    {
        final String url = mRequestMap.get(target);
        Bitmap bitmap = null;
        if (url.length() > 0) {
            byte[] bitmapBytes = Util.sendGetRequest(url, new JSONObject());
//            bitmap = BitmapFactory
//                    .decodeByteArray(bitmapBytes, 0, bitmapBytes.length);
            bitmap = Util.decodeSampledBitmapFromBytes(bitmapBytes, reqWidth, reqHeight);
            Log.i(TAG, "Bitmap created");
        }
        else
            bitmap = null;

        final Bitmap imageBitmap = bitmap;

        responseHandler.post(new Runnable()
        {
            public void run()
            {
                try {
                    if (mRequestMap.get(target) != url) {
                        return;
                    }
                    if (imageBitmap != null)
                        downloadListener.onImageDownloaded(target, imageBitmap);
                    else
                        downloadListener.onImageDownloaded(target, ImageDownloader.this.defDrawable);
                }
                catch(Exception e) {
                    Log.d(Util.APP_TAG, "Error: " + e.getMessage() + " No: " + no);
                    e.printStackTrace();
//                    Toast.makeText(null, R.string.internal_server_error, Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    public interface DownloadListener<T>
    {
        void onImageDownloaded(T target, Bitmap thumbnail) throws Exception;

        void onImageDownloaded(T target, Drawable drawable) throws Exception;
    }
}
