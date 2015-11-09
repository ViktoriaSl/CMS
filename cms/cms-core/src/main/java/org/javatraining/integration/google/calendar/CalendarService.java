package org.javatraining.integration.google.calendar;

import org.javatraining.model.PersonVO;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

/**
 * Created by olga on 27.05.15.
 */
public interface CalendarService {

    CalendarVO addCalendar(@Valid CalendarVO calendarVO);

    void removeCalendar(@NotNull CalendarVO calendarVO);

    @NotNull
    @Valid
    CalendarVO getCalendarById(@NotNull String calendarId);

    void addTeacherToCalendar(@NotNull CalendarVO calendarVO, @Valid PersonVO teacher);

    void removeTeacherFromCalendar(@NotNull CalendarVO calendarVO, @Valid PersonVO teacher);

    void addStudentToCalendar(@NotNull CalendarVO calendarVO, @Valid PersonVO student);

    void removeStudentFromCalendar(@NotNull CalendarVO calendarVO, @Valid PersonVO student);
}
