package com.example.soundcakeprototype3

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.bluetooth.BluetoothAdapter
import android.content.pm.PackageManager
import androidx.core.app.ComponentActivity
import androidx.core.app.ComponentActivity.ExtraData
import androidx.core.content.ContextCompat.getSystemService
import android.icu.lang.UCharacter.GraphemeClusterBreak.T
import android.widget.Toast
import android.R
import android.content.Context.BLUETOOTH_SERVICE
import android.bluetooth.BluetoothManager
import android.content.Context
import androidx.core.app.ActivityCompat.startActivityForResult
import android.content.Intent
import android.app.Activity

class MainActivity : AppCompatActivity() {

    // 定数
    private val REQUEST_ENABLEBLUETOOTH = 1 // Bluetooth機能の有効化要求時の識別コード

    // メンバー変数
    private var mBluetoothAdapter: BluetoothAdapter? = null    // BluetoothAdapter : Bluetooth処理で必要

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Android端末がBLEをサポートしてるかの確認
        if( !getPackageManager().hasSystemFeature( PackageManager.FEATURE_BLUETOOTH_LE ) )
        {
            Toast.makeText( this, R.string.ble_is_not_supported, Toast.LENGTH_SHORT ).show()
            finish()    // アプリ終了宣言
            return
        }

        // Bluetoothアダプタの取得
        val bluetoothManager = getSystemService(Context.BLUETOOTH_SERVICE) as BluetoothManager
        mBluetoothAdapter = bluetoothManager.adapter
        if (null == mBluetoothAdapter) {    // Android端末がBluetoothをサポートしていない
            Toast.makeText(this, R.string.bluetooth_is_not_supported, Toast.LENGTH_SHORT).show()
            finish()    // アプリ終了宣言
            return
        }
    }

    override fun onResume() {
        super.onResume()

        // Android端末のBluetooth機能の有効化要求
        requestBluetoothFeature()
    }
    // Android端末のBluetooth機能の有効化要求
    private fun requestBluetoothFeature() {
        if (mBluetoothAdapter.isEnabled()) {
            return
        }
        // デバイスのBluetooth機能が有効になっていないときは、有効化要求（ダイアログ表示）
        val enableBtIntent = Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE)
        startActivityForResult(enableBtIntent, REQUEST_ENABLEBLUETOOTH)
    }
    // 機能の有効化ダイアログの操作結果
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent) {
        when (requestCode) {
            REQUEST_ENABLEBLUETOOTH // Bluetooth有効化要求
            -> if (Activity.RESULT_CANCELED == resultCode) {    // 有効にされなかった
                Toast.makeText(this, R.string.bluetooth_is_not_working, Toast.LENGTH_SHORT).show()
                finish()    // アプリ終了宣言
                return
            }
        }
        super.onActivityResult(requestCode, resultCode, data)
    }
}
