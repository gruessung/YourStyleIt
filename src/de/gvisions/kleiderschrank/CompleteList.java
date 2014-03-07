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
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.Spinner;
import cards.ListCardSachen;

import com.espian.showcaseview.ShowcaseViews;
import com.fima.cardsui.views.CardUI;

import de.gvisions.kleiderschrank.service.DatabaseHelper;

public class CompleteList extends Activity {

	Spinner spinner;
	CardUI listView;
	ArrayList<ArrayList<String>> cats;
	String selectedCatId = null;
	ArrayList<ArrayList<String>> valueList;
	ImageView add;
	
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
		setContentView(R.layout.complete_list);
		
		
		//Actionbar
		ActionBar bar = getActionBar();
		bar.setBackgroundDrawable(new ColorDrawable(Color.parseColor("#F9BFBE")));
		bar.setTitle("Deine Klamotten");
		bar.setDisplayHomeAsUpEnabled(true);
		
		
		spinner = (Spinner) findViewById(R.id.spCat);
		listView = (CardUI) findViewById(R.id.lvComplete);
		listView.setSwipeable(true);
		add = (ImageView) findViewById(R.id.clAdd);
		add.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				arg0.getContext().startActivity(new Intent(arg0.getContext(), NewItem.class));
			}
		});
		getCats();
		buildSpinner();
		

		spinner.setOnItemSelectedListener(new OnItemSelectedListener() {

			@Override
			public void onItemSelected(AdapterView<?> arg0, final View arg1,
					int arg2, long arg3) {
			    int pos = spinner.getSelectedItemPosition();
			    selectedCatId = cats.get(pos).get(0);
			    Log.e("DEBUG", selectedCatId);
			    if (spinner.getSelectedItem().toString().contains("Schublade wählen") == false)
			    {
			    	buildList();   
			    }
			}

			@Override
			public void onNothingSelected(AdapterView<?> arg0) {
				// TODO Auto-generated method stub
				
			}
		});
		

		
		
		SharedPreferences prefs = this.getSharedPreferences(
			      "de.gvisions.kleiderschrank", Context.MODE_PRIVATE);
		
		if (prefs.getBoolean("clshowcase", false) == false)     
		{
			
			ShowcaseViews mViews = new ShowcaseViews(this);
			
			//ShowCase
			mViews.addView( new ShowcaseViews.ItemViewProperties(R.id.spCat, R.string.clT1, R.string.clD1));
			mViews.addView( new ShowcaseViews.ItemViewProperties(R.id.clAdd, R.string.clT2, R.string.clD2));
			mViews.show();
			prefs.edit().putBoolean("clshowcase", true).commit();
		}
	
		
		
		
		
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		getCats();
		buildSpinner();
		buildList();
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
		v.add("xxx");
		v.add("Schublade wählen");
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
	
	public void buildList()
	{
		//Clear
		listView.clearCards();
		
		//DB
		SQLiteOpenHelper database;
		SQLiteDatabase connection;
		
		//Datanbankverbindung aufbauen 
		database = new DatabaseHelper(this);
		connection = database.getReadableDatabase();
		
		Cursor c = connection.rawQuery("SELECT sachen.id, sachen.name, bilder.pfad FROM sachen, bilder WHERE sachen.type = '"+selectedCatId+"' AND bilder.id = sachen.bild", null);
		valueList = new ArrayList<ArrayList<String>>();
		List v = new ArrayList<String>();
		
		while (c.moveToNext())
		{

			
			ArrayList<String> t = new  ArrayList<String>();  
			t.add(c.getString(0)); //id
			t.add(c.getString(1)); //name
			t.add(c.getString(2)); //bild
			
			ListCardSachen card = new ListCardSachen(c.getString(1), c.getString(2), this);
			card.setData(t);
			listView.addCard(card);

		}
		
		//StableArrayAdapter adapter = new StableArrayAdapter(this, valueList, v);
		//listView.setAdapter(adapter);
		listView.refresh();
		connection.close();
	}

}
