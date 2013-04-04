package com.ocrproject.androidocr;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import com.ocrproject.androidocr.R;

import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.provider.MediaStore.Images.Media;
import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends Activity {

	Context context;
	int duration = Toast.LENGTH_SHORT;

	private static final int ACTION_TAKE_PHOTO = 1;
	private static final int ACTION_SELECT_PHOTO = 2;
	private ImageView imageView;
	private Bitmap imageBitmap;
	private Uri imageUri;
	private File image;

	TextView info;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		context = getApplicationContext();

		imageView = (ImageView) findViewById(R.id.imageView);
		imageBitmap = null;
		info = (TextView) findViewById(R.id.info);

		Button selectPicBtn = (Button) findViewById(R.id.btnSelectPic);
		selectPicBtn.setOnClickListener(selectPicOnClickListener);

		Button takePicBtn = (Button) findViewById(R.id.btnTakePic);
		if (isIntentAvailable(this, MediaStore.ACTION_IMAGE_CAPTURE)) {
			takePicBtn.setOnClickListener(takePicOnClickListener);
		} else {
			takePicBtn.setText(getText(R.string.impossible).toString());
			takePicBtn.setClickable(false);
		}

	}

	public void processImage(String capturedImageFilePath) {// Context mContext, , File
											// imageFile
/*
		String[] projection = { MediaStore.Images.Media.DATA };
		Cursor cursor = managedQuery(imageLoc, projection, null, null, null);
		
		int column_index_data = cursor
				.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
		cursor.moveToFirst();
		String capturedImageFilePath = cursor.getString(column_index_data);
*/
		// Get the dimensions of the View
		int targetW = 100;// imageView.getWidth();
		int targetH = 100;// imageView.getHeight();

		// Get the dimensions of the bitmap
		BitmapFactory.Options bmOptions = new BitmapFactory.Options();
		bmOptions.inJustDecodeBounds = true;
		BitmapFactory.decodeFile(capturedImageFilePath, bmOptions);
		int photoW = bmOptions.outWidth;
		int photoH = bmOptions.outHeight;

		// Determine how much to scale down the image
		int scaleFactor = Math.min(photoW / targetW, photoH / targetH);

		// Decode the image file into a Bitmap sized to fill the View
		bmOptions.inJustDecodeBounds = false;
		bmOptions.inSampleSize = scaleFactor;
		bmOptions.inPurgeable = true;

		imageBitmap = BitmapFactory
				.decodeFile(capturedImageFilePath, bmOptions);
		imageView.setImageBitmap(imageBitmap);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		switch (requestCode) {
		case ACTION_TAKE_PHOTO: {
			if (resultCode == RESULT_OK) {
				// TRABALHANDO A FOTO

				if (data == null) {
					//processImage(imageUri,imageUri.toString());
					if (imageBitmap == null)
						msg("imageBitmap == null");
					else {
						// imageView.setImageBitmap(imageBitmap);

						// msg("Image saved to:" + data.getData().toString());
						msg("imageBitmap.h:" + imageBitmap.getHeight());
						msg("imageBitmap.w:" + imageBitmap.getWidth());
					}
					// imageView.setImageDrawable(new
					// BitmapDrawable(context.getResources(), imageBitmap));
					info.setText("ImageUri:" + imageUri.toString());
				}
				else{
					//Bitmap photo = (Bitmap) data.getExtras().get("data"); 
		            //imageView.setImageBitmap(photo);
		            Cursor cursor = getContentResolver().query(Media.EXTERNAL_CONTENT_URI, new String[]{Media.DATA, Media.DATE_ADDED, MediaStore.Images.ImageColumns.ORIENTATION}, Media.DATE_ADDED, null, "date_added ASC");
		            if(cursor != null && cursor.moveToFirst())
		            {
		                do {
		                	imageUri = Uri.parse(cursor.getString(cursor.getColumnIndex(Media.DATA)));
		                    //photoPath = uri.toString();
		                }while(cursor.moveToNext());
		                cursor.close();
		            }
					//Uri imageUri = data.getData();
					
					processImage(imageUri.toString());
					if (imageBitmap == null)
						msg("imageBitmap == null");
					else {
						// imageView.setImageBitmap(imageBitmap);

						// msg("Image saved to:" + data.getData().toString());
						msg("imageBitmap.h:" + imageBitmap.getHeight());
						msg("imageBitmap.w:" + imageBitmap.getWidth());
					}
					
					// imageView.setImageDrawable(new
					// BitmapDrawable(context.getResources(), imageBitmap));
					info.setText("ImageUri:" + imageUri.toString());
				}
			}
			break;
		} // ACTION_TAKE_PHOTO
		case ACTION_SELECT_PHOTO: {

			if (resultCode == RESULT_OK) {
				imageUri = data.getData();
				
				processImage(getRealPathFromURI(imageUri));
				if (imageBitmap == null)
					msg("imageBitmap == null");
				else {
					// imageView.setImageBitmap(imageBitmap);

					// msg("Image saved to:" + data.getData().toString());
					msg("imageBitmap.h:" + imageBitmap.getHeight());
					msg("imageBitmap.w:" + imageBitmap.getWidth());
				}
				
				info.setText("ImageUri:" + imageUri.toString());
			}
			break;
		}
		} // switch
	}

	Button.OnClickListener selectPicOnClickListener = new Button.OnClickListener() {
		@Override
		public void onClick(View v) {
			Intent photoPickerIntent = new Intent(Intent.ACTION_PICK);
			photoPickerIntent.setType("image/*");
			startActivityForResult(photoPickerIntent, ACTION_SELECT_PHOTO);
		}
	};

	Button.OnClickListener takePicOnClickListener = new Button.OnClickListener() {
		@Override
		public void onClick(View v) {
			/*
			String fileName = "temp.jpg";
			ContentValues values = new ContentValues();
			values.put(MediaStore.Images.Media.TITLE, fileName);
			imageUri = getContentResolver().insert(
					MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
*/
			Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
			//intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
			startActivityForResult(intent, ACTION_TAKE_PHOTO);
		}
	};

	
	
	// And to convert the image URI to the direct file system path of the image file
	public String getRealPathFromURI(Uri contentUri) {

	        // can post image
	        String [] proj={MediaStore.Images.Media.DATA};
	        Cursor cursor = managedQuery( contentUri,
	                        proj, // Which columns to return
	                        null,       // WHERE clause; which rows to return (all rows)
	                        null,       // WHERE clause selection arguments (none)
	                        null); // Order-by clause (ascending by name)
	        int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
	        cursor.moveToFirst();

	        return cursor.getString(column_index);
	}
	
	
	private void msg(String msg) {
		Toast.makeText(context, msg, duration).show();
	}

	public static boolean isIntentAvailable(Context context, String action) {
		final PackageManager packageManager = context.getPackageManager();
		final Intent intent = new Intent(action);
		List<ResolveInfo> list = packageManager.queryIntentActivities(intent,
				PackageManager.MATCH_DEFAULT_ONLY);
		return list.size() > 0;
	}

}
