package com.pocketporter.partner.frgment;

import static com.pocketporter.partner.utility.SessionManager.isChange;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.pocketporter.partner.R;
import com.pocketporter.partner.model.Order;
import com.pocketporter.partner.model.OrderDataItem;
import com.pocketporter.partner.model.Orderstatus;
import com.pocketporter.partner.model.User;
import com.pocketporter.partner.service.APIClient;
import com.pocketporter.partner.service.GetResult;
import com.pocketporter.partner.ui.TripDetailsActivity;
import com.pocketporter.partner.utility.CustPrograssbar;
import com.pocketporter.partner.utility.SessionManager;

import org.json.JSONObject;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import okhttp3.MediaType;
import okhttp3.RequestBody;
import retrofit2.Call;


public class HomeFragment extends Fragment implements GetResult.MyListener {


    @BindView(R.id.txt_neworder)
    TextView txtNeworder;
    @BindView(R.id.txt_ongoing)
    TextView txtOngoing;
    @BindView(R.id.txt_complet)
    TextView txtComplet;
    @BindView(R.id.txt_title)
    TextView txtTitle;
    @BindView(R.id.recycle_pending)
    RecyclerView recyclePending;
    @BindView(R.id.lvl_notfound)
    LinearLayout lvlNotfound;

    CustPrograssbar custPrograssbar;
    SessionManager sessionManager;
    User user;
    Order orderNew;
    int clickedItemPositionRv = 0;
    private FusedLocationProviderClient fusedLocationClient;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_home, container, false);
        ButterKnife.bind(this, view);
        requestPermissions(new String[]{Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION}, 201);
        custPrograssbar = new CustPrograssbar();
        sessionManager = new SessionManager(getActivity());
        user = sessionManager.getUserDetails();

        LinearLayoutManager recyclerLayoutManager = new LinearLayoutManager(getActivity());
        recyclePending.setLayoutManager(recyclerLayoutManager);
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(getActivity());

        getOrderlist("New");
        return view;
    }

    private void getOrderlist(String status) {
        custPrograssbar.prograssCreate(getActivity());
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("rid", user.getId());
            jsonObject.put("dzone", user.getDzone());
            jsonObject.put("vid", user.getVehiid());
            jsonObject.put("status", status);

            Log.d("darwinbark", "getOrderlist: "+jsonObject);

        } catch (Exception e) {
            e.printStackTrace();
        }
        RequestBody bodyRequest = RequestBody.create(MediaType.parse("application/json"), jsonObject.toString());
        Call<JsonObject> call = APIClient.getInterface().pendingOrder(bodyRequest);
        GetResult getResult = new GetResult();
        getResult.setMyListener(this);
        getResult.callForLogin(call, "1");

    }

    public void orderStatusChange(String status, String oid, String lat, String longs) {
        custPrograssbar.prograssCreate(getActivity());
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("rid", user.getId());
            jsonObject.put("oid", oid);
            jsonObject.put("status", status);
            jsonObject.put("lats", lat);
            jsonObject.put("longs", longs);

        } catch (Exception e) {
            e.printStackTrace();
        }
        RequestBody bodyRequest = RequestBody.create(MediaType.parse("application/json"), jsonObject.toString());
        Call<JsonObject> call = APIClient.getInterface().orderStatusChange(bodyRequest);
        GetResult getResult = new GetResult();
        getResult.setMyListener(this);
        getResult.callForLogin(call, "2");

    }

    @Override
    public void callback(JsonObject result, String callNo) {
        try {
            custPrograssbar.closePrograssBar();
            if (callNo.equalsIgnoreCase("1")) {
                Gson gson = new Gson();
                Order order = gson.fromJson(result, Order.class);
                orderNew = order;
                if (order.getResult().equalsIgnoreCase("true")) {
                    if (order.getOrderData().size() != 0) {
                        lvlNotfound.setVisibility(View.GONE);
                        recyclePending.setVisibility(View.VISIBLE);
                        LinearLayoutManager recyclerLayoutManager = new LinearLayoutManager(getActivity());
                        recyclePending.setLayoutManager(recyclerLayoutManager);
                        recyclePending.setAdapter(new PendingAdepter(order.getOrderData()));

                    } else {
                        lvlNotfound.setVisibility(View.VISIBLE);
                        recyclePending.setVisibility(View.GONE);
                    }


                }
            } else if (callNo.equalsIgnoreCase("2")) {
                Gson gson = new Gson();
                Orderstatus orderstatus = gson.fromJson(result.toString(), Orderstatus.class);
                if (orderstatus.getResult().equalsIgnoreCase("true")) {
                    //txtOngoing.performClick(); commented to stop it from goung to ongoinng deleievry
                    // Toast.makeText(getContext(), "" + orderNew.getOrderData().get(0).toString(), Toast.LENGTH_SHORT).show();
                    //startActivity();
                    OrderDataItem clickedOrderItemdata=orderNew.getOrderData().get(clickedItemPositionRv);
                    //since it is accepted callback so we are changing status to accept
                    clickedOrderItemdata.setOrderStatus("accept");
                    startActivity(new Intent(getActivity(), TripDetailsActivity.class).putExtra("myclass", clickedOrderItemdata));

                } else {
                    txtNeworder.performClick(); //clicking neworder button programatically to referesh current fab
                }

            }
        } catch (Exception e) {
            Log.e("Error", "-->" + e.getMessage());
        }
    }


    @OnClick({R.id.txt_neworder, R.id.txt_ongoing, R.id.txt_complet})
    public void onBindClick(View view) {
        switch (view.getId()) {
            case R.id.txt_neworder:
                txtNeworder.setTextColor(getActivity().getResources().getColor(R.color.selectcolor));
                txtComplet.setTextColor(getActivity().getResources().getColor(R.color.colorgrey2));
                txtOngoing.setTextColor(getActivity().getResources().getColor(R.color.colorgrey2));
                txtTitle.setText("New Order");

                txtNeworder.setBackground(getResources().getDrawable(R.drawable.box));
                txtComplet.setBackground(getResources().getDrawable(R.drawable.box1));
                txtOngoing.setBackground(getResources().getDrawable(R.drawable.box1));
                getOrderlist("New");
                break;
            case R.id.txt_ongoing:
                txtNeworder.setTextColor(getActivity().getResources().getColor(R.color.colorgrey2));
                txtComplet.setTextColor(getActivity().getResources().getColor(R.color.colorgrey2));
                txtOngoing.setTextColor(getActivity().getResources().getColor(R.color.selectcolor));
                txtTitle.setText(" OnGoing ");
                txtNeworder.setBackground(getResources().getDrawable(R.drawable.box1));
                txtComplet.setBackground(getResources().getDrawable(R.drawable.box1));
                txtOngoing.setBackground(getResources().getDrawable(R.drawable.box));
                getOrderlist("Ongoing");
                break;
            case R.id.txt_complet:
                txtNeworder.setTextColor(getActivity().getResources().getColor(R.color.colorgrey2));
                txtComplet.setTextColor(getActivity().getResources().getColor(R.color.selectcolor));
                txtOngoing.setTextColor(getActivity().getResources().getColor(R.color.colorgrey2));
                txtTitle.setText("Completed");
                txtNeworder.setBackground(getResources().getDrawable(R.drawable.box1));
                txtComplet.setBackground(getResources().getDrawable(R.drawable.box));
                txtOngoing.setBackground(getResources().getDrawable(R.drawable.box1));
                getOrderlist("Completed");

                break;
            default:
                throw new IllegalStateException("Unexpected value: " + view.getId());
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if (isChange) {
            isChange = false;
            txtNeworder.performClick();
        }
    }

    public class PendingAdepter extends RecyclerView.Adapter<PendingAdepter.ViewHolder> {

        private final List<OrderDataItem> pendinglist;

        public PendingAdepter(List<OrderDataItem> pendinglist) {
            this.pendinglist = pendinglist;
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent,
                                             int viewType) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.pending_item, parent, false);
            ViewHolder viewHolder = new ViewHolder(view);
            return viewHolder;


        }

        @Override
        public void onBindViewHolder(ViewHolder holder,
                                     @SuppressLint("RecyclerView") int position) {
            OrderDataItem item = pendinglist.get(position);
            Glide.with(getActivity()).load(APIClient.baseUrl + "/" + item.getCatImg()).thumbnail(Glide.with(getActivity()).load(R.drawable.emty)).into(holder.imgIcon);
            holder.txtType.setText("" + item.getCatName());
            holder.txtDate.setText("" + item.getDateTime());
            holder.txtTotle.setText(sessionManager.getStringData(SessionManager.currency) + item.getSubtotal());
            holder.txtPickname.setText("" + item.getPickName());
            holder.txtPickaddress.setText("" + item.getPickAddress());
            holder.txtDropname.setText("" + item.getDropName());
            holder.txtDropaddress.setText("" + item.getDropAddress());
            if (item.getOrderStatus().equalsIgnoreCase("Pending")) {
                holder.txtReject.setVisibility(View.VISIBLE);
                holder.txtAccept.setVisibility(View.VISIBLE);
                holder.txtPickp.setVisibility(View.GONE);
                holder.txtDpro.setVisibility(View.GONE);
            } else if (item.getOrderStatus().equalsIgnoreCase("Processing")) {
                holder.txtReject.setVisibility(View.GONE);
                holder.txtAccept.setVisibility(View.GONE);
                holder.txtPickp.setVisibility(View.VISIBLE);
                holder.txtDpro.setVisibility(View.GONE);
            } else if (item.getOrderStatus().equalsIgnoreCase("On Route")) {
                holder.txtReject.setVisibility(View.GONE);
                holder.txtAccept.setVisibility(View.GONE);
                holder.txtPickp.setVisibility(View.GONE);
                holder.txtDpro.setVisibility(View.VISIBLE);
            } else {
                holder.txtReject.setVisibility(View.GONE);
                holder.txtAccept.setVisibility(View.GONE);
                holder.txtPickp.setVisibility(View.GONE);
                holder.txtDpro.setVisibility(View.GONE);
            }

            //topheader listener click

            holder.topHeader.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    startActivity(new Intent(getActivity(), TripDetailsActivity.class).putExtra("myclass", item));
                }
            });
            //linear layout
            holder.lvlClick.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Toast.makeText(getContext(), "clicked linear layout", Toast.LENGTH_SHORT).show();
                    //startActivity(new Intent(getActivity(), TripDetailsActivity.class).putExtra("myclass", item));
                }
            });
            holder.txtAccept.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Toast.makeText(getContext(), "clicked", Toast.LENGTH_SHORT).show();

                    if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                        return;
                    }
                    for( OrderDataItem orderItem: orderNew.getOrderData()){
                        if("processing".equalsIgnoreCase(orderItem.getOrderStatus())) {
                            Toast.makeText(getContext(), "complete ongoing task first", Toast.LENGTH_SHORT).show();
                            return;
                        }
                    }
                    fusedLocationClient.getLastLocation()
                            .addOnSuccessListener(getActivity(), new OnSuccessListener<Location>() {
                                @Override
                                public void onSuccess(Location location) {
                                    // Got last known location. In some rare situations this can be null.
                                    if (location != null) {
                                        // Logic to handle location object
                                        Log.e("lat ", "" + location.getLatitude());
                                        Log.e("long ", "" + location.getLongitude());
                                        clickedItemPositionRv=position;
                                        orderStatusChange("accept", item.getOrderid(), String.valueOf(location.getLatitude()), String.valueOf(location.getLongitude()));
                                        //startActivity(new Intent(getActivity(), TripDetailsActivity.class).putExtra("myclass", item));

                                    }
                                }
                            });

                }
            });
            holder.txtPickp.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                        return;
                    }

                    fusedLocationClient.getLastLocation()
                            .addOnSuccessListener(getActivity(), new OnSuccessListener<Location>() {
                                @Override
                                public void onSuccess(Location location) {
                                    // Got last known location. In some rare situations this can be null.
                                    if (location != null) {
                                        // Logic to handle location object
                                        Log.e("lat ", "" + location.getLatitude());
                                        Log.e("long ", "" + location.getLongitude());
                                        orderStatusChange("pickup", item.getOrderid(), String.valueOf(location.getLatitude()), String.valueOf(location.getLongitude()));
                                    }
                                }
                            });

                }
            });
            holder.txtDpro.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                        return;
                    }

                    fusedLocationClient.getLastLocation()
                            .addOnSuccessListener(getActivity(), new OnSuccessListener<Location>() {
                                @Override
                                public void onSuccess(Location location) {
                                    // Got last known location. In some rare situations this can be null.
                                    if (location != null) {
                                        // Logic to handle location object
                                        //accept-->pickup-->drop-->drop doen(completed)
                                        Log.e("lat ", "" + location.getLatitude());
                                        Log.e("long ", "" + location.getLongitude());
                                        orderStatusChange("complete", item.getOrderid(), String.valueOf(location.getLatitude()), String.valueOf(location.getLongitude()));
                                    }
                                }
                            });

                }
            });
            holder.txtReject.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    orderStatusChange("reject", item.getOrderid(), "null", "null");
                }
            });
        }

        @Override
        public int getItemCount() {
            return pendinglist.size();

        }

        public class ViewHolder extends RecyclerView.ViewHolder {

            @BindView(R.id.img_icon)
            ImageView imgIcon;
            @BindView(R.id.txt_type)
            TextView txtType;
            @BindView(R.id.txt_date)
            TextView txtDate;
            @BindView(R.id.txt_totle)
            TextView txtTotle;
            @BindView(R.id.txt_pickname)
            TextView txtPickname;
            @BindView(R.id.txt_pickaddress)
            TextView txtPickaddress;
            @BindView(R.id.txt_dropname)
            TextView txtDropname;
            @BindView(R.id.txt_dropaddress)
            TextView txtDropaddress;
            @BindView(R.id.txt_reject)
            TextView txtReject;
            @BindView(R.id.txt_accept)
            TextView txtAccept;
            @BindView(R.id.txt_pickp)
            TextView txtPickp;
            @BindView(R.id.txt_dpro)
            TextView txtDpro;
            @BindView(R.id.lvl_click)
            LinearLayout lvlClick;
            @BindView(R.id.topHeader)
            LinearLayout topHeader;

            public ViewHolder(View view) {
                super(view);
                ButterKnife.bind(this, view);
            }
        }
    }
}