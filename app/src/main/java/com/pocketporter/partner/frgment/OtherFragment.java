package com.pocketporter.partner.frgment;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.pocketporter.partner.R;
import com.pocketporter.partner.model.Help;
import com.pocketporter.partner.model.Pages;
import com.pocketporter.partner.model.User;
import com.pocketporter.partner.service.APIClient;
import com.pocketporter.partner.service.GetResult;
import com.pocketporter.partner.ui.HelpDetailsActivity;
import com.pocketporter.partner.utility.CustPrograssbar;
import com.pocketporter.partner.utility.SessionManager;

import org.json.JSONObject;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import okhttp3.MediaType;
import okhttp3.RequestBody;
import retrofit2.Call;


public class OtherFragment extends Fragment implements GetResult.MyListener {


    @BindView(R.id.recycler_menu)
    public RecyclerView recyclerMenu;

    CustPrograssbar custPrograssbar;
    SessionManager sessionManager;
    User user;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_other, container, false);
        ButterKnife.bind(this, view);

        custPrograssbar = new CustPrograssbar();
        sessionManager = new SessionManager(getActivity());
        user = sessionManager.getUserDetails();

        LinearLayoutManager mLayoutManager2 = new LinearLayoutManager(getActivity());
        mLayoutManager2.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerMenu.setLayoutManager(mLayoutManager2);
        recyclerMenu.setItemAnimator(new DefaultItemAnimator());
        getPrivacy();

        return view;

    }

    private void getPrivacy() {
        custPrograssbar.prograssCreate(getActivity());
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("rid", user.getId());

        } catch (Exception e) {
            e.printStackTrace();
        }
        RequestBody bodyRequest = RequestBody.create(MediaType.parse("application/json"), jsonObject.toString());
        Call<JsonObject> call = APIClient.getInterface().pagelist(bodyRequest);
        GetResult getResult = new GetResult();
        getResult.setMyListener(this);
        getResult.callForLogin(call, "1");

    }


    @Override
    public void callback(JsonObject result, String callNo) {
        try {
            custPrograssbar.closePrograssBar();
            if (callNo.equalsIgnoreCase("1")) {
                Gson gson = new Gson();
                Help help = gson.fromJson(result.toString(), Help.class);
                if (help.getResult().equalsIgnoreCase("true")) {
                    recyclerMenu.setAdapter(new MyFaqAdepter(help.getPagelist()));
                }
            }

        } catch (Exception e) {
            Log.e("Error","==>"+e.getMessage());

        }
    }

    public class MyFaqAdepter extends RecyclerView.Adapter<MyFaqAdepter.ViewHolder> {
        private List<Pages> orderData;

        public MyFaqAdepter(List<Pages> orderData) {
            this.orderData = orderData;
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent,
                                             int viewType) {

            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.halp_item, parent, false);
            ViewHolder viewHolder = new ViewHolder(view);
            return viewHolder;
        }

        @Override
        public void onBindViewHolder(ViewHolder holder,
                                     int position) {
            Log.e("position", "" + position);
            Pages order = orderData.get(position);
            holder.txtTital.setText("" + order.getTitle());

            holder.lvlClick.setOnClickListener(v -> startActivity(new Intent(getActivity(), HelpDetailsActivity.class).putExtra("title", order.getTitle()).putExtra("desc", order.getDescription())));
        }

        @Override
        public int getItemCount() {
            return orderData.size();
        }

        public class ViewHolder extends RecyclerView.ViewHolder {
            @BindView(R.id.txt_tital)
            TextView txtTital;
            @BindView(R.id.lvl_click)
            LinearLayout lvlClick;


            public ViewHolder(View view) {
                super(view);
                ButterKnife.bind(this, view);
            }
        }
    }
}