package demo;

import java.net.MalformedURLException;
import java.net.URL;

import com.example.helloworld.R;

import rekognition.RekoSDK;


import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.widget.EditText;

public class MainActivity extends Activity {

	public final static String EXTRA_MESSAGE = "com.example.myfirstapp.MESSAGE";
	
	private final static int PICK_PHOTO_REQUEST_CODE = 1;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}
	
	// Called when clicked pick up image button 
	public void pickImageButtonOnClick(View view) {
		//Body of your click handler
		Intent photoPickerIntent = new Intent(Intent.ACTION_GET_CONTENT);
		photoPickerIntent.setType("image/*");
		startActivityForResult(photoPickerIntent, PICK_PHOTO_REQUEST_CODE);
	}
	
	public void goButtonOnClick(View view) {
		EditText editText = (EditText) findViewById(R.id.edit_message);
		
		final String imageURL = editText.getText().toString();
		try {
			final URL url = new URL(imageURL);
			ImageDataHolder.INSTANCE.loadData(url, this);
		}
		catch (MalformedURLException e) {
		}
		RekoSDK.APICallback callback = new RekoSDK.APICallback(){
			public void gotResponse(String sResponse){
				startCollectionActivity(sResponse);
			}
		};
		RekoSDK.face_detect(imageURL, callback);
	}
	
	protected void onActivityResult(int requestCode, int resultCode, Intent data)
	{
	    super.onActivityResult(requestCode, resultCode, data);
	    if (requestCode == PICK_PHOTO_REQUEST_CODE)
	    {
	    	if (resultCode == RESULT_OK)
		    {
		        Uri chosenImageUri = data.getData();
		        ImageDataHolder holder = ImageDataHolder.INSTANCE;
		        holder.loadData(chosenImageUri, this);
				RekoSDK.APICallback callback = new RekoSDK.APICallback(){
					public void gotResponse(String sResponse){
						startCollectionActivity(sResponse);
					}
				};
				RekoSDK.face_detect(holder.getByteArray(), callback);
		    }
	    }
	}
	
	private void startCollectionActivity(String message)
	{
		Intent intent = new Intent(this, TabsCollectionActivity.class);
		intent.putExtra(EXTRA_MESSAGE, message);
		startActivity(intent);
	}
	
}
