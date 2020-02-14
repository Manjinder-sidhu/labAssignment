package com.example.labassignment;

import androidx.annotation.DrawableRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.os.Looper;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity implements OnMapReadyCallback ,GoogleMap.OnMarkerDragListener,GoogleMap.OnInfoWindowClickListener{

AlertDialog.Builder alertDialog;
    DatabaseHelper mDataBase;

   private GoogleMap mMap;
    private final int REQUEST_CODE = 1;
         Marker marker;

boolean justforuse;
     String address;
    private static final int SECOND_ACTIVITY_REQUESTCODE = 0;
    //    Location location;
    List<Address> addresses;
   LatLng cutommarker;

private  static final String TAG = "MainActivity";
    //get User Location

    private FusedLocationProviderClient fusedLocationProviderClient;
    LocationCallback locationCallback;
    LocationRequest locationRequest;

    // latitude and longitude

    double latitude, longitude;
    double destlat, destlng;
    final int RADIUS = 1500;
    Geocoder gcode;
    LatLng favMarker;



    public static boolean directionRequested;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mDataBase = new DatabaseHelper(this);


        initMap();
        getUserLocation();

        if(!checkPermission())
            requestPermission();
        else
            fusedLocationProviderClient.requestLocationUpdates(locationRequest, locationCallback, Looper.myLooper());

//        Toast.makeText(this, "here is "+address, Toast.LENGTH_SHORT).show();


    }

    private  void initMap(){
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    private  void getUserLocation(){

        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
        locationRequest = new LocationRequest();
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        locationRequest.setInterval(5000);
        locationRequest.setFastestInterval(3000);
        locationRequest.setSmallestDisplacement(10);
        setHomeMarker();
    }




    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == SECOND_ACTIVITY_REQUESTCODE) {
            if (resultCode == RESULT_OK) {

                // Get String data from Intent
                double lat = data.getExtras().getDouble("latitude");
                double lng = data.getExtras().getDouble("longitude");
                String address1 = data.getExtras().getString("address");
                String date = data.getExtras().getString("date");
                int id = data.getExtras().getInt("id");
                favMarker = new LatLng(lat, lng);
                Toast.makeText(MainActivity.this, "favorite Place" + lat + lng, Toast.LENGTH_SHORT).show();
                mMap.clear();
                mMap.addMarker(new MarkerOptions()
                        .position(favMarker)
                        .title("You are here " + date)
                        .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_MAGENTA))
                        .draggable(true)

                );

                mMap.setOnMarkerDragListener(this);

//                if(mDataBase.updatePlace(id,address1,latitude, longitude, date)){
//
//                    Toast.makeText(MainActivity.this, "location updated", Toast.LENGTH_SHORT).show();
//                }


            }}

    }





    private void setHomeMarker(){
        locationCallback = new LocationCallback(){

            @Override
            public void onLocationResult(LocationResult locationResult) {
                for(Location location : locationResult.getLocations()){
                  LatLng  userLocation = new LatLng(location.getLatitude(), location.getLongitude());
                    latitude = userLocation.latitude;
                    longitude = userLocation.longitude;
                    if (marker != null)
                        marker.remove();
                    CameraPosition cameraPosition = CameraPosition.builder()
                            .target(userLocation)
                            .zoom(15)
                            .bearing(0)
                            .tilt(45)
                            .build();

                    mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
                    mMap.addMarker(new MarkerOptions().position(userLocation)
                                        .title("Your Location")
                                        .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE))
//                            .icon(bitmapDescriptorFromVector(getApplicationContext(), R.drawable.location_icon))
                        );
                    }
                }

        };
    }

    private BitmapDescriptor bitmapDescriptorFromVector(Context context, @DrawableRes int vectorDrawableResourceId){

        Drawable vectorDrawable = ContextCompat.getDrawable(context, vectorDrawableResourceId);
        vectorDrawable.setBounds(0, 0, vectorDrawable.getIntrinsicWidth(), vectorDrawable.getIntrinsicHeight());
        Bitmap bitmap = Bitmap.createBitmap(vectorDrawable.getIntrinsicWidth(), vectorDrawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        vectorDrawable.draw(canvas);
        return  BitmapDescriptorFactory.fromBitmap(bitmap);

    }

    private boolean checkPermission(){
        int permissionState = ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION);
        return permissionState == PackageManager.PERMISSION_GRANTED;
    }

    private  void  requestPermission(){

        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_CODE);

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if(requestCode == REQUEST_CODE){
            if(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                setHomeMarker();
                fusedLocationProviderClient.requestLocationUpdates(locationRequest, locationCallback, Looper.myLooper());

            }
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        mMap.setOnMapLongClickListener(new GoogleMap.OnMapLongClickListener() {
            @Override
            public void onMapLongClick(LatLng latLng) {
                Location location = new Location("Your Destination");
                location.setLatitude(latLng.latitude);
                location.setLongitude(latLng.longitude);

                cutommarker = latLng;
                destlat = latLng.latitude;
                destlng = latLng.longitude;
                if (marker != null) {
                    marker.remove();
                }
                //set marker

                setMarker(location);



                alertDialog = new AlertDialog.Builder(MainActivity.this);
                alertDialog.setMessage("Do you want to Save this place.");
                alertDialog.setCancelable(true);

               alertDialog.setPositiveButton("yes",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                        //to test address here
//                                getaddress();
                                 addressOfPlaces(destlat, destlng);
//                                Toast.makeText(MainActivity.this, "here is "+ address+"nothing", Toast.LENGTH_SHORT).show();

                                if(marker!= null)
                                    marker.remove();
                            }
                        });

                alertDialog.setNegativeButton(
                        "No",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.cancel();
                            }
                        });

               AlertDialog alert11 = alertDialog.create();
                alert11.show();
            }
        });
        mMap.setOnMarkerDragListener(this);
    }


    private void getaddress() {
        String add = "";

        Geocoder geocoder = new Geocoder(getApplicationContext(), Locale.getDefault());
        try {
            List<Address> address = geocoder.getFromLocation(destlat, destlng, 1);
            if (address != null && address.size() > 0) {
                Log.i(TAG, "onLocationResult" + address.get(0));

                if (address.get(0).getSubLocality() != null) {
                    add += " " + address.get(0).getSubLocality();

                }

                if (address.get(0).getLocality() != null) {
                    add += " " + address.get(0).getLocality();

                }

                if (address.get(0).getCountryName() != null) {
                    add += " " + address.get(0).getCountryName();

                }


                if (address.get(0).getPostalCode() != null) {
                    add += " " + address.get(0).getPostalCode();

                }


                Toast.makeText(MainActivity.this, add, Toast.LENGTH_SHORT).show();
            }
        }
        catch (IOException e){
            e.printStackTrace();
        }
       address = add;

    }


    public  void setMarker(Location location){

        LatLng userLatlng = new LatLng(location.getLatitude(), location.getLongitude());
        MarkerOptions options = new MarkerOptions().position(userLatlng)
                .title("your selected place")
                .snippet("you are going there")
                .draggable(true)
                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED));


       marker =  mMap.addMarker(options);
//        counter +=1;
    }


    public void btnClick(View view){

        Object[] dataTransfer;

        switch (view.getId()){


            case R.id.btn_restaurant:
                //get the url from place api
                mMap.clear();
                String url = getUrl(latitude, longitude, "restaurant");
                dataTransfer = new Object[2];
                dataTransfer[0] = mMap;
                dataTransfer[1] = url;
                GetNearbyPlaceData getNearbyPlaceData = new GetNearbyPlaceData();
                getNearbyPlaceData.execute(dataTransfer);

                Toast.makeText(this, "Restaurants", Toast.LENGTH_SHORT).show();
                break;
            case R.id.btn_cafe:
                //get the url from place api
                mMap.clear();
                String url1 = getUrl(latitude, longitude, "cafe");
                dataTransfer = new Object[2];
                dataTransfer[0] = mMap;
                dataTransfer[1] = url1;
                GetNearbyPlaceData getNearbyPlaceData1 = new GetNearbyPlaceData();
                getNearbyPlaceData1.execute(dataTransfer);

                Toast.makeText(this, "Cafes", Toast.LENGTH_SHORT).show();
                break;

            case R.id.btn_groceries:
                //get the url from place api
               mMap.clear();
                String url2 = getUrl(latitude, longitude, "groceries");
                dataTransfer = new Object[2];
                dataTransfer[0] = mMap;
                dataTransfer[1] = url2;
                GetNearbyPlaceData getNearbyPlaceData2 = new GetNearbyPlaceData();
                getNearbyPlaceData2.execute(dataTransfer);

                Toast.makeText(this, "Groceries", Toast.LENGTH_SHORT).show();
                break;

            case R.id.btn_distance:
                //new added
                if (marker != null) {

                    url = getDirectionUrl();
                    dataTransfer = new Object[4];
                    dataTransfer[0] = mMap;
                    dataTransfer[1] = url;
                    dataTransfer[2] = new LatLng(destlat, destlng);
                    dataTransfer[3] = cutommarker;

                    GetDirectionData getDirectionData = new GetDirectionData();
                    getDirectionData.execute(dataTransfer);

                    if (view.getId() == R.id.btn_direction)
                        directionRequested = true;
                    else
                        directionRequested = false;
                }

                break;
            case R.id.btn_direction:
                //new added
                if (marker != null) {

                    url = getDirectionUrl();
                    dataTransfer = new Object[4];
                    dataTransfer[0] = mMap;
                    dataTransfer[1] = url;
                    dataTransfer[2] = new LatLng(destlat, destlng);
                    dataTransfer[3] = cutommarker;

                    GetDirectionData getDirectionData = new GetDirectionData();
                    getDirectionData.execute(dataTransfer);

                    if (view.getId() == R.id.btn_direction)
                        directionRequested = true;
                    else
                        directionRequested = false;
                }
                //new added
                else if (marker == null){
                    Toast.makeText(this, "no destination selected", Toast.LENGTH_SHORT).show();
                }
                break;

            case R.id.btn_clear:
                if(marker != null)
                    marker.remove();
               mMap.clear();
                initMap();
                getUserLocation();

                if(!checkPermission())
                    requestPermission();
                else
                    fusedLocationProviderClient.requestLocationUpdates(locationRequest, locationCallback, Looper.myLooper());


                break;


            case R.id.btn_fav_place:
                //start activity to another activity to use the list of employees
                Intent intent = new Intent(MainActivity.this,fav_place_Activity.class);
               startActivityForResult(intent, SECOND_ACTIVITY_REQUESTCODE);


                break;

        }
    }

    private String getDirectionUrl(){

        StringBuilder directionUrl = new StringBuilder("https://maps.googleapis.com/maps/api/directions/json?");
        directionUrl.append("origin="+latitude+","+longitude);
        directionUrl.append("&destination="+destlat+","+destlng);
        directionUrl.append("&key="+getString(R.string.api_key_places));
        return directionUrl.toString();
    }

    private  String getUrl(double latitude, double longitude, String nearbyPlace){
        StringBuilder placeUrl = new StringBuilder("https://maps.googleapis.com/maps/api/place/nearbysearch/json?");
        placeUrl.append("location="+latitude+","+longitude);
        placeUrl.append("&radius="+RADIUS);
        placeUrl.append("&type="+nearbyPlace);
        placeUrl.append("&sensor=true");
        placeUrl.append("&key="+getString(R.string.api_key_places));
        return placeUrl.toString();
    }


 private void addplaces() {

         Calendar calendar = Calendar.getInstance();
         SimpleDateFormat sdf = new SimpleDateFormat("yyyy-mm-dd hh:mm:ss");
         String date = sdf.format(calendar.getTime());

         if (mDataBase.addPlace(address, destlat, destlng, date))
             Toast.makeText(this, "Place added", Toast.LENGTH_SHORT).show();
         else
             Toast.makeText(this, "Place not added", Toast.LENGTH_SHORT).show();

 }


    private void addressOfPlaces(Double latitude ,Double longitude) {
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-mm-dd hh:mm:ss");
        String addDate = simpleDateFormat.format(calendar.getTime());
//        gcode = new Geocoder(this, Locale.getDefault());
//        try{
//            addresses = gcode.getFromLocation(latitude, longitude,1);
//            if(!address.isEmpty()){
//                address = addresses.get(0).getLocality() + " " + addresses.get(0).getAddressLine(0);
//                System.out.println(addresses.get(0).getAddressLine(0));
//
//
//                if (mDataBase.addPlace(address, latitude, longitude, addDate))
//
////                    Toast.makeText(MainActivity.this, "places:"+addresses.get(0).getAddressLine(0), Toast.LENGTH_SHORT).show();
//        Toast.makeText(MainActivity.this, "places:"+address, Toast.LENGTH_SHORT).show();
//
//                else
//                    Toast.makeText(MainActivity.this, "places NOT FOUND:", Toast.LENGTH_SHORT).show();
//
//
//            }
//            else{


                if (mDataBase.addPlace("", latitude, longitude, addDate))

//                    Toast.makeText(MainActivity.this, "places:"+addresses.get(0).getAddressLine(0), Toast.LENGTH_SHORT).show();
                    Toast.makeText(MainActivity.this, "place added", Toast.LENGTH_SHORT).show();

                else
                    Toast.makeText(MainActivity.this, "places NOT FOUND:", Toast.LENGTH_SHORT).show();

//            }


//        }catch (IOException e){
//            e.printStackTrace();
//
//        }
//
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_item,menu);
       return  true;
    }


    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.normal:
                // do something
                mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
                return true;
            case R.id.sattelite:
                // do something
                mMap.setMapType(GoogleMap.MAP_TYPE_SATELLITE);
                return true;
            case R.id.hybrid:
                // do something
                mMap.setMapType(GoogleMap.MAP_TYPE_HYBRID);
                return true;

            default:
                return super.onContextItemSelected(item);
        }
    }

    @Override
    public void onMarkerDragStart(Marker marker) {

    }

    @Override
    public void onMarkerDrag(Marker marker) {

    }

    @Override
    public void onMarkerDragEnd(Marker marker) {
        destlat = marker.getPosition().latitude;
        destlng = marker.getPosition().longitude;

        mMap.animateCamera(CameraUpdateFactory.newLatLng(marker.getPosition()));
    }

    @Override
    public void onInfoWindowClick(Marker marker) {
        addressOfPlaces(marker.getPosition().latitude, marker.getPosition().longitude);
        Toast.makeText(this, "added", Toast.LENGTH_SHORT).show();


        System.out.println("MARKERS: "+ marker.getTitle());
        android.app.AlertDialog.Builder ad = new android.app.AlertDialog.Builder(this);
        ad.setMessage("You want to add this place as Favourite?");
        ad.setCancelable(true);
        final Marker mMarker = marker;
        ad.setPositiveButton(
                "Yes",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        justforuse = true;
                        Calendar calendar = Calendar.getInstance();
                        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-mm-dd hh:mm:ss");
                        String date = simpleDateFormat.format(calendar.getTime());
                        if (justforuse && mDataBase.addPlace(mMarker.getTitle(), mMarker.getPosition().latitude, mMarker.getPosition().longitude, date)) {
                            Toast.makeText(MainActivity.this, "Place added as favorite", Toast.LENGTH_SHORT).show();
                            justforuse = false;

                        }
                        //Toast.makeText(MainActivity.this, "Place Added As Favourite!", Toast.LENGTH_SHORT).show();

                    }
                });
        ad.setNegativeButton("no", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });

        android.app.AlertDialog alert11 = ad.create();
        alert11.show();
    }
}

