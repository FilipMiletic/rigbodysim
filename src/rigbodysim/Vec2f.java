package rigbodysim;

/**
 * Created by phil on 12/9/16.
 */

public class Vec2f extends Object {
    public float x;
    public float y;

    public Vec2f() {
        x = y = 0f;
    }

    public Vec2f(float x, float y) {
        this.x = x;
        this.y = y;
    }

    public Vec2f(Vec2f v) {
        x = v.x;
        y = v.y;
    }

    public Vec2f set(float x, float y) {
        this.x = x;
        this.y = y;
        return this;
    }

    public Vec2f zero() {
        x = y = 0f;
        return this;
    }

    public Vec2f add(Vec2f v) {
        x += v.x;
        y += v.y;
        return this;
    }

    public Vec2f sub(Vec2f v) {
        x -= v.x;
        y -= v.y;
        return this;
    }

    public Vec2f mulScalar(float s) {
        x *= s;
        y *= s;
        return this;
    }

    public Vec2f addMulScalar(Vec2f v, float s) {
        x += v.x * s;
        y += v.y * s;
        return this;
    }

    public float dot(Vec2f v) {
        return (x * v.x + y * v.y);
    }

    public float lengthSqrd(){
        return dot(this);
    }

    public float length() {
        return (float)Math.sqrt(lengthSqrd());
    }

    public Vec2f normalize() {
        float l = length();
        if (l == 0) {
            l = 1;
        }
        float invLen = 1.0f/l;
        x *= invLen;
        y *= invLen;
        return this;
    }

    public Vec2f perpendicularLeft() {
        float tmp = x;
        this.x = -y;
        this.y = tmp;
        return this;
    }

    public Vec2f perpendicularRight() {
        float tmp = x;
        this.x = y;
        this.y = -tmp;
        return this;
    }

    @Override
    public String toString() {
        return String.format("(%f. %f)", x, y);
    }
}
