package de.gvisions.kleiderschrank;



import android.app.Activity;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;

public class NavDrawerMainActivity extends Activity {

    private DrawerLayout mDrawerLayout;
    private ListView mDrawerList;
    private ActionBarDrawerToggle mDrawerToggle;

    private CharSequence mDrawerTitle;
    private CharSequence mTitle;
    
    private String[] drawerTitles;
	
    Button btn;
    
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.nav_drawer_main_activity);
		
		
		
		/////////////// NAV DRAWER ///////////////
		mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
	    mDrawerList = (ListView) findViewById(R.id.left_drawer);
  

	    // Set the list's click listener
	    mDrawerList.setOnItemClickListener(new DrawerItemClickListener());
	    
	    mTitle = mDrawerTitle = getTitle();
	   
	    mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
	    mDrawerList = (ListView) findViewById(R.id.left_drawer);


	    // set a custom shadow that overlays the main content when the drawer opens
	    mDrawerLayout.setDrawerShadow(R.drawable.drawer_shadow, GravityCompat.START);
	    // set up the drawer's list view with items and click listener
	    
	    SharedPreferences localSharedPreferences = getSharedPreferences("de.gvisions.oweapp", 0);
	    
	    //Drawer Einträge
	    //Erst Titel
	    //Dann Subtitle
	    //Dann Icon
	    
	  //"Login",
	    drawerTitles = new String[]
	    		{
	    			"Nutzerumfrage",
	    			"Kontakt",
	    			"Einstellungen"
	    		};

		//localSharedPreferences.getString("username", "nicht eingeloggt"),

	    String[] drawerSubtitles = new String[]
	    		{
	    			"Auswertung online",
	    			"Fragen und Anregungen",
	    			"Personalisiere dir die App"
	    		};
		//R.drawable.ic_action_device_access_accounts,
	    int[] drawerIcons = new int[] 
	    		{
	    			R.drawable.ic_action_edit,
	    			R.drawable.ic_action_action_help,
	    	
	    			R.drawable.ic_action_action_settings 
	    		};
	    MenuListAdapter mMenuAdapter = new MenuListAdapter(this, drawerTitles, drawerSubtitles, drawerIcons);
//	    mDrawerList.setAdapter(new ArrayAdapter<String>(this,
//	            R.layout.drawer_list_item, mPlanetTitles));
	    mDrawerList.setAdapter(mMenuAdapter);
	    
	    mDrawerList.setOnItemClickListener(new DrawerItemClickListener());

	    // enable ActionBar app icon to behave as action to toggle nav drawer
	    getActionBar().setDisplayHomeAsUpEnabled(true);
	    getActionBar().setHomeButtonEnabled(true);

	    // ActionBarDrawerToggle ties together the the proper interactions
	    // between the sliding drawer and the action bar app icon
	    mDrawerToggle = new ActionBarDrawerToggle(
	            this,                  /* host Activity */
	            mDrawerLayout,         /* DrawerLayout object */
	            R.drawable.ic_drawer,  /* nav drawer image to replace 'Up' caret */ 
	            0, 
	            0

	            ) {
	        public void onDrawerClosed(View view) {
	            getActionBar().setTitle(mTitle);
	            invalidateOptionsMenu(); // creates call to onPrepareOptionsMenu()
	        }

	        public void onDrawerOpened(View drawerView) {
	            getActionBar().setTitle(mDrawerTitle);
	            invalidateOptionsMenu(); // creates call to onPrepareOptionsMenu()
	        }
	    };
	    mDrawerLayout.setDrawerListener(mDrawerToggle);


	
	}
    /* The click listner for ListView in the navigation drawer */
    private class DrawerItemClickListener implements ListView.OnItemClickListener {
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            selectItem(position);
        }
    }
    
	    private void selectItem(int position) {
	        // update the main content by replacing fragments
	  	  
	  	  switch(position)
	  	  {
	  	  //Nutzerumfrage
	  	  case 0:
	  		  //startActivity(new Intent(this, WebView.class).putExtra("url", "http://goo.gl/g0XlsA"));
	  		  break;
	  	  //Nutzerumfrage
	  	  case 1:
	  		  //startActivity(new Intent(this, WebView.class).putExtra("url", "http://projects.gvisions.de/index.php?p=trouble&project=ild"));
	  		  break;
	  	  //Einstellungen	  
	  	  case 2:
	  		  //startActivity(new Intent(this, PreferenceScreen.class));
	  		  break;
	  	  }
	  	  

	        // update selected item and title, then close the drawer
	        mDrawerList.setItemChecked(position, true);
	        setTitle(drawerTitles[position]);
	        mDrawerLayout.closeDrawer(mDrawerList);
	    }
	    
	    @Override
	    protected void onPostCreate(Bundle savedInstanceState) {
	        super.onPostCreate(savedInstanceState);
	        // Sync the toggle state after onRestoreInstanceState has occurred.
	        mDrawerToggle.syncState();
	    }

	    @Override
	    public void onConfigurationChanged(Configuration newConfig) {
	        super.onConfigurationChanged(newConfig);
	        // Pass any configuration change to the drawer toggls
	        mDrawerToggle.onConfigurationChanged(newConfig);
	    }
	    
	
	
	

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	
	public boolean onOptionsItemSelected(MenuItem paramMenuItem)
	{
		// The action bar home/up action should open or close the drawer.
	      // ActionBarDrawerToggle will take care of this.
	     if (mDrawerToggle.onOptionsItemSelected(paramMenuItem)) {
	         return true;
	     }
		return false;	
	
	}
}
