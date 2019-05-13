package liz220.cse216.lehigh.edu.lehighmap;

import android.app.Activity;
import android.content.Intent;
import android.content.res.AssetFileDescriptor;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.SystemClock;
import android.provider.MediaStore;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.Toast;

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
import org.tensorflow.lite.Interpreter;

import java.io.FileInputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.util.ArrayList;
import java.util.Collections;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private ArrayList<Pinpoint> mMarkers = new ArrayList<Pinpoint>();
    private Interpreter tflite;

    private final String backend_url = "https://lehigh-map.herokuapp.com";
    static final int REQUEST_IMAGE_CAPTURE = 1;
    private final int input_width = 128;
    private final int input_height = 128;
    private static final int DIM_BATCH_SIZE = 1;
    private static final int DIM_PIXEL_SIZE = 3;
    private static final int numBytesPerChannel = 4;
    private static final int numLabels = 14;
    private int[] mIntImgData;

    protected ByteBuffer imgData = null;
    private float[][] labelProbArray = null;
    // private final String backend_url = "https://ip:8888";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        // Create the tflite object, loaded from the model file
        try{
            tflite = new Interpreter(loadModelFile(MapsActivity.this, "converted_model.tflite"));
        } catch (Exception e){
            e.printStackTrace();
        }

        // Attack the take photo icon with method
        ImageButton btn = (ImageButton)findViewById(R.id.camera);
        btn.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                dispatchTakePictureIntent();
            }
        });

        // prepare the classifier
        imgData = ByteBuffer.allocateDirect(
                DIM_BATCH_SIZE
                        * input_width
                        * input_height
                        * DIM_PIXEL_SIZE
                        * numBytesPerChannel);
        imgData.order(ByteOrder.nativeOrder());
        mIntImgData = new int[input_width * input_height];

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
//                                Log.d("liz",title);
                                mMarkers.add(new Pinpoint(lat, lng, title));
                            }

                            Collections.sort(mMarkers, new Sortbyroll());
                            for (int i = 0; i < mMarkers.size(); ++i) {
                                Log.d("liz",mMarkers.get(i).title+"\n");
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


    private MappedByteBuffer loadModelFile(Activity activity, String MODEL_FILE) throws IOException {
        AssetFileDescriptor fileDescriptor = activity.getAssets().openFd(MODEL_FILE);
        FileInputStream inputStream = new FileInputStream(fileDescriptor.getFileDescriptor());
        FileChannel fileChannel = inputStream.getChannel();
        long startOffset = fileDescriptor.getStartOffset();
        long declaredLength = fileDescriptor.getDeclaredLength();
        return fileChannel.map(FileChannel.MapMode.READ_ONLY, startOffset, declaredLength);
    }


    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
        }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            // ImageView imageView = (ImageView)findViewById(R.id.imageView);
            Bundle extras = data.getExtras();
            Bitmap imageBitmap = (Bitmap) extras.get("data");
            // imageView.setImageBitmap(imageBitmap);
            Log.d("liz", "get picture");

            float[] predict = processImage(imageBitmap);
            Toast.makeText(getApplicationContext(),
                    "Building:"+mMarkers.get((int)predict[0]).title+"\nProbability:"+predict[1]*100+"%",
                    Toast.LENGTH_LONG).show();

        }
    }

    private float[] processImage(Bitmap imageBitmap) {
        Bitmap ResizedBitmap = Bitmap.createScaledBitmap(imageBitmap, input_width, input_height, true);
        convertBitmapToByteBuffer(ResizedBitmap);
        labelProbArray = new float[1][numLabels];
        tflite.run(imgData, labelProbArray);

        int index = argmax(labelProbArray[0]);
        return new float[]{index, labelProbArray[0][index]};
    }

    private int argmax(float[] array) {
        int index = 0;
        float max = 0;
        for(int i = 0; i<14;i++) {
            if(array[i]>max){
                max = array[i];
                index = i;
            }
        }
        return index;
    }

    private void convertBitmapToByteBuffer(Bitmap bitmap) {
        if (imgData == null) {
            return;
        }
        imgData.rewind();
        bitmap.getPixels(mIntImgData, 0, bitmap.getWidth(), 0, 0, bitmap.getWidth(), bitmap.getHeight());
        // Convert the image to floating point.
        int pixel = 0;
        long startTime = SystemClock.uptimeMillis();
        for (int i = 0; i < input_width; ++i) {
            for (int j = 0; j < input_height; ++j) {
                final int val = mIntImgData[pixel++];
                addPixelValue(val);
            }
        }
        long endTime = SystemClock.uptimeMillis();
        Log.d("liz","Timecost to put values into ByteBuffer: " + (endTime - startTime));
    }

    private void addPixelValue(int pixelValue) {
        imgData.putFloat(((pixelValue >> 16) & 0xFF));
        imgData.putFloat(((pixelValue >> 8) & 0xFF));
        imgData.putFloat((pixelValue & 0xFF));
    }
}
