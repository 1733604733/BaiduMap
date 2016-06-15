package com.example.baidumap;

import java.lang.reflect.Method;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.mapapi.SDKInitializer;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.MapStatusUpdate;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.MyLocationConfiguration;
import com.baidu.mapapi.map.MyLocationConfiguration.LocationMode;
import com.baidu.mapapi.map.MyLocationData;
import com.baidu.mapapi.model.LatLng;
import com.example.baidumap.MyOrientationListener.OnOrientationListener;

import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.Window;

/**
 * 
 * MapView�Ļ����÷�
 * 
 * @author yaowen.wang
 */

public class MainActivity extends Activity {

	MapView mMapView = null;// ��ͼ�ؼ�
	BaiduMap mBaiduMap;// ��ͼʵ��
	LocationClient mLocationClient;// ��λ�Ŀͻ���
	MyLocationListener mMyLocationListener;// ��λ�ļ�����
	LocationMode mCurrentMode = LocationMode.NORMAL;// ��ǰ��λ��ģʽ
	private volatile boolean isFristLocation = true;// �Ƿ��ǵ�һ�ζ�λ
	// ����һ�εľ�γ��
	private double mCurrentLantitude;
	private double mCurrentLongitude;
	// ��ǰ�ľ���
	private float mCurrentAccracy;
	// ���򴫸����ļ�����
	private MyOrientationListener myOrientationListener;
	private int mXDirection;// ���򴫸���X�����ֵ
	// ��ͼ��λ��ģʽ
	private String[] mStyles = new String[] { "��ͼģʽ--����", "��ͼģʽ--����", "��ͼģʽ--����" };
	private int mCurrentStyle = 0;

	@Override
	protected void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		// �O���Ƿ�ȫ��
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		// ��ʹ��SDK�����֮ǰ��ʼ��context��Ϣ������ApplicationContext
		// ע��÷���Ҫ��setContentView����֮ǰʵ��
		SDKInitializer.initialize(getApplicationContext());
		setContentView(R.layout.activity_main);
		// ��һ�ζ�λ
		isFristLocation = true;
		// ��ȡ��ͼ�ؼ�����
		mMapView = (MapView) findViewById(R.id.bmapView);
		// ��õ�ͼ��ʵ��
		mBaiduMap = mMapView.getMap();
		MapStatusUpdate msu = MapStatusUpdateFactory.zoomTo(15.0f);
		mBaiduMap.setMapStatus(msu);
		// ��ʼ����λ
		initMyLocation();
		// ��ʼ��������
		initOritationListener();

	}

	/**
	 * ��ʼ�����򴫸���
	 */
	private void initOritationListener() {
		// TODO Auto-generated method stub
		myOrientationListener = new MyOrientationListener(getApplicationContext());
		myOrientationListener.setOnOrientationListener(new OnOrientationListener() {
			@Override
			public void onOrientationChanged(float x) {
				mXDirection = (int) x;

				// ���춨λ����
				MyLocationData locData = new MyLocationData.Builder().accuracy(mCurrentAccracy)
						// �˴����ÿ����߻�ȡ���ķ�����Ϣ��˳ʱ��0-360
						.direction(mXDirection).latitude(mCurrentLantitude).longitude(mCurrentLongitude).build();
				// ���ö�λ����
				mBaiduMap.setMyLocationData(locData);
				// �����Զ���ͼ��
				BitmapDescriptor mCurrentMarker = BitmapDescriptorFactory.fromResource(R.drawable.navi_map_gps_locked);
				MyLocationConfiguration config = new MyLocationConfiguration(mCurrentMode, true, mCurrentMarker);
				mBaiduMap.setMyLocationConfigeration(config);

			}
		});
	}

	/**
	 * ��ʼ����λ��ؿؼ�
	 */
	private void initMyLocation() {
		// TODO Auto-generated method stub
		// ��λ��ʼ��
		mLocationClient = new LocationClient(this);
		mMyLocationListener = new MyLocationListener();
		mLocationClient.registerLocationListener(mMyLocationListener);
		// ���ö�λ���������
		LocationClientOption option = new LocationClientOption();
		option.setOpenGps(true);// ��gps
		option.setCoorType("bd09ll"); // ������������
		option.setScanSpan(1000);
		mLocationClient.setLocOption(option);
	}

	/**
	 * ʵ��ʵλ�ص�����
	 */
	public class MyLocationListener implements BDLocationListener {

		@Override
		public void onReceiveLocation(BDLocation arg0) {
			// TODO Auto-generated method stub
			// map view ���ٺ��ڴ����½��յ�λ��
			if (arg0 == null || mMapView == null)
				return;
			// ���춨λ����
			MyLocationData locData = new MyLocationData.Builder().accuracy(arg0.getRadius())
					// �˴����ÿ����߻�ȡ���ķ�����Ϣ��˳ʱ��0-360
					.direction(mXDirection).latitude(arg0.getLatitude()).longitude(arg0.getLongitude()).build();
			mCurrentAccracy = arg0.getRadius();
			// ���ö�λ����
			mBaiduMap.setMyLocationData(locData);
			mCurrentLantitude = arg0.getLatitude();
			mCurrentLongitude = arg0.getLongitude();
			// �����Զ���ͼ��
			BitmapDescriptor mCurrentMarker = BitmapDescriptorFactory.fromResource(R.drawable.navi_map_gps_locked);
			MyLocationConfiguration config = new MyLocationConfiguration(mCurrentMode, true, mCurrentMarker);
			mBaiduMap.setMyLocationConfigeration(config);
			// ��һ�ζ�λʱ������ͼλ���ƶ�����ǰλ��
			if (isFristLocation) {
				isFristLocation = false;
				LatLng ll = new LatLng(arg0.getLatitude(), arg0.getLongitude());
				MapStatusUpdate u = MapStatusUpdateFactory.newLatLng(ll);
				mBaiduMap.animateMapStatus(u);
			}
		}

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.main, menu);
		return super.onCreateOptionsMenu(menu);
	}

	/**
	 * Ĭ�ϵ��menu�˵����˵����ʵͼ�꣬����ǿ������ʾ
	 */
	@Override
	public boolean onMenuOpened(int featureId, Menu menu) {

		if (featureId == Window.FEATURE_OPTIONS_PANEL && menu != null) {
			if (menu.getClass().getSimpleName().equals("MenuBuilder")) {
				try {
					Method m = menu.getClass().getDeclaredMethod("setOptionalIconsVisible", Boolean.TYPE);
					m.setAccessible(true);
					m.invoke(menu, true);
				} catch (Exception e) {
				}
			}

		}
		return super.onMenuOpened(featureId, menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {

		switch (item.getItemId()) {
		case R.id.id_menu_map_common:
			// ��ͨ��ͼ
			mBaiduMap.setMapType(BaiduMap.MAP_TYPE_NORMAL);
			break;
		case R.id.id_menu_map_site:// ���ǵ�ͼ
			mBaiduMap.setMapType(BaiduMap.MAP_TYPE_SATELLITE);
			break;
		case R.id.id_menu_map_traffic:
			// ������ͨͼ

			if (mBaiduMap.isTrafficEnabled()) {
				item.setTitle("����ʵʱ��ͨ");
				mBaiduMap.setTrafficEnabled(false);
			} else {
				item.setTitle("�ر�ʵʱ��ͨ");
				mBaiduMap.setTrafficEnabled(true);
			}
			break;
		case R.id.id_menu_map_myLoc:
			center2myLoc();
			break;
		case R.id.id_menu_map_style:
			mCurrentStyle = (++mCurrentStyle) % mStyles.length;
			item.setTitle(mStyles[mCurrentStyle]);
			// �����Զ���ͼ��
			switch (mCurrentStyle) {
			case 0:
				mCurrentMode = LocationMode.NORMAL;
				break;
			case 1:
				mCurrentMode = LocationMode.FOLLOWING;
				break;
			case 2:
				mCurrentMode = LocationMode.COMPASS;
				break;
			}
			BitmapDescriptor mCurrentMarker = BitmapDescriptorFactory.fromResource(R.drawable.navi_map_gps_locked);
			MyLocationConfiguration config = new MyLocationConfiguration(mCurrentMode, true, mCurrentMarker);
			mBaiduMap.setMyLocationConfigeration(config);
			break;
		}

		return super.onOptionsItemSelected(item);
	}

	/**
	 * ��ͼ�ƶ����ҵ�λ��,�˴��������·���λ����Ȼ��λ�� ֱ�������һ�ξ�γ�ȣ������ʱ��û�ж�λ�ɹ������ܻ���ʾЧ������
	 */
	private void center2myLoc() {
		LatLng ll = new LatLng(mCurrentLantitude, mCurrentLongitude);
		MapStatusUpdate u = MapStatusUpdateFactory.newLatLng(ll);
		mBaiduMap.animateMapStatus(u);
	}

	@Override
	protected void onStart() {
		// ����ͼ�㶨λ
		mBaiduMap.setMyLocationEnabled(true);
		if (!mLocationClient.isStarted()) {
			mLocationClient.start();
		}
		// �������򴫸���
		myOrientationListener.start();
		super.onStart();
	}

	@Override
	protected void onStop() {
		// �ر�ͼ�㶨λ
		mBaiduMap.setMyLocationEnabled(false);
		mLocationClient.stop();

		// �رշ��򴫸���
		myOrientationListener.stop();
		super.onStop();
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		// ��activityִ��onDestroyʱִ��mMapView.onDestroy()��ʵ�ֵ�ͼ�������ڹ���
		mMapView.onDestroy();
		mMapView = null;
	}

	@Override
	protected void onResume() {
		super.onResume();
		// ��activityִ��onResumeʱִ��mMapView. onResume ()��ʵ�ֵ�ͼ�������ڹ���
		mMapView.onResume();
	}

	@Override
	protected void onPause() {
		super.onPause();
		// ��activityִ��onPauseʱִ��mMapView. onPause ()��ʵ�ֵ�ͼ�������ڹ���
		mMapView.onPause();
	}

}
