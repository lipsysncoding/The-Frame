# App Development & Architecture Guidelines

## 기본 설계 원칙 (Design Principles)
1. Separation of Concerns (관심사 분리)
    - UI 로직, 비즈니스 로직, AI 통신 로직을 엄격히 분리합니다.

2. Reactivity 
   - AI 응답은 스트리밍(Streaming)인 경우가 많으므로 Flow 또는 LiveData를 통해 실시간으로 UI에 반영합니다.

3. Resilience (회복력)
   - API 제한(Rate Limit), 네트워크 끊김, 잘못된 AI 응답(Hallucination)에 대한 예외 처리를 필수적으로 설계합니다.

4. Security
   - API Key는 절대 클라이언트 코드에 하드코딩하지 않으며, local.properties나 서버 프록시를 통해 관리합니다.

## Develop Principles
1. 작은 단위로 코드를 생성하고 빌드.
2. 빌드 오류 발생시 오류 수정 후 재빌드.
3. 테스트 코드 작성하여 테스트 수행.
4. 점진적으로 코드 생성 후 빌드와 테스트를 수행.

## 앱 구조
- 안드로이드 개발자 사이트의 앱 아키텍츠 가이드라인을 따름. 
  - https://developer.android.com/topic/architecture/intro?_gl=1*1fzy5y0*_up*MQ..*_ga*MTg4MjE3NDIzNC4xNzY5MTYwMTg3*_ga_6HH9YJMN9M*czE3NjkxNjAxODckbzEkZzAkdDE3NjkxNjAyNDIkajUkbDAkaDg3NTA1OTE0Mw..
- UI
  - com.lib.syncoding.the.frame.ui 패키지에 생성.
  - 각 UI는 재활용이 가능하고 일관성 유지를 위해 컴포넌트화 하여 생성.
  - 필요하다면 컴포넌트 분류 등을 위해 하위 패키지로 분류. 

- ViewModel
  - com.lib.syncoding.the.fram.viewmodel 패키지에 생성

- UseCase
  - com.lib.syncoding.the.frame.usecase 패키지에 생성

## Tech Stack
- Language: Kotlin (Coroutines + Flow)
- Local DB: Room
- UI: Compose
- Android 개발자 사이트를 참고
  - https://developer.android.com/

