package de.gvisions.kleiderschrank;

import java.util.HashMap;
import java.util.Set;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;
import de.gvisions.kleiderschrank.service.DatabaseHelper;

public class Outfit extends Activity implements OnClickListener {

	private static final int PICK_IMAGE = 999;
	int currentView;
	ImageView o1, o2, o3, o4, o5;
	Button save;
	EditText name;
	boolean edit;
	String id;
	
	HashMap<Integer, Integer> scale = new HashMap<Integer, Integer>();
	HashMap<Integer, String> images = new HashMap<Integer, String>();
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	
		setContentView(R.layout.outfit2);
		
		
		o1 = (ImageView) findViewById(R.id.o1);
		o1.setOnClickListener(this);
		
		o2 = (ImageView) findViewById(R.id.o2);
		o2.setOnClickListener(this);
		
		o3 = (ImageView) findViewById(R.id.o3);
		o3.setOnClickListener(this);
		
		o4 = (ImageView) findViewById(R.id.o4);
		o4.setOnClickListener(this);
		
		o5 = (ImageView) findViewById(R.id.o5);
		o5.setOnClickListener(this);
		
		name = (EditText) findViewById(R.id.oEdit);
		save = (Button) findViewById(R.id.oSave);
		
		save.setOnClickListener(this);
	
		try
		{
			edit = getIntent().getExtras().getBoolean("edit");
		}
		catch (NullPointerException ex)
		{
			edit = false;
		}
		
		//init images
		images.put(R.id.o1, null);
		images.put(R.id.o2, null);
		images.put(R.id.o3, null);
		images.put(R.id.o4, null);
		images.put(R.id.o5, null);
		//
		
		scale.put(R.id.o1, 20);
		scale.put(R.id.o2, 20);
		scale.put(R.id.o3, 20);
		
		scale.put(R.id.o4, 15);  
		scale.put(R.id.o5, 15);
		
		if (edit)
		{
			//DB
			SQLiteOpenHelper database;
			SQLiteDatabase connection;
			
			//Datanbankverbindung aufbauen 
			database = new DatabaseHelper(this);
			connection = database.getWritableDatabase();
			id = getIntent().getExtras().getString("id");
			
			String sql = "SELECT bilder.pfad, outfit_link.platz FROM bilder, sachen, outfit_link" +
					     " WHERE outfit_link.id_outfit = "+id+" AND sachen.id = outfit_link.id_sache AND bilder.id = sachen.bild";
			Cursor c = connection.rawQuery(sql, null);
			Log.e("DEBUG", "COUNT: "+String.valueOf(c.getCount()));
			while(c.moveToNext())
			{
				String cbild = c.getString(0);
				int cid = c.getInt(1);
				
				if (cid == R.id.o1) currentView = R.id.o1;
				if (cid == R.id.o2) currentView = R.id.o2;
				if (cid == R.id.o3) currentView = R.id.o3;
				if (cid == R.id.o4) currentView = R.id.o4;
				if (cid == R.id.o5) currentView = R.id.o5;
				
				images.remove(currentView);
				

				try
				{
					Uri b = Uri.parse(cbild);
					images.put(currentView, b.toString());
					BitmapFactory.Options options = new BitmapFactory.Options();
					options.inSampleSize = scale.get(currentView);
				    Bitmap bitmap = BitmapFactory.decodeFile(b.getPath(), options);
				    findViewById(currentView).setRotation(90);
				    ((ImageView) findViewById(currentView)).setImageBitmap(bitmap);
				}
				catch (Exception e)
				{
					Log.d("ERROR", "VIEW NOT FOUND!!" + String.valueOf(currentView) + " --- " + cid);
				}
			}
			
			name.setText(getIntent().getExtras().getString("name"));
			connection.close();
			
			
		}

		

		

	
	}

	@Override
	public void onClick(View v) {

		if (v.getId() == R.id.oSave)
		{
			//DB
			SQLiteOpenHelper database;
			SQLiteDatabase connection;
			
			//Datanbankverbindung aufbauen 
			database = new DatabaseHelper(this);
			connection = database.getWritableDatabase();
			
			//OutfitName setzen und speichern
			String n = name.getText().toString();
			if (n.isEmpty() == false)
			{
				if (!edit)
				{
					connection.execSQL("INSERT INTO outfit(name) VALUES ('"+n+"');");
					String sql = "SELECT last_insert_rowid() as id";
					Cursor cursor = connection.rawQuery(sql, null);
					cursor.moveToFirst();
					id = cursor.getString(0);
				}
				else
				{
					connection.execSQL("UPDATE outfit SET `name` = '"+n+"' WHERE `id` = "+id+";");
				}
				
				//SachenID suchen
				
				if(edit)
				{
					connection.execSQL("DELETE FROM outfit_link WHERE id_outfit = "+id);
				}
				
				Set<Integer> keys = images.keySet();
				boolean error = false;
				for (Integer i : keys) {
					String uri = images.get(i);
					if (uri != null)
					{
						
						try
						{
						uri = uri.replace("file://", "");
						String sql = "SELECT sachen.id FROM sachen, bilder WHERE sachen.bild = bilder.id  AND bilder.pfad = 'file://"+uri+"'";
						
						Log.e("SQL", sql);
						Cursor c = connection.rawQuery(sql, null);
						c.moveToFirst();
						
						
							String sid = c.getString(0);
						
							connection.execSQL("INSERT INTO outfit_link(id_outfit, id_sache, platz) VALUES ("+id+","+sid+",'"+i+"')");
							Log.e("INSERT", "INSERT INTO outfit_link(id_outfit, id_sache, platz) VALUES ("+id+","+sid+",'"+i+"')");
						}
						catch (final Exception e)
						{
							
							String message = e.getMessage().toString()+"\n\n";
							StackTraceElement[] s = e.getStackTrace();
							for (StackTraceElement el : s) {
								message += el.getClassName()+":"+el.getMethodName()+"("+el.getFileName()+":"+el.getLineNumber()+")\n";
							}
							final String m = message;
							
							error = true;
							new AlertDialog.Builder(this)
						    .setTitle("Fehler aufgetreten")
						    .setMessage("Leider ist beim Speichern ein Fehler aufgetreten.\nWir bitten dich es einfach nochmal zu probieren. Manchmal hilft es die Klamotte nochmal hinzuzufügen.\nDie App ist noch Beta und wird stetig verbessert. Melde mir den Fehler bitte unter android@gvisions.de\n\nDanke!")
						    .setIcon(R.drawable.ic_launcher)
						    .setNeutralButton("Ok", new DialogInterface.OnClickListener() {
								
								@Override
								public void onClick(DialogInterface dialog, int which) {
									dialog.dismiss();
								}
							})
							.setPositiveButton("Fehler senden", new DialogInterface.OnClickListener() {
								
								@Override
								public void onClick(DialogInterface dialog, int which) {
									Intent emailIntent = new Intent(Intent.ACTION_SENDTO, Uri.fromParts(
								            "mailto","android@gvisions.de", null));
								emailIntent.putExtra(Intent.EXTRA_SUBJECT, "Fehler im Kleiderschrank");
								emailIntent.putExtra(Intent.EXTRA_TEXT, m);
								startActivity(Intent.createChooser(emailIntent, "Send email..."));
									
								}
							})
						     .show();
							
						}
					}
				}
				
				if (!error)
				{
					connection.close();
					Toast.makeText(this, "Gespeichert.", Toast.LENGTH_SHORT).show();
					finish();
				}
			}
			else
			{
				Toast.makeText(this, "Ein Name wäre gut", Toast.LENGTH_SHORT).show();
			}
				
		}
		else
		{
			fetchImage(v.getId());
		}
		
		
	}
	
	private void fetchImage(int v)
	{
		this.currentView = v;
		Intent i = new Intent(this, ViewGroupExample.class);
		startActivityForResult(i, PICK_IMAGE);
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		
		if (requestCode == PICK_IMAGE && resultCode == Activity.RESULT_OK)
		{
			Uri b = data.getData();
			BitmapFactory.Options options = new BitmapFactory.Options();
			options.inSampleSize = scale.get(currentView);
		    Bitmap bitmap = BitmapFactory.decodeFile(b.getPath(), options);
		    findViewById(currentView).setRotation(90);
		    ((ImageView) findViewById(currentView)).setImageBitmap(bitmap);
		    
		    images.remove(currentView);
			images.put(currentView, b.getPath());
		    
		}
	}

}
