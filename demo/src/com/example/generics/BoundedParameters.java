package com.example.generics;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class BoundedParameters <T extends List>{

	//can access methods defined by bounds
	//if T did not extend List, then you wouldn't have been able to access size() method
	//here type parameter would replace T with List
	
	void go(T list) {
		int i = list.size();
	}
	
	
}
class AnotherBoundedType <T extends List & Serializable> {
	//here you can do 
	//AnotherBoundedType<ArrayList> test = new AnotherBoundedType<>();
	//because arrayList extends both list and serializable
	
	//but can't do:
	//AnotherBoundedType<List> test = new AnotherBoundedType<>();
	List<Integer> l = new ArrayList<>();
	
	
}

//what you can do with such a class
//BoundedParameters<List> test = new BoundedParameters<>();
//BoundedParameters<ArrayList> test = new BoundedParameters<>();
//BoundedParameters<LinkedList> test = new BoundedParameters<>();

//what you cannot do:
//BoundedParameters<Collection> test = new BoundedParameters<>();
//because collection is a superclass of List


//if class is one of the bounds, it must be first
//first bound is class -> remaining must be interfaces

