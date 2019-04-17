package liz220.cse216.lehigh.edu.lehighmap;

import android.support.v4.app.FragmentActivity;
import android.os.Bundle;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ArrayList;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private ArrayList<Pinpoint> mMarkers = new ArrayList<Pinpoint>();

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
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        populateMarkers(mMarkers);

        // plot markers to map
        for(int i =0; i< mMarkers.size();i++) {
            mMap.addMarker(mMarkers.get(i).getMarker());
        }

        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(40.606691, -75.377016),16));
    }

    private void populateMarkers(ArrayList<Pinpoint> mMarkers){

        // later change this to http request
        mMarkers.add(new Pinpoint(40.608962, -75.377874,"E.W. Fairchild-Martindale Library"));
        mMarkers.add(new Pinpoint(40.609028, -75.377249,"Sinclair Laboratory"));
        mMarkers.add(new Pinpoint(40.607257, -75.374035,"Taylor Gymnasium"));
    }
}
