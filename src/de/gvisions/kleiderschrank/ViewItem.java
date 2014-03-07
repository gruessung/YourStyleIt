package de.gvisions.kleiderschrank;

import java.io.File;

import android.app.ActionBar;
import android.app.Activity;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import de.gvisions.kleiderschrank.service.DatabaseHelper;

public class ViewItem extends Activity {

	//DB
		SQLiteOpenHelper database;
		SQLiteDatabase connection;
	
	ImageView delete;
	TextView name;
	ImageView pic;
	
	String id;
	String uri;
	String n;
	
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
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.view_item);
		
		//Actionbar
		ActionBar bar = getActionBar();
		bar.setBackgroundDrawable(new ColorDrawable(Color.parseColor("#F9BFBE")));
		bar.setTitle("Detailansicht: ");
		bar.setDisplayHomeAsUpEnabled(true);
		

		
		delete = (ImageView) findViewById(R.id.imageView2);
		
		pic = (ImageView) findViewById(R.id.imageView1);
		
		id = getIntent().getExtras().getString("id");
		uri = getIntent().getExtras().getString("bild");
		uri = uri.replace(".scaled1024", "");
		Log.d("VIEW", uri);
		n = getIntent().getExtras().getString("name");
		
		bar.setTitle("Detailansicht: " + n);
		
		String catn = getIntent().getExtras().getString("catn");
		
		BitmapFactory.Options options = new BitmapFactory.Options();
		
		String rotationUri = uri.replace("file://", "");
//		if (rotationUri.contains("Galerie"))
//		{
//			options.inSampleSize = 2;
//		}
//		else
//		{
//			options.inSampleSize = 5;
//		}

	    Bitmap bitmap = BitmapFactory.decodeFile(uri.replace("file://", ""), options);

      int nh = (int) ( bitmap.getHeight() * (1024.0 / bitmap.getWidth()) );
      Bitmap scaled = Bitmap.createScaledBitmap(bitmap, 1024, nh, true);
	    
		Log.d("VIEW", new File(uri).getAbsolutePath());
		
		

		if (rotationUri.contains("Galerie"))
		{
			pic.setRotation(0);
		}
		else
		{
			pic.setRotation(90);
		}
	    
		
		
		
		//uri = uri.replace(".scaled1024", "");
		Log.d("DEBUG", uri);
		pic.setImageBitmap(scaled);
		
		
		
	}

}
