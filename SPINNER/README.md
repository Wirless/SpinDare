# 🎉 SPIN & DARE - Party Spinner App

A fun and interactive Android party app where players spin a wheel and land on one of 80 exciting challenges across 4 color categories. Perfect for high-energy parties and social gatherings!

## 🎯 Features

### 🎨 4 Challenge Categories (20 cards each)

#### 🔴 RED - Pocałunek/Zbliżenie (Kissing/Intimacy)
- 20 romantic and intimate challenges
- Examples: "Pocałuj osobę po lewej", "Zbliżenie z kimkolwiek w zasięgu ręki"

#### 🔵 BLUE - Przytulenie/Gest (Hugging/Gestures)
- 20 hugging and gesture challenges
- Examples: "Przytul 2 osoby jednocześnie", "Przytul i obróć 360 stopni"

#### 🟢 GREEN - Komplement/Śmieszna akcja (Compliments/Funny Actions)
- 20 compliment and silly action challenges
- Examples: "Udawaj zwierzę", "Powiedz najgłupszy żart jaki znasz"

#### 🟡 YELLOW - Taniec/Akcja absurdalna (Dancing/Absurd Actions)
- 20 dance and absurd action challenges
- Examples: "Zrób taniec zombie", "Zrób taniec na stole lub krześle"

### 🎮 Game Features
- **Interactive Spinner Wheel**: Beautiful 4-color wheel with smooth rotation animation
- **Random Challenge Selection**: Each spin selects from 80 unique challenges
- **Party Mode Effects**: Vibration, screen flashing, and visual feedback
- **Color-Coded Challenges**: Challenge display changes color based on selected category
- **Smooth Animations**: 3-second spinning animation with realistic physics

### 🎨 UI Features
- **Dark Party Theme**: Modern dark interface perfect for party environments
- **Responsive Design**: Works on all Android devices (API 21+)
- **Touch Controls**: Tap wheel or button to spin
- **Visual Feedback**: Color-coded challenge display with animations

## 🚀 Getting Started

### Prerequisites
- Android Studio Arctic Fox or later
- Android SDK 21+ (Android 5.0 Lollipop)
- Kotlin support enabled

### Installation

1. **Clone the repository**
   ```bash
   git clone <repository-url>
   cd SPIN-DARE
   ```

2. **Open in Android Studio**
   - Open Android Studio
   - Select "Open an existing Android Studio project"
   - Navigate to the project folder and select it

3. **Build and Run**
   - Connect an Android device or start an emulator
   - Click the "Run" button (green play icon)
   - Select your target device and click "OK"

### Building from Command Line

```bash
# Navigate to project directory
cd SPIN-DARE

# Build the project
./gradlew assembleDebug

# Install on connected device
./gradlew installDebug
```

## 🎯 How to Play

1. **Launch the App**: Open "SPIN & DARE" on your device
2. **Spin the Wheel**: Tap the "SPIN!" button or the wheel itself
3. **Watch the Animation**: Enjoy the 3-second spinning animation
4. **Complete the Challenge**: Read your assigned challenge and perform it!
5. **Repeat**: Keep spinning for more fun challenges

## 🛠️ Technical Details

### Architecture
- **MVVM Pattern**: Clean separation of concerns
- **View Binding**: Type-safe view access
- **Kotlin**: Modern Android development language

### Key Components
- **MainActivity**: Main game logic and UI management
- **ChallengeManager**: Manages all 80 challenges and game logic
- **Challenge Data Class**: Represents individual challenge cards
- **Custom Drawables**: Vector graphics for wheel and UI elements

### Dependencies
- **AndroidX Core**: Core Android functionality
- **Material Design**: Modern UI components
- **ConstraintLayout**: Flexible layout system
- **Lifecycle Components**: Lifecycle-aware components

### Permissions
- **VIBRATE**: For party mode haptic feedback

## 🎨 Customization

### Adding New Challenges
1. Edit `ChallengeManager.kt`
2. Add new challenge strings to the respective color arrays
3. Update the challenge count in comments

### Changing Colors
1. Edit `colors.xml` to modify challenge colors
2. Update `themes.xml` for overall app theme
3. Modify drawable files for custom graphics

### Modifying Animations
1. Edit `MainActivity.kt` spin logic
2. Adjust animation duration and easing
3. Customize party mode effects

## 📱 Device Compatibility

- **Minimum SDK**: API 21 (Android 5.0 Lollipop)
- **Target SDK**: API 34 (Android 14)
- **Screen Orientation**: Portrait only (optimized for party use)
- **Hardware Requirements**: Vibration support recommended

## 🎉 Party Mode Features

### High Energy (100dB Party Mode)
- **Vibration Patterns**: Multiple vibration sequences for excitement
- **Visual Effects**: Screen flashing and color changes
- **Smooth Animations**: Realistic wheel spinning physics
- **Color Coordination**: Challenge display matches selected category

### Social Interaction
- **Physical Actions**: Challenges encourage movement and interaction
- **Group Participation**: Multiple players can take turns
- **Fun Atmosphere**: Silly and entertaining challenge descriptions
- **No Overthinking**: Fast-paced, action-oriented gameplay

## 🐛 Troubleshooting

### Common Issues

**App crashes on launch**
- Ensure device has Android 5.0+ installed
- Check that all permissions are granted
- Verify device has sufficient memory

**Wheel doesn't spin**
- Ensure touch events are enabled
- Check that animation system is working
- Verify device supports ObjectAnimator

**No vibration**
- Check device vibration settings
- Ensure VIBRATE permission is granted
- Some devices may not support vibration

### Performance Optimization
- **Memory Management**: Efficient challenge data structures
- **Animation Smoothness**: Hardware acceleration enabled
- **Battery Optimization**: Minimal background processing

## 🤝 Contributing

1. Fork the repository
2. Create a feature branch (`git checkout -b feature/AmazingFeature`)
3. Commit your changes (`git commit -m 'Add some AmazingFeature'`)
4. Push to the branch (`git push origin feature/AmazingFeature`)
5. Open a Pull Request

## 📄 License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.

## 🙏 Acknowledgments

- **Android Team**: For the amazing platform
- **Kotlin Team**: For the modern language features
- **Material Design**: For the beautiful UI components
- **Party Enthusiasts**: For the inspiration and fun challenges!

## 📞 Support

For support, questions, or feature requests:
- Create an issue in the repository
- Contact the development team
- Check the troubleshooting section above

---

**🎉 Ready to SPIN & DARE? Let the party begin! 🎉**
