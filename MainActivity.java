package com.gknokari.matdata;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.webkit.JavascriptInterface;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;
import java.io.OutputStream;
import java.nio.charset.Charset;
import java.util.Set;
import java.util.UUID;

public class MainActivity extends Activity {
    private WebView web;
    private BluetoothSocket socket;
    private OutputStream output;
    private final UUID SPP_UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");
    private static final int REQ_BT = 77;

    @Override public void onCreate(Bundle state) {
        super.onCreate(state);
        web = new WebView(this);
        web.getSettings().setJavaScriptEnabled(true);
        web.getSettings().setDomStorageEnabled(true);
        web.setWebChromeClient(new WebChromeClient());
        web.setWebViewClient(new WebViewClient());
        web.addJavascriptInterface(new PrinterBridge(), "AndroidPrinter");
        setContentView(web);
        web.loadUrl("file:///android_asset/index.html");
    }

    private boolean permissionReady() {
        if (Build.VERSION.SDK_INT >= 31 && checkSelfPermission(Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.BLUETOOTH_CONNECT}, REQ_BT); return false;
        }
        return true;
    }

    public class PrinterBridge {
        @JavascriptInterface public String pairedDevices() {
            if (!permissionReady()) return "[]";
            try {
                BluetoothAdapter a = BluetoothAdapter.getDefaultAdapter();
                if (a == null || !a.isEnabled()) return "[]";
                Set<BluetoothDevice> ds = a.getBondedDevices();
                org.json.JSONArray arr = new org.json.JSONArray();
                for (BluetoothDevice d : ds) {
                    org.json.JSONObject o = new org.json.JSONObject();
                    o.put("name", d.getName() == null ? "Bluetooth Printer" : d.getName());
                    o.put("address", d.getAddress()); arr.put(o);
                }
                return arr.toString();
            } catch (Exception e) { return "[]"; }
        }
        @JavascriptInterface public String connect(String address) {
            if (!permissionReady()) return "Android Bluetooth permission दें और फिर प्रयास करें";
            try {
                BluetoothAdapter a = BluetoothAdapter.getDefaultAdapter();
                BluetoothDevice d = a.getRemoteDevice(address);
                if (socket != null) try { socket.close(); } catch (Exception ignored) {}
                socket = d.createRfcommSocketToServiceRecord(SPP_UUID);
                a.cancelDiscovery(); socket.connect(); output = socket.getOutputStream();
                return "OK";
            } catch (Exception e) { socket = null; output = null; return "कनेक्शन विफल: " + e.getMessage(); }
        }
        @JavascriptInterface public String printText(String value) {
            try {
                if (output == null || socket == null || !socket.isConnected()) return "पहले प्रिंटर कनेक्ट करें";
                output.write(new byte[]{0x1B,0x40});
                output.write(value.getBytes(Charset.forName("UTF-8")));
                output.write(new byte[]{0x0A,0x0A,0x0A}); output.flush(); return "OK";
            } catch (Exception e) { return "प्रिंट त्रुटि: " + e.getMessage(); }
        }
        @JavascriptInterface public boolean isConnected() { return socket != null && socket.isConnected() && output != null; }
    }

    @Override public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] results) {
        super.onRequestPermissionsResult(requestCode, permissions, results);
        if (requestCode == REQ_BT && results.length > 0 && results[0] == PackageManager.PERMISSION_GRANTED)
            Toast.makeText(this, "Bluetooth अनुमति मिल गई। फिर से प्रिंटर कनेक्ट करें।", Toast.LENGTH_LONG).show();
    }
    @Override public void onBackPressed() { if (web != null && web.canGoBack()) web.goBack(); else super.onBackPressed(); }
    @Override protected void onDestroy() { try { if (socket != null) socket.close(); } catch (Exception ignored) {} super.onDestroy(); }
}
