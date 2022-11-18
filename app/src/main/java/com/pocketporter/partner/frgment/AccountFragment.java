package com.pocketporter.partner.frgment;

import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.pocketporter.partner.R;
import com.pocketporter.partner.model.Account;
import com.pocketporter.partner.model.Orderstatus;
import com.pocketporter.partner.model.PayTrazection;
import com.pocketporter.partner.model.Payout;
import com.pocketporter.partner.model.PayoutListDataItem;
import com.pocketporter.partner.model.User;
import com.pocketporter.partner.service.APIClient;
import com.pocketporter.partner.service.GetResult;
import com.pocketporter.partner.utility.CustPrograssbar;
import com.pocketporter.partner.utility.SessionManager;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import okhttp3.MediaType;
import okhttp3.RequestBody;
import retrofit2.Call;

public class AccountFragment extends Fragment implements GetResult.MyListener {


    @BindView(R.id.img_profile)
    public ImageView imgProfile;
    @BindView(R.id.ed_username)
    public TextView edUsername;
    @BindView(R.id.ed_email)
    public TextView edEmail;
    @BindView(R.id.ed_phone)
    public TextView edPhone;
    @BindView(R.id.txt_star)
    public TextView txtStar;
    @BindView(R.id.txt_complet)
    public TextView txtComplet;
    @BindView(R.id.txt_cencel)
    public TextView txtCencel;
    @BindView(R.id.txt_sale)
    public TextView txtSale;
    @BindView(R.id.txt_tips)
    public TextView txtTips;
    @BindView(R.id.txt_recived)
    public TextView txtRecived;
    @BindView(R.id.txt_case)
    public TextView txtCase;
    @BindView(R.id.lvl_edit)
    public LinearLayout lvlEdit;
    @BindView(R.id.lvl_logout)
    public LinearLayout lvlLogout;
    @BindView(R.id.txt_status)
    public TextView txtStatus;
    @BindView(R.id.switch1)
    public Switch switch1;


    SessionManager sessionManager;
    User user;
    CustPrograssbar custPrograssbar;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_account, container, false);
        ButterKnife.bind(this, view);
        sessionManager = new SessionManager(getActivity());
        user = sessionManager.getUserDetails();
        custPrograssbar = new CustPrograssbar();

        Glide.with(getActivity()).load(APIClient.baseUrl + "/" + user.getRimg()).thumbnail(Glide.with(getActivity()).load(R.drawable.emty)).into(imgProfile);
        getReports();
        return view;
    }

    public void bottonDetails() {
        if (account == null) {
            return;
        }
        BottomSheetDialog mBottomSheetDialog = new BottomSheetDialog(getActivity());
        View sheetView = getLayoutInflater().inflate(R.layout.account_deails_bottom, null);
        TextView txt_vnumber = sheetView.findViewById(R.id.txt_vnumber);
        TextView txt_Dzoner = sheetView.findViewById(R.id.txt_Dzoner);
        TextView txt_address = sheetView.findViewById(R.id.txt_address);
        TextView txt_bname = sheetView.findViewById(R.id.txt_bname);
        TextView txt_anumber = sheetView.findViewById(R.id.txt_anumber);
        TextView txt_ifsccode = sheetView.findViewById(R.id.txt_ifsccode);
//        TextView txt_paypalid = sheetView.findViewById(R.id.txt_paypalid);
//        TextView txt_upiid = sheetView.findViewById(R.id.txt_upiid);

        txt_vnumber.setText("" + account.getUser().getLcode());
        txt_Dzoner.setText("" + account.getUser().getDzone());
        txt_address.setText("" + account.getUser().getLandmark() + "," + account.getUser().getFullAddress());
        txt_bname.setText("" + account.getUser().getBankName());
        txt_anumber.setText("" + account.getUser().getAccNumber());
        txt_ifsccode.setText("" + account.getUser().getIfsc());
//        txt_paypalid.setText("" + account.getUser().getPaypalId());
//        txt_upiid.setText("" + account.getUser().getUpiId());

        mBottomSheetDialog.setContentView(sheetView);
        mBottomSheetDialog.show();
    }

    @OnClick({R.id.lvl_edit, R.id.lvl_payout, R.id.lvl_payoutlist, R.id.lvl_logout})

    public void onBindClick(View view) {
        int id = view.getId();
        if (id == R.id.lvl_edit) {
            bottonDetails();
        } else if (id == R.id.lvl_payout) {
            bottonPaymentList();

        } else if (id == R.id.lvl_payoutlist) {
            getPayoutList();

        } else if (id == R.id.lvl_logout) {
            sessionManager.logoutUser();
            getActivity().finish();
        }
    }

    public void bottonPaymentList() {
        BottomSheetDialog mBottomSheetDialog = new BottomSheetDialog(getActivity());
        View rootView = getLayoutInflater().inflate(R.layout.custome_payout, null);

        EditText edAmount = rootView.findViewById(R.id.ed_acount);
        TextView txtIncome = rootView.findViewById(R.id.txt_income);
        TextView txtExpence = rootView.findViewById(R.id.txt_expence);
        Spinner spinner = rootView.findViewById(R.id.spinner);
        TextView btnCountinue = rootView.findViewById(R.id.btn_countinue);
        List<String> list = new ArrayList<>();
        list.add("BANK");
//        list.add("PAYPAL");
//        list.add("UPI");
        txtIncome.setText(""+account.getPayoutlimit());
        txtExpence.setText(""+account.getOrderData().getTotalEaning());
        ArrayAdapter<String> dataAdapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_spinner_item, list);
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_item);
        spinner.setAdapter(dataAdapter);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                Log.e("tem", parent.toString());
            }
        });

        btnCountinue.setOnClickListener(v -> {

            if (TextUtils.isEmpty(edAmount.getText().toString())) {
                edAmount.setError("");
                return;
            }
            Log.e("data", "-->" + account.getPayoutlimit());
            Log.e("data", "-->" + edAmount.getText().toString());
            int a = Integer.parseInt(account.getPayoutlimit());
            int b = Integer.parseInt(edAmount.getText().toString());
            if (a < b) {
                requestPayout(String.valueOf(b), spinner.getSelectedItem().toString());
            } else {
                Toast.makeText(getActivity(), "Minimum Withdraw Limit : " + account.getPayoutlimit(), Toast.LENGTH_LONG).show();
            }
            mBottomSheetDialog.cancel();
        });
        mBottomSheetDialog.setContentView(rootView);
        mBottomSheetDialog.show();
    }

    BottomSheetDialog mBottomSheetDialog;

    public void bottonPayOutList(List<PayoutListDataItem> list) {

        mBottomSheetDialog = new BottomSheetDialog(getActivity());
        View rootView = getLayoutInflater().inflate(R.layout.custome_payoutlist, null);

        LinearLayout listView = rootView.findViewById(R.id.lvl_list);
        LinearLayout lvlNotfound = rootView.findViewById(R.id.lvl_notfound);
        if (list.size() == 0) {
            lvlNotfound.setVisibility(View.VISIBLE);
        }else {
            lvlNotfound.setVisibility(View.GONE);

        }
        for (int i = 0; i < list.size(); i++) {
            LayoutInflater inflater = LayoutInflater.from(getActivity());
            PayoutListDataItem payout = list.get(i);
            View view = inflater.inflate(R.layout.custome_payoutitem, null);
            ImageView imgProof = view.findViewById(R.id.img_proof);
            TextView txtStatus = view.findViewById(R.id.txt_status);
            TextView txtRequst = view.findViewById(R.id.txt_requst);
            TextView txtAmt = view.findViewById(R.id.txt_amt);
            TextView txtPayby = view.findViewById(R.id.txt_payby);
            TextView txtRDate = view.findViewById(R.id.txt_r_date);
            TextView txtBankname = view.findViewById(R.id.txt_bankname);
            TextView txtIfccode = view.findViewById(R.id.txt_ifccode);
            TextView txtAcno = view.findViewById(R.id.txt_acno);
            TextView txtRname = view.findViewById(R.id.txt_rname);
//            TextView txtPaypalid = view.findViewById(R.id.txt_paypalid);
//            TextView txtUpi = view.findViewById(R.id.txt_upi);

            Glide.with(getActivity()).load(APIClient.baseUrl + "/" + payout.getProof()).into(imgProof);

            txtStatus.setText("" + payout.getStatus());
            txtRequst.setText("" + payout.getRequestId());
            txtAmt.setText("" + payout.getAmt());
            txtPayby.setText("" + payout.getPBy());
            txtRDate.setText("" + payout.getRDate());
            txtBankname.setText("" + payout.getBname());
            txtIfccode.setText("" + payout.getIfsc());
            txtAcno.setText("" + payout.getAcno());
            txtRname.setText("" + payout.getRname());
//            txtPaypalid.setText("" + payout.getPaypalid());
//            txtUpi.setText("" + payout.getUpi());

            listView.addView(view);
        }

        mBottomSheetDialog.setContentView(rootView);
        mBottomSheetDialog.show();
    }

    private void requestPayout(String amount, String type) {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("rid", user.getId());
            jsonObject.put("type", type);
            jsonObject.put("amt", amount);


            RequestBody bodyRequest = RequestBody.create(MediaType.parse("application/json"), jsonObject.toString());
            Call<JsonObject> call = APIClient.getInterface().requestPayout(bodyRequest);
            GetResult getResult = new GetResult();
            getResult.setMyListener(this);
            getResult.callForLogin(call, "3");
            custPrograssbar.prograssCreate(getActivity());
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void getPayoutList() {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("pid", user.getId());

            RequestBody bodyRequest = RequestBody.create(MediaType.parse("application/json"), jsonObject.toString());
            Call<JsonObject> call = APIClient.getInterface().getPayoutlist(bodyRequest);
            GetResult getResult = new GetResult();
            getResult.setMyListener(this);
            getResult.callForLogin(call, "4");
            custPrograssbar.prograssCreate(getActivity());
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void getReports() {
        custPrograssbar.prograssCreate(getActivity());
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("rid", user.getId());

        } catch (Exception e) {
            e.printStackTrace();
        }
        RequestBody bodyRequest = RequestBody.create(MediaType.parse("application/json"), jsonObject.toString());
        Call<JsonObject> call = APIClient.getInterface().totalOrderReport(bodyRequest);
        GetResult getResult = new GetResult();
        getResult.setMyListener(this);
        getResult.callForLogin(call, "1");

    }

    private void statusChange(String status) {
        custPrograssbar.prograssCreate(getActivity());
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("rid", user.getId());
            jsonObject.put("status", status);

        } catch (Exception e) {
            e.printStackTrace();
        }
        RequestBody bodyRequest = RequestBody.create(MediaType.parse("application/json"), jsonObject.toString());
        Call<JsonObject> call = APIClient.getInterface().dboyAppstatus(bodyRequest);
        GetResult getResult = new GetResult();
        getResult.setMyListener(this);
        getResult.callForLogin(call, "2");

    }

    Account account;

    @Override
    public void callback(JsonObject result, String callNo) {
        try {
            custPrograssbar.closePrograssBar();
            if (callNo.equalsIgnoreCase("1")) {
                Gson gson = new Gson();
                account = gson.fromJson(result.toString(), Account.class);
                if (account.getResult().equalsIgnoreCase("true")) {
                    user = account.getUser();
                    sessionManager.setUserDetails(user);
                    edUsername.setText("" + user.getTitle());
                    edEmail.setText("" + user.getEmail());
                    edPhone.setText("" + user.getMobile());
                    txtStar.setText("" + user.getRate());
                    txtComplet.setText("" + account.getOrderData().getTotalCompleteOrder());
                    txtRecived.setText("" + account.getOrderData().getTotalReceiveOrder());
                    txtSale.setText(sessionManager.getStringData(SessionManager.currency) + account.getOrderData().getTotalSale());
                    txtCase.setText(sessionManager.getStringData(SessionManager.currency) + account.getOrderData().getCashOnHand());
                    txtCencel.setText("" + account.getOrderData().getTotalRejectOrder());
                    txtTips.setText(sessionManager.getStringData(SessionManager.currency) + account.getOrderData().getTotalEaning());
                    switch1.setChecked(account.getUser().getRstatus().equalsIgnoreCase("1"));
                    switch1.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                        @Override
                        public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                            if (b) {
                                statusChange("1");

                            } else {
                                statusChange("0");

                            }
                        }
                    });
                }
            } else if (callNo.equalsIgnoreCase("2")) {
                Gson gson = new Gson();
                Orderstatus orderstatus = gson.fromJson(result.toString(), Orderstatus.class);
                Toast.makeText(getActivity(), orderstatus.getResponseMsg(), Toast.LENGTH_SHORT).show();
                if (orderstatus.getResult().equalsIgnoreCase("true")) {
                    if (switch1.isChecked()) {
                        user.setRstatus("1");
                        sessionManager.setUserDetails(user);
                    } else {
                        user.setRstatus("0");
                        sessionManager.setUserDetails(user);
                    }
                }
            } else if (callNo.equalsIgnoreCase("3")) {
                Gson gson = new Gson();
                Payout payout = gson.fromJson(result.toString(), Payout.class);
                Toast.makeText(getActivity(), payout.getResponseMsg(), Toast.LENGTH_LONG).show();
                if (payout.getResult().equalsIgnoreCase("true")) {
                    mBottomSheetDialog.cancel();
                }
            } else if (callNo.equalsIgnoreCase("4")) {
                Gson gson = new Gson();
                PayTrazection payout = gson.fromJson(result.toString(), PayTrazection.class);

                if (payout.getResult().equalsIgnoreCase("true")) {
                    bottonPayOutList(payout.getPayoutListData());
                }
            }

        } catch (Exception e) {
            Log.e("Error", "-->" + e.toString());
        }
    }
}