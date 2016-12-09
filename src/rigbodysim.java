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

    public static void main(String[] args) {
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
        initGame();

        final long TARGET_FPS = 60;
        final long NANO_SECOND = 1000000000;
        final long NANO_SECONDS_FPS = NANO_SECOND / TARGET_FPS;
        final float DT = 1.0f / (float)TARGET_FPS;
        long startFPSTime = System.currentTimeMillis();
        int numFrames = 0;
        boolean isRunning = true;

        while (isRunning) {
            long frameStartTime = System.nanoTime();

            // TODO: Further implementation
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

    class Plane {
        public final Vec2f normal;
        public final float distance;

        public Plane(Vec2f normal, float distance) {
            this.normal = normal;
            this.distance = distance;
        }

        public Vec2f getPoint() {
            return new Vec2f(normal).mulScalar(distance);
        }
    }

    private Vec2f pos;
    private Vec2f vel;
    private Vec2f acc;
    private Vec2f radius;
    private Plane[] planes;

    private void initGame() {
        pos = new Vec2f(WIDTH / 2f, HEIGHT / 2f);
        vel = new Vec2f();
        acc = new Vec2f();
        radius = new Vec2f(50f, 50f);
        planes = new Plane[4];
        planes[0] = new Plane(new Vec2f(0, 1), 0);
        planes[1] = new Plane(new Vec2f(0, -1), -(HEIGHT - 1));
        planes[2] = new Plane(new Vec2f(1, 0), 0);
        planes[3] = new Plane(new Vec2f(-1, 0), -(WIDTH - 1));
    }

    private void collisionResponse(Vec2f n, float restitution) {
        float projVel = vel.dot(n);
        float e = 1.0f + restitution;
        vel.addMulScalar(n, -projVel * e);
    }

    private void updateGame(float dt) {
        acc.x = 0;
        acc.y = 0;

        if (isKeyDown(87)) {
            // W pressed
            acc.y += 10f / dt;
        } else if (isKeyDown(83)) {
            // S pressed
            acc.y -= 10f / dt;
        }

        if (isKeyDown(65)) {
            // A pressed
            acc.x -= 10f / dt;
        } else if (isKeyDown(68)) {
            // D pressed
            acc.x += 10f / dt;
        }

        // Explicit Euler
        vel.addMulScalar(acc, dt);
        pos.addMulScalar(vel, dt);

        // Collision and reaction
        // Enables bouncing depending on the state (0/1)

        float restitution = 0f;
        for (Plane plane: planes) {
            Vec2f pointOnPlane = plane.getPoint();
            Vec2f distanceToPlane = new Vec2f(pointOnPlane).sub(pos);
            float projDistance = distanceToPlane.dot(plane.normal);
            float projRadius =  Math.abs(radius.dot(plane.normal));
            float d = projDistance + projRadius;
            System.out.println(d);
        }
        /*
        Vec2f n = new Vec2f();
        if (pos.x < 0 + radius.x) {
            pos.x = 0 + radius.x;
            n.set(1, 0);
            collisionResponse(n, restitution);
        }
        if (pos.x > WIDTH - 1 - radius.x) {
            pos.x = WIDTH - 1 - radius.x;
            n.set(-1, 0);
            collisionResponse(n, restitution);
        }
        if (pos.y < 0 + radius.y) {
            pos.y = 0 + radius.y;
            n.set(0, 1);
            collisionResponse(n, restitution);
        }
        if (pos.y > HEIGHT - 1 - radius.x) {
            pos.y = HEIGHT - 1 - radius.x;
            n.set(0, -1);
            collisionResponse(n, restitution);
        }
        */
    }

    private void renderGame() {
        for (int i = 0; i < WIDTH * HEIGHT; i++) {
            frameBufferData[i] = 0x000000;
        }

        for (Plane plane: planes) {
            Vec2f point = plane.getPoint();
            drawRect(point.x - radius.x, point.y - radius.y, point.x + radius.x, point.y + radius.y, 0xFFFFFF);
        }

        drawRect(pos.x - radius.x, pos.y - radius.y, pos.x + radius.x, pos.y + radius.y, 0xFF0000);
        drawRect(pos.x - 2, pos.y - 2, pos.x + 2, pos.y + 2, 0xFF0000);
    }
}

// TODO: Continue from part 16