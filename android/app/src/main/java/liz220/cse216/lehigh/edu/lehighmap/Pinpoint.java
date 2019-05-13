package liz220.cse216.lehigh.edu.lehighmap;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.Comparator;

class Pinpoint {
    // Latitude, in degrees.
    double lat;
    // Longitude, in degrees.
    double lng;

    String title;

    LatLng latlng;

    MarkerOptions marker;

    public MarkerOptions getMarker(){
        return marker;
    }

    public LatLng getLatLng(){
        return latlng;
    }

    Pinpoint(double lat, double lng, String title){
        this.lat = lat;
        this.lng = lng;
        this.title = title;
        latlng = new LatLng(lat, lng);
        marker = new MarkerOptions().position(this.latlng).title(title);
    }

}


class Sortbyroll implements Comparator<Pinpoint>
{
    public int compare(Pinpoint a, Pinpoint b)
    {
        return a.title.compareTo(b.title);
    }
}
