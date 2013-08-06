package demo;
import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Point;
import android.net.Uri;
import android.view.Display;


public enum ImageDataHolder {
	
	INSTANCE;
	
	public boolean loadData(Uri uri, Activity _activity) {
		mActivity = _activity;
        try
        {
        	mBitmap = getBitmapFromInputStream(mActivity.getContentResolver().openInputStream(uri), mActivity.getContentResolver().openInputStream(uri));
        	compress();
        }
        catch(IOException e)
        {
        	return false;
        }
		return true;
	}
	
	private Bitmap getBitmapFromInputStream(InputStream is, InputStream is2) throws IOException {
    	BitmapFactory.Options o = new BitmapFactory.Options();
    	o.inJustDecodeBounds = true;
    	Bitmap bitmap = BitmapFactory.decodeStream(is, null, o);
    	nImage_width = o.outWidth;
    	nImage_height = o.outHeight;
		Display display = mActivity.getWindowManager().getDefaultDisplay();
		Point size = new Point();
		display.getSize(size);
		int nScreen_width = size.x;
		int sSreen_height = size.y;
		int nDownSize = 1;
		while (true) {
			if ((nImage_width / nDownSize < nScreen_width) && (nImage_height / nDownSize < sSreen_height )) {
				break;
			}
			nDownSize *= 2;
		}
		o.inSampleSize = nDownSize;
		o.inJustDecodeBounds = false;
		bitmap = BitmapFactory.decodeStream(is2, null, o);
		return bitmap;
	}
	
	public boolean loadData(URL url, Activity activity) {
		final URL Url = url;
		
		mActivity = activity;
		Thread trd = new Thread(new Runnable() {
			  @Override
			  public void run(){
				  mBitmap = loadBitmapFromInternet(Url);
				  compress();
			  }
			});
		trd.start();
		return true;
	}

	private void compress() {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		mBitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byteData = baos.toByteArray();
	}
	
	private Bitmap mBitmap;
	private byte[] byteData ;
	private Activity mActivity;
	
	int nImage_width;
	int nImage_height;

	public Bitmap getBitmap() {
		return mBitmap;
	}
	
	public byte[] getByteArray() {
		return byteData;
	}
	 
	private Bitmap loadBitmapFromInternet(URL url)
	{
	    Bitmap bm = null;
	    InputStream is = null;
	    BufferedInputStream bis = null;
	    try 
	    {
	        URLConnection conn = url.openConnection();
	        conn.connect();
	        is = conn.getInputStream();
	        BufferedInputStream bis1 = new BufferedInputStream(is, 8192);
	        bm = BitmapFactory.decodeStream(bis1);
	        /*
	        OutputStream os = new FileOutputStream(f);
	        bis = new BufferedInputStream(is, 8192);
	        BufferedInputStream bis1 = new BufferedInputStream(is, 8192);
	        bm = getBitmapFromInputStream(bis, bis1);*/
	    }
	    catch (Exception e) 
	    {
	        e.printStackTrace();
	    }
	    finally {
	        if (bis != null) 
	        {
	            try 
	            {
	                bis.close();
	            }
	            catch (IOException e) 
	            {
	                e.printStackTrace();
	            }
	        }
	        if (is != null) 
	        {
	            try 
	            {
	                is.close();
	            }
	            catch (IOException e) 
	            {
	                e.printStackTrace();
	            }
	        }
	    }
	    return bm;
	}
}
