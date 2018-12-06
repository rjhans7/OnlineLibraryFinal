package com.example.roosevelt.onlinelibrary;

import android.content.Context;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;

public class RegisterAdapter {
    private static RegisterAdapter mRegisterAdapterInstance;
    private RequestQueue mRequestQueue;
    private static Context mContext;

    private RegisterAdapter(Context context) {
        mContext = context;
        mRequestQueue = getRequestQueue();
    }

    public static synchronized RegisterAdapter getInstance(Context context) {
        if (mRegisterAdapterInstance == null) {
            mRegisterAdapterInstance = new RegisterAdapter(context);
        }
        return mRegisterAdapterInstance;
    }

    public RequestQueue getRequestQueue() {
        if (mRequestQueue == null) {
            // getApplicationContext() is key, it keeps you from leaking the
            // Activity or BroadcastReceiver if someone passes one in.
            mRequestQueue = Volley.newRequestQueue(mContext.getApplicationContext());
        }
        return mRequestQueue;
    }

    public <T> void addToRequestQueue(Request<T> req,String tag) {
        req.setTag(tag);
        getRequestQueue().add(req);
    }
}
