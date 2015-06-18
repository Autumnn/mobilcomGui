package mobilcom.com.example.com.mobilcomgui;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.os.Bundle;
import android.os.SystemClock;
import android.view.Menu;
import android.view.View;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.ToggleButton;

import com.memetix.mst.language.Language;

import java.io.File;

import mobilcom.com.example.com.ocr.LocalRun;
import mobilcom.com.example.com.ocr.Offloading;
import mobilcom.com.example.com.translation.Translator;

public class Edit extends Activity{
    private ImageView iv;
    private boolean offloading;
    private Language cfrom;
    private Language cto;
    private Bitmap img;
    private Bitmap show_img;
    private File imgpath;
    private CheckBox chkbox;

    private Spinner spinnerFrom;
    private Spinner spinnerTo;

    private String recognized_text = "";
    private String translated_text = "";

    private Offloading offload;
    private LocalRun localrun;
    private Translator translator;
    private int degree=0;
    private Matrix matrix = new Matrix();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit);
        Bundle bundle = getIntent().getExtras();
        if( bundle != null) {
            //geschossenes Bild wird in ImageView geladen
            imgpath = new File((String) bundle.get("imgpath"));
            iv = (ImageView) findViewById(R.id.imageEdit);
            img = BitmapFactory.decodeFile(imgpath.getAbsolutePath());
            degree = bundle.getInt(Home.EXTRA_MESSAGE);
            matrix.postRotate((float)degree);
            show_img = Bitmap.createBitmap(img,0, 0, img.getWidth(), img.getHeight(), matrix, true);
            iv.setImageBitmap(show_img);

            //set Spinners
            spinnerFrom = (Spinner) findViewById(R.id.lang_from);
            spinnerTo = (Spinner) findViewById(R.id.lang_to);
        }
    }

    public boolean onCreateOptionsMenue ( Menu menu){

//		getMenuInflater().inflate(R.menu.edit, menu);
        return true;
    }


    public void toggleClicked(View view){
        offloading = ((ToggleButton) view).isChecked();
    }

    /*
     * Returns a string array with the Tring from the ocr and the result of the translation
     */
    // File tif String countryfrom String country to Type offloading
    public void runOCR(View v){
        Intent intent = new Intent();
        cfrom = langresolve(spinnerFrom);
        cto = langresolve(spinnerTo);
        chkbox = (CheckBox) findViewById(R.id.btn_offloading_enabled);
        offloading = chkbox.isChecked();


        if(offloading) {
            offload = new Offloading(imgpath, getString(R.string.Fu_URL), this, SystemClock.elapsedRealtime(), cfrom, cto);
            Thread thread = new Thread(offload);
            thread.start();

            while(thread.isAlive()) {   }     // Busy waiting

            recognized_text = offload.getRecognized();
            translated_text = offload.getTranslated();
        }else {
            localrun = new LocalRun();
            translator = new Translator(this);


            recognized_text = localrun.recognised(imgpath,cfrom.toString());
            if(cto != null) {
                translated_text = translator.translate(recognized_text,cfrom,cto);
            }else translated_text = "";
        }

           // RESULT TO NEXT VIEW (RESULT)
        intent = new Intent(this, Result.class);
        intent.putExtra("recognizedtext", recognized_text);
        intent.putExtra("translatedtext", translated_text);
        intent.putExtra("imgpath", imgpath.toString());
        intent.putExtra("cfrom",cfrom.toString());
        if(cto != null) {
            intent.putExtra("cto",cto.toString());
        }else {
            intent.putExtra("cto","none");
        }

        startActivity(intent);
        //Toast.makeText(Edit.this, "You pressed the button runOCR", Toast.LENGTH_LONG).show();
    }

    public Language langresolve(Spinner spinner){
        String langString = spinner.getSelectedItem().toString();
        Language lang = null;
        switch(langString){
            case "German":
                lang = Language.GERMAN;
                break;
            case "English":
                lang = Language.ENGLISH;
                break;
            case "French":
                lang = Language.FRENCH;
                break;
            case "Italian":
                lang = Language.ITALIAN;
                break;
            case "Spanish":
                lang = Language.SPANISH;
                break;
            case "Chinese":
                lang = Language.CHINESE_SIMPLIFIED;
                break;
            case "none":
                lang = null;
        }
        return lang;
    }
}