package com.kl.cameraapp.view;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.kl.cameraapp.R;

import java.util.ArrayList;

public class MapsFragment extends Fragment {
    SupportMapFragment mapFragment;
    GoogleMap ggMap;
    ArrayList<LatLng> arrLatlng = new ArrayList<>();
    LatLng hanoi = new LatLng(21.028511, 105.804817);
    public MapsFragment(){

    }
    Button btnGetJam;
    private OnMapReadyCallback callback = new OnMapReadyCallback() {

        /**
         * Manipulates the map once available.
         * This callback is triggered when the map is ready to be used.
         * This is where we can add markers or lines, add listeners or move the camera.
         * In this case, we just add a marker near Sydney, Australia.
         * If Google Play services is not installed on the device, the user will be prompted to
         * install it inside the SupportMapFragment. This method will only be triggered once the
         * user has installed Google Play services and returned to the app.
         */
        @Override
        public void onMapReady(GoogleMap googleMap) {

            ggMap = googleMap;
            for(int i = 0; i<arrLatlng.size(); i++){
                ggMap.addMarker(new MarkerOptions().position(arrLatlng.get(i)).title("Marker in HN"));
                //ggMap.moveCamera(CameraUpdateFactory.newLatLngZoom(hanoi, 15));
//                ggMap.animateCamera(CameraUpdateFactory.zoomIn());

                //ggMap.animateCamera(CameraUpdateFactory.zoomTo(15), 2000, null);
            }
            ggMap.moveCamera(CameraUpdateFactory.newLatLngZoom(arrLatlng.get(0), 15));
        }
    };

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_maps, container, false);
        btnGetJam = view.findViewById(R.id.btn_getjam);

        btnGetJam.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LatLng longbien = new LatLng(21.015, 105.58);
                arrLatlng.add(longbien);
                mapFragment.getMapAsync(callback);
            }
        });
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);
        if (mapFragment != null) {
            arrLatlng.add(hanoi);
            mapFragment.getMapAsync(callback);
        }
    }

}