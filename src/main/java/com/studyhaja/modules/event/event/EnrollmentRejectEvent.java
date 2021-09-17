package com.studyhaja.modules.event.event;

import com.studyhaja.modules.event.Enrollment;
import org.springframework.context.ApplicationEvent;

public class EnrollmentRejectEvent extends EnrollmentEvent {
    public EnrollmentRejectEvent(Enrollment enrollment) {
        super(enrollment, "모임 참가 신청을 거절했습니다.");
    }
}
