package com.example.staticAndfinal;

public class ConstantVariables {
	//without static, final byte month = 3, is an instance variable and you can't access non-static fiels from inside a static method, so make this static and 
	//everything will be fine.
	
	
	//can't be a constant if a static initializer is used
	
//	static final byte month;
//	static {
//		month = 1;
//	}
	static final byte month = 3;
	static void switchExample() {
		System.out.println("Inside Switch");
		//making it final makes it constant
		//final byte month = 3;
		switch (month) {
		case 1: System.out.println("jan");
		        break;
		case month:
		}
	}
}
