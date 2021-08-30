package com.studyhaja.settings;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.studyhaja.WithAccount;
import com.studyhaja.account.AccountRepository;
import com.studyhaja.account.AccountService;
import com.studyhaja.domain.Account;
import com.studyhaja.domain.Tag;
import com.studyhaja.settings.form.TagForm;
import com.studyhaja.tag.TagRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import static com.studyhaja.settings.SettingsController.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@Transactional
@SpringBootTest
@AutoConfigureMockMvc
class SettingsControllerTest {

    @Autowired MockMvc mockMvc;
    @Autowired AccountRepository accountRepository;
    @Autowired AccountService accountService;
    @Autowired PasswordEncoder passwordEncoder;
    @Autowired ObjectMapper objectMapper;
    @Autowired TagRepository tagRepository;

    @AfterEach
    void afterEach() {
        accountRepository.deleteAll();
    }

    @WithAccount("kimmy")
    @DisplayName("태그 수정 폼")
    @Test
    void updateTagsForm() throws Exception {
        mockMvc.perform(get(ROOT + SETTINGS + TAGS))
                .andExpect(status().isOk())
                .andExpect(view().name(SETTINGS + TAGS))
                .andExpect(model().attributeExists("account"))
                .andExpect(model().attributeExists("whitelist"))
                .andExpect(model().attributeExists("tags"));
    }


    @WithAccount("kimmy")
    @DisplayName("계정에 태그 추가")
    @Test
    void addTag() throws Exception {

        TagForm tagForm = new TagForm();
        tagForm.setTagTitle("newTag");

        mockMvc.perform(post(ROOT + SETTINGS + TAGS + "/add")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(tagForm))
                .with(csrf()))
                .andExpect(status().isOk());

        Tag newTag = tagRepository.findByTitle("newTag");
        assertNotNull(newTag);
        accountRepository.findByNickname("kimmy").getTags().contains(newTag);
    }

    @WithAccount("kimmy")
    @DisplayName("계정에서 태그 삭제")
    @Test
    void removeTag() throws Exception{
        Account kimmy = accountRepository.findByNickname("kimmy");
        Tag newTag = tagRepository.save(Tag.builder().title("newTag").build());
        accountService.addTag(kimmy, newTag);

        assertTrue(kimmy.getTags().contains(newTag));

        TagForm tagForm = new TagForm();
        tagForm.setTagTitle("newTag");

        mockMvc.perform(post(ROOT + SETTINGS + TAGS + "/remove")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(tagForm))
                .with(csrf()))
                .andExpect(status().isOk());

        assertFalse(kimmy.getTags().contains(newTag));
    }

    @WithAccount("kimmy")
    @DisplayName("닉네임 수정 폼")
    @Test
    void updateAccountForm() throws Exception {
        mockMvc.perform(get(ROOT + SETTINGS + ACCOUNT))
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("account"))
                .andExpect(model().attributeExists("nicknameForm"));
    }

    @WithAccount("kimmy")
    @DisplayName("닉네임 수정하기 - 입력값 정상")
    @Test
    void updateAccount_success() throws Exception {
        String newNickname = "jadey";
        mockMvc.perform(post(ROOT + SETTINGS + ACCOUNT)
                .param("nickname", newNickname)
                .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl(ROOT + SETTINGS + ACCOUNT))
                .andExpect(flash().attributeExists("message"));

        assertNotNull(accountRepository.findByNickname("jadey"));
    }

    @WithAccount("kimmy")
    @DisplayName("닉네임 수정 - 입력값 오류")
    @Test
    void updateAccount_with_error() throws Exception {
        String wrong_nickname = "/;-**/";
        mockMvc.perform(post(ROOT + SETTINGS + ACCOUNT)
                .param("nickname", wrong_nickname)
                .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(view().name(SETTINGS + ACCOUNT))
                .andExpect(model().hasErrors())
                .andExpect(model().attributeExists("account"))
                .andExpect(model().attributeExists("nicknameForm"));
    }

    @WithAccount("kimmy")
    @DisplayName("프로필 수정 폼")
    @Test
    void updateProfileForm() throws Exception {
        String bio = "소개를 수정합니다.";
        mockMvc.perform(get(ROOT + SETTINGS + PROFILE))
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("account"))
                .andExpect(model().attributeExists("profile"));
    }

    @WithAccount("kimmy")
    @DisplayName("프로필 수정 - 입력값 정상")
    @Test
    void updateProfile() throws Exception {
        String bio = "소개를 수정합니다.";
        mockMvc.perform(post(ROOT + SETTINGS + PROFILE)
                .param("bio", bio)
                .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl(ROOT + SETTINGS + PROFILE))
                .andExpect(flash().attributeExists("message"));

        Account kimmy = accountRepository.findByNickname("kimmy");
        assertEquals(bio, kimmy.getBio());

    }

    @WithAccount("kimmy")
    @DisplayName("프로필 수정 - 입력값 오류")
    @Test
    void updateProfile_with_error() throws Exception {
        String bio = "소개를 수정합니다.소개를 수정합니다.소개를 수정합니다.소개를 수정합니다.소개를 수정합니다.소개를 수정합니다.소개를 수정합니다.소개를 수정합니다.소개를 수정합니다.소개를 수정합니다.소개를 수정합니다.소개를 수정합니다.소개를 수정합니다.소개를 수정합니다.소개를 수정합니다.";
        mockMvc.perform(post(ROOT + SETTINGS + PROFILE)
                .param("bio", bio)
                .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(view().name(SETTINGS + PROFILE))
                .andExpect(model().attributeExists("profile"))
                .andExpect(model().attributeExists("account"))
                .andExpect(model().hasErrors());

        Account kimmy = accountRepository.findByNickname("kimmy");
        assertNull(kimmy.getBio());
    }

    @WithAccount("kimmy")
    @DisplayName("비밀번호 수정 폼")
    @Test
    void updatePasswordForm() throws Exception {
        mockMvc.perform(get(ROOT + SETTINGS + PASSWORD))
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("account"))
                .andExpect(model().attributeExists("passwordForm"));
    }

    @WithAccount("kimmy")
    @DisplayName("비밀번호 수정 - 입력값 정상")
    @Test
    void updatePassword() throws Exception {
        mockMvc.perform(post(ROOT + SETTINGS + PASSWORD)
                .param("newPassword", "12345678")
                .param("newPasswordConfirm", "12345678")
                .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl(ROOT + SETTINGS + PASSWORD))
                .andExpect(flash().attributeExists("message"));

        Account kimmy = accountRepository.findByNickname("kimmy");
        assertTrue(passwordEncoder.matches("12345678", kimmy.getPassword()));
    }

    @WithAccount("kimmy")
    @DisplayName("비밀번호 수정 - 입력값 오류")
    @Test
    void updatePassword_with_error() throws Exception {
        mockMvc.perform(post(ROOT + SETTINGS + PASSWORD)
                .param("newPassword", "12345678")
                .param("newPasswordConfirm", "123456789")
                .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(view().name(SETTINGS + PASSWORD))
                .andExpect(model().attributeExists("passwordForm"))
                .andExpect(model().attributeExists("account"))
                .andExpect(model().hasErrors());
    }

}