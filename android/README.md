# IoT Logic Android Application

A comprehensive Android application for managing IoT devices using multiple communication protocols including Bluetooth LE, WiFi, USB Serial, and MQTT.

## 🚀 Features

- **Multi-Protocol Device Support**: Connect to IoT devices via BLE, WiFi, USB Serial, and MQTT
- **Real-time Telemetry**: Monitor device data with live charts and analytics
- **Offline Functionality**: Queue commands and sync data when connectivity is restored
- **Device Discovery**: Automatic scanning and QR code-based device provisioning
- **Push Notifications**: FCM-based alerts for device status and threshold violations
- **Geofencing**: Location-based device automation and tracking
- **Secure Authentication**: Biometric login and secure token management
- **Modern UI**: Material Design 3 with Jetpack Compose

## 📋 Requirements

- **Android API Level**: 21+ (Android 5.0)
- **Target SDK**: 34 (Android 14)
- **Kotlin**: 1.9.0+
- **Gradle**: 8.0+
- **Java**: 17

## 🏗️ Architecture

This application follows **Clean Architecture** principles with **MVVM** pattern:

```
┌─── UI Layer (Jetpack Compose)
│    ├── Screens
│    ├── ViewModels
│    └── Components
│
├─── Domain Layer
│    ├── Models
│    ├── Use Cases
│    └── Repositories (Interfaces)
│
└─── Data Layer
     ├── Repositories (Implementation)
     ├── Local Data Sources (Room)
     ├── Remote Data Sources (Retrofit)
     └── Hardware Managers
```

### Key Components

- **Hilt Dependency Injection**: Modular and testable architecture
- **Room Database**: Local data persistence with offline support
- **Retrofit**: REST API communication with automatic token refresh
- **WorkManager**: Background synchronization and periodic tasks
- **Jetpack Compose**: Modern, declarative UI framework
- **StateFlow & Coroutines**: Reactive programming and async operations

## 🛠️ Setup Instructions

### 1. Clone Repository

```bash
git clone https://github.com/your-org/iot-logic-android.git
cd iot-logic-android
```

### 2. Configure API Endpoints

Create `local.properties` file:

```properties
# API Configuration
API_BASE_URL="https://api.iotlogic.com/v1/"
API_TIMEOUT_SECONDS=30

# Firebase Configuration
FIREBASE_PROJECT_ID="your-firebase-project"

# Debug Configuration
DEBUG_LOGGING=true
```

### 3. Firebase Setup

1. Download `google-services.json` from Firebase Console
2. Place it in `app/` directory
3. Configure FCM for push notifications

### 4. Build and Run

```bash
# Debug build
./gradlew assembleDebug

# Run tests
./gradlew test

# Install on device
./gradlew installDebug
```

## 📱 Supported Devices

### Bluetooth LE Devices
- Temperature/Humidity Sensors
- Smart Lights and Switches
- Motion Detectors
- Environmental Monitors

### WiFi Devices
- Smart Home Controllers
- IP Cameras
- Environmental Stations
- Custom HTTP-based devices

### USB Serial Devices
- LoRa Communication Modules
- Arduino-based sensors
- Industrial monitoring equipment

### MQTT Devices
- Home automation systems
- Industrial IoT sensors
- Cloud-connected devices

## 🔧 Configuration

### Device Types

The app supports various device types with specific capabilities:

```kotlin
// Sensor devices (read-only)
Device(type = "sensor", capabilities = ["read_temperature", "read_humidity"])

// Actuator devices (controllable)
Device(type = "actuator", capabilities = ["turn_on", "turn_off", "set_brightness"])

// Hybrid devices (both sensor and actuator)
Device(type = "hybrid", capabilities = ["read_data", "send_command"])
```

### Protocol Configuration

#### Bluetooth LE
- Automatic service discovery
- Characteristic read/write/notify
- Connection management with retry logic

#### WiFi
- HTTP/HTTPS communication
- Custom endpoint configuration
- Authentication support

#### USB Serial
- Multiple baud rates (9600, 115200, etc.)
- Hardware flow control
- Custom protocol parsing

#### MQTT
- TLS/SSL support
- QoS levels 0, 1, 2
- Topic subscription management

## 🔐 Security Features

### Authentication
- Email/password login
- Biometric authentication (fingerprint, face)
- Automatic token refresh
- Secure token storage

### Data Protection
- Local database encryption
- API communication over HTTPS
- Secure credential storage using EncryptedSharedPreferences
- Certificate pinning for API calls

### Permissions
- Runtime permission handling
- Granular permission requests
- Permission rationale explanations

## 📊 Data Management

### Local Storage
- Room database for offline data
- Automatic data synchronization
- Command queuing for offline operations
- Telemetry data retention policies

### Cloud Synchronization
- Real-time data sync
- Conflict resolution
- Incremental sync for efficiency
- Background sync with WorkManager

## 🧪 Testing

### Unit Tests
```bash
./gradlew test
```

### Integration Tests
```bash
./gradlew connectedAndroidTest
```

### UI Tests
```bash
./gradlew connectedDebugAndroidTest
```

Test coverage includes:
- Repository layer testing
- ViewModel testing with MockK
- Database integration tests
- API integration tests
- Hardware manager mocking
- Complete UI flow testing

## 📈 Performance Optimization

### Memory Management
- Efficient image loading with Coil
- Lazy loading for large datasets
- Proper lifecycle management

### Battery Optimization
- Intelligent sync scheduling
- Foreground service for active connections
- Doze mode compatibility

### Network Efficiency
- Request caching with OkHttp
- Gzip compression
- Connection pooling

## 🚀 Deployment

### Build Variants

- **Debug**: Development builds with logging
- **Release**: Production builds with obfuscation
- **Staging**: Testing builds with staging API

### Signing Configuration

```gradle
android {
    signingConfigs {
        release {
            storeFile file('release-keystore.jks')
            storePassword project.findProperty('KEYSTORE_PASSWORD')
            keyAlias project.findProperty('KEY_ALIAS')
            keyPassword project.findProperty('KEY_PASSWORD')
        }
    }
}
```

### Release Process

1. Update version numbers
2. Generate signed APK/AAB
3. Upload to Google Play Console
4. Configure staged rollout

## 🔍 Troubleshooting

### Common Issues

#### Bluetooth Connection Problems
- Ensure location permissions are granted
- Check device compatibility
- Verify Bluetooth is enabled

#### WiFi Device Discovery
- Confirm devices are on same network
- Check firewall settings
- Verify device HTTP endpoints

#### Sync Issues
- Check network connectivity
- Verify API credentials
- Review sync logs in debug mode

### Debug Tools

- **Network Inspector**: Monitor API calls
- **Database Inspector**: View local data
- **Layout Inspector**: Debug UI issues
- **Memory Profiler**: Monitor memory usage

## 📖 Additional Documentation

- [API Integration Guide](docs/api-integration.md)
- [Hardware Integration Guide](docs/hardware-integration.md)
- [UI Component Library](docs/ui-components.md)
- [Testing Guide](docs/testing.md)
- [Contributing Guidelines](docs/contributing.md)

## 🤝 Contributing

1. Fork the repository
2. Create feature branch (`git checkout -b feature/amazing-feature`)
3. Commit changes (`git commit -m 'Add amazing feature'`)
4. Push to branch (`git push origin feature/amazing-feature`)
5. Open Pull Request

## 📄 License

This project is licensed under the MIT License - see [LICENSE](LICENSE) file for details.

## 🆘 Support

- **Documentation**: [docs/](docs/)
- **Issues**: [GitHub Issues](https://github.com/your-org/iot-logic-android/issues)
- **Discussions**: [GitHub Discussions](https://github.com/your-org/iot-logic-android/discussions)
- **Email**: support@iotlogic.com

## 🔄 Changelog

### Version 1.0.0 (Latest)
- Initial release
- Multi-protocol device support
- Real-time telemetry monitoring
- Offline functionality
- Push notifications
- Comprehensive testing suite

---

**Built with ❤️ using Kotlin and Jetpack Compose**