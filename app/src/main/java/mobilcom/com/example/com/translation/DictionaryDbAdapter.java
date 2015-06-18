package mobilcom.com.example.com.translation;

import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import java.io.IOException;

/**
 * Diese Klasse stellt die leitet Anfragen an die Datenbank weiter
 */

public class DictionaryDbAdapter {

    protected static final String TAG = "DataAdapter";

    private final Context mContext;
    private SQLiteDatabase mDb;
    private DictionaryDB mDbHelper;

    public DictionaryDbAdapter(Context context) {
        this.mContext = context;
        mDbHelper = new DictionaryDB(mContext);
    }


    /**
     * Diese Methode erstellt  die Datenbank
     */

    public DictionaryDbAdapter createDatabase() throws SQLException {
        try {
            mDbHelper.createDataBase();
        }
        catch (IOException mIOException) {
            Log.e(TAG, mIOException.toString() + "  UnableToCreateDatabase");
            throw new Error("UnableToCreateDatabase");
        }
        return this;
    }

    /**
     * Diese Methode öffnet die Datenbank
     */

    public DictionaryDbAdapter open() throws SQLException {
        try {
            mDbHelper.openDataBase();
            mDbHelper.close();
            mDb = mDbHelper.getReadableDatabase();
        }
        catch (SQLException mSQLException) {
            Log.e(TAG, "open >>" + mSQLException.toString());
            throw mSQLException;
        }
        return this;
    }

    public void close() {
        mDbHelper.close();
    }



    /**
     * Diese Methode stellt die Anfrage an die Datenbank.
     *
     * @param   text Der zu übersetzende Text
     * @param   from        Ursprungssprache des Textes, der übersetzt werden soll
     * @param   to          Zielsprache
     * @return              übersetzter Text
     */

    public String dbQuery(String text, String from, String to) {
        String sql = "SELECT " + from + "," + to + " FROM deen WHERE " + from + " LIKE '%" + text + "%' ";
        Cursor mCur = mDb.rawQuery(sql, null);

        return getResults(mCur);

    }


    /**
     * Diese Methode bereitet das Datenbankergebnis für die Ausgabe vor.
     *
     * @param   mCur        Ergebnis der Datenbankanfrage als Cursor
     * @return              String mit Ergebnissen für die Ausgabe
     */

    private String getResults(Cursor mCur) {

        int resultLength = mCur.getCount();
        String results = "";

        if (mCur != null) {
            mCur.moveToFirst();

            for (int i = 0; i < resultLength; i++) {
                results += mCur.getString(0).trim() + "\n :: \n" + mCur.getString(1).trim() + "\n\n";
                mCur.moveToNext();
            }
        }

        return results;
    }
}