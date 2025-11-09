Patient Care App 🏥


Modern Android healthcare management system for patient registration, vitals tracking, and BMI-based health assessments.









🎯 Overview


The Patient Care App streamlines healthcare workflows for medical providers through a digital patient management system. The app follows clinical best practices with intelligent BMI-based routing, automatic health assessments, and comprehensive patient tracking.

Key Features

- 📋 Patient Registration - Digital patient onboarding with unique ID validation

- 📊 Vitals Tracking - Height, weight, and automatic BMI calculation

- 🔀 Smart Routing - BMI-based assessment routing (≤25: General, >25: Overweight)

- 📱 Modern UI - Material 3 design optimized for healthcare environments

- 💾 Offline-First - Local storage with API synchronization

- 📈 Dashboard - Patient listing with filtering and health status overview

🚀 Quick Start

Prerequisites

- Android Studio Flamingo+

- JDK 11 or higher

- Android SDK 34

- Minimum API level 24

Installation

1. 


Clone the repository



	git clone https://github.com/yourusername/patient-care-app.git
	cd patient-care-app



2. 
Open in Android Studio



	# Open the project in Android Studio
	open -a "Android Studio" .



3. 
Build and run



	./gradlew build
	./gradlew installDebug



⚠️ Critical Setup Required


The project currently has compilation issues that need to be fixed before running:


1. 
Create missing API interface - data/remote/PatientApiService.kt:



	interface PatientApiService {
	    @POST("user/signin")
	    suspend fun signin(@Body request: LoginRequest): Response<AuthResponse>
	    
	    @POST("patients/register") 
	    suspend fun registerPatient(@Body request: PatientRequest): Response<ApiResponse>
	    
	    @POST("vital/add")
	    suspend fun addVitals(@Body request: VitalsRequest): Response<ApiResponse>
	}



2. 
Add authentication implementation - Currently missing despite API requirements



3. 
Create missing data models - LoginRequest, AuthResponse, etc.



🏗️ Architecture


Built with modern Android development practices:


	UI Layer (Compose) → ViewModels (MVVM) → Repository → Room DB + Retrofit API

Tech Stack

- UI: Jetpack Compose + Material 3

- Architecture: MVVM with Repository pattern

- DI: Dagger Hilt

- Database: Room + SQLite

- Networking: Retrofit + Moshi

- Async: Kotlin Coroutines + Flow

📱 App Flow

1. Patient Registration → Enter patient details and unique ID

2. Vitals Collection → Record height, weight (auto-calculates BMI)

3. Health Assessment → BMI-based routing to appropriate form

4. Patient Dashboard → View all patients with health status

🛠️ Configuration

API Setup


Update the base URL in NetworkModule.kt:


	private const val BASE_URL = "https://patientvisitapis.intellisoftkenya.com/api/"

Database


Room database with the following entities:


- PatientEntity - Patient demographics

- VitalsEntity - Health measurements

- GeneralAssessmentEntity - For BMI ≤ 25

- OverweightAssessmentEntity - For BMI > 25

🧪 Testing


Currently no tests implemented. Run when available:


	./gradlew test                    # Unit tests
	./gradlew connectedAndroidTest    # Integration tests

🤝 Contributing


We welcome contributions! Here's how to get started:

Priority Issues to Fix

1. 🔥 Critical - Create missing PatientApiService interface

2. 🔥 Critical - Implement authentication flow

3. ⚠️ Important - Replace fragile touch overlay patterns

4. ⚠️ Important - Add comprehensive error handling

Development Setup

1. Fork the repository

2. Create a feature branch: git checkout -b feature/your-feature

3. Make your changes and add tests

4. Commit with clear messages: git commit -m "Add: patient validation"

5. Push and create a Pull Request

Code Style

- Follow Kotlin coding conventions

- Use meaningful commit messages

- Add KDoc comments for public APIs

- Ensure all tests pass

📋 Roadmap

-  Phase 1: Fix critical compilation issues

-  Phase 2: Complete authentication implementation

-  Phase 3: Add comprehensive testing suite

-  Phase 4: Implement data encryption for HIPAA compliance

-  Phase 5: Add offline sync capabilities

-  Phase 6: Performance optimization and CI/CD

🔒 Security & Privacy


Important: This is a development version and requires security hardening before production use:


- Patient data stored unencrypted locally

- No input sanitization implemented

- Missing authentication token management

- API keys hardcoded (not production-ready)

For production deployment, implement:


- Database encryption

- Input validation and sanitization

- Secure token storage

- Certificate pinning

- HIPAA compliance measures

📄 License


This project is licensed under the MIT License - see the LICENSE file for details.

📞 Support

- Issues: GitHub Issues

- Discussions: GitHub Discussions

- Documentation: See Technical Documentation

🙏 Acknowledgments

- Built following Android development best practices

- Material 3 design system implementation

- Healthcare workflow optimization based on clinical standards


---
⭐ If this project helps you, please consider giving it a star!
