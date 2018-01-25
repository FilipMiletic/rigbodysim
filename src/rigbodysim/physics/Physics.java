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
        // Integration for gravity
        for (int i = 0; i < numOfBodies; i++) {
            Body body = bodies[i];
            if (body.impulseWeight > 0) {
                body.acc.y += -10f / dt;
            }
        }

        // Integration for acceleration
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
            for (int j = i + 1; j < numOfBodies; j++) {
                Body bodyB = bodies[j];
                if (bodyA.impulseWeight > 0 || bodyB.impulseWeight > 0) {
                    int newContacts = contactGenFactory.generate(bodyA, bodyB, numOfContacts, contacts);
                    numOfContacts += newContacts;
                }
            }
        }

        /* Contact
         *
         * TODO: THIS STINKS! Whole simulation blows when number of contacts goes beyond 2k!
         * I would probably need to re-implement this part as it is one of the core unoptimised parts of whole simulation.
         * The problem is next: The more I increment velocitySolverIterations the more program pops at lower number of
         * contacts. So, I need to think of way to make this part go faster! Thought about loop unrolling but it doesn't
         * seem like logical thing to do. I will probably need to reproach the whole problem from different angle
         * and restructure the computation, because here I just calculated formulas with given parameters.
         */
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
                float projectedRelVel = vAB.dot(normal);

                if (projectedRelVel < 0) {
                    float e = 1.0f + restitution;
                    float impulse = (projectedRelVel) * e * impulseRatio;
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

