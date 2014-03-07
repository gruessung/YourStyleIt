package de.gvisions.kleiderschrank;

import java.util.ArrayList;
import java.util.List;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

import com.espian.showcaseview.ShowcaseViews;

import de.gvisions.kleiderschrank.service.DatabaseHelper;
import de.gvisions.kleiderschrank.service.OutfitListAdapter;

public class OutfitNew extends Activity implements TextWatcher {

	
	String id = null;
	ListView acc;
	ListView haupt;
	EditText name;
	Button ok;
	ImageView a1;
	ImageView a2;
	ImageView back;
	
	int count;
	final int IMAGE_ACC = 111;
	final int IMAGE_BIG = 222;
	boolean edit;
	
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
		setContentView(R.layout.outfit3);
		
		//Actionbar
		ActionBar bar = getActionBar();
		bar.setBackgroundDrawable(new ColorDrawable(Color.parseColor("#F9BFBE")));
		bar.setTitle("Neues Outfit");
		bar.setDisplayHomeAsUpEnabled(true);
		
		name = (EditText) findViewById(R.id.o3Name); 
		
		try
		{
			edit = getIntent().getExtras().getBoolean("edit");
			id = getIntent().getExtras().getString("id");
			name.setText(getIntent().getExtras().getString("name"));
		}
		catch (Exception ex)
		{
			edit = false;
			
			//DB
			SQLiteOpenHelper database;
			SQLiteDatabase connection;
			
			//Datanbankverbindung aufbauen   
			database = new DatabaseHelper(this);
			connection = database.getReadableDatabase();
			
			
			//Lege neues Outfit in Datenbank an und hole ID
			connection.execSQL("INSERT INTO outfit(name, tags) VALUES ('Neues, namenloses Outfit', 'none');");
			String sql = "SELECT last_insert_rowid() as id";
			Cursor cursor = connection.rawQuery(sql, null);
			cursor.moveToFirst();
			id = cursor.getString(0);
			
			connection.close();
			
		}
		
		acc = (ListView) findViewById(R.id.oAcc);
		haupt = (ListView) findViewById(R.id.oBig);
		

		
		
		a1 = (ImageView) findViewById(R.id.o3Add1);
		a2 = (ImageView) findViewById(R.id.o3Add2);
		
		a1.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				Intent i = new Intent(arg0.getContext(), ViewGroupExample.class);
				startActivityForResult(i, IMAGE_ACC);
				
			}
		});
		
		a2.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				Intent i = new Intent(arg0.getContext(), ViewGroupExample.class);
				startActivityForResult(i, IMAGE_BIG);
				
			}
		});
		
		
		name.addTextChangedListener(this);
		
		buildList();
		
		SharedPreferences prefs = this.getSharedPreferences(
			      "de.gvisions.kleiderschrank", Context.MODE_PRIVATE);
		
		if (prefs.getBoolean("outfitshowcase", false) == false)
		{
			
			ShowcaseViews mViews = new ShowcaseViews(this);
			
			//ShowCase
			mViews.addView( new ShowcaseViews.ItemViewProperties(R.id.o3Name, R.string.oT1, R.string.oD1));
			mViews.addView( new ShowcaseViews.ItemViewProperties(R.id.o3Add1, R.string.oT2, R.string.oD2));
			mViews.addView( new ShowcaseViews.ItemViewProperties(R.id.o3Add2, R.string.oT3, R.string.oD3));
			mViews.show();  
			
			Toast.makeText(this,"Tipp: Du musst nicht speichern. Gehe einfach zurück.",Toast.LENGTH_LONG).show();
			
			prefs.edit().putBoolean("outfitshowcase", true).commit();
		}
		
		
		
	}

	public void buildList()
	{
		//DB
		SQLiteOpenHelper database;
		SQLiteDatabase connection;
		
		//Datanbankverbindung aufbauen   
		database = new DatabaseHelper(this);
		connection = database.getReadableDatabase();
		
		
		

		
		String sql = "SELECT bilder.pfad, outfit_link.platz FROM bilder, sachen, outfit_link" +
				     " WHERE outfit_link.id_outfit = "+id+" AND sachen.id = outfit_link.id_sache AND bilder.id = sachen.bild" +
				     " AND outfit_link.platz = 0";
		Cursor c = connection.rawQuery(sql, null);
		count = c.getCount();
		
		List v = new ArrayList<String>();
		
		while (c.moveToNext())
		{
			v.add(c.getString(0));
		}
		
		
		
		
		OutfitListAdapter adapter = new OutfitListAdapter(this,  v, 20);
		acc.setAdapter(adapter);
		
		sql = "SELECT bilder.pfad, outfit_link.platz FROM bilder, sachen, outfit_link" +
			     " WHERE outfit_link.id_outfit = "+id+" AND sachen.id = outfit_link.id_sache AND bilder.id = sachen.bild" +
			     " AND outfit_link.platz = 1";
		c = connection.rawQuery(sql, null);
		count = c.getCount();
		
		v = new ArrayList<String>();
		
		while (c.moveToNext())
		{
			v.add(c.getString(0));
		}
		
		
		adapter = new OutfitListAdapter(this,  v, 5);
		haupt.setAdapter(adapter);
		connection.close();
	}
	

	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		
		if (requestCode == IMAGE_ACC && resultCode == Activity.RESULT_OK)
		{
			Uri b = data.getData();
			//DB
			SQLiteOpenHelper database;
			SQLiteDatabase connection;
			
			//Datanbankverbindung aufbauen   
			database = new DatabaseHelper(this);
			connection = database.getWritableDatabase();
			
			String s = b.toString().replace("file://", "");
			String sql = "SELECT sachen.id FROM sachen, bilder WHERE sachen.bild = bilder.id  AND bilder.pfad = 'file://"+s+"'";
			
			Log.e("SQL", sql);
			Cursor c = connection.rawQuery(sql, null);
			c.moveToFirst();
			String sid = c.getString(0);
			
			connection.execSQL("INSERT INTO outfit_link (id_outfit, id_sache, platz) VALUES ('"+id+"','"+sid+"', '0');");
			connection.close();
			buildList();
		    
		}
		else if (requestCode == IMAGE_BIG && resultCode == Activity.RESULT_OK)
		{
			Uri b = data.getData();
			//DB
			SQLiteOpenHelper database;
			SQLiteDatabase connection;
			
			//Datanbankverbindung aufbauen   
			database = new DatabaseHelper(this);
			connection = database.getWritableDatabase();
			
			String s = b.toString().replace("file://", "");
			String sql = "SELECT sachen.id FROM sachen, bilder WHERE sachen.bild = bilder.id  AND bilder.pfad = 'file://"+s+"'";
			
			Log.e("SQL", sql);
			Cursor c = connection.rawQuery(sql, null);
			c.moveToFirst();
			String sid = c.getString(0);
			
			connection.execSQL("INSERT INTO outfit_link (id_outfit, id_sache, platz) VALUES ('"+id+"','"+sid+"', '1');");
			connection.close();
			buildList();
		    
		}
		
	}

	@Override
	public void afterTextChanged(Editable arg0) {
		//DB
		SQLiteOpenHelper database;
		SQLiteDatabase connection;
		
		//Datanbankverbindung aufbauen   
		database = new DatabaseHelper(this);
		connection = database.getWritableDatabase();
		
		connection.execSQL("UPDATE outfit SET `name` = '"+arg0.toString()+"' WHERE `id` = "+id);
		connection.close();
		
	}

	@Override
	public void beforeTextChanged(CharSequence arg0, int arg1, int arg2,
			int arg3) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {
		// TODO Auto-generated method stub
		
	}
	
}
