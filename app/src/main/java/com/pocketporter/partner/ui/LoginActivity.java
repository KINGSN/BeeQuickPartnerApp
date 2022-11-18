package com.pocketporter.partner.ui;

import static com.pocketporter.partner.utility.SessionManager.login;

import android.Manifest;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.CheckBox;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.onesignal.OneSignal;
import com.pocketporter.partner.PolicyActivity;
import com.pocketporter.partner.R;
import com.pocketporter.partner.model.Login;
import com.pocketporter.partner.service.APIClient;
import com.pocketporter.partner.service.GetResult;
import com.pocketporter.partner.utility.CustPrograssbar;
import com.pocketporter.partner.utility.SessionManager;

import org.json.JSONObject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import okhttp3.MediaType;
import okhttp3.RequestBody;
import retrofit2.Call;

public class LoginActivity extends AppCompatActivity implements GetResult.MyListener {

    @BindView(R.id.ed_email)
    EditText edEmail;
    @BindView(R.id.ed_password)
    EditText edPassword;
    @BindView(R.id.checkBoxText)
    TextView checkBoxText;
    @BindView(R.id.txt_continue)
    TextView txtContinue;
    @BindView(R.id.checkBoxPolicy)
    CheckBox checkBox;

    CustPrograssbar custPrograssbar;
    SessionManager sessionManager;

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        requestPermissions(new String[]{Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION}, 201);
        ButterKnife.bind(this);
        custPrograssbar = new CustPrograssbar();
        sessionManager = new SessionManager(this);
        if (sessionManager.getBooleanData(login)) {
            startActivity(new Intent(this, HomeActivity.class));
        }
        checkBoxText.setOnClickListener(View ->{
           startActivity(new Intent(LoginActivity.this, PolicyActivity.class));
        });

    }


    @OnClick({R.id.txt_continue})
    public void onBindClick(View view) {
        if (view.getId() == R.id.txt_continue) {
            if (TextUtils.isEmpty(edEmail.getText().toString())) {
                edEmail.setError("Enter Mobile");
            } else if (TextUtils.isEmpty(edPassword.getText().toString())) {
                edPassword.setError("Enter Password");
            } else {
                login();

            }
        }
    }

    private void login() {
        if (checkBox.isChecked()) {


            custPrograssbar.prograssCreate(this);
            JSONObject jsonObject = new JSONObject();
            try {
                jsonObject.put("mobile", edEmail.getText().toString());
                jsonObject.put("password", edPassword.getText().toString());

            } catch (Exception e) {
                e.printStackTrace();
            }
            RequestBody bodyRequest = RequestBody.create(MediaType.parse("application/json"), jsonObject.toString());
            Call<JsonObject> call = APIClient.getInterface().login(bodyRequest);
            GetResult getResult = new GetResult();
            getResult.setMyListener(this);
            getResult.callForLogin(call, "1");
        }else {
            Toast.makeText(this, "Please accept the policy", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void callback(JsonObject result, String callNo) {
        try {
            custPrograssbar.closePrograssBar();
            if (callNo.equalsIgnoreCase("1")) {
                Gson gson = new Gson();
                Login login = gson.fromJson(result.toString(), Login.class);
                if (login.getResult().equalsIgnoreCase("true")) {
                    sessionManager.setBooleanData(SessionManager.login, true);
                    sessionManager.setUserDetails(login.getUser());
                    sessionManager.setStringData(SessionManager.currency, login.getCurrency());
                    startActivity(new Intent(this, HomeActivity.class));
                    JSONObject tags = new JSONObject();
                    tags.put("riderid", login.getUser().getId());
                    tags.put("dzoneid", login.getUser().getDzone());
                    tags.put("vehicleid", login.getUser().getVehiid());

                    OneSignal.sendTags(tags);

                }
            }
        } catch (Exception e) {
            Log.e("Error", "==>" + e.getMessage());
        }
    }
}