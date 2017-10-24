package rigbodysim;

public class Contact {
    public final Vec2f normal;
    public final float distance;
    public final Vec2f point;

    public Contact(Vec2f normal, float distance, Vec2f point) {
        this.normal = normal;
        this.distance = distance;
        this.point = point;
    }
}
