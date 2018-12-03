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

import com.javierorbe.neuron.NeuralNetwork;
import com.javierorbe.neuron.neuroevolution.Evolvable;

import java.awt.*;
import java.util.List;

import static com.javierorbe.math.util.MathUtils.mapNumber;

public class Bird extends Evolvable<Bird> {

    private static final double MUTATION_RATE = 0.1;
    private static final double GRAVITY = 0.6;
    private static final double BIRD_LIFT = -15;

    static final int BIRD_WIDTH = 24;
    static final int BIRD_HEIGHT = 24;

    private double x = 64;
    private double y = (double) FlappyEvolution.WINDOW_HEIGHT / 2;

    private double velocity = 0;

    private Bird(NeuralNetwork brain) {
        super(brain);
    }

    Bird() {
        super(new NeuralNetwork(new int[] {5, 8, 1}));
    }

    @Override
    protected Bird getMutatedCopy() {
        NeuralNetwork brain = getBrain().copy();
        brain.mutate(MUTATION_RATE);
        return new Bird(brain);
    }

    double getX() {
        return x;
    }

    double getY() {
        return y;
    }

    private void jump() {
        velocity += BIRD_LIFT;
    }

    /**
     * Update the position of the bird and add score.
     */
    void update() {
        velocity += GRAVITY;

        velocity = velocity < -5 ? -5 : velocity;
        velocity = velocity > 5 ? 5 : velocity;

        y += velocity;
        addScore(1);
    }

    /**
     * Test if the bird is outside the upper and lower bounds.
     *
     * @return {@code true} if the y coordinate of the bird is outside the bounds.
     */
    boolean limitCollide() {
        return y < 0 || y > FlappyEvolution.WINDOW_HEIGHT;
    }

    /**
     * Generate an action, using the neural network, based on the closest pipe.
     *
     * @param pipes the list of existing pipes.
     */
    void process(List<Pipe> pipes) {
        Pipe closest = null;
        double closestDist = Double.MAX_VALUE;

        for (Pipe pipe : pipes) {
            double dif = pipe.getX() - x;
            if (dif > 0 && dif < closestDist) {
                closestDist = dif;
                closest = pipe;
            }
        }

        if (closest == null) {
            return;
        }

        double[] output = getBrain().evaluate(new double[] {
                mapNumber(y, 0, FlappyEvolution.WINDOW_HEIGHT, 0, 1),
                mapNumber(closest.getTop(), 0, FlappyEvolution.WINDOW_HEIGHT, 0, 1),
                mapNumber(closest.getBottom(), 0, FlappyEvolution.WINDOW_HEIGHT, 0, 1),
                mapNumber(closest.getX(), 0, FlappyEvolution.WINDOW_WIDTH, 0, 1),
                mapNumber(velocity, -5, 5, 0, 1)
        });

        if (output[0] > 0.5) {
            jump();
        }
    }

    /**
     * Render the bird to a graphics context.
     *
     * @param g the graphics context.
     */
    void render(Graphics g) {
        g.setColor(new Color(255, 255, 0, 77));
        g.fillRect(
                (int) (x - ((double) BIRD_WIDTH/ 2)),
                (int) (y - ((double) BIRD_HEIGHT / 2)),
                BIRD_WIDTH,
                BIRD_HEIGHT
        );

        g.setColor(new Color(255, 255, 0, 230));
        g.drawRect(
                (int) (x - ((double) BIRD_WIDTH / 2)),
                (int) (y - ((double) BIRD_HEIGHT / 2)),
                BIRD_WIDTH,
                BIRD_HEIGHT
        );
    }
}
