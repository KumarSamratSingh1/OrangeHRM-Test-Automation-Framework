package com.hrm.util;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.testng.ITestContext;
import org.testng.ITestListener;
import org.testng.ITestResult;

import com.hrm.base.TestBase;

public class TestListener extends TestBase implements ITestListener {

	@Override
	public void onTestFailure(ITestResult result) {
		System.out.println("***** Error " + result.getName() + " test has failed *****");
		String methodName = result.getName().trim();
		try {
			takeScreenShotForFailedTestCase(methodName);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void onTestStart(ITestResult result) {
		System.out.println("Starting Test: " + result.getName());
	}

	@Override
	public void onTestSuccess(ITestResult result) {
		System.out.println("Test Passed: " + result.getName());
	}

	@Override
	public void onTestSkipped(ITestResult result) {
		System.out.println("Test Skipped: " + result.getName());
	}

	@Override
	public void onTestFailedButWithinSuccessPercentage(ITestResult result) {
	}

	@Override
	public void onStart(ITestContext context) {
		System.out.println("Starting Test Suite: " + context.getName());
	}

	@Override
	public void onFinish(ITestContext context) {
		// List of test results which we will delete later
		List<ITestResult> testsToBeRemoved = new ArrayList<>();

		// collect all id's from passed test
		Set<Integer> passedTestIds = new HashSet<>();
		for (ITestResult passedTest : context.getPassedTests().getAllResults()) {
			passedTestIds.add(getId(passedTest));
		}

		Set<Integer> failedTestIds = new HashSet<>();
		for (ITestResult failedTest : context.getFailedTests().getAllResults()) {
			int failedTestId = getId(failedTest);

			// if we saw this test as a failed test before we mark as to be deleted
			// or delete this failed test if there is at least one passed version
			if (failedTestIds.contains(failedTestId) || passedTestIds.contains(failedTestId)) {
				testsToBeRemoved.add(failedTest);
			} else {
				failedTestIds.add(failedTestId);
			}
		}

		Set<Integer> skippedTestIds = new HashSet<>();
		for (ITestResult skippedTest : context.getSkippedTests().getAllResults()) {
			int skippedTestId = getId(skippedTest);

			// if we saw this test as a Skipped test before we mark as to be deleted
			// or delete this skipped test if there is at least one passed version
			if (skippedTestIds.contains(skippedTestId) || passedTestIds.contains(skippedTestId)
					|| failedTestIds.contains(skippedTestId)) {
				testsToBeRemoved.add(skippedTest);
			} else {
				skippedTestIds.add(skippedTestId);
			}
		}

		// finally delete all tests that are marked
		for (Iterator<ITestResult> iterator = context.getFailedTests().getAllResults().iterator(); iterator.hasNext();) {
			ITestResult testResult = iterator.next();
			if (testsToBeRemoved.contains(testResult)) {
				iterator.remove();
			}
		}

		for (Iterator<ITestResult> iterator = context.getSkippedTests().getAllResults().iterator(); iterator.hasNext();) {
			ITestResult testResult = iterator.next();
			if (testsToBeRemoved.contains(testResult)) {
				iterator.remove();
			}
		}
	}

	public static int getId(ITestResult result) {
		int id = result.getTestClass().getName().hashCode();
		id = 31 * id + result.getMethod().getMethodName().hashCode();
		id = 31 * id + (result.getParameters() != null ? Arrays.hashCode(result.getParameters()) : 0);
		return id;
	}
}
