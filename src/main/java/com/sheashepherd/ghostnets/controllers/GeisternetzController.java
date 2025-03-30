package com.sheashepherd.ghostnets.controllers;


import jakarta.persistence.TypedQuery;
import java.util.List;

import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Named;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;
import jakarta.transaction.Transactional;

import com.sheashepherd.ghostnets.models.Geisternetz;
import com.sheashepherd.ghostnets.models.MeldendePerson;

@Named
@RequestScoped
public class GeisternetzController {

    // JPA-Komponenten - Datenbankzugriff
    private EntityManagerFactory emf;
    private EntityManager em;

    // Zustandsobjekte - Formularbindung
    private Geisternetz geisternetz = new Geisternetz();
    private MeldendePerson meldendePerson = new MeldendePerson();
    private boolean anonym = false;
    private String statusFilter;

    // Gibt alle Geisternetze aus der Datenbank zurück
    public List<Geisternetz> getAlleGeisternetze() {
        if (em == null) {
            System.out.println("EntityManager ist NULL!");
            return null;
        }

        TypedQuery<Geisternetz> query = em.createQuery("SELECT g FROM Geisternetz g", Geisternetz.class);
        return query.getResultList();
    }

    // Initialisiert EntityManager beim Laden der Seite
    @PostConstruct
    public void init() {
        try {
            emf = Persistence.createEntityManagerFactory("ghostnetsPU");
            em = emf.createEntityManager();
            System.out.println("EntityManager erfolgreich initialisiert!");
        } catch (Exception e) {
            System.err.println("Fehler beim Initialisieren des EntityManagers: " + e.getMessage());
        }
    }

    // Verarbeitet neue Netz-Meldung
    @Transactional
    public String melden() {
        if (em == null) {
            System.out.println("EntityManager ist NULL!");
            return "error";
        }

        geisternetz.generateStandortFromCoordinates();

        if (anonym) {
            System.out.println("Anonyme Meldung erkannt");
            MeldendePerson anonymePerson = new MeldendePerson();
            anonymePerson.setAnonym(true);
            em.getTransaction().begin();
            em.persist(anonymePerson);
            em.getTransaction().commit();
            geisternetz.setMeldendePerson(anonymePerson);
        } else {
            System.out.println("Normale Meldung erkannt");
            System.out.println("Name: " + meldendePerson.getVorname() + " " + meldendePerson.getNachname());
            System.out.println("Telefon: " + meldendePerson.getTelefon());
            em.getTransaction().begin();
            em.persist(meldendePerson);
            em.getTransaction().commit();
            geisternetz.setMeldendePerson(meldendePerson);
        }
        // Netz mit Status „GEMELDET“ speichern
        geisternetz.setStatus(Geisternetz.Status.GEMELDET);
        em.getTransaction().begin();
        em.persist(geisternetz);
        em.getTransaction().commit();
        System.out.println("Geisternetz erfolgreich gemeldet");
        geisternetz = new Geisternetz();
        meldendePerson = new MeldendePerson();
        return "meldung_erfolgreich.xhtml";
    }

    // Getter und Setter
    public Geisternetz getGeisternetz() {
        return geisternetz;
    }

    public void setGeisternetz(Geisternetz geisternetz) {
        this.geisternetz = geisternetz;
    }

    public MeldendePerson getMeldendePerson() {
        return meldendePerson;
    }

    public void setMeldendePerson(MeldendePerson meldendePerson) {
        this.meldendePerson = meldendePerson;
    }

    public String getStatusFilter() {
        return statusFilter;
    }

    public void setStatusFilter(String statusFilter) {
        this.statusFilter = statusFilter;
    }

    public List<Geisternetz> getGefilterteGeisternetze() {
        if (statusFilter == null || statusFilter.isEmpty()) {
            // Kein Filter -> Alle Netze
            return em.createQuery("SELECT g FROM Geisternetz g", Geisternetz.class)
                    .getResultList();
        } else {
            // Nur Netze mit passendem Status
            return em.createQuery("SELECT g FROM Geisternetz g WHERE g.status = :status", Geisternetz.class)
                    .setParameter("status", Geisternetz.Status.valueOf(statusFilter))
                    .getResultList();
        }
    }
    public void filterAnwenden() {
    }
}
