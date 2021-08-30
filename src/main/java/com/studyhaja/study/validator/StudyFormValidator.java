package com.studyhaja.study.validator;

import com.studyhaja.study.StudyRepository;
import com.studyhaja.study.form.StudyForm;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

@Component
@RequiredArgsConstructor
public class StudyFormValidator implements Validator {

    private StudyRepository studyRepository;

    @Override
    public boolean supports(Class<?> aClass) {
        return StudyForm.class.isAssignableFrom(aClass);
    }

    @Override
    public void validate(Object object, Errors errors) {
        StudyForm studyForm = (StudyForm) object;
        if(studyRepository.existsByPath(studyForm.getPath())) {
            errors.rejectValue("path", "wrong.path", "스터디 경로값을 사용할 수 없습니다.");
        }
    }
}
