package rigbodysim.physics.contact;

import rigbodysim.physics.Body;

public interface ContactGenerator {
    int generate(Body bodyA, Body bodyB, int offset, Contact[] contacts);

}


