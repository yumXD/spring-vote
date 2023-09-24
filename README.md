# 🚀 선거 관리 시스템 🗳️

## 🔥 개요

우리의 선거 관리 시스템은 선거의 투명성과 효율성을 향상시키기 위한 모던한 솔루션입니다. Spring MVC와 MySQL을 기반으로 한 이 시스템은 선거 시작, 후보자 관리, 투표 내역 추적 및 사용자 관리와 같은 주요 기능들을 탑재하고 있습니다.

## 🎉 주요 특징

1. **통합 관리 대시보드** - 선거의 전반적인 관리와 모니터링을 위한 직관적인 대시보드.
2. **투명한 투표 시스템** - 사용자 별 투표 내역 추적 및 실시간 업데이트 기능.
3. **유연한 후보자 관리** - 후보자 등록, 수정, 삭제를 간편하게!
4. **안전한 사용자 인증** - 회원 정보 보호 및 안전한 인증 절차.

## 😋 프로그램 실행 방법

### 1. Mysql 환경설정

#### application.properties

```
#MySQL 연결 설정
spring.datasource.driver-class-name=com.mysql.cj.jdbc.Driver
spring.datasource.url=jdbc:mysql://localhost:3306/vote?serverTimezone=UTC
spring.datasource.username=root
spring.datasource.password=1234
```

#### Mysql 프로그램 내 SQL문 입력

```
create database vote default character set utf8 collate utf8_general_ci;
```

## 📚 엔티티와 연관관계

### 1. Election (선거)

선거에 관한 기본 정보를 포함하며, 여러 후보자와 투표 내역을 관리합니다.

- **Candidate**와 One-to-Many 연관관계
- **Votes**와 One-to-Many 연관관계
- **ElectionStart**와 One-to-One 연관관계

### 2. Candidate (후보자)

선거에 참여하는 후보자에 대한 정보를 포함합니다.

- **Election**과 Many-to-One 연관관계
- **Votes**와 One-to-Many 연관관계

### 3. Votes (투표 내역)

사용자의 투표 내역을 저장합니다.

- **Member**와 Many-to-One 연관관계
- **Candidate**와 Many-to-One 연관관계
- **Election**과 Many-to-One 연관관계

### 4. Member (회원)

사용자 정보를 포함합니다.

- **Votes**와 One-to-Many 연관관계

### 5. ElectionStart (투표 시작 및 제한시간)

특정 선거의 시작과 종료 시간 정보를 포함합니다.

- **Election**과 One-to-One 연관관계

## 🔧 기술 스택

### 개발 환경 & 도구

- **IDE:** IntelliJ IDEA
- **VCS:** Git

### 언어 & 프레임워크

- **Language:** Java 17
- **Framework:** Spring Boot 3.1.4

### 데이터베이스

- **RDBMS:** MySQL

### 기타 라이브러리 & 도구

- **ORM:** JPA, Hibernate
- **Build Tool:** Gradle
- **Backend**: Spring MVC
- **Frontend**: Thymeleaf, Bootstrap

## 💡 비전

우리는 민주주의의 핵심 가치를 고려하여 이 시스템을 개발하였습니다. 선거의 모든 단계에서 투명성을 보장하며, 사용자와 후보자 모두에게 공정한 환경을 제공하는 것이 우리의 목표입니다.

## 🤝 기여 및 참여

이 프로젝트는 오픈소스로 운영되며, 모든 기여와 피드백을 환영합니다! 이슈 제출, 풀 리퀘스트 및 코드 리뷰를 통해 함께 더 나은 선거 관리 시스템을 만들어나갑시다.
