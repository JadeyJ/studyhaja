package com.studyhaja.event.validator;

import com.studyhaja.account.form.SignUpForm;
import com.studyhaja.domain.Event;
import com.studyhaja.event.form.EventForm;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

import java.time.LocalDateTime;

@Component
public class EventValidator implements Validator {
    @Override
    public boolean supports(Class<?> aClass) {
        return EventForm.class.isAssignableFrom(aClass);
    }

    @Override
    public void validate(Object object, Errors errors) {
        EventForm eventForm = (EventForm) object;

        if( isNotValidEndEnrollmentDateTime(eventForm) ) {
            errors.rejectValue("endEnrollmentDateTime", "wrong.datetime", "모임 접수 종료 일시를 정확히 입력하세요.");
        }

        if( isNotValidEndDateTime(eventForm) ) {
            errors.rejectValue("endDateTime", "wrong.datetime", "모임 종료 일시를 정확히 입력하세요.");
        }

        if( isNotValidStartDateTime(eventForm) ) {
            errors.rejectValue("startDateTime", "wrong.datetime", "모임 시작 일시를 정확히 입력하세요.");
        }
    }

    private boolean isNotValidStartDateTime(EventForm eventForm) {
        return eventForm.getStartDateTime().isBefore(eventForm.getEndEnrollmentDateTime());
    }

    private boolean isNotValidEndEnrollmentDateTime(EventForm eventForm) {
        return eventForm.getEndEnrollmentDateTime().isBefore(LocalDateTime.now());
    }

    private boolean isNotValidEndDateTime(EventForm eventForm) {
        LocalDateTime endDateTime = eventForm.getEndDateTime();
        return endDateTime.isBefore(eventForm.getStartDateTime()) || endDateTime.isBefore(eventForm.getEndEnrollmentDateTime());
    }

    public void validateUpdateForm(EventForm eventForm, Event event, Errors errors) {
        if( eventForm.getLimitOfEnrollments() < event.getNumberOfAcceptedEnrollments() ) {
            errors.rejectValue("limitOfEnrollments", "wrong.value", "확인된 참가 신청 수 보다 변경할 모집 인원 수가 커야 합니다.");
        }
    }
}
