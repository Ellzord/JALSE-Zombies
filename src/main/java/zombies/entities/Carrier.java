package zombies.entities;

import static jalse.attributes.Attributes.newNamedDoubleType;

import jalse.attributes.NamedAttributeType;
import jalse.entities.annotations.GetAttribute;
import jalse.entities.annotations.SetAttribute;

public interface Carrier extends Person {

    final NamedAttributeType<Double> INFECTION_PERCENTAGE_TYPE = newNamedDoubleType("infectionPercentage");

    @GetAttribute
    double getInfectionPercentage();

    @SetAttribute
    void setInfectionPercentage(double infectionPercentage);
}