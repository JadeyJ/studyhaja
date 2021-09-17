package com.studyhaja.modules.event.event;

import com.studyhaja.modules.event.Enrollment;
import com.studyhaja.modules.event.Event;
import org.springframework.context.ApplicationEvent;

public class EnrollmentAcceptEvent extends EnrollmentEvent {
    public EnrollmentAcceptEvent(Enrollment enrollment) {
        super(enrollment, "모임 참가 신청을 확인했습니다. 모임에 참석하세요.");
    }
}
