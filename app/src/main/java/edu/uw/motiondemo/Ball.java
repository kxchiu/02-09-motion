package edu.uw.motiondemo;

/**
 * A simple struct to hold a shape
 */
public class Ball {

    public float cx; //center
    public float cy;
    public float radius; //radius
    public float dx; //velocity
    public float dy;

    public Ball(float cx, float cy, float radius) {
        this.cx = cx;
        this.cy = cy;
        this.radius = radius;
        this.dx = 0;
        this.dy = 0;
    }

}
