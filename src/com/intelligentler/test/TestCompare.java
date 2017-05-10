package com.intelligentler.test;

public class TestCompare implements Comparable<TestCompare> {
	
	public int empId;

	@Override
	public int compareTo(TestCompare o) {
		return this.empId - o.empId;
	}

}
