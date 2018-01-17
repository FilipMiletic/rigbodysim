package rigbodysim.physics.contact.generators;

import rigbodysim.math.Vec2f;
import rigbodysim.physics.Body;
import rigbodysim.physics.Circle;
import rigbodysim.physics.contact.Contact;
import rigbodysim.physics.contact.ContactGenerator;

public class CircleCircleContactGenerator implements ContactGenerator{
    @Override
    public int generate(Body bodyA, Body bodyB, int offset, Contact[] contacts) {
        int result = 0;
        Circle circleA = (Circle) bodyA;
        Circle circleB = (Circle) bodyB;
        Vec2f distanceBetween = new Vec2f(circleB.pos).sub(circleA.pos);
        Vec2f normal = new Vec2f(distanceBetween).normalize();
        float projectionDistance = distanceBetween.dot(normal);
        float bothRadius = circleA.radius + circleB.radius;
        float d = projectionDistance - bothRadius;
        if (d < 0) {
            Vec2f closestPointOnA = new Vec2f(circleA.pos).addMulScalar(normal, circleA.radius);
            Contact newContact = new Contact(normal, d, closestPointOnA, circleA, circleB);
            contacts[offset + 0] = newContact;
            result = 1;
        }
        return (result);
    }

}
