/*
 * Copyright 2013 David Schreiber
 *           2013 John Paul Nalog
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */

package de.gvisions.kleiderschrank;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.sax.StartElementListener;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import at.technikum.mti.fancycoverflow.FancyCoverFlow;
import at.technikum.mti.fancycoverflow.FancyCoverFlowAdapter;
import de.gvisions.kleiderschrank.service.DatabaseHelper;

public class ViewGroupExample extends Activity implements TextWatcher {
	
	//DB
	SQLiteOpenHelper database;
	SQLiteDatabase connection;
	
	ArrayList<String> bilder = new ArrayList<String>();
	ArrayList<String> ids = new ArrayList<String>();
	ArrayList<String> namen = new ArrayList<String>();
	
	AutoCompleteTextView search;
	ImageView btnSuche;
	ArrayList<String> result;
	
	

    // =============================================================================
    // Supertype overrides
    // =============================================================================

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setContentView(R.layout.layout_inflate_example);
        
        getActionBar().hide();
        
        final String IMAGE_PATH = Environment.getExternalStorageDirectory() + "/kleiderschrank/";
        //Check directory
  		File path = new File(IMAGE_PATH);
  		if (path.exists() == false)
  		{
  			path.mkdirs();
  			
  		}
        
      //Datanbankverbindung aufbauen 
    	database = new DatabaseHelper(this);
    	connection = database.getWritableDatabase();
    	
    	btnSuche = (ImageView) findViewById(R.id.ivSuche);
    
    	
    	String tsql = "SELECT name FROM tags";
    	Cursor j = this.connection.rawQuery(tsql, null);
    	result = new ArrayList<String>();
    	while(j.moveToNext())
    	{
    		result.add(j.getString(0));
    		Log.d("ADD TAG", j.getString(0));
    	}
    	connection.close();
    	Object[] tags = result.toArray();
    	String[] StringArray = Arrays.copyOf(tags,tags.length,String[].class);

    	search = (AutoCompleteTextView) findViewById(R.id.tvSearch);
    	//search.addTextChangedListener(this);
        search.setAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_dropdown_item_1line, StringArray));
    	
        btnSuche.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				ladeBilder(search.getText().toString());
			}
		});
    	
        ladeBilder("");
        
        
    }
    
	@Override
	public void afterTextChanged(Editable searchFor) {
		
		if (result.indexOf(searchFor.toString()) != 0)
		{
			ladeBilder(searchFor.toString());
		}
		
	}
	
	public void ladeBilder(String tag)
	{
		//Datanbankverbindung aufbauen 
    	database = new DatabaseHelper(this);
    	connection = database.getWritableDatabase();
    	
    	//Leeren
    	bilder.clear();
    	ids.clear();
    	namen.clear();
    	
    	
		String sql = "";
		
		//Bilder laden
		if (tag.isEmpty())
		{
			sql ="SELECT sachen.id, sachen.name, bilder.pfad " +
    				"FROM sachen, bilder " +
    				"WHERE bilder.id = sachen.bild " +
    				"LIMIT 15";
		}
		else
		{
			String where = "";
			if (tag.contains(","))
			{
				String[] h = tag.split(",");
				for (String string : h) {
					where += "AND sachen.tags LIKE '%"+string.trim()+"%' ";
				}
			}
			else
			{
				where = "AND sachen.tags LIKE '%"+tag.trim()+"%'";
			}
			
			sql ="SELECT sachen.id, sachen.name, bilder.pfad " +
    				"FROM sachen, bilder " +
    				"WHERE bilder.id = sachen.bild " +
    				where+
    				"LIMIT 15";
		}
    	Cursor c = this.connection.rawQuery(sql, null);
    	if (c.getCount() == 0)
    	{
    		Toast.makeText(this, "Keine Ergebnisse", Toast.LENGTH_SHORT).show();
    	}
    	
    	while (c.moveToNext())
    	{
    		int id = c.getInt(0);
    		String name = c.getString(1);
    		String bild = c.getString(2)+"";
    		
    		bilder.add(bild);
    		ids.add(String.valueOf(id));
    		namen.add(name);
    	}
    	connection.close();
        FancyCoverFlow fancyCoverFlow = (FancyCoverFlow) findViewById(R.id.fancyCoverFlow);
        fancyCoverFlow.setAdapter(new ViewGroupExampleAdapter(bilder, ids, namen, this));
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

    // =============================================================================
    // Private classes
    // =============================================================================

    private static class ViewGroupExampleAdapter extends FancyCoverFlowAdapter {

        // =============================================================================
        // Private members
        // =============================================================================

        private int[] images = {R.drawable.ic_action_action_help, R.drawable.ic_action_add, R.drawable.ic_action_edit, R.drawable.ic_action_calicon, R.drawable.ic_action_search, R.drawable.ic_launcher,};

        ArrayList<String> bilder = new ArrayList<String>();
    	ArrayList<String> ids = new ArrayList<String>();
    	ArrayList<String> namen = new ArrayList<String>();
    	Activity a;


        // =============================================================================
        // Supertype overrides
        // =============================================================================

        public ViewGroupExampleAdapter(ArrayList<String> bilder, ArrayList<String> ids, ArrayList<String> namen, Activity a) {
        	this.bilder.clear();
        	this.ids.clear();
        	this.namen.clear();
        	
        	this.bilder = bilder;
        	this.ids = ids;
        	this.namen = namen;
        	this.a = a;
        }

		@Override
        public int getCount() {
            return bilder.size();
            
        }

        @Override
        public String getItem(int i) {
            return bilder.get(i);
        }

        @Override
        public long getItemId(int i) {
            return i;
        }
        
        public String getItemNr(int i)
        {
        	return ids.get(i);
        }
        
        public String getItemName(int i)
        {
        	return namen.get(i);
        }

        @Override
        public View getCoverFlowItem(final int i, View reuseableView, ViewGroup viewGroup) {
            CustomViewGroup customViewGroup = null;

            if (reuseableView != null) {
                customViewGroup = (CustomViewGroup) reuseableView;
            } else {
                customViewGroup = new CustomViewGroup(viewGroup.getContext());
                customViewGroup.setLayoutParams(new FancyCoverFlow.LayoutParams(400, 1000));
            }

            BitmapFactory.Options options = new BitmapFactory.Options();
    		options.inSampleSize = 3;
    	    Bitmap bitmap = BitmapFactory.decodeFile(this.getItem(i).replace("file://", ""), options);
            
            customViewGroup.getImageView().setImageBitmap(bitmap);
            customViewGroup.getImageView().setRotation(90);
            customViewGroup.getImageView().setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					Uri bild = Uri.parse(getItem(i));
					Intent _result = new Intent();
					_result.setData(bild);
					a.setResult(Activity.RESULT_OK, _result);
					a.finish();
				}
			});
            //customViewGroup.getTextView().setText(getItemName(i));
            return customViewGroup;
        }	
    }

    private static class CustomViewGroup extends LinearLayout {

        // =============================================================================
        // Child views
        // =============================================================================

        private TextView textView;

        private ImageView imageView;

        private Button button;

        // =============================================================================
        // Constructor
        // =============================================================================

        private CustomViewGroup(Context context) {
            super(context);

            this.setOrientation(VERTICAL);

            this.imageView = new ImageView(context);
            this.textView = new TextView(context);
            this.textView.setGravity(Gravity.CENTER);

            

            LinearLayout.LayoutParams layoutParams = new LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            this.imageView.setLayoutParams(layoutParams);
            this.textView.setLayoutParams(layoutParams);

            

            this.imageView.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
            this.imageView.setAdjustViewBounds(true);

            
            this.addView(this.textView);
            this.addView(this.imageView);
        }

        // =============================================================================
        // Getters
        // =============================================================================

       private TextView getTextView()
       {
    	   return textView;
       }

        private ImageView getImageView() {
            return imageView;
        }
    }


}
