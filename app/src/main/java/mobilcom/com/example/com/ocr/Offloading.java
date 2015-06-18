package mobilcom.com.example.com.ocr;

import android.content.Context;
import android.os.Looper;
import android.widget.Toast;

import com.memetix.mst.language.Language;

import java.io.File;

/**
 * Created by wangqs on 10/1/14.
 *
 *
 * Diese Klasse wurde aus dem ursprünglichen Offloading-Projekt übernommen und von uns angepasst
 * angepasst.
 *
 */

public class Offloading implements Runnable {

    private String temp_received;
    private String temp_recognized = "";
    private String temp_translated = "";
    private File image;
    private String webURL;
    private Context current;
    private long StartTime;
    private boolean Network_State;
    private Language from;
    private Language to;

    public Offloading(File f, String web, Context c, long s, Language from, Language to){
        this.image = f;
        this.webURL = web;
        this.current = c;
        this.StartTime = s;
        this.Network_State = false;
        this.from = from;
        this.to = to;

    }

    public void run() {

        android.os.Process.setThreadPriority(android.os.Process.THREAD_PRIORITY_FOREGROUND);
        RemoteRun remoterun = new RemoteRun();


            temp_received = null;

            try {
                temp_received = remoterun.run(image, webURL, current, StartTime, from, to);
                GetElement getelement = new GetElement();
                try {
                    temp_recognized = getelement.getElementValueFromXML(temp_received, "Identifiedtext");
                    temp_translated = getelement.getElementValueFromXML(temp_received, "Translatedtext");

                } catch (Exception e) {
                    temp_recognized = "Offloading Content Catch Failure";
                }

            } catch (Exception e) {
                temp_recognized = temp_received;
            }

    }

    public String getRecognized(){
        return temp_recognized;
    }

    public String getTranslated() {
        return temp_translated;
    }

}
