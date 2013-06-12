/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.citius.reservas.models;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Objects;
import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author Esther Álvarez Feijoo
 */
@Entity
@Table(name = "reservations")
@NamedQueries(value = {
    @NamedQuery(name = "Reservation.findById", query = "SELECT r FROM Reservation r WHERE r.id = :id"),
    @NamedQuery(name = "Reservation.findByName", query = "SELECT r FROM Reservation r WHERE r.name = :name"),
    @NamedQuery(name = "Reservation.findAll", query = "SELECT r FROM Reservation r"),
    @NamedQuery(name = "Reservation.findByOwner", query = "SELECT r FROM Reservation r WHERE r.owner = :owner"),
    @NamedQuery(name = "Reservation.findBetweenDates", query = "SELECT r FROM Reservation r WHERE r.startDate <= :endDate OR r.endDate >= :startDate"),
    @NamedQuery(name = "Reservation.findByResource", query = "SELECT r FROM Reservation r WHERE :resource MEMBER OF r.resources"),

})
@XmlRootElement
public class Reservation implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    
    @NotNull
    @Size(max = 50)
    private String name;
    
    @Size(max = 250)
    private String description;
    
    @ManyToOne()
    @JoinColumn(name = "owner_id",
            referencedColumnName = "id")
    private User owner;
    
    @Temporal(javax.persistence.TemporalType.DATE)
    @Column(name = "start_date")
    private Calendar startDate;
    
    @Temporal(javax.persistence.TemporalType.DATE)
    @Column(name = "end_date")
    private Calendar endDate;
    
    @Temporal(javax.persistence.TemporalType.TIME)
    @Column(name = "start_time")
    private Calendar startTime;
    
    @Temporal(javax.persistence.TemporalType.TIME)
    @Column(name = "end_time")
    private Calendar endTime;
    
    @Embedded
    private Repetition repetition;
    
    @OneToMany(fetch = FetchType.LAZY,
            mappedBy = "reservation",
            cascade = CascadeType.ALL)
    private List<ReservationInstance> instances;
    
    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
            name = "reserved_resources",
            joinColumns = {
                @JoinColumn(name = "reservation_id", referencedColumnName = "id")},
            inverseJoinColumns = {
                @JoinColumn(name = "resource_id", referencedColumnName = "id")})
    private List<Resource> resources;

    public Reservation() {
        this.resources = new ArrayList<>();
    }
    
    public Reservation(String name, String description, User owner, Calendar startDate, Calendar endDate, Calendar startTime, Calendar endTime) {
        this.name = name;
        this.description = description;
        this.owner = owner;
        this.startDate = startDate;
        this.endDate = endDate;
        this.startTime = startTime;
        this.endTime = endTime;
    }

    public Reservation(String name, String description, User owner, Calendar startDate, Calendar endDate, Calendar startTime, Calendar endTime, Repetition repetition) {
        this.name = name;
        this.description = description;
        this.owner = owner;
        this.startDate = startDate;
        this.endDate = endDate;
        this.startTime = startTime;
        this.endTime = endTime;
        this.repetition = repetition;
    }

    public Reservation(Integer id, String name, String description, User owner, Calendar startDate, Calendar endDate, Calendar startTime, Calendar endTime, Repetition repetition) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.owner = owner;
        this.startDate = startDate;
        this.endDate = endDate;
        this.startTime = startTime;
        this.endTime = endTime;
        this.repetition = repetition;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public User getOwner() {
        return owner;
    }

    public void setOwner(User owner) {
        this.owner = owner;
    }

    public Calendar getStartDate() {
        return startDate;
    }

    public void setStartDate(Calendar startDate) {
        this.startDate = startDate;
    }

    public Calendar getEndDate() {
        return endDate;
    }

    public void setEndDate(Calendar endDate) {
        this.endDate = endDate;
    }

    public Calendar getStartTime() {
        return startTime;
    }

    public void setStartTime(Calendar startTime) {
        this.startTime = startTime;
    }

    public Calendar getEndTime() {
        return endTime;
    }

    public void setEndTime(Calendar endTime) {
        this.endTime = endTime;
    }

    public Repetition getRepetition() {
        return repetition;
    }

    public void setRepetition(Repetition repetition) {
        this.repetition = repetition;
    }

    public List<ReservationInstance> getInstances() {
        return instances;
    }

    public void setInstances(List<ReservationInstance> instances) {
        this.instances = instances;
    }
    
    public void setDateTime(Calendar startDate, Calendar endDate, Calendar startTime, Calendar endTime){
        this.startDate = startDate;
        this.endDate = endDate;
        this.startTime = startTime;
        this.endTime = endTime;
    }

    public List<Resource> getResources() {
        return resources;
    }

    public void setResources(List<Resource> resources) {
        this.resources = resources;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 37 * hash + Objects.hashCode(this.name);
        hash = 37 * hash + Objects.hashCode(this.startDate);
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final Reservation other = (Reservation) obj;
        if (!Objects.equals(this.name, other.name)) {
            return false;
        }
        if (!Objects.equals(this.startDate, other.startDate)) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "Reservation{" + "id=" + id + ", name=" + name + ", description=" + description + ", owner=" + owner + ", startDate=" + startDate + ", endDate=" + endDate + ", startTime=" + startTime + ", endTime=" + endTime + ", repetition=" + repetition + '}';
    }

}
