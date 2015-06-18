package mobilcom.com.example.com.mobilcomgui;

//Überflüssige Klasse

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

public class Load_Pic extends Activity {

    private static final int REQUEST_CODE = 1;
    //Button btn_edit;
    private Bitmap bitmap;
    private ImageView imageView;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_load_pic);

        //ImageView) findViewById(R.id.result);
        //Intent intent = getIntent();
    }

    public void editClick(View v) {
        Intent i = new Intent(this, Edit.class);
        i.putExtra("img", bitmap);
        startActivity(i);
    }

    //Abrufen der Bilder erfolgt über loadImageClick
    public void loadImageClick(View v) {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        startActivityForResult(intent, REQUEST_CODE);
    }

    //TODO onResult back to former Intent
    //TODO URL von Bild speichern und Kopie von Bild erstellen
    //TODO Button für loadImage darf nicht verschwinden


    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        InputStream stream = null;
        if (requestCode == REQUEST_CODE && resultCode == Activity.RESULT_OK)
            try {
                // recyle unused bitmaps
                if (bitmap != null) {
                    bitmap.recycle();
                }
                stream = getContentResolver().openInputStream(data.getData());
                bitmap = BitmapFactory.decodeStream(stream);

                //imageView.setImageBitmap(bitmap);
                imageView = (ImageView) findViewById(R.id.result);
                imageView.setImageBitmap(bitmap);

            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        if (stream != null)
            try {
                stream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
    }
}