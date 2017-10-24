package rigbodysim;

/**
 * Created by phil on 12/9/16.
 */

public class Vec2i {
    public int x;
    public int y;

    public Vec2i() {
        x = y = 0;
    }

    public Vec2i (int x, int y) {
        this.x = x;
        this.y = y;
    }

    public Vec2i (Vec2i v) {
        x = v.x;
        y = v.y;
    }

    public Vec2i set(int x, int y) {
        this.x = x;
        this.y = y;
        return this;
    }

    public Vec2i set(Vec2i v) {
        this.x = v.x;
        this.y = v.y;
        return this;
    }

    public Vec2i zero() {
        x = y = 0;
        return this;
    }

    public Vec2i add(Vec2i v) {
        x += v.x;
        y += v.y;
        return this;
    }

    public Vec2i sub(Vec2i v) {
        x -= v.x;
        y -= v.y;
        return this;
    }

    @Override
    public String toString() {
        return String.format("(%d. %d)", x, y);
    }
}
