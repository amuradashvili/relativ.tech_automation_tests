package com.relativ.core;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.opentest4j.AssertionFailedError;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.atomic.AtomicInteger;

public final class StructuredTestReport {

    private static final ExtensionContext.Namespace NAMESPACE = ExtensionContext.Namespace.create(StructuredTestReport.class);
    private static final String RESOURCE_KEY = "structured-report-resource";

    private static final List<TestCaseResult> RESULTS = Collections.synchronizedList(new ArrayList<>());
    private static final ConcurrentMap<String, TestCaseResult> RESULTS_BY_ID = new ConcurrentHashMap<>();
    private static final ThreadLocal<TestCaseResult> CURRENT_RESULT = new ThreadLocal<>();
    private static final AtomicInteger EXECUTION_ORDER = new AtomicInteger(0);

    private StructuredTestReport() {
    }

    public static void initialize(ExtensionContext context) {
        context.getRoot().getStore(NAMESPACE)
                .getOrComputeIfAbsent(RESOURCE_KEY, key -> new ReportResource(), ReportResource.class);
    }

    public static void startTest(ExtensionContext context) {
        TestCaseResult result = new TestCaseResult();
        result.executionOrder = EXECUTION_ORDER.incrementAndGet();
        result.testName = formatDisplayName(context);
        result.className = context.getRequiredTestClass().getName();
        result.fileName = context.getRequiredTestClass().getSimpleName() + ".java";
        result.filePath = resolveSourcePath(context.getRequiredTestClass());
        result.status = "RUNNING";

        RESULTS.add(result);
        RESULTS_BY_ID.put(context.getUniqueId(), result);
        CURRENT_RESULT.set(result);
    }

    public static void recordStep(int stepNumber, String message) {
        TestCaseResult current = CURRENT_RESULT.get();
        if (current == null) {
            return;
        }
        current.steps.add(new StepResult(stepNumber, "PASS", message));
    }

    public static void testSuccessful(ExtensionContext context) {
        TestCaseResult result = getResult(context);
        if (result == null) {
            return;
        }
        result.status = "PASSED";
        finishCurrentResult(result);
    }

    public static void testFailed(ExtensionContext context, Throwable cause) {
        TestCaseResult result = getResult(context);
        if (result == null) {
            return;
        }
        result.status = "FAILED";
        populateFailure(result, cause);
        markLastStepAsFailed(result);
        finishCurrentResult(result);
    }

    public static void testAborted(ExtensionContext context, Throwable cause) {
        TestCaseResult result = getResult(context);
        if (result == null) {
            return;
        }
        result.status = "SKIPPED";
        populateFailure(result, cause);
        finishCurrentResult(result);
    }

    public static void testDisabled(ExtensionContext context, Optional<String> reason) {
        TestCaseResult result = new TestCaseResult();
        result.executionOrder = EXECUTION_ORDER.incrementAndGet();
        result.testName = formatDisplayName(context);
        result.className = context.getRequiredTestClass().getName();
        result.fileName = context.getRequiredTestClass().getSimpleName() + ".java";
        result.filePath = resolveSourcePath(context.getRequiredTestClass());
        result.status = "SKIPPED";
        result.error = reason.filter(text -> !text.isBlank()).orElse("Disabled");
        result.possibleReason = "The test is disabled by configuration or annotation.";
        RESULTS.add(result);
    }

    private static TestCaseResult getResult(ExtensionContext context) {
        return RESULTS_BY_ID.get(context.getUniqueId());
    }

    private static void finishCurrentResult(TestCaseResult result) {
        RESULTS_BY_ID.entrySet().removeIf(entry -> entry.getValue() == result);
        if (CURRENT_RESULT.get() == result) {
            CURRENT_RESULT.remove();
        }
    }

    private static void populateFailure(TestCaseResult result, Throwable cause) {
        if (cause == null) {
            return;
        }

        result.error = firstLine(cause.getMessage(), cause.getClass().getSimpleName());
        result.failedStep = result.steps.isEmpty() ? "N/A" : result.steps.get(result.steps.size() - 1).text;
        result.locatorOrAction = findLocatorOrAction(result.steps);
        result.expected = extractExpected(cause);
        result.actual = extractActual(cause);
        result.possibleReason = inferPossibleReason(cause);
        result.rawStack = stackTraceOf(cause);
    }

    private static void markLastStepAsFailed(TestCaseResult result) {
        if (result.steps.isEmpty()) {
            return;
        }
        StepResult last = result.steps.get(result.steps.size() - 1);
        result.steps.set(result.steps.size() - 1, new StepResult(last.number, "FAIL", last.text));
    }

    private static String resolveSourcePath(Class<?> testClass) {
        String packagePath = testClass.getName().replace('.', '/');
        return Path.of("src", "test", "java", packagePath + ".java").toString();
    }

    private static String formatDisplayName(ExtensionContext context) {
        return formatClassName(context.getRequiredTestClass()) + "." + context.getDisplayName();
    }

    private static String formatClassName(Class<?> testClass) {
        DisplayName displayName = testClass.getAnnotation(DisplayName.class);
        if (displayName != null && !displayName.value().isBlank()) {
            return displayName.value();
        }
        return testClass.getSimpleName();
    }

    private static String findLocatorOrAction(List<StepResult> steps) {
        for (int index = steps.size() - 1; index >= 0; index--) {
            String text = steps.get(index).text;
            if (text.contains("By.") || text.startsWith("Click ") || text.startsWith("Type ")
                    || text.startsWith("Upload ") || text.startsWith("Wait ")) {
                return text;
            }
        }
        return "N/A";
    }

    private static String extractExpected(Throwable cause) {
        if (cause instanceof AssertionFailedError error) {
            if (error.getExpected() == null) {
                return "N/A";
            }
            Object expected = error.getExpected().getValue();
            return expected == null ? "N/A" : String.valueOf(expected);
        }
        return "N/A";
    }

    private static String extractActual(Throwable cause) {
        if (cause instanceof AssertionFailedError error) {
            if (error.getActual() == null) {
                return "N/A";
            }
            Object actual = error.getActual().getValue();
            return actual == null ? "N/A" : String.valueOf(actual);
        }
        return "N/A";
    }

    private static String inferPossibleReason(Throwable cause) {
        String simpleName = cause.getClass().getSimpleName();
        String message = cause.getMessage() == null ? "" : cause.getMessage();

        if (simpleName.contains("Timeout")) {
            return "The expected element or page state did not appear before the timeout.";
        }
        if (simpleName.contains("NoSuchElement")) {
            return "The selector likely no longer matches the page or the element was not rendered.";
        }
        if (simpleName.contains("ClickIntercepted")) {
            return "Another element likely overlapped the target during the click.";
        }
        if (cause instanceof AssertionFailedError) {
            return "The UI behavior did not match the assertion expected by the test.";
        }
        if (simpleName.contains("TestAborted") || message.contains("Assumption failed")) {
            return "The test prerequisites were not configured for this run.";
        }
        return "Inspect the raw stack trace for the underlying Selenium or assertion failure.";
    }

    private static String firstLine(String message, String fallback) {
        if (message == null || message.isBlank()) {
            return fallback;
        }
        int lineBreak = message.indexOf('\n');
        return lineBreak >= 0 ? message.substring(0, lineBreak).trim() : message.trim();
    }

    private static String stackTraceOf(Throwable cause) {
        StringWriter writer = new StringWriter();
        cause.printStackTrace(new PrintWriter(writer));
        return writer.toString().trim();
    }

    private static String renderReport() {
        List<TestCaseResult> orderedResults = new ArrayList<>(RESULTS);
        orderedResults.sort((left, right) -> Integer.compare(left.executionOrder, right.executionOrder));

        long passed = orderedResults.stream().filter(result -> "PASSED".equals(result.status)).count();
        long failed = orderedResults.stream().filter(result -> "FAILED".equals(result.status)).count();
        long skipped = orderedResults.stream().filter(result -> "SKIPPED".equals(result.status)).count();

        StringBuilder report = new StringBuilder();
        report.append("TEST RUN SUMMARY").append(System.lineSeparator());
        report.append("- Total: ").append(orderedResults.size()).append(System.lineSeparator());
        report.append("- Passed: ").append(passed).append(System.lineSeparator());
        report.append("- Failed: ").append(failed).append(System.lineSeparator());
        report.append("- Skipped: ").append(skipped).append(System.lineSeparator()).append(System.lineSeparator());

        report.append("FAILED TESTS").append(System.lineSeparator());
        if (failed == 0) {
            report.append("None").append(System.lineSeparator()).append(System.lineSeparator());
        } else {
            int counter = 1;
            for (TestCaseResult result : orderedResults) {
                if (!"FAILED".equals(result.status)) {
                    continue;
                }
                report.append(counter++).append(". ").append(result.testName).append(System.lineSeparator());
                report.append("   - File: ").append(result.filePath).append(System.lineSeparator());
                report.append("   - Class: ").append(result.className).append(System.lineSeparator());
                report.append("   - Failed step: ").append(orDefault(result.failedStep)).append(System.lineSeparator());
                report.append("   - Error: ").append(orDefault(result.error)).append(System.lineSeparator());
                report.append("   - Expected: ").append(orDefault(result.expected)).append(System.lineSeparator());
                report.append("   - Actual: ").append(orDefault(result.actual)).append(System.lineSeparator());
                report.append("   - Possible reason: ").append(orDefault(result.possibleReason)).append(System.lineSeparator());
            }
            report.append(System.lineSeparator());
        }

        report.append("DETAILED RESULTS").append(System.lineSeparator()).append(System.lineSeparator());
        for (TestCaseResult result : orderedResults) {
            report.append("[TEST] ").append(result.testName).append(System.lineSeparator());
            report.append("Status: ").append(result.status).append(System.lineSeparator());
            report.append("Steps:").append(System.lineSeparator());
            if (result.steps.isEmpty()) {
                report.append("1. PASS - No explicit steps recorded").append(System.lineSeparator());
            } else {
                for (StepResult step : result.steps) {
                    report.append(step.number)
                            .append(". ")
                            .append(step.state)
                            .append(" - ")
                            .append(step.text)
                            .append(System.lineSeparator());
                }
            }
            report.append("Error details:").append(System.lineSeparator());
            if ("FAILED".equals(result.status)) {
                report.append("File: ").append(result.filePath).append(System.lineSeparator());
                report.append("Class: ").append(result.className).append(System.lineSeparator());
                report.append("Failed step: ").append(orDefault(result.failedStep)).append(System.lineSeparator());
                report.append("Locator/action: ").append(orDefault(result.locatorOrAction)).append(System.lineSeparator());
                report.append("Error: ").append(orDefault(result.error)).append(System.lineSeparator());
                report.append("Expected: ").append(orDefault(result.expected)).append(System.lineSeparator());
                report.append("Actual: ").append(orDefault(result.actual)).append(System.lineSeparator());
                report.append("Possible reason: ").append(orDefault(result.possibleReason)).append(System.lineSeparator());
                report.append("Raw stack trace:").append(System.lineSeparator());
                report.append(orDefault(result.rawStack)).append(System.lineSeparator());
            } else if ("SKIPPED".equals(result.status)) {
                report.append(orDefault(result.error)).append(System.lineSeparator());
            } else {
                report.append("None").append(System.lineSeparator());
            }
            report.append(System.lineSeparator());
        }

        return report.toString();
    }

    private static String orDefault(String value) {
        return value == null || value.isBlank() ? "N/A" : value;
    }

    private static final class ReportResource implements ExtensionContext.Store.CloseableResource {

        private ReportResource() {
            RESULTS.clear();
            RESULTS_BY_ID.clear();
            CURRENT_RESULT.remove();
            EXECUTION_ORDER.set(0);
        }

        @Override
        public void close() throws Exception {
            String report = renderReport();
            Path reportDir = Path.of("target", "test-reports");
            Files.createDirectories(reportDir);
            Path reportFile = reportDir.resolve("structured-test-report.txt");
            Files.writeString(reportFile, report);
            System.out.println("Structured test report saved to " + reportFile.toAbsolutePath());
        }
    }

    private record StepResult(int number, String state, String text) {
    }

    private static final class TestCaseResult {
        private int executionOrder;
        private String testName;
        private String className;
        private String fileName;
        private String filePath;
        private String status;
        private String failedStep;
        private String locatorOrAction;
        private String error;
        private String expected;
        private String actual;
        private String possibleReason;
        private String rawStack;
        private final List<StepResult> steps = new ArrayList<>();
    }
}
