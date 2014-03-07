package de.gvisions.kleiderschrank.service;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class DatabaseHelper extends SQLiteOpenHelper {

	private Context context;
	
	private final static String DB_NAME = "kleiderschrank3";
	private final static int DB_VERSION = 5;
	
	
	
	public DatabaseHelper(Context context) { 
		super(
		        context,
		        DB_NAME,
		        null,
		        DB_VERSION
			 );
		this.context=context;
		Log.d("DB_DEBUG", "DB Helper start");
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		Log.d("DB_DEBUG", "onCreate");
		db.execSQL("CREATE TABLE IF NOT EXISTS bilder (id INTEGER PRIMARY KEY, pfad TEXT);");
		db.execSQL("CREATE TABLE IF NOT EXISTS sachen (id INTEGER PRIMARY KEY, name TEXT, bild NUMERIC, tags TEXT, type NUMERIC);");
		db.execSQL("CREATE TABLE IF NOT EXISTS outfit (id INTEGER PRIMARY KEY, name TEXT, tags TEXT);");
		db.execSQL("CREATE TABLE IF NOT EXISTS outfit_link (id INTEGER PRIMARY KEY, id_outfit NUMERIC, id_sache NUMERIC, type NUMERIC, platz TEXT);");
		db.execSQL("CREATE TABLE IF NOT EXISTS tags(name TEXT);");	
		db.execSQL("CREATE TABLE IF NOT EXISTS cats(id INTEGER PRIMARY KEY, name TEXT)");

	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int VOld, int VNew) {
		Log.d("DB_DEBUG", String.valueOf(VNew));
		switch(VNew)
		{
			case 2:
				db.execSQL("CREATE TABLE sachen (id INTEGER PRIMARY KEY, name TEXT, bild NUMERIC, tags TEXT, type NUMERIC);");
				break;
			case 4:
				db.execSQL("CREATE TABLE IF NOT EXISTS cats(id INTEGER PRIMARY KEY, name TEXT)");
				break;
			case 5:
				db.execSQL("ALTER TABLE outfit_link ADD COLUMN platz TEXT;");
				
			break;
			default: break;
		}

	}

}
