package com.example.fernana6.encuentramicoche;

import android.content.Context;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.mapbox.mapboxsdk.geometry.LatLng;
import com.mapbox.mapboxsdk.geometry.LatLngBounds;



public abstract class LocationAwareActivity
        extends AppCompatActivity
        implements LocationListener {

    public static final LatLng KM0 = new LatLng(40.41855, -3.6916800000000194);
    public static final LatLngBounds DEFAULT_BOUNDS =
            new LatLngBounds.Builder().include(new LatLng(43.79, 4.32))
                    .include(new LatLng(27.6355, -18.16)).build();


    private static final String STATE_ENABLE_LOCATION = "STATE_ENABLE_LOCATION";
    // Default minimum time interval between updates
    private static final long MIN_TIME = 5 * 1000;
    // Default minimum distance between updates
    private static final float MIN_DISTANCE = 10;


    protected boolean mEnableLocation;
    protected Location mNewLocation;
    protected Location mOldLocation;
    private LocationManager mLocationManager;
    private boolean mListeningToLocationUpdates;
    private boolean mIsPermissionAlreadyDenied;


    /**
     * @return whether or not location updates should be enabled on activity launch
     */
    protected abstract boolean isEnableLocationOnLaunch();

    /**
     * Location changed callback
     */
    protected abstract void onLocationChanged();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (savedInstanceState == null) {
            mEnableLocation = isEnableLocationOnLaunch();
        } else {
            mEnableLocation = savedInstanceState.getBoolean(STATE_ENABLE_LOCATION, true);
        }
        mLocationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

    }


    @Override
    protected void onResume() {
        super.onResume();

        enableLocation(mEnableLocation);
    }


    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putSerializable(STATE_ENABLE_LOCATION, mEnableLocation);

        super.onSaveInstanceState(outState);
    }


    @Override
    protected void onPause() {
        super.onPause();

        enableLocation(false);
    }

//
//    @Override
//    public void onRequestPermissionsResult(final int requestCode,
//                                           @NonNull final String permissions[],
//                                           @NonNull final int[] grantResults) {
//        if (requestCode == PermissionUtil.PERMISSIONS_REQUEST_LOCATION && grantResults.length > 0) {
//            final int grantResult = grantResults[0];
//            mIsPermissionAlreadyDenied = (grantResult == PackageManager.PERMISSION_DENIED);
//        }
//    }


    @Override
    public final void onLocationChanged(Location location) {
        if (LocationUtil.isBetterLocation(location, mNewLocation)) {
            mNewLocation = location;

            onLocationChanged();
        }
    }


    @Override
    public void onStatusChanged(final String provider,
                                final int status,
                                final Bundle extras) {
    }


    @Override
    public void onProviderEnabled(final String provider) {
    }


    @Override
    public void onProviderDisabled(final String provider) {
    }


    private void enableLocation(final boolean enabled) {
        if (enabled) {
//            if (PermissionUtil.checkPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)) {
//                if (!LocationUtil.isLocationEnabled(mLocationManager)) {
//                    requestEnablingLocationFeature();
//                }
//                else {
//                    final Criteria criteria = new Criteria();
//                    criteria.setAccuracy(Criteria.ACCURACY_FINE);
//                    criteria.setBearingRequired(false);
//                    criteria.setSpeedRequired(false);
//                    criteria.setCostAllowed(false);
//                    mLocationManager.requestLocationUpdates(
//                            getMinTime(), getMinDistance(), criteria, this, Looper.getMainLooper());
//                    mListeningToLocationUpdates = true;
//                    mLocation = LocationUtil.getLastKnownLocation(this, mLocationManager);
//                    if (mLocation != null) {
//                        onLocationChanged();
//                    }
//                }
//            }
//            else if (mIsPermissionAlreadyDenied) {
//                if (PermissionUtil.shouldShowRequestPermissionRationale(this,
//                                                                        Manifest.permission.ACCESS_FINE_LOCATION)) {
//                    showRequestLocationPermissionRationale();
//                }
//                else {
//                    showFeatureWontWork();
//                }
//            }
//            else {
//                PermissionUtil.requestPermission(this, Manifest.permission.ACCESS_FINE_LOCATION);
//            }
        } else {
            mLocationManager.removeUpdates(this);
            mListeningToLocationUpdates = false;
        }
    }

//
//    private void requestEnablingLocationFeature() {
//        new AlertDialog.Builder(this) //
//                                      .setMessage(R.string.enable_location) //
//                                      .setCancelable(false) //
//                                      .setPositiveButton(R.string.settings,
//                                                         new DialogInterface.OnClickListener() {
//                                                             @Override
//                                                             public void onClick(final DialogInterface dialog,
//                                                                                 final int id) {
//                                                                 dialog.dismiss();
//                                                                 final Intent intent = new Intent(
//                                                                         Settings.ACTION_LOCATION_SOURCE_SETTINGS);
//                                                                 LocationAwareActivity.this
//                                                                         .startActivity(intent);
//                                                             }
//                                                         }) //
//                                      .setNegativeButton(android.R.string.cancel,
//                                                         new DialogInterface.OnClickListener() {
//                                                             @Override
//                                                             public void onClick(DialogInterface dialog,
//                                                                                 int i) {
//                                                                 dialog.dismiss();
//                                                             }
//                                                         })
//                                      .create().show();
//    }
//

    /**
     * @return whether or not the activity is listening to location updates
     */
    protected boolean isListeningToLocationUpdates() {
        return mListeningToLocationUpdates;
    }


    /**
     * @return minimum time interval between location updates (milliseconds)
     */
    protected long getMinTime() {
        return MIN_TIME;
    }


    /**
     * @return minimum distance between location updates (meters)
     */
    protected float getMinDistance() {
        return MIN_DISTANCE;
    }

//
//    protected void showRequestLocationPermissionRationale() {
//        Log.w(LocationAwareActivity.class.getSimpleName(),
//              "showRequestLocationPermissionRationale");
//    }
//

    protected void showFeatureWontWork() {
        Log.w(LocationAwareActivity.class.getSimpleName(),
                "showFeatureWontWork");
    }


    public Location getLocation() {

        //     if (mLocation == null) {
        if(LocationUtil.getLastKnownLocation(LocationAwareActivity.this, mLocationManager) == null ){
            // Si no tenemos acceso al GPS en absoluto, devolvemos siempre el KMO.
            return null;
        }else {
            mNewLocation = LocationUtil.getLastKnownLocation(LocationAwareActivity.this, mLocationManager);
        }
        //   }

        askLocationChanged(mNewLocation);
        return mNewLocation;
    }

    private void askLocationChanged(Location location) {
        mNewLocation = location;
        Log.e("Locationchaned ", "askLocationChanged(): locationchang: " + location.getLatitude() + "," + location.getLongitude());
        //comparar la location con una anterior y enviarla si ha cambiado. si es null, se manda siempre
        if (mOldLocation != null) {
            Log.e("Location", "askLocationChanged(): oldLocation "+mOldLocation.toString());
        }
        if (mNewLocation != null) {
            Log.e("Location", "askLocationChanged(): newLocation"+mNewLocation.toString());
        }
        if (mOldLocation == null || mOldLocation == mNewLocation) {
            mOldLocation = location;
            Log.e("Location", "askLocationChanged()");
        }
    }
    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(newBase);
    }
}
