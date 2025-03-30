package com.sheashepherd.ghostnets.models;
import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "geisternetze")
public class Geisternetz {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String standort;
    private Double groesse;

    @Column(nullable = false)
    private Double latitude;

    @Column(nullable = false)
    private Double longitude;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Status status;

    @ManyToOne
    @JoinColumn(name = "meldende_person_id", nullable = false)
    private MeldendePerson meldendePerson;

    @ManyToOne
    @JoinColumn(name = "bergende_person_id")
    private BergendePerson bergendePerson;

    @ManyToOne
    @JoinColumn(name = "planende_person_id")
    private PlanendePerson planendePerson;



    @Column(name = "meldung_datum")
    private LocalDateTime meldungDatum;

    @Column(name = "bergung_datum")
    private LocalDateTime bergungDatum;

    @Column(name = "sichtungszeitpunkt")
    private LocalDateTime sichtungszeitpunkt;

    public enum Status {
        GEMELDET,
        BERGUNG_BEVORSTEHEND,
        GEBORGEN,
        VERSCHOLLEN
    }

    // Konstruktor für neue Meldungen
    public Geisternetz() {
        this.meldungDatum = LocalDateTime.now();
        this.status = Status.GEMELDET;
    }

    // GETTER UND SETTER
    public Long getId() {
        return id;
    }

    public Double getGroesse() {
        return groesse;
    }

    public void setGroesse(Double groesse) {
        this.groesse = groesse;
    }

    public Double getLatitude() {
        return latitude;
    }

    public void setLatitude(Double latitude) {
        this.latitude = latitude;
    }

    public Double getLongitude() {
        return longitude;
    }

    public void setLongitude(Double longitude) {
        this.longitude = longitude;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public MeldendePerson getMeldendePerson() {
        return meldendePerson;
    }

    public void setMeldendePerson(MeldendePerson meldendePerson) {
        this.meldendePerson = meldendePerson;
    }

    public BergendePerson getBergendePerson() {
        return bergendePerson;
    }

    public void setBergendePerson(BergendePerson bergendePerson) {
        this.bergendePerson = bergendePerson;
    }

    public LocalDateTime getSichtungszeitpunkt() {
        return sichtungszeitpunkt;
    }

    public void setSichtungszeitpunkt(LocalDateTime sichtungszeitpunkt) {
        this.sichtungszeitpunkt = sichtungszeitpunkt;
    }

    public LocalDateTime getBergungDatum() {return bergungDatum;}
    public void setBergungDatum(LocalDateTime bergungDatum) {this.bergungDatum = bergungDatum;}

    public PlanendePerson getPlanendePerson() {
        return planendePerson;
    }

    public void setPlanendePerson(PlanendePerson planendePerson) {
        this.planendePerson = planendePerson;
    }


    // Hilfsmethode zum Generieren des Standort-Textes aus Koordinaten
    public void generateStandortFromCoordinates() {
        if (latitude != null && longitude != null) {
            this.standort = String.format("%.6f, %.6f", latitude, longitude);
        }
    }
}