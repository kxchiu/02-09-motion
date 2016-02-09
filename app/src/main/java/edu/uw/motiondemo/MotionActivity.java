package edu.uw.motiondemo;

import android.app.Activity;
import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.support.v4.view.GestureDetectorCompat;
import android.support.v4.view.MotionEventCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;

import java.util.Arrays;
import java.util.List;

public class MotionActivity extends Activity {

    private static final String TAG = "**MOTION**";

    private DrawingSurfaceView view;

    private GestureDetectorCompat mDetector;


    @Override
    protected void onCreate(
            Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_motion);

        view = (DrawingSurfaceView)findViewById(R.id.drawingView);

        mDetector = new GestureDetectorCompat(this, new MyGestureListener());
    }

    //handles the touch event on the device screen (need to override!)
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        //log touch event details (action type, coordinate, etc)
        //Log.v(TAG, "" + event);

        //check the gesture with our own detector
        //if gesture is detected, return true and do nothing more!
        boolean gesture = mDetector.onTouchEvent(event);
        if (gesture) return true;

        //extracts the type of event from the motion
        int action = MotionEventCompat.getActionMasked(event);

        //much like selecting items from a menu
        switch (action) {
            case MotionEvent.ACTION_DOWN:
                Log.v(TAG, "Finger Down!");
                return true;
            case MotionEvent.ACTION_UP:
                Log.v(TAG, "Finger Up!");
                //note: should be synchronized!
                view.ball.cx = event.getX();
                view.ball.cy = event.getY();
                return true;
            case MotionEvent.ACTION_POINTER_DOWN:
                //if there's a second finger, then the pseudo code is like:
                //int mSecondPointId = event.getPointerId(1);

            case MotionEvent.ACTION_MOVE:
                //view.ball.cx = event.getX();
                //view.ball.cy = event.getY();
                //event.findPointerIndex(mSecondPointId)
                    //respond to second finger


                return true;
            default:
                return super.onTouchEvent(event);
        }
    }

    class MyGestureListener extends GestureDetector.SimpleOnGestureListener {

        @Override
        public boolean onDown(MotionEvent e) {

            return true; //we've got this
        }

        @Override
        public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
            float scaleFactor = .03f;

            //fling!
            Log.v(TAG, "Fling! " + velocityX + ", " + velocityY);

            view.ball.dx = velocityX * scaleFactor;
            view.ball.dy = -1 * velocityY * scaleFactor;

            return true;
        }
    }

    class MyScaleListener extends ScaleGestureDetector.SimpleOnScaleGestureListener {
        @Override
        public boolean onScale(ScaleGestureDetector detector) {

            return super.onScale(detector);
        }
    }
}
