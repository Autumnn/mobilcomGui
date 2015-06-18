package mobilcom.com.example.com.ocr;

import com.googlecode.tesseract.android.TessBaseAPI;

import java.io.File;

/**
 * Created by wangqs on 9/30/14.
 */
public class LocalRun {
    private String recognised_text;

    public String recognised(File image, String input){
        String lang = convertLanguage(input);
        TessBaseAPI baseAPI = new TessBaseAPI();
        //baseAPI.init(Environment.getExternalStorageDirectory().toString(),lang);
        baseAPI.init("/mnt/sdcard",lang);         //database directory is "/mnt/sdcard/"
        //baseAPI.init("/storage/sdcard1/",lang);
        baseAPI.setImage(image);
        recognised_text = baseAPI.getUTF8Text();
        baseAPI.clear();
        baseAPI.end();
        return recognised_text;
    }

    public String convertLanguage(String l){
        String result = "";
        if(l.equals("en")) result = "eng";
        if(l.equals("de")) result = "deu";
        if(l.equals("es")) result = "spa";
        if(l.equals("fr")) result = "fra";
        if(l.equals("it")) result = "ita";
        if(l.equals("zh-CHS")) result = "chi_sim";
        return result;
    }
}
