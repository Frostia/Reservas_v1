/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.citius.reservas.controllers.impl;

import com.citius.reservas.business.AccessBusiness;
import com.citius.reservas.business.InvitationBusiness;
import com.citius.reservas.controllers.InvitationController;
import com.citius.reservas.exceptions.InputRequestValidationException;
import com.citius.reservas.exceptions.UnknownResourceException;
import com.citius.reservas.models.Invitation;
import com.citius.reservas.models.InvitationState;
import java.util.List;
import javax.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;

/**
 *
 * @author Esther
 */
@Controller
public class InvitationControllerImpl implements InvitationController {

    @Autowired
    private InvitationBusiness ib;
    @Autowired
    private AccessBusiness access;

    @Override
    public List<Invitation> readPendingInvitations() {
        String uniqueName = access.getUniqueNameOfLoggedUser();
        return ib.getPendingInvitationsByGuest(uniqueName);
    }

    //Role Admin
    @Override
    public List<Invitation> readPendingInvitations(
            @PathVariable(value = "uniqueName") String guestUniqueName) {
        
        return ib.getPendingInvitationsByGuest(guestUniqueName);

    }

    @Override
    public Invitation createInvitation(@Valid @RequestBody Invitation invitation,
            BindingResult result)
            throws InputRequestValidationException, UnknownResourceException {

        String uniqueName = access.getUniqueNameOfLoggedUser();

        if (!invitation.getReservation().getOwner().getUniqueName().equals(uniqueName)
                && !access.isAdmin()) {
            throw new AccessDeniedException("");
        }

        if (!result.getAllErrors().isEmpty()) {
            throw new InputRequestValidationException(result.getAllErrors());
        }

        return ib.createOrSaveInvitation(invitation);
    }

    //Admin
    @Override
    public Invitation changeStateInvitation(
            @PathVariable(value = "reservationId") Integer reservationId,
            @PathVariable(value = "uniqueName") String guestUniqueName,
            @PathVariable(value = "state") InvitationState state)
            throws UnknownResourceException {

        Invitation invitation = ib.changeStateInvitation(reservationId, guestUniqueName, state);
        return invitation;
    }

    @Override
    public Invitation changeStateInvitation(@PathVariable(value = "reservationId") Integer reservationId,
            @PathVariable(value = "state") InvitationState state)
            throws UnknownResourceException {
        
        String uniqueName = access.getUniqueNameOfLoggedUser();

        Invitation invitation = ib.changeStateInvitation(reservationId, uniqueName, state);

        return invitation;
    }
//    @Override
//    public String mismatch(HttpServletRequest request, Model model) throws NoSuchRequestHandlingMethodException {
//        throw new NoSuchRequestHandlingMethodException(request);
//    }
}
