# UI/UX Detail

## Navigation Flow
- Intro -> Settings
- Settings -> PhotoViewer
- PhotoViewer -> Settings
- Settings -> Google Photo Login

## Style
- Material Design 3
- Soft UI (Neumorphism)
- 버튼은 눌렸을 때 안으로 오목하게 들어가는(In-set) 애니메이션 효과
- 이콘은 Material Symbols Outlined 타입을 사용
- 너무 튀지 않는 차분한 강조색

## Detail of Each View
### IntroActivity
- 역할: 앱의 첫 화면이며 진입점
- 상세
  - 이 앱의 주요 기능을 그래픽과 함께 설명.
  - 이 앱은 사집 접근 권한이 필요하다는 설명

### SettingsActivity
- 역할: 앱의 기능 설정 화면
- 상세
  - 다음 사진이 변경 될 때 적용이 될 이펙트 리스트
  - 이펙트 적용 시간 설정
  - 사진 표시 시간 설정
  - 이펙트 적용 효과 미리보기 화면 표시
  - 구글 포토 연동 설정

### PhotoViewActivity
- 역할: 앱의 메인 기능 화면
- 상세
  - 설정된 사진을 표시
  - 설정에 적용된 효과 값에 맞게 동작