package mobilcom.com.example.com.translation;

/**
 * Created by christianbruns on 25.11.14.
 */

import android.content.Context;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class DictionaryDB extends SQLiteOpenHelper {


    private static String TAG = "DataBaseHelper"; // Tag just for the LogCat window
    //destination path (location) of our database on device
    private static String DB_PATH = "";
    private static String DB_NAME ="dictionary";// Database name
    private SQLiteDatabase mDataBase;
    private final Context mContext;




    public DictionaryDB(Context context) {
        super(context, DB_NAME, null, 1);// 1? its Database Version
        if (android.os.Build.VERSION.SDK_INT >= 17) {
            DB_PATH = context.getApplicationInfo().dataDir + "/databases/";
        } else {
            DB_PATH = "/data/data/" + context.getPackageName() + "/databases/";
        }
        this.mContext = context;
    }

    /**
     * Diese Methode erstellt die Datenbank
     */

    public void createDataBase() throws IOException {
        //If database not exists copy it from the assets

        boolean mDataBaseExist = checkDataBase();
        if(!mDataBaseExist) {
            this.getReadableDatabase();
            this.close();
            try {
                //Copy the database from assests
                copyDataBase();
                Log.e(TAG, "createDatabase database created");
            }
            catch (IOException mIOException) {
                throw new Error("ErrorCopyingDataBase");
            }
        }
    }



    /**
     * Diese Methode prüft, ob bereits eine Datenbank vorhanden ist
     *
     * @return              True, wenn Datenbank vorhanden
     */
    private boolean checkDataBase() {
        File dbFile = new File(DB_PATH + DB_NAME);
        //Log.v("dbFile", dbFile + "   "+ dbFile.exists());
        return dbFile.exists();
    }

    /**
     * Diese Methode kopiert die SQLite Datenbank aus dem Assets Folder an die richtige Stelle im Android System
     *
     */
    private void copyDataBase() throws IOException {
        InputStream mInput = mContext.getAssets().open(DB_NAME);
        String outFileName = DB_PATH + DB_NAME;
        OutputStream mOutput = new FileOutputStream(outFileName);
        byte[] mBuffer = new byte[1024];
        int mLength;
        while ((mLength = mInput.read(mBuffer))>0) {
            mOutput.write(mBuffer, 0, mLength);
        }
        mOutput.flush();
        mOutput.close();
        mInput.close();
    }


    /**
     * Diese Methode öffnet die Datenbank, so dass Anfragen möglich sind
     *
     * @return              True, wenn Datenbank geöffnet
     */
    public boolean openDataBase() throws SQLException {
        String mPath = DB_PATH + DB_NAME;
        //Log.v("mPath", mPath);
        mDataBase = SQLiteDatabase.openDatabase(mPath, null, SQLiteDatabase.CREATE_IF_NECESSARY);
        //mDataBase = SQLiteDatabase.openDatabase(mPath, null, SQLiteDatabase.NO_LOCALIZED_COLLATORS);
        return mDataBase != null;
    }

    @Override
    public synchronized void close() {
        if(mDataBase != null)
            mDataBase.close();
        super.close();
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {

    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i2) {

    }

}


















//    private final static String DB_NAME = "dictionary";
//    private String databasePath;
//
//
//    public DictionaryDB(Context context) {
//        super(context,
//                DB_NAME,
//                null,
//                Integer.parseInt(context.getResources().getString(R.string.version)));
//
//        this.databasePath = context.getDatabasePath(this.DB_NAME).toString();
//    }
//
//    public SQLiteDatabase openDataBase() {
//        try{
//            String mypath = databasePath;
//            return SQLiteDatabase.openDatabase(mypath, null,SQLiteDatabase.OPEN_READWRITE);
//        }catch(SQLiteException sle){
//            System.out.println("Exception while opning database : "+sle);
//        }
//        return null;
//
//    }
//
//    @Override
//    public void onCreate(SQLiteDatabase sqLiteDatabase) {
//
//    }
//
//    @Override
//    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i2) {
//
//    }
//}
