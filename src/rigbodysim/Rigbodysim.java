/**
 * Created by Filip Miletic on 1/5/16.
 * Personal website: 8bitphil.me
 * You can contact me via email: filip.miletic@me.com
 * or hit me on Twitter: www.twitter.com/@osmobitni
 * GitHub: www.github.com/FilipMiletic
 **/
package rigbodysim;

import rigbodysim.math.Vec2f;
import rigbodysim.math.Vec2i;
import rigbodysim.physics.*;
import rigbodysim.physics.contact.Contact;

import javax.swing.JFrame;
import javax.swing.WindowConstants;

import java.awt.Canvas;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferInt;
import java.security.Key;

public class Rigbodysim implements KeyListener, WindowListener, MouseListener, MouseMotionListener {

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


    public Rigbodysim() {
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
        Rigbodysim base = new Rigbodysim();
        base.run();
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

    private void setKeyDown(int keyCode, boolean value) {
        keyState[keyCode] = value;
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
        mousePos.set(e.getX(), getY(e.getY()));
    }

    @Override
    public void mouseMoved(MouseEvent e) {
        mousePos.set(e.getX(), getY(e.getY()));
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
        final float DT = 1.0f / (float) TARGET_FPS;
        long startFPSTime = System.currentTimeMillis();
        int numFrames = 0;
        boolean isRunning = true;

        while (isRunning) {
            long frameStartTime = System.nanoTime();
            updateInput(DT);
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
            setPixel((int) x, (int) y, color);
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
        drawRect((int) x0, (int) y0, (int) x1, (int) y1, color);
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
        drawLine((int) x0, (int) y0, (int) x1, (int) y1, color);
    }

    private void drawPoint(float x, float y, float radius, int color) {
        drawRect(x - radius, y - radius, x + radius, y + radius, color);
    }

    private float getCircleError(float x, float y, float r) {
        return x * x + y * y - r * r;
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
                drawLine(cx + -x, cy + -y, cx + x, cy + -y, color);

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

    private Physics physics;

    private void initGame() {
        physics = new Physics();

        physics.addBody(new Plane(new Vec2f(0, 1), 50, WIDTH - 1));
        physics.addBody(new Plane(new Vec2f(0, -1), -(HEIGHT - 1 - 50), -(WIDTH - 1)));
        physics.addBody(new Plane(new Vec2f(1, 0), 50, -(HEIGHT - 1)));
        physics.addBody(new Plane(new Vec2f(-1, 0), -(WIDTH - 1 - 50), HEIGHT - 1));
    }

    private boolean isPointInCircle(float x, float y, float cx, float cy, float radius) {
        float dx = x - cx;
        float dy = y - cy;
        float lengthSquared = dx * dx + dy * dy;
        return lengthSquared <= radius * radius;
    }

    private boolean showContacts = true;
    private boolean dragging = false;
    private Vec2i dragStart = new Vec2i();
    private Circle dragCircle = null;
    private boolean placeCircle = false;

    private void updateInput(float dt) {
        boolean leftMousePressed = mouseState[1];

        if (!dragging) {
            if (leftMousePressed) {
                dragCircle = null;
                for (int i = 0; i < physics.numOfBodies; i++) {
                    Body body = physics.bodies[i];
                    if (body instanceof Circle) {
                        Circle circle = (Circle) physics.bodies[i];
                        if (isPointInCircle(mousePos.x, mousePos.y, circle.pos.x, circle.pos.y, circle.radius)) {
                            dragging = true;
                            dragStart.set(mousePos);
                            dragCircle = circle;
                            break;
                        }
                    }

                }
                if (dragCircle == null) {
                    placeCircle = true;

                }
             } else {
                if (placeCircle) {
                    placeCircle= false;
                    final float radius = 1f;
                    final int numX = 10;
                    final int numY = 10;
                    final float halfDimX = radius * numX;
                    final float halfDimY = radius * numY;

                    for (int y = 0; y < numY; y++) {
                        for (int x = 0; x < numX; x++) {
                            Circle circle;
                            physics.addBody(circle = new Circle(radius, 0xFF00FF));
                            circle.pos.set(mousePos.x - halfDimX + x*radius*2f, mousePos.y - halfDimY + y*radius*2f);
                        }
                    }
                }
            }
        } else {
            if (leftMousePressed) {
                int dx = mousePos.x - dragStart.x;
                int dy = mousePos.y - dragStart.y;
                dragCircle.vel.x += dx * 10f;
                dragCircle.vel.y += dy * 10f;
                dragStart.set(mousePos);
            } else {
                dragging = false;
            }
        }

        // Hide contact marks by pressing X on keyboard
        if (isKeyDown(KeyEvent.VK_X)) {
            showContacts = !showContacts;
            setKeyDown(KeyEvent.VK_X, false);
        }

        // Body dynamics per user input
        for (int i = 0; i < physics.numOfBodies; i++) {
            Body body = physics.bodies[i];

            if (isKeyDown(KeyEvent.VK_W)) {
                body.acc.y += 12 / dt;
            } else if (isKeyDown(KeyEvent.VK_S)) {
                body.acc.y -= 12f / dt;
            }

            if (isKeyDown(KeyEvent.VK_A)) {
                body.acc.x -= 12f / dt;
            } else if (isKeyDown(KeyEvent.VK_D)) {
                body.acc.x += 12f / dt;
            }
        }
    }

    private void updateGame(float dt) {
        physics.step(dt);
    }


    private void drawNormal(Vec2f center, Vec2f normal) {
        final float arrowRadiusX = 8;
        final float arrowRadiusY = 8;
        Vec2f perp = new Vec2f(normal).perpendicularRight();
        Vec2f arrowTip = new Vec2f(center).addMulScalar(normal, 15);
        drawLine(center.x, center.y, arrowTip.x, arrowTip.y, 0xFFFFFF);
        drawLine(arrowTip.x, arrowTip.y, arrowTip.x + perp.x * arrowRadiusX + normal.x * -arrowRadiusY,
                arrowTip.y + perp.y * arrowRadiusX + normal.y * -arrowRadiusY, 0xFFFFFF);
        drawLine(arrowTip.x, arrowTip.y, arrowTip.x + -perp.x * arrowRadiusX + normal.x * -arrowRadiusY,
                arrowTip.y + -perp.y * arrowRadiusX + normal.y * -arrowRadiusY, 0xFFFFFF);
    }

    private void renderGame() {
        // Initializing frameBuffer
        for (int i = 0; i < WIDTH * HEIGHT; i++) {
            frameBufferData[i] = 0x000000;
        }

        for (int i = 0; i < physics.numOfBodies; i++) {
            Body body = physics.bodies[i];
            if (body instanceof Plane) {
                Plane plane = (Plane) body;
                Vec2f normal = plane.normal;
                Vec2f startPoint = plane.getPoint();
                Vec2f perp = new Vec2f(normal).perpendicularRight();
                Vec2f endPoint = new Vec2f(startPoint).addMulScalar(perp, plane.len);
                drawLine(startPoint.x, startPoint.y, endPoint.x, endPoint.y, 0xFFFFFF);
            }
        }

        for (int i = 0; i < physics.numOfBodies; i++) {
            Body body = physics.bodies[i];
            if (body instanceof Circle) {
                Circle circle = (Circle) body;
                drawCircle(circle.pos.x, circle.pos.y, circle.radius, circle.color, false);
            }
        }
        if (showContacts) {
            for (int i = 0; i < physics.numOfContacts; i++) {
                Contact contact = physics.contacts[i];
                Vec2f normal = contact.normal;
                Vec2f closestPointOnPlane = contact.point;
                Vec2f closestPointOnBox = new Vec2f(closestPointOnPlane).addMulScalar(normal, contact.distance);
                drawCircle(closestPointOnPlane.x, closestPointOnPlane.y, 4f, 0xFF00FF, true);
                drawCircle(closestPointOnBox.x, closestPointOnBox.y, 4f, 0xFFFF00, true);
                drawNormal(closestPointOnPlane, normal);
            }
        }

        drawPoint(mousePos.x, mousePos.y, 2, 0x0000FF);
    }
}
