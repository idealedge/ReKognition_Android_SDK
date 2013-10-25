package demo;

import rekognition.RekoSDK;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.orbeus.R;

public class MainActivity extends Activity {

    public final static String JSON = "demo.JSON";
    private final static int PICK_PHOTO_REQUEST_CODE = 1;
    private final static String ARG_IMAGE_BYTES = "image_holder";
    private ProgressDialog mDialog = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        if (savedInstanceState != null) {
            ImageDataHolder.INSTANCE.recoverFromByteArray(savedInstanceState.getByteArray(ARG_IMAGE_BYTES));
            this.onImagePicked();
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putByteArray(ARG_IMAGE_BYTES, ImageDataHolder.INSTANCE.getByteArray());
    }

    public void onPickImage(View view) {
        //Body of your click handler
        Intent photoPickerIntent = new Intent(Intent.ACTION_GET_CONTENT);
        photoPickerIntent.setType("image/*");
        startActivityForResult(photoPickerIntent, PICK_PHOTO_REQUEST_CODE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_PHOTO_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                Uri chosenImageUri = data.getData();
                ImageDataHolder.INSTANCE.loadData(chosenImageUri, this);
                this.onImagePicked();
            }
        }
    }
    
    private void onImagePicked() {
        Button pickButton = (Button) this.findViewById(R.id.button_pick_image);
        pickButton.setText(R.string.repick);
        Button faceButton = (Button) this.findViewById(R.id.button_face_detection);
        Button sceneButton = (Button) this.findViewById(R.id.button_scene_understanding);
        faceButton.setVisibility(View.VISIBLE);
        sceneButton.setVisibility(View.VISIBLE);
    }

    public void onFaceDetection(View view) {
        RekoSDK.APICallback callback = new RekoSDK.APICallback() {
            public void gotResponse(String sResponse) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        mDialog.dismiss();
                    }
                });
                startDisplayMessageActivity(sResponse);   
            }
        };
        mDialog = ProgressDialog.show(this, "", "Loading...");
        RekoSDK.face_detect(ImageDataHolder.INSTANCE.getByteArray(), callback);
    }

    public void onSceneUnderstanding(View view) {
        RekoSDK.APICallback callback = new RekoSDK.APICallback() {
            public void gotResponse(String sResponse){
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        mDialog.dismiss();
                    }
                });
                startDisplayMessageActivity(sResponse);
            }
        };
        mDialog = ProgressDialog.show(this, "", "Loading...");
        RekoSDK.scene_understand(ImageDataHolder.INSTANCE.getByteArray(), callback);
    }

    private void startDisplayMessageActivity(String message) {
        Intent intent = new Intent(this, DisplayMessageActivity.class);
        intent.putExtra(JSON, message);
        startActivity(intent);
    }
}
