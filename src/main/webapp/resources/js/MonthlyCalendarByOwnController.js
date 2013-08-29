
var MonthlyCalendarByOwn = function(year, month){
    MyCalendar.call(this, MonthlyCalendarByOwn.events , year, month, null, MonthlyCalendarByOwn.path);    
    $('#calendar').fullCalendar('option','aspectRatio', 16/6);
    window.addEventListener("popstate", function(e) {
        var historyObject = e.state;
        $('#calendar').fullCalendar('gotoDate', historyObject.year, historyObject.month-1);
    });
};

/*Extends of Calendar*/
MonthlyCalendarByOwn.prototype = Object.create(MyCalendar.prototype);

MonthlyCalendarByOwn.path={"monthly": applicationPath+'reservations/month/'};


MonthlyCalendarByOwn.events = function(start, end, callback){
    
    start.setDate(start.getDate()+7);
    var year = start.getFullYear();
    var month = start.getMonth()+1;
    var label = MyCalendar.monthNames[locale][month-1]+' '+year;
    
    //On ajax success
    var success = function(response){
        $('#reservation_nav .label').text(label);
        var events = MonthlyCalendarByOwn.serializeToCalendarEvents(response);
        
        
        var historyObject={"year": year, "month": month, "events": events};
        MyCalendar.updateLinks(MonthlyCalendarByOwn.path);
        callback(events);
    };
    
    //Ajax Request
    Reservation.getOwnReservationsByMonth(year, month, success, ajaxError);
};




MonthlyCalendarByOwn.serializeToCalendarEvents = function(instances) {
    var events = [];

    for (var i = 0; i < instances.length; i++) {
        var instance = instances[i];
        var event = {
            title: instance.reservation.name,
            start: new Date(instance.startTimeDate),
            end: new Date(instance.endTimeDate),
            url: applicationPath + 'reservations/' + instance.reservation.id
        };
        
        event.backgroundColor = (loggedUniqueName == instance.reservation.owner.uniqueName)? 
            MyCalendar.orange : event.backgroundColor = MyCalendar.grey;

        events.push(event);
    }
    return events;
};