package com.citius.reservas.models;

import java.io.Serializable;
import java.util.Date;
import java.util.Objects;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.constraints.NotNull;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author Esther Álvarez Feijoo
 */
@Entity
@Table(name = "reservation_instances")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "ReservationInstance."
            + "findAll",
            query = "SELECT r FROM ReservationInstance r"),
    @NamedQuery(name = "ReservationInstance."
            + "findByReservation",
            query = "SELECT r FROM ReservationInstance r WHERE "
            + "r.reservation.id = :reservationId"),
    @NamedQuery(name = "ReservationInstance."
            + "findBetweenDates",
            query = "SELECT r FROM ReservationInstance r WHERE "
            + "(r.startTimeDate BETWEEN :startTimeDate AND :endTimeDate) OR "
            + "(r.endTimeDate BETWEEN :startTimeDate AND :endTimeDate) OR "
            + "(r.startTimeDate <= :startTimeDate AND :startTimeDate < r.endTimeDate) OR "
            + "(r.startTimeDate < :endTimeDate AND :endTimeDate <= r.endTimeDate)"),
    @NamedQuery(name = "ReservationInstance."
            + "findBetweenDatesByUser",
            query = "SELECT r FROM ReservationInstance r WHERE "
            + "((r.startTimeDate BETWEEN :startTimeDate AND :endTimeDate) OR "
            + "(r.endTimeDate BETWEEN :startTimeDate AND :endTimeDate) OR"
            + "(r.startTimeDate <= :startTimeDate AND :startTimeDate < r.endTimeDate) OR "
            + "(r.startTimeDate < :endTimeDate AND :endTimeDate <= r.endTimeDate))"
            + "AND ("
            + "(r.reservation.owner.uniqueName LIKE :userUniqueName) OR "
            + "(r.reservation.id IN "
                + "(SELECT i.reservation.id FROM Invitation i"
            + "      WHERE i.guest.uniqueName LIKE :userUniqueName2 AND i.state LIKE com.citius.reservas.models.InvitationState.ACCEPTED)))"
            + "ORDER BY r.startTimeDate"),
    @NamedQuery(name = "ReservationInstance."
            + "findBetweenDatesByResource",
            query = "SELECT r "
            + "FROM ReservationInstance r JOIN r.reservation.resources rce "
            + "WHERE "
            + "((r.startTimeDate BETWEEN :startTimeDate AND :endTimeDate) OR "
            + "(r.endTimeDate BETWEEN :startTimeDate AND :endTimeDate) OR "
            + "(r.startTimeDate <= :startTimeDate AND :startTimeDate < r.endTimeDate) OR "
            + "(r.startTimeDate < :endTimeDate AND :endTimeDate <= r.endTimeDate)) "
            + "AND (rce.id = :resourceId) "
            + "ORDER BY r.startTimeDate"),
    @NamedQuery(name = "ReservationInstance."
            + "findBetweenDatesByResourceGroup",
            query = "SELECT r "
            + "FROM ReservationInstance r JOIN r.reservation.resources rce "
            + "WHERE "
            + "((r.startTimeDate BETWEEN :startTimeDate AND :endTimeDate) OR "
            + "(r.endTimeDate BETWEEN :startTimeDate AND :endTimeDate) OR "
            + "(r.startTimeDate <= :startTimeDate AND :startTimeDate< r.endTimeDate) OR "
            + "(r.startTimeDate < :endTimeDate AND :endTimeDate <= r.endTimeDate)) "
            + "AND rce.group.id = :resourceGroupId "
            + "ORDER BY r.startTimeDate")
})
public class ReservationInstance implements Serializable {

    private static final long serialVersionUID = 2L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    @NotNull
    @Column(name = "start_time_date")
    @Temporal(TemporalType.TIMESTAMP)
    private Date startTimeDate;
    @NotNull
    @Column(name = "end_time_date")
    @Temporal(TemporalType.TIMESTAMP)
    private Date endTimeDate;
    @JoinColumn(name = "reservation_id", referencedColumnName = "id")
    @ManyToOne(optional = false)
    private Reservation reservation;

    public ReservationInstance() {
    }

    public ReservationInstance(Integer id) {
        this.id = id;
    }

    public ReservationInstance(Reservation reservation, Date startTimeDate, Date endTimeDate) {
        this.reservation = reservation;
        this.startTimeDate = startTimeDate;
        this.endTimeDate = endTimeDate;
    }

    public ReservationInstance(Reservation reservation, Date startTimeDate, Long duration) {
        this.reservation = reservation;
        this.startTimeDate = startTimeDate;
        this.endTimeDate = new Date(startTimeDate.getTime() + duration);
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Date getStartTimeDate() {
        return startTimeDate;
    }

    public void setStartTimeDate(Date startTimeDate) {
        this.startTimeDate = startTimeDate;
    }

    public Date getEndTimeDate() {
        return endTimeDate;
    }

    public void setEndTimeDate(Date endTimeDate) {
        this.endTimeDate = endTimeDate;
    }

    public Reservation getReservation() {
        return reservation;
    }

    public void setReservation(Reservation reservation) {
        this.reservation = reservation;
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 31 * hash + Objects.hashCode(this.id);
        hash = 31 * hash + Objects.hashCode(this.startTimeDate);
        hash = 31 * hash + Objects.hashCode(this.endTimeDate);
        hash = 31 * hash + Objects.hashCode(this.reservation.getId());
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
        final ReservationInstance other = (ReservationInstance) obj;
        if (!Objects.equals(this.id, other.id)) {
            return false;
        }
        if (!Objects.equals(this.startTimeDate, other.startTimeDate)) {
            return false;
        }
        if (!Objects.equals(this.endTimeDate, other.endTimeDate)) {
            return false;
        }
        if (!Objects.equals(this.reservation, other.reservation)) {
            return false;
        }
        return true;
    }

    

    @Override
    public String toString() {
        return "ReservationInstance{" + "id=" + id + ", startTimeDate=" + startTimeDate + ", endTimeDate=" + endTimeDate + ", reservationId=" + reservation.getId() + '}';
    }
}
