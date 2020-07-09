package com.kl.cameraapp.view;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.gson.JsonObject;
import com.kl.cameraapp.MainActivity;
import com.kl.cameraapp.R;
import com.kl.cameraapp.controller.MyService;
import com.kl.cameraapp.data.model.DirectionsParser;
import com.kl.cameraapp.data.remote.RetrofitClient;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MapsFragment extends Fragment {
    SupportMapFragment mapFragment;
    GoogleMap ggMap;
    ArrayList arrJam = new ArrayList<>();
    ArrayList<LatLng> route = new ArrayList<>();
    ArrayList listRoute = new ArrayList();
    LatLng hanoi = new LatLng(21.028511, 105.804817);
    private static final int LOCATION_REQUEST = 500;
    String jlat = "";
    String jlon = "";
    PolylineOptions plo = new PolylineOptions();
    String lat = "";
    String lon = "";
    static String baseUrl = "http://192.168.43.194:5000";

    public MapsFragment() {

    }

    Button btnGetJam, btnUpJam;
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
            if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION)
                    != PackageManager.PERMISSION_GRANTED
                    && ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_COARSE_LOCATION)
                    != PackageManager.PERMISSION_GRANTED) {

                ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                        LOCATION_REQUEST);
                return;
            }
            ggMap = googleMap;
            ggMap.setMyLocationEnabled(true);
//            for (int i = 0; i < arrJam.size(); i++) {
//                ggMap.addMarker(new MarkerOptions().position(arrJam.get(i)).title("Marker in HN"));
//                //ggMap.moveCamera(CameraUpdateFactory.newLatLngZoom(hanoi, 15));
////                ggMap.animateCamera(CameraUpdateFactory.zoomIn());
//
//                //ggMap.animateCamera(CameraUpdateFactory.zoomTo(15), 2000, null);
//
//            }

            ggMap.setOnMapLongClickListener(new GoogleMap.OnMapLongClickListener() {
                @Override
                public void onMapLongClick(LatLng latLng) {
                    //Reset marker when already 2
                    if (route.size() == 2) {
                        route.clear();
                        ggMap.clear();
                    }
                    //Save first point select
                    route.add(latLng);
                    //Create marker
                    MarkerOptions markerOptions = new MarkerOptions();
                    markerOptions.position(latLng);

                    if (route.size() == 1) {
                        //Add first marker to the map
                        markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN));
                    } else {
                        //Add second marker to the map
                        markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED));
                    }
                    ggMap.addMarker(markerOptions);

                    if (route.size() == 2) {
                        //Create the URL to get request from first marker to second marker
                        String url = getRequestUrl(route.get(0), route.get(1));
                        TaskRequestDirections taskRequestDirections = new TaskRequestDirections();
                        taskRequestDirections.execute(url);

                    }
//                    mapFragment.getMapAsync(callback);
                }

            });
        }
    };

    private String getRequestUrl(LatLng origin, LatLng dest) {
        //Value of origin
        String str_org = "origin=" + origin.latitude + "," + origin.longitude;
        //Value of destination
        String str_dest = "destination=" + dest.latitude + "," + dest.longitude;
        //Set value enable the sensor
        String sensor = "sensor=true";
        //Mode for find direction
        String mode = "mode=driving";
        //Build the full param
        String param = str_org + "&" + str_dest + "&" + sensor + "&" + mode;
        String key = "&key=AIzaSyAkLMHYfm2RZbGFdZEe8mkM_cIK9x0FY38";
        //Output format
        String output = "json";
        //Create url to request
        String url = "https://maps.googleapis.com/maps/api/directions/" + output + "?" + param + key;
        Log.w("requesturl", url);
        return url;
    }

    private String requestDirection(String reqUrl) throws IOException {
        String responseString = "";
        InputStream inputStream = null;
        HttpURLConnection httpURLConnection = null;
        try {
            URL url = new URL(reqUrl);
            httpURLConnection = (HttpURLConnection) url.openConnection();
            httpURLConnection.connect();

            //Get the response result
            inputStream = httpURLConnection.getInputStream();
            InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
            BufferedReader bufferedReader = new BufferedReader(inputStreamReader);

            StringBuffer stringBuffer = new StringBuffer();
            String line = "";
            while ((line = bufferedReader.readLine()) != null) {
                stringBuffer.append(line);
            }

            responseString = stringBuffer.toString();
            bufferedReader.close();
            inputStreamReader.close();

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (inputStream != null) {
                inputStream.close();
            }
            httpURLConnection.disconnect();
        }
        Log.d("response string", responseString);
        return responseString;
    }

    @SuppressLint("MissingPermission")
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        switch (requestCode) {
            case LOCATION_REQUEST:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    ggMap.setMyLocationEnabled(true);
                }
                break;
        }
    }

    public class TaskRequestDirections extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... strings) {
            String responseString = "jel";
            try {
                responseString = requestDirection(strings[0]);

            } catch (IOException e) {
                e.printStackTrace();
            }
            return responseString;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            //Parse json here
            TaskParser taskParser = new TaskParser();
            taskParser.execute(s);
        }
    }

    public class TaskParser extends AsyncTask<String, Void, List<List<HashMap<String, String>>>> {

        @Override
        protected List<List<HashMap<String, String>>> doInBackground(String... strings) {
            JSONObject jsonObject;
            List<List<HashMap<String, String>>> routes = null;
            try {
                jsonObject = new JSONObject(strings[0]);
                DirectionsParser directionsParser = new DirectionsParser();
                routes = directionsParser.parse(jsonObject);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return routes;
        }

        @Override
        protected void onPostExecute(List<List<HashMap<String, String>>> lists) {
            //Get list route and display it into the map

            ArrayList points;
            jlat = "";
            jlon = "";
            PolylineOptions polylineOptions = null;

            for (List<HashMap<String, String>> path : lists) {
                points = new ArrayList();
                polylineOptions = new PolylineOptions();

                for (HashMap<String, String> point : path) {
                    double lat = Double.parseDouble(point.get("lat"));
                    jlat += lat + ";";
                    double lon = Double.parseDouble(point.get("lon"));

                    jlon += lon + ";";
                    points.add(new LatLng(lat, lon));
                }

                polylineOptions.addAll(points);
                polylineOptions.width(15);
                polylineOptions.color(Color.BLUE);
                polylineOptions.geodesic(true);
            }

            if (polylineOptions != null) {
                ggMap.addPolyline(polylineOptions);
            } else {
                Toast.makeText(getContext(), "Direction not found!", Toast.LENGTH_SHORT).show();
            }

        }
    }


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
                //LatLng longbien = new LatLng(21.015, 105.58);
//                plo = new PolylineOptions();
                listRoute.clear();
                MyService myRetrofit = RetrofitClient.getInstance(baseUrl)
                        .create(MyService.class);

                Call<JsonObject> call = myRetrofit.getAllLat();
                call.enqueue(new Callback<JsonObject>() {
                    @Override
                    public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                        lat = response.body().get("listLat").getAsString();
                        lon = response.body().get("listLon").getAsString();
                        lat = lat.trim();
                        lon = lon.trim();

                        if (!lat.isEmpty() && !lon.isEmpty()) {
                            String[] routeLat = lat.split("\\,");
                            String[] routeLon = lon.split("\\,");
//                            Log.d("lalt routes", latPoint.toString());
                            ExecutorService executor = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());
                            try {
                                for (int i = 0; i < routeLat.length; i++) {
//                                //arrJam.add(new LatLng(Double.parseDouble(latPoint[i]), Double.parseDouble(lonPoint[i])));

//                                arrJam.clear();
//
//                                for (int j = 0; j < lats.length; j++) {
//                                    if (lats[j].contains("[")) {
//                                        lats[j] = lats[j].substring(1);
//                                    }
//                                    if (lons[j].contains("[")) {
//                                        lons[j] = lons[j].substring(1);
//                                    }
//                                    if (lats[j].contains("]")) {
//                                        lats[j] = lats[j].substring(0, lats[j].length() - 2);
//                                    }
//                                    if (lons[j].contains("]")) {
//                                        lons[j] = lons[j].substring(0, lons[j].length() - 2);
//                                    }
////                                    arrJam.add(new LatLng(Double.parseDouble(lats[j]), Double.parseDouble(lons[j])));
//
//                                    arrJam.add(new LatLng(Double.parseDouble(lats[j]), Double.parseDouble(lons[j])));
//                                }
//                                plo.addAll(arrJam);
//                                plo.width(15);
//                                plo.color(Color.RED);
//                                plo.geodesic(true);
//                                ggMap.addPolyline(plo);
//                                Log.d("point", plo.getPoints().size() + "");
//
////                                plo.getPoints().clear();
                                    executor.execute(new DrawRouteThread(routeLat[i], routeLon[i], i+1));
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                            executor.shutdown();

                        }

                    }

                    @Override
                    public void onFailure(Call<JsonObject> call, Throwable t) {
                        Log.d("Fail zz", "");
                    }
                });
//                arrJam.add(longbien);
//                mapFragment.getMapAsync(callback);
            }
        });
        btnUpJam = view.findViewById(R.id.btn_upjam);
        btnUpJam.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ggMap.clear();
                JSONObject json = new JSONObject();
                try {
                    jlat = jlat.substring(0, jlat.length() - 1);
                    jlon = jlon.substring(0, jlon.length() - 1);
                    json.put("user", MainActivity.user);
                    json.put("lat", "[" + jlat + "]");
                    json.put("lon", "[" + jlon + "]");

                    MyService myRetrofit = RetrofitClient.getInstance(baseUrl)
                            .create(MyService.class);

                    Call<Boolean> call = myRetrofit.postJam(json);
                    call.enqueue(new Callback<Boolean>() {
                        @Override
                        public void onResponse(Call<Boolean> call, Response<Boolean> response) {
                            Log.d("post jam ok", "ok");

                        }

                        @Override
                        public void onFailure(Call<Boolean> call, Throwable t) {
                            Log.d("Fail zz", "");
                        }
                    });
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);
        if (mapFragment != null) {
//            arrJam.add(hanoi);
            mapFragment.getMapAsync(callback);
        }
    }

    class DrawRouteThread extends Thread {
        String lats, lons;
        int threadNo;
        ArrayList<LatLng> arrStuckRoad = new ArrayList();

        PolylineOptions po = new PolylineOptions();


        public DrawRouteThread(String lat, String lon, int no) {
            this.lats = lat;
            this.lons = lon;
            this.threadNo = no;

        }

        @Override
        public void run() {

            try {
                mapFragment.getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        String[] latPoints = lats.split("\\;");
                        String[] lonPoints = lons.split("\\;");
                        for (int i = 0; i < latPoints.length; i++) {
                            if (latPoints[i].contains("[")) {
                                latPoints[i] = latPoints[i].substring(1);
                            }
                            if (lonPoints[i].contains("[")) {
                                lonPoints[i] = lonPoints[i].substring(1);
                            }
                            if (latPoints[i].contains("]")) {
                                latPoints[i] = latPoints[i].substring(0, latPoints[i].length() - 2);
                            }
                            if (lonPoints[i].contains("]")) {
                                lonPoints[i] = lonPoints[i].substring(0, lonPoints[i].length() - 2);
                            }
                            arrStuckRoad.add(new LatLng(Double.parseDouble(latPoints[i]),
                                    Double.parseDouble(lonPoints[i])));
                        }
                        Log.d("road 0, " + "thread " + threadNo, arrStuckRoad.get(arrStuckRoad.size()-1).latitude
                                + " " + arrStuckRoad.get(arrStuckRoad.size()-1).longitude);
                        po.addAll(arrStuckRoad);
                        po.width(15);
                        po.color(Color.RED);
                        po.geodesic(true);
                        if (po != null){
                            Polyline p =ggMap.addPolyline(po);
                            if(p != null){
                                Log.d("draw", "ok");
                            }
                        }

                    }
                });

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
