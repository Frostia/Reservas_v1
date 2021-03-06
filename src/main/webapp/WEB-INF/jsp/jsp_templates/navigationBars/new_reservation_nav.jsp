<%-- 
    Document   : reservation_nav
    Created on : 21-jun-2013, 19:25:15
    Author     : Esther Álvarez Feijoo
--%>

<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@taglib uri="http://www.springframework.org/tags" prefix="s"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>

<nav id="reservation_nav">
    <nav class="left">
        <ul>

            <!--Return-->
            <li>
                <a class="reservas_btn return" 
                   name="return"
                   href="<c:url value="/reservations/${reservation.id}" />" 
                   id="create_resource">
                    <span class="icon-arrow-left-3"></span>
                    <s:message code="form.returnBtn" />
                </a>
            </li>

            <!--Save-->
            <li>
                <a class="reservas_btn" 
                   name="submit"
                   href="#"
                   create="<s:message code="form.createBtn"/>" 
                   update="<s:message code="form.updateBtn"/>"
                   id="create_resource">
                    <s:message code="form.saveBtn" />
                </a>
            </li>

        </ul>
    </nav>
    <c:if test="${operation eq 'update'}">

        <nav class="right">
            <ul>
                <!--Delete-->
                <li>
                    <a class="reservas_btn delete"  
                       href="#"
                       name="delete"
                       reservationId="${reservation.id}"
                       id="create_resource">
                        <s:message code="form.deleteBtn" />
                        <span class="icon-remove"></span>
                    </a>
                </li>
            </ul>
        </nav>
    </c:if>
</nav>