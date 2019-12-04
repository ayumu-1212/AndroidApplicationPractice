package com.robotsfx.bleserial_test_android;

import java.util.ArrayList;

import android.app.Activity;
import android.app.ListActivity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

/**
 * BLEのスキャンと、選択したBLEデバイスとの通信Activityを起動
 * @author T.Ishii
 */
public class BLEScanActivity extends ListActivity {
	//メンバ
//	private static final String TAG="BLESerial_test MainActivity";
	private LeDeviceListAdapter mLeDeviceListAdapter;
    private BluetoothAdapter mBluetoothAdapter;
    private boolean mScanning;
    private Handler mHandler;
    private static final int REQUEST_ENABLE_BT = 1;
//    private static final long SCAN_PERIOD = 3000;	//スキャンを始めてからSCAN_PERIOD mS後にスキャンを自動停止
    private static final long SCAN_PERIOD = 10000;	//スキャンを始めてからSCAN_PERIOD mS後にスキャンを自動停止

    
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		getActionBar().setTitle("BLESerial2 スキャン");
        mHandler = new Handler();
		
		// 対象デバイスがBLEをサポートしているかのチェック。
        if (!getPackageManager().hasSystemFeature(PackageManager.FEATURE_BLUETOOTH_LE)) {
            Toast.makeText(this, "このデバイスではBLEはサポートされておりません。", Toast.LENGTH_SHORT).show();
            finish();
        }

        // Bluetooth adapter 初期化.  （API 18以上が必要)
        final BluetoothManager bluetoothManager =
                (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
        mBluetoothAdapter = bluetoothManager.getAdapter();

        // 対象デバイスがBluetoothをサポートしているかのチェック.
        if (mBluetoothAdapter == null) {
            Toast.makeText(this, "Bluetoothがサポートされておりません。", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }
	}

	//　メニュー展開（画面上部）
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.main, menu);
		if (!mScanning) {
            menu.findItem(R.id.menu_stop).setVisible(false);
            menu.findItem(R.id.menu_scan).setVisible(true);
            menu.findItem(R.id.menu_refresh).setActionView(null);
        } else {
            menu.findItem(R.id.menu_stop).setVisible(true);
            menu.findItem(R.id.menu_scan).setVisible(false);
            menu.findItem(R.id.menu_refresh).setActionView(
                    R.layout.actionbar_indeterminate_progress);
        }
		return true;
	}
	
	//　メニュー選択時
	@Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
        	//　スキャンする
            case R.id.menu_scan:
                mLeDeviceListAdapter.clear();
                scanLeDevice(true);
                break;
            //　スキャンを停止
            case R.id.menu_stop:
                scanLeDevice(false);
                break;
        }
        return true;
    }

	//　画面が出る時
    @Override
    protected void onResume() {
        super.onResume();

        // Bluetooth機能が有効になっているかのチェック。無効の場合はダイアログを表示して有効をうながす。(intentにて)
        if (!mBluetoothAdapter.isEnabled()) {
            if (!mBluetoothAdapter.isEnabled()) {
                Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
            }
        }

        // Initializes list view adapter.
        mLeDeviceListAdapter = new LeDeviceListAdapter();
        setListAdapter(mLeDeviceListAdapter);
        scanLeDevice(true);
    }

    //　intent戻り時
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // User chose not to enable Bluetooth.
        if (requestCode == REQUEST_ENABLE_BT && resultCode == Activity.RESULT_CANCELED) {
            finish();
            return;
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    //　画面が消える時
    @Override
    protected void onPause() {
        super.onPause();
        scanLeDevice(false);
        mLeDeviceListAdapter.clear();
    }

    //　リストアイテムクリック時（通信画面へ遷移）
    @Override
    protected void onListItemClick(ListView l, View v, int position, long id) {
        final BluetoothDevice device = mLeDeviceListAdapter.getDevice(position);
        if (device == null) return;
        final Intent intent = new Intent(this, SerialComActivity.class);
        intent.putExtra(SerialComActivity.EXTRAS_DEVICE_NAME, device.getName());
        intent.putExtra(SerialComActivity.EXTRAS_DEVICE_ADDRESS, device.getAddress());
        
        if (mScanning) {
            mBluetoothAdapter.stopLeScan(mLeScanCallback);
            mScanning = false;
        }
        startActivity(intent);
    }

    //　スキャンメソッド
    private void scanLeDevice(final boolean enable) {
        if (enable) {
            // 一定時間後にスキャンを停止（をセット）（３）
            mHandler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    mScanning = false;
                    mBluetoothAdapter.stopLeScan(mLeScanCallback);
                    invalidateOptionsMenu();
                }
            }, SCAN_PERIOD);
            // 前の結果を消去（１）
            mLeDeviceListAdapter.clear();          
            //　スキャン開始（２）
            mScanning = true;
            mBluetoothAdapter.startLeScan(mLeScanCallback);
        } else {
            mScanning = false;
            mBluetoothAdapter.stopLeScan(mLeScanCallback);
        }
        invalidateOptionsMenu();
    }
    
    // デバイスの情報を持ったアダプター。スキャン中に使用。
    private class LeDeviceListAdapter extends BaseAdapter {
        private ArrayList<BluetoothDevice> mLeDevices;
        private ArrayList<Integer> mRssi;
        private LayoutInflater mInflator;

        public LeDeviceListAdapter() {
            super();
            mLeDevices = new ArrayList<BluetoothDevice>();
            mRssi = new ArrayList<Integer>();
            mInflator = BLEScanActivity.this.getLayoutInflater();
        }

        public void addDevice(BluetoothDevice device) {
            if(!mLeDevices.contains(device)) {
                mLeDevices.add(device);
            }
        }
        
        public void addRssi(Integer i){
        	mRssi.add(i);
        }

        public BluetoothDevice getDevice(int position) {
            return mLeDevices.get(position);
        }

        public void clear() {
            mLeDevices.clear();
            mRssi.clear();
        }

        @Override
        public int getCount() {
            return mLeDevices.size();
        }

        @Override
        public Object getItem(int i) {
            return mLeDevices.get(i);
        }

        @Override
        public long getItemId(int i) {
            return i;
        }

        @Override
        public View getView(int i, View view, ViewGroup viewGroup) {
            ViewHolder viewHolder;	//View一時保存クラス（下の方で定義している）
            // 初回（リストがNull）の時、Viewをインフレート
            if (view == null) {
                view = mInflator.inflate(R.layout.listitem_device, null);
                viewHolder = new ViewHolder();
                viewHolder.deviceAddress = (TextView) view.findViewById(R.id.device_address);
                viewHolder.deviceName = (TextView) view.findViewById(R.id.device_name);
                viewHolder.deviceRssi = (TextView) view.findViewById(R.id.device_rssi);
                view.setTag(viewHolder);
            }
            //　初回以外はインフレートしない。
            else {
                viewHolder = (ViewHolder) view.getTag();
            }
            //　各値をセット
            BluetoothDevice device = mLeDevices.get(i);
            final String deviceName = device.getName();
            if (deviceName != null && deviceName.length() > 0)
                viewHolder.deviceName.setText(deviceName);
            else
                viewHolder.deviceName.setText("unknown_device");
            viewHolder.deviceAddress.setText("アドレス："+device.getAddress());
            viewHolder.deviceRssi.setText("信号強度："+String.valueOf(mRssi.get(i)));

            return view;
        }
    }

    // スキャンのコールバック
    private BluetoothAdapter.LeScanCallback mLeScanCallback =
            new BluetoothAdapter.LeScanCallback() {
    	//　デバイスが発見された時
        @Override
        public void onLeScan(final BluetoothDevice device, final int rssi, final byte[] scanRecord) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                	//(*1)参照
 //               	if(device.getName().equals("BLESerial")){    
                	if("BLESerial2".equals(device.getName())){
	                    mLeDeviceListAdapter.addDevice(device);
	                    mLeDeviceListAdapter.addRssi(rssi);
	                    mLeDeviceListAdapter.notifyDataSetChanged();
                	}
                }
            });
        }
    };
    
    /**
     * Viewを一時保存するクラス
     */
    static class ViewHolder {
        TextView deviceName;
        TextView deviceAddress;
        TextView deviceRssi;
    }

}