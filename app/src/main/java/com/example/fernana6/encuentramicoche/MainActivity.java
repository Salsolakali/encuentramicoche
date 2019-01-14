package com.example.fernana6.encuentramicoche;


import android.Manifest;
import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionRequestErrorListener;
import com.karumi.dexter.listener.multi.CompositeMultiplePermissionsListener;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;
import com.karumi.dexter.listener.multi.SnackbarOnAnyDeniedMultiplePermissionsListener;
import com.karumi.dexter.listener.single.CompositePermissionListener;
import com.karumi.dexter.listener.single.DialogOnDeniedPermissionListener;
import com.karumi.dexter.listener.single.PermissionListener;
import com.karumi.dexter.listener.single.SnackbarOnDeniedPermissionListener;
import com.mapbox.mapboxsdk.Mapbox;
import com.mapbox.mapboxsdk.camera.CameraPosition;
import com.mapbox.mapboxsdk.camera.CameraUpdateFactory;
import com.mapbox.mapboxsdk.geometry.LatLng;
import com.mapbox.mapboxsdk.maps.MapView;
import com.mapbox.mapboxsdk.maps.MapboxMap;
import com.mapbox.mapboxsdk.maps.Style;

public class MainActivity extends LocationAwareActivity
        implements NavigationView.OnNavigationItemSelectedListener {


    private MultiplePermissionsListener allPermissionsListener;
    private PermissionListener cameraPermissionListener;
    private PermissionListener contactsPermissionListener;
    private PermissionListener audioPermissionListener;
    private PermissionRequestErrorListener errorListener;
    private MapboxMap mapboxMap;
    private MapView mapView;

    @Override
    protected boolean isEnableLocationOnLaunch() {
        return false;
    }

    @Override
    protected void onLocationChanged() {

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        createPermissionListeners();
        Mapbox.getInstance(this, "pk.eyJ1Ijoic2Fsc29sYWthbGkiLCJhIjoiY2pxcWQwcDk4MDEwMTQzcGt0eG5hZTU2NyJ9.M6GJbBtHc_JbIDm_Pn0a4g");
        LocationAwareActivity locationActivity = new LocationAwareActivity() {
            @Override
            protected boolean isEnableLocationOnLaunch() {
                return false;
            }

            @Override
            protected void onLocationChanged() {
                updateMap();
            }
        };
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);



        mapView = findViewById(R.id.mapView);
        mapView.onCreate(savedInstanceState);
        updateMap();

    }
    private void createPermissionListeners() {
        PermissionListener feedbackViewPermissionListener = new SamplePermissionListener(this);
        MultiplePermissionsListener feedbackViewMultiplePermissionListener =
                new SampleMultiplePermissionListener(this);

        allPermissionsListener =
                new CompositeMultiplePermissionsListener(feedbackViewMultiplePermissionListener,
                                                         SnackbarOnAnyDeniedMultiplePermissionsListener.Builder.with(mapView,
                                                         R.string.all_permissions_denied_feedback)
                    .withOpenSettingsButton(R.string.permission_rationale_settings_button_text)
                    .build());
        contactsPermissionListener = new CompositePermissionListener(feedbackViewPermissionListener,
                                                                     SnackbarOnDeniedPermissionListener.Builder.with(mapView,
                                                                     R.string.contacts_permission_denied_feedback)
                .withOpenSettingsButton(R.string.permission_rationale_settings_button_text)
                .withCallback(new Snackbar.Callback() {
            @Override public void onShown(Snackbar snackbar) {
                super.onShown(snackbar);
            }

            @Override public void onDismissed(Snackbar snackbar, int event) {
                super.onDismissed(snackbar, event);
            }
        })
                .build());

        PermissionListener dialogOnDeniedPermissionListener =
                DialogOnDeniedPermissionListener.Builder.withContext(this)
                        .withTitle(R.string.audio_permission_denied_dialog_title)
                        .withMessage(R.string.audio_permission_denied_dialog_feedback)
                        .withButtonText(android.R.string.ok)
                        .withIcon(R.mipmap.ic_logo_karumi)
                        .build();
        audioPermissionListener = new CompositePermissionListener(feedbackViewPermissionListener,
                                                                  dialogOnDeniedPermissionListener);
        cameraPermissionListener = new SampleBackgroundThreadPermissionListener(this);

        errorListener = new SampleErrorListener();
    }

    private void updateMap() {
        Location location = new Location(LocationManager.GPS_PROVIDER);
        double latitude  = location.getLatitude();
        double longitude = location.getLongitude();
        mapView.getMapAsync(mapboxMap ->
                mapboxMap.setStyle(Style.MAPBOX_STREETS, new Style.OnStyleLoaded() {
                    @Override
                    public void onStyleLoaded(@NonNull Style style) {
                        MainActivity.this.mapboxMap = mapboxMap;
                        //enableLocationComponent();
                        CameraPosition position = new CameraPosition.Builder()
                                .target(new LatLng(latitude, longitude)) // Sets the new camera position
                                .zoom(17) // Sets the zoom
                                .bearing(180) // Rotate the camera
                                .tilt(30) // Set the camera tilt
                                .build(); // Creates a CameraPosition from the builder

                        mapboxMap.animateCamera(CameraUpdateFactory
                                .newCameraPosition(position), 7000);



                    }
                })
        );
    }

    /*@SuppressWarnings( {"MissingPermission"})
    private void enableLocationComponent() {
        // Check if permissions are enabled and if not request
        if (PermissionsManager.areLocationPermissionsGranted(this)) {

            // Get an instance of the component
            LocationComponent locationComponent = mapboxMap.getLocationComponent();

            // Activate
            locationComponent.activateLocationComponent(getApplicationContext(), mapboxMap.getStyle());

            // Enable to make component visible
            locationComponent.setLocationComponentEnabled(true);

            // Set the component's camera mode
            locationComponent.setCameraMode(CameraMode.TRACKING);

            // Set the component's render mode
            locationComponent.setRenderMode(RenderMode.COMPASS);

        } else {
            permissionsManager = new PermissionsManager(this.permissionsManager.getListener());
            permissionsManager.requestLocationPermissions(this);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        permissionsManager.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }*/

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR1)
    public void showPermissionRationale(final PermissionToken token) {
        new AlertDialog.Builder(this).setTitle(R.string.permission_rationale_title)
                .setMessage(R.string.permission_rationale_message)
                .setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
                    @Override public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        token.cancelPermissionRequest();
                    }
                })
                .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                    @Override public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        token.continuePermissionRequest();
                    }
                })
                .setOnDismissListener(new DialogInterface.OnDismissListener() {
                    @Override public void onDismiss(DialogInterface dialog) {
                        token.cancelPermissionRequest();
                    }
                })
                .show();
    }

    public void showPermissionGranted(String permission) {
        TextView feedbackView = getFeedbackViewForPermission(permission);
        feedbackView.setText(R.string.permission_granted_feedback);
        feedbackView.setTextColor(ContextCompat.getColor(this, R.color.permission_granted));
    }

    public void showPermissionDenied(String permission, boolean isPermanentlyDenied) {
        TextView feedbackView = getFeedbackViewForPermission(permission);
        feedbackView.setText(isPermanentlyDenied ? R.string.permission_permanently_denied_feedback
                : R.string.permission_denied_feedback);
        feedbackView.setTextColor(ContextCompat.getColor(this, R.color.permission_denied));
    }


    private TextView getFeedbackViewForPermission(String name) {
        TextView feedbackView;

        switch (name) {
            case Manifest.permission.CAMERA:
                feedbackView = cameraPermissionFeedbackView;
                break;
            case Manifest.permission.READ_CONTACTS:
                feedbackView = contactsPermissionFeedbackView;
                break;
            case Manifest.permission.RECORD_AUDIO:
                feedbackView = audioPermissionFeedbackView;
                break;
            default:
                throw new RuntimeException("No feedback view for this permission");
        }

        return feedbackView;
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_camera) {
            // Handle the camera action
        } else if (id == R.id.nav_gallery) {

        } else if (id == R.id.nav_slideshow) {

        } else if (id == R.id.nav_manage) {

        } else if (id == R.id.nav_share) {

        } else if (id == R.id.nav_send) {

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }



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
    protected void onStop() {
        super.onStop();
        mapView.onStop();
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
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        mapView.onSaveInstanceState(outState);
    }
}
