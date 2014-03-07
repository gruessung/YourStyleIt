package de.gvisions.kleiderschrank;

import java.io.IOException;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactoryConfigurationError;

import de.gvisions.kleiderschrank.service.DatabaseHelper;
import de.gvisions.kleiderschrank.service.XMLBuilder;
import android.app.ActionBar;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceClickListener;
import android.preference.PreferenceActivity;
import android.view.Menu;
import android.view.MenuItem;

public class Einstellungen extends PreferenceActivity {

	
	ProgressDialog progress;
	
	
	 @Override
	    public void onCreate(Bundle savedInstanceState) {        
	        super.onCreate(savedInstanceState);        
	        addPreferencesFromResource(R.xml.einstellungen);     
	        
			//Actionbar
			ActionBar bar = getActionBar();
			bar.setBackgroundDrawable(new ColorDrawable(Color.parseColor("#F9BFBE")));
			bar.setTitle("Einstellungen");
			bar.setDisplayHomeAsUpEnabled(true);
			
			PackageInfo pInfo;
			try {
				pInfo = getPackageManager().getPackageInfo(getPackageName(), 0);
				String version = pInfo.versionName;
				
				Preference versionPref = (Preference) findPreference("version");
				versionPref.setSummary(version);
			} catch (NameNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			
			Preference backup = (Preference) findPreference("backup");
			backup.setOnPreferenceClickListener(new OnPreferenceClickListener() {
				
				private DatabaseHelper database;
				private SQLiteDatabase connection;

				@Override
				public boolean onPreferenceClick(Preference preference) {
		    		this.database = new DatabaseHelper(preference.getContext());	
		    		this.connection = this.database.getWritableDatabase();
		 
		    			

		
		    		progress = ProgressDialog.show(Einstellungen.this, "Erstelle Backup",
		    				  "Bitte warten...", true);

		    				Thread t = new Thread(new Runnable() {
		    				  @Override
		    				  public void run()
		    				  {
		    				    XMLBuilder xml = new XMLBuilder(connection);
								try {
									xml.backup();
								} catch (TransformerConfigurationException e) {
									// TODO Auto-generated catch block
									e.printStackTrace();
								} catch (ParserConfigurationException e) {
									// TODO Auto-generated catch block
									e.printStackTrace();
								} catch (IOException e) {
									// TODO Auto-generated catch block
									e.printStackTrace();
								} catch (TransformerException e) {
									// TODO Auto-generated catch block
									e.printStackTrace();
								} catch (TransformerFactoryConfigurationError e) {
									// TODO Auto-generated catch block
									e.printStackTrace();
								}

		    				    runOnUiThread(new Runnable() {
		    				      @Override
		    				      public void run()
		    				      {
		    				        progress.dismiss();
		    			        	connection.close();
		    						
		    			            AlertDialog.Builder localBuilder = new AlertDialog.Builder(Einstellungen.this);
		    			            localBuilder.setTitle("Backup erfolgreich");
		    			            localBuilder.setMessage("Das Backup wurde in den Ordner\n\n"+Environment.getExternalStorageDirectory() + "/kleiderschrank_backup/\n\ngelegt. Sichere den Ordner bspw. auf deinem Computer oder einem Cloudspeicher.").setPositiveButton("OK", null);
		    			            localBuilder.create().show();
		    				      }
		    				    });
		    				  }
		    				});
		    				t.start();
		    				

		    		
		    		


					//
					
					return false;
				}
			});
			
			Preference restore = (Preference) findPreference("restore");
			restore.setOnPreferenceClickListener(new OnPreferenceClickListener() {
				
				private DatabaseHelper database;
				private SQLiteDatabase connection;

				@Override
				public boolean onPreferenceClick(Preference preference) {
		    		this.database = new DatabaseHelper(preference.getContext());	
		    		this.connection = this.database.getWritableDatabase();
		    		
		    		

		    		
		    		progress = ProgressDialog.show(Einstellungen.this, "Lade Daten",
		    				  "Bitte warten...", true);

		    				Thread t = new Thread(new Runnable() {
		    				  @Override
		    				  public void run()
		    				  {
		    				    XMLBuilder xml = new XMLBuilder(connection);
								try {
									xml.restore();
								} catch (TransformerFactoryConfigurationError e) {
									// TODO Auto-generated catch block
									e.printStackTrace();
								}

		    				    runOnUiThread(new Runnable() {
		    				      @Override
		    				      public void run()
		    				      {
		    				        progress.dismiss();
		    			        	connection.close();
		    						
		    			            AlertDialog.Builder localBuilder = new AlertDialog.Builder(Einstellungen.this);
		    			            localBuilder.setTitle("Wiederherstellung erfolgreich");
		    			            localBuilder.setMessage("Alle Daten sind wieder da :-)").setPositiveButton("OK", null);
		    			            localBuilder.create().show();
		    				      }
		    				    });
		    				  }
		    				});
		    				t.start();
		    				
		    		

					
					return false;
				}
			});
			
			
			
			
	        
	    }
	    
	    @Override
	    public boolean onCreateOptionsMenu(Menu menu) {
	        return super.onCreateOptionsMenu(menu);
	    }

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


		 
}


