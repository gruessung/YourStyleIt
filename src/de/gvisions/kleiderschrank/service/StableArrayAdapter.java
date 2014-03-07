package de.gvisions.kleiderschrank.service;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout.LayoutParams;
import android.widget.TextView;
import de.gvisions.kleiderschrank.R;

public class StableArrayAdapter extends ArrayAdapter<String> {

	private final Context context;
	  private final ArrayList<ArrayList<String>> values;

	  public StableArrayAdapter(Context context, ArrayList<ArrayList<String>> values, List v) {
	    super(context, R.layout.complete_list_item, v);
	    this.context = context;
	    this.values = values;
	  }

	  @Override
	  public View getView(int position, View convertView, ViewGroup parent) {
	    LayoutInflater inflater = (LayoutInflater) context
	        .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	    View rowView = inflater.inflate(R.layout.complete_list_item, parent, false);
	    
	    
	    TextView textView = (TextView) rowView.findViewById(R.id.clLabel);
	    ImageView imageView = (ImageView) rowView.findViewById(R.id.clImage);
 
	   	    
	    try
	    {
		    if (imageView != null) {
	            new setImageTask(imageView).execute(values.get(position).get(2).replace("file://", ""));
	        }
	    }
	    catch (IndexOutOfBoundsException ex)
	    {
	    	imageView.setMaxWidth(0);
	    	imageView.setVisibility(ImageView.INVISIBLE);
	    	imageView.setLayoutParams(new LayoutParams(0, 150));
	    }
	    textView.setText(values.get(position).get(1).toString());
	    return rowView;
	  }

	  
	  private class setImageTask extends AsyncTask<String, Void, Bitmap>
	  {

		WeakReference<ImageView> imageView;
		Bitmap bm;
		  
		public setImageTask(ImageView imageView) {
			this.imageView = new WeakReference<ImageView>(imageView);
		}

		@Override
		protected Bitmap doInBackground(String... params) {
			BitmapFactory.Options options = new BitmapFactory.Options();
			options.inSampleSize = 30;
			Log.e("DEBUG", params[0]);
		    Bitmap bitmap = BitmapFactory.decodeFile(params[0], options);
		    this.bm = bitmap;
			return bitmap;
		}
		  
		@Override
		protected void onPostExecute(Bitmap result) {
			super.onPostExecute(result);
			ImageView iv = this.imageView.get();
			if (iv != null && result != null)
			{
				iv.setRotation(90);
			    iv.setImageBitmap(result);
			    
			}
		}
		
	  }
	  
	
}
