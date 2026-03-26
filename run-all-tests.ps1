param(
    [switch]$IncludeLogin
)

$ErrorActionPreference = "Stop"

if (-not $env:RELATIV_BASE_URL) {
    Write-Host "RELATIV_BASE_URL not set. UiConfig will auto-detect a local Relativ app on ports 3000-3005 and then fall back to https://www.relativ.tech."
}

if (-not $env:RELATIV_HEADLESS) {
    $env:RELATIV_HEADLESS = "true"
}

$mavenArgs = @(
    "-Dmaven.repo.local=.m2\repository"
)

$mavenArgs += @(
    "-Dtest=AllPublicSiteTestsSuite",
    "test"
)

Write-Host "Running AllPublicSiteTestsSuite"
Write-Host "RELATIV_BASE_URL=$(if ($env:RELATIV_BASE_URL) { $env:RELATIV_BASE_URL } else { '<auto-detect>' })"
Write-Host "RELATIV_HEADLESS=$env:RELATIV_HEADLESS"
Write-Host "Include login=$IncludeLogin"

& ".\mvnw.cmd" @mavenArgs
$exitCode = $LASTEXITCODE

if ($exitCode -eq 0 -and $IncludeLogin) {
    Write-Host ""
    Write-Host "Running LoginTest"

    $loginArgs = @(
        "-Dmaven.repo.local=.m2\repository",
        "-Pinclude-optional-login",
        "-Dtest=LoginTest",
        "test"
    )

    & ".\mvnw.cmd" @loginArgs
    $exitCode = $LASTEXITCODE
}

$structuredReport = Resolve-Path ".\target\test-reports\structured-test-report.txt" -ErrorAction SilentlyContinue
$surefireReports = Resolve-Path ".\target\surefire-reports" -ErrorAction SilentlyContinue

Write-Host ""
Write-Host "Structured report: $(if ($structuredReport) { $structuredReport.Path } else { 'Not found' })"
Write-Host "Surefire reports: $(if ($surefireReports) { $surefireReports.Path } else { 'Not found' })"

exit $exitCode
