package com.relativ.core;

import org.junit.jupiter.api.extension.BeforeEachCallback;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.api.extension.TestWatcher;

import java.util.Optional;

public final class TestResultLogger implements BeforeEachCallback, TestWatcher {

    @Override
    public void beforeEach(ExtensionContext context) {
        StructuredTestReport.initialize(context);
        StructuredTestReport.startTest(context);
        TestExecutionLog.startTest();
    }

    @Override
    public void testSuccessful(ExtensionContext context) {
        StructuredTestReport.testSuccessful(context);
        TestExecutionLog.finishTest();
    }

    @Override
    public void testFailed(ExtensionContext context, Throwable cause) {
        StructuredTestReport.testFailed(context, cause);
        TestExecutionLog.finishTest();
    }

    @Override
    public void testAborted(ExtensionContext context, Throwable cause) {
        StructuredTestReport.testAborted(context, cause);
        TestExecutionLog.finishTest();
    }

    @Override
    public void testDisabled(ExtensionContext context, Optional<String> reason) {
        StructuredTestReport.initialize(context);
        StructuredTestReport.testDisabled(context, reason);
        TestExecutionLog.finishTest();
    }
}
