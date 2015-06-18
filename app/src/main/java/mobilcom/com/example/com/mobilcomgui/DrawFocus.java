package mobilcom.com.example.com.mobilcomgui;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.os.Handler;
import android.util.AttributeSet;
import android.view.Display;
import android.view.SurfaceHolder;
import android.view.WindowManager;
import android.widget.ImageView;

/**
 * Created by wangqs on 6/11/15.
 */
public class DrawFocus extends ImageView{

    private ScreenData screenData;
    public Rect rect;
    private boolean initialized;
    private SurfaceHolder surfaceHolder;
    private WindowManager manager;
    private boolean flag;
    public Handler handler;
    private Canvas canvas;

    public DrawFocus(Context context, AttributeSet attrs) {
        super(context, attrs);
        // TODO Auto-generated constructor stub
        setFocusable(true);
        manager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        Display display = manager.getDefaultDisplay();
        screenData = new ScreenData(display);
        rect = screenData.rectangle();
    }

    Paint paint = new Paint();
    {
        paint.setAntiAlias(true);
        paint.setColor(Color.BLUE);
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(5f);//设置线宽
        paint.setAlpha(100);
    };

   // @Override
    protected void onDraw(Canvas c) {
        canvas = c;
        // TODO Auto-generated method stub
        super.onDraw(canvas);
        canvas.drawRect(rect, paint);//绘制矩形
        initialized = true;
    }

    public void adjustRect(int dW, int dH){
        int newWidth = rect.width() + dW;
        int newHeight = rect.height() + dH;
        int newleftOffset = rect.left;
        int newtopOffset = rect.top;
        rect = new Rect(newleftOffset,newtopOffset,newleftOffset + newWidth, newtopOffset + newHeight);
//        reDraw();
    }

    public void adjustPosition(int x, int y){
        int newleft = rect.left + x;
        int newright = rect.right + x;
        int newtop = rect.top + y;
        int newbottom = rect.bottom + y;
        rect = new Rect(newleft, newtop, newright, newbottom);
    }

    public Rect getP(){
         return rect;
    }



}
