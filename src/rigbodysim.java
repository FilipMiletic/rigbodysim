/**
  * Created by Filip Miletic on 1/5/16.
  * Personal website: 8bitphil.me
  * You can contact me via email: filip.miletic@me.com
  * or hit me on Twitter: www.twitter.com/@osmobitni
  * GitHub: www.github.com/FilipMiletic
**/

import javax.swing.JFrame;
import javax.swing.WindowConstants;

import java.awt.Canvas;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferInt;

public class rigbodysim implements KeyListener, WindowListener, MouseListener, MouseMotionListener {

    private final String TITLE = "PhySim";
    private final int WIDTH = 800;
    private final int HEIGHT = 600;
    private JFrame frame;
    private Canvas canvas;
    private BufferedImage frameBuffer;
    private int[] frameBufferData;
    private boolean[] keyState = new boolean[128];
    private boolean[] mouseState = new boolean[16];
    private Vec2i mousePos = new Vec2i();


    public rigbodysim() {
        frameBuffer = new BufferedImage(WIDTH, HEIGHT, BufferedImage.TYPE_INT_RGB);
        frameBufferData = ((DataBufferInt) frameBuffer.getRaster().getDataBuffer()).getData();

        frame = new JFrame();
        frame.setSize(WIDTH, HEIGHT);
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        frame.setTitle(TITLE);
        frame.setResizable(false);
        frame.setIgnoreRepaint(true);
        frame.addWindowListener(this);

        canvas = new Canvas();
        canvas.setPreferredSize(new Dimension(WIDTH, HEIGHT));
        canvas.setIgnoreRepaint(true);
        canvas.addKeyListener(this);
        canvas.addMouseListener(this);
        canvas.addMouseMotionListener(this);
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

    @Override
    public void mouseDragged(MouseEvent e) {
        mousePos.set(e.getX(), e.getY());
    }

    @Override
    public void mouseMoved(MouseEvent e) {
        mousePos.set(e.getX(), e.getY());
    }

    @Override
    public void mouseClicked(MouseEvent e) {

    }

    @Override
    public void mousePressed(MouseEvent e) {
        mouseState[e.getButton()] = true;
    }

    @Override
    public void mouseReleased(MouseEvent e) {
        mouseState[e.getButton()] = false;
    }

    @Override
    public void mouseEntered(MouseEvent e) {
    }

    @Override
    public void mouseExited(MouseEvent e) {
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

    private int getY(int y) {
        return HEIGHT - 1 - y;
    }

    private void setPixel(int x, int y, int color) {
            int index = getY(y) * WIDTH + x;
            frameBufferData[index] = color;
    }

    private void setPixelSafe(int x, int y, int color) {
        if (!(x < 0 || x > WIDTH - 1 || y < 0 || y > HEIGHT - 1)) {
            setPixel(x, y, color);
        }
    }

    private void setPixelSafe(float x, float y, int color) {
        if (!(x < 0 || x > WIDTH - 1 || y < 0 || y > HEIGHT - 1)) {
            setPixel((int)x, (int)y, color);
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
        int minX = x0;
        int minY = y0;
        int maxX = x1;
        int maxY = y1;

        int dx = maxX - minX;
        int dy = maxY - minY;

        int signX = dx < 0 ? -1 : 1;
        int signY = dy < 0 ? -1 : 1;

        dx = Math.abs(dx);
        dy = Math.abs(dy);


        if (dx > dy) {
            int err = dx / 2;
            int y = 0;
            for (int x = 0; x < dx; x++) {
                err = err - dy;
                if (err < 0) {
                    y++;
                    err = err + dx;
                }
                setPixelSafe(minX + x * signX, minY + y * signY, color);
            }
        } else {
            int err = dy / 2;
            int x = 0;
            for (int y = 0; y < dy; y++) {
                err = err - dx;
                if (err < 0) {
                    x++;
                    err = err + dy;
                }
                setPixelSafe(minX + x * signX, minY + y * signY, color);
            }
        }
    }

    private void drawLine(float x0, float y0, float x1, float y1, int color) {
        drawLine((int)x0, (int)y0, (int)x1, (int)y1, color);
    }

    private void drawPoint(float x, float y, float radius, int color) {
        drawRect(x - radius, y - radius, x + radius, y + radius, color);
    }

    private float getCircleError(float x, float y, float r) {
        return x*x + y*y - r*r;
    }

    private void drawCircle(float cx, float cy, float radius, int color, boolean filled) {
        float x = 0f;
        float y = radius;
        float f = getCircleError(1f, radius - 0.5f, radius);
        while (x <= y) {
            if (!filled) {
                setPixelSafe(cx + x, cy + y, color);
                setPixelSafe(cx + -x, cy + y, color);

                setPixelSafe(cx + -x, cy + -y, color);
                setPixelSafe(cx + x, cy + -y, color);

                setPixelSafe(cx + y, cy + x, color);
                setPixelSafe(cx + -y, cy + x, color);

                setPixelSafe(cx + -y, cy + -x, color);
                setPixelSafe(cx + y, cy + -x, color);
            } else {
                drawLine(cx + x, cy + y, cx + -x, cy + y, color);
                drawLine(cx + -x, cy + - y, cx + x, cy + -y, color);

                drawLine(cx + y, cy + x, cx + -y, cy + x, color);
                drawLine(cx + -y, cy + -x, cx + y, cy + -x, color);
            }
            x++;
            if (f > 0) {
                y--;
                f = getCircleError(x, y - 0.5f, radius);
            } else {
                f = getCircleError(x, y - 0.5f, radius);
            }
        }
    }

    class Plane {
        public final Vec2f normal;
        public final float distance;
        public final float len;

        public Plane(Vec2f normal, float distance, float len) {
            this.normal = normal;
            this.distance = distance;
            this.len = len;
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
        planes[0] = new Plane(new Vec2f(0, 1), 50, WIDTH - 1);
        planes[1] = new Plane(new Vec2f(0, -1), -(HEIGHT - 1 - 50), -(WIDTH - 1));
        planes[2] = new Plane(new Vec2f(1, 0), 50, -(HEIGHT - 1));
        planes[3] = new Plane(new Vec2f(-1, 0), -(WIDTH - 1 - 50), HEIGHT - 1);
    }

    private void collisionResponse(Vec2f n, float restitution) {
        float projVel = vel.dot(n);
        float e = 1.0f + restitution;
        vel.addMulScalar(n, -projVel * e);
    }

    private boolean isPointInRect(float x, float y, float x0, float y0, float x1, float y1) {
        return !(x < x0 || x > x1 || y < y0 || y > y1);
    }

    private boolean rectDragging = false;
    private Vec2i rectDragStart = new Vec2i();

    private void updateGame(float dt) {
        boolean leftMousePressed = mouseState[1];

        if (!rectDragging) {
            if (isPointInRect(mousePos.x, getY(mousePos.y), pos.x - radius.x, pos.y - radius.y, pos.x + radius.x, pos.y + radius.y) && leftMousePressed) {
                rectDragging = true;
                rectDragStart.set(mousePos);
            }
        } else {
            if (leftMousePressed) {
                int dx = mousePos.x - rectDragStart.x;
                int dy = mousePos.y - rectDragStart.y;
                pos.x += dx;
                pos.y += dy * (-1);
                rectDragStart.set(mousePos);
            } else {
                rectDragging = false;
            }
        }

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
        // float restitution: Enables bouncing, coefficient
        float restitution = 0.4f;
        for (Plane plane: planes) {
            Vec2f pointOnPlane = plane.getPoint();
            Vec2f distanceToPlane = new Vec2f(pointOnPlane).sub(pos);
            float projDistance = distanceToPlane.dot(plane.normal);
            float projRadius =  -Math.abs(radius.dot(plane.normal));
            float d = projRadius - projDistance;
            if (d < 0) {
                Vec2f mtv = new Vec2f(plane.normal).mulScalar(d);
                pos.sub(mtv);
                collisionResponse(plane.normal, restitution);
            }
        }
    }

    private void renderGame() {
        for (int i = 0; i < WIDTH * HEIGHT; i++) {
            frameBufferData[i] = 0x000000;
        }

        final float arrowRadiusX = 15;
        final float arrowRadiusY = 15;

        for (Plane plane: planes) {

            Vec2f normal = plane.normal;
            Vec2f startPoint = plane.getPoint();
            Vec2f perp = new Vec2f(normal).perpendicularRight();
            Vec2f endPoint = new Vec2f(startPoint).addMulScalar(perp, plane.len);
            Vec2f center = new Vec2f(startPoint).addMulScalar(perp, plane.len * 0.5f);

            drawLine(startPoint.x, startPoint.y, endPoint.x, endPoint.y, 0xFFFFFF);
            drawPoint(startPoint.x, startPoint.y, 2, 0xFFFF00);
            drawPoint(endPoint.x, endPoint.y, 2, 0xFF00FF);
            Vec2f arrowTip = new Vec2f(center).addMulScalar(normal, 40);
            drawLine(center.x, center.y, arrowTip.x, arrowTip.y, 0xFFFFFF);
            drawLine(arrowTip.x, arrowTip.y, arrowTip.x + perp.x * arrowRadiusX + normal.x * -arrowRadiusY, arrowTip.y + perp.y * arrowRadiusX + normal.y * -arrowRadiusY, 0xFFFFFF);
            drawLine(arrowTip.x, arrowTip.y, arrowTip.x + -perp.x * arrowRadiusX + normal.x * -arrowRadiusY, arrowTip.y + -perp.y * arrowRadiusX + normal.y * -arrowRadiusY, 0xFFFFFF);

        }

        int color = 0xFF0000;
        if (isPointInRect(mousePos.x, getY(mousePos.y), pos.x - radius.x, pos.y - radius.y, pos.x + radius.x, pos.y + radius.y)) {
            color = 0x00FF00;
        }

        drawRect(pos.x - radius.x, pos.y - radius.y, pos.x + radius.x, pos.y + radius.y, color);
        drawCircle(pos.x, pos.y, radius.x, 0xFF00FF, false);
        drawPoint(pos.x, pos.y, 2, 0xFFFFFF);
        drawPoint(mousePos.x, getY(mousePos.y), 2, 0x0000FF);

    }
}