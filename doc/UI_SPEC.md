# UI/UX Style Guide: "Comic-Pastel" Theme

## 1. Overall Concept
- **Theme:** Energetic Comic Book Style mixed with Soft Pastel Tones.
- **Vibe:** Friendly, intuitive, playful, and easy to understand.
- **Visual Key:** Thick black outlines (Cell-shading style) combined with soft, bright colors.

## 2. Color Palette (Pastel Focus)
- **Primary:** Soft Mint (#B2F2BB) or Sky Blue (#A5D8FF)
- **Secondary:** Pale Pink (#FFD1DC) or Mellow Yellow (#FFF9C4)
- **Accent:** Playful Coral (#FF8B94) for Call-to-Action buttons.
- **Lines/Outlines:** Solid Black (#212529) - Used for borders to give a 'Comic' feel.
- **Background:** Very Light Grey or Off-White (#F8F9FA) to keep the pastel colors popping.

## 3. Typography (Comic Style)
- **Headlines:** Rounded, bold, and expressive fonts (e.g., 'Fredoka One' or 'Nunito' style).
- **Body:** Clean, highly readable sans-serif (e.g., 'Inter' or 'Roboto').
- **Emphasis:** Use uppercase or 'Bubble' backgrounds for important alerts/tips.

## 4. Components & Shapes
- **Borders:** All buttons and cards should have a 2dp ~ 3dp black outline.
- **Corners:** High rounded corners (16dp to 24dp) to avoid a sharp/rigid look.
- **Shadows:** Hard, offset shadows (No blur) to mimic comic book depth.
- **Icons:** Use thick-lined, hand-drawn style icons. Prefer colorful icons over monochrome ones.

## 5. Visual Communication
- **Imagery:** Actively use illustrations and expressive icons to explain features.
- **Empty States:** Instead of text, use cute characters or comic bubbles to guide the user.
- **Feedback:** Use "POW!", "DONE!", "SUCCESS!" style badges for action confirmations.

## 6. Implementation Notes (Jetpack Compose)
- Use `Modifier.border()` with `RoundedCornerShape`.
- Define a custom `ComicTheme` that wraps `MaterialTheme`.
- All Image vectors should be chosen/generated to match the 'thick-line' aesthetic.

## Navigation Flow
- Intro -> Settings
- Settings -> PhotoViewer
- PhotoViewer -> Settings
- Settings -> Google Photo Login

## Detail of Each View
## 1. IntroActivity (App Entry Point)
- **Role:** The first impression of the app. Introduces the "Digital Photo Frame" concept and secures necessary permissions.
- **Design Intent:** Use high-impact comic-style visuals to make the "Photo Frame" concept intuitive.

- **Layout Structure:**
    1. **Top Section (Brand Identity):**
        - **App Logo/Image:** A large, playful illustration of a comic-style photo frame.
        - **App Name:** Displayed in a bold, rounded "Headline" font with a thick outline.
        - **Tagline:** A short, catchy phrase explaining the digital frame feature.

    2. **Middle Section (Information & Consent):**
        - **Feature Explanation:** A brief, easy-to-read comic bubble or card explaining how the app turns the device into a photo frame using Google Photos and local storage.
        - **Permission Guide:** - Explicitly explain **why** Google Photos login and Local Storage access are required.
            - Use friendly icons (e.g., a folder icon and a cloud icon) to represent these permissions.
            - *Text Hint:* "To show your beautiful memories, we need to access your photo library!"

    3. **Bottom Section (Action):**
        - **"Get Started" Button:** A prominent, coral-colored button with a hard shadow.
        - **Action:** Clicking this button triggers the system permission request flow (Google Sign-in and Media Access).

- **Interaction:**
    - When the user clicks "Get Started," show the system permission dialogs sequentially.
    - Use a bouncy animation on the button to match the comic theme.

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