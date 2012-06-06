package com.ntu.dealsinterest.models;

public class TestingClass {
	public static long startTime;
	public static long endTime;
	
	public static long calculateTime(){
		long timeTaken = (endTime - startTime) / 1000000;
		return timeTaken;
	}

	public static long getStartTime() {
		return startTime;
	}

	public static void setStartTime() {
		TestingClass.startTime = System.nanoTime();
	}

	public static long getEndTime() {
		return endTime;
	}

	public static void setEndTime() {
		TestingClass.endTime = System.nanoTime();
	}
}
