# 🏠 Catching (캐칭)
> 국민대학교 학생들을 위한 교내 맞춤형 매칭 서비스

---

## 📱 프로그램 소개

**Catching(캐칭)** 은 국민대학교 학생들을 위한 모바일 매칭 애플리케이션입니다.  
회원가입 시 입력한 생활 패턴 및 선호도를 기반으로 유사 성향의 상대와 연결해 주며,  
기숙사 룸메이트 매칭뿐만 아니라 팀 빌딩, 학업·상담 상대 탐색 등 다양한 학내 활동에서 활용할 수 있습니다.

---

## 🛠 개발 환경

| 항목 | 내용 |
|------|------|
| 개발 도구 | Android Studio |
| 개발 언어 | Java |
| 빌드 시스템 | Gradle + Android Gradle Plugin |
| compileSdk | 35 (Android 15) |
| targetSdk | 35 (Android 15) |
| minSdk | 24 (Android 7.0 Nougat 이상) |
| 주요 라이브러리 | Firebase Authentication, Firebase Firestore, Google Sign-In / Play Services |
| 테스트 환경 | Android 15 이상 실제 기기 및 에뮬레이터 |

---

## ⚙️ 주요 기능

### 🎬 첫 시작 화면
- `activity_main_start.xml` / `StartActivity.java`
- ObjectAnimator, AnimatorSet을 사용한 순차 애니메이션으로 로고 및 텍스트 등장
- 애니메이션 완료 후 로그인 화면으로 자동 전환

---

### 🔐 로그인 / 회원가입
- `LoginActivity.java` / `CreateProfileActivity.java`
- Google OAuth + Firebase Authentication 기반 로그인
- `@kookmin.ac.kr` 도메인 계정만 로그인 허용, 외부 계정은 팝업 안내 후 차단
- 신규 사용자 → 프로필 설문 화면(`CreateProfileActivity`)으로 이동
- 기존 사용자 → 홈 화면(`HomeActivity`)으로 바로 이동
- 로그인 상태는 `SharedPreferences`에 저장되어 앱 재실행 시 유지

---

### 🏡 홈 화면
- `HomeActivity.java` / `SchoolFragment.java` / `NoticeFragment.java`
- Jsoup을 이용한 국민대 학사공지 웹 크롤링
- 크롤링된 공지 제목 및 링크를 카드 형태로 표시

---

### 🔔 알림 탭
- `NotificationFragment.java` / `AlarmAdapter.java` / `AlarmItem.java`
- **받은 매칭**: 나에게 도착한 매칭 신청 확인 및 수락·거절 처리
- **보낸 매칭**: 내가 보낸 신청의 처리 결과 확인, 수락 시 상대 카카오톡 ID 복사 가능
- Firestore `SnapshotListener`를 통한 실시간 알림 갱신

---

### 💘 매칭 기능
- `RoommateFragment.java`
- 동일 성별·생활관 조건으로 후보군 필터링
- 청결도, 수면 패턴, 예민도 항목별 유사도 점수 계산 후 합산
- 상위 5명을 카드 형태로 추천, RecyclerView SnapHelper로 중앙 카드 강조

---

### 👤 마이 프로필
- `MyprofileFragment.java` / `MyprofilePagerAdapter.java`
- 로그인 사용자 정보 조회 및 표시
- 카카오톡 ID 수정 및 Firestore 저장
- ViewPager2 기반 3탭 구성

---

### ⚙️ 설정
- `SettingsFragment.java` / `LogoutDialogFragment.java` / `DeleteAccountDialogFragment.java`
- 인앱 알림 ON/OFF (SharedPreferences 저장)
- 로그아웃: Firestore 초기화 및 SharedPreferences 초기화 후 로그인 화면 이동
- 계정 삭제: 관련 매칭 데이터 및 사용자 문서 일괄 삭제 후 로그인 화면 이동

---

## ✅ 실행 필요 조건

- 📱 OS: Android 7.0 (Nougat) 이상 (권장: Android 10 ~ 15)
- 📧 Google 계정: `@kookmin.ac.kr` 도메인 필수
- 🌐 인터넷: Firebase 사용을 위한 안정적인 네트워크 연결 필요
- 💾 저장 공간: 200MB 이상
- 🔧 기타: Google Play Services 지원 기기, 2GB RAM 이상 권장

---

## 📥 설치 방법

### APK 직접 설치
1. 아래 노션 링크에서 APK 파일 다운로드  
   👉 [배포 테스트 파일 다운로드](https://www.notion.so/2bdef6a0746c809d8398ea45825b0ece)
2. 기기 설정에서 **'알 수 없는 출처의 앱 설치 허용'** 활성화
3. APK 파일 실행 후 설치

### OneStore 다운로드
1. OneStore 계정 로그인
2. **Catching** 앱 페이지에서 '다운로드' 버튼 클릭하여 설치

---

## 📌 사용 시 주의사항

- 반드시 `@kookmin.ac.kr` Google 계정으로 로그인해야 합니다.
- 최초 로그인 시 프로필 설문을 완료해야 홈 화면 및 매칭 기능을 이용할 수 있습니다.
- 설문은 최초 1회만 진행되며, 이후 수정은 **마이 프로필** 화면에서 가능합니다.
- 네트워크 연결이 불안정할 경우 로그인, 데이터 로딩, 매칭 요청 기능이 제한될 수 있습니다.

---

## 📬 문의처

궁금한 점이 있으시면 아래 이메일로 문의해 주세요!  
📧 ykk11@kookmin.ac.kr
