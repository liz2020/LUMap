package liz220.cse216.lehigh.edu.lehighmap;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private ArrayList<Pinpoint> mMarkers = new ArrayList<Pinpoint>();

    private final String backend_url = "https://lehigh-map.herokuapp.com";
    // private final String backend_url = "https://ip:8888";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }
    
    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        populateMarkers(mMap);

        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(40.606691, -75.377016),16));
    }

    private void populateMarkers(final GoogleMap mMap){
        JsonObjectRequest jsonRequest = new JsonObjectRequest(Request.Method.GET,backend_url,null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.d("liz", response.toString());
                        try {
                            JSONArray json = response.getJSONArray("data");
                            for (int i = 0; i < json.length(); ++i) {
                                double lat = json.getJSONObject(i).getDouble("lat");
                                double lng = json.getJSONObject(i).getDouble("lng");
                                String title = json.getJSONObject(i).getString("title");
                                Log.d("liz",title);
                                mMarkers.add(new Pinpoint(lat, lng, title));
                            }
                        }catch (Exception e){
                            e.printStackTrace();
                            Log.e("liz","populate marker fail");
                        }
                        // plot markers to map
                        for(int i =0; i< mMarkers.size();i++) {
                            mMap.addMarker(mMarkers.get(i).getMarker());
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d("liz", "Connection refused");
                error.printStackTrace();
            }
        });

        // Add the request to the RequestQueue.
        VolleySingleton.getInstance(this).addToRequestQueue(jsonRequest);
    }
}
