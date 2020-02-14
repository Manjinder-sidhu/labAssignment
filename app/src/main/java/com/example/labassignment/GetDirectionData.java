package com.example.labassignment;

import android.graphics.Color;
import android.os.AsyncTask;
import android.util.Log;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.maps.android.PolyUtil;

import java.io.IOException;
import java.util.HashMap;

public class GetDirectionData extends AsyncTask<Object, String,String> {

    String googleDirectionsData;
    GoogleMap mMap;
    String url;

    String distance;
    String duration;

    LatLng latLng,userlocation;

    @Override
    protected String doInBackground(Object... objects) {
        mMap = (GoogleMap) objects[0];
        url = (String) objects[1];
        userlocation = (LatLng) objects[2];
    latLng =(LatLng) objects[3];
        FetchUrl fetchURL = new FetchUrl();
        try {
            googleDirectionsData = fetchURL.readUrl(url);
        } catch (IOException e) {
            e.printStackTrace();
        }

        return googleDirectionsData;
    }

    @Override
    protected void onPostExecute(String s) {

        HashMap<String, String> distances = null;
        DataParser distancesParser = new DataParser();
        distances = distancesParser.parseDistance(s);

        distance = distances.get("distance");
        duration = distances.get("duration");

        mMap.clear();
        // we create marker options
//        MarkerOptions markerOptions = new MarkerOptions()
//                .position(latLng)
//                .draggable(true)
//                .title("Duration : " + duration)
//                .snippet("Distance : " + distance);
//        mMap.addMarker(markerOptions);




        MarkerOptions markerOptions = new MarkerOptions()
                .position(latLng)
                .draggable(true)
                .title("Duration : " + duration).icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE))
                .snippet("Distance : " + distance);
        mMap.addMarker(markerOptions);


        MarkerOptions markerOptions2 = new MarkerOptions()
                .position(userlocation)
                .draggable(true)
                .title("Duration : " + duration).icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED))
                .snippet("Distance : " + distance);
        mMap.addMarker(markerOptions2);



        /*---------------------------------*/

//        if (MainActivity.directionRequested) {
            String[] directionsList;
            DataParser parser = new DataParser();
            directionsList = parser.parseDirection(s);
            Log.d("", "onPostExecute: " + directionsList);
            displayDirections(directionsList);
        }
//    }

    private void displayDirections(String[] directionsList) {
        int count = directionsList.length;
        for (int i = 0; i < count; i++) {
            PolylineOptions options = new PolylineOptions()
                    .color(Color.RED)
                    .width(10)
                    .addAll(PolyUtil.decode(directionsList[i]));
            mMap.addPolyline(options);
        }
    }


}
