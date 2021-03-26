package org.me.gcu.equakestartercode;

import androidx.fragment.app.FragmentActivity;

import android.graphics.Color;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import org.w3c.dom.Text;
//Kellan brosnan-S1828587
public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private Double LatPass = 4.1;
    private Double LongPass = 1.2;
    private Double Magnitude = 0.1;
    private String QuakeInfodata;
    private TextView QuakeInfoText;
    int colour = 120;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            LatPass = extras.getDouble("Luuat");
            LongPass = extras.getDouble("Loonge");
            QuakeInfodata = extras.getString("QuakInfo");
            Magnitude = extras.getDouble("Magnitude");
            //The key argument here must match that used in the other activity
        }
        //QuakeText
        QuakeInfoText = (TextView) findViewById(R.id.QuakeInfo);
        QuakeInfoText.setText(QuakeInfodata);
        if(Magnitude <= 0.6)
        {
            QuakeInfoText.setBackgroundColor(Color.GREEN);
            colour = 120;
        }
        else if (Magnitude > 0.6 && Magnitude <= 1.2)
        {
            QuakeInfoText.setBackgroundColor(Color.YELLOW);
            colour = 60;
        }
        else
        {
            QuakeInfoText.setBackgroundColor(Color.RED);
            colour =0;
        }
        QuakeInfoText.setTextColor(Color.BLACK);

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
        //Set up marker
        mMap = googleMap;
        LatLng pos = new LatLng(LatPass, LongPass);
        MarkerOptions options = new MarkerOptions().position(pos).title("Earthquake Location");
        options.icon(BitmapDescriptorFactory.defaultMarker(colour));
        //Set marker
        mMap.addMarker(options);
        mMap.moveCamera(CameraUpdateFactory.newLatLng(pos));
    }
}