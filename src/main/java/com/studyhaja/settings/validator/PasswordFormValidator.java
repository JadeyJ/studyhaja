package com.studyhaja.settings.validator;

import com.studyhaja.settings.form.PasswordForm;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

public class PasswordFormValidator implements Validator {
    @Override
    public boolean supports(Class<?> aClass) {
        return PasswordForm.class.isAssignableFrom(aClass);
    }

    @Override
    public void validate(Object object, Errors errors) {
        PasswordForm passwordForm = (PasswordForm) object;
        if ( !passwordForm.getNewPassword().equals(passwordForm.getNewPasswordConfirm()) ) {
            errors.rejectValue("newPassword", "wrong.value", "입력한 새 비밀번호가 일치하지 않습니다.");
        }
    }
}
