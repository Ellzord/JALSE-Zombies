package zombies.entities;

import java.awt.Dimension;
import java.util.UUID;
import java.util.stream.Stream;

import jalse.entities.Entity;
import jalse.entities.annotations.GetAttribute;
import jalse.entities.annotations.SetAttribute;
import jalse.entities.annotations.StreamEntities;

public interface Field extends Entity {

    UUID ID = UUID.randomUUID();

    @GetAttribute
    Dimension getSize();

    @SetAttribute
    void setSize(Dimension size);

    @StreamEntities
    Stream<Person> streamPeople();
}