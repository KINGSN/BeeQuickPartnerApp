package com.pocketporter.partner.ui;

import static com.pocketporter.partner.utility.SessionManager.currency;
import static com.pocketporter.partner.utility.SessionManager.isChange;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.gson.JsonObject;
import com.pocketporter.partner.R;
import com.pocketporter.partner.map.FetchURL;
import com.pocketporter.partner.map.TaskLoadedCallback;
import com.pocketporter.partner.model.OrderDataItem;
import com.pocketporter.partner.model.User;
import com.pocketporter.partner.service.APIClient;
import com.pocketporter.partner.service.GetResult;
import com.pocketporter.partner.utility.CustPrograssbar;
import com.pocketporter.partner.utility.SessionManager;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import okhttp3.MediaType;
import okhttp3.RequestBody;
import retrofit2.Call;

public class TripDetailsActivity extends AppCompatActivity implements OnMapReadyCallback, TaskLoadedCallback, GetResult.MyListener {


    @BindView(R.id.img_back)
    ImageView imgBack;
    @BindView(R.id.orderid)
    TextView orderid;
    @BindView(R.id.status)
    TextView status;
    @BindView(R.id.txt_details)
    TextView txtDetails;

    @BindView(R.id.txt_pickname)
    TextView txtPickname;
    @BindView(R.id.txt_pickaddress)
    TextView txtPickaddress;
    @BindView(R.id.txt_distance)
    TextView txtDistance;
    @BindView(R.id.txt_time)
    TextView txtTime;
    @BindView(R.id.txt_amount)
    TextView txtAmount;
    @BindView(R.id.txt_reject)
    TextView txtReject;
    @BindView(R.id.txt_accept)
    TextView txtAccept;
    @BindView(R.id.txt_pickp)
    TextView txtPickp;
    @BindView(R.id.txt_dpro)
    TextView txtDpro;
    @BindView(R.id.img_call)
    ImageView imgCall;
    @BindView(R.id.lvl_accept)
    LinearLayout lvlAccept;
    @BindView(R.id.btnWhatsapp)
    ImageView btnWhatsapp;
    @BindView(R.id.btnMap)
    Button btnMap;
    @BindView(R.id.btnMapDrop)
    Button btnMapDrop;
    OrderDataItem item;
    GoogleMap mMap;
    double dist;
    SessionManager sessionManager;
    CustPrograssbar custPrograssbar;
    User user;
    String clickedButtonId = "";
    private Polyline currentPolyline;
    private FusedLocationProviderClient fusedLocationClient;
    Double  distanceDouble;
    public String time;

    private static double distance(double lat1, double lon1, double lat2, double lon2) {
        if ((lat1 == lat2) && (lon1 == lon2)) {
            return 0;
        } else {
            double theta = lon1 - lon2;
            double dist = Math.sin(Math.toRadians(lat1)) * Math.sin(Math.toRadians(lat2)) + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2)) * Math.cos(Math.toRadians(theta));
            dist = Math.acos(dist);
            dist = Math.toDegrees(dist);
            dist = dist * 60 * 1.1515;
            dist = dist * 1.609344;
            dist = (int) Math.round(dist);

            return (dist);
        }
    }

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_trip_details);
        ButterKnife.bind(this);
        custPrograssbar = new CustPrograssbar();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            requestPermissions(new String[]{Manifest.permission.CALL_PHONE}, 201);
        }
        item = getIntent().getParcelableExtra("myclass");
        sessionManager = new SessionManager(this);
        user = sessionManager.getUserDetails();
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        orderid.setText("" + item.getPackageNumber());
        status.setText("" + item.getOrderStatus());
        Toast.makeText(this, item.getOrderStatus(), Toast.LENGTH_SHORT).show();
        /*if (item.getOrderStatus().equalsIgnoreCase("Pending")) {
            lvlAccept.setVisibility(View.VISIBLE);
        } else if (item.getOrderStatus().equalsIgnoreCase("accept")) {
            lvlAccept.setVisibility(View.GONE);
            txtPickp.setVisibility(View.VISIBLE);
        }*/

        btnWhatsapp.setOnClickListener(view ->{
            //NOTE : please use with country code first 2digits without plus signed
            try {
                String mobile = "918984748984";
                String msg = "Order Id Is";
                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://api.whatsapp.com/send?phone=" + mobile + "&text=" + msg)));
            }catch (Exception e){
                //whatsapp app not install
            }

        });
        btnMap.setOnClickListener(view ->{

            String strUri = "http://maps.google.com/maps?q=loc:" + item.getPickLat() + "," + item.getPickLng() + " (" + "Label which you want" + ")";
            Intent intent = new Intent(android.content.Intent.ACTION_VIEW, Uri.parse(strUri));
            intent.setClassName("com.google.android.apps.maps", "com.google.android.maps.MapsActivity");
            startActivity(intent);
        });

        btnMapDrop.setOnClickListener(view ->{
            String strUri = "http://maps.google.com/maps?q=loc:" + item.getDropLat() + "," + item.getDropLng() + " (" + "Label which you want" + ")";
            Intent intent = new Intent(android.content.Intent.ACTION_VIEW, Uri.parse(strUri));
            intent.setClassName("com.google.android.apps.maps", "com.google.android.maps.MapsActivity");
            startActivity(intent);
                });

      //  dist = distance(item.getPickLat(), item.getPickLng(), item.getDropLat(), item.getDropLng());
        try {
            distance2(item.getPickAddress(),item.getDropAddress());

          /*  if (time < 60) {
                txtTime.setText(new DecimalFormat("##").format(time) + " mins");

            } else {
                double tamp = time / 60;
                txtTime.setText(new DecimalFormat("##.##").format(tamp) + " Hours");
            }*/
        } catch (JSONException e) {
            e.printStackTrace();
        }

      //  txtDistance.setText("" + dist);

     /*   txtDistance.setText("" + dist);

        try {
            txtDistance.setText("" + distance2(item.getPickAddress(),item.getDropAddress()));
        } catch (JSONException e) {
            e.printStackTrace();
        }*/
        txtAmount.setText(sessionManager.getStringData(currency) + item.getSubtotal());


        if (item.getOrderStatus().equalsIgnoreCase("Pending")) {
            txtReject.setVisibility(View.VISIBLE);
            txtAccept.setVisibility(View.VISIBLE);
            txtPickp.setVisibility(View.GONE);
            txtDpro.setVisibility(View.GONE);
            imgCall.setVisibility(View.VISIBLE);
            txtPickname.setText("" + item.getPickName());
            txtPickaddress.setText("" + item.getPickAddress());
        } else if (item.getOrderStatus().equalsIgnoreCase("Processing")) {
            txtReject.setVisibility(View.GONE);
            txtAccept.setVisibility(View.GONE);
            txtPickp.setVisibility(View.VISIBLE);
            txtDpro.setVisibility(View.GONE);
            imgCall.setVisibility(View.VISIBLE);

            txtPickname.setText("" + item.getPickName());
            txtPickaddress.setText("" + item.getPickAddress());

        } else if (item.getOrderStatus().equalsIgnoreCase("On Route")) {
            txtReject.setVisibility(View.GONE);
            txtAccept.setVisibility(View.GONE);
            txtPickp.setVisibility(View.GONE);
            txtDpro.setVisibility(View.VISIBLE);
            imgCall.setVisibility(View.VISIBLE);

            txtPickname.setText("" + item.getDropName());
            txtPickaddress.setText("" + item.getDropAddress());

        } else if (item.getOrderStatus().equalsIgnoreCase("accept")) {
           /* txtReject.setVisibility(View.VISIBLE);
            txtAccept.setVisibility(View.GONE);
            txtPickp.setVisibility(View.VISIBLE);
            txtDpro.setVisibility(View.VISIBLE);*/
            txtReject.setVisibility(View.GONE);
            txtAccept.setVisibility(View.GONE);
            txtPickp.setVisibility(View.VISIBLE);
            txtDpro.setVisibility(View.GONE);
        } else {
            txtReject.setVisibility(View.GONE);
            txtAccept.setVisibility(View.GONE);
            txtPickp.setVisibility(View.GONE);
            txtDpro.setVisibility(View.GONE);
            txtPickname.setText("" + item.getDropName());
            txtPickaddress.setText("" + item.getDropAddress());
        }

        imgBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(TripDetailsActivity.this,
                        HomeActivity.class));
            }
        });

    }
    

    @Override
    public void callback(JsonObject result, String callNo) {
        Log.d("darwinbark", "callback: "+result);

        try {
            custPrograssbar.closePrograssBar();
            if (callNo.equalsIgnoreCase("1")) {
                isChange = true;
               // Log.d("darwinbark", "callbackjason: "+result.get("Result")+"\n"+result.get("ResponseMsg"));
                //finish();
                //todo rmeove toast
                // Toast.makeText(this, "callback json "+result, Toast.LENGTH_SHORT).show();
                //we come here after network call finisesh to send ''accept'' data in server
                /*txtPickp.setVisibility(View.VISIBLE);
                txtAccept.setVisibility(View.GONE);
                txtReject.setVisibility(View.GONE);*/

                if (clickedButtonId.equalsIgnoreCase("pickup")) {
                    Toast.makeText(this, "Pickup Complete", Toast.LENGTH_SHORT).show();
                    visibleForDrop();
                }
                if (clickedButtonId.equalsIgnoreCase("accept")) {
                    if(!result.get("Result").getAsBoolean()){
                        Toast.makeText(this, result.get("ResponseMsg").toString(), Toast.LENGTH_SHORT).show();
                        new Handler().postDelayed(() -> {
                            startActivity(new Intent(TripDetailsActivity.this, HomeActivity.class));
                            finish();
                        }, 2000);
                    }else{
                        Toast.makeText(this, "Order Accept", Toast.LENGTH_SHORT).show();
                        visibleForPickup();
                    }


                }
                if (clickedButtonId.equalsIgnoreCase("reject")) {
                    Toast.makeText(this, "Order Reject", Toast.LENGTH_SHORT).show();
                    finish();
                }
                if (clickedButtonId.equalsIgnoreCase("drop done")) {
                    Toast.makeText(this, "Order Delivered", Toast.LENGTH_SHORT).show();
                    finish();
                }


            }


        } catch (Exception e) {
            Log.e("Error", "==>" + e.getMessage());
            Toast.makeText(this, "Something went wrong, pls retry", Toast.LENGTH_SHORT).show();
        }
    }

    private void orderStatusChange(String status, String oid, String lat, String longs) {

        custPrograssbar.prograssCreate(this);
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("rid", user.getId());
            jsonObject.put("oid", oid);
            jsonObject.put("status", status);
            jsonObject.put("lats", lat);
            jsonObject.put("longs", longs);
            Log.d("darwinbark", "orderStatusChange: "+jsonObject);

        } catch (Exception e) {
            e.printStackTrace();
        }

        RequestBody bodyRequest = RequestBody.create(MediaType.parse("application/json"), jsonObject.toString());
        Call<JsonObject> call = APIClient.getInterface().orderStatusChange(bodyRequest);
        GetResult getResult = new GetResult();
        getResult.setMyListener(this);
        getResult.callForLogin(call, "1");

    }



    public void bottonDetails() {
        BottomSheetDialog mBottomSheetDialog = new BottomSheetDialog(this);
        View sheetView = getLayoutInflater().inflate(R.layout.tripe_deails_bottom, null);
        ImageView img_icon = sheetView.findViewById(R.id.img_icon);
        TextView txt_type = sheetView.findViewById(R.id.txt_type);
        TextView txt_date = sheetView.findViewById(R.id.txt_date);
        TextView txt_totle = sheetView.findViewById(R.id.txt_totle);
        TextView txt_pickname = sheetView.findViewById(R.id.txt_pickname);
        TextView txt_pickaddress = sheetView.findViewById(R.id.txt_pickaddress);
        TextView txt_dropname = sheetView.findViewById(R.id.txt_dropname);
        TextView txt_dropaddress = sheetView.findViewById(R.id.txt_dropaddress);
        TextView txt_distance = sheetView.findViewById(R.id.txt_distance);
        TextView txt_time = sheetView.findViewById(R.id.txt_time);
        TextView txt_ptype = sheetView.findViewById(R.id.txt_ptype);


        Glide.with(this).load(APIClient.baseUrl + "/" + item.getCatImg()).thumbnail(Glide.with(this).load(R.drawable.emty)).into(img_icon);
        txt_type.setText("" + item.getCatName());
        txt_date.setText("" + item.getDateTime());
        txt_totle.setText(sessionManager.getStringData(currency) + item.getSubtotal());
        txt_pickname.setText("" + item.getPickName());
        txt_pickaddress.setText("" + item.getPickAddress());
        txt_dropname.setText("" + item.getDropName());
        txt_dropaddress.setText("" + item.getDropAddress());
        txt_ptype.setText("" + item.getPMethodName());
        txt_distance.setText("" + dist);
        txt_time.setText(time);
        /* time = dist * 10;

        if (time < 60) {
            txt_time.setText(new DecimalFormat("##").format(time) + " mins");

        } else {
            double tamp = time / 60;
            txt_time.setText(new DecimalFormat("##.##").format(tamp) + " Hours");
        }*/
        mBottomSheetDialog.setContentView(sheetView);
        mBottomSheetDialog.show();
    }

    @OnClick({R.id.img_back, R.id.txt_details, R.id.img_call, R.id.txt_reject, R.id.txt_accept, R.id.txt_pickp, R.id.txt_dpro})

    public void onBindClick(View view) {
        switch (view.getId()) {
            case R.id.img_back:
                //finish();
                break;
            case R.id.txt_details:
                bottonDetails();
                break;
            case R.id.img_call:
                Intent intent = new Intent(Intent.ACTION_CALL);
                intent.setData(Uri.parse("tel:" + item.getPickMobile()));
                startActivity(intent);
                break;
            case R.id.txt_reject:
                if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    return;
                }
                clickedButtonId = txtReject.getText().toString();

                fusedLocationClient.getLastLocation()
                        .addOnSuccessListener(this, new OnSuccessListener<Location>() {
                            @Override
                            public void onSuccess(Location location) {
                                // Got last known location. In some rare situations this can be null.
                                if (location != null) {
                                    // Logic to handle location object
                                    Log.e("lat ", "" + location.getLatitude());
                                    Log.e("long ", "" + location.getLongitude());
                                    orderStatusChange("reject", item.getOrderid(), String.valueOf(location.getLatitude()), String.valueOf(location.getLongitude()));
                                }
                            }
                        });
                break;
            case R.id.txt_accept:
                if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    return;
                }
                clickedButtonId = txtAccept.getText().toString();
                fusedLocationClient.getLastLocation()
                        .addOnSuccessListener(this, new OnSuccessListener<Location>() {
                            @Override
                            public void onSuccess(Location location) {
                                // Got last known location. In some rare situations this can be null.
                                if (location != null) {
                                    // Logic to handle location object
                                    Log.e("lat ", "" + location.getLatitude());
                                    Log.e("long ", "" + location.getLongitude());
                                    orderStatusChange("accept", item.getOrderid(), String.valueOf(location.getLatitude()), String.valueOf(location.getLongitude()));

                                }
                            }
                        });


                break;
            case R.id.txt_pickp:
                if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    return;
                }
                clickedButtonId = txtPickp.getText().toString();
                fusedLocationClient.getLastLocation()
                        .addOnSuccessListener(this, new OnSuccessListener<Location>() {
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


                break;
            case R.id.txt_dpro:
                if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    return;
                }
                clickedButtonId = txtDpro.getText().toString();

                fusedLocationClient.getLastLocation()
                        .addOnSuccessListener(this, new OnSuccessListener<Location>() {
                            @Override
                            public void onSuccess(Location location) {
                                // Got last known location. In some rare situations this can be null.
                                if (location != null) {
                                    // Logic to handle location object
                                    Log.e("lat ", "" + location.getLatitude());
                                    Log.e("long ", "" + location.getLongitude());
                                    orderStatusChange("complete", item.getOrderid(), String.valueOf(location.getLatitude()), String.valueOf(location.getLongitude()));
                                }
                            }
                        });


                break;
            default:
                throw new IllegalStateException("Unexpected value: " + view.getId());
        }

    }

    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {

        mMap = googleMap;
        MarkerOptions place2;
        MarkerOptions place1;
        if (item.getOrderStatus().equalsIgnoreCase("Pending")) {
            place1 = new MarkerOptions().position(new LatLng(item.getPickLat(), item.getPickLng())).title("Path").icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_current_long));
            place2 = new MarkerOptions().position(new LatLng(item.getDropLat(), item.getDropLng())).title("f").icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_destination_long));
        } else {
            place1 = new MarkerOptions().position(new LatLng(item.getPickLat(), item.getPickLng())).title("Path").icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_current_long));
            place2 = new MarkerOptions().position(new LatLng(item.getDropLat(), item.getDropLng())).title("f").icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_destination_long));
        }

        new FetchURL(this).execute(getUrl(place1.getPosition(), place2.getPosition(), "driving"), "driving");
        mMap.addMarker(place1);
        mMap.addMarker(place2);


        LatLng coordinate = new LatLng(item.getPickLat(), item.getPickLng());
        CameraUpdate yourLocation = CameraUpdateFactory.newLatLngZoom(coordinate, 12);
        mMap.animateCamera(yourLocation);
    }

    private String getUrl(LatLng origin, LatLng dest, String directionMode) {
        // Origin of route
        String strOrigin = "origin=" + origin.latitude + "," + origin.longitude;
        // Destination of route
        String strDest = "destination=" + dest.latitude + "," + dest.longitude;
        // Mode
        String mode = "mode=" + directionMode;
        // Building the parameters to the web service
        String parameters = strOrigin + "&" + strDest + "&" + mode;
        // Output format
        String output = "json";
        // Building the url to the web service
        String url = "https://maps.googleapis.com/maps/api/directions/" + output + "?" + parameters + "&key=" + getString(R.string.API_KEY);
        return url;
    }

    @Override
    public void onTaskDone(Object... values) {

        if (currentPolyline != null)
            currentPolyline.remove();
        currentPolyline = mMap.addPolyline((PolylineOptions) values[0]);

    }

    void visibleForPending() {
        txtReject.setVisibility(View.VISIBLE);
        txtAccept.setVisibility(View.VISIBLE);
        txtPickp.setVisibility(View.GONE);
        txtDpro.setVisibility(View.GONE);
        imgCall.setVisibility(View.VISIBLE);
    }

    void visibleForAccept() {
        txtReject.setVisibility(View.GONE);
        txtAccept.setVisibility(View.GONE);
        txtPickp.setVisibility(View.VISIBLE);
        txtDpro.setVisibility(View.GONE);
    }

    void visibleForDrop() {
        txtReject.setVisibility(View.GONE);
        txtAccept.setVisibility(View.GONE);
        txtPickp.setVisibility(View.GONE);
        txtDpro.setVisibility(View.VISIBLE);
        imgCall.setVisibility(View.VISIBLE);
    }

    void visibleForPickup() {
        txtReject.setVisibility(View.GONE);
        txtAccept.setVisibility(View.GONE);
        txtPickp.setVisibility(View.VISIBLE);
        txtDpro.setVisibility(View.GONE);
        imgCall.setVisibility(View.VISIBLE);
    }


    public void distance2(String address, String address1) throws JSONException {
        String sb="https://maps.googleapis.com/maps/api/distancematrix/json?origins="+address+"&destinations="+address1+"&units=metric&key="+getString(R.string.API_KEY);
        Log.d("KINGSN", "distance2: "+sb);
        String api=sb;
        StringRequest stringRequest = new StringRequest(Request.Method.GET, api,
                new Response.Listener<String>() {
                    @SuppressLint("SetTextI18n")
                    @Override
                    public void onResponse(String response) {
                        System.out.println(response);
                        Log.d("KINGSN", "onResponse:Superactivity1 "+response);
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            JSONObject distanceJson = new JSONObject(response)
                                    .getJSONArray("rows")
                                    .getJSONObject(0)
                                    .getJSONArray("elements")
                                    .getJSONObject(0)
                                    .getJSONObject("distance");

                            JSONObject timeJson = new JSONObject(response)
                                    .getJSONArray("rows")
                                    .getJSONObject(0)
                                    .getJSONArray("elements")
                                    .getJSONObject(0)
                                    .getJSONObject("duration");
                              distanceDouble = null ;
                            String distance = distanceJson.get("text").toString();
                            String timetodeliver = timeJson.get("text").toString();
                            distance=(distance.replace("km", ""));
                            distance=(distance.replace(" ", ""));
                            if (distance.contains("km")){
                                // distanceDouble = Double.parseDouble(distance.replace("km", ""));
                                distanceDouble = Double.parseDouble(distance.replace(",", ""));


                            }else {
                                // distanceDouble = Double.parseDouble(String.valueOf(Integer.parseInt(distance)/1000));
                                distanceDouble = Double.parseDouble(distance.replace(",", ""));
                            }

                            Log.d("KINGSN","array_rows:"+distanceDouble);
                            dist=distanceDouble;
                            txtDistance.setText("" + dist);
                             time = timetodeliver;
                            txtTime.setText(timetodeliver);


                        } catch (JSONException e) {
                            e.printStackTrace();
                        }


                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        //progressDialog.dismiss();
                        //   method.loadingDialogg(MainActivity.this);
                        Log.e("Error", "" + error.getMessage());
                    }

                }) {

            @Override
            protected Map<String, String> getParams() {


                return new HashMap<>();

            }

        };

        stringRequest.setShouldCache(false);
        RequestQueue requestQueue = Volley.newRequestQueue(TripDetailsActivity.this);
        requestQueue.add(stringRequest);
        /*  Log.d("KINGSN", "distance2: "+data1);
        OkHttpClient client = new OkHttpClient().newBuilder()
                .build();
        MediaType mediaType = MediaType.parse("text/plain");
        RequestBody body = RequestBody.create(mediaType, "");
        Request request = new Request.Builder()
                .url("https://maps.googleapis.com/maps/api/distancematrix/json?origins="+address+"&destinations="+address1+"&units=imperial&key="+getString(R.string.API_KEY))
                .method("GET", body)
                .build();
        Response response = client.newCall(request).execute();
        Log.d("KINGSN", "distance: "+response);
    return response.toString();*/






    }
    @Override
    public void onBackPressed() {
      super.onBackPressed();
    }

}