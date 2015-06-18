package mobilcom.com.example.com.mobilcomgui;

import android.app.Activity;
import android.content.Intent;
import android.content.res.Configuration;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.PixelFormat;
import android.graphics.Rect;
import android.hardware.Camera;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Home extends Activity implements SurfaceHolder.Callback {

    private static final int REQUEST_CODE = 1;

    Camera camera;
    SurfaceView surfaceView;
    SurfaceHolder surfaceHolder;
    boolean previewing = false;
    LayoutInflater controlInflater = null;
    private int counter = 0;
    String path;
    public int result = 0;
    public final static String EXTRA_MESSAGE = "Rotated_Degree";

    private Rect rect;
 //   private ScreenData screenData;
 //   private CaptureActivityHandler handler;

    private DrawFocus drawFocus = null;
    private Canvas canvas;
    private int screen_width;
    private int screen_height;
//    private Handler handler;
//    private Bundle bundle;
//    private Message message;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);


        surfaceView = (SurfaceView) findViewById(R.id.camerapreview);
        surfaceView.setZOrderOnTop(false);
        surfaceHolder = surfaceView.getHolder();
        surfaceHolder.setFormat(PixelFormat.TRANSPARENT);
        surfaceHolder.addCallback(this);
        surfaceHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);

        canvas = new Canvas();
        drawFocus = (mobilcom.com.example.com.mobilcomgui.DrawFocus)findViewById(R.id.DrawFocus);

        drawFocus.onDraw(canvas);
//        rect = drawFocus.getP();
        screen_width = ScreenData.screen_width;
        screen_height = ScreenData.screen_height;

        drawFocus.setOnTouchListener(new View.OnTouchListener(){
            int lastX = -1;
            int lastY = -1;
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        lastX = -1;
                        lastY = -1;
                        return true;
                    case MotionEvent.ACTION_MOVE:
                        int currentX = (int) event.getX();
                        int currentY = (int) event.getY();
                        try {

                            final int BUFFER = 50;
                            final int BIG_BUFFER = 100;
                            if (lastX >= 0) {
                                // Adjust the size of the viewfinder rectangle. Check if the touch event occurs in the corner areas first, because the regions overlap.
                                if (((currentX >= drawFocus.rect.left + BIG_BUFFER && currentX <= drawFocus.rect.right - BIG_BUFFER) || (lastX >= drawFocus.rect.left + BIG_BUFFER && lastX <= drawFocus.rect.right - BIG_BUFFER))
                                        && ((currentY >= drawFocus.rect.top + BIG_BUFFER && currentY <= drawFocus.rect.bottom - BIG_BUFFER) || (lastY >= drawFocus.rect.top + BIG_BUFFER && lastY <= drawFocus.rect.bottom - BIG_BUFFER))) {
                                    // Top left corner: adjust both top and left sides

                                    drawFocus.adjustPosition((currentX - lastX), (currentY - lastY));

                                } else if (((currentX >= drawFocus.rect.right - BUFFER && currentX <= drawFocus.rect.right + BUFFER) || (lastX >= drawFocus.rect.right - BUFFER && lastX <= drawFocus.rect.right + BUFFER))
                                        && ((currentY <= drawFocus.rect.bottom && currentY >= drawFocus.rect.top) || (lastY <= drawFocus.rect.bottom && lastY >= drawFocus.rect.top))) {
                                    // Adjusting right side: event falls within BUFFER pixels of right side, and between top and bottom side limits
                                    drawFocus.adjustRect((currentX - lastX), 0);

                                } else if (((currentY <= drawFocus.rect.bottom + BUFFER && currentY >= drawFocus.rect.bottom - BUFFER) || (lastY <= drawFocus.rect.bottom + BUFFER && lastY >= drawFocus.rect.bottom - BUFFER))
                                        && ((currentX <= drawFocus.rect.right && currentX >= drawFocus.rect.left) || (lastX <= drawFocus.rect.right && lastX >= drawFocus.rect.left))) {
                                    // Adjusting bottom side: event falls within BUFFER pixels of bottom side, and between left and right side limits
                                    drawFocus.adjustRect(0, (currentY - lastY));

                                }
                            }
                        } catch (NullPointerException e) {
//                            Log.e(TAG, "Framing rect not available", e);
                        }
                        v.invalidate();
                        lastX = currentX;
                        lastY = currentY;
                        return true;
                    case MotionEvent.ACTION_UP:
                        lastX = -1;
                        lastY = -1;
                        return true;
                }
                return false;

            }
        });

        controlInflater = LayoutInflater.from(getBaseContext());
        View viewControl = controlInflater.inflate(R.layout.control, null);
        ViewGroup.LayoutParams layoutParamsControl = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        this.addContentView(viewControl, layoutParamsControl);

    }

    @Override
    public void onConfigurationChanged(Configuration newConfig){
        super.onConfigurationChanged(newConfig);

    }

    public void takePicture(View view) {
        camera.takePicture(myShutterCallback, myPictureCallback_RAW, myPictureCallback_JPG);
    }

    Camera.ShutterCallback myShutterCallback = new Camera.ShutterCallback() {
        @Override
        public void onShutter() {

        }
    };

    Camera.PictureCallback myPictureCallback_RAW = new Camera.PictureCallback() {
        @Override
        public void onPictureTaken(byte[] arg0, Camera arg1) {

        }
    };

    Camera.PictureCallback myPictureCallback_JPG = new Camera.PictureCallback() {

        @Override
        public void onPictureTaken(byte[] arg0, Camera arg1) {
            try {
                //Methode savePicture wird aufgerufen
                savePicture(arg0);
                Intent i = new Intent(Home.this, Edit.class);
                i.putExtra("imgpath", path);
                i.putExtra(EXTRA_MESSAGE,result);
                startActivity(i);
            }
            catch (Exception except) {
                except.getMessage();
                // Log.d("CameraActivity , scanButton.setOnClickListener(): exception = ", exc.getMessage());
            }
            finally
            {
                camera.stopPreview();
                //start previewing again on the SurfaceView in case use wants to take another pic/scan
                camera.startPreview();
            }
        }
    };

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
 /*
        initCamera(holder);
*/
        camera = Camera.open();

        //Für die Orientierung der Kamera zuständig
        setCameraDisplayOrientation(Home.this, Camera.CameraInfo.CAMERA_FACING_BACK, camera);

        //TODO Manueller Fokus kann eventuell noch eingebaut werden
        //Auto-Focus
        Camera.Parameters params = camera.getParameters();
        params.setFocusMode(Camera.Parameters.FOCUS_MODE_CONTINUOUS_PICTURE);
        camera.setParameters(params);

    }

    @Override
    protected void onResume(){
        super.onResume();
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        camera.stopPreview();
        camera.release();
        camera = null;
        previewing = false;
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
        if (previewing) {
            camera.stopPreview();
            previewing = false;
        }

        if (camera != null) {
            try {
                camera.setPreviewDisplay(surfaceHolder);
                camera.startPreview();
                previewing = true;
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void setCameraDisplayOrientation(Activity activity, int cameraId, android.hardware.Camera camera) {
        android.hardware.Camera.CameraInfo info = new android.hardware.Camera.CameraInfo();
        android.hardware.Camera.getCameraInfo(cameraId, info);
        int rotation = activity.getWindowManager().getDefaultDisplay().getRotation();
        int degrees=0;
        switch (rotation) {
            case Surface.ROTATION_0:
                degrees = 0;
                break;
            case Surface.ROTATION_90:
                degrees = 90;
                break;
            case Surface.ROTATION_180:
                degrees = 180;
                break;
            case Surface.ROTATION_270:
                degrees = 270;
                break;
        }
        if (info.facing == Camera.CameraInfo.CAMERA_FACING_FRONT) {
            result = (info.orientation + degrees) % 360;
            result = (360 - result) % 360; // compensate the mirror
        } else { // back-facing
            result = (info.orientation - degrees + 360) % 360;
        }
        camera.setDisplayOrientation(result);
    }

    public Bitmap rotateImage(Bitmap src, float degree, Rect r)
    {
        Matrix matrix = new Matrix();
        //Rotationsgrad wird festgelegt
        matrix.postRotate(degree);
        Bitmap bmp;
        int bit_width = src.getWidth();
        int bit_height = src.getHeight();
        int rec_width = r.width();
        int rec_height = r.height();
        int leftOffset;
        int topOffset;
        int width;
        int height;

        if(this.getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT){
//            bmp = Bitmap.createBitmap(src, r.top, src.getHeight()-r.right, r.height(), r.width(), matrix, true);
            height = rec_width*bit_height/screen_width;
            width = rec_height*bit_width/screen_height;
            leftOffset = r.top*bit_width/screen_height;
            topOffset = ((screen_width-r.right)*bit_height/screen_width);
        }else {
//            bmp = Bitmap.createBitmap(src, r.left, r.top, r.width(), r.height(), matrix, true);
            width = rec_width*bit_width/screen_width;
            height = rec_height*bit_height/screen_height;
            leftOffset = r.left*bit_width/screen_width;
            topOffset = r.top*bit_height/screen_height;
        }
        bmp = Bitmap.createBitmap(src, leftOffset, topOffset, width, height, matrix, true);
        return bmp;
    }

    public void savePicture(byte[] data) throws IOException {
        Bitmap imageCamera = null;
        //Bezeichnung der Bilder
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyymmddhhmmss");
        String date = dateFormat.format(new Date());
        String photoFile = "Picture_" + counter + "_" + date + ".png";
        //      File sdDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM);
        String sdDir = "/mnt/sdcard/";
        String filename = sdDir + File.separator + photoFile;

        //Pfad vom Bild
        File pictureFile = new File(filename);
        rect = drawFocus.getP();
        path = pictureFile.getAbsolutePath();

        try {
            //Liefert bitmap von Kamerabild
            imageCamera = BitmapFactory.decodeByteArray(data, 0, data.length);

            Bitmap bm;
            //gespeichertes Bild wird hier gedreht. Anderenfalls wird das Bild verkehrt herum gespeichert
            //horizontol
            //bm = rotateImage(imageCamera, -270);
            bm = rotateImage(imageCamera, 0, rect);


            //Speichern der Bilder
            FileOutputStream fos = new FileOutputStream(pictureFile);
            BufferedOutputStream bos = new BufferedOutputStream(fos);
            bm.compress(Bitmap.CompressFormat.PNG, 100, bos);

            bos.write(data);
            bos.close();



            Toast.makeText(this, "Image saved:" + photoFile, Toast.LENGTH_LONG).show();
        } catch (Exception error) {
            Log.d("File not saved: ", error.getMessage());
            Toast.makeText(this, "Image could not be saved: "+ error.getMessage(), Toast.LENGTH_LONG).show();
        }
        counter++;
    }

    public void finishActivity(View v){
        finish();
    }

    public void navigateLoadImage(View view) {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        startActivityForResult(intent, REQUEST_CODE);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        InputStream stream = null;
        if (requestCode == REQUEST_CODE && resultCode == Activity.RESULT_OK) {
            String imgpath = getImagePath(data.getData());
            Intent intent = new Intent(this, Edit.class);
            intent.putExtra("imgpath", imgpath);
            startActivity(intent);
        } else if (stream != null)
            try {
                stream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        else {
            //Toast.makeText(getApplicationContext(), "Operation aborted", Toast.LENGTH_LONG).show();
        }
    }

    public String getImagePath(Uri uri) {
        Cursor cursor = getContentResolver().query(uri, null, null, null, null);
        cursor.moveToFirst();
        String document_id = cursor.getString(0);
        document_id = document_id.substring(document_id.lastIndexOf(":") + 1);
        cursor.close();

        cursor = getContentResolver().query(
                android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                null, MediaStore.Images.Media._ID + " = ? ", new String[]{document_id}, null);
        cursor.moveToFirst();
        String path = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DATA));
        cursor.close();

        return path;
    }
/*
    private void adjustRect(int w, int h){
        bundle.putInt("width", w);
        bundle.putInt("Height", h);
        message.setData(bundle);
        message.sendToTarget();
    }
    */
}