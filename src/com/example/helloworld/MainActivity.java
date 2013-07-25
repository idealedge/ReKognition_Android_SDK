package com.example.helloworld;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore.Images.Media;
import android.util.Base64;
import android.util.Log;
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
	// There are two ways to call ReKognition API
	// 1. Call API with image URL
	private void callAPIwithImageURL(final String sURL)
	{
		Thread trd = new Thread(new Runnable() {
			  @Override
			  public void run(){
				  List<NameValuePair> params = getBasicParameters();
				  // Call API passing image URL
				  params.add(new BasicNameValuePair("urls", sURL));
				  String response = getAPIResponse(params);
				  startDisplayTextActivity(response);
			  }
			});
		trd.start();
	}
	
	// 2. Call API with image data
	private void callAPIwithImageData(byte[] b, final String imageUri)
	{
		final String encodedImage = Base64.encodeToString(b, Base64.DEFAULT);
		Thread trd = new Thread(new Runnable() {
			  @Override
			  public void run(){
				  List<NameValuePair> params = getBasicParameters();
				  // Call this API with the image data
				  params.add(new BasicNameValuePair("base64", encodedImage));
				  String response = getAPIResponse(params);
				  startDisplayTextActivity(response);
			  }
			});
		trd.start();
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
		callAPIwithImageURL(imageURL);
	}
	
	private List<NameValuePair> getBasicParameters()
	{
        List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
        // api_key
        nameValuePairs.add(new BasicNameValuePair("api_key", "1234"));
        // api_secret 
        nameValuePairs.add(new BasicNameValuePair("api_secret", "5678")); 
        // Use face_detection jobs to detect face attributes, 
        // you can use different jobs to fulfill different tasks; 
        // you can refer to the documentation pages on ReKognition.com to learn more jobs 
        nameValuePairs.add(new BasicNameValuePair("jobs", "face_aggressive_gender_age_glass_smile_emotion")); 
        return nameValuePairs;
	}
	
	private String getAPIResponse(List<NameValuePair> params) {
		String sResponse = null;
        HttpClient httpclient = new DefaultHttpClient();
        // Please refer http://www.rekognition.com/docs/ for more documentation
        HttpPost httppost = new HttpPost("http://rekognition.com/func/api/index.php");
        try {
        	// parameters to http
            httppost.setEntity(new UrlEncodedFormEntity(params));
            Log.v("info", httppost.toString());
            // Make API call
            HttpResponse response = httpclient.execute(httppost);
            HttpEntity responseEntity = response.getEntity();
            if(responseEntity!=null) {
            	//You can refer to the documentation pages on ReKogntion.com to understand the structure of response
            	sResponse = EntityUtils.toString(responseEntity);
                Log.v("json_result", sResponse);  
            }
        } catch (ClientProtocolException e) {
            Log.v("ClientProtocolException", e.getMessage());
        } catch (IOException e) {
            Log.v("IOException", e.getMessage());
        }
		return sResponse;
	}
	
	protected void onActivityResult(int requestCode, int resultCode, Intent data)
	{
	    super.onActivityResult(requestCode, resultCode, data);
	    if (requestCode == PICK_PHOTO_REQUEST_CODE)
	    {
	    	if (resultCode == RESULT_OK)
		    {
		        Uri chosenImageUri = data.getData();
		        Bitmap imageBitmap;
		        ByteArrayOutputStream baos = new ByteArrayOutputStream();
		        try
		        {
		        	imageBitmap = Media.getBitmap(this.getContentResolver(), chosenImageUri);
		        }
		        catch(IOException e)
		        {
		        	return;
		        }
		        // Will do a resize if the picture is large.
		        // NOTE: this resizing code is not necessary. 
		        // It is just for faster response because uploading a large picture
		        // on a mobile device is slow.
		        // The Orbeus API can handle request with large image size
		        // See documentation for more details.
		        if (imageBitmap.getHeight() > 480 || imageBitmap.getWidth() > 640)
		        {
		        	Bitmap scaledBitMap = Bitmap.createScaledBitmap(imageBitmap, 640, 480, false);
		        	scaledBitMap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
		        }
		        else
		        {
		        	imageBitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
		        }
				byte[] b = baos.toByteArray();
				callAPIwithImageData(b, chosenImageUri.toString());
		    }
	    }
	}
	
	private void startDisplayTextActivity(String sText)
	{
		Intent intent = new Intent(this, DisplayMessageActivity.class);
		intent.putExtra(EXTRA_MESSAGE, sText);
		startActivity(intent);
		
	}
}
