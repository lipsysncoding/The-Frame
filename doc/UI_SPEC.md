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
### 1. IntroActivity (App Entry Point)
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

### 2. SettingsActivity
- **Role:** The central hub for configuring the digital photo frame experience and managing Google Photos integration.
- **Design Intent:** Use organized "Comic-style Cards" to group related settings. Use pastel color accents to distinguish between different types of controls (e.g., toggles, sliders, and pickers).

- **Layout Structure:**
    1. **Top Section (Google Photos Integration Card):**
        - **Status Display:** A prominent card showing connection status (e.g., "Linked to [User Name]" or "Not Connected").
        - **Album Selection:** If connected, show a list of selected albums with thumbnail previews.
        - **Action Button:** A bold "Link/Unlink" button. Use a cloud icon with a thick outline.

    2. **Middle Section (Main Configuration List):**
        - **Transition Effects:** - A dropdown or scrollable list of effect names (e.g., Fade, Slide, Zoom).
            - Each item should have a small comic-style icon representing the motion.
        - **Timing Controls (Sliders):**
            - **Effect Duration:** How long the transition lasts.
            - **Photo Interval:** How long each photo stays on screen.
            - *Visual:* Use pastel-colored sliders with thick black borders for the thumb/handle.

    3. **Visual Preview Section (Real-time Feedback):**
        - **Preview Frame:** A mini comic-style photo frame showing a dummy image.
        - **Function:** When an effect or timing is changed, this preview immediately reflects the change so the user can see the result before applying.

    4. **Bottom Section:**
        - **Save/Apply Button:** A large "Apply Changes" button with a 'POW!' style visual feedback when clicked.

- **Interaction & Logic:**
    - **UDF Pattern:** Any change in the settings should update the `SettingsUiState` in the ViewModel.
    - **Instant Preview:** The preview frame must be reactive to the StateFlow values of the timing and effect selections.
    - **Error Handling:** If Google Photos fails to sync, show a comic-style alert bubble with an "Oops!" message.
      드디어 이 앱의 주인공인 PhotoViewActivity 스펙이군요! '코믹 파스텔' 컨셉을 유지하면서도, 사진 자체가 돋보여야 하는 화면이기에 몰입감과 직관적인 메뉴 노출이 핵심입니다.

### 3. PhotoViewActivity
- **Role:** The core functional screen that acts as the "Digital Photo Frame." It displays photos based on the user's settings.
- **Design Intent:** Full-screen immersion for the photos, with a "Comic-style Overlay Menu" that appears only when needed.

- **Layout Structure:**
    1. **Main Display Area:**
        - **Full-Screen Canvas:** Displays local or Google Photos images.
        - **Dynamic Transitions:** Photos transition according to the `EffectType`, `EffectDuration`, and `PhotoInterval` defined in `SettingsActivity`.
        - **Visual Style:** Apply a very subtle thick-border frame (like a comic panel) around the edge of the screen to maintain the theme.

    2. **Hidden Overlay Menu (Appears on Tap):**
        - **Background:** A semi-transparent pastel overlay (e.g., 50% opacity Soft Mint).
        - **Menu Buttons:**
            - **Settings Button:** A round comic-style button with a 'Gear' icon. Redirects to `SettingsActivity`.
            - **Exit Button:** A round button with an 'X' or 'Exit' icon. Closes the activity.
        - **Visual Feedback:** Buttons should pop in with a "Scale-up" animation when the screen is tapped.

- **Interaction & Logic:**
    - **Single Tap:** Toggles the visibility of the Overlay Menu.
    - **Auto-Hide:** The menu should automatically disappear after 5 seconds of inactivity.
    - **Effect Implementation:** Use Compose's `AnimatedContent` or `Crossfade` to implement the transition effects (Fade, Slide, Zoom) based on the settings data.
    - **State Management:** The ViewModel should observe the settings repository and update the playback logic in real-time.

- **Special Features:**
    - **Comic Bubble Toasts:** If a network error occurs while fetching Google Photos, show a small comic speech bubble at the bottom saying "Searching for memories..." or "Signal lost in the Multiverse!"