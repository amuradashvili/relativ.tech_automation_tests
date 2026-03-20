package com.relativ.core;

public final class TestExecutionLog {

    private static final ThreadLocal<Boolean> TEST_ACTIVE = ThreadLocal.withInitial(() -> false);
    private static final ThreadLocal<Integer> STEP_COUNTER = ThreadLocal.withInitial(() -> 0);

    private TestExecutionLog() {
    }

    public static void startTest() {
        TEST_ACTIVE.set(true);
        STEP_COUNTER.set(0);
    }

    public static void finishTest() {
        TEST_ACTIVE.remove();
        STEP_COUNTER.remove();
    }

    public static void step(String message) {
        if (!TEST_ACTIVE.get()) {
            System.out.println("STEP - " + message);
            return;
        }

        int nextStep = STEP_COUNTER.get() + 1;
        STEP_COUNTER.set(nextStep);
        StructuredTestReport.recordStep(nextStep, message);
        System.out.printf("STEP %02d - %s%n", nextStep, message);
    }
}
