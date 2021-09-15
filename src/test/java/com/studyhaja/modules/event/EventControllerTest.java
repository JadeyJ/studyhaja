package com.studyhaja.modules.event;

import com.studyhaja.infra.MockMvcTest;
import com.studyhaja.modules.account.Account;
import com.studyhaja.modules.account.AccountFactory;
import com.studyhaja.modules.account.AccountRepository;
import com.studyhaja.modules.account.WithAccount;
import com.studyhaja.modules.study.Study;
import com.studyhaja.modules.study.StudyFactory;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@MockMvcTest
class EventControllerTest {

    @Autowired MockMvc mockMvc;
    @Autowired AccountFactory accountFactory;
    @Autowired StudyFactory studyFactory;
    @Autowired EventService eventService;
    @Autowired AccountRepository accountRepository;
    @Autowired EnrollmentRepository enrollmentRepository;

    @Test
    @DisplayName("선착순 모임에 참가 신청 - 자동 수락")
    @WithAccount("kimmy")
    void newEnrollment_to_FCFS_event_accepted() throws Exception {
        Account kimmy2 = accountFactory.createAccount("kimmy2");
        Study study = studyFactory.createStudy("test-study", kimmy2);
        Event event = createEvent("test-event", EventType.FCFS, 4, study, kimmy2);

        mockMvc.perform(post("/study/" + study.getPath() + "/events/" + event.getId() + "/enroll")
                .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/study/" + study.getPath() + "/events/" + event.getId()));

        Account kimmy = accountRepository.findByNickname("kimmy");
        isAccepted(kimmy, event);
    }

    @Test
    @DisplayName("선착순 모임에 참가 신청 - 대기중 (이미 인원이 꽉차서)")
    @WithAccount("kimmy")
    void newEnrollment_to_FCFS_event_not_accepted() throws Exception {
        Account kimmy2 = accountFactory.createAccount("kimmy2");
        Study study = studyFactory.createStudy("test-study", kimmy2);
        Event event = createEvent("test-event", EventType.FCFS, 2, study, kimmy2);

        Account may = accountFactory.createAccount("may");
        Account june = accountFactory.createAccount("june");
        eventService.newEnrollment(event, may);
        eventService.newEnrollment(event, june);

        mockMvc.perform(post("/study/" + study.getPath() + "/events/" + event.getId() + "/enroll")
                .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/study/" + study.getPath() + "/events/" + event.getId()));

        Account kimmy = accountRepository.findByNickname("kimmy");
        isNotAccepted(kimmy, event);
    }

    @Test
    @DisplayName("참가신청 확정자가 선착순 모임에 참가 신청을 취소하는 경우, 바로 다음 대기자를 자동으로 신청 확인한다.")
    @WithAccount("kimmy")
    void accepted_account_cancelEnrollment_to_FCFS_event_not_accepted() throws Exception {
        Account kimmy = accountRepository.findByNickname("kimmy");
        Account kimmy2 = accountFactory.createAccount("kimmy2");
        Account may = accountFactory.createAccount("may");
        Study study = studyFactory.createStudy("test-study", kimmy2);
        Event event = createEvent("test-event", EventType.FCFS, 2, study, kimmy2);

        eventService.newEnrollment(event, may);
        eventService.newEnrollment(event, kimmy);
        eventService.newEnrollment(event, kimmy2);

        isAccepted(may, event);
        isAccepted(kimmy, event);
        isNotAccepted(kimmy2, event);

        mockMvc.perform(post("/study/" + study.getPath() + "/events/" + event.getId() + "/disenroll")
                .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/study/" + study.getPath() + "/events/" + event.getId()));

        isAccepted(may, event);
        isAccepted(kimmy2, event);
        assertNull(enrollmentRepository.findByEventAndAccount(event, kimmy));
    }

    @Test
    @DisplayName("참가신청 비확정자가 선착순 모임에 참가 신청을 취소하는 경우, 기존 확정자를 그대로 유지하고 새로운 확정자는 없다.")
    @WithAccount("kimmy")
    void not_accepterd_account_cancelEnrollment_to_FCFS_event_not_accepted() throws Exception {
        Account kimmy = accountRepository.findByNickname("kimmy");
        Account kimmy2 = accountFactory.createAccount("kimmy2");
        Account may = accountFactory.createAccount("may");
        Study study = studyFactory.createStudy("test-study", kimmy2);
        Event event = createEvent("test-event", EventType.FCFS, 2, study, kimmy2);

        eventService.newEnrollment(event, may);
        eventService.newEnrollment(event, kimmy2);
        eventService.newEnrollment(event, kimmy);

        isAccepted(may, event);
        isAccepted(kimmy2, event);
        isNotAccepted(kimmy, event);

        mockMvc.perform(post("/study/" + study.getPath() + "/events/" + event.getId() + "/disenroll")
                .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/study/" + study.getPath() + "/events/" + event.getId()));

        isAccepted(may, event);
        isAccepted(kimmy2, event);
        assertNull(enrollmentRepository.findByEventAndAccount(event, kimmy));
    }

    private void isNotAccepted(Account kimmy2, Event event) {
        assertFalse(enrollmentRepository.findByEventAndAccount(event, kimmy2).isAccepted());
    }

    private void isAccepted(Account account, Event event) {
        assertTrue(enrollmentRepository.findByEventAndAccount(event, account).isAccepted());
    }

    @Test
    @DisplayName("관리자 확인 모임에 참가 신청 - 대기중")
    @WithAccount("kimmy")
    void newEnrollment_to_CONFIMATIVE_event_not_accepted() throws Exception {
        Account kimmy2 = accountFactory.createAccount("kimmy2");
        Study study = studyFactory.createStudy("test-study", kimmy2);
        Event event = createEvent("test-event", EventType.CONFIRMATIVE, 2, study, kimmy2);

        mockMvc.perform(post("/study/" + study.getPath() + "/events/" + event.getId() + "/enroll")
                .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/study/" + study.getPath() + "/events/" + event.getId()));

        Account kimmy = accountRepository.findByNickname("kimmy");
        isNotAccepted(kimmy, event);
    }

    private Event createEvent(String eventTitle, EventType eventType, int limit, Study study, Account account) {
        Event event = new Event();
        event.setTitle(eventTitle);
        event.setEventType(eventType);
        event.setStudy(study);
        event.setLimitOfEnrollments(limit);
        event.setCreatedBy(account);
        event.setCreatedDateTime(LocalDateTime.now());
        event.setEndEnrollmentDateTime(LocalDateTime.now().plusDays(1));
        event.setStartDateTime(LocalDateTime.now().plusDays(1).plusHours(5));
        event.setEndDateTime(LocalDateTime.now().plusDays(1).plusHours(7));
        return eventService.createEvent(event, study, account);
    }

}