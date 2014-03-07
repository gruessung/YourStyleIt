package de.gvisions.kleiderschrank;



import android.app.ActionBar;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.widget.Button;
import android.widget.ImageView;
import de.gvisions.kleiderschrank.service.DatabaseHelper;

public class MainActivity extends Activity {

	
	 private Uri imageUri;
	 private Bitmap bitmap;
	 private ImageView imageView;
	
	//DB
	SQLiteOpenHelper database;
	SQLiteDatabase connection;
	
    Button btnKlamotten;
    Button btnOutfits;
	private Bundle colorMap;

    @Override    
    public boolean onTouchEvent(MotionEvent event) {    
    // We only care about the ACTION_UP event    
    if (event.getAction() != MotionEvent.ACTION_UP) {    
    return super.onTouchEvent(event);    
    }    
        
    // Get the colour of the clicked coordinates    
    // And yes, I spell it coloUr.    
    int x = (int) event.getX();    
    int y = (int) event.getY();    
    
    try
    {  
    	int touchColour = getHitboxColour(x, y);    
        StringBuilder sb = new StringBuilder();    
        
        sb.append(Color.alpha(touchColour));    
        sb.append("-");    
        sb.append(Color.red(touchColour));    
        sb.append("-");    
        sb.append(Color.green(touchColour));    
        sb.append("-");    
        sb.append(Color.blue(touchColour));    
           
            
        Log.e("Clicked", sb.toString());    
            
        Intent i;
        if (sb.toString().trim().equals("255-255-0-0"))
        {
        	i = new Intent(this,CompleteList.class);
    		startActivity(i);
    		return true;
        }
        else if(sb.toString().trim().equals("255-0-38-255"))
        {
        	i = new Intent(this,CompleteListOutfits.class);
    		startActivity(i);

        	
    		return true;
        }
        else if(sb.toString().trim().equals("255-0-255-33"))
        {
        	i = new Intent(this,Einstellungen.class);
    		startActivity(i);
    		return true;
        }
         
        // No close matches found    
        Log.e("clicked", "nothing");    
    }
    catch (IllegalArgumentException ex)
    {
    	//do nothing
    }
        

    return false;    
    } 
    
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main_hit);
		
		//Datanbankverbindung aufbauen und Struktur herstellen
		this.database = new DatabaseHelper(this);	
		this.connection = this.database.getWritableDatabase();
		//this.database.onCreate(this.connection);
		this.connection.close();
		//////////////////////////////////////////////////////
		
	
		ActionBar bar = getActionBar();
		bar.setDisplayHomeAsUpEnabled(true);
		bar.hide();
		
		
		//Das ist neu...- Fenster && Bewerte-uns-Fenster
	    final SharedPreferences localSharedPreferences = getSharedPreferences("de.gvisions.kleiderschrank", 0);
	    final String appPackageName = getPackageName();
	    Object localObject = "";
	    try
	    {
	      String str = String.valueOf(getPackageManager().getPackageInfo(getPackageName(), 0).versionName);
	      localObject = str;
	      if (!Boolean.valueOf(localSharedPreferences.getBoolean("version_" + (String)localObject, false)).booleanValue())
	      {
	        AlertDialog.Builder localBuilder = new AlertDialog.Builder(this);
	        localBuilder.setTitle("Das ist neu in " + (String)localObject);
	        localBuilder.setMessage("Einige Benutzer berichteten von Problemen beim Sichern ihrer Daten, weil es schlichtweg zu viele waren. Ich habe den Prozess nun angepasst und nun können auch große Kleiderschränke gesichert werden :-)").setPositiveButton("OK", null);
	        localBuilder.create().show();
	        localSharedPreferences.edit().putBoolean("version_" + (String)localObject, true).commit();
	        
	      }
	      
	      //Bewerte Fenster immer nach 5 Aufrufen, es sei denn der Nutzer will es nicht
	      final int aktuelleAufrufe = localSharedPreferences.getInt("aufrufe", 1);
	      int niemals = localSharedPreferences.getInt("niemals", 0);
	      
	      Log.d("AKTUELLE AUFRUFE", String.valueOf(aktuelleAufrufe));
	      
	      if (aktuelleAufrufe >= 5 && niemals == 0)
	      {
	    	  
		        AlertDialog.Builder localBuilder = new AlertDialog.Builder(this);
		        localBuilder.setTitle("Gefällt dir meine App?");
		        localBuilder.setMessage("Du nutzt Your Style it! nun schon eine Weile.\nGefällt dir die App oder willst du uns Kritik näher bringen?\n\nBewerte meine App doch bitte im PlayStore.");
		        localBuilder	.setPositiveButton("Jetzt bewerten", new OnClickListener() {
											
											@Override
											public void onClick(DialogInterface dialog, int which) {
											    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + appPackageName)));		
											    localSharedPreferences.edit().putInt("niemals",1).commit();	
											}
										});
		        localBuilder.setNegativeButton("Nein,  niemals", new OnClickListener() {
					
					@Override
					public void onClick(DialogInterface dialog, int which) {
						localSharedPreferences.edit().putInt("niemals",1).commit();	
						
					}
				});
		        localBuilder.setNeutralButton("Später...", new OnClickListener() {
					
					@Override
					public void onClick(DialogInterface dialog, int which) {
						localSharedPreferences.edit().putInt("aufrufe",1).commit();	
						
					}
				});
		        localBuilder.create().show();    	  
	      }
	      else
	      {
	    	  localSharedPreferences.edit().putInt("aufrufe",aktuelleAufrufe+1).commit();	
	      }

	      return;
	    }
	    catch (PackageManager.NameNotFoundException localNameNotFoundException)
	    {
	      while (true)
	        localNameNotFoundException.printStackTrace();
	    }
	}
		

    
/**    
* This is where the magic happens.    
* @return Color The colour of the clicked position.    
*/    
public int getHitboxColour(int x, int y) {    
ImageView iv = (ImageView) findViewById(R.id.hitimage);    
Bitmap bmpHotspots;    
int pixel;    
    
// Fix any offsets by the positioning of screen elements such as Activity titlebar.    
// This part was causing me issues when I was testing out Bill Lahti's code.    
int[] location = new int[2];    
iv.getLocationOnScreen(location);    
x -= location[0];    
y -= location[1];    
    
// Prevent crashes, return background noise    
if ((x < 0) || (y < 0)) {    
return Color.WHITE;    
}    
    
// Draw the scaled bitmap into memory    
iv.setDrawingCacheEnabled(true);    
bmpHotspots = Bitmap.createBitmap(iv.getDrawingCache());    
iv.setDrawingCacheEnabled(false);    
    
pixel = bmpHotspots.getPixel(x, y);    
bmpHotspots.recycle();    
return pixel;    
}    
    
public boolean closeMatch(int color1, int color2) {    
int tolerance = 25;    
    
if ((int) Math.abs (Color.red (color1) - Color.red (color2)) > tolerance) {    
return false;    
}    
if ((int) Math.abs (Color.green (color1) - Color.green (color2)) > tolerance) {    
return false;    
}    
if ((int) Math.abs (Color.blue (color1) - Color.blue (color2)) > tolerance) {    
return false;    
}    
    
return true;    
}


	
}