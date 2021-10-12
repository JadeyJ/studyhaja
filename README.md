# 스터디 커뮤니티 서비스

자신이 관심있는 주제를 기반으로 지역별 스터디 모임을 운영하는 프로젝트입니다.

- 스터디를 생성하면 관리자로 지정되며 관리자에게는 모임 생성 권한이 주어집니다.
- 관심주제와 관심지역을 검색하여 현재 오픈되어 있는 스터디를 검색할 수 있습니다.
- 모집 방법은 '선착순'과 '관리자 수락' 두가지로, '관리자 수락' 방식의 경우 신청자는 참가 신청을 누른 후 관리자의 승인을 받아 참가를 확정합니다.
- 관심 있거나 신청 대기, 신청 완료인 스터디, 모임의 변경사항은 이메일 알림, 웹 알림으로 보내지며, 두 가지 중 원하는 수신 방법을 선택할 수 있습니다.

---

## 1. 로그인
![로그인](https://user-images.githubusercontent.com/62224973/136950146-2276d448-8202-46d6-8d94-5fd741d145d1.png)

## 2. 프로필
> 계정 정보를 수정하고 프로필 이미지를 등록합니다.

![프로필](https://user-images.githubusercontent.com/62224973/136949821-a1e50a4e-480e-46cb-908d-39412c978ab2.png)

## 3. 홈
> 진행중인 스터디 모임에 대한 정보를 확인합니다.

![메인페이지](https://user-images.githubusercontent.com/62224973/136949819-f414c878-9b37-403a-aeb2-b42bae4f0c58.png)
- 프로필에 설정한 <u>관심 스터디 주제</u>, <u>주요 활동 지역</u> 리스트
- <u>최근 신청한 스터디 모임</u>과 <u>관심 지역의 관심 주제 스터디</u> 리스트
- <u>참여중인 스터디</u> 리스트
- [관리자] <u>관리중인 스터디</u> 리스트

## 4. 스터디 검색
> 관심있는 지역 또는 주제를 검색합니다.

![관심 주제 검색](https://user-images.githubusercontent.com/62224973/136949814-ee32236d-13c3-4535-86cf-3f2a5ffa018a.png)
![지역 검색](https://user-images.githubusercontent.com/62224973/136949804-de21ebe5-bfd9-4909-9cf5-e4724cc12426.png)

## 5. 스터디 정보
![스터디 소개](https://user-images.githubusercontent.com/62224973/136954906-95667c45-a6fd-4ed0-bca7-9224a36d24a4.png)
![스터디 구성원](https://user-images.githubusercontent.com/62224973/136956026-213be0a0-63cd-4a67-b3f3-b64a91b8dbfa.png)
![스터디 모임](https://user-images.githubusercontent.com/62224973/136954899-7c44c38d-2ab4-43be-ae0d-9bed31fbca7b.png)

## 6. 모임
![모임](https://user-images.githubusercontent.com/62224973/136954877-189d05d5-4a17-4fb1-8812-06a27a6ceaf3.png)

---

# Project Structure
> Spring Boot(Server) + Thymeleaf(Template Engine) 구성
> 
- [x] Spring Boot
- [x] Spring Security
- [x] Spring Boot Validation
- [x] PostgreSQL (RDB)
- [x] JPA & QueryDSL (ORM)
- [x] JUnit (Test)
- [x] Thymeleaf / thymeleaf-extra-springsecurity5
- [x] Bootstrap 4.x
- [ ] AWS (Infra)
- [ ] Nginx (Reverse Proxy Server)
- [ ] Jenkins & Codedeploy (CI/CD)


## Spring Boot
```
infra
└───config
└───mail
modules
└───account
└───event
└───main
└───notification
└───study
└───tag
└───zone
```
- 엔티티 관계도

![엔티티 관계도](https://user-images.githubusercontent.com/62224973/136949795-4f3c797a-5565-49a1-8dde-3aaae1af14fb.png)
<br>

## Spring Security
- Security 설정을 추가해 허용된 사용자만 특정 url에 접근할 수 있도록 제한합니다.
<br>

## JPA & QueryDSL (ORM)
- 객체 중심 domain 설계 및 반복적인 CRUD 작업을 대체해 비즈니스 로직에 집중합니다.

JPA : 반복적인 CRUD 작업을 대체해 간단히 DB에서 데이터를 조회
QueryDSL : Join & Projections 등 JPA로 해결할 수 없는 SQL은 QueryDSL로 작성
<br>

[study]
- Study (Domain Class)
- StudyRepository (JPA Interface)
- StudyRepositoryExtension (QueryDSL Interface)
- StudyRepositoryExtensionImpl (QueryDSL-implemented Class)
<br>

## JUnit (Test)
- 임의로 로그인 된 사용자로 테스트 하기 위하여 @WithMockUser 어노테이션을 사용합니다.
- 실제 DB에 저장되어 있는 정보에 대응하는 인증된 Authentication이 필요한데 이는 @WithMockUser 로는 처리할 수 없기 때문에 SecurityContextFactory를 이용하여 <u>인증된 사용자를 제공할 커스텀 어노테이션</u>을 만들었습니다.
