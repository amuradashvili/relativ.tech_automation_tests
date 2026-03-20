param(
    [switch]$IncludeLogin
)

$ErrorActionPreference = "Stop"

if (-not $env:RELATIV_BASE_URL) {
    $env:RELATIV_BASE_URL = "http://localhost:3000"
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
Write-Host "RELATIV_BASE_URL=$env:RELATIV_BASE_URL"
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
