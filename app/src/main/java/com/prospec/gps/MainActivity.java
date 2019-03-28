package com.prospec.gps;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {


    private LocationManager locationManager;  //1
    private Criteria criteria;//2
    private double latDouble, longDouble;
    //เมื่อมีการเปลี่ยนพิกัด class นี้จะทำงาน //4
    public LocationListener locationListener = new LocationListener() {
        @Override

        public void onLocationChanged(Location location) {
            //5
            latDouble = location.getLatitude();
            longDouble = location.getLongitude();
        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {

        }

        @Override
        public void onProviderEnabled(String provider) {

        }

        @Override
        public void onProviderDisabled(String provider) {

        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        if (!checkIfAlreadyhavePermission()) {
            requestForSpecificPermission();
        }


        //setup Location //3
        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        criteria = new Criteria();
        criteria.setAccuracy(Criteria.ACCURACY_FINE);
        criteria.setAltitudeRequired(false);
        criteria.setBearingRequired(false); //ไม่หาพิกัดความสูงจากระดับน้ำทะเล


    }//main

    //11
    //method นี้จะทำงานเมื่อหลังจากพัก aPP แล้วกลับเข้ามาใหม่
    @Override
    protected void onResume() {
        super.onResume();

        //หาพิกัดจาก network
        Location networkLocation = myFindLocation(LocationManager.NETWORK_PROVIDER);
        if (networkLocation != null) {
            latDouble = networkLocation.getLatitude();
            longDouble = networkLocation.getLongitude();
        }

        //หาพิกัดจาก PGS Card
        Location gpsLocation = myFindLocation(LocationManager.GPS_PROVIDER);
        if (gpsLocation != null) {
            latDouble = gpsLocation.getLatitude();
            longDouble = gpsLocation.getLongitude();
        }

        showView();

    }

    private void showView() {
        TextView latTextView = findViewById(R.id.txtLat);
        TextView longTextView = findViewById(R.id.txtLong);

        latTextView.setText("Lat : " + Double.toString(latDouble));
        longTextView.setText("Long : " + Double.toString(longDouble));
    }

    //10
    //method นี้ทำงานเมื่อปิด app
    @Override
    protected void onStop() {
        super.onStop();
        locationManager.removeUpdates(locationListener);

    }

    //6
    public Location myFindLocation(String strProvider) {

        Location location = null;
        //เช็คการ์ด GPS
        if (locationManager.isProviderEnabled(strProvider)) {

            //8
            //Check permission ได้มาจากกด alt + enter เลือก add permission จากการส่งตรง locationManager.requestLocationUpdates(strProvider, 1000, 10, locationListener);
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                // TODO: Consider calling
                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.
                return null;
            }
            //7
            locationManager.requestLocationUpdates(strProvider, 1000, 10, locationListener);
            //9
            location = locationManager.getLastKnownLocation(strProvider);
        }
        return location;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case 101:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    //granted
                } else {
                    //not granted
                }
                break;
            default:
                super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }

    //What is permission be request
    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    private void requestForSpecificPermission() {
        ActivityCompat.requestPermissions(this, new String[]{
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.INTERNET,
                Manifest.permission.CAMERA,
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.BLUETOOTH,
                Manifest.permission.READ_PHONE_STATE,
                Manifest.permission.READ_CALL_LOG,
                Manifest.permission.READ_CONTACTS}, 101);

    }

    //Check the permission is already have
    private boolean checkIfAlreadyhavePermission() {

        int result = ContextCompat.checkSelfPermission(this,
                Manifest.permission.GET_ACCOUNTS);
        return result == PackageManager.PERMISSION_GRANTED;
    }

}