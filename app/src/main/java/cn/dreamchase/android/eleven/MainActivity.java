package cn.dreamchase.android.eleven;

import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.widget.TextView;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.location.Poi;
import com.baidu.mapapi.SDKInitializer;

/**
 * -百度定位SDK
 */
public class MainActivity extends AppCompatActivity {

    private TextView textView;
    private LocationClient locationClient = null;

    //位置权限需要临时获取
    private String[] perms = {Manifest.permission.ACCESS_FINE_LOCATION};
    private final int PERMS_REQUEST_CODE = 200;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        SDKInitializer.initialize(getApplicationContext());
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        textView = findViewById(R.id.tv_location_result);

        //Android 6.0以上版本需要临时获取权限
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP_MR1 &&
                PackageManager.PERMISSION_GRANTED != checkSelfPermission(perms[0])) {
            requestPermissions(perms, PERMS_REQUEST_CODE);
        } else {
            startRequestLocation();
        }
    }

    public void startRequestLocation() {
        LocationClientOption option = new LocationClientOption();

        // 定位模式设置成高精度模式，除了高精度模式之外，还有Battery_Saving(低功耗模式)/Device_Sensors(仅设备Gps模式)
        option.setLocationMode(LocationClientOption.LocationMode.Hight_Accuracy);
        // 可选，默认为gcj02，设置返回的定位结果坐标系，如果配合百度地图使用，建议设置为bd091
        option.setCoorType("bd0911");

        //默认0，即仅定位一次，设置发起定位请求的间隔需要大于等于1000ms才是有效的
        option.setScanSpan(3000);
        // 可选，设置是否需要地址信息，默认不需要
        option.setIsNeedAddress(true);
        // 可选，设置是否需要地址描述
        option.setIsNeedLocationDescribe(true);
        // 可选，设置是否需要设备方向结果
        option.setNeedDeviceDirect(false);
        // 可选，默认为false，设置是否当Gps有效时按照1s1次频率输出GPS结果
        option.setLocationNotify(false);

        // 可选，默认为true，定位SDK内部是一个SERVICE，并放到了独立进程
        // 设置是否在停止时杀死这个进程，默认不杀死
        option.setIgnoreKillProcess(true);

        // 可以在BDLocation.getLocationDescribe中得到，结果类似于“在北京天安门附近”
        // 可选，默认false,设置是否需要POI结果，可以在DBLocation.getPoiList中得到
        option.setIsNeedLocationPoiList(true);

        // 可选，默认为false，设置是否收集crash信息，默认为收集
        option.SetIgnoreCacheException(false);

        // 可选，设置定位时是否需要海拔信息，默认为false，即不需要，除基础定位版本为都可用
        option.setIsNeedAltitude(false);

        try {
            locationClient = new LocationClient(getApplicationContext());
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("==============================" + e.getMessage());

        }
        locationClient.setLocOption(option);
        locationClient.registerLocationListener(locationListenter);

        locationClient.start();
    }

    private BDLocationListener locationListenter = new BDLocationListener() {
        @Override
        public void onReceiveLocation(BDLocation bdLocation) {
            if (null != bdLocation && bdLocation.getLocType() != BDLocation.TypeServerError) {
                StringBuffer sb = new StringBuffer(256);

                sb.append("time : ");
                /**
                 * 也可以使用systemClock.elapsedRealtime()方法，获取手机开机到现在的毫秒数，手机睡眠的时间也可以包括在内
                 * location.getTime()是指定位成功后的时间，如果位置不发生变化，则时间不变
                 */

                sb.append(bdLocation.getTime());
                sb.append("\nlocType : ");
                sb.append(bdLocation.getLocType());
                sb.append("\nlocType description : "); // *********对应的定位类型说明********
                sb.append(bdLocation.getLocTypeDescription());
                sb.append("\nlatitude : "); // 纬度
                sb.append(bdLocation.getLatitude());
                sb.append("\nlontitude"); // 经度
                sb.append(bdLocation.getLongitude());
                sb.append("\nradius : "); // 半径
                sb.append(bdLocation.getRadius());
                sb.append("\nCountryCode : "); // 国家/地区代码
                sb.append(bdLocation.getCountryCode());
                sb.append("\nCountry : "); // 国家/地区名称
                sb.append(bdLocation.getCountry());
                sb.append("\ncitycode : ");// 城市编码
                sb.append(bdLocation.getCityCode());
                sb.append("\ncity : ");// 城市
                sb.append(bdLocation.getCity());
                sb.append("\nDistrict : "); // 区
                sb.append(bdLocation.getDistrict());
                sb.append("\nStreet"); // 街道
                sb.append(bdLocation.getStreet());
                sb.append("\naddr : "); // 地址信息
                sb.append(bdLocation.getAddrStr());

                sb.append("\nUserIndoorState : "); // **********返回用户室内外判断结果**********
                sb.append(bdLocation.getUserIndoorState());

                sb.append("\nDirection(not all devices have value) : ");
                sb.append(bdLocation.getDirection()); // 方向
                sb.append("\nlocationdescribe : "); // 位置描述
                sb.append(bdLocation.getLocationDescribe());

                sb.append("\nPoi"); // POI信息
                if (bdLocation.getPoiList() != null && !bdLocation.getPoiList().isEmpty()) {
                    for (int i = 0; i < bdLocation.getPoiList().size(); i++) {
                        Poi poi = bdLocation.getPoiList().get(i);
                        sb.append(poi.getName() + ";");
                    }
                }

                if (bdLocation.getLocType() == BDLocation.TypeGpsLocation) {
                    // GPS定位结果
                    sb.append("\nspeed");
                    sb.append(bdLocation.getSpeed()); // 速度，单位：km/h
                    sb.append("\nsatellite : ");
                    sb.append(bdLocation.getSatelliteNumber()); // 卫星数目
                    sb.append("\nheight");
                    sb.append(bdLocation.getAltitude()); // 海拔高度，单位: m
                    sb.append("\ngps status : ");
                    sb.append(bdLocation.getGpsAccuracyStatus());

                    // ********gps质量判断*******
                    sb.append("\ndescribe : ");
                    sb.append("gps定位成功");

                } else if (bdLocation.getLocType() == BDLocation.TypeNetWorkLocation) {
                    // 网络定位结果
                    // 运营商信息
                    if (bdLocation.hasAltitude()) {
                        // *******如果有海拔高度*******
                        sb.append("\nheight");
                        sb.append(bdLocation.getAltitude());
                    }

                    sb.append("\noperationers : "); // 运营商信息
                    sb.append(bdLocation.getOperators());

                    sb.append("\ndescribe");
                    sb.append("网络定位成功");
                } else if (bdLocation.getLocType() == BDLocation.TypeOffLineLocation) {
                    // 离线定位结果
                    sb.append("\ndescribe");
                    sb.append("离线定位成功，结果有效");
                } else if (bdLocation.getLocType() == BDLocation.TypeServerError) {
                    sb.append("\ndescribe");
                    sb.append("服务器定位失败，可以反馈IMEI号和大体定位时间到loc-bugs@baidu.com，会有人追查原因");
                } else if (bdLocation.getLocType() == BDLocation.TypeNetWorkException) {
                    sb.append("\ndescribe");
                    sb.append("网络不同导致定位失败，请检查网络是否通畅");
                } else if (bdLocation.getLocType() == BDLocation.TypeCriteriaException) {
                    sb.append("\ndescribe");
                    sb.append("无法获取有效定位依据导致定位失败，一般是手机的原因，处于飞行模式下会造成这种结果，可以试着重启手机");
                }

                textView.setText(sb.toString());
            }
        }
    };

    @Override
    public void onRequestPermissionsResult(int permsRequestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(permsRequestCode, permissions, grantResults);
        switch (permsRequestCode) {
            case PERMS_REQUEST_CODE:
                boolean storageAccepted = grantResults[0] == PackageManager.PERMISSION_GRANTED;
                if (storageAccepted) {
                    startRequestLocation();
                }
                break;

        }
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        locationClient.stop(); // 停止定位
    }
}