package mobilcom.com.example.com.translation;

/**
 * Created by christianbruns on 24.11.14.
 */


import android.content.Context;
import android.os.AsyncTask;

import com.memetix.mst.language.Language;
import com.memetix.mst.translate.Translate;

import java.util.concurrent.ExecutionException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Diese Klasse stellt Methoden zur Übersetzung bereit
 */


public class Translator {

    private static final String CLIENT_ID = "MSI-Translator";
    private static final String CLIENT_SECRET = "4gIvNg08mnbOuNofn+GxCQSWOyetmco26s6gnQu5owc=";

    private final Context context;
    DictionaryDbAdapter mDbHelper;

    /**
     * Konstruktor um ein Translator Objekt zu erstellen.
     *
     * @param   context Der Kontext wird für die Datenbank-Schnittstelle benötigt
     */

    public Translator(Context context){
        this.context = context;
        mDbHelper = new DictionaryDbAdapter(this.context);
        mDbHelper.createDatabase();
    }


    /**
     * Diese Methode leitet die Anfrage an die MS-Translation-API weiter.
     *
     * @param   toTranslate Der zu übersetzende Text
     * @param   from        Ursprungssprache des Textes, der übersetzt werden soll
     * @param   to          Zielsprache
     * @return              übersetzter Text
     */

    private String translateOnline(String toTranslate, Language from, Language to) {
        String translatedText = null;
        try {
            translatedText = new MsTranslatorAPI(){}.execute(toTranslate, from, to).get();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }

        return translatedText;


    }

    /**
     * Diese Methode dient als Einstiegspunkt in die Übersetzung. Nach dem Erstellen eines
     * Translator-Objektes diese Methode mit dem zu übersetzenden Text und den Sprachen aufrufen.
     *
     * @param   toTranslate Der zu übersetzende Text
     * @param   from        Ursprungssprache des Textes, der übersetzt werden soll
     * @param   to          Zielsprache
     * @return              übersetzter Text
     */

    public String translate(String toTranslate, Language from, Language to){
        Pattern pattern = Pattern.compile("\\s");
        Matcher matcher = pattern.matcher(toTranslate);
        boolean found = matcher.find();

        // if toTranslate is one word only use offline dictionary else use MS-Translator-API
        if (!found) {
            if( (from.toString().equals("en") && to.toString().equals("de")) ||  (from.toString().equals("de") && to.toString().equals("en"))) {
                return translateOffline(toTranslate, from, to);
            }else {
                return translateOnline(toTranslate, from, to);
            }
        } else {
            return translateOnline(toTranslate, from, to);
        }
    }

    /**
     * Diese Methode leitet die Anfrage an die SQLite Datenbank weiter.
     *
     * @param   toTranslate Der zu übersetzende Text
     * @param   from        Ursprungssprache des Textes, der übersetzt werden soll
     * @param   to          Zielsprache
     * @return              übersetzter Text
     */

    private String translateOffline(String toTranslate, Language from, Language to) {
        String translatedText;

        mDbHelper.open();

        translatedText = mDbHelper.dbQuery(toTranslate, from.name().toLowerCase(), to.name().toLowerCase());

        mDbHelper.close();

        if (translatedText == null) {
            translatedText = translateOnline(toTranslate, from, to);
        }

        return translatedText;
    }


    /**
     * Diese Klasse implementiert einen AsyncTask, um eine Verbindung zur MS-Translation herzustellen
     */

    class MsTranslatorAPI extends AsyncTask<Object, Void, String> {
        @Override
        protected String doInBackground(Object... params) {
            Translate.setClientId(CLIENT_ID);
            Translate.setClientSecret(CLIENT_SECRET);

            String translatedText;
            try {
                translatedText = Translate.execute((String) params[0], (Language) params[1], (Language) params[2]);
            } catch(Exception e) {
                translatedText = e.toString();
            }
            return translatedText;
        }
    }


}
