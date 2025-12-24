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

import com.amap.api.maps.CameraUpdateFactory;
import com.amap.api.maps.model.LatLng;
import com.amap.api.maps.model.MarkerOptions;
import com.amap.api.maps.model.BitmapDescriptorFactory;

public class MapFragment extends Fragment {

    private MapView mapView;
    private AMap aMap;
    private FloatingActionButton fabLocation;
    private static final int PERMISSION_REQUEST_CODE = 100;

    // 杭州师范大学仓前校区中心坐标
    private static final LatLng HZNU_CANGQIAN = new LatLng(30.295487, 120.004844);

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        MapsInitializer.updatePrivacyShow(requireContext(), true, true);
        MapsInitializer.updatePrivacyAgree(requireContext(), true);
        MapsInitializer.setApiKey("2cc14774025f2fb3b2fa3fc5fca6ab45");
        
        View view = inflater.inflate(R.layout.fragment_map, container, false);
        mapView = view.findViewById(R.id.map_view);
        fabLocation = view.findViewById(R.id.fab_location);

        mapView.onCreate(savedInstanceState);
        initMap();

        // 点击按钮回到校园中心
        fabLocation.setOnClickListener(v -> {
            aMap.animateCamera(CameraUpdateFactory.newLatLngZoom(HZNU_CANGQIAN, 16f));
            Toast.makeText(getContext(), "已回到仓前校区", Toast.LENGTH_SHORT).show();
        });

        return view;
    }

    private void initMap() {
        if (aMap == null) {
            aMap = mapView.getMap();
        }

        // 设置默认缩放级别和中心点
        aMap.moveCamera(CameraUpdateFactory.newLatLngZoom(HZNU_CANGQIAN, 16f));
        
        // 显示室内地图（如果高德支持该区域）
        aMap.showIndoorMap(true);
        aMap.getUiSettings().setZoomControlsEnabled(true);
        aMap.getUiSettings().setCompassEnabled(true);

        addCampusMarkers();
    }

    private void addCampusMarkers() {
        // 添加校园关键地点
        addMarker(30.2965, 120.0060, "杭师大图书馆", "仓前校区核心地标");
        addMarker(30.2940, 120.0035, "第一餐厅", "美食聚集地");
        addMarker(30.2980, 120.0020, "恕园教学楼", "日常上课地点");
        addMarker(30.2930, 120.0080, "体育场", "运动健身区");
    }

    private void addMarker(double lat, double lng, String title, String snippet) {
        MarkerOptions options = new MarkerOptions();
        options.position(new LatLng(lat, lng));
        options.title(title);
        options.snippet(snippet);
        aMap.addMarker(options);
    }

    private void checkPermissionAndLocate() {
        // 模拟器下此功能受限，保留作为真实手机使用
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