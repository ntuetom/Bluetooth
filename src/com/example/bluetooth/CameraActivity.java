package com.example.bluetooth;

import android.hardware.Camera;
import android.os.Bundle;
import android.app.Activity;
import android.util.Log;
import android.view.Menu;

public class CameraActivity extends Activity {
	
	private final String tag = "VideoServer";
	Camera mcam;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_camera);
		mcam.open();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.camera, menu);
		return true;
	}
	private void start_camera(){
	try{
	      mcam = Camera.open();
	     }catch(RuntimeException e){
	      Log.e(tag, "init_camera: " + e);
	      return;
	     }
	     Camera.Parameters param;
	     param = mcam.getParameters();
	     //modify parameter
	     param.setPreviewFrameRate(20);
	     param.setPreviewSize(176, 144);
	     mcam.setParameters(param);
	     try {
	   mcam.setPreviewDisplay(surfaceHolder);
	   mcam.startPreview();
	  } catch (Exception e) {
	   Log.e(tag, "init_camera: " + e);
	   return;
	  }
	    }
	}

}
