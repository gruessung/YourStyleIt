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
import com.google.ads.ac;

import de.gvisions.kleiderschrank.CompleteList;
import de.gvisions.kleiderschrank.CompleteListOutfits;
import de.gvisions.kleiderschrank.OutfitNew;
import de.gvisions.kleiderschrank.R;
import de.gvisions.kleiderschrank.ViewItem;
import de.gvisions.kleiderschrank.service.DatabaseHelper;




public class ListCardOutfit extends Card {

	private String text;
	private String picture;
	private CompleteListOutfits activity;
	private View v;
	private String title;
	private ImageView imageView;
	
public ListCardOutfit(String title,  CompleteListOutfits activity){
	super(title);
	this.activity = activity;
	
	this.title = title;
}




@Override
public View getCardContent(final Context context) {
	View view = LayoutInflater.from(context).inflate(de.gvisions.kleiderschrank.R.layout.cl_item_card, null);

	imageView = (ImageView) view.findViewById(R.id.card_image);
	
	this.v= view;
	((TextView) view.findViewById(R.id.title)).setText(title);
	



	
	this.setOnCardSwipedListener(new OnCardSwiped() {
		
		public void onCardSwiped(Card card, View layout) {
			new AlertDialog.Builder(activity)
		    .setTitle("Löschen?")
		    .setMessage("Willst du das Outfit wirklich löschen?")
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
					
					connection.execSQL("DELETE FROM outfit WHERE id = "+id);
					connection.execSQL("DELETE FROM outfit_link WHERE id_outfit = " + id);
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
			
			Intent intent = new Intent(v.getContext(), OutfitNew.class);
			intent.putExtra("id", t.get(0));
			intent.putExtra("name", t.get(1));
			intent.putExtra("edit", true);
			v.getContext().startActivity(intent);			
		}
	});

	return view;
}




}