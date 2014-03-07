//package de.gvisions.kleiderschrank;
//
//import android.app.Activity;
//import android.content.pm.PackageManager;
//import android.hardware.Camera;
//import android.hardware.Camera.CameraInfo;
//import android.os.Bundle;
//import android.util.Log;
//import android.view.View;
//import android.widget.Toast;
//import de.gvisions.digitalerkuehlschrank.R;
//import de.gvisions.kleiderschrank.service.PhotoHandler;
//
//public class MakePhotoActivity extends Activity {
//
//	public MakePhotoActivity() {
//		// TODO Auto-generated constructor stub
//	}
//	public final static String DEBUG_TAG = "MakePhotoActivity";
//	  private Camera camera;
//	  private int cameraId = 0;
//
//	  @Override
//	  public void onCreate(Bundle savedInstanceState) {
//	    super.onCreate(savedInstanceState);
//	    setContentView(R.layout.activity_main);
//
//	    // do we have a camera?
//	    if (!getPackageManager()
//	        .hasSystemFeature(PackageManager.FEATURE_CAMERA)) {
//	      Toast.makeText(this, "No camera on this device", Toast.LENGTH_LONG)
//	          .show();
//	    } else {
//	      cameraId = findFrontFacingCamera();
//	      if (cameraId < 0) {
//	        Toast.makeText(this, "No front facing camera found.",
//	            Toast.LENGTH_LONG).show();
//	      } else {
//	        camera = Camera.open(cameraId);
//	        if (camera != null)
//	        {
//	        	Log.d(DEBUG_TAG, String.valueOf(cameraId));
//	        	Log.d(DEBUG_TAG, String.valueOf(Intent intent = new Intent();
//	            intent.setType("image/*");
//	            intent.setAction(Intent.ACTION_GET_CONTENT);
//	            intent.addCategory(Intent.CATEGORY_OPENABLE);
//	            startActivityForResult(intent, REQUEST_CODE);Intent intent = new Intent();
//	            intent.setType("image/*");
//	            intent.setAction(Intent.ACTION_GET_CONTENT);
//	            intent.addCategory(Intent.CATEGORY_OPENABLE);
//	            startActivityForResult(intent, REQUEST_CODE);camera.getNumberOfCameras()));
//	        	camera.takePicture(null, null,
//	    	        new PhotoHandler(getParent().getApplicationContext()));
//	        }
//	      }
//	    }
//	  }
//
//	  public void onClick(View view) {
//	    
//	  }
//
//	  private int findFrontFacingCamera() {
//	    int cameraId = -1;
//	    // Search for the front facing camera
//	    int numberOfCameras = Camera.getNumberOfCameras();
//	    for (int i = 0; i < numberOfCameras; i++) {
//	      CameraInfo info = new CameraInfo();
//	      Camera.getCameraInfo(i, info);
//	      if (info.facing == CameraInfo.CAMERA_FACING_BACK) {
//	        Log.d(DEBUG_TAG, "Camera found");
//	        cameraId = i;
//	        break;
//	      }
//	    }
//	    return cameraId;
//	  }
//
//	  @Override
//	  protected void onPause() {
//	    if (camera != null) {
//	      camera.release();
//	      camera = null;
//	    }
//	    super.onPause();
//	  }
//
//}
