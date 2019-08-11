package com.project.s1s1s1.syncdemo;

import android.content.Context;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;

public class VolleySingletonClass {
    private static VolleySingletonClass volleyInstance;
    private Context context;
    private RequestQueue requestQueue;

    public VolleySingletonClass(Context context) {
        this.context=context;
        this.requestQueue = getRequestQueue();
    }

    private RequestQueue getRequestQueue() {
        if (requestQueue==null)
            requestQueue = Volley.newRequestQueue(context.getApplicationContext());
        return requestQueue;
    }

    public static synchronized VolleySingletonClass getInstance(Context context){

        if (volleyInstance==null)
            volleyInstance=new VolleySingletonClass(context);
        return volleyInstance;
    }

    public<T> void addToRequestQueue(Request<T> request){
        getRequestQueue().add(request);
    }
}
