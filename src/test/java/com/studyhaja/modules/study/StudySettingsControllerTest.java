package com.studyhaja.modules.study;

import com.studyhaja.infra.AbstractContainerBaseTest;
import com.studyhaja.infra.MockMvcTest;
import com.studyhaja.modules.account.Account;
import com.studyhaja.modules.account.AccountFactory;
import com.studyhaja.modules.account.AccountRepository;
import com.studyhaja.modules.account.WithAccount;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@MockMvcTest
class StudySettingsControllerTest extends AbstractContainerBaseTest {

    @Autowired MockMvc mockMvc;
    @Autowired StudyFactory studyFactory;
    @Autowired AccountFactory accountFactory;
    @Autowired AccountRepository accountRepository;
    @Autowired StudyRepository studyRepository;

    @Test
    @WithAccount("kimmy")
    @DisplayName("스터디 소개 수정 폼 조회 - 실패 (권한 없는 유저)")
    void updateDescriptionForm_fail() throws Exception {
        Account kimmy2 = accountFactory.createAccount("kimmy2");
        Study study = studyFactory.createStudy("test-study", kimmy2);

        mockMvc.perform(get("/study/" + study.getPath() + "/settings/description"))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithAccount("kimmy")
    @DisplayName("스터디 소개 수정 폼 조회 - 성공")
    void updateDescriptionForm_success() throws Exception {
        Account kimmy = accountRepository.findByNickname("kimmy");
        Study study = studyFactory.createStudy("test-study", kimmy);

        mockMvc.perform(get("/study/" + study.getPath() + "/settings/description"))
                .andExpect(status().isOk())
                .andExpect(view().name("/study/settings/description"))
                .andExpect(model().attributeExists("studyDescriptionForm"))
                .andExpect(model().attributeExists("account"))
                .andExpect(model().attributeExists("study"));
    }

    @Test
    @WithAccount("kimmy")
    @DisplayName("스터디 소개 수정 - 성공")
    void updateDescription_success() throws Exception {
        Account kimmy = accountRepository.findByNickname("kimmy");
        Study study = studyFactory.createStudy("test-study", kimmy);

        String settingsDescriptionUrl = "/study/" + study.getPath() + "/settings/description";
        mockMvc.perform(post(settingsDescriptionUrl)
                .param("shortDescription", "short description")
                .param("fullDescription", "full description")
                .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl(settingsDescriptionUrl))
                .andExpect(flash().attributeExists("message"));
    }

    @Test
    @WithAccount("kimmy")
    @DisplayName("스터디 소개 수정 - 실패")
    void updateDescription_fail() throws Exception {
        Account kimmy = accountRepository.findByNickname("kimmy");
        Study study = studyFactory.createStudy("test-study", kimmy);

        String settingsDescriptionUrl = "/study/" + study.getPath() + "/settings/description";
        mockMvc.perform(post(settingsDescriptionUrl)
                .param("shortDescription", "")
                .param("fullDescription", "full description")
                .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(model().hasErrors())
                .andExpect(model().attributeExists("studyDescriptionForm"))
                .andExpect(model().attributeExists("study"))
                .andExpect(model().attributeExists("account"));
    }
    
}