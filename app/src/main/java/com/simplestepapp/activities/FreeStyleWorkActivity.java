package com.simplestepapp.activities;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.StrictMode;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatButton;
import android.util.Log;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ListView;
import android.widget.QuickContactBadge;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.gson.Gson;
import com.simplestepapp.R;
import com.simplestepapp.adapters.ExerciseAdapter;
import com.simplestepapp.adapters.UserExerciseAdapter;
import com.simplestepapp.models.exercise.ExerciseModel;
import com.simplestepapp.models.exercise.UExercise;
import com.simplestepapp.utils.AppConfig;
import com.simplestepapp.utils.SessionManager;
import com.simplestepapp.utils.Toaster;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import pl.droidsonroids.gif.GifImageView;

/**
 * Created by Srinivas on 2/8/2019.
 */

public class FreeStyleWorkActivity extends AppCompatActivity {

    ProgressDialog progressDialog;
    RequestQueue requestQueue;
    SessionManager sessionManager;

    AppCompatButton btn_Start;

    CheckBox cb_SlctAll;

    ListView lv_Exrcis;

    public static List<UExercise> list_Exercises = new ArrayList<>();

    String userName = "", eMailId = "", token = "";

    UserExerciseAdapter exerciseAdapter;

    String selected_videos, str_UserID;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_frestlewrk);

        initviews();

        progressDialog = new ProgressDialog(this);
        requestQueue = Volley.newRequestQueue(this);
        sessionManager = new SessionManager(this);

        if (sessionManager.isLoggedIn()) {
            HashMap<String, String> user = sessionManager.getUserDetails();
            userName = user.get(SessionManager.KEY_NAME);
            eMailId = user.get(SessionManager.KEY_EMAIL);
            token = user.get(SessionManager.KEY_TOKEN);
            str_UserID = user.get(SessionManager.KEY_USERID);
        }

        getUserExcercises();

        cb_SlctAll.setEnabled(false);

        cb_SlctAll.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    for (int i = 0; i < list_Exercises.size(); i++) {
                        list_Exercises.get(i).setSelected(true);
                    }
                } else {
                    for (int i = 0; i < list_Exercises.size(); i++) {
                        list_Exercises.get(i).setSelected(false);
                    }
                }

                if (exerciseAdapter == null) {
                    exerciseAdapter = new UserExerciseAdapter(FreeStyleWorkActivity.this, list_Exercises);
                    lv_Exrcis.setAdapter(exerciseAdapter);
                } else {
                    exerciseAdapter.notifyDataSetChanged();
                }
            }
        });


        btn_Start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                selected_videos = "";
                for (int i = 0; i < list_Exercises.size(); i++) {
                    if (list_Exercises.get(i).getSelected()) {
                        if (selected_videos.equalsIgnoreCase("")) {
                            selected_videos = selected_videos + list_Exercises.get(i).get_id();
                        } else {
                            selected_videos = selected_videos + "," + list_Exercises.get(i).get_id();
                        }
                    }
                }
                Intent intent = new Intent(FreeStyleWorkActivity.this, FreStlVideoPlayActivity.class);
                /*intent.putExtra("sets", list_Exercises.get(0).getSets());
                intent.putExtra("reps", list_Exercises.get(0).getReps());
                intent.putExtra("selected_videos", selected_videos);
                intent.putExtra("master_id", String.valueOf(list_Exercises.get(0).get_id()));*/
                intent.putExtra("sets", "3");
                intent.putExtra("reps", "5");
                intent.putExtra("selected_videos", selected_videos);
                intent.putExtra("master_id", "5545688655");

                startActivity(intent);
            }
        });

    }

    private void initviews() {
        btn_Start = findViewById(R.id.btn_Start);
        cb_SlctAll = findViewById(R.id.cb_SlctAll);
        lv_Exrcis = findViewById(R.id.lv_Exrcis);
    }

    private void getUserExcercises() {

        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        progressDialog.setMessage("Loading ...");
        progressDialog.show();

        Map<String, String> params = new HashMap<>();
        params.put("userid", str_UserID);
        JSONObject jsonObject = new JSONObject(params);

        JsonObjectRequest request_Excercise = new JsonObjectRequest(Request.Method.GET, AppConfig.getUser_Excercises, jsonObject, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                progressDialog.dismiss();
                Log.d("Excercise Re", "" + response.toString());
                try {
                    list_Exercises = new ArrayList<>();
                    JSONArray jsonArray = new JSONArray(response.getString("userExercise"));
                    list_Exercises = new Gson().fromJson(String.valueOf(jsonArray), ExerciseModel.class);
                    if (list_Exercises.size() > 0) {

                        for (int i = 0; i < list_Exercises.size(); i++) {
                            list_Exercises.get(i).setSelected(false);
                        }

                        exerciseAdapter = new UserExerciseAdapter(FreeStyleWorkActivity.this, list_Exercises);
                        lv_Exrcis.setAdapter(exerciseAdapter);
                        cb_SlctAll.setEnabled(true);
                        cb_SlctAll.setChecked(true);
                        if (cb_SlctAll.isChecked()) {
                            for (int i = 0; i < list_Exercises.size(); i++) {
                                list_Exercises.get(i).setSelected(true);
                            }
                        } else {
                            for (int i = 0; i < list_Exercises.size(); i++) {
                                list_Exercises.get(i).setSelected(false);
                            }
                        }

                        if (exerciseAdapter == null) {
                            exerciseAdapter = new UserExerciseAdapter(FreeStyleWorkActivity.this, list_Exercises);
                            lv_Exrcis.setAdapter(exerciseAdapter);
                        } else {
                            exerciseAdapter.notifyDataSetChanged();

                        }
                    }else{
                        cb_SlctAll.setVisibility(View.GONE);
                        Toaster.showInfoMessage("No Workouts !");
                    }
                } catch (
                        JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                HashMap<String, String> headers = new HashMap<String, String>();
                headers.put("Content-Type", "application/json");
                headers.put("Authorization", "token " + token);
                return headers;
            }
        };
        requestQueue.add(request_Excercise);
    }
}

