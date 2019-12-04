package com.robotsfx.bleserial_test_android;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;
/**
 * BLESerialとの接続、データ送信、受信データ表示を行う
 * GATTサーバ = BLE機器 = BLESerial
 * 
 * Backボタンで終了＆前画面へ戻る
 * 
 * 送信は、EditText内文字をByte[]に変換して送信
 * 
 * 受信は、データを１バイトづつ表示（符号付8bit値）
 * BLESerialのデータペイロードが２０バイト／パケットなので、２０バイトを
 * 超えるデータを送ると、２０バイトづつ上書きして表示（最後のパケットしか見れない）
 * 
 * @author T.Ishii
 */
public class SerialComActivity extends Activity {
	private final static String TAG = "SerialComActivity";
    public static final String EXTRAS_DEVICE_NAME = "DEVICE_NAME";
    public static final String EXTRAS_DEVICE_ADDRESS = "DEVICE_ADDRESS";
    private String mDeviceName;
    private String mDeviceAddress;
    private BluetoothLeService mBluetoothLeService;
    private Button ledOnButton;
    private Button ledOffButton;
    private TextView resDataText;
    
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_serial_com);
		//　BTデバイス名とアドレス引継ぎ
		final Intent intent = getIntent();
        mDeviceName = intent.getStringExtra(EXTRAS_DEVICE_NAME);
        mDeviceAddress = intent.getStringExtra(EXTRAS_DEVICE_ADDRESS);
        //　タイトル部表示
        getActionBar().setTitle(mDeviceName);
        getActionBar().setDisplayHomeAsUpEnabled(true);	//左上の戻るを表示
        //　サービス接続（BluetoothLeService）
        Intent gattServiceIntent = new Intent(this, BluetoothLeService.class);
        bindService(gattServiceIntent, mServiceConnection, BIND_AUTO_CREATE);
              
        //ボタン
        ledOnButton = (Button)findViewById(R.id.led_on_button);
        ledOnButton.setOnClickListener(new ButtonClickListener());
        ledOffButton = (Button)findViewById(R.id.led_off_button);
        ledOffButton.setOnClickListener(new ButtonClickListener());
        
        //センサー値表示
        resDataText = (TextView)findViewById(R.id.res_text);
	}

	@Override
    protected void onResume() {
        super.onResume();
        //　イベントレシーバの登録と、GATTサーバ接続
        registerReceiver(mGattUpdateReceiver, makeGattUpdateIntentFilter());
        if (mBluetoothLeService != null) {
            final boolean result = mBluetoothLeService.connect(mDeviceAddress);
            Log.d(TAG, "Connect request result=" + result);
        }
    }

    @Override
    protected void onPause() {
    	//　各種終了処理
        unregisterReceiver(mGattUpdateReceiver);
        unbindService(mServiceConnection);
        mBluetoothLeService = null;
        super.onPause();
    }
	
    /**
     * タイトルを押すと前画面へ戻る
     */
	@Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
	
	//　サービスコネクション（BluetoothLeService間）
    private final ServiceConnection mServiceConnection = new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName componentName, IBinder service) {
            mBluetoothLeService = ((BluetoothLeService.LocalBinder) service).getService();
            if (!mBluetoothLeService.initialize()) {
                Log.e(TAG, "Unable to initialize Bluetooth");
                finish();
            }
            // BTアダプタへの参照の初期化が成功したら、接続動作を開始
            mBluetoothLeService.connect(mDeviceAddress);
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {
            mBluetoothLeService = null;
        }
    };
    
    // BLEサービスからのイベントを処理
    // ACTION_GATT_CONNECTED: GATT server　接続.
    // ACTION_GATT_DISCONNECTED: GATT server　切断.
    // ACTION_GATT_SERVICES_DISCOVERED: GATT services　取得.
    // ACTION_DATA_AVAILABLE: 受信データあり. 
    private final BroadcastReceiver mGattUpdateReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            final String action = intent.getAction();
            
            //　GATT接続時
            if (BluetoothLeService.ACTION_GATT_CONNECTED.equals(action)) {
                
            }
            
            //　GATT切断時
            else if (BluetoothLeService.ACTION_GATT_DISCONNECTED.equals(action)) {
                
            } 
            
            //　GATTサービス一覧取得時
            else if (BluetoothLeService.ACTION_GATT_SERVICES_DISCOVERED.equals(action)) {
                
            } 
            
            //　データ受信時
            else if (BluetoothLeService.ACTION_DATA_AVAILABLE.equals(action)) {
            	//受信データ受け取り（byte配列）
            	byte[] rsvData = intent.getByteArrayExtra(BluetoothLeService.EXTRA_DATA);
            	//　受信内容表示
            	if(rsvData != null){
            		int sensorValue = convByteToInt(rsvData[0]); //符号ありbyteを符号なしintへ変換
	            	resDataText.setText("["+String.valueOf(sensorValue)+"]");	//受信結果表示
            	}
            	
            }
        }
    };
    
    ///////////////////////////////////////////////
    //	byteを符号無しintへ変換
	////////////////////////////////////////////////
    private static int convByteToInt(byte b){
		int i = (int)b;
		i &= 0x000000FF;
		return i;
	}
    
    /**
     * GATTインテントフィルタを作る
     * @return
     */
    private static IntentFilter makeGattUpdateIntentFilter() {
        final IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(BluetoothLeService.ACTION_GATT_CONNECTED);
        intentFilter.addAction(BluetoothLeService.ACTION_GATT_DISCONNECTED);
        intentFilter.addAction(BluetoothLeService.ACTION_GATT_SERVICES_DISCOVERED);
        intentFilter.addAction(BluetoothLeService.ACTION_DATA_AVAILABLE);
        return intentFilter;
    }
    
    /**
	 * ボタンクリックのリッスン
	 * (送信ボタンを押したとき）
	 */	
	class ButtonClickListener implements OnClickListener{
		@Override
		public void onClick(View v){	
			//byte[] bytes = new byte[1];	//送信データ配列（今回は送信データ１つ）
			
			switch (v.getId()) {
			case R.id.led_on_button:
				//bytes[0]=1;							//1をセット
				
				//debug for OBT
				byte[] bytes = new byte[8];
				bytes[0] = -1;
				bytes[1] = 6;
				bytes[2] = 1;
				bytes[3] = 40;
				bytes[4] = 100;
				bytes[5] = 100;
				bytes[6] = 50;
				bytes[7] = 50;
				
				mBluetoothLeService.sendData(bytes);	//送信
				break;
			case R.id.led_off_button:
				//byte[0]=0;							//0をセット
				byte[] b = new byte[3];
				b[0] = -1;
				b[1] = 1;
				b[2] = 0;
				mBluetoothLeService.sendData(b);	//送信
				break;
			default :			
				break;
			}
			
		}
	}

}
