package com.studyhaja.event;

import com.studyhaja.account.CurrentUser;
import com.studyhaja.domain.Account;
import com.studyhaja.domain.Event;
import com.studyhaja.domain.Study;
import com.studyhaja.event.form.EventForm;
import com.studyhaja.event.validator.EventValidator;
import com.studyhaja.study.StudyRepository;
import com.studyhaja.study.StudyService;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.Errors;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Controller
@RequestMapping("/study/{path}")
@RequiredArgsConstructor
public class EventController {

    private final StudyService studyService;
    private final StudyRepository studyRepository;
    private final EventService eventService;
    private final EventRepository eventRepository;
    private final ModelMapper modelMapper;
    private final EventValidator eventValidator;

    @InitBinder(value = "eventForm")
    public void initBinder(WebDataBinder webDataBinder) {
        webDataBinder.addValidators(eventValidator);
    }

    @GetMapping("/new-event")
    public String newEventForm(@CurrentUser Account account, @PathVariable String path, Model model) {
        Study study = studyService.getStudyToUpdateStatus(account, path);
        model.addAttribute(study);
        model.addAttribute(account);
        model.addAttribute(new EventForm());
        return "event/form";
    }

    @PostMapping("/new-event")
    public String newEventSubmit(@CurrentUser Account account, @PathVariable String path, @Valid EventForm eventForm,
                                 Errors errors, Model model) {
        Study study = studyService.getStudyToUpdateStatus(account, path);
        if(errors.hasErrors()) {
            model.addAttribute(account);
            model.addAttribute(study);
            return "event/form";
        }
        Event event = eventService.createEvent(modelMapper.map(eventForm, Event.class), study, account);
        return "redirect:/study/" + study.getEncodedPath() + "/events/" + event.getId();
    }

    @GetMapping("/events/{id}")
    public String getEvent(@CurrentUser Account account, @PathVariable String path, @PathVariable Long id, Model model) {
        model.addAttribute(account);
        model.addAttribute(studyRepository.findStudyWithManagersByPath(path));
        model.addAttribute(eventRepository.findById(id).orElseThrow());
        return "event/view";
    }

    @GetMapping("/events")
    public String viewEvents(@CurrentUser Account account, @PathVariable String path, Model model) {
        Study study = studyService.getStudy(path);
        model.addAttribute(account);
        model.addAttribute(study);

        List<Event> events = eventRepository.findByStudyOrderByStartDateTime(study);
        List<Event> oldEvents = new ArrayList<>();
        List<Event> newEvents = new ArrayList<>();

        events.forEach(e -> {
            if(e.getEndDateTime().isAfter(LocalDateTime.now())) {
                newEvents.add(e);
            }else {
                oldEvents.add(e);
            }
        });

        model.addAttribute("oldEvents", oldEvents);
        model.addAttribute("newEvents", newEvents);

        return "study/events";
    }

    @GetMapping("/events/{id}/edit")
    public String updateEventForm(@CurrentUser Account account, @PathVariable String path, @PathVariable Long id, Model model) {
        Study study = studyService.getStudyToUpdate(account, path);
        Event event = eventRepository.findById(id).orElseThrow();
        model.addAttribute(account);
        model.addAttribute(study);
        model.addAttribute(event);
        model.addAttribute(modelMapper.map(event, EventForm.class));
        return "event/update-form";
    }

    @PostMapping("/events/{id}/edit")
    public String updateEventSubmit(@CurrentUser Account account, @PathVariable String path, @PathVariable Long id,
                                    @Valid EventForm eventForm, Errors errors, Model model) {
        Study study = studyService.getStudyToUpdate(account, path);
        Event event = eventRepository.findById(id).orElseThrow();
        eventForm.setEventType(event.getEventType());
        eventValidator.validateUpdateForm(eventForm, event, errors);

        if(errors.hasErrors()) {
            model.addAttribute(account);
            model.addAttribute(study);
            model.addAttribute(event);
            return "event/update-form";
        }

        eventService.updateEvent(event, eventForm);
        return "redirect:/study/" + study.getEncodedPath() + "/events/" + event.getId();
    }

    @DeleteMapping("/events/{id}")
    public String cancelEvent(@CurrentUser Account account, @PathVariable String path, @PathVariable Long id) {
        Study study = studyService.getStudyToUpdate(account, path);
        eventService.deleteEvent(eventRepository.findById(id).orElseThrow());
        return "redirect:/study/" + study.getEncodedPath() + "/events";
    }

    @PostMapping("/events/{id}/enroll")
    public String newEnrollment(@CurrentUser Account account, @PathVariable String path, @PathVariable Long id) {
        Study study = studyService.getStudyToEnroll(path);
        eventService.newEnrollment(eventRepository.findById(id).orElseThrow(), account);
        return "redirect:/study/" + study.getEncodedPath() + "/events/" + id;
    }

    @PostMapping("/events/{id}/disenroll")
    public String cancelEnrollment(@CurrentUser Account account, @PathVariable String path, @PathVariable Long id) {
        Study study = studyService.getStudyToEnroll(path);
        eventService.cancelEnrollment(eventRepository.findById(id).orElseThrow(), account);
        return "redirect:/study/" + study.getEncodedPath() + "/events/" + id;
    }
}
