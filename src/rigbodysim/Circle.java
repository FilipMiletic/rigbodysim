package rigbodysim;

public class Circle {
    public final Vec2f pos = new Vec2f();
    public final Vec2f vel = new Vec2f();
    public final Vec2f acc = new Vec2f();
    public float radius;
    public int color;

    public Circle(float radius, int color) {
        this.radius = radius;
        this.color = color;
    }
}
