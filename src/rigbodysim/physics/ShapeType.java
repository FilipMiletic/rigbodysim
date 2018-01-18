package rigbodysim.physics;

public enum ShapeType {
    PLANE(1),
    CIRCLE(2),
    COUNT(3);

    public final int id;

    ShapeType(int id) {
        this.id = id;
    }

}
