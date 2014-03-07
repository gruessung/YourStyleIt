package de.gvisions.kleiderschrank;

import java.util.ArrayList;
import java.util.List;

import cards.ListCardOutfit;
import cards.ListCardSachen;

import com.espian.showcaseview.ShowcaseViews;
import com.fima.cardsui.views.CardUI;

import android.app.ActionBar;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;
import de.gvisions.kleiderschrank.service.DatabaseHelper;
import de.gvisions.kleiderschrank.service.StableArrayAdapter;

public class CompleteListOutfits extends Activity {

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
		setContentView(R.layout.complete_list_outfits);
		
		//Actionbar
		ActionBar bar = getActionBar();
		bar.setBackgroundDrawable(new ColorDrawable(Color.parseColor("#F9BFBE")));
		bar.setTitle("Deine Outfits");
		bar.setDisplayHomeAsUpEnabled(true);
		
		
		listView = (CardUI) findViewById(R.id.lvComplete);
		add = (ImageView) findViewById(R.id.clAdd);
		add.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				arg0.getContext().startActivity(new Intent(arg0.getContext(), OutfitNew.class));
			}
		});
		
		buildList();
		
//		listView.setOnItemClickListener(new OnItemClickListener() {
//
//			@Override
//			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
//					long arg3) {
//				Intent intent = new Intent(arg1.getContext(), OutfitNew.class);
//				intent.putExtra("id", valueList.get(arg2).get(0));
//				intent.putExtra("edit", true);
//				intent.putExtra("name", valueList.get(arg2).get(1));
//				arg1.getContext().startActivity(intent);
//			}
//		});
//		
//		listView.setOnItemLongClickListener(new OnItemLongClickListener() {
//
//		      @Override
//		      public boolean onItemLongClick(AdapterView<?> parent, final View view,
//		          final int position, long id) {
//		    	  new AlertDialog.Builder(view.getContext())
//				    .setTitle("Löschen?")
//				    .setMessage("Willst du das Outfit wirklich löschen?")
//				    .setIcon(R.drawable.ic_launcher)
//				    .setPositiveButton("Ja", new DialogInterface.OnClickListener() {
//						
//						@Override
//						public void onClick(DialogInterface dialog, int which) {
//							String id = valueList.get(position).get(0);
//							//DB
//							SQLiteOpenHelper database;
//							SQLiteDatabase connection;
//							
//							//Datanbankverbindung aufbauen 
//							database = new DatabaseHelper(view.getContext());
//							connection = database.getReadableDatabase();
//							
//							connection.execSQL("DELETE FROM outfit WHERE id = "+id);
//							connection.execSQL("DELETE FROM outfit_link WHERE id_outfit = " + id);
//							Toast.makeText(view.getContext(), "Gelöscht", Toast.LENGTH_SHORT).show();
//							buildList();
//							dialog.dismiss();
//						}
//					})
//					.setNegativeButton("Nein", new DialogInterface.OnClickListener() {
//						
//						@Override
//						public void onClick(DialogInterface dialog, int which) {
//							dialog.cancel();
//							
//						}
//					})
//				     .show();
//		    	  
//		        return true;
//		      }
//		    });
//		
		SharedPreferences prefs = this.getSharedPreferences(
			      "de.gvisions.kleiderschrank", Context.MODE_PRIVATE);
		
		if (prefs.getBoolean("closhowcase", false) == false)
		{
			
			ShowcaseViews mViews = new ShowcaseViews(this);
			
			//ShowCase
			mViews.addView( new ShowcaseViews.ItemViewProperties(R.id.clAdd, R.string.clT3, R.string.clD3)); 
			mViews.show();
			prefs.edit().putBoolean("closhowcase", true).commit();
		}
		
		
		
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		buildList();
	}
	
	
	public void buildList()
	{
		listView.clearCards();
		listView.setSwipeable(true);
		
		
		//DB
		SQLiteOpenHelper database;
		SQLiteDatabase connection;
		
		//Datanbankverbindung aufbauen 
		database = new DatabaseHelper(this);
		connection = database.getReadableDatabase();
		
		Cursor c = connection.rawQuery("SELECT outfit.id, outfit.name FROM outfit", null);
		valueList = new ArrayList<ArrayList<String>>();
		List v = new ArrayList<String>();
		
		while (c.moveToNext())
		{
			ArrayList<String> t = new  ArrayList<String>();  
			t.add(c.getString(0)); //id
			t.add(c.getString(1)); //name
			
			ListCardOutfit card = new ListCardOutfit(c.getString(1),  this);
			card.setData(t);
			listView.addCard(card);
		}
		
		//StableArrayAdapter adapter = new StableArrayAdapter(this, valueList, v);
		//listView.setAdapter(adapter);
		listView.refresh();
		connection.close();
	}


}
