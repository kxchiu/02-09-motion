package edu.uw.motiondemo;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

/**
 * An example SurfaceView for generating graphics on
 * @author Joel Ross
 * @version 2/7/2016
 */
public class DrawingSurfaceView extends SurfaceView implements SurfaceHolder.Callback {

    private static final String TAG = "SurfaceView";

    private int viewWidth, viewHeight; //size of the view

    private Bitmap bmp; //image to draw on

    private SurfaceHolder mHolder; //the holder we're going to post updates to

    private DrawingRunnable mRunnable; //the code htat we'll want to run on a background thread
    //we will be making our own thread
    private Thread mThread; //the background thread

    private Paint redPaint; //drawing variables (pre-defined for speed)

    //the Ball; public for allowing interactions
    public Ball ball;

    /**
     * We need to override all the constructors, since we don't know which will be called
     */
    public DrawingSurfaceView(Context context) {
        this(context, null);
    }

    public DrawingSurfaceView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public DrawingSurfaceView(Context context, AttributeSet attrs, int defaultStyle) {
        super(context, attrs, defaultStyle);

        viewWidth = 1; viewHeight = 1; //positive defaults; will be replaced when #surfaceChanged() is called

        // register our interest in hearing about changes to our surface
        mHolder = getHolder();
        mHolder.addCallback(this);

        mRunnable = new DrawingRunnable();

        //set up drawing variables ahead of timme
        redPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        redPaint.setColor(Color.RED);

        //The Ball
        ball = new Ball(100,100,100);
        //ball.dx = 1;
    }


    /**
     * Helper method for the "animation loop"
     */
    public void update(){

        ball.cx += ball.dx; //move
        ball.cy += ball.dy;

        //slow down
        ball.dx *= 0.95;
        ball.dy *= 0.95;

        //if(ball.dx < 1) ball.dx = 0;
        //if(ball.dy < 1) ball.dy = 0;

        /* hit detection */
        if(ball.cx + ball.radius > viewWidth) { //left bound
            ball.cx = viewWidth - ball.radius;
            ball.dx *= -1;
        }
        else if(ball.cx - ball.radius < 0) { //right bound
            ball.cx = ball.radius;
            ball.dx *= -1;
        }
        else if(ball.cy + ball.radius > viewHeight) { //bottom bound
            ball.cy = viewHeight - ball.radius;
        }
        else if(ball.cy - ball.radius < 0) { //top bound
            ball.cy = ball.radius;
            ball.dy *= -1;
        }
    }


    /**
     * Helper method for the "render loop"
     * @param canvas The canvas to draw on
     */
    public void render(Canvas canvas){
        if(canvas == null) return; //if we didn't get a valid canvas for whatever reason

        canvas.drawColor(Color.BLACK); //black out the background

        canvas.drawCircle(ball.cx, ball.cy, ball.radius, redPaint); //we can draw directly onto the canvas
    }


    //when our surface is first created
    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        // create thread only; it's started in surfaceCreated()
        Log.v(TAG, "making new thread");
        mThread = new Thread(mRunnable);
        mRunnable.setRunning(true); //turn on the runner
        mThread.start(); //start up the thread when surface is created

    }

    //when the dimension of the surface is changed
    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
        synchronized (mHolder) { //synchronized to keep this stuff atomic
            viewWidth = width;
            viewHeight = height;
            bmp = Bitmap.createBitmap(viewWidth, viewHeight, Bitmap.Config.ARGB_8888); //new buffer to draw on

            //Remake ball
            ball = new Ball(viewWidth/2, viewHeight/2, 100);
        }
    }

    //when our surface goes away (invisible, stopped, or paused), stop the thread
    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        // we have to tell thread to shut down & wait for it to finish, or else
        // it might touch the Surface after we return and explode
        mRunnable.setRunning(false); //turn off
        boolean retry = true;

        //repetitively try to stop the thread until it's stopped
        while(retry) {
            try {
                mThread.join();
                retry = false;
            } catch (InterruptedException e) {
                //will try again...
            }
        }
        Log.d(TAG, "Drawing thread shut down.");
    }




    /**
     * An inner class representing a runnable that does the drawing. Animation timing could go in here.
     * http://obviam.net/index.php/the-android-game-loop/ has some nice details about using timers to specify animation
     */
    public class DrawingRunnable implements Runnable {

        private boolean isRunning; //whether we're running or not (so we can "stop" the thread)

        public void setRunning(boolean running){
            this.isRunning = running;
        }

        public void run() {
            Canvas canvas;
            //infinite loop
            while(isRunning)
            {
                canvas = null;
                try {
                    canvas = mHolder.lockCanvas(); //grab the current canvas
                    synchronized (mHolder) {
                        //update(); //update the game (do the animation)
                        render(canvas); //redraw the screen
                    }
                }
                finally { //no matter what (even if something goes wrong), make sure to push the drawing so isn't inconsistent
                    if (canvas != null) {
                        mHolder.unlockCanvasAndPost(canvas);
                    }
                }
            }
        }
    }
}