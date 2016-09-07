/**
  * Created by Filip Miletic on 1/5/16.
  * Personal website: 8bitphil.me
  * You can contact me via email: filip.miletic@me.com
  * or hit me on Twitter: www.twitter.com/@osmobitni
  * GitHub: www.github.com/FilipMiletic
**/

import javax.swing.JFrame;

import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferInt;

public class rigbodysim implements KeyListener, WindowListener {

    private final String TITLE = "PhySim";
    private final int WIDTH = 800;
    private final int HEIGHT = 600;
    private JFrame frame;
    private Canvas canvas;
    private BufferedImage frameBuffer;
    private int[] frameBufferData;
    private boolean[] keyState = new boolean[128];

    public rigbodysim() {
        frameBuffer = new BufferedImage(WIDTH, HEIGHT, BufferedImage.TYPE_INT_RGB);
        frameBufferData = ((DataBufferInt) frameBuffer.getRaster().getDataBuffer()).getData();


        frame = new JFrame();
        frame.setSize(WIDTH, HEIGHT);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setTitle(TITLE);
        frame.setResizable(false);
        frame.setIgnoreRepaint(true);
        frame.addWindowListener(this);

        canvas = new Canvas();
        canvas.setPreferredSize(new Dimension(WIDTH, HEIGHT));
        canvas.setIgnoreRepaint(true);
        canvas.addKeyListener(this);
        frame.add(canvas);

        frame.pack();

        frame.setVisible(true);

        canvas.requestFocus();
    }

    @SuppressWarnings("unused")
    private static void integrationTest() {

        final int acceleration = 10;

        System.out.println("Time-position calculation, constant, linear");
        final int time = 10;

        for (int t = 1; t <= time; t++) {
            int distance = (acceleration / 2) * t * t;
            int speed = acceleration * t;
            System.out.println(String.format("%d.) Speed: %d, Distance: %d", t, speed, distance ));
        }

        System.out.println("");
        System.out.println("Implicit Eluer Integration");

        final int numSteps = 100;
        final float timeDifference = 1.0f;
        final float timeDifferenceStep = 1.0f / (float)numSteps;
        float position = 0.0f;
        float startPosition = 0.0f;
        for (int frame =  0; frame < time; frame++) {

            for (int step = 0; step < numSteps; step++) {
                position = position + startPosition * timeDifferenceStep;
                startPosition = startPosition + (float)acceleration * timeDifferenceStep;
            }

            System.out.println(String.format("%d.) startPosition: %f, Position: %f", frame, startPosition, position));
        }

        System.out.println("");
        System.out.println("Explicit Euler Integration");

        position = 0.0f;
        startPosition = 0.0f;
        for (int i=0; i < time; i++) {
            for (int step = 0; step < numSteps; step++) {
                startPosition = startPosition + (float)acceleration * timeDifferenceStep;
                position = position + startPosition * timeDifferenceStep;
            }
            System.out.println(String.format("%d.) startPosition: %f, Position: %f", i, startPosition, position));
        }

        System.out.println("");
        System.out.println("Fixed Euler Integration");

        position = 0.0f;
        startPosition = 0.0f;
        for (int i = 0; i < time; i++) {
            float otherStartPosition = startPosition;
            startPosition = startPosition + (float)acceleration * timeDifference;
            position = position + (otherStartPosition + startPosition) / 2f * timeDifference;
            System.out.println(String.format("%d.) startPosition: %f, Position: %f", i, startPosition, position));
        }

    }

    public static void main(String[] args) {
        final float PI180 = 180.0f / (float)Math.PI;
        
        float x = 60;
        float y = 30;

        float cos = (float)Math.cos(Math.PI * 2);
        float sin = (float)Math.sin(Math.PI * 1.5);
        System.out.println(cos*x + " - " + sin*y);

        float angle = (float)Math.atan2(y, x);
        System.out.println(angle + " = in deegres:" + (PI180 * angle));

        float rx = (float)Math.cos(angle);
        float ry = (float)Math.sin(angle);
        System.out.println(rx + " - " + ry);



        rigbodysim game = new rigbodysim();
        game.run();
    }

    @Override
    public void keyTyped(KeyEvent e) {

    }

    @Override
    public void keyPressed(KeyEvent e) {
        keyState[e.getKeyCode()] = true;
    }

    @Override
    public void keyReleased(KeyEvent e) {
        keyState[e.getKeyCode()] = false;
    }

    private boolean isKeyDown(int keyCode) {
        return keyState[keyCode];
    }

    @Override
    public void windowOpened(WindowEvent e) {

    }

    @Override
    public void windowClosing(WindowEvent e) {

    }

    @Override
    public void windowClosed(WindowEvent e) {

    }

    @Override
    public void windowIconified(WindowEvent e) {

    }

    @Override
    public void windowDeiconified(WindowEvent e) {

    }

    @Override
    public void windowActivated(WindowEvent e) {

    }

    @Override
    public void windowDeactivated(WindowEvent e) {
        for (int i = 0; i < keyState.length; i++) {
            keyState[i] = false;
        }
    }

    private void run() {
        final long TARGET_FPS = 60;
        final long NANO_SECOND = 1000000000;
        final long NANO_SECONDS_FPS = NANO_SECOND / TARGET_FPS;
        final float DT = 1.0f / (float)TARGET_FPS;
        long startFPSTime = System.currentTimeMillis();
        int numFrames = 0;
        boolean isRunning = true;

        while (isRunning) {
            long frameStartTime = System.nanoTime();

            updateGame(DT);

            renderGame();

            // Framebuffer displaying
            Graphics graphics = canvas.getGraphics();
            graphics.drawImage(frameBuffer, 0, 0, null);
            graphics.dispose();
            numFrames++;

            // Display frame every second
            if (System.currentTimeMillis() - startFPSTime > 1000) {
                System.out.println("Frames: " + numFrames);
                startFPSTime = System.currentTimeMillis();
                numFrames = 0;
            }

            // CPU sleep when FPS go over 60, no need to waste resources
            long sleepDuration = NANO_SECONDS_FPS - (System.nanoTime() - frameStartTime);

            if (sleepDuration > 0) {
                long sleepStart = System.nanoTime();
                while (System.nanoTime() - sleepStart < sleepDuration) {
                    try {
                        Thread.sleep(1);
                    } catch (InterruptedException e) {
                    }
                }
            }
        }
    }

    private void setPixel(int x, int y, int color) {
            int index = (HEIGHT - 1 - y) * WIDTH + x;
            frameBufferData[index] = color;
    }

    private void setPixelSafe(int x, int y, int color) {
        if (!(x < 0 || x > WIDTH - 1 || y < 0 || y > HEIGHT - 1)) {
            setPixel(x, y, color);
        }
    }


    private void drawRect(int x0, int y0, int x1, int y1, int color) {
        // Checking and defining right values for min/max as arguments
        int minX = Math.min(x0, x1);
        int minY = Math.min(y0, y1);
        int maxX = Math.max(x0, x1);
        int maxY = Math.max(y0, y1);

        // Checking and putting values into our frameBuffer
        if (!(maxX < 0 || minX > WIDTH - 1 || maxY < 0 || minY > HEIGHT - 1)) {
            // Restricting rectangle to the viewable area
            minX = Math.max(Math.min(minX, WIDTH - 1), 0);
            minY = Math.max(Math.min(minY, HEIGHT - 1), 0);
            maxX = Math.max(Math.min(maxX, WIDTH - 1), 0);
            maxY = Math.max(Math.min(maxY, HEIGHT - 1), 0);

            // Passing right pixels to frameBuffer
            for (int y = minY; y <= maxY; y++) {
                for (int x = minX; x <= maxX; x++) {
                    setPixelSafe(x, y, color);
                }
            }
        }
    }

    private void drawRect(float x0, float y0, float x1, float y1, int color) {
        drawRect((int)x0, (int)y0, (int)x1, (int)y1, color);
    }

        private void drawLine(int x0, int y0, int x1, int y1, int color) {
        int minX = Math.min(x0, x1);
        int minY = Math.min(y0, y1);
        int maxX = Math.max(x0, x1);
        int maxY = Math.max(y0, y1);

        int dx = maxX - minX;
        int dy = maxY - minY;

        setPixelSafe(minX, minY, color);
        setPixelSafe(maxX, maxY, color);

        if (Math.abs(dx) > Math.abs(dy)) {
            int err = dx / 2;
            int y = minY;
            for (int x = minX + 1; x < maxX; x++) {
                err = err - dy;
                if (err < 0) {
                    y++;
                    err = err + dx;
                }
                setPixelSafe(x, y, color);
            }
        } else {
            int err = dy / 2;
            int x = minX;
            for (int y = minY + 1; y < maxY; y++) {
                err = err - dx;
                if (err < 0) {
                    x++;
                    err = err + dy;
                }
                setPixelSafe(x, y, color);
            }
        }
    }

    private float posX;
    private float posY;

    private float velX;
    private float velY;

    private float accX;
    private float accY;

    private void updateGame(float dt) {
        accX = 0;
        accY = -15f / dt;


        if (isKeyDown(87)) {
            // W pressed
            accY += 1f / dt;
        } else if (isKeyDown(83)) {
            // S pressed
            accY -= 1f / dt;
        }

        if (isKeyDown(65)) {
            // A pressed
            accX -= 10f / dt;
        } else if (isKeyDown(68)) {
            // D pressed
            accX += 10f / dt;
        }

        // Explicit Euler
        velX += accX * dt;
        velY += accY * dt;
        posX += velX * dt;
        posY += velY * dt;
    }

    private void renderGame() {
        for (int i = 0; i < WIDTH * HEIGHT; i++) {
            frameBufferData[i] = 0x000000;
        }
        drawRect(posX, posY, posX + 100f, posY + 100f, 0xFF0000);

//        drawLine(-300, 0, 500, 500, 0x0000FF);
    }

}
