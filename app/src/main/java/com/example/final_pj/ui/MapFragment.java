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

import com.amap.api.services.geocoder.GeocodeQuery;
import com.amap.api.services.geocoder.GeocodeResult;
import com.amap.api.services.geocoder.GeocodeSearch;
import com.amap.api.services.geocoder.RegeocodeResult;
import android.widget.SearchView;
import android.widget.Button;

public class MapFragment extends Fragment implements GeocodeSearch.OnGeocodeSearchListener {

    private MapView mapView;
    private AMap aMap;
    private FloatingActionButton fabLocation;
    private SearchView searchView;
    private Button btnSearch;
    private GeocodeSearch geocoderSearch;

    private static final int PERMISSION_REQUEST_CODE = 100;
    private static final LatLng HZNU_CANGQIAN = new LatLng(30.295487, 120.004844);

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        try {
            MapsInitializer.updatePrivacyShow(requireContext(), true, true);
            MapsInitializer.updatePrivacyAgree(requireContext(), true);
        } catch (Exception e) {
            e.printStackTrace();
        }

        View view = inflater.inflate(R.layout.fragment_map, container, false);
        mapView = view.findViewById(R.id.map_view);
        fabLocation = view.findViewById(R.id.fab_location);
        searchView = view.findViewById(R.id.search_view);
        btnSearch = view.findViewById(R.id.btn_search);

        mapView.onCreate(savedInstanceState);
        initMap();

        fabLocation.setOnClickListener(v -> aMap.animateCamera(CameraUpdateFactory.newLatLngZoom(HZNU_CANGQIAN, 16f)));
        btnSearch.setOnClickListener(v -> performSearch());

        return view;
    }

    private void initMap() {
        if (aMap == null) aMap = mapView.getMap();
        
        aMap.moveCamera(CameraUpdateFactory.newLatLngZoom(HZNU_CANGQIAN, 16f));
        aMap.getUiSettings().setZoomControlsEnabled(true);
        addCampusMarkers();

        try {
            geocoderSearch = new GeocodeSearch(getContext());
            geocoderSearch.setOnGeocodeSearchListener(this);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void performSearch() {
        String query = searchView.getQuery().toString().trim();
        if (query.isEmpty()) {
            Toast.makeText(getContext(), "请输入搜索地址", Toast.LENGTH_SHORT).show();
            return;
        }
        // 第二个参数 "杭州" 用于限定城市，提高搜索准确率
        GeocodeQuery geocodeQuery = new GeocodeQuery(query, "杭州");
        geocoderSearch.getFromLocationNameAsyn(geocodeQuery);
    }

    @Override
    public void onGeocodeSearched(GeocodeResult result, int rCode) {
        if (rCode == 1000) { // 1000 is success code
            if (result != null && result.getGeocodeAddressList() != null && !result.getGeocodeAddressList().isEmpty()) {
                com.amap.api.services.geocoder.GeocodeAddress address = result.getGeocodeAddressList().get(0);
                com.amap.api.services.core.LatLonPoint latLonPoint = address.getLatLonPoint();
                LatLng latLng = new LatLng(latLonPoint.getLatitude(), latLonPoint.getLongitude());

                aMap.clear(); // 清除旧标记
                addCampusMarkers(); // 重新添加校园标记
                aMap.addMarker(new MarkerOptions().position(latLng).title(address.getFormatAddress()));
                aMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 17f));
            } else {
                Toast.makeText(getContext(), "未找到相关地址", Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(getContext(), "搜索失败, 错误码: " + rCode, Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onRegeocodeSearched(RegeocodeResult result, int rCode) {
        // Not used in this scenario
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