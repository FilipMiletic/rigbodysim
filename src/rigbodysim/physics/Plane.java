package rigbodysim.physics;

import rigbodysim.math.Vec2f;

public class Plane extends Body {
    public final Vec2f normal;
    public final float distance;
    public final float len;

    public Plane(Vec2f normal, float distance, float len) {
        super(0, ShapeType.PLANE);
        this.normal = normal;
        this.distance = distance;
        this.len = len;
    }

    public Vec2f getPoint() {
        return new Vec2f(normal).mulScalar(distance);
    }
}

