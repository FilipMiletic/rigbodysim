package rigbodysim.physics.contact.generators;

import rigbodysim.math.Vec2f;
import rigbodysim.physics.Body;
import rigbodysim.physics.Circle;
import rigbodysim.physics.Plane;
import rigbodysim.physics.contact.Contact;
import rigbodysim.physics.contact.ContactGenerator;

public class PlaneCircleContactGenerator implements ContactGenerator {
    @Override
    public int generate(Body bodyA, Body bodyB, int offset, Contact[] contacts) {
        int result = 0;
        Plane planeA = (Plane) bodyA;
        Circle circleB = (Circle) bodyB;
        Vec2f normal = planeA.normal;
        Vec2f pointOnPlane = planeA.getPoint();
        Vec2f distanceToPlane = new Vec2f(pointOnPlane).sub(circleB.pos);
        float projDistance = distanceToPlane.dot(planeA.normal);
        float projRadius = -circleB.radius;
        float d = projRadius - projDistance;
        if (d < 0) {
            Vec2f closestPointOnA = new Vec2f(circleB.pos).addMulScalar(normal, projDistance);
            Contact newContact = new Contact(normal, d, closestPointOnA, planeA, circleB);
            contacts[offset + 0] = newContact;
            result = 1;
        }
        return (result);
    }
}
