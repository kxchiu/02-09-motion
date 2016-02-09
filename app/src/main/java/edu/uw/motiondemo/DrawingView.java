package edu.uw.motiondemo;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

/**
 * Created by joelross on 2/8/16.
 */
public class DrawingView extends View {

    private static final String TAG = "SurfaceView";

    private int viewWidth, viewHeight; //size of the view

    private Bitmap bmp; //image to draw on

    //drawing values
    private Paint redPaint; //drawing variables (pre-defined for speed)
    public Ball ball;


    /**
     * We need to override all the constructors, since we don't know which will be called
     * All the constructors eventually call init()
     */
    public DrawingView(Context context) {
        this(context, null);
    }

    public DrawingView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public DrawingView(Context context, AttributeSet attrs, int defaultStyle) {
        super(context, attrs, defaultStyle);

        viewWidth = 1; viewHeight = 1; //positive defaults; will be replaced when #surfaceChanged() is called

        //set up drawing variables ahead of timme
        redPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        redPaint.setColor(Color.RED);
    }

    /**
     * Override method that is called when the size of the display is specified (or changes
     * due to rotation).
     */
    @Override
    public void onSizeChanged(int w, int h, int oldw, int oldh)
    {
        //store new size of the view
        viewWidth = w;
        viewHeight = h;

        //create a properly-sized bitmap to draw on
        bmp = Bitmap.createBitmap(viewWidth, viewHeight, Bitmap.Config.ARGB_8888);

        ball = new Ball(viewWidth/2, viewHeight/2, 100);

    }

    /**
     * Override this method to specify drawing. It is like our "paintComponent()" method.
     */
    @Override
    public void onDraw(Canvas canvas)
    {
        super.onDraw(canvas); //make sure to have the parent do any drawing it is supposed to!

        ball.cy += 3;

        canvas.drawColor(Color.BLACK); //black out the background

        //red ball
        canvas.drawCircle(ball.cx, ball.cy, ball.radius, redPaint); //we can draw directly onto the canvas

        //draw 5 blue lines of width 1 pixel
        for(int x=50; x<viewWidth-50; x++) { //most of the width
            for(int y=100; y<110; y++) { //10 pixels high
                bmp.setPixel(x, y, Color.BLUE); //we can also set individual pixels in a Bitmap (like a BufferedImage)
            }
        }
        canvas.drawBitmap(bmp, 0, 0, null); //and then draw the BitMap onto the canvas.
        /*
         * Each of these images are seem as one frame.
         * To draw in animation, we have to be able to tell the canvas to redraw itself.
         */
        //Canvas bmc = new Canvas(bmp); //we can also make a canvas out of a Bitmap to draw on that (like fetching g2d from a BufferedImage)

        //tells the canvas to redraw
        invalidate();
    }

}
