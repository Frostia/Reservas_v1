package com.citius.reservas.controllers.impl;

import com.citius.reservas.controllers.*;
import com.citius.reservas.business.AccessBusiness;
import com.citius.reservas.business.ReservationBusiness;
import com.citius.reservas.business.ResourceGroupBusiness;
import com.citius.reservas.controllers.controllerModel.ReservationCustom;
import com.citius.reservas.controllers.controllerModel.ReservationInstanceCustom;
import com.citius.reservas.controllers.i18n.i18nReservationsHelper;
import com.citius.reservas.exceptions.InputRequestValidationException;
import com.citius.reservas.exceptions.NotAvaliableException;
import com.citius.reservas.exceptions.NotPossibleInstancesException;
import com.citius.reservas.exceptions.UnknownResourceException;
import com.citius.reservas.models.Invitation;
import com.citius.reservas.models.Reservation;
import com.citius.reservas.models.ReservationInstance;
import com.citius.reservas.models.ResourceGroup;
import com.citius.reservas.models.User;
import java.io.IOException;
import java.util.Calendar;
import java.util.List;
import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import org.apache.log4j.Logger;
import org.codehaus.jackson.map.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;

@Controller
public class ReservationControllerImpl implements ReservationController {

    @Autowired
    private ReservationBusiness rb;
    @Autowired
    private ResourceGroupBusiness rgb;
    @Autowired
    private AccessBusiness access;
    @Autowired
    private i18nReservationsHelper rch;
    @Autowired
    private ObjectMapper mapper;
    private static final Logger logger = Logger.getLogger(ReservationControllerImpl.class);

    @Override
    public void initBinder(WebDataBinder binder) {
//        binder.setValidator(new ReservationValidator());
    }

    @Override
    public List<ReservationInstance> readAll() {
        return rb.readAll();
    }

    @Override
    public List<ReservationInstance> readAllMonth(
            @PathVariable(value = "year") Integer year,
            @PathVariable(value = "month") Integer month,
            @PathVariable(value = "id") Integer id) {
        return rb.readByMonthByResource(id, month, year);
    }

    @Override
    public List<ReservationInstance> readAllWeek(
            @PathVariable(value = "year") Integer year,
            @PathVariable(value = "week") Integer week,
            @PathVariable(value = "id") Integer id) {
        Calendar c = Calendar.getInstance();
        c.set(year, 0, 0, 0, 0, 0);
        c.set(Calendar.WEEK_OF_YEAR, week);
        return rb.readByWeekByResource(id, c);
    }

    @Override
    public List<ReservationInstance> readAll(
            @PathVariable(value = "year") Integer year,
            @PathVariable(value = "month") Integer month) {
        return rb.readByMonth(month, year);
    }

    @Override
    public Reservation read(@PathVariable(value = "id") Integer id) throws UnknownResourceException {
        return rb.read(id);
    }

    @Override
    public List<ReservationInstance> readAllByLoggedUser(
            @PathVariable(value = "year") Integer year,
            @PathVariable(value = "month") Integer month) {
        String uniqueName = access.getUniqueNameOfLoggedUser();
        return rb.readByMonthByUser(uniqueName, month - 1, year);
    }

    @Override
    public Reservation create(@Valid @RequestBody ReservationCustom r, BindingResult result)
            throws NotAvaliableException, NotPossibleInstancesException,
            InputRequestValidationException, UnknownResourceException {

//        Errors errors 
//        ReservationValidator validator = new ReservationValidator();
//        validator.validate(r, result.get);

        if (!result.getAllErrors().isEmpty()) {
            throw new InputRequestValidationException(result.getAllErrors());
        }

        String uniqueName = access.getUniqueNameOfLoggedUser();
        r.getOwner().setUniqueName(uniqueName);

        Reservation created = rb.saveReservation(r.getParentInstance(), r.getGroups());
        return created;
    }

    @Override
    public Reservation update(@Valid @RequestBody ReservationCustom r, BindingResult result)
            throws NotAvaliableException, NotPossibleInstancesException,
            InputRequestValidationException, UnknownResourceException {

        String uniqueName = access.getUniqueNameOfLoggedUser();

        if (!rb.canEdit(r.getId(), uniqueName)) {
            throw new AccessDeniedException("");
        } else if (!result.getAllErrors().isEmpty()) {
            throw new InputRequestValidationException(result.getAllErrors());
        }

        return rb.updateReservation(r.getParentInstance(), r.getGroups());

    }

    @Override
    public Boolean delete(@PathVariable(value = "id") Integer id) throws AccessDeniedException, UnknownResourceException {
        String uniqueName = access.getUniqueNameOfLoggedUser();

        if (!rb.canEdit(id, uniqueName)) {
            throw new AccessDeniedException("Logged user is not admin or is not the reservation's owner");
        }

        rb.deleteReservation(id);

        return true;
    }

    /**
     * ****************** HTML ************************
     */
    @Override
    public String redirectReservation() {
        Calendar now = Calendar.getInstance();

        String url = "/reservations/week/" + now.get(Calendar.YEAR) + "/" + now.get(Calendar.WEEK_OF_YEAR);

        return "redirect:" + url;
    }

    @Override
    public String weeklyReservation() {
        Calendar now = Calendar.getInstance();

        String url = "/reservations/week/" + now.get(Calendar.YEAR) + "/" + now.get(Calendar.WEEK_OF_YEAR);

        this.logger.debug("Redirecting now: " + url);

        return "redirect:" + url;
    }

    @Override
    public String monthlyReservation() {
        Calendar now = Calendar.getInstance();
        Integer year = now.get(Calendar.YEAR);
        Integer month = now.get(Calendar.MONTH) + 1;

        String url = "/reservations/month/" + year + "/" + month;
        return "redirect:" + url;
    }

    @Override
    public String monthlyReservation(Model model, 
            @PathVariable(value = "year") Integer year,
            @PathVariable(value = "month") Integer month) {
        model.addAttribute("year", year);
        model.addAttribute("month", month);
        return "monthly_reservations";
    }

    @Override
    public String weeklyReservationView(Model model, HttpServletRequest request,
            @PathVariable(value = "year") Integer year,
            @PathVariable(value = "week") Integer week) {

        //String uniqueName = ab.getLoggedUser();
        this.logger.debug("Year:" + year + ". Week:" + week);

        String uniqueName = access.getUniqueNameOfLoggedUser();

        //Check if URL is right
        Calendar c = Calendar.getInstance();

        c.set(Calendar.HOUR_OF_DAY, 0);
        c.set(Calendar.MINUTE, 0);
        c.set(Calendar.SECOND, 0);
        c.set(Calendar.MILLISECOND, 0);

        c.setMinimalDaysInFirstWeek(1);
        c.set(Calendar.YEAR, year);
        c.set(Calendar.WEEK_OF_YEAR, week);

        //If URL not right, redirect to right one
        if (year != c.get(Calendar.YEAR)) {
            String url = "/reservations/week/" + c.get(Calendar.YEAR) + "/" + c.get(Calendar.WEEK_OF_YEAR);
            return "redirect:" + url;
        }

        //Get weekDescription
        String weekDescription = rch.weekDescription(c, request);

        List<ReservationInstance> instances = rb.readByWeekByUser(uniqueName, c);
        List<ReservationInstanceCustom> customInstances = rch.parseToWeeklyList(instances, request);

        //URL of Previous Week
        c.add(Calendar.WEEK_OF_YEAR, -1);
        String previous = "/reservations/week/" + c.get(Calendar.YEAR) + "/" + c.get(Calendar.WEEK_OF_YEAR);
        //URL of Next Week
        c.add(Calendar.WEEK_OF_YEAR, +2);
        String next = "/reservations/week/" + c.get(Calendar.YEAR) + "/" + c.get(Calendar.WEEK_OF_YEAR);
        c.add(Calendar.WEEK_OF_YEAR, -1);

        model.addAttribute("weekDescription", weekDescription);
        model.addAttribute("reservationInstances", customInstances);
        model.addAttribute("next", next);
        model.addAttribute("previous", previous);


        c.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY);
        model.addAttribute("start", c);
        Calendar end = (Calendar) c.clone();
        end.set(Calendar.DAY_OF_WEEK, Calendar.SUNDAY);
        model.addAttribute("end", end);
        return "weekly_reservations";
    }

    private void initializeNewReservationView(Model model) throws IOException {
        List<ResourceGroup> resourcesList = rgb.readAll();
        List<User> usersList = access.getUsers();

        String resourcesJson = mapper.writeValueAsString(resourcesList);
        String usersJson = mapper.writeValueAsString(usersList);

        model.addAttribute("resourcesJson", resourcesJson);
        model.addAttribute("usersJson", usersJson);
    }

    @Override
    public String createReservationView(Model model) throws IOException {

        this.initializeNewReservationView(model);
        model.addAttribute("operation", "create");

        return "new_reservation";
    }

    @Override
    public String createReservationView(Model model, 
            @PathVariable(value = "year") Integer year,
            @PathVariable(value = "month") Integer month,
            @PathVariable(value = "day") Integer day) throws IOException {

        Calendar now = Calendar.getInstance();
        Integer yearNow = now.get(Calendar.YEAR);
        Integer monthNow = now.get(Calendar.MONTH) + 1;
        Integer dayNow = now.get(Calendar.DATE);

        if ((year > yearNow)
                || (year == yearNow && month > monthNow)
                || (year == yearNow && month == monthNow && day >= dayNow)) {

            model.addAttribute("year", year);
            model.addAttribute("month", month);
            model.addAttribute("day", day);
            model.addAttribute("operation", "create");
        }
        return this.createReservationView(model);
    }

    @Override
    public String updateReservationView(Model model, 
            @PathVariable(value = "id") Integer id)
            throws IOException, UnknownResourceException, AccessDeniedException {
        String uniqueName = access.getUniqueNameOfLoggedUser();

        Boolean canEdit = (access.isAdmin() || rb.isOwner(id, uniqueName));

        if (canEdit) {

            this.initializeNewReservationView(model);
            String reservationJson = rb.readAsJson(this.mapper, id);
            model.addAttribute("reservationJson", reservationJson);
            model.addAttribute("operation", "update");
            return "new_reservation";
        } else {
            throw new AccessDeniedException("");
        }
    }

    @Override
    public String viewReservation(Model model, 
        @PathVariable(value = "id") Integer id) throws UnknownResourceException {
        Reservation reservation = rb.read(id);
        if (reservation != null) {
            String uniqueName = access.getUniqueNameOfLoggedUser();
            Boolean canEdit = (access.isAdmin() || rb.isOwner(id, uniqueName));

            model.addAttribute("canEdit", canEdit);

            String invitation = "";
            for (Invitation inv : reservation.getInvitations()) {
                if (inv.getGuest().getUniqueName().equals(uniqueName)) {
                    model.addAttribute("invitation", inv.getState().toString());
                    break;
                }
            }
            model.addAttribute("reservation", reservation);
            return "view_reservation";
        } else {
            throw new UnknownResourceException();
        }
    }
}
