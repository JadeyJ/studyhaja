package com.studyhaja.modules.study;

import com.studyhaja.infra.AbstractContainerBaseTest;
import com.studyhaja.infra.MockMvcTest;
import com.studyhaja.modules.account.Account;
import com.studyhaja.modules.account.AccountFactory;
import com.studyhaja.modules.account.AccountRepository;
import com.studyhaja.modules.account.WithAccount;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.web.servlet.MockMvc;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@MockMvcTest
public class StudyControllerTest extends AbstractContainerBaseTest {

    @Autowired protected MockMvc mockMvc;
    @Autowired AccountFactory accountFactory;
    @Autowired StudyFactory studyFactory;
    @Autowired protected StudyService studyService;
    @Autowired protected StudyRepository studyRepository;
    @Autowired protected AccountRepository accountRepository;

    @AfterEach
    void afterEach() {
        accountRepository.deleteAll();
    }

    @Test
    @WithAccount("kimmy")
    @DisplayName("스터디 개설 폼 조회")
    void createStudyForm() throws Exception {
        mockMvc.perform(get("/new-study"))
                .andExpect(status().isOk())
                .andExpect(view().name("study/form"))
                .andExpect(model().attributeExists("account"))
                .andExpect(model().attributeExists("studyForm"));
    }

    @Test
    @WithAccount("kimmy")
    @DisplayName("스터디 개설 - 성공")
    void createStudy_success() throws Exception {
        mockMvc.perform(post("/new-study")
                .param("path", "test-path")
                .param("title", "study title")
                .param("shortDescription", "short description of a study")
                .param("fullDescription", "full description of a study")
                .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/study/test-path"));

        Study study = studyRepository.findByPath("test-path");
        assertNotNull(study);
        Account account = accountRepository.findByNickname("kimmy");
        assertTrue(study.getManagers().contains(account));
    }

    @Test
    @WithAccount("kimmy")
    @DisplayName("스터디 개설 - 실패")
    void createStudy_fail() throws Exception {
        mockMvc.perform(post("/new-study")
                .param("path", "wrong path")
                .param("title", "study title")
                .param("shortDescription", "short description of a study")
                .param("fullDescription", "full description of a study")
                .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(view().name("study/form"))
                .andExpect(model().hasErrors())
                .andExpect(model().attributeExists("studyForm"))
                .andExpect(model().attributeExists("account"));

        Study study = studyRepository.findByPath("test-path");
        assertNull(study);
    }

    @Test
    @WithAccount("kimmy")
    @DisplayName("스터디 조회")
    void viewStudy() throws Exception {
        Study study = new Study();
        study.setPath("test-path");
        study.setTitle("test study");
        study.setShortDescription("short description");
        study.setFullDescription("<p>full description</p>");

        Account kimmy = accountRepository.findByNickname("kimmy");
        studyService.createNewStudy(study, kimmy);

        mockMvc.perform(get("/study/test-path"))
                .andExpect(view().name("study/view"))
                .andExpect(model().attributeExists("account"))
                .andExpect(model().attributeExists("study"));
    }

    @Test
    @WithAccount("kimmy")
    @DisplayName("스터디 가입")
    void joinStudy() throws Exception{
        Account kim2 = accountFactory.createAccount("kim2");
        Study study = studyFactory.createStudy("test-study", kim2);

        mockMvc.perform(get("/study/" + study.getPath() + "/join"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/study/" + study.getEncodedPath() + "/members"));

        Account kimmy = accountRepository.findByNickname("kimmy");
        assertTrue(study.getMembers().contains(kimmy));
    }

    @Test
    @WithAccount("kimmy")
    @DisplayName("스터디 탈퇴")
    void leaveStudy() throws Exception{
        Account kim2 = accountFactory.createAccount("kim2");
        Study study = studyFactory.createStudy("test-study", kim2);

        Account kimmy = accountRepository.findByNickname("kimmy");
        studyService.addMember(study, kimmy);

        mockMvc.perform(get("/study/" + study.getPath() + "/leave"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/study/" + study.getEncodedPath() + "/members"));

        assertFalse(study.getMembers().contains(kimmy));
    }

}