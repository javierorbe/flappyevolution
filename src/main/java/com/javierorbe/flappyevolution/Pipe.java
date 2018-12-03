/*
 * Copyright (c) 2018 Javier Orbe
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package com.javierorbe.flappyevolution;

import java.awt.*;

import static com.javierorbe.flappyevolution.FlappyEvolution.WINDOW_WIDTH;
import static com.javierorbe.flappyevolution.FlappyEvolution.WINDOW_HEIGHT;
import static com.javierorbe.math.util.MathUtils.random;

/**
 * Represents a pipe in the game.
 * Each pipe is separated in a top pipe and a bottom pipe.
 */
class Pipe {

    private static final int PIPE_WIDTH = 48;
    private static final double PIPE_SPEED = 2;
    private static final int PIPE_SPACING = 160;

    private double top;
    private double bottom;

    private int x;

    private boolean highlight = false;
    private boolean passed = false;

    /**
     * Construct a pipe.
     */
    Pipe() {
        int center = (int) random(PIPE_SPACING, WINDOW_HEIGHT - PIPE_SPACING);
        top = center - ((double) PIPE_SPACING / 2);
        bottom = WINDOW_HEIGHT - (center + ((double) PIPE_SPACING / 2));
        x = WINDOW_WIDTH;
    }

    /**
     * Returns the y coordinate of the top pipe.
     *
     * @return the y coordinate of the top pipe.
     */
    double getTop() {
        return top;
    }

    /**
     * Returns the y coordinate of the bottom pipe.
     *
     * @return the y coordinate of the bottom pipe.
     */
    double getBottom() {
        return bottom;
    }

    /**
     * Returns the x coordinate of the left size of the pipe.
     *
     * @return the x coordinate of the pipe.
     */
    int getX() {
        return x;
    }

    /**
     * Test if a bird hits this pipe.
     *
     * @param bird the bird.
     * @return {@code true} if the bird hits this pipe, otherwise {@code false}.
     */
    boolean hits(Bird bird) {
        if (bird.getY() - (Bird.BIRD_HEIGHT / 2) < top || bird.getY() + (Bird.BIRD_HEIGHT / 2) > (WINDOW_HEIGHT - bottom)) {
            if (bird.getX() + (Bird.BIRD_WIDTH / 2) > x && bird.getX() - (Bird.BIRD_WIDTH / 2) < x + PIPE_WIDTH) {
                highlight = true;
                return true;
            }
        }

        highlight = false;
        return false;
    }

    /**
     * Test if a bird has already passed the pipe in the x coordinate.
     *
     * @param bird the bird.
     * @return {@code true} if the bird has passed the pipe.
     */
    boolean hasPassed(Bird bird) {
        if (bird.getX() > x && !passed) {
            passed = true;
            return true;
        }

        return false;
    }

    /**
     * Update the position of the pipe.
     */
    void update() {
        x -= PIPE_SPEED;
    }

    /**
     * Returns {@code true} if the pipe is offscreen.
     *
     * @return {@code true} if the pipe is offscreen.
     */
    boolean isOffscreen() {
        return x < -PIPE_WIDTH;
    }

    /**
     * Render the pipe to a graphics context.
     *
     * @param g the graphics context.
     */
    void render(Graphics g) {
        if (highlight) {
            g.setColor(Color.RED);
        } else {
            g.setColor(Color.GREEN);
        }

        g.fillRect(x, 0, PIPE_WIDTH, (int) top);
        g.fillRect(x, (int) (WINDOW_HEIGHT - bottom), PIPE_WIDTH, (int) bottom);
    }
}
