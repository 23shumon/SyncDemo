package com.project.s1s1s1.syncdemo;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SyncUser extends BroadcastReceiver {
    @Override
    public void onReceive(final Context context, Intent intent) {
        if ((Constant.UI_UPDATE_BBROADCAST).equals(intent.getAction())){
        final DbQuery query = new DbQuery(context);
        List<User> failedUserList = query.syncFailedUser();
        for (final User user : failedUserList) {
            StringRequest stringRequest = new StringRequest(Request.Method.POST, Constant.SERVER_URL, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    try {
                        JSONObject jsonObject = new JSONObject(response);
                        String result = jsonObject.getString("response");
                        if (result.equals("ok")) {
                            user.setSync_status(Constant.SYNC_STATUS_OK);
                            query.updateData(user);
                            context.sendBroadcast(new Intent(Constant.UI_UPDATE_BBROADCAST));
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }


                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {

                }
            }) {
                @Override
                protected Map<String, String> getParams() throws AuthFailureError {
                    Map<String, String> params = new HashMap<>();
                    params.put("name", user.getName());
                    return params;
                }
            };
            VolleySingletonClass.getInstance(context).addToRequestQueue(stringRequest);
        }
        }
    }
}
