package demo;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.orbeus.R;

public class DisplayMessageActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display_message);

        Intent intent = getIntent();
        String message = intent.getStringExtra(MainActivity.JSON);

        LinearLayout layout = (LinearLayout) this.findViewById(R.id.activity_display);
        Bitmap bitmap = null;
        StringBuilder text = new StringBuilder();
        try {
            JSONObject jsonRoot = new JSONObject(message);
            if (jsonRoot.has("face_detection")) {
                bitmap = ImageDataHolder.INSTANCE.getBitmap().copy(Bitmap.Config.ARGB_8888 ,true);
                JSONArray jsonArray = jsonRoot.getJSONArray("face_detection");
                int count = 0;
                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject json = jsonArray.getJSONObject(i);
                    if (json.getDouble("confidence") >= 0.1) {
                        count ++;
                        this.drawTag(json, count, bitmap);
                        this.setText(json, count, text);
                    }
                }
            } else if (jsonRoot.has("scene_understanding")) {
                bitmap = ImageDataHolder.INSTANCE.getBitmap();
                JSONArray jsonArray = jsonRoot.getJSONArray("scene_understanding");
                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject json = jsonArray.getJSONObject(i);
                    if (i > 0) {
                        text.append("\n");
                    }
                    text.append(String.format("%s: %f", json.getString("label"), json.getDouble("score")));
                }
            }
        } catch(JSONException e) {
            Log.v("Exception", e.getMessage());
        }
        ImageView drawView = new ImageView(this);
        drawView.setImageBitmap(bitmap);
        drawView.setBackgroundColor(Color.BLACK);
        layout.addView(drawView);

        TextView textView = new TextView(this);
        textView.setText(text);
        layout.addView(textView);
    }

    private void setText(JSONObject json, int index, StringBuilder text) throws JSONException {
        if (index > 1) {
            text.append("\n");
        }
        text.append(String.format(
                "Face %d -- confidence: %.2f; glass: %.2f; age: %.2f; eye_closed: %.2f.",
                index,
                json.getDouble("confidence"),
                json.getDouble("glasses"),
                json.getDouble("age"),
                json.getDouble("eye_closed")));
    }

    private void drawTag(JSONObject json, int index, Bitmap bitmap) throws JSONException {
        Canvas canvas = new Canvas(bitmap);
        Paint paint = new Paint();
        JSONObject box = json.getJSONObject("boundingbox");
        JSONObject size = box.getJSONObject("size");
        JSONObject tl = box.getJSONObject("tl");
        float height = (float) size.getDouble("height");
        float width = (float) size.getDouble("width");
        float top = (float) tl.getDouble("y");
        float left = (float) tl.getDouble("x");
        paint.setColor(json.getDouble("sex") >= 0.5 ? Color.BLUE : Color.RED);
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(3);
        canvas.drawRect(left, top, left + width, top + height, paint);
        paint.setStrokeWidth(2);
        paint.setTextSize(20);
        paint.setTextAlign(Paint.Align.CENTER);
        canvas.drawText(String.format("%d", index), left + width / 2, (float) (top + height + 25), paint);

        // Draw five points.
        paint.setStyle(Paint.Style.FILL);
        JSONObject eyeLeft = json.getJSONObject("eye_left");
        JSONObject eyeRight = json.getJSONObject("eye_right");
        JSONObject mouthLeft = json.getJSONObject("mouth_l");
        JSONObject mouthRight = json.getJSONObject("mouth_r");
        JSONObject nose = json.getJSONObject("nose");
        int pointWidth = (int) (width / 30);
        canvas.drawCircle((float) eyeLeft.getDouble("x"), (float) eyeLeft.getDouble("y"), pointWidth, paint);
        canvas.drawCircle((float) eyeRight.getDouble("x"), (float) eyeRight.getDouble("y"), pointWidth, paint);
        canvas.drawCircle((float) mouthLeft.getDouble("x"), (float) mouthLeft.getDouble("y"), pointWidth, paint);
        canvas.drawCircle((float) mouthRight.getDouble("x"), (float) mouthRight.getDouble("y"), pointWidth, paint);
        canvas.drawCircle((float) nose.getDouble("x"), (float) nose.getDouble("y"), pointWidth, paint);
    }
}
