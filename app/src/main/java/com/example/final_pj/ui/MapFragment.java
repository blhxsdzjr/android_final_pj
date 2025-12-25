package com.example.final_pj.ui;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.SearchView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import com.amap.api.maps.AMap;
import com.amap.api.maps.CameraUpdateFactory;
import com.amap.api.maps.MapView;
import com.amap.api.maps.MapsInitializer;
import com.amap.api.maps.model.LatLng;
import com.amap.api.maps.model.MarkerOptions;
import com.amap.api.services.core.ServiceSettings;
import com.amap.api.services.geocoder.GeocodeAddress;
import com.amap.api.services.geocoder.GeocodeQuery;
import com.amap.api.services.geocoder.GeocodeResult;
import com.amap.api.services.geocoder.GeocodeSearch;
import com.amap.api.services.geocoder.RegeocodeResult;
import com.example.final_pj.R;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import java.security.MessageDigest;
import java.util.Locale;

public class MapFragment extends Fragment implements GeocodeSearch.OnGeocodeSearchListener {

    private MapView mapView;
    private AMap aMap;
    private FloatingActionButton fabLocation;
    private SearchView searchView;
    private Button btnSearch;
    private GeocodeSearch geocoderSearch;

    private static final int PERMISSION_REQUEST_CODE = 100;
    // 杭州师范大学仓前校区学术交流中心精准坐标
    private static final LatLng ACADEMIC_CENTER = new LatLng(30.2941, 120.0033);

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        Context context = requireContext();
        String apiKey = "25436b050dce6afde70ed3de11ea6876";
        
        try {
            MapsInitializer.setApiKey(apiKey);
            ServiceSettings.getInstance().setApiKey(apiKey);
            
            MapsInitializer.updatePrivacyShow(context, true, true);
            MapsInitializer.updatePrivacyAgree(context, true);
            ServiceSettings.updatePrivacyShow(context, true, true);
            ServiceSettings.updatePrivacyAgree(context, true);
        } catch (Exception e) {
            Log.e("AMapDebug", "Init error", e);
        }

        View view = inflater.inflate(R.layout.fragment_map, container, false);
        mapView = view.findViewById(R.id.map_view);
        fabLocation = view.findViewById(R.id.fab_location);
        searchView = view.findViewById(R.id.search_view);
        btnSearch = view.findViewById(R.id.btn_search);

        mapView.onCreate(savedInstanceState);
        initMap();

        fabLocation.setOnClickListener(v -> aMap.animateCamera(CameraUpdateFactory.newLatLngZoom(ACADEMIC_CENTER, 17f)));
        btnSearch.setOnClickListener(v -> performSearch());

        return view;
    }

    private void initMap() {
        if (aMap == null) aMap = mapView.getMap();
        
        // 初始镜头对准学术交流中心
        aMap.moveCamera(CameraUpdateFactory.newLatLngZoom(ACADEMIC_CENTER, 17f));
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
        // 恢复为地理编码搜索
        GeocodeQuery geocodeQuery = new GeocodeQuery(query, "杭州");
        geocoderSearch.getFromLocationNameAsyn(geocodeQuery);
    }

    @Override
    public void onGeocodeSearched(GeocodeResult result, int rCode) {
        if (rCode == 1000) {
            if (result != null && result.getGeocodeAddressList() != null && !result.getGeocodeAddressList().isEmpty()) {
                GeocodeAddress address = result.getGeocodeAddressList().get(0);
                com.amap.api.services.core.LatLonPoint latLonPoint = address.getLatLonPoint();
                LatLng latLng = new LatLng(latLonPoint.getLatitude(), latLonPoint.getLongitude());

                aMap.clear(); 
                addCampusMarkers(); 
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
    public void onRegeocodeSearched(RegeocodeResult result, int rCode) {}

    private void addCampusMarkers() {
        addMarker(30.2965, 120.0060, "杭师大图书馆", "仓前校区核心地标");
        addMarker(30.2940, 120.0035, "第一餐厅", "美食聚集地");
        addMarker(30.2980, 120.0020, "恕园教学楼", "日常上课地点");
        addMarker(30.2930, 120.0080, "体育场", "运动健身区");
        addMarker(30.2941, 120.0033, "学术交流中心", "校内会议与接待中心");
    }

    private void addMarker(double lat, double lng, String title, String snippet) {
        aMap.addMarker(new MarkerOptions().position(new LatLng(lat, lng)).title(title).snippet(snippet));
    }

    @Override public void onResume() { super.onResume(); mapView.onResume(); }
    @Override public void onPause() { super.onPause(); mapView.onPause(); }
    @Override public void onDestroy() { super.onDestroy(); mapView.onDestroy(); }
    @Override public void onSaveInstanceState(@NonNull Bundle outState) { super.onSaveInstanceState(outState); mapView.onSaveInstanceState(outState); }
}