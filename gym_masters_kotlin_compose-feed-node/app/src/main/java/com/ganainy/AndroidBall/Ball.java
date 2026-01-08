package com.ganainy.AndroidBall;

class Ball {
    private float mass = 1;
    // TODO use renderscript for vector coordinates?
    private double slopeForceX;
    private double slopeForceY;
    private double accelX;
    private double accelY;
    private double speedX;
    private double speedY;
    // position is limited to [0,width], [0,height] properties
    private int width;
    private int height;
    double posX;
    double posY;

    void calculateForce(float angleX, float angleY) {
        final float g = 10; // gravity
        // slope: m*g*sin(slopeAngle), rolling: 2/7* m*g*sin(slopeAngle), given that torque for sphere (Theta) is 2/5*m*r^2
        //Log.i("Ball","angle:" + angleX + ":" + angleY);
        slopeForceX = mass * g * Math.sin(angleY) * 5 / 7;
        slopeForceY = mass * g * Math.sin(angleX) * 5 / 7;
    }

    void calculateAcceleration() {
        // acceleration
        accelX = slopeForceX/mass;
        accelY = slopeForceY/mass;
    }

    /**
     *
     * @param executionRate in sec
     */
    void updateVelocity(float executionRate) {
        speedX += accelX * executionRate;
        speedY += accelY * executionRate;
        //Log.i("Ball","speed:" + speedX + ":" + speedY);
    }

    void setArenaSize(int w, int h) {
        width = w;
        height = h;
    }

    void resetPosition() {
        posX = width/2;
        posY = height/2;
    }

    /**
     *
     * @param executionRate in sec
     */
    void updatePosition(float executionRate) {
        final int pixelsToMove = 150; // how many pixels to move by force
        if( posX < 0 ) {
            posX = 0;
            speedX = 0;
        }
        if( posX > width ) {
            posX = width;
            speedX = 0;
        }
        if( posY < 0 ) {
            posY = 0;
            speedY = 0;
        }
        if( posY > height ) {
            posY = height;
            speedY = 0;
        }

        posX += pixelsToMove * speedX * executionRate;
        posY += pixelsToMove * speedY * executionRate;
    }

}
