//package de.gvisions.kleiderschrank.service;
//
//import java.io.File;
//import java.io.IOException;
//import java.text.SimpleDateFormat;
//import java.util.Date;
//
//import android.app.Activity;
//import android.content.Intent;
//import android.net.Uri;
//import android.os.Environment;
//import android.provider.MediaStore;
//
//public class Camera {
//
//	private Activity a;
//	
//	public Camera(Activity a) {
//		this.a = a;
//	}
//
//	String mCurrentPhotoPath;
//
//	private File createImageFile() throws IOException {
//	    // Create an image file name
//	    String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
//	    String imageFileName = "JPEG_" + timeStamp + "_";
//	    File storageDir = Environment.getExternalStoragePublicDirectory(
//	            Environment.DIRECTORY_PICTURES);
//	    File image = File.createTempFile(
//	        imageFileName,  /* prefix */
//	        ".jpg",         /* suffix */
//	        storageDir      /* directory */
//	    );
//
//	    // Save a file: path for use with ACTION_VIEW intents
//	    mCurrentPhotoPath = "file:" + image.getAbsolutePath();
//	    return image;
//	}
//	
//	static final int REQUEST_TAKE_PHOTO = 1;
//
//	public void dispatchTakePictureIntent() {
//	    Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
//	    // Ensure that there's a camera activity to handle the intent
//	    if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
//	        // Create the File where the photo should go
//	        File photoFile = null;
//	        try {
//	            photoFile = createImageFile();
//	        } catch (IOException ex) {
//	            // Error occurred while creating the File
//	            
//	        }
//	        // Continue only if the File was successfully created
//	        if (photoFile != null) {
//	            takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT,
//	                    Uri.fromFile(photoFile));
//	            startActivityForResult(takePictureIntent, REQUEST_TAKE_PHOTO);
//	        }
//	    }
//	}
//}