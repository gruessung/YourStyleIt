package de.gvisions.kleiderschrank;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import android.app.ActionBar;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;

import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import com.espian.showcaseview.ShowcaseView;
import com.espian.showcaseview.ShowcaseViews;
import com.espian.showcaseview.targets.ViewTarget;

import de.gvisions.kleiderschrank.service.DatabaseHelper;
import de.gvisions.kleiderschrank.service.XMLBuilder;

public class NewItem extends Activity implements OnClickListener {

	ImageView imageView;
	Button btnSave;
	Uri imageUri;
	private static final int REQUEST_CODE = 1;
	private static final int RESULT_LOAD_IMAGE = 2;
	static final String IMAGE_PATH = Environment.getExternalStorageDirectory() + "/kleiderschrank/";
	boolean image = false;
	EditText edit;
	long curTime = System.currentTimeMillis();
	EditText etTags;
	Spinner spinner;
	ArrayList<ArrayList<String>> cats;
	String selectedCatId = null;
	
	 @Override
	    public boolean onOptionsItemSelected(MenuItem item) {
	        switch (item.getItemId()) {
	        case android.R.id.home:
	            finish();
	            return true;
	        default:
	            return super.onOptionsItemSelected(item);
	        }
	    }
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.new_item);
		
		//Actionbar
		ActionBar bar = getActionBar();
		bar.setBackgroundDrawable(new ColorDrawable(Color.parseColor("#F9BFBE")));
		bar.setTitle("Neues Teil");
		bar.setDisplayHomeAsUpEnabled(true);
	
		spinner = (Spinner) findViewById(R.id.spinner1);
		getCats();
		buildSpinner();
		spinner.setOnItemSelectedListener(new OnItemSelectedListener() {

			@Override
			public void onItemSelected(AdapterView<?> arg0, final View arg1,
					int arg2, long arg3) {
				String selected = spinner.getSelectedItem().toString();
			    if (selected.equalsIgnoreCase("Neu...")) {
			    	AlertDialog.Builder alert = new AlertDialog.Builder(arg1.getContext());

			    	alert.setTitle("Neue Kategorie");
			    	alert.setMessage("Gib der Kategorie einen Namen.");

			    	// Set an EditText view to get user input 
			    	final EditText input = new EditText(arg1.getContext());
			    	alert.setView(input);

			    	alert.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
			    	public void onClick(DialogInterface dialog, int whichButton) {
				    	String value = input.getText().toString();
						//DB
						SQLiteOpenHelper database;
						SQLiteDatabase connection;
						
						//Datanbankverbindung aufbauen 
						database = new DatabaseHelper(arg1.getContext());
						connection = database.getReadableDatabase();
						
						if (value.isEmpty() == false)
						{
							connection.execSQL("INSERT INTO cats(name) VALUES ('"+value+"');");
						}
						else
						{
							Toast.makeText(arg1.getContext(), "Leerer Eintrag...", Toast.LENGTH_SHORT);
						}
						getCats();
						buildSpinner();
			    	}
			    	});

			    	alert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
			    	 public void onClick(DialogInterface dialog, int whichButton) {
			    	     // Canceled.
			    	}
			    	});

			    	 alert.show();
			    }
			    else if (selected.equalsIgnoreCase(""))
			    {
			    	//nothing
			    }
			    else
			    {
			    	int pos = spinner.getSelectedItemPosition();
			    	selectedCatId = cats.get(pos).get(0);
			    }
			
				
			}

			@Override
			public void onNothingSelected(AdapterView<?> arg0) {
				// TODO Auto-generated method stub
				
			}
		});
		
		imageView = (ImageView) findViewById(R.id.imageView1);
		imageView.setOnClickListener(this);
		
		btnSave = (Button) findViewById(R.id.btnSaveItem);
		btnSave.setOnClickListener(this);
		
		edit = (EditText) findViewById(R.id.etName);
		etTags = (EditText) findViewById(R.id.etTags);
		//Check directory
		File path = new File(IMAGE_PATH);
		if (path.exists() == false)
		{
			path.mkdirs();
			
		}
		
		SharedPreferences prefs = this.getSharedPreferences(
			      "de.gvisions.kleiderschrank", Context.MODE_PRIVATE);
		
		if (prefs.getBoolean("newitemshowcase", false) == false)
		{
			ShowcaseViews mViews = new ShowcaseViews(this);
					
			//ShowCase
			mViews.addView( new ShowcaseViews.ItemViewProperties(R.id.etName, R.string.niT1, R.string.niD1));
			mViews.addView( new ShowcaseViews.ItemViewProperties(R.id.spinner1, R.string.niT2, R.string.niD2));
			mViews.addView( new ShowcaseViews.ItemViewProperties(R.id.etTags, R.string.niT3, R.string.niD3));
			mViews.addView( new ShowcaseViews.ItemViewProperties(R.id.imageView1, R.string.niT4, R.string.niD4));
			mViews.addView( new ShowcaseViews.ItemViewProperties(R.id.btnSaveItem, R.string.niT5, R.string.niD5));
			mViews.show();
			prefs.edit().putBoolean("newitemshowcase", true).commit();
		}
	}

	public void getCats()
	{
		//DB
		SQLiteOpenHelper database;
		SQLiteDatabase connection;
		
		//Datanbankverbindung aufbauen 
		database = new DatabaseHelper(this);
		connection = database.getReadableDatabase();
		
		Cursor c = connection.rawQuery("SELECT * FROM cats", null);
		
		cats = new ArrayList<ArrayList<String>>();
		cats.clear();
		
		ArrayList<String> v = new ArrayList<String>();
		v.add("001");
		v.add("");
		cats.add(v);
		
		v = new ArrayList<String>();
		v.add("00");
		v.add("Neu...");
		cats.add(v);
		
		while (c.moveToNext())
		{
			ArrayList<String> t = new ArrayList<String>();
			t.add(c.getString(0));
			t.add(c.getString(1));
			cats.add(t);
		}
		connection.close();
	}
	
	public void buildSpinner()
	{
		List<String> SpinnerArray =  new ArrayList<String>();
		for (ArrayList<String> a : cats) {
			SpinnerArray.add(a.get(1));
		}
		
		ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, SpinnerArray);
	    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
	    spinner.setAdapter(adapter);	
	}
	
	@Override
	public void onClick(final View arg0) {

		int id = arg0.getId();
		switch (id)
		{
		
		case R.id.imageView1:
			

			 
			 //Wähle zw. Kamera und Galerie
		        AlertDialog.Builder localBuilder = new AlertDialog.Builder(this);
		        localBuilder.setTitle("Wo kommt das Bild her?");
		        localBuilder.setMessage("Tipp: Fotografiere mit der Kamera immer Hochkant für das beste Ergebnis.");
		        localBuilder	.setPositiveButton("Kamera", new android.content.DialogInterface.OnClickListener() {
											
											@Override
											public void onClick(DialogInterface dialog, int which) {
												final File photo = new File(IMAGE_PATH,  "Image_"+curTime+".jpg");
												 imageUri = Uri.fromFile(photo);
												 Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
												    
												    intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(photo));
												    intent.putExtra(MediaStore.EXTRA_SCREEN_ORIENTATION, ActivityInfo.SCREEN_ORIENTATION_PORTRAIT); 
												    startActivityForResult(intent, REQUEST_CODE);
											}
										});
		        localBuilder.setNegativeButton("Galerie", new android.content.DialogInterface.OnClickListener() {
					
					@Override
					public void onClick(DialogInterface dialog, int which) {
						final File photo = new File(IMAGE_PATH,  "Galerie_"+curTime+".jpg");
						 imageUri = Uri.fromFile(photo);
						Intent i = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
						startActivityForResult(i, RESULT_LOAD_IMAGE);
						
					}
				});
		        localBuilder.create().show();    	  
			 
			break;
		case R.id.btnSaveItem:
			
			
		
			
			String name = edit.getText().toString();
			String tags = etTags.getText().toString();
			String selected = spinner.getSelectedItem().toString();
			if (image && name.isEmpty() == false && tags.isEmpty() == false && selected.equalsIgnoreCase("") == false && selected.equalsIgnoreCase("Neu...") == false)
			{
				//DB
				SQLiteOpenHelper database;
				SQLiteDatabase connection;
				
				//Datanbankverbindung aufbauen 
				database = new DatabaseHelper(this);
				connection = database.getWritableDatabase();
				
				String sql = "INSERT INTO bilder(pfad) VALUES ('"+imageUri.toString()+"');";
				connection.execSQL(sql);
				
				sql = "SELECT last_insert_rowid() as id";
				Cursor cursor = connection.rawQuery(sql, null);
				cursor.moveToFirst();
				int lastId = cursor.getInt(0);
				
				//Tags
				tags = tags.replace("'", "");
				tags = tags.replace("(", "");
				tags = tags.replace(")", "");
				String[] t = tags.split(",");
				for (String string : t) {
					Cursor a = connection.rawQuery("SELECT COUNT(name) FROM tags WHERE `name` = '"+string.trim()+"'", null);
					a.moveToFirst();
					if (a.getInt(0) <= 0)
					{
						connection.execSQL("INSERT INTO tags(name) VALUES('"+string.trim()+"')");
					}	
					
				}
				tags = tags.replace(",","#");
				
				sql = "INSERT INTO sachen(name, bild, tags, type) VALUES ('"+name+"',"+lastId+", '"+tags+"', '"+selectedCatId+"');";
				connection.execSQL(sql);
				connection.close();
				
				Toast.makeText(this, "Gespeichert", Toast.LENGTH_SHORT).show();
				
				//Activity schließen
				finish();
				
			
			}
			else
			{
				Toast.makeText(this, "Ein Foto, ein Name, eine Kategorie und ein Schlagwort wäre gut", Toast.LENGTH_SHORT).show();
			}
			
			break;
		}
		
	}
	
	public int getCameraPhotoOrientation(Context context, Uri imageUri, String imagePath){
	    int rotate = 0;
	    try {
	        context.getContentResolver().notifyChange(imageUri, null);
	        File imageFile = new File(imagePath);

	        ExifInterface exif = new ExifInterface(imageFile.getAbsolutePath());
	        int orientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);

	        switch (orientation) {
	        case ExifInterface.ORIENTATION_ROTATE_270:
	            rotate = 270;
	            break;
	        case ExifInterface.ORIENTATION_ROTATE_180:
	            rotate = 180;
	            break;
	        case ExifInterface.ORIENTATION_ROTATE_90:
	            rotate = 90;
	            break;
	        case 0:
	        	rotate = 0;
	        	break;
	        }

	        Log.i("RotateImage", "Exif orientation: " + orientation);
	        Log.i("RotateImage", "Rotate value: " + rotate);
	    } catch (Exception e) {
	        e.printStackTrace();
	    }
	    return rotate;
	}

	

	
	
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
	    super.onActivityResult(requestCode, resultCode, data);
	    Log.d("DEBUG", String.valueOf(resultCode));
	    switch (requestCode) {
	    case REQUEST_CODE:
	    	
	    	//Kamera
	        if (resultCode == Activity.RESULT_OK) {
	            Uri selectedImage = imageUri;
	            getContentResolver().notifyChange(selectedImage, null);
	            
	            ContentResolver cr = getContentResolver();
	            Bitmap bitmap;
	            try {
	                 bitmap = android.provider.MediaStore.Images.Media
	                 .getBitmap(cr, selectedImage);

	                
	                 int nh = (int) ( bitmap.getHeight() * (1024.0 / bitmap.getWidth()) );
	                 Bitmap scaled = Bitmap.createScaledBitmap(bitmap, 1024, nh, true);
	                
	                 
	                 int rotateImage = getCameraPhotoOrientation(this, selectedImage, IMAGE_PATH);
	      
	                 if (rotateImage == 0)
	                 {
	                	 rotateImage = 90;
	                 }
	                 
	                 imageView.setRotation(rotateImage);

	            
	                 
                
	                 
	                 imageView.setImageBitmap(scaled);
	                 image = true;
	                 
	                 

	            } catch (Exception e) {
	                Toast.makeText(this, "Failed to load", Toast.LENGTH_SHORT)
	                        .show();
	                image = false;
	                Log.e("Camera", e.toString());
	            }
	        }
	        break;
	        
	        //Galerie
	    case  RESULT_LOAD_IMAGE:
	    	 if (requestCode == RESULT_LOAD_IMAGE && resultCode == RESULT_OK && data != null) {
	             Uri selectedImage = data.getData();
	             String[] filePathColumn = { MediaStore.Images.Media.DATA };
	     
	             Cursor cursor = getContentResolver().query(selectedImage,
	                     filePathColumn, null, null, null);
	             cursor.moveToFirst();
	     
	             int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
	             String picturePath = cursor.getString(columnIndex);
	             cursor.close();
	                          
	             Log.d("BILD GALLERIE", picturePath);
	             
	             File src = new File(picturePath);
	             File dest = new File(IMAGE_PATH+"Galerie_"+curTime+".jpg");
	             
	             try {
						XMLBuilder.copy(src, dest);
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}       
	             
	             
                Bitmap bitmap = BitmapFactory.decodeFile(IMAGE_PATH+"Galerie_"+curTime+".jpg");

//                
//                 int nh = (int) ( bitmap.getHeight() * (1024.0 / bitmap.getWidth()) );
//                 Bitmap scaled = Bitmap.createScaledBitmap(bitmap, 1024, nh, true);
                
                 
                 int rotateImage = getCameraPhotoOrientation(this, selectedImage, IMAGE_PATH);
             
            	 imageView.setRotation(rotateImage);
                 
                 Log.d("rotation view", String.valueOf(imageView.getRotation()));
                 
                 imageView.setImageBitmap(bitmap);
                 image = true;
	             
	 
	             
	             

	             
	         }
	    	break;
	    }
	}

	

	
	
}
