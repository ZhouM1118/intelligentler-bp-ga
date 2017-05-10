package com.intelligentler.test;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

public class Testthis {
	
	public static List<TestCompare> geneTest(int size)
	{
		List<TestCompare> tests = new ArrayList<TestCompare>();
		Random random = new Random();
		for (int i = 0; i < size; i++) 
		{
			TestCompare aCompare = new TestCompare();
			aCompare.empId = i + random.nextInt(10);
			tests.add(aCompare);
		}
		return tests;
	}
	
	private static void printList(List<TestCompare> list) {
		for (TestCompare e: list) {
			System.out.println(e.empId);
		}
	}

	public static void main(String[] args) {
		List<TestCompare> tests = geneTest(10);
		Collections.sort(tests);
		printList(tests);

	}

}
