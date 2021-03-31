package com.example.covidtrackmonitor;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.BitmapFactory;
import android.location.Location;
import android.os.Bundle;
import android.os.Message;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.firebase.ui.auth.data.model.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.mapbox.android.core.location.LocationEngine;
import com.mapbox.android.core.location.LocationEngineProvider;
import com.mapbox.android.core.permissions.PermissionsListener;
import com.mapbox.android.core.permissions.PermissionsManager;
import com.mapbox.api.directions.v5.models.DirectionsResponse;
import com.mapbox.api.directions.v5.models.DirectionsRoute;
import com.mapbox.geojson.Feature;
import com.mapbox.geojson.Point;
import com.mapbox.mapboxsdk.Mapbox;
import com.mapbox.mapboxsdk.annotations.MarkerOptions;
import com.mapbox.mapboxsdk.camera.CameraUpdateFactory;
import com.mapbox.mapboxsdk.geometry.LatLng;
import com.mapbox.mapboxsdk.location.LocationComponent;
import com.mapbox.mapboxsdk.maps.MapView;
import com.mapbox.mapboxsdk.maps.MapboxMap;
import com.mapbox.mapboxsdk.maps.OnMapReadyCallback;
import com.mapbox.mapboxsdk.maps.Style;
import com.mapbox.mapboxsdk.plugins.locationlayer.LocationLayerPlugin;
import com.mapbox.mapboxsdk.plugins.locationlayer.modes.CameraMode;
import com.mapbox.mapboxsdk.plugins.locationlayer.modes.RenderMode;
import com.mapbox.mapboxsdk.style.layers.SymbolLayer;
import com.mapbox.mapboxsdk.style.sources.GeoJsonSource;
import com.mapbox.services.android.navigation.ui.v5.NavigationLauncher;
import com.mapbox.services.android.navigation.ui.v5.NavigationLauncherOptions;
import com.mapbox.services.android.navigation.ui.v5.route.NavigationMapRoute;
import com.mapbox.services.android.navigation.v5.navigation.NavigationRoute;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.mapbox.mapboxsdk.style.layers.PropertyFactory.iconAllowOverlap;
import static com.mapbox.mapboxsdk.style.layers.PropertyFactory.iconIgnorePlacement;
import static com.mapbox.mapboxsdk.style.layers.PropertyFactory.iconImage;

public class NaviguationActivity extends AppCompatActivity implements OnMapReadyCallback, MapboxMap.OnMapClickListener, PermissionsListener {
    private MapView mapView;
    private MapboxMap map;
    private PermissionsManager permissionsManager;
    private LocationComponent locationComponent;
    private Location originLocation;
    private FirebaseAuth auth;
    private DatabaseReference reference,usersReference;
    private FirebaseUser user;
    private DirectionsRoute currentRoute;
    private NavigationMapRoute navigationMapRoute;
    private MenuItem item;
    private ArrayList<CreateUser> listecontacts;
    private String circleid;
    private CreateUser createUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Mapbox.getInstance(this, getString(R.string.acces_token));
        setContentView(R.layout.activity_naviguation);
        mapView = (MapView) findViewById(R.id.mapView);
        mapView.onCreate(savedInstanceState);
        mapView.getMapAsync(this);
        }
    //reinitialiser menu
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater=getMenuInflater();
        inflater.inflate(R.menu.menu,menu);
        item= (MenuItem) findViewById(R.id.infecte);
        return true;
    }
    //reinitialiser menu
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){
            case R.id.infecte:
                Intent intentinfecte=new Intent(NaviguationActivity.this,NotifyActivity.class);
                startActivity(intentinfecte);

                return true;
            case R.id.cercle:
                Intent intentcircle=new Intent(NaviguationActivity.this,CirclesActivity.class);
                startActivity(intentcircle);
                return true;

            case R.id.joincercle:
                Intent intentjoin=new Intent(NaviguationActivity.this,JoinCircleActivity.class);
                startActivity(intentjoin);
                return true;

            case R.id.invite:
                Intent intent=new Intent(NaviguationActivity.this,CodeActivity.class);
                startActivity(intent);
                return true;

            case R.id.signout:
                FirebaseUser user=auth.getCurrentUser();
                if(user !=null){
                    auth.signOut();
                    finish();
                    Intent myIntent= new Intent(NaviguationActivity.this,MainActivity.class);
                    startActivity(myIntent);
            }
                return true;

        }
        return super.onOptionsItemSelected(item);
    }

    public void startNavigationBtnClick(View v) {
        boolean simulateRoute = true;
        NavigationLauncherOptions options = NavigationLauncherOptions.builder()
                .directionsRoute(currentRoute)
                .shouldSimulateRoute(simulateRoute)
                .build();
        NavigationLauncher.startNavigation(NaviguationActivity.this, options);
    }

    @Override
    public void onMapReady(MapboxMap mapboxMap) {
        map = mapboxMap;
        map.setMinZoomPreference(10);
        map.setStyle(getString(R.string.navigation_guidance_day), new Style.OnStyleLoaded() {
            @Override
            public void onStyleLoaded(@NonNull Style style) {
                enableLocation(style);
                addDestinationIconLayer(style);
                map.addOnMapClickListener(NaviguationActivity.this);
            }
        });
        auth=FirebaseAuth.getInstance();
        user=auth.getCurrentUser();
        usersReference= FirebaseDatabase.getInstance().getReference().child("users");
        listecontacts= new ArrayList<>();
        reference= FirebaseDatabase.getInstance().getReference().child("users").child(user.getUid()).child("CircleMembers");

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                listecontacts.clear();

                if(snapshot.exists()){
                    for(DataSnapshot snap: snapshot.getChildren())
                    {
                        circleid=snap.child("circlememberid").getValue(String.class);
                        // now we fetch the circleid
                        usersReference.child(circleid)
                                .addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                                        createUser=snapshot.getValue(CreateUser.class);
                                        listecontacts.add(createUser);
                                        //show all contatcts positions on map
                                        MarkerOptions options=new MarkerOptions();
                                        options.title(createUser.name);
                                        Double lat=Double.parseDouble(createUser.lat);
                                        Double lng=Double.parseDouble(createUser.lng);

                                        options.position(new LatLng(lat, lng));
                                        mapboxMap.addMarker(options);
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError error) {
                                        Toast.makeText(getApplicationContext(),error.getMessage(),Toast.LENGTH_SHORT).show();
                                    }
                                });
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(getApplicationContext(),"erreur",Toast.LENGTH_SHORT).show();
            }
        });

    }

    private void addDestinationIconLayer(Style style) {
        style.addImage("destination-icon-id",
                BitmapFactory.decodeResource(this.getResources(), R.drawable.mapbox_marker_icon_default));

        GeoJsonSource geoJsonSource = new GeoJsonSource("destination-source-id");
        style.addSource(geoJsonSource);
        SymbolLayer destinationSymbolLayer = new SymbolLayer("destination-symbol-layer-id", "destination-source-id");
        destinationSymbolLayer.withProperties(iconImage("destination-icon-id"), iconAllowOverlap(true),
                iconIgnorePlacement(true));
        style.addLayer(destinationSymbolLayer);
    }

    @SuppressLint("WrongConstant")
    private void enableLocation(Style LoadedMapStyle) {
        if (PermissionsManager.areLocationPermissionsGranted(this)) {
            locationComponent = map.getLocationComponent();
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

                return;
            }
            locationComponent.activateLocationComponent(this, LoadedMapStyle);
            locationComponent.setLocationComponentEnabled(true);
            locationComponent.setCameraMode(CameraMode.TRACKING);
            //update data base fields lat and lng to get lastKnowLocation
            reference= FirebaseDatabase.getInstance().getReference().child("users"); //pointer sur la table users
            reference.child(auth.getUid()).child("lat").setValue(String.valueOf(locationComponent.getLastKnownLocation().getLatitude()));
            reference.child(auth.getUid()).child("lng").setValue(String.valueOf(locationComponent.getLastKnownLocation().getLongitude()));
        } else {
            permissionsManager = new PermissionsManager(this);
            permissionsManager.requestLocationPermissions(this);
        }
    }


    private void setCameraPosition(Location location) {
        map.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(location.getLatitude(), location.getLongitude()), 13));
    }

    //PermissionListener
    @Override
    public void onExplanationNeeded(List<String> permissionsToExplain) {

    }

    //PermissionListener
    @Override
    public void onPermissionResult(boolean granted) {
        if (granted) {
            enableLocation(map.getStyle());
        } else {
            Toast.makeText(getApplicationContext(), "permission non fournie", Toast.LENGTH_SHORT).show();
            finish();
        }
    }

    @SuppressLint("MissingSuperCall")
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        permissionsManager.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    @SuppressLint("MissingPermission")
    @Override
    protected void onStart() {
        super.onStart();
        mapView.onStart();
    }

    @Override
    protected void onResume() {
        super.onResume();
        mapView.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mapView.onPause();
    }

    @SuppressLint("MissingPermission")
    @Override
    protected void onStop() {
        super.onStop();
        mapView.onStop();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        mapView.onSaveInstanceState(outState);
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mapView.onLowMemory();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mapView.onDestroy();
    }


    @Override
    public boolean onMapClick(@NonNull LatLng point) {
        Point destinationPoint = Point.fromLngLat(point.getLongitude(), point.getLatitude());
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
           //demander les permissions et overriding
        }
        Point originPoint = Point.fromLngLat(locationComponent.getLastKnownLocation().getLongitude(),
                locationComponent.getLastKnownLocation().getLatitude());
        GeoJsonSource source =map.getStyle().getSourceAs("destination-source-id");
        if (source !=null){
            source.setGeoJson((Feature.fromGeometry(destinationPoint)));
        }
        getRoute(originPoint,destinationPoint);
        return false;
    }

    private void getRoute(Point originPoint, Point destinationPoint) {
        NavigationRoute.builder(this)
                .accessToken(Mapbox.getAccessToken())
                .origin((originPoint))
                .destination(destinationPoint)
                .build()
                .getRoute((new Callback<DirectionsResponse>() {
                    @Override
                    public void onResponse(Call<DirectionsResponse> call, Response<DirectionsResponse> response) {
                        if(response.body()!=null && response.body().routes().size()>0){
                            currentRoute= response.body().routes().get(0);
                            if(navigationMapRoute !=null){
                                navigationMapRoute.removeRoute();
                            }else{
                                navigationMapRoute= new NavigationMapRoute(null,mapView,map,R.style.NavigationMapRoute);
                            }
                            navigationMapRoute.addRoute(currentRoute);
                        }
                    }

                    @Override
                    public void onFailure(Call<DirectionsResponse> call, Throwable t) {

                    }
                }));
    }
}