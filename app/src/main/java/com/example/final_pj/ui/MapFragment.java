package com.example.final_pj.ui;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import com.amap.api.maps.AMap;
import com.amap.api.maps.MapView;
import com.amap.api.maps.MapsInitializer;
import com.amap.api.maps.model.MyLocationStyle;
import com.example.final_pj.R;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class MapFragment extends Fragment {

    private MapView mapView;
    private AMap aMap;
    private FloatingActionButton fabLocation;
    private static final int PERMISSION_REQUEST_CODE = 100;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // Privacy policy requirement for Amap SDK 8.1.0+
        MapsInitializer.updatePrivacyShow(requireContext(), true, true);
        MapsInitializer.updatePrivacyAgree(requireContext(), true);
        
        // Set Security Key (安全密钥)
        // 注意：这里传入的是安全密钥 2cc14774025f2fb3b2fa3fc5fca6ab45，不是 API Key
        MapsInitializer.setApiKey("2cc14774025f2fb3b2fa3fc5fca6ab45");
        
        View view = inflater.inflate(R.layout.fragment_map, container, false);
        mapView = view.findViewById(R.id.map_view);
        fabLocation = view.findViewById(R.id.fab_location);

        // Required by AMap
        mapView.onCreate(savedInstanceState);

        initMap();

        fabLocation.setOnClickListener(v -> checkPermissionAndLocate());

        return view;
    }

    private void initMap() {
        if (aMap == null) {
            aMap = mapView.getMap();
        }

        // Setup Blue dot style
        MyLocationStyle myLocationStyle = new MyLocationStyle();
        myLocationStyle.myLocationType(MyLocationStyle.LOCATION_TYPE_LOCATION_ROTATE_NO_CENTER);
        myLocationStyle.interval(2000); // 2 seconds
        aMap.setMyLocationStyle(myLocationStyle);
        
        aMap.getUiSettings().setMyLocationButtonEnabled(false); // We use custom FAB
        aMap.getUiSettings().setZoomControlsEnabled(true);
    }

    private void checkPermissionAndLocate() {
        if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, PERMISSION_REQUEST_CODE);
        } else {
            startLocation();
        }
    }

    private void startLocation() {
        aMap.setMyLocationEnabled(true);
        // Move camera to current location once
        aMap.getMapScreenShot(null); // Just to wake it up
        Toast.makeText(getContext(), "正在定位...", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                startLocation();
            } else {
                Toast.makeText(getContext(), "需要定位权限才能显示您的位置", Toast.LENGTH_SHORT).show();
            }
        }
    }

    // Lifecycle methods required by MapView
    @Override
    public void onResume() {
        super.onResume();
        mapView.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
        mapView.onPause();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mapView.onDestroy();
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        mapView.onSaveInstanceState(outState);
    }
}