package rigbodysim;

public class Circle extends Body {

    public float radius;
    public int color;

    public Circle(float radius, int color) {
        super(1f);
        this.radius = radius;
        this.color = color;
    }
}
