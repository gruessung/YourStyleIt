package de.gvisions.kleiderschrank.service;

import java.lang.ref.WeakReference;
import java.util.List;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnLongClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import de.gvisions.kleiderschrank.R;

public class OutfitListAdapter extends ArrayAdapter<String> {

	private final Context context;
	  private final List values;
	  private int scale;

	  public OutfitListAdapter(Context context, List v, int scale) {
	    super(context, R.layout.complete_list_item, v);
	    this.context = context;
	    this.values = v;
	    this.scale = scale;
	  }

	  
	  
	  
	  @Override
	  public View getView(int position, View convertView, ViewGroup parent) {
	    LayoutInflater inflater = (LayoutInflater) context
	        .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	    View rowView = inflater.inflate(R.layout.outfit3_list_item, parent, false);
	    
	   
	    
	    
	    ImageView imageView = (ImageView) rowView.findViewById(R.id.imageView1);
	    
	    rowView.setOnLongClickListener(new OnLongClickListener() {
			
	    	@Override
			public boolean onLongClick(View v) {

	    		
	    		
	    		return false;
			}
		});
	    
	    
	    
	    
	    Log.e("DRAWABLE", String.valueOf(values.get(position)));
        
    	//imageView.setImageResource((Integer) values.get(position));
    	
	   	    
	    try
	    {
	    	new setImageTask(imageView).execute(values.get(position).toString().replace("file://", ""));
//	    	BitmapFactory.Options options = new BitmapFactory.Options();
//			options.inSampleSize = scale;
//			Log.e("DEBUG", values.get(position).toString().replace("file://", ""));
//		    Bitmap bitmap = BitmapFactory.decodeFile(values.get(position).toString().replace("file://", ""), options);
//		    imageView.setRotation(90);
//		    imageView.setImageBitmap(bitmap);
	    	rowView.setClickable(false);
	    }
	    catch (Exception ex)
	    {
	    	
	    }
	   
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
			Bitmap bitmap = null;
			
				BitmapFactory.Options options = new BitmapFactory.Options();
				options.inSampleSize = scale;
				Log.e("DEBUG", params[0]);
			    bitmap = BitmapFactory.decodeFile(params[0], options);
			
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
