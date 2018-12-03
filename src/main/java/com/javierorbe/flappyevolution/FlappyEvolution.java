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

import com.javierorbe.neuron.neuroevolution.NeuroEvolution;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

public class FlappyEvolution extends NeuroEvolution<Bird> {

    // DEBUG: render only one bird
    private static final boolean SINGLE_BIRD = false;

    public static void main(String[] args) {
        FlappyEvolution program = new FlappyEvolution(300);

        JPanel panel = new JPanel() {
            @Override
            public void paintComponent(Graphics g) {
                super.paintComponent(g);
                program.render(g);
            }
        };

        JFrame frame = new JFrame("Flappy Evolution");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.getContentPane().add(panel);
        frame.pack();
        frame.setLocationByPlatform(true);
        frame.setSize(WINDOW_WIDTH, WINDOW_HEIGHT);
        frame.setVisible(true);

        while (frame.isVisible()) {
            program.tick();
            panel.repaint();

            try {
                Thread.sleep(1000 / 60);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    static final int WINDOW_WIDTH = 480;
    static final int WINDOW_HEIGHT = 720;

    private static final int PIPE_FREQUENCY = 150;

    private List<Bird> alive;
    private List<Pipe> pipes = new ArrayList<>();

    /**
     * Tick count in each generation.
     */
    private int tickCount = 0;

    /**
     * Current score of the remaining birds.
     */
    private int currentScore = 0;

    /**
     * Best score.
     */
    private int record = 0;

    private FlappyEvolution(int populationCount) {
        for (int i = 0; i < populationCount; i++) {
            getPopulation().add(new Bird());
        }

        alive = new ArrayList<>(getPopulation());
    }

    /**
     * Continue to the next generation.
     */
    @Override
    public void nextGeneration() {
        // Reset game data
        tickCount = 0;
        currentScore = 0;
        pipes.clear();

        super.nextGeneration();
        alive = new ArrayList<>(getPopulation());
    }

    /**
     * Next logic step.
     */
    private void tick() {
        if (tickCount % PIPE_FREQUENCY == 0) {
            pipes.add(new Pipe());
        }

        for (int i = 0; i < pipes.size(); i++) {
            Pipe pipe = pipes.get(i);

            pipe.update();
            if (pipe.isOffscreen()) {
                pipes.remove(pipe);
            }
        }

        for (int i = 0; i < alive.size(); i++) {
            Bird bird = alive.get(i);

            bird.process(pipes);
            bird.update();

            AtomicBoolean removed = new AtomicBoolean(false);

            pipes.forEach((pipe) -> {
                if (pipe.hits(bird)) {
                    alive.remove(bird);
                    removed.set(true);
                }
            });

            if (!removed.get() && bird.limitCollide()) {
                alive.remove(bird);
            }
        }

        tickCount += 1;

        if (alive.size() == 0) {
            if (currentScore > record) {
                record = currentScore;
            }

            nextGeneration();
        } else {
            pipes.forEach((pipe) -> {
                if (pipe.hasPassed(alive.get(0))) {
                    currentScore += 1;
                }
            });
        }
    }

    /**
     * Render the game to a graphics context.
     *
     * @param g the graphics context.
     */
    private void render(Graphics g) {
        g.setColor(Color.BLUE);
        g.fillRect(0, 0, WINDOW_WIDTH, WINDOW_HEIGHT);

        pipes.forEach(pipe -> pipe.render(g));

        if (SINGLE_BIRD) {
            alive.get(0).render(g);
        } else {
            alive.forEach(bird -> bird.render(g));
        }

        g.drawString("Generation: " + getGeneration(), 16, 16);
        g.drawString("Record: " + record, 16, 16 * 2);
        g.drawString("Score: " + currentScore, 16, 16 * 3);
        g.drawString("Alive: " + alive.size(), 16, 16 * 4);
    }
}
