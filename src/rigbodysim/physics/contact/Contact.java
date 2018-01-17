package rigbodysim.physics.contact;

import rigbodysim.math.Vec2f;
import rigbodysim.physics.Body;

public class Contact {
    public final Vec2f normal;
    public final float distance;
    public final Vec2f point;
    public final Body bodyA;
    public final Body bodyB;

    public Contact(Vec2f normal, float distance, Vec2f point, Body bodyA, Body bodyB) {
        this.normal = normal;
        this.distance = distance;
        this.point = point;
        this.bodyA = bodyA;
        this.bodyB = bodyB;
    }
}
