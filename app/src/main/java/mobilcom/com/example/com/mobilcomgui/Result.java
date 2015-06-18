package mobilcom.com.example.com.mobilcomgui;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.SystemClock;
import android.text.method.ScrollingMovementMethod;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.memetix.mst.language.Language;

import java.io.File;

import mobilcom.com.example.com.ocr.Offloading;
import mobilcom.com.example.com.translation.Translator;

public class Result extends Activity {
    private Button btn_tranlate;
    private EditText editText_original;
    private EditText editText_translated;
    private TextView textView1;
    private TextView textView2;
    private Spinner translate_to;
    private String recognized_text = "";
    private String translated_text = "";
    private Language cfrom;
    private Language cto;
    private File imgpath;

    private Translator translator;
    private Offloading offload;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_result);
        Bundle bundle = getIntent().getExtras();
        Intent intent = getIntent();

        btn_tranlate = (Button)findViewById(R.id.btn_translate_again);
        editText_original = (EditText)findViewById(R.id.editText1);
        editText_translated = (EditText)findViewById(R.id.editText2);
        textView1 = (TextView)findViewById(R.id.textView1);
        textView2 = (TextView)findViewById(R.id.textView2);


        if(bundle!=null) {
            recognized_text = (String) bundle.get("recognizedtext");
            translated_text = (String) bundle.get("translatedtext");
            imgpath = new File((String) bundle.get("imgpath"));
            cfrom = Language.fromString((String)bundle.get("cfrom"));
            String tmp_cto = ((String)bundle.get("cto"));
            if(tmp_cto.equals("none")) {
                cto = null;
                textView2.setText("Translated Result");
            }else {
                cto = Language.fromString(tmp_cto);
                textView2.setText("Translated Result [" + cfrom.toString() + " -> " + cto.toString() + "]");
            }
            textView1.setText("Original Text ["+cfrom.toString()+"]");

            setEditTextFields();
        }
    }

    public boolean onCreateOptionsMenue ( Menu menu){

//		getMenuInflater().inflate(R.menu.edit, menu);
        return true;
    }


    public void repeatWithOffloading(View v){
        translate_to = (Spinner) findViewById(R.id.lang_to2);
        Language tmp_lang = langresolve(translate_to);
        if(tmp_lang != null) cto = tmp_lang;

        offload = new Offloading(imgpath, getString(R.string.Fu_URL), this, SystemClock.elapsedRealtime(), cfrom, cto);
        Thread thread = new Thread(offload);
        thread.start();

        while(thread.isAlive()) { }     // Busy waiting

        recognized_text = offload.getRecognized();
        translated_text = offload.getTranslated();
        setEditTextFields();
        if(cto != null) {
            textView2.setText("Translated Result ["+cto.toString()+"]");
        }
        Toast.makeText(Result.this,"Offloading complete", Toast.LENGTH_LONG).show();

    }

    public void nextPic(View v){
        Intent i = new Intent(this, Home.class);
        startActivity(i);
    }

    public void translateResultText(View v){
        translate_to = (Spinner) findViewById(R.id.lang_to2);
        cto = langresolve(translate_to);

        if(cto != null) {
            if(!recognized_text.equals("")) {
                translator = new Translator(this);
                editText_original = (EditText)findViewById(R.id.editText1);
                translated_text = translator.translate(editText_original.getText().toString(),cfrom,cto);
                textView2.setText("Translated Result ["+cto.toString()+"]");
                setEditTextFields();
                Toast.makeText(Result.this,"Re-translation complete", Toast.LENGTH_LONG).show();
            }
        }

    }

    public void setEditTextFields() {
//        editText_original.setMaxLines(5);
        editText_original.setVerticalScrollBarEnabled(true);
        editText_original.setMovementMethod(new ScrollingMovementMethod());
        editText_original.setText(recognized_text);
//        editText_translated.setMaxLines(5);
        editText_translated.setVerticalScrollBarEnabled(true);
        editText_translated.setMovementMethod(new ScrollingMovementMethod());
        editText_translated.setText(translated_text);

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
            case "none":
                lang = null;
        }
        return lang;
    }
}