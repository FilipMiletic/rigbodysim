package rigbodysim.physics;

import rigbodysim.math.Vec2f;

public abstract class Body {
    public final Vec2f pos = new Vec2f();
    public final Vec2f vel = new Vec2f();
    public final Vec2f acc = new Vec2f();
    public float impulseWeight;
    public final ShapeType type;

    public Body(float impulseWeight, ShapeType type) {
        this.type = type;
        this.impulseWeight = impulseWeight;
    }
}
