package com.sheashepherd.ghostnets.controllers;

import com.sheashepherd.ghostnets.models.PlanendePerson;
import jakarta.enterprise.context.SessionScoped;
import jakarta.faces.application.FacesMessage;
import jakarta.faces.context.FacesContext;
import jakarta.inject.Named;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;
import jakarta.persistence.Column;


import com.sheashepherd.ghostnets.models.BergendePerson;
import com.sheashepherd.ghostnets.models.Geisternetz;
import com.sheashepherd.ghostnets.models.PlanendePerson;

import java.io.Serializable;
import java.util.List;
import java.time.LocalDateTime;



import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;

@Named
@SessionScoped
public class BergungsController implements Serializable {

    private static final long serialVersionUID = 1L;

    @PersistenceContext(unitName = "ghostnetsPU")
    private EntityManager em;

    public BergungsController() {
        if (em == null) {
            EntityManagerFactory emf = Persistence.createEntityManagerFactory("ghostnetsPU");
            em = emf.createEntityManager();
        }
    }


    private Long ausgewähltesGeisternetzId;
    private LocalDateTime bergungszeitpunkt;
    @Column(name = "bergung_datum")
    private LocalDateTime bergungDatum;



    private BergendePerson bergendePerson = new BergendePerson();
    private PlanendePerson planendePerson = new PlanendePerson();

    // Status von "GEMELDET" -> "BERGUNG_BEVORSTEHEND" setzen
    public void planeBergung() {
        System.out.println("DEBUG: planeBergung() wurde aufgerufen");

        if (ausgewähltesGeisternetzId == null) {
            System.out.println("DEBUG: Kein Geisternetz ausgewählt");
            return;
        }

        Geisternetz geisternetz = em.find(Geisternetz.class, ausgewähltesGeisternetzId);
        if (geisternetz == null) {
            System.out.println("DEBUG: Geisternetz nicht gefunden");
            return;
        }

        System.out.println("DEBUG: Status vorher: " + geisternetz.getStatus());

        if (geisternetz.getStatus() != Geisternetz.Status.GEMELDET) {
            System.out.println("DEBUG: Status war nicht GEMELDET");
            return;
        }

        // Planende Person speichern
        em.persist(planendePerson);
        geisternetz.setPlanendePerson(planendePerson);

        // Status ändern und speichern
        geisternetz.setStatus(Geisternetz.Status.BERGUNG_BEVORSTEHEND);
        em.getTransaction().begin();
        em.merge(geisternetz);
        em.getTransaction().commit();

        System.out.println("DEBUG: Status nachher: " + geisternetz.getStatus());

        // Seite neu laden
        try {
            FacesContext.getCurrentInstance().getExternalContext().redirect("bergung_erfolgreich.xhtml");
        } catch (Exception e) {
            System.out.println("Fehler beim Reload: " + e.getMessage());
        }}


    // Status von "BERGUNG_BEVORSTEHEND" -> "GEBORGEN" setzen
    @Transactional
    public void alsGeborgenMelden() {
        if (ausgewähltesGeisternetzId == null) {
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_ERROR, "Fehler", "Kein Geisternetz ausgewählt."));
            return;
        }

        Geisternetz geisternetz = em.find(Geisternetz.class, ausgewähltesGeisternetzId);
        if (geisternetz == null) {
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_ERROR, "Fehler", "Geisternetz nicht gefunden."));
            return;
        }

        // Bergende Person speichern
        em.persist(bergendePerson);
        geisternetz.setBergendePerson(bergendePerson);
        geisternetz.setBergungDatum(bergungszeitpunkt);


        // Status aktualisieren
        geisternetz.setStatus(Geisternetz.Status.GEBORGEN);

        em.getTransaction().begin();
        em.merge(geisternetz);
        em.getTransaction().commit();

        FacesContext.getCurrentInstance().addMessage(null,
                new FacesMessage(FacesMessage.SEVERITY_INFO, "Erfolg", "Geisternetz wurde als geborgen markiert."));

        // Seite neu laden
        try {
            FacesContext.getCurrentInstance().getExternalContext().redirect("bergung_erfolgreich.xhtml");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Getter und Setter
    public void setAusgewähltesGeisternetzId(Long id) {
        System.out.println("DEBUG: Setter aufgerufen mit ID = " + id);
        this.ausgewähltesGeisternetzId = id;
    }

    public Long getAusgewähltesGeisternetzId() {
        System.out.println("DEBUG: Getter aufgerufen, aktueller Wert = " + this.ausgewähltesGeisternetzId);
        return this.ausgewähltesGeisternetzId;
    }


    public BergendePerson getBergendePerson() {
        return bergendePerson;
    }

    public void setBergendePerson(BergendePerson bergendePerson) {
        this.bergendePerson = bergendePerson;
    }

    public LocalDateTime getBergungszeitpunkt() {
        return bergungszeitpunkt;
    }

    public void setBergungszeitpunkt(LocalDateTime bergungszeitpunkt) {
        this.bergungszeitpunkt = bergungszeitpunkt;
    }


    public PlanendePerson getPlanendePerson() {return planendePerson;}

    public void setPlanendePerson(PlanendePerson planendePerson) {this.planendePerson = planendePerson;}

}