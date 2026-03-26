# Relativ Tech Selenium Automation

This project contains Selenium + JUnit + Maven UI automation for the Relativ Tech website using a Page Object Model structure.

It covers the main site routes and the major interactive flows in the source app at:

`C:\Users\am\OneDrive\Desktop\relativ-tech_dev\relativ-tech-landing`

## Project structure

- `src/test/java/com/relativ/core`: driver setup, config, resource helpers
- `src/test/java/com/relativ/pages`: page objects and shared UI component objects
- `src/test/java/com/relativ/tests`: JUnit suite and feature tests
- `src/test/java/com/relativ/tests/navigation`: navigation tests
- `src/test/java/com/relativ/tests/home`: home page tests
- `src/test/java/com/relativ/tests/company`: company page tests
- `src/test/java/com/relativ/tests/blog`: blog page tests
- `src/test/java/com/relativ/tests/careers`: careers page tests
- `src/test/java/com/relativ/tests/contact`: contact page tests
- `src/test/java/com/relativ/tests/footer`: footer contact tests
- `src/test/java/com/relativ/tests/custom`: configured click test
- `src/test/java/com/relativ/tests/auth`: optional login test
- `src/test/resources/files`: runtime test assets such as the sample resume upload

## Covered flows

- Top navigation on desktop and mobile
- Home page CTAs
- Company page approach toggles and careers CTA
- Blog index navigation and blog detail "More to read"
- Careers filters, active role detail panel, general application validation, file upload, privacy modal
- Contact page info links, validation, privacy modal
- Shared footer contact section on non-contact routes
- Visual design regression baselines for the main public pages and key UI states

## Test classes

- `SiteNavigationTest`
- `HomePageTest`
- `CompanyPageTest`
- `BlogPageTest`
- `CareersPageTest`
- `ContactPageTest`
- `FooterContactSectionTest`
- `ConfiguredClickTest`
- `LoginTest`
- `VisualRegressionTest`

## Quick run buttons

- [Start local app](#1-start-the-local-site)
- [Run all test cases](#2-run-all-test-cases)
- [Run all test cases from one class](#3-run-all-test-cases-from-one-class)
- [Run main public-site suite](#4-run-main-public-site-suite)
- [Run test cases by page](#5-run-test-cases-by-page)
- [Run optional tests](#6-run-optional-tests)

## 1. Start the local site

Start the Next.js source app first:

```powershell
cd C:\Users\am\OneDrive\Desktop\relativ-tech_dev\relativ-tech-landing
npm run dev
```

## 2. Run all test cases

Open a second PowerShell window and run all public-site test cases from this automation project:

```powershell
cd C:\Users\am\OneDrive\Desktop\test_automation\https-www.relativ.tech-

$env:RELATIV_HEADLESS="true"

.\mvnw test
```

Console output now includes per-test status lines such as:

```text
1. START Blog Page Flows.User can open the featured blog post
1.01 STEP Blog Page Flows.User can open the featured blog post -> Open the blog index page
1.02 STEP Blog Page Flows.User can open the featured blog post -> Open the featured blog card
1. PASS Blog Page Flows.User can open the featured blog post
2. FAIL Careers Page Flows.User sees required validation on the general application form -> assertion message
```

If you prefer Maven installed globally, you can use:

```powershell
mvn test
```

## 3. Run all test cases from one class

If you want one IntelliJ run button that starts the full project suite, run this class:

- `src/test/java/com/relativ/tests/AllPublicSiteTestsSuite.java`

From IntelliJ:

- Click the green Run button next to `AllPublicSiteTestsSuite`

From PowerShell:

```powershell
.\run-all-tests.ps1
```

From npm:

```powershell
npm run test:all
```

If you want the run to begin from the home page and then continue through all project cases, use `AllPublicSiteTestsSuite`. The suite order starts with `HomePageTest`.
`LoginTest` is not part of the default suite.
It runs only when you enable it explicitly.

To include login too:

```powershell
.\run-all-tests.ps1 -IncludeLogin
```

or

```powershell
npm run test:all:login
```

## 4. Run main public-site suite

This command runs the main page-object tests for the public website:

```powershell
.\mvnw "-Dtest=SiteNavigationTest,HomePageTest,CompanyPageTest,BlogPageTest,CareersPageTest,ContactPageTest,FooterContactSectionTest,ConfiguredClickTest" test
```

## 5. Run test cases by page

Use these commands when you want to run test cases separately by page or feature. In IntelliJ, each `*Test` class below already has its own Run button.

### Navigation page tests

```powershell
.\mvnw "-Dtest=SiteNavigationTest" test
```

### Home page tests

```powershell
.\mvnw "-Dtest=HomePageTest" test
```

### Company page tests

```powershell
.\mvnw "-Dtest=CompanyPageTest" test
```

### Blog page tests

```powershell
.\mvnw "-Dtest=BlogPageTest" test
```

### Careers page tests

```powershell
.\mvnw "-Dtest=CareersPageTest" test
```

### Contact page tests

```powershell
.\mvnw "-Dtest=ContactPageTest" test
```

### Footer contact section tests

```powershell
.\mvnw "-Dtest=FooterContactSectionTest" test
```

### Custom click tests

```powershell
.\mvnw "-Dtest=ConfiguredClickTest" test
```

## 6. Run optional tests

### Login test

`LoginTest` is separate from the main public-site suite. It is excluded from the default `test` run and only runs when credentials are configured.

```powershell
$env:RELATIV_USERNAME="your-user"
$env:RELATIV_PASSWORD="your-password"
$env:RELATIV_LOGIN_URL="http://localhost:3000/login"

.\mvnw -Pinclude-optional-login "-Dtest=LoginTest" test
```

## 7. Run visual design regression

Use this class when you want screenshot-based design validation for layout, colors, and small UI changes:

- `src/test/java/com/relativ/tests/visual/VisualRegressionTest.java`

### Compare current UI against approved baselines

```powershell
$env:RELATIV_HEADLESS="true"

.\mvnw "-Dtest=VisualRegressionTest" test
```

### Approve a new design baseline intentionally

Run this only after a designer-approved UI change. It overwrites the saved baseline images in `src/test/resources/visual-baselines`.

```powershell
$env:RELATIV_HEADLESS="true"

.\mvnw "-Dtest=VisualRegressionTest" "-Drelativ.visualUpdateBaselines=true" test
```

If a visual test fails, compare these files:

- baseline: `src/test/resources/visual-baselines/*.png`
- actual capture: `target/visual-regression/*-actual.png`
- diff image: `target/visual-regression/*-diff.png`

## Config

Supported system properties or environment variables:

- `relativ.baseUrl` / `RELATIV_BASE_URL`
- `relativ.headless` / `RELATIV_HEADLESS`
- `relativ.loginUrl` / `RELATIV_LOGIN_URL`
- `relativ.username` / `RELATIV_USERNAME`
- `relativ.password` / `RELATIV_PASSWORD`
- `relativ.clickKeys` / `RELATIV_CLICK_KEYS`
- `relativ.stayOpenSeconds` / `RELATIV_STAY_OPEN_SECONDS`
- `relativ.chromedriverPath` / `RELATIV_CHROMEDRIVER_PATH`
- `relativ.visualBaselineDir` / `RELATIV_VISUAL_BASELINE_DIR`
- `relativ.visualArtifactDir` / `RELATIV_VISUAL_ARTIFACT_DIR`
- `relativ.visualUpdateBaselines` / `RELATIV_VISUAL_UPDATE_BASELINES`
- `relativ.visualColorTolerance` / `RELATIV_VISUAL_COLOR_TOLERANCE`
- `relativ.visualAllowedDiffPixels` / `RELATIV_VISUAL_ALLOWED_DIFF_PIXELS`
- `relativ.visualViewportWidth` / `RELATIV_VISUAL_VIEWPORT_WIDTH`
- `relativ.visualViewportHeight` / `RELATIV_VISUAL_VIEWPORT_HEIGHT`
- `relativ.visualSettleMillis` / `RELATIV_VISUAL_SETTLE_MILLIS`

If `RELATIV_BASE_URL` is not set, the suite first auto-detects a local Relativ app on `http://localhost:3000` through `http://localhost:3005`. If none of those endpoints looks like the Relativ site, it falls back to `https://www.relativ.tech`. Set `RELATIV_BASE_URL` explicitly when you want to force a specific environment.

If `RELATIV_HEADLESS` is not set, the suite falls back to `false` for direct IDE and Maven runs so Chrome opens visibly while you debug. The provided `run-all-tests.ps1` wrapper still defaults to headless mode for batch runs. If you want stable visual regression screenshots, set `RELATIV_HEADLESS=true`.
