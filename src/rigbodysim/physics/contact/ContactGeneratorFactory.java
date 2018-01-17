package rigbodysim.physics.contact;

import rigbodysim.physics.Body;
import rigbodysim.physics.ShapeType;
import rigbodysim.physics.contact.generators.CircleCircleContactGenerator;
import rigbodysim.physics.contact.generators.PlaneCircleContactGenerator;

public final class ContactGeneratorFactory {
    private final int MAX_GENERATORS_PER_SHAPE = ShapeType.COUNT.id;
    private final ContactGenerator[][] generators =
            new ContactGenerator[MAX_GENERATORS_PER_SHAPE][MAX_GENERATORS_PER_SHAPE];

    public ContactGeneratorFactory() {
        generators[ShapeType.PLANE.id][ShapeType.CIRCLE.id] = new PlaneCircleContactGenerator();
        generators[ShapeType.CIRCLE.id][ShapeType.CIRCLE.id] = new CircleCircleContactGenerator();
    }

    public int generate(Body bodyA, Body bodyB, int offset, Contact[] contacts) {
        int result = 0;

        // Find contact generator
        ShapeType typeA = bodyA.type;
        ShapeType typeB = bodyB.type;
        ContactGenerator generator = generators[typeA.id][typeB.id];
        assert (generator != null);

        // Produce contact and index it
        result = generator.generate(bodyA, bodyB, offset, contacts);

        return (result);
    }
}
