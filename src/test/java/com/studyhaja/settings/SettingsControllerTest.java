package com.studyhaja.settings;

import com.studyhaja.WithAccount;
import com.studyhaja.account.AccountRepository;
import com.studyhaja.domain.Account;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class SettingsControllerTest {

    @Autowired
    MockMvc mockMvc;

    @Autowired
    AccountRepository accountRepository;

    @AfterEach
    void afterEach() {
        accountRepository.deleteAll();
    }

    @WithAccount("kimmy")
    @DisplayName("프로필 수정 폼")
    @Test
    void updateProfileForm() throws Exception {
        String bio = "소개를 수정합니다.";
        mockMvc.perform(get(SettingsController.SETTINGS_PROFILE_URL))
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("account"))
                .andExpect(model().attributeExists("profile"));

        Account kimmy = accountRepository.findByNickname("kimmy");
        assertEquals(bio, kimmy.getBio());

    }

    @WithAccount("kimmy")
    @DisplayName("프로필 수정 - 입력값 정상")
    @Test
    void updateProfile() throws Exception {
        String bio = "소개를 수정합니다.";
        mockMvc.perform(post(SettingsController.SETTINGS_PROFILE_URL)
                .param("bio", bio)
                .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl(SettingsController.SETTINGS_PROFILE_URL))
                .andExpect(flash().attributeExists("message"));

        Account kimmy = accountRepository.findByNickname("kimmy");
        assertEquals(bio, kimmy.getBio());

    }

    @WithAccount("kimmy")
    @DisplayName("프로필 수정 - 입력값 오류")
    @Test
    void updateProfile_with_error() throws Exception {
        String bio = "소개를 수정합니다.소개를 수정합니다.소개를 수정합니다.소개를 수정합니다.소개를 수정합니다.소개를 수정합니다.소개를 수정합니다.소개를 수정합니다.소개를 수정합니다.소개를 수정합니다.소개를 수정합니다.소개를 수정합니다.소개를 수정합니다.소개를 수정합니다.소개를 수정합니다.";
        mockMvc.perform(post(SettingsController.SETTINGS_PROFILE_URL)
                .param("bio", bio)
                .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(view().name(SettingsController.SETTINGS_PROFILE_VIEW_NAME))
                .andExpect(model().attributeExists("profile"))
                .andExpect(model().attributeExists("account"))
                .andExpect(model().hasErrors());

        Account kimmy = accountRepository.findByNickname("kimmy");
        assertNull(kimmy.getBio());
    }

}