package DefinitlyNotSnake;

import processing.core.PApplet;
import processing.core.PVector;

/**
 * Created by anton on 2017-03-20.
 */

public class Main extends PApplet {

    Tentacle tentacle;
    Ball firstBall;
    Ball lastBall;
    float ballsize = 15;

    int points = 0;
    boolean gameover = false;

    /*ListIterator<Wall> itw;
    int level = 0;
    public List<Wall> walls;*/

    public static void main(String[] args) {
        PApplet.main("DefinitlyNotSnake.Main");
    }

    public void settings() {
        size(1000, 700);
        fullScreen();
    }

    public void setup() {
        tentacle = new Tentacle(this, 30, 7.5f, width / 2, height / 2);
        startGame();
    }

    public void startGame() {
        points = 0;
        firstBall = new Ball(random(width / 2, width - ballsize), random(0, -50), true);
        lastBall = new Ball(random(width / 2, width - ballsize), random(0, -50), false);
        firstBall.next = lastBall;
        gameover = false;
    }

    public void keyPressed() {
        if (gameover && (key == 'r' || key == 'R')) {
            startGame();
        }
    }

    public void draw() {
        background(51);
        if (!gameover) {
            strokeWeight(20);
            stroke(15);
            line(0, 0, 0, height);
            line(0, height - 25, width, height - 25);
            line(width, 0, width, height);

            Ball current = firstBall;
            while (current != null) {
                if (current.show) {
                    current.update();
                } else {
                    current.start(random(ballsize, width - ballsize), random(0, -50));
                    if (points % 5 == 0) {
                        addBall(new Ball(random(ballsize, width - ballsize), random(0, -50), true));
                    }
                    if (points % 10 == 0) {
                        addBall(new Ball(random(ballsize, width - ballsize), random(0, -50), false));
                    }
                }
                current = current.next;
            }
            fill(200);
            textAlign(CENTER);
            textSize(32);
            text(points, width - 20, 32);
            tentacle.update(mouseX, mouseY);
        } else {
            textAlign(CENTER);
            textSize(60);
            fill(255, 100, 0);
            text("GAME OVER", width / 2, height / 2);
            textSize(32);
            text("PRESS R TO START AGAIN", width / 2, height / 2 + 32);
            text("SCORE: " + points, width / 2, height / 2 + 64);
        }
    }

    void addBall(Ball b) {
        lastBall.setNext(b);
        lastBall = b;
    }

    class Ball {

        PVector pos;
        PVector vel = new PVector();
        PVector gravity = new PVector(0, 0.1f);
        float size = ballsize;
        int fade = 200;
        boolean show = true;
        boolean edible;
        Ball next;

        Ball(float x, float y, boolean edible_) {
            edible = edible_;
            start(x, y);
        }

        void start(float x, float y) {
            vel.set(random(3, 7), random(3, 7));
            vel.x = vel.x * (round(random(1, 2)) == 1 ? -1 : 1);
            vel.y = vel.y * (round(random(1, 2)) == 1 ? 1 : -1);
            pos = new PVector(x, y);
            show = true;
            fade = 200;
        }

        void update() {
            vel.add(gravity);
            if (pos.x + vel.x > width - ballsize || pos.x + vel.x < ballsize) {
                vel.x = vel.x * (-1);
                vel.mult(0.95f);
            }
            if (pos.y + vel.y > height - ballsize) {
                vel.y = vel.y * (-1);
                vel.mult(0.95f);
            }
            if (vel.mag() < 4 && pos.y > height * 0.8f) {
                fade -= 10;
                if (fade == 0) start(random(size, width - size), random(0, -50));
            }
            pos.add(vel);
            show();
        }

        void show() {
            noStroke();
            if (edible) fill(100, 255, 0, fade);
            else fill(255, 50, 0, fade);
            ellipse(pos.x, pos.y, size, size);
        }

        void setNext(Ball b) {
            next = b;
        }
    }

    class Wall {

        PVector pos;
        float height;
        float angle;
        float thickness;

        Wall() {
            thickness = random(15, 30);
            height = random(height / 4, height / 2);
            angle = random(-PI / 4, PI / 4);
            pos = new PVector(random(thickness * 3, width - (thickness * 3)), height);
        }

        void draw() {

        }
    }

    class Tentacle {

        PVector pos;
        PApplet graphics;
        Segment[] segments;
        Segment first;
        int joints;
        float length;

        Tentacle(PApplet graphics_, int size_, float length_, float x, float y) {
            graphics = graphics_;
            joints = size_;
            length = length_;
            pos = new PVector(x, y);
            segments = new Segment[joints];
            init();
        }

        void init() {
            Segment current = new Segment(graphics, pos.x, pos.y, length, radians(0), 2);
            for (int i = 1; i < joints; i++) {
                Segment next = new Segment(graphics, current, length, radians(0), map(i, 1, joints, 2, 10));
                current = next;
            }
            first = current;
        }

        void checkCollision(Segment s) {

            Ball ball = firstBall;
            while (ball != null) {
                if (s.checkCollision(ball.pos.x, ball.pos.y) && ball.show) {
                    ball.show = false;
                    if (!ball.edible) gameover = true;
                    else points++;
                }
                ball = ball.next;
            }
        }

        void update(float tx, float ty) {
            first.follow(tx, ty);
            first.update(length + points * 0.25f);
            first.show();
            checkCollision(first);
            Segment current = first.parent;
            while (current != null) {
                current.follow();
                current.update(length + points * 0.25f);
                current.show();
                checkCollision(current);
                current = current.parent;
            }
        }
    }
}