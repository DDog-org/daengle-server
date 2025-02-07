![image](https://github.com/user-attachments/assets/1571a693-841e-471b-8b45-4efba5601e26)

<div align=center>
  <h1> 🐶댕글 서비스의 핵심 키워드!🐶 </h2>
  https://www.daengle.com
  <br>
  <br>
  <strong>댕글</strong> ~~~~~~ 하는 서비스
  <br>


</div>

## 📦 멀티 모듈
### 🔌 Ports & Adapters Architecture
외부 라이브러리(JPA)로부터 도메인 객체와 비즈니스 로직 의존성을 역전 시켰습니다.
각 가능에서 테스트 및 모니터링을 통해 기술스텍이 변경되는 일이 잦았습니다. 이때, 어뎁터만 변경해줌으로써 유연한 확장이 가능했습니다.

<br />
<br />

![멀티모듈_7](https://github.com/user-attachments/assets/f7bb12e2-2462-4cfd-9092-4389dfe6a63f)

<br />
<br />

### 🚨 장애대응

외부 API를 사용하는 기능들에 타임아웃을 설정했습니다.
외부 API 서버의 장애가 우리 API서버로의 장애로 전파되는 것을 방지했습니다.
<br />
<br />

### 🚛 CI/CD 모듈별 자동 배포

api 모듈을 독립적으로 배포할 수 있도록 브랜치 전략을 활용했습니다. 
이로써 각 모듈을 빠르게 빌드하고 배포할 수 있었습니다.
단, 공통 라이브러리 모듈에 변경이 생길 때에는 모든 모듈을 재배포해야하기 때문에 이에 따른 워크플로우도 작성했습니다.
<br />
<br />

### 🖥️ 부하 테스트 및 성능 모니터링

처음에는 모놀리식 프로젝트였습니다. 하지만 알림 API, 결제 API가 굉장히 많이 요청되는 기능에 붙어있기 때문에 만약 이 의존된 외부 API 서버에 장애가 생겼을 때, 우리 서버의 전반적인 장애로 이어질 것 같았습니다.
해서, 의도적으로 타임아웃이 발동되도록 프로젝트를 세팅하고 부하테스트를 진행해보았습니다. 결과로 타임아웃에 묶여있는 스레드들로 인해 프로젝트 전반적인 장애가 생겼고, 멀티모듈로의 전환을 결심했습니다.
<br />
<br />

## 💬 DynamoDB + 실시간 채팅

<br />
<br />

## 🚀 SQL 튜닝을 통한 DB 최적화

<br />
<br />

## 💵 결제 시스템

<br />


![페이먼츠_플로우](https://github.com/user-attachments/assets/8144f1e0-22db-43d8-99eb-53144f8bca67)


</div>

<br />

## 🕵️ 금칙어 필터링

<br />

![필터링 플로우_ (1)](https://github.com/user-attachments/assets/46c01b77-6983-4199-8162-684d5febc838)

![Node Tree_ (1)](https://github.com/user-attachments/assets/2c1e47a1-87aa-4d63-9b37-7c757425af70)


<br />

## 🧰 기술 스택
![image](https://github.com/user-attachments/assets/1f16efb5-5d72-449c-b3fb-bbe83b2446d1)
<br />
<br />

## 🏗️ 시스템 아키텍처

![아키텍처이벤트](https://github.com/user-attachments/assets/8efcdec5-005f-4292-a705-f1c2957eced3)

<br />
<br />

## 🚢 CI/CD

![CICD_FLOW](https://github.com/user-attachments/assets/b1b341d9-c94e-42dc-be5a-a54e8f5b7d61)

<br />
<br />

## 🗃️ ERD 설계도
![image](https://github.com/user-attachments/assets/dc1f3e07-9a46-44bf-bab6-22daa21da6de)
![image](https://github.com/user-attachments/assets/92c07e93-3e63-4cc0-9c06-e1d9405d1ae8)


<br />
<br />

## 👥 팀원 소개
|                                              [🐈 진명인](https://github.com/myeonginjin)                                               |                                              [🐳 백효석](https://github.com/alexization)                                               |                                               [🌱 심지혜](https://github.com/sapientia1007)                                                |
| :-------------------------------------------------------------------------------------------------------------------------------------: | :-------------------------------------------------------------------------------------------------------------------------------: | :-------------------------------------------------------------------------------------------------------------------------------: |
| <a href="https://github.com/myeonginjin"> <img src="https://avatars.githubusercontent.com/myeonginjin" width=200px alt="_"/> </a> | <a href="https://github.com/alexization"> <img src="https://avatars.githubusercontent.com/alexization" width=200px alt="_"/> </a> | <a href="https://github.com/sapientia1007"> <img src="https://avatars.githubusercontent.com/sapientia1007" width=200px alt="_"/> </a> |
|                                                               백엔드                                                                |                                                            백엔드                                                             |                                                              백엔드                                                               |
|                                                 팀원들이 보는 명인은                                                                    |                                                       팀원들이 보는 효석은                                                        |                                                         팀원들이 보는 지혜는                                                        |
| 🕵️‍부지런한 해결사 <br/> 👩‍💻개발이 제일 좋아, 찐 개발자 <br/> 🍜밥 잘 먹고 코드 맛있게 짜는 사람 <br/> 🚗맡은 일은 끝까지 간다! 진격의 개발자|🤩분위기 메이커<br/>😁항상 웃긴 재밌는 사람<br/>🏃‍매일같이 문열고 문닫는 성실왕<br/>👨‍🏫사소한 디테일 놓치지 않는 꼼꼼왕|🥳 언제나 맑은 긍정왕 <br />🔫 듬직한 트러블 슈터 <br />🤩항상 밝은 분위기 메이커 <br />🎯 버그 꼼짝마! 백발백중 버그 퇴치|

<br />
<br />

## 🔨 작업 및 역할 분담
<div align=center>
  
| 팀원  | 사진 | 역할 |
|-----------------|-----------------|-----------------|
| 진명인   |  <img src="https://avatars.githubusercontent.com/myeonginjin" alt="진명인" width="100"> | <ul><li>인프라 구축</li><li>페이먼츠 시스템</li><li>금칙어 필터링 시스템</li></ul>     |
| 백효석   |  <img src="https://avatars.githubusercontent.com/alexization" alt="백효석" width="100">| <ul><li>인증/인가 시스템</li><li>견적 입찰 시스템</li> |
| 심지혜   |  <img src="https://avatars.githubusercontent.com/sapientia1007" alt="심지혜" width="100">    |<ul><li>실시간 채팅 시스템</li><li>실시간 화상 시스템</li></ul>  |

</div>
<br/> 
<br/>
