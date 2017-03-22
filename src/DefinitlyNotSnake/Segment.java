package DefinitlyNotSnake;

import processing.core.PApplet;
import processing.core.PVector;

/**
 * Created by anton on 2017-03-20.
 */
public class Segment {

    PApplet graphics;
    PVector a;
    PVector b = new PVector();
    Segment child;
    Segment parent;
    float strokeW;
    float len;
    float angle;

    Segment(PApplet graphics_, float x, float y, float len_, float angle_, float strokeW_) {
        graphics = graphics_;
        a = new PVector(x, y);
        len = len_;
        angle = angle_;
        strokeW = strokeW_;
        calculateB();
    }

    Segment(PApplet graphics_, Segment parent_, float len_, float angle_, float strokeW_) {
        graphics = graphics_;
        parent = parent_;
        parent.child = this;
        strokeW = strokeW_;
        a = parent.b.copy();
        len = len_;
        angle = angle_;
        calculateB();
    }

    void calculateB() {
        b.set(a.x + len*PApplet.cos(angle), a.y + len*PApplet.sin(angle));
    }

    void update(float newLength) {
        len = newLength;
        calculateB();
    }

    boolean checkCollision(float x, float y) {
        if(PVector.dist(new PVector(a.x, a.y), new PVector(x, y)) < strokeW*2) {
            return true;
        }
        return false;
    }

    void follow() {
        float targetX = child.a.x;
        float targetY = child.a.y;
        follow(targetX, targetY);
    }

    void follow(float tx, float ty) {
        PVector target = new PVector(tx, ty);
        PVector dir = PVector.sub(target, a);
        angle = dir.heading();

        b.set(dir);

        dir.setMag(len);
        dir.mult(-1);

        a = PVector.add(target, dir);
    }

    void show() {
        graphics.stroke(255);
        graphics.strokeWeight(strokeW);
        graphics.line(a.x, a.y, b.x, b.y);
    }
}
