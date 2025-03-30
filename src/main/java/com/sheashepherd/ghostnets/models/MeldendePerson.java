package com.sheashepherd.ghostnets.models;

import jakarta.persistence.*;

@Entity
@DiscriminatorValue("MELDEND")
public class MeldendePerson extends Person {

    @Column(nullable = false)
    private boolean anonym;

    public boolean isAnonym() {
        return anonym;
    }

    public void setAnonym(boolean anonym) {
        this.anonym = anonym;
    }
}
