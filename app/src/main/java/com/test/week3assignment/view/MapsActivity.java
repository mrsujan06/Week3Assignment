package com.test.week3assignment.view;

import android.content.Intent;
import android.location.Location;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.messaging.FirebaseMessaging;
import com.test.week3assignment.R;
import com.test.week3assignment.api.SendPushApi;
import com.test.week3assignment.model.Notification;
import com.test.week3assignment.model.PushRequestBody;
import com.test.week3assignment.model.PushResponse;
import com.test.week3assignment.presenter.CustomInfoWindowAdapter;
import com.test.week3assignment.api.ApiObservableParkingService;
import com.test.week3assignment.model.ParkingResponse;
import com.test.week3assignment.presenter.DatabaseAdapter;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.meta.When;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;
import io.realm.Realm;
import io.realm.RealmConfiguration;
import io.realm.RealmResults;
import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

import static android.content.pm.PackageManager.PERMISSION_GRANTED;
import static com.google.android.gms.location.LocationServices.FusedLocationApi;

public class MapsActivity extends FragmentActivity implements GoogleMap.OnInfoWindowClickListener, OnMapReadyCallback, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {

    private GoogleMap mMap;
    private GoogleApiClient googleApiClient;
    private static final int LOCATION_REQUEST_CODE = 101;
    private RecyclerView recyclerView;
    DatabaseAdapter myAdapter;
    Realm realm;

    @BindView(R.id.parkingReservation)
    Button parkingReservation;

    @BindView(R.id.search)
    Button search;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        ButterKnife.bind(this);
        FirebaseMessaging.getInstance().subscribeToTopic("news");

        googleApiClient = new GoogleApiClient.Builder(this)
                .addApi(LocationServices.API)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build();
    }

    @OnClick(R.id.parkingReservation)
    public void reservation(View view) {
        Toast.makeText(MapsActivity.this, "I couldn't finish this :'(", Toast.LENGTH_SHORT).show();
    }


    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        checkLocationandAddToMap();
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    public void onStart() {
        super.onStart();
        googleApiClient.connect();
    }

    public void onStop() {
        super.onStop();
        googleApiClient.disconnect();
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case LOCATION_REQUEST_CODE:
                if (grantResults.length > 0 && grantResults[0] == PERMISSION_GRANTED) {
                    checkLocationandAddToMap();
                } else
                    Toast.makeText(this, "Location Permission Denied", Toast.LENGTH_SHORT).show();
                break;
        }
    }

    /**
     * Checking if the user has granted the permission
     * Requesting the Location permission
     * Fetching the last known location using the Fus
     * MarkerOptions are used to create a new Marker.You can specify location, title etc with MarkerOptions
     * Adding the created the marker on the map
     **/
    private void checkLocationandAddToMap() {

        try {
            if (ActivityCompat.checkSelfPermission(MapsActivity.this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(MapsActivity.this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(MapsActivity.this, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION}, LOCATION_REQUEST_CODE);
                return;
            }
            Location location = FusedLocationApi.getLastLocation(googleApiClient);
            MarkerOptions markerOptions = new MarkerOptions().position(new LatLng(location.getLatitude(), location.getLongitude())).title("You are here bro");
            mMap.addMarker(markerOptions);

            CameraUpdate myLocation = CameraUpdateFactory.newLatLngZoom(markerOptions.getPosition(), 11);
            mMap.animateCamera(myLocation);
        } catch (Exception e) {
            Toast.makeText(MapsActivity.this, "Please Turn on your GPS", Toast.LENGTH_LONG).show();
        }
    }

    //Calling network
    private void doNetworkCall() {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("http://ridecellparking.herokuapp.com")
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .build();

        ApiObservableParkingService apiService = retrofit.create(ApiObservableParkingService.class);
        apiService.getData()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<List<ParkingResponse>>() {

                               @Override
                               public void accept(List<ParkingResponse> parkingResponses) throws Exception {
                                   checkLocationandAddToMap();

                                   try {
                                       mMap.clear();
                                       mMap.setInfoWindowAdapter(new CustomInfoWindowAdapter(MapsActivity.this));
                                       // This loop will go through all the data and add marker on each location.
                                       for (int i = 0; i < parkingResponses.size(); i++) {
                                           Double lat = Double.valueOf(parkingResponses.get(i).getLat());
                                           Double lng = Double.valueOf(parkingResponses.get(i).getLng());

                                           if (parkingResponses.get(i).getIsReserved() == true) {

                                               String placeName = parkingResponses.get(i).getName();
                                               String snippet = "Reserved Until " + parkingResponses.get(i).getReservedUntil() + "\n" +
                                                       "Reservation Information" + "\n" +
                                                       "Minimum Time  " + " Maximum Time   " + "Cost" + "\n" +
                                                       parkingResponses.get(i).getMinReserveTimeMins() + " Minutes" + "          " +
                                                       parkingResponses.get(i).getMaxReserveTimeMins() + " Minutes" + "         " +
                                                       "$" + parkingResponses.get(i).getCostPerMinute() + " per ";

                                               MarkerOptions markerOptions = new MarkerOptions();
                                               LatLng latLng = new LatLng(lat, lng);
                                               markerOptions.position(latLng);
                                               markerOptions.title(placeName);
                                               markerOptions.snippet(snippet);
                                               markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED));
                                               Marker m = mMap.addMarker(markerOptions);
                                               mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
                                               mMap.animateCamera(CameraUpdateFactory.zoomTo(15));

                                           } else if (parkingResponses.get(i).getIsReserved() == false) {

                                               String placeName = parkingResponses.get(i).getName();
                                               String snippet = "Not Reserved , Tab to Reserve" + "\n" +
                                                       "Minimum Time  " + " Maximum Time   " + "Cost" + "\n" +
                                                       parkingResponses.get(i).getMinReserveTimeMins() + " Minutes" + "          " +
                                                       parkingResponses.get(i).getMaxReserveTimeMins() + " Minutes" + "         " +
                                                       "$" + parkingResponses.get(i).getCostPerMinute() + " per ";

                                               MarkerOptions markerOptions = new MarkerOptions();
                                               LatLng latLng = new LatLng(lat, lng);
                                               markerOptions.position(latLng);
                                               markerOptions.title(placeName);
                                               markerOptions.snippet(snippet);
                                               markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN));
                                               mMap.addMarker(markerOptions);
                                               mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
                                               mMap.animateCamera(CameraUpdateFactory.zoomTo(15));
                                               mMap.setOnInfoWindowClickListener(MapsActivity.this);
                                           }
                                       }
                                   } catch (Exception e) {
                                       Log.d("onResponse", "There is an error");
                                       e.printStackTrace();
                                   }
                                   Toast.makeText(MapsActivity.this, "Available Parking Shown in Green Marker", Toast.LENGTH_SHORT).show();
                               }
                           }
                        , new Consumer<Throwable>() {
                            @Override
                            public void accept(Throwable throwable) throws Exception {
                                //  printMessage(throwable.getMessage());
                            }
                        });
    }

    @OnClick(R.id.search)
    public void searchParking(View view) {
        doNetworkCall();
        Toast.makeText(getApplicationContext(), "Parking Location Loading ...", Toast.LENGTH_SHORT).show();
    }

    /**When user click on the info window Push notification
     * will be confirmation of reservation will be send to
     * the user
     **/
    @Override
    public void onInfoWindowClick(Marker marker) {

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://fcm.googleapis.com")
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .build();

        String token = "dZyY46ICrV4:APA91bF9i1duUTYI903mGy6bGVUR_MiCjo1f2fiIftbs8L6gVv7Z_9eMOmcxifPE7uTLbUQrzX8VKQdrQNNUAL8eJa2EmiOeLjW2d1cXY85XArYTtm2SkUKvqp_aow18kONjQyhcL6Tu";
        SendPushApi pushApi = retrofit.create(SendPushApi.class);
        Notification notification = new Notification("You have Successful Reserved Parking space" , "Reservation Confirmation");
        pushApi.sendPush(new PushRequestBody(token , notification))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<PushResponse>() {
                    @Override
                    public void accept(PushResponse pushResponse) throws Exception {
                        Toast.makeText(getApplicationContext(), "Reservation Successful", Toast.LENGTH_SHORT).show();
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        Toast.makeText(getApplicationContext(),throwable.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });


//        Retrofit retro = new Retrofit.Builder()
//                .baseUrl("http://ridecellparking.herokuapp.com")
//                .addConverterFactory(GsonConverterFactory.create())
//                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
//                .build();
//
//        ApiObservableParkingService apiService = retro.create(ApiObservableParkingService.class);
//        apiService.reserveParking()
//                .subscribeOn(Schedulers.io())
//                .observeOn(AndroidSchedulers.mainThread())
//                .subscribe(new Consumer<List<ParkingResponse>>() {
//
//                               @Override
//                              public void accept(List<ParkingResponse> parkingResponses) throws Exception {
//                                Toast.makeText(MapsActivity.this, "Successfully Reserved" + parkingResponses.get(i).getId() , Toast.LENGTH_SHORT).show();
//                               }

    }

    public void DatabaseNetworkCall(){


    }

    //Realm Config
    private Realm addNewObject() {

        Realm.init(this);
        RealmConfiguration config = new RealmConfiguration
                .Builder()
                .name(Realm.DEFAULT_REALM_NAME)
                .schemaVersion(0)
                .deleteRealmIfMigrationNeeded()
                .build();
        Realm.setDefaultConfiguration(config);
        realm = Realm.getInstance(config);
        return realm;
    }

    public void loadFromDatabase() {

        RealmResults<ParkingResponse> realmResults = realm.where(ParkingResponse.class).findAllAsync();
        realmResults.load();
        myAdapter.addSpaces(realmResults);
        final String text = " Found " + myAdapter.getItemCount() + " Results";
        Toast.makeText(MapsActivity.this, text, Toast.LENGTH_SHORT).show();

    }
    

}
