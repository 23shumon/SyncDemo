package com.project.s1s1s1.syncdemo;

import android.app.job.JobParameters;
import android.app.job.JobService;
import android.content.Intent;
import android.os.Build;
import android.util.Log;

import androidx.annotation.RequiresApi;

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

import static com.android.volley.VolleyLog.TAG;

@RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)

public class SyncService extends JobService {
    @Override
    public boolean onStartJob(JobParameters jobParameters) {
        syncUser(jobParameters);
        Log.d(TAG, "onStartJob: job started");
        return true;
    }

    @Override
    public boolean onStopJob(JobParameters jobParameters) {
        return false;
    }

    private void syncUser(final JobParameters jobParameters) {
        final DbQuery query = new DbQuery(getApplicationContext());
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
                            jobFinished(jobParameters,false);
                            Log.d(TAG, "onResponse: data synced");
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
            VolleySingletonClass.getInstance(getApplicationContext()).addToRequestQueue(stringRequest);
        }
    }

}
