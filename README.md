# Enterprise Test Automation Framework for OrangeHRM

[![Java](https://img.shields.io/badge/Java-25-orange.svg)](https://www.oracle.com/java/)
[![Selenium](https://img.shields.io/badge/Selenium-4.28.1-green.svg)](https://www.selenium.dev/)
[![TestNG](https://img.shields.io/badge/TestNG-7.10.2-blue.svg)](https://testng.org/)
[![Rest-Assured](https://img.shields.io/badge/Rest--Assured-5.5.1-red.svg)](https://rest-assured.io/)
[![Maven](https://img.shields.io/badge/Maven-3.x-C71A36.svg)](https://maven.apache.org/)
[![ExtentReports](https://img.shields.io/badge/Reports-Extent%20&%20Allure-yellow.svg)](http://extentreports.com/)

A comprehensive, production-grade test automation framework developed for the **OrangeHRM 4.0 Enterprise/Open Source Human Resource Management System**. The framework implements **Page Object Model (POM)** architecture, **Data-Driven Testing (DDT)** with Apache POI, **REST API Testing** with REST Assured, automated failure screenshots, automatic test retry, and multi-channel HTML reporting.

---

## Table of Contents
1. [Project Objectives](#project-objectives)
2. [Key Features & Capabilities](#key-features--capabilities)
3. [Technology Stack](#technology-stack)
4. [Project Architecture & Package Structure](#project-architecture--package-structure)
5. [Test Coverage by Module](#test-coverage-by-module)
6. [Prerequisites & Environment Setup](#prerequisites--environment-setup)
7. [OrangeHRM & XAMPP Configuration](#orangehrm--xampp-configuration)
8. [Running Tests](#running-tests)
9. [Test Reports & Artifacts](#test-reports--artifacts)
10. [Framework Design Highlights](#framework-design-highlights)
11. [Framework Screenshots & Execution Artifacts](#framework-screenshots--execution-artifacts)
12. [Known Limitations & Current Status](#known-limitations--current-status)

---

## Project Objectives

- **End-to-End Verification**: Automate regression testing for critical OrangeHRM 4.0 modules (Login, Admin, PIM, Recruitment, Performance, and My Info).
- **Maintainability & Modularity**: Apply Page Object Model (POM) pattern to isolate UI changes from test logic across 11 page classes.
- **Data-Driven Architecture**: Decouple test logic from input datasets using Apache POI with externalized Excel sheets (`TestData.xlsx`).
- **Hybrid Test Layering**: Complement end-to-end browser workflows (Selenium 4) with lightweight API validation (REST Assured) for authentication and status checks.
- **Observability & Diagnostics**: Integrate automated failure screenshot capture, Log4j logging, ExtentReports, and Allure lifecycle reports.
- **Modern Compatibility**: Ensure clean builds and execution on modern enterprise runtimes (**Java 25 LTS**, Selenium 4.28.1, TestNG 7.10.2, Maven 3.9+).

---

## Key Features & Capabilities

- **Page Object Model (POM)**: Strict separation between web element locators, page actions, and test assertions across 11 Page Object classes.
- **Hybrid Test Automation**:
  - **UI Automation**: End-to-end browser automation covering all core HRM workflows with Selenium WebDriver 4.
  - **REST API Testing**: Direct HTTP/REST testing of authentication, session management, and endpoint security using REST Assured.
- **Data-Driven Testing (DDT)**: Externalized test datasets maintained in `TestData.xlsx`, consumed seamlessly via TestNG `@DataProvider` and Apache POI.
- **Self-Healing / Resilient Locators**: Robust XPath and CSS strategies with dynamic `WebDriverWait` and `ExpectedConditions` avoiding synchronization flakiness.
- **Automatic Failure Recovery & Retry**: Custom `IRetryAnalyzer` and `IAnnotationTransformer` automatically retry transient test failures.
- **Automated Failure Screenshots**: Real-time screenshot capture on test failure integrated directly into TestNG listeners and saved to disk.
- **Rich Reporting**: Extent HTML Reports, TestNG HTML/XML reports, Surefire JUnit XML, and Allure test reports.
- **Configuration Management**: Environment-specific settings managed via `config.properties` and local overrides via `config.local.properties`.

---

## Technology Stack

| Component | Technology | Version | Description |
|:---|:---|:---|:---|
| **Programming Language** | Java (OpenJDK) | **25** | Core test framework implementation |
| **Web Browser Automation** | Selenium WebDriver | **4.28.1** | Browser automation & Selenium Manager driver management |
| **Test Runner & Assertions** | TestNG | **7.10.2** | Test execution engine, data providers, listeners, assertions |
| **API Testing** | REST Assured | **5.5.1** | HTTP API testing, status codes, cookies, session validation |
| **Data-Driven Engine** | Apache POI / POI-OOXML | **5.4.0** | Read and parse `.xlsx` test data files |
| **Build & Dependency Tool** | Apache Maven | **3.x** | Build lifecycle management, surefire test execution |
| **Reporting Framework** | ExtentReports | **2.41.2** | Rich interactive HTML executive test dashboard |
| **Advanced Reporting** | Allure TestNG | **2.29.1** | Test execution lifecycle diagrams and reporting |
| **Logging** | Apache Log4j 2 + 1.2-bridge | **2.24.3** | Structured runtime logging to console and log files |
| **Target Application** | OrangeHRM Open Source | **4.0** | Local web application under test hosted via XAMPP |
| **Local Web Server** | XAMPP | **8.x** | Apache HTTP Server + MariaDB / MySQL |

---

## Project Architecture & Package Structure

```
OrangeHRM_UIAutomationFrameWork/
├── pom.xml                               # Maven project object model & dependencies
├── config.properties                     # Default framework & application configuration
├── log4j.xml                             # Log4j configuration
├── testng.xml                            # Master TestNG suite (All UI & API modules)
├── testng_api.xml                        # REST Assured API test suite
├── testng_AdminTab.xml                   # Admin module suite
├── testng_PIMTab.xml                     # PIM module suite
├── testng_recruitmenttab.xml             # Recruitment module suite
├── testng_myInfoTest.xml                 # My Info module suite
├── testng_all_hrms.xml                   # Extended complete suite
│
├── src/main/java/com/hrm/
│   ├── base/
│   │   └── TestBase.java                 # Base class: driver lifecycle, waits, screenshots
│   ├── pages/                            # Page Object Model classes
│   │   ├── AdminPage.java                # Admin module actions & locators
│   │   ├── DashboardPage.java            # Dashboard header & module navigation
│   │   ├── EmployeeListPage.java         # PIM Employee List actions & search
│   │   ├── ForgotPasswordPage.java       # Password recovery page
│   │   ├── LoginPage.java                # Login page actions & authentication
│   │   ├── MyInfoPage.java               # Personal/contact details, dependents, qualifications
│   │   ├── PerformancePage.java          # KPIs and Employee Performance Trackers
│   │   ├── PIMPage.java                  # PIM navigation & reports
│   │   ├── RecruitmentPage.java          # Candidates, Vacancies, Resume downloads
│   │   └── TopNevigationMenuPage.java    # Top bar navigation & user profile
│   ├── testdata/
│   │   └── TestData.xlsx                 # Data-driven Excel test sheets (fallback location)
│   └── util/                             # Utilities & Infrastructure
│       ├── Config.java                   # Properties loader (base + local override)
│       ├── ExcelReaderUtill.java         # Apache POI Excel data provider utility
│       ├── ExtentReporterNG.java         # ExtentReports TestNG listener implementation
│       ├── Log.java                      # Log4j wrapper for structured test logging
│       ├── Retry.java                    # IRetryAnalyzer implementation (retry count = 1)
│       ├── RetryListener.java            # IAnnotationTransformer for auto-wiring retry
│       ├── TestConfig.java               # Additional path & configuration helpers
│       ├── TestListener.java             # TestNG test lifecycle listener (failure screenshot)
│       └── WebDriverFactory.java         # Multi-browser WebDriver initialization
│
├── src/test/java/com/hrm/
│   ├── api/
│   │   ├── CreateTestData.java           # Programmatic POI generator for TestData.xlsx
│   │   └── OrangeHRMApiTest.java         # REST Assured API test suite (HTTP, auth, security)
│   ├── testcases/
│   │   ├── LoginTest.java                # Authentication UI test cases
│   │   ├── AdminTab/                     # 31 Admin test classes (Users, Jobs, Organization)
│   │   ├── PIMTab/                       # 21 PIM test classes (Employees, Reports, Search)
│   │   ├── Recruitment/                  # 20 Recruitment test classes (Candidates, Vacancies)
│   │   └── PerformancePage/              # 19 Performance test classes (KPIs, Trackers)
│   └── MyInfoTab/                        # 20 My Info test classes (Personal Details, Contact)
│
├── src/test/resources/
│   ├── TestData.xlsx                     # Primary Excel dataset (multi-sheet)
│   ├── Dhoni.jpg                         # Sample profile image for upload tests
│   ├── steveJobs.jpg                     # Sample image asset
│   ├── Resume.doc                        # Sample candidate resume for upload tests
│   └── Resume.odt                        # Sample alternate resume format
└── Screenshots/FailedTestCase/           # Destination folder for failure screenshots
```

---

## Test Coverage by Module

| Module | Classes | Description / Key Automated Scenarios |
|:---|:---:|:---|
| **Authentication** | 1 | Successful login, invalid credentials validation, logout |
| **Admin** | 31 | User management (Add/Search/Delete User), Job Categories, Pay Grades, Employment Status, Work Shifts, Organization Locations, Nationalities, Localization, Skills, Education |
| **PIM** | 21 | Employee List, Add Employee, Search Employee (by Name, ID, Supervisor), Reports generation, Delete Employee, Reset filters |
| **Recruitment** | 20 | Candidates management (Add, Search, Edit, Delete), Job Vacancies (Add, Modify, Search, Delete), Candidate Resume Download |
| **Performance** | 19 | KPIs configuration (Add, Search, Update, Delete), Performance Trackers, Reviewers assignment, Tracker Logs |
| **My Info** | 20 | Data-driven Personal Details edit, Contact Details edit, Emergency Contacts, Dependents, Immigration details, Work Experience, Languages, Memberships |
| **REST API** | 1 | Login page HTTP 200, valid credentials POST (200/302), invalid credentials rejection, authenticated dashboard access, unauthenticated redirect security |

---

## Prerequisites & Environment Setup

1. **Java Development Kit (JDK 25)**:
   ```powershell
   java -version
   ```
2. **Apache Maven (3.x)**:
   ```powershell
   mvn -version
   ```
3. **Google Chrome**:
   Ensure Google Chrome is installed. Selenium 4.28.1 uses automated **Selenium Manager** to download and manage the matching ChromeDriver automatically.
4. **XAMPP Local Server**:
   Ensure XAMPP is installed at `C:\xampp` with Apache and MySQL modules enabled.

---

## OrangeHRM & XAMPP Configuration

1. **Start XAMPP**:
   - Open **XAMPP Control Panel**.
   - Start **Apache** and **MySQL** services.
2. **Access OrangeHRM 4.0**:
   - Verify OrangeHRM is accessible at:
     ```
     http://localhost/orangehrm/symfony/web/index.php/auth/login
     ```
3. **Configure Framework Properties**:
   Review `config.properties` in the project root:
   ```properties
   # Application URL (Use localhost or reachable OrangeHRM instance)
   app.url.local=http://127.0.0.1/orangehrm/symfony/web/index.php/auth/login
   app.url.remote=http://127.0.0.1/orangehrm/symfony/web/index.php/auth/login

   # Browser Settings (CHROME, FIREFOX)
   local.browser.type=CHROME

   # Credentials (Placeholders - override in config.local.properties or env vars)
   username=your_username
   password=your_password
   ```
   *(Optional)* To override credentials without modifying version-controlled files, create `config.local.properties` (ignored by Git):
   ```properties
   app.url.local=http://127.0.0.1/orangehrm/symfony/web/index.php/auth/login
   local.browser.type=CHROME
   username=your_username
   password=your_password
   ```

---

## Running Tests

### 1. Run Complete Automation Suite (Master `testng.xml`)
To execute the full master test suite covering all UI modules and REST Assured API tests:
```powershell
mvn clean test
```

### 2. Run Only REST Assured API Tests
```powershell
mvn test "-Dtest=OrangeHRMApiTest"
```
Or execute via the standalone API suite:
```powershell
mvn test "-DsuiteXmlFile=testng_api.xml"
```

### 3. Run Specific Functional UI Suites
```powershell
# Admin Module
mvn test "-DsuiteXmlFile=testng_AdminTab.xml"

# PIM Module
mvn test "-DsuiteXmlFile=testng_PIMTab.xml"

# Recruitment Module
mvn test "-DsuiteXmlFile=testng_recruitmenttab.xml"

# My Info Module
mvn test "-DsuiteXmlFile=testng_myInfoTest.xml"
```

### 4. Run Single Test Class
```powershell
mvn test "-Dtest=LoginTest"
mvn test "-Dtest=VerifyRecruitmentTab"
```

### 5. Running from IDE (Eclipse / IntelliJ)
- Right-click any test class or `testng.xml` -> **Run As** -> **TestNG Suite / TestNG Test**.

---

## Test Reports & Artifacts

After test execution completes, reports are generated automatically at the following locations:

| Report Type | Location | Description |
|:---|:---|:---|
| **Extent HTML Report** | `target/surefire-reports/Extent.html` | High-level interactive test execution dashboard with pass/fail charts and test timings. |
| **TestNG Emailable Report** | `target/surefire-reports/emailable-report.html` | Compact standalone single-file HTML report suitable for CI/CD notification. |
| **TestNG Index Report** | `target/surefire-reports/index.html` | Detailed TestNG execution logs, parameters, and stack traces. |
| **Surefire JUnit XML** | `target/surefire-reports/TEST-TestSuite.xml` | Standard JUnit-compatible XML for CI/CD integration (Jenkins, GitHub Actions). |
| **Failure Screenshots** | `Screenshots/FailedTestCase/*.png` | Full-page browser screenshot timestamped at the exact moment of test failure. |
| **Allure HTML Report** | Generated via `mvn allure:serve` | Modern interactive test report with steps and attachment viewing. |

---

## Framework Design Highlights

### 1. Robust Explicit Synchronization
Instead of brittle sleep statements, pages use dynamic synchronization via `WebDriverWait` and `ExpectedConditions`:
```java
WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(15));
wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("...")));
```

### 2. Resilient Data-Driven Testing (Apache POI)
`ExcelReaderUtill.java` resolves `TestData.xlsx` across multiple environments (classpath, project root, or system property). When required data sheets are absent or server is offline, tests report `SkipException` rather than crashing unhandled.

### 3. Automatic Failure Retry Logic
Transient failures caused by network lag or animation delays are automatically re-executed using TestNG's `IRetryAnalyzer`:
```java
// Wires automatically to all @Test methods via RetryListener
public class Retry implements IRetryAnalyzer {
    private int retryCount = 0;
    private int maxRetryCount = 1;
    ...
}
```

### 4. Non-Intrusive API Testing
`OrangeHRMApiTest` tests HTTP responses, redirects (302), cookies (`symfony`), and authentication tokens (`_csrf_token`), providing API level verification for OrangeHRM without requiring heavy external mocks.

---

## Framework Screenshots & Execution Artifacts

The framework includes genuine execution captures and architecture visual artifacts:
- **Dashboard Navigation**: Full dashboard header, welcome banner, and module tab bars.
- **Admin & PIM Modules**: User search grids, pagination controls, employee information forms, and report generation tables.
- **Automated Failure Capture**: Real-time full-page screenshots automatically saved under `Screenshots/FailedTestCase/` with microsecond timestamps when an assertion fails.
- **Interactive Reports**: Extent HTML executive dashboards (`target/surefire-reports/Extent.html`) and TestNG execution summaries.

---

## Known Limitations & Current Status

- **Application Dependency**: Full end-to-end UI and API test execution requires an active, reachable OrangeHRM 4.0 instance (Apache HTTP Server & MySQL via XAMPP). When the server is offline, the API test suite gracefully skips tests (`SkipException`) rather than crashing.
- **Database Seed Consistency**: Tests assume standard OrangeHRM 4.0 sample database records exist (e.g., initial Admin user, standard job titles, default nationalities). Modifying or deleting master data during manual exploration may cause dependent assertions to fail.
- **Grid Execution**: While `WebDriverFactory` includes configuration stubs for Selenium Grid, remote hub routing requires setting `seleniumgrid.runtests=true` and configuring an accessible Selenium Grid Hub URL.
- **Real Build & Execution Status**:
  - `mvn clean compile`: **BUILD SUCCESS** (21 main source classes compiled on Java 25 LTS).
  - `mvn test-compile`: **BUILD SUCCESS** (114 test classes compiled on Java 25 LTS).
  - `OrangeHRMApiTest`: **BUILD SUCCESS** (6 API tests executed; skipped cleanly when local host is offline).
  - Full UI suite execution depends on runtime availability of the local application server.

