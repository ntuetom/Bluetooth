package com.example.bluetooth;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.Method;
import java.util.Set;
import java.util.UUID;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

public class MainActivity extends Activity {

	private static final String TAG = "bluetooth2";
	final int RECIEVE_MESSAGE = 1;
	
	private TextView TV;
	private Button Btnconnect, Btnnext,Btndisconnect;
	private ToggleButton onoff;
	private static BluetoothAdapter mBluetoothAdapter = null; // 用來搜尋、管理藍芽裝置
	private static BluetoothSocket mBluetoothSocket = null; // 用來連結藍芽裝置、以及傳送指令
	private BluetoothDevice device;
	private StringBuilder sb = new StringBuilder();
	private static final UUID MY_UUID = UUID
			.fromString("00001101-0000-1000-8000-00805F9B34FB"); // 一定要是這
	private static InputStream mInputStream = null;
	private static String address = "00:13:03:13:80:05";
	private Thread mConnectedThread;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		Btnnext = (Button) this.findViewById(R.id.button1);
		Btnconnect = (Button) this.findViewById(R.id.button2);
		Btndisconnect = (Button) this.findViewById(R.id.button3);	
		onoff = (ToggleButton) this.findViewById(R.id.toggleButton1);
		TV = (TextView) this.findViewById(R.id.textView1);

		Btnconnect.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				searchBoundDevice();
				searchnearDevice();
			}
		});
		Btnnext.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				Intent intent = new Intent(MainActivity.this,CameraActivity.class);
				startActivity(intent);
			}
		});
		onoff.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
			public void onCheckedChanged(CompoundButton buttonView,
					boolean isChecked) {
				if (isChecked) {
					openBT();
				} else {

				}
			}
		});		
		Btndisconnect.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {

			}
		});
		

	}
	
	private Handler mhandler = new Handler() {
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case RECIEVE_MESSAGE: // if receive massage

				Log.d(TAG, "...Receive...11111");
				byte[] readBuf = (byte[]) msg.obj;
				String strIncom = new String(readBuf, 0, msg.arg1); // create
																	// string
																	// from
																	// bytes
																	// array
				sb.append(strIncom); // append string
				int endOfLineIndex = sb.indexOf("\r\n"); // determine the
															// end-of-line
				if (endOfLineIndex > 0) { // if end-of-line,
					String sbprint = sb.substring(0, endOfLineIndex); // extract
																		// string

					TV.setText("Data from Arduino: " + sbprint); // update
					sb.delete(0, sb.length()); // and clear // TextView

				}
				// Log.d(TAG, "...String:"+ sb.toString() + "Byte:" +
				// msg.arg1 + "...");
				break;
			}
		}
	};

	private BluetoothSocket createBluetoothSocket(BluetoothDevice device)
			throws IOException {
		if (Build.VERSION.SDK_INT >= 10) {
			try {
				final Method m = device.getClass().getMethod(
						"createInsecureRfcommSocketToServiceRecord",
						new Class[] { UUID.class });
				return (BluetoothSocket) m.invoke(device, MY_UUID);
			} catch (Exception e) {
				Log.e(TAG, "Could not create Insecure RFComm Connection", e);
			}
		}
		return device.createRfcommSocketToServiceRecord(MY_UUID);
	}
	
	@Override
	public void onPause() {
		super.onPause();

		Log.d(TAG, "...In onPause()...");

		try {
			if(mBluetoothSocket!=null){
				mBluetoothSocket.close();
				mBluetoothSocket = null;
			}
			
		} catch (IOException e2) {
			errorExit("Fatal Error", "In onPause() and failed to close socket."
					+ e2.getMessage() + ".");
		}
	}


	void OpenThread() {
		Log.d(TAG, "...try connect...");

		// Set up a pointer to the remote node using it's address.
		device = mBluetoothAdapter.getRemoteDevice(address);

		// Two things are needed to make a connection:
		// A MAC address, which we got above.
		// A Service ID or UUID. In this case we are using the
		// UUID for SPP.

		try {
			mBluetoothSocket = createBluetoothSocket(device);
		} catch (IOException e) {
			errorExit("Fatal Error", "In onResume() and socket create failed: "
					+ e.getMessage() + ".");
		}

		// Discovery is resource intensive. Make sure it isn't going on
		// when you attempt to connect and pass your message.
		mBluetoothAdapter.cancelDiscovery();

		// Establish the connection. This will block until it connects.
		Log.d(TAG, "...Connecting...");
		try {
			mBluetoothSocket.connect();
			Log.d(TAG, "....Connection ok...");
		} catch (IOException e) {
			try {
				mBluetoothSocket.close();
			} catch (IOException e2) {
				errorExit("Fatal Error",
						"In onResume() and unable to close socket during connection failure"
								+ e2.getMessage() + ".");
			}
		}

		// Create a data stream so we can talk to server.
		Log.d(TAG, "...Create Socket...");

		mConnectedThread = new ConnectedThread(mBluetoothSocket);
		mConnectedThread.start();

	}

	// 取得目前已經配對過的裝置
	void searchBoundDevice() {
		Set<BluetoothDevice> setPairedDevices = mBluetoothAdapter
				.getBondedDevices();
		// 如果已經有配對過的裝置
		if (setPairedDevices.size() > 0) {
			// 把裝置名稱以及MAC Address印出來
			for (BluetoothDevice device : setPairedDevices) {

				// adapter.add(device.getName() + "\n" + device.getAddress());
			}
		}
	}

	void searchnearDevice() {
		mBluetoothAdapter.startDiscovery();
		IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
		// registerReceiver(mReceiver, filter);
	}

	public String read() {
		if (!(mInputStream != null)) {
			Log.i(TAG, "Nothing");
			return "";
		}
		String inStr = "";
		try {
			if (0 < mInputStream.available()) {
				byte[] inBuffer = new byte[1024];
				int bytesRead = mInputStream.read(inBuffer);
				inStr = new String(inBuffer, "ASCII");
				inStr = inStr.substring(0, bytesRead);
				Log.i(TAG, "byteCount: " + bytesRead + ", inStr: " + inStr);
			}
		} catch (IOException e) {
			Log.e(TAG, "Read failed", e);
		}
		return inStr;
	}

	private void openBT() {

		if (mBluetoothAdapter == null)
			errorExit("Fatal Error", "Bluetooth not support");

		if (mBluetoothAdapter.isEnabled()) {
			Log.d(TAG, "...Bluetooth ON...");
		} 
		else {
			// Prompt user to turn on Bluetooth
			Intent enableBtIntent = new Intent(
					BluetoothAdapter.ACTION_REQUEST_ENABLE);
			startActivityForResult(enableBtIntent, 1);
		}

		
		TV.setText("Bluetooth Device Found");
	}

	private void errorExit(String title, String message) {
		Toast.makeText(getBaseContext(), title + " - " + message,
				Toast.LENGTH_LONG).show();
		finish();
	}

	private class ConnectedThread extends Thread {
		private final InputStream mmInStream;
		private final OutputStream mmOutStream;

		public ConnectedThread(BluetoothSocket socket) {
			InputStream tmpIn = null;
			OutputStream tmpOut = null;

			// Get the input and output streams, using temp objects because
			// member streams are final
			try {
				tmpIn = socket.getInputStream();
				tmpOut = socket.getOutputStream();
			} catch (IOException e) {
			}

			mmInStream = tmpIn;
			mmOutStream = tmpOut;
		}

		public void run() {
			Log.d(TAG, "...Data to receive: ");
			byte[] buffer = new byte[128];
			int bytes;

			// Keep listening to the InputStream until an exception occurs
			while (true) {
				try {

					bytes = mmInStream.read(buffer);
					mhandler.obtainMessage(RECIEVE_MESSAGE, bytes, -1, buffer)
							.sendToTarget(); // Send to message queue Handler

				} catch (IOException e) {
					break;
				}
			}
		}

		/* Call this from the main activity to send data to the remote device */
		public void write(String message) {
			Log.d(TAG, "...Data to send: " + message + "...");
			byte[] msgBuffer = message.getBytes();
			try {
				mmOutStream.write(msgBuffer);
			} catch (IOException e) {
				Log.d(TAG, "...Error data send: " + e.getMessage() + "...");
			}
		}
	}
//	@Override
//	public boolean onCreateOptionsMenu(Menu menu) {
//		// Inflate the menu; this adds items to the action bar if it is present.
//		getMenuInflater().inflate(R.menu.main, menu);
//		return true;
//	}

}
