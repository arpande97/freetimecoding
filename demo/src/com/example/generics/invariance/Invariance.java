package com.example.generics.invariance;

import java.util.ArrayList;
import java.util.List;

public class Invariance {
	public static void main(String[] args) {
		go(new Integer[2]);
		//go(new ArrayList<Integer>());
	}
	
	//Invariance
	static void go(List<Number> list) {
		//ensures type safety at compile-time
	}
	
	//Covariance
	static void go(Number[] list) {
		//generates an ArrayStoreException at runtime
		list[0] = 24.4;
	}
}
