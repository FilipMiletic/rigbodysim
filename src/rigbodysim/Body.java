package rigbodysim;


public class Body {
    public final Vec2f pos = new Vec2f();
    public final Vec2f vel = new Vec2f();
    public final Vec2f acc = new Vec2f();
    public float impulseWeight;

    public Body(float impulseWeight) {
        this.impulseWeight = impulseWeight;
    }
}
