package com.studyhaja.event;

import com.studyhaja.domain.Account;
import com.studyhaja.domain.Event;
import com.studyhaja.domain.Study;
import com.studyhaja.event.form.EventForm;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@Transactional
@RequiredArgsConstructor
public class EventService {

    private final EventRepository eventRepository;
    private final ModelMapper modelMapper;

    public Event createEvent(Event event, Study study, Account account) {
        event.setCreatedBy(account);
        event.setCretedDateTime(LocalDateTime.now());
        event.setStudy(study);
        return eventRepository.save(event);
    }

    public void updateEvent(Event event, EventForm eventForm) {
        modelMapper.map(eventForm, event);
        // TODO 선착순 모임의 경우, 추가 인원의 참가 신청 후 상태를 자동으로 '확정'으로 변경.
    }
}
