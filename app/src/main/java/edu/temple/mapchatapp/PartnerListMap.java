package edu.temple.mapchatapp;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Handler;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PartnerListMap extends AppCompatActivity implements ListFrag.OnUpdatePartnersListener, OnMapReadyCallback, LocationListener {
    private JSONArray jsonPartners;
    private ArrayList<Partner> partners;
    private Location myLoc;
    private ListFrag listFrag;
    private SupportMapFragment mapFrag;
    private AppCompatActivity act = this;
    private GoogleMap theMap;
    private Handler h;
    private Runnable r;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_partner_list_map);
        partners = new ArrayList<Partner>();
        listFrag = null;
        mapFrag = null;
        theMap = null;

        h = new Handler();

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_COARSE_LOCATION}, 1);
            return;
        } else {
            LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
            LocationListener locationListener = this;
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 5000, 10, locationListener);
            myLoc = getLastKnownLocation();//locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
            sendMyLoc();
        }


        if ((getResources().getConfiguration().screenLayout & Configuration.SCREENLAYOUT_SIZE_MASK) != Configuration.SCREENLAYOUT_SIZE_LARGE) {
            listFrag = (ListFrag) getSupportFragmentManager().findFragmentById(R.id.listFrag);
            final Button switchButton = findViewById(R.id.switchButton);
            switchButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (mapFrag == null) {
                        mapFrag = new SupportMapFragment();
                    }
                    getSupportFragmentManager().beginTransaction().addToBackStack(null).replace(R.id.fragContainer, mapFrag).commit();
                    mapFrag.getMapAsync((OnMapReadyCallback) act);
                }
            });
        } else {
            listFrag = (ListFrag) getSupportFragmentManager().findFragmentById(R.id.listFrag);
            mapFrag = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.mapFrag);
            mapFrag.getMapAsync((OnMapReadyCallback) act);
        }


    }

    @Override
    protected void onResume() {
        super.onResume();
        setPartners();
        h.postDelayed(new Runnable() {
            public void run() {
                setPartners();

                r = this;

                h.postDelayed(r, 30000);
            }
        }, 30000);

    }

    public void setPartners() {
        // Initialize a new RequestQueue instance
        RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
        //final TextView textView = (TextView)findViewById(R.id.textView);
        // Initialize a new JsonArrayRequest instance
        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(
                Request.Method.GET,
                "https://kamorris.com/lab/get_locations.php",
                null,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        jsonPartners = response;
                        partners.clear();
                        try {
                            for (int k = 0; k < jsonPartners.length(); k++) {
                                JSONObject obj = jsonPartners.getJSONObject(k);
                                Location temp = new Location("test");
                                if (myLoc != null) {
                                    partners.add(new Partner(obj, myLoc));
                                }
                                Collections.sort(partners);
                            }
                            //textView.setText(jsonPartners.getJSONObject(0).getString("username"));
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        updateFrags();
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        int a = 5;

                    }
                }
        );

        // Add JsonArrayRequest to the RequestQueue
        requestQueue.add(jsonArrayRequest);
    }

    @Override
    public ArrayList<Partner> getPartnersList() {
        return partners;
    }

    @Override
    public void updatePartners() {
        setPartners();

    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        theMap = googleMap;
        LatLng myLatLng;
        if (myLoc != null) {
            myLatLng = new LatLng(myLoc.getLatitude(), myLoc.getLongitude());
        } else {
            myLatLng = new LatLng(37.421998333333335, -122.08400000000002);
        }
        if (partners != null) {
            for (int k = 0; k < partners.size(); k++) {
                googleMap.addMarker(new MarkerOptions().position(partners.get(k).getLatLng())
                        .title(partners.get(k).toString()));//add a marker for each partner
            }
        }
        //move the camera to the user's location
        googleMap.moveCamera(CameraUpdateFactory.newLatLng(myLatLng));
    }

    private void updateFrags() {
        if (listFrag != null) {
            listFrag.updateAdapter();
        }
        if (mapFrag != null) {
            updateMap();
        }
    }

    private void updateMap() {
        if (partners != null && theMap != null) {
            theMap.clear();
            for (int k = 0; k < partners.size(); k++) {
                theMap.addMarker(new MarkerOptions().position(partners.get(k).getLatLng())
                        .title(partners.get(k).toString()));//add a marker for each partner
            }
        }
    }

    @Override
    public void onLocationChanged(Location location) {
        if (myLoc == null) {
            myLoc = location;
        }
        if (myLoc.distanceTo(location) >= 10) {
            myLoc = location;
            sendMyLoc();
        }
    }

    @Override
    public void onStatusChanged(String s, int i, Bundle bundle) {

    }

    @Override
    public void onProviderEnabled(String s) {

    }

    @Override
    public void onProviderDisabled(String s) {

    }

    private void sendMyLoc() {
        // Initialize a new RequestQueue instance
        RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
        StringRequest stringRequest = new StringRequest(
                Request.Method.POST,
                "https://kamorris.com/lab/register_location.php",
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        if (response.equals("OK")) {

                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {

                    }
                }
        ) {
            @Override
            protected Map<String, String> getParams() {
                EditText editText = (EditText) findViewById(R.id.myUsername);
                Map<String, String> params = new HashMap<String, String>();
                params.put("user", editText.getText().toString());
                params.put("latitude", myLoc.getLatitude() + "");
                params.put("longitude", myLoc.getLongitude() + "");

                return params;
            }
        };
        // Add StringRequest to the RequestQueue
        requestQueue.add(stringRequest);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case 1: {

                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
                    LocationListener locationListener = this;
                    if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                        // TODO: Consider calling
                        //    ActivityCompat#requestPermissions
                        // here to request the missing permissions, and then overriding
                        //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                        //                                          int[] grantResults)
                        // to handle the case where the user grants the permission. See the documentation
                        // for ActivityCompat#requestPermissions for more details.
                        return;
                    }
                    locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 5000, 10, locationListener);
                    myLoc = getLastKnownLocation();//locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                    sendMyLoc();
                    setPartners();

                } else {
                }
                return;
            }
        }
    }

    private Location getLastKnownLocation() {
        LocationManager mLocationManager;
        mLocationManager = (LocationManager) getApplicationContext().getSystemService(LOCATION_SERVICE);
        List<String> providers = mLocationManager.getProviders(true);
        Location bestLocation = null;
        for (String provider : providers) {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            }
            Location l = mLocationManager.getLastKnownLocation(provider);
            if (l == null) {
                continue;
            }
            if (bestLocation == null || l.getAccuracy() < bestLocation.getAccuracy()) {
                // Found best last known location: %s", l);
                bestLocation = l;
            }
        }
        return bestLocation;
    }

}
