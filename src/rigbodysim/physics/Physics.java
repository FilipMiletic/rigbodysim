package rigbodysim.physics;

import rigbodysim.math.Vec2f;
import rigbodysim.physics.contact.Contact;
import rigbodysim.physics.contact.ContactGeneratorFactory;

public class Physics {

    private final int MAX_BODIES = 10000;
    public final Body[] bodies;
    public int numOfBodies = 0;

    private final int MAX_CONTACTS = 100000;
    public final Contact[] contacts;
    public int numOfContacts;

    private final ContactGeneratorFactory contactGenFactory;

    public Physics() {
        numOfBodies = 0;
        bodies = new Body[MAX_BODIES];

        contacts = new Contact[MAX_CONTACTS];
        numOfContacts = 0;

        contactGenFactory = new ContactGeneratorFactory();
    }

    public Physics addBody(Body body) {
        assert(numOfBodies < MAX_BODIES);
        bodies[numOfBodies++] = body;
        return this;
    }

    public void step(float dt) {
        // Gravity
        for (int i = 0; i < numOfBodies; i++) {
            Body body = bodies[i];
            if (body.impulseWeight > 0) {
                body.acc.y += -10f / dt;
            }
        }

        // Integration (Acceleration)
        for (int i = 0; i < numOfBodies; i++) {
            Body body = bodies[i];
            if (body.impulseWeight > 0) {
                body.vel.addMulScalar(body.acc, dt);
            }
        }

        numOfContacts = 0;
        // Contact detection between plane and circle
        for (int i = 0; i < numOfBodies; i++) {
            Body bodyA = bodies[i];
            if (bodyA instanceof Plane) {
                Plane planeA = (Plane) bodyA;
                for (int j = i+1; j < numOfBodies; j++) {
                    Body bodyB = bodies[j];
                    if (bodyB instanceof Circle) {
                        int newContacts = contactGenFactory.generate(bodyA, bodyB, numOfContacts, contacts);
                        numOfContacts += newContacts;
//                        Circle circleB = (Circle) bodyB;
//                        Vec2f normal = planeA.normal;
//                        Vec2f pointOnPlane = planeA.getPoint();
//                        Vec2f distanceToPlane = new Vec2f(pointOnPlane).sub(circleB.pos);
//                        float projDistance = distanceToPlane.dot(planeA.normal);
//                        float projRadius = -circleB.radius;
//                        float d = projRadius - projDistance;
//                        if (d < 0) {
//                            Vec2f closestPointOnA = new Vec2f(circleB.pos).addMulScalar(normal, projDistance);
//                            Contact newContact = new Contact(normal, d, closestPointOnA, planeA, circleB);
//                            contacts[numOfContacts++] = newContact;
//                        }
                    }
                }
            }
        }

        // Contact detection between two circles
        for (int i = 0; i < numOfBodies; i++) {
            Body bodyA = bodies[i];
            if (bodyA instanceof Circle) {
                Circle circleA = (Circle) bodyA;
                for (int j = i+1; j < numOfBodies; j++) {
                    Body bodyB = bodies[j];
                    if (bodyB instanceof Circle) {
                        int newContacts = contactGenFactory.generate(bodyA, bodyB, numOfContacts, contacts);
                        numOfContacts += newContacts;
//                        Circle circleB = (Circle) bodyB;
//                        Vec2f distanceBetween = new Vec2f(circleB.pos).sub(circleA.pos);
//                        Vec2f normal = new Vec2f(distanceBetween).normalize();
//                        float projectionDistance = distanceBetween.dot(normal);
//                        float bothRadius = circleA.radius + circleB.radius;
//                        float d = projectionDistance - bothRadius;
//                        if (d < 0) {
//                            Vec2f closestPointOnA = new Vec2f(circleA.pos).addMulScalar(normal, circleA.radius);
//                            Contact newContact = new Contact(normal, d, closestPointOnA, circleA, circleB);
//                            contacts[numOfContacts++] = newContact;
//                        }
                    }
                }
            }
        }

        // Contact
        final int velocitySolverIterations = 100;
        final float restitution = 0.0f;
        for (int j = 0; j < velocitySolverIterations; j++) {
            for (int i = 0; i < numOfContacts; i++) {
                Contact contact = contacts[i];
                Vec2f normal = contact.normal;
                Body bodyA = contact.bodyA;
                Body bodyB = contact.bodyB;
                Vec2f vA = bodyA.vel;
                Vec2f vB = bodyB.vel;
                Vec2f vAB = new Vec2f(vB).sub(vA);
                float impulseWeightA = bodyA.impulseWeight;
                float impulseWeightB = bodyB.impulseWeight;
                float impulseRatio = 1.0f / (impulseWeightA + impulseWeightB);
                float projRelVel = vAB.dot(normal);

                if (projRelVel < 0) {
                    float e = 1.0f + restitution;
                    float impulse = (projRelVel) * e * impulseRatio;
                    vA.addMulScalar(normal, impulse * impulseWeightA);
                    vB.addMulScalar(normal, -impulse * impulseWeightB);
                }
            }
        }

        // Integration (Speed)
        for (int i = 0; i < numOfBodies; i++) {
            Body body = bodies[i];
            if (body.impulseWeight > 0) {
                body.pos.addMulScalar(body.vel, dt);
            }
        }

        // Check for correct position
        final float minDistance = 0.01f;
        final float maxCorrection = 0.5f;
        for (int i = 0; i < numOfContacts; i++) {
            Contact contact = contacts[i];
            Vec2f normal = contact.normal;
            Body bodyA = contact.bodyA;
            Body bodyB = contact.bodyB;
            float impulseWeightA = bodyA.impulseWeight;
            float impulseWeightB = bodyB.impulseWeight;
            float impulseRatio = 1.0f / (impulseWeightA + impulseWeightB);
            float correction = (contact.distance + minDistance) * maxCorrection * impulseRatio;
            bodyA.pos.addMulScalar(normal, correction * impulseWeightA);
            bodyB.pos.addMulScalar(normal, -correction * impulseWeightB);
        }

        // Cleaning
        for (int i = 0; i < numOfBodies; i++) {
            Body body = bodies[i];
            if (body.impulseWeight > 0) {
                body.acc.zero();
            }
        }
    }


}

