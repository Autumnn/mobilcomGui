package mobilcom.com.example.com.mobilcomgui;

import android.graphics.Rect;
import android.view.Display;
import android.view.Surface;

/**
 * Created by wangqs on 6/5/15.
 */
public class ScreenData {

    private Display display;
    private int rotation;
    public static Rect rect;
    private Rect pre_rect;
    private boolean Portrait;
    private int width;
    private int height;
    public static int screen_width;
    public static int screen_height;
    private static final int MIN_FRAME_WIDTH = 50; // originally 240
    private static final int MIN_FRAME_HEIGHT = 20; // originally 240
    private static final int MAX_FRAME_WIDTH = 800; // originally 480
    private static final int MAX_FRAME_HEIGHT = 600; // originally 360

    public ScreenData(Display d){
        display = d;
        rotation = 0;
        Portrait = true;
        rect = new Rect();
        pre_rect = new Rect();
        display.getRectSize(pre_rect);
    }

    public Rect rectangle(){
        screen_width = Math.abs(pre_rect.right - pre_rect.left);
        screen_height = Math.abs(pre_rect.bottom - pre_rect.top);
        width = screen_width*3/5;
        if((screen_width*3/5) < MIN_FRAME_WIDTH){
            width = MIN_FRAME_WIDTH;
        }else if ((width*3/5) > MAX_FRAME_WIDTH){
            width = MAX_FRAME_WIDTH;
        }
        height = screen_height*1/5;
        if((screen_height*1/5) < MIN_FRAME_HEIGHT){
            height = MIN_FRAME_HEIGHT;
        }else if ((height*1/5) > MAX_FRAME_HEIGHT){
            height = MAX_FRAME_HEIGHT;
        }
        int leftOffset = (screen_width - width) / 2;
        int topOffset = (screen_height - height) / 2;
        rect.set(leftOffset, topOffset - height, leftOffset + width, topOffset + height);

        return rect;
    }

    public void get_rotation(){
        rotation = display.getRotation();
        if(rotation == Surface.ROTATION_90 || rotation == Surface.ROTATION_270){
            Portrait = true;
        }else if(rotation == Surface.ROTATION_0 || rotation == Surface.ROTATION_180){
            Portrait = false;
        }
    }


}
