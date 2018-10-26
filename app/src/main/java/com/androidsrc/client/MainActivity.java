package com.androidsrc.client;

import android.net.nsd.NsdManager;
import android.net.nsd.NsdServiceInfo;
import android.os.Bundle;
import android.app.Activity;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import java.net.InetAddress;

import static android.content.ContentValues.TAG;

public class MainActivity extends Activity {

	TextView response;
	EditText editTextAddress, editTextPort;
	Button buttonConnect, buttonClear;
	final String SERVICE_TYPE = "_http._tcp.";
	NsdManager mNsdManager;
	int port;
	InetAddress host;
	Client myClient;
	//private String mServiceName = ""

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		editTextAddress = (EditText) findViewById(R.id.addressEditText);
		editTextPort = (EditText) findViewById(R.id.portEditText);
		buttonConnect = (Button) findViewById(R.id.connectButton);
		buttonClear = (Button) findViewById(R.id.clearButton);
		response = (TextView) findViewById(R.id.responseTextView);

		buttonConnect.setEnabled(false);

		buttonConnect.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				buttonConnect.setEnabled(false);
				myClient = new Client(host.toString().replaceAll("/",""),port, response);
				myClient.execute();
			}
		});

		mNsdManager = (NsdManager) getApplicationContext().getSystemService(getApplicationContext().NSD_SERVICE);

		mNsdManager.discoverServices(
				SERVICE_TYPE, NsdManager.PROTOCOL_DNS_SD, mDiscoveryListener);

		buttonClear.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				response.setText("");
			}
		});
	}

	NsdManager.DiscoveryListener mDiscoveryListener = new NsdManager.DiscoveryListener() {

		// Called as soon as service discovery begins.
		@Override
		public void onDiscoveryStarted(String regType) {
			Log.d(TAG, "Service discovery started");
		}

		@Override
		public void onServiceFound(NsdServiceInfo service) {
			// A service was found! Do something with it.
			Log.d(TAG, "Service discovery success" + service);
//			if (!service.getServiceType().equals(SERVICE_TYPE)) {
//				// Service type is the string containing the protocol and
//				// transport layer for this service.
//				Log.d(TAG, "Unknown Service Type: " + service.getServiceType());
//			} else if (service.getServiceName().equals(mServiceName)) {
//				// The name of the service tells the user what they'd be
//				// connecting to. It could be "Bob's Chat App".
//				Log.d(TAG, "Same machine: " + mServiceName);
//			} else if (service.getServiceName().contains("NsdChat")){
//				mNsdManager.resolveService(service, mResolveListener);
//			}
			if (service.getServiceName().contains("NsdChat")){
				mNsdManager.resolveService(service, mResolveListener);
			}
		}

		@Override
		public void onServiceLost(NsdServiceInfo service) {
			// When the network service is no longer available.
			// Internal bookkeeping code goes here.
			Log.e(TAG, "service lost: " + service);
		}

		@Override
		public void onDiscoveryStopped(String serviceType) {
			Log.i(TAG, "Discovery stopped: " + serviceType);
			buttonConnect.setEnabled(true);
		}

		@Override
		public void onStartDiscoveryFailed(String serviceType, int errorCode) {
			Log.e(TAG, "Discovery failed: Error code:" + errorCode);
			mNsdManager.stopServiceDiscovery(this);
		}

		@Override
		public void onStopDiscoveryFailed(String serviceType, int errorCode) {
			Log.e(TAG, "Discovery failed: Error code:" + errorCode);
			mNsdManager.stopServiceDiscovery(this);
		}
	};

	NsdManager.ResolveListener mResolveListener = new NsdManager.ResolveListener() {

		@Override
		public void onResolveFailed(NsdServiceInfo serviceInfo, int errorCode) {
			// Called when the resolve fails. Use the error code to debug.
			Log.e(TAG, "Resolve failed: " + errorCode);
		}

		@Override
		public void onServiceResolved(NsdServiceInfo serviceInfo) {
			Log.e(TAG, "Resolve Succeeded. " + serviceInfo);

//			if (serviceInfo.getServiceName().equals(mServiceName)) {
//				Log.d(TAG, "Same IP.");
//				return;
//			}
//			mService = serviceInfo;
			port = serviceInfo.getPort();
			host = serviceInfo.getHost();
			buttonConnect.setEnabled(true);
		}
	};

	@Override
	protected void onDestroy() {
		super.onDestroy();
		mNsdManager.stopServiceDiscovery(mDiscoveryListener);
		myClient.onDestroy();
	}
}
