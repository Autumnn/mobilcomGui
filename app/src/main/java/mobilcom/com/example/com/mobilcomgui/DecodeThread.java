package mobilcom.com.example.com.mobilcomgui;

import android.os.Handler;
import android.os.Looper;

import java.util.concurrent.CountDownLatch;

/**
 * Created by wangqs on 6/5/15.
 */
public class DecodeThread extends Thread {

        private final Home activity;
        private Handler handler;
        private final CountDownLatch handlerInitLatch;

        DecodeThread(Home activity) {
            this.activity = activity;
            handlerInitLatch = new CountDownLatch(1);
        }

        Handler getHandler() {
            try {
                handlerInitLatch.await();
            } catch (InterruptedException ie) {
                // continue?
            }
            return handler;
        }

        @Override
        public void run() {
            Looper.prepare();
 //           handler = new DecodeHandler(activity);
            handlerInitLatch.countDown();
            Looper.loop();
        }
}
