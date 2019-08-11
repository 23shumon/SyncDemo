package com.project.s1s1s1.syncdemo;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.TargetApi;
import android.app.job.JobInfo;
import android.app.job.JobScheduler;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.project.s1s1s1.syncdemo.Constant.SYNC_STATUS_OK;
import static com.project.s1s1s1.syncdemo.Constant.SYNC_STSTUS_FAILED;
import static com.project.s1s1s1.syncdemo.Utils.isNetworkAvailable;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = MainActivity.class.getSimpleName();
    private static final int SYNC_JOB_ID = 420;
    RecyclerView dataRV;
    EditText nameET;
    UserAdapter adapter;
    List<User> userList = new ArrayList<>();
    User user;
    DbQuery query;
    BroadcastReceiver receiver;
    IntentFilter filter;
    JobScheduler scheduler;
    JobInfo info;

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        adapter = new UserAdapter(userList);
        nameET = findViewById(R.id.nameET);
        query = new DbQuery(this);
        dataRV = findViewById(R.id.userRV);
        dataRV.setHasFixedSize(true);
        dataRV.setLayoutManager(new LinearLayoutManager(this, RecyclerView.VERTICAL, false));
        dataRV.setAdapter(adapter);

        scheduler = (JobScheduler) this.getSystemService(Context.JOB_SCHEDULER_SERVICE);
        ComponentName jobService = new ComponentName(this, SyncService.class);
        JobInfo.Builder builder = new JobInfo.Builder(SYNC_JOB_ID, jobService);
        long interval = 900000;
        builder.setPeriodic(interval);
        builder.setPersisted(true); /// trigger again when device is rebooted

        info = builder.build();


        getDataFromDb();


//        filter = new IntentFilter();
//        filter.addAction(Constant.UI_UPDATE_BBROADCAST);
//        receiver = new SyncUser() {
//            @Override
//            public void onReceive(Context context, Intent intent) {
//                if (intent.getAction().equals(Constant.UI_UPDATE_BBROADCAST)) {
//                    getDataFromDb();
//                }
//            }
//        };
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    private void doSync() {
        List<User> failedUser = query.syncFailedUser();
        if ( isNetworkAvailable(this)) {
            if (failedUser.size() > 0) {


                int resultCode = scheduler.schedule(info);
                if (resultCode == JobScheduler.RESULT_SUCCESS) {
                    Log.d(TAG, "Job scheduled");
                    getDataFromDb();
                } else {
                    Log.d(TAG, "Job scheduling failed");
                }

//                if (scheduler.schedule(info)==JobScheduler.RESULT_SUCCESS){
//
//                    Toast.makeText(this, "job success", Toast.LENGTH_SHORT).show();
//                    Log.d(TAG, "doSync: success");
//                }
//                Toast.makeText(this, "job started", Toast.LENGTH_SHORT).show();


            } else {
                scheduler.cancel(SYNC_JOB_ID);
                Toast.makeText(this, "job stopped", Toast.LENGTH_SHORT).show();
                Log.d(TAG, "doSync: finish");
            }
        }
        getDataFromDb();
    }

    private void getDataFromDb() {
        userList.clear();
        userList.addAll(query.getAlluser());
        Log.e(TAG, "getDataFromDb: " + userList.size());
        adapter.notifyDataSetChanged();
    }

    private void saveData(final String name) {
        userList.clear();
        if (isNetworkAvailable(this)) {
            StringRequest stringRequest = new StringRequest(Request.Method.POST, Constant.SERVER_URL, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    try {
                        JSONObject jsonObject = new JSONObject(response);
                        String result = jsonObject.getString("response");
                        if (result.equals("ok")) {
                            user = new User(name, SYNC_STATUS_OK);
                            query.saveDataIntoDb(user);
                            getDataFromDb();
                        } else {
                            saveToLocalDb(name);
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Toast.makeText(MainActivity.this, "" + error.getMessage(), Toast.LENGTH_SHORT).show();
                    saveToLocalDb(name);
                }
            }) {
                @Override
                protected Map<String, String> getParams() throws AuthFailureError {
                    Map<String, String> params = new HashMap<>();
                    params.put("name", name);
                    return params;
                }
            };
            VolleySingletonClass singletonClass = new VolleySingletonClass(this);
            singletonClass.addToRequestQueue(stringRequest);
        } else {
            saveToLocalDb(name);
        }
    }

    public void btn_submit(View view) {
        String name = nameET.getText().toString();
        saveData(name);
//        getDataFromDb();
        nameET.setText("");
    }

    public void saveToLocalDb(String name) {
        Log.e(TAG, "saveData: else");
        user = new User(name, SYNC_STSTUS_FAILED);
        query.saveDataIntoDb(user);
        getDataFromDb();
    }

    @Override
    protected void onStart() {
        super.onStart();
        doSync();
//        registerReceiver(receiver, filter);
        Log.e(TAG, "onStart: called");
    }

    @Override
    protected void onPause() {
        super.onPause();
//        unregisterReceiver(receiver);
    }
}
