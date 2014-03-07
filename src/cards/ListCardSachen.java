package cards;



import java.lang.ref.WeakReference;
import java.util.ArrayList;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout.LayoutParams;
import android.widget.TextView;
import android.widget.Toast;

import com.fima.cardsui.objects.Card;

import de.gvisions.kleiderschrank.CompleteList;
import de.gvisions.kleiderschrank.R;
import de.gvisions.kleiderschrank.ViewItem;
import de.gvisions.kleiderschrank.service.DatabaseHelper;




public class ListCardSachen extends Card {

	private String text;
	private String picture;
	private CompleteList activity;
	private View v;
	private String title;
	private ImageView imageView;
	
public ListCardSachen(String title, String picture, CompleteList activity){
	super(title, picture);
	this.activity = activity;
	this.picture = picture;
	this.title = title;
}




@Override
public View getCardContent(final Context context) {
	View view = LayoutInflater.from(context).inflate(de.gvisions.kleiderschrank.R.layout.cl_item_card, null);

	imageView = (ImageView) view.findViewById(R.id.card_image);
	
	this.v= view;
	((TextView) view.findViewById(R.id.title)).setText(title);
	
	
		if (picture != null)
		{
		    try
		    {
			    if (imageView != null) {
		            new setImageTask(imageView).execute(picture.replace("file://", ""));
		        }
		    }
		    catch (IndexOutOfBoundsException ex)
		    {
		    	imageView.setMaxWidth(0);
		    	imageView.setVisibility(ImageView.INVISIBLE);
		    	imageView.setLayoutParams(new LayoutParams(0, 150));
		    }
		}
			


	
	this.setOnCardSwipedListener(new OnCardSwiped() {
		
		public void onCardSwiped(Card card, View layout) {
			new AlertDialog.Builder(activity)
		    .setTitle("Löschen?")
		    .setMessage("Willst du das Teil wirklich löschen?")
		    .setIcon(R.drawable.ic_launcher)
		    .setPositiveButton("Ja", new DialogInterface.OnClickListener() {
				
				@Override
				public void onClick(DialogInterface dialog, int which) {
					ArrayList<String> t = (ArrayList<String>) getData();
					String id = t.get(0);
					//DB
					SQLiteOpenHelper database;
					SQLiteDatabase connection;
					
					//Datanbankverbindung aufbauen 
					database = new DatabaseHelper(activity);
					connection = database.getReadableDatabase();
					
					connection.execSQL("DELETE FROM sachen WHERE id = "+id);
					connection.execSQL("DELETE FROM outfit_link WHERE id_sache = " + id);
					Toast.makeText(activity, "Gelöscht", Toast.LENGTH_SHORT).show();
					activity.buildList();
					dialog.dismiss();
				}
			})
			.setNegativeButton("Nein", new DialogInterface.OnClickListener() {
				
				@Override
				public void onClick(DialogInterface dialog, int which) {
					activity.buildList();
					dialog.cancel();
					
				}
			})
		     .show();
		}
	});
	
	this.setOnClickListener(new OnClickListener() {
		
		@Override
		public void onClick(View v) {
			
			ArrayList<String> t = (ArrayList<String>) getData();
			
			Intent intent = new Intent(v.getContext(), ViewItem.class);
			intent.putExtra("id", t.get(0));
			intent.putExtra("name", t.get(1));
			intent.putExtra("bild", t.get(2));
			v.getContext().startActivity(intent);			
		}
	});

	return view;
}

private class setImageTask extends AsyncTask<String, Void, Bitmap>
{

	WeakReference<ImageView> imageView;
	Bitmap bm;
	int rotation;
	int size;
	  
	public setImageTask(ImageView imageView) {
		this.imageView = new WeakReference<ImageView>(imageView);
	}

	@Override
	protected Bitmap doInBackground(String... params) {
		
		
		if (params[0].contains("Galerie"))
		{
			size = 15;
			rotation = 0;
		}
		else
		{
			size = 30;
			rotation = 90;
		}
		
		
		BitmapFactory.Options options = new BitmapFactory.Options();
		Log.e("DEBUG", params[0]);
	    Bitmap bitmap = BitmapFactory.decodeFile(params[0], options);
	    
	    if (bitmap.getWidth() > 100)
	    {
            int nh = (int) ( bitmap.getHeight() * (75 / bitmap.getWidth()) );
            if (nh <= 0) nh = 75;
            bitmap = Bitmap.createScaledBitmap(bitmap, 75, nh, true);
	    }
        
	    this.bm = bitmap;
		return bitmap;
	}
	  
	@Override
	protected void onPostExecute(Bitmap result) {
		super.onPostExecute(result);
		ImageView iv = this.imageView.get();
		if (iv != null && result != null)
		{
			iv.setRotation(rotation);
		    iv.setImageBitmap(result);
		    
		}
	}
	
}


}