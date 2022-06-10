
//Cannot Overload a Method Where the Formal Parameter Types of Each Overload Erase to the Same Raw Type
//A class cannot have two overloaded methods that will have the same signature after type erasure.
//
//public class Example {
//    public void print(Set<String> strSet) { }
//    public void print(Set<Integer> intSet) { }
//}




//Cannot Declare Static Fields Whose Types are Type Parameters
//A class's static field is a class-level variable shared by all non-static objects of the class. Hence, static fields of 
//type parameters are not allowed. Consider the following class:
//
//public class MobileDevice<T> {
//    private static T os;
//
//    // ...
//}
//If static fields of type parameters were allowed, then the following code would be confused:
//
//MobileDevice<Smartphone> phone = new MobileDevice<>();
//MobileDevice<Pager> pager = new MobileDevice<>();
//MobileDevice<TabletPC> pc = new MobileDevice<>();
//Because the static field os is shared by phone, pager, and pc, what is the actual type of os? 
//It cannot be Smartphone, Pager, and TabletPC at the same time. You cannot, therefore, create static fields of type parameters.


package com.example.generics;

import java.io.Serializable;
import java.util.AbstractCollection;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;


public class GenericsDemo <T> {
	
	// Generic Constructors are rare!!
	<E extends T> GenericsDemo(E object) { }
	//<E> GenericsDemo(E object) {}
	//GenericsDemo(T object) { }
	//<E extends T> GenericsDemo() {}
	
	public static void main(String[] args) {
		Container<String> stringStore = new Store<>();
		stringStore.set("java");
		//stringStore.set(1);
		System.out.println(stringStore.get());
		
		//here we can see type erasure at work
		//1. Remove String from Container<String>
		//2. In the object, erase Container<T> with Container
		//3. in the method get(), remove T get() to Object get()
		//4. and in the method invocation, compiler implicitly type casts Object into String
		//Note: if we have Container<T extends smth> , then instead of Object get() we get smth get()
		//Compiler ~ type erasure + explicit casting
		
		Container<Integer> integerStore = new Store<>();
		integerStore.set(1);
		System.out.println(integerStore.get());
		
		Container<List<Integer>> listStore = new Store<>();
		listStore.set(Arrays.asList(1, 2, 3));
		System.out.println(listStore.get());
		
		//Container<int> intStore = new Store<>();
		List<Number> list = new ArrayList<>();
		list.add(new Integer(1));
		list.add(new Double(22.0));
		//list.add(new String("22.0"));you can't do this cuz string doesn't inherit from Number class
		
		List[] array = new List[2];
		array[0] = new ArrayList();
		array[1] = new LinkedList();
		
		// Raw type demo:		
		//rawTypeTest();		
		
		List<String> strList1 = Arrays.asList("a", "b", "c");
		List<String> strList2 = Arrays.asList("b", "c", "d");
		//getCommonElementsCount(strList1, strList2);
		
//		GenericsDemo<List> lis1 = new GenericsDemo<>();
		
		// Wildcard
		//use them if you want compile time type safety instead of raw types
//		getCommonElementsCountWithWildcard(strList1, strList2);
		getCommonElementsCount(strList1, strList2);
		
		Container<?> someStore = stringStore;
		Object object = someStore.get();
		System.out.println("Stored element: " + object);	
		
		List<Integer> intList1 = Arrays.asList(1, 2);
		List<Integer> intList2 = Arrays.asList(3, 4);		
		invalidAggregate(intList1, intList2, new ArrayList());
		
		//first go would throw compile time error due to invariance 
		//Integer is a subclass of Number but ArrayList<Integer> is not a subclass of ArrayList<Number>
//		go(new ArrayList<Integer>());
		
		
		//the next statement will not throw a compile time error but a runtime ArrayStoreException
		//this is because arrays are covariant
		//Integer[] is also a subtype of Number[]
		
//		go(new Integer[1]);
		
		genericMethodsDemo();
		boundedWildcards();
		//generic methods are good for static utility methods
	}
	
	static <T> void arrayToCollection(T[] a, Collection<T> c) {
	    for (T o : a) {
	        c.add(o); // Correct
	    }
	}
	
	static <T> T typeArgInference4(T object1, T object2) {
		System.out.println("Most specific type argument inferred: " + object2.getClass().getName());
		return object1;
	}
	
	static <T> void uselessGenericMethod() {
		T t = (T) new Integer(2);
		System.out.println("typeWitness: " + t.getClass().getName());
	}
	
	static void targetTypeInvoker1(List<String> list) {
		for (String s : list) {
			System.out.println("Element: " + s);
		}
	}	
	
	static <T> List<T> targetTypeInvoker2(List<T> list) {
		return list;
	}
	
	static <T> T typeArgInference3(T object1, T object2) {
		System.out.println("Most specific type argument inferred: " + object2.getClass().getName());
		return object1;
	}
	
	public static <T> T typeArgInference1(T object) {
		System.out.println("Type Argument: " + object.getClass().getName());
		return object;
	}
	
	// Type argument inference via method argument
	public static <T> void typeArgInference(T object) {
		System.out.println("Type Argument: " + object.getClass().getName());
	}
	
	// Type argument inference via target type
	public static <T> List<T> typeArgInferenceFromTargetType2() {
		List<String> list = new ArrayList<>();
		list.add("abc"); 
			
		return (List<T>) list;
	}
	
	// Type argument inference via target type
	public static <T> T typeArgInferenceFromTargetType1() {
		return (T) "abc"; // T would be Object after type erasure
	}
	
	// Demonstrates: 
	//    (a) Type argument inference via method arguments & target type
	//    (b) Explicit type argument specification
	//    (c) Generic Constructor
	//    (d) aggregate method fix from wildcard demo
	static void genericMethodsDemo() {
		System.out.println("\n\nInside genericMethodsDemo ... ");
		
		// Type argument inference via method arguments
		typeArgInference(22.0);
		typeArgInference("Java");
		
		// Compile-time type-safety benefit in a generic method
//		Double doubleVal = typeArgInference1("Java");
		
		// Compile-time type-safety benefit in a generic method ~ wrong arguments
		Integer[] na = new Integer[100];
		Collection<Integer> cs = new ArrayList<>(); // Show with Number, String
		arrayToCollection(na, cs);
		
		// Type argument inference via target type		    
		String strVal = typeArgInferenceFromTargetType1(); 
		// Compiler places implicit Integer cast. But, method returns string!!
		//Integer intVal = typeArgInferenceFromTargetType1(); 
		
		// Type arg inference in method invocation context ~ works from Java 8 (show for Java 7)
		GenericsDemo.targetTypeInvoker1(typeArgInferenceFromTargetType2()); // Eclipse Mars showing incorrect type arg
		GenericsDemo.targetTypeInvoker1(new ArrayList<>()); // Eclipse Mars showing incorrect type arg
		GenericsDemo.targetTypeInvoker2(typeArgInferenceFromTargetType2()); // Infers as Object
		List<String> strList = GenericsDemo.targetTypeInvoker2(typeArgInferenceFromTargetType2());
		GenericsDemo.targetTypeInvoker2(new ArrayList<>());
		List<String> strList2 = GenericsDemo.targetTypeInvoker2(new ArrayList<>());
		
		// Inferring most specific super-type
		Serializable obj = typeArgInference3("", new ArrayList());		
		AbstractCollection c = typeArgInference4(new ArrayList(), new HashSet());
		
		GenericsDemo.<String>uselessGenericMethod(); // type witness
		
		// Explicit Type Argument Specification: Type witness. Comment out Generic constructor!!
		// GenericsDemo.<GenericsDemo>typeArgInference(new GenericsDemo());
		
		// Type arg for both constructor & new expression inference: 
		//    (i) inferred from constructor argument. If that's not possible then
		//    (ii) context comes into play, e.g., target type or method invocation content
		new GenericsDemo<Number>(12.0); // T is Number, E is Double
		new GenericsDemo<>(12.0); // T & E are Double
		new <Double>GenericsDemo<Number>(12.0); // Type witness!!
		//new <Double>GenericsDemo<>(12.0); // Could have inferred from arg
		GenericsDemo<Number> gd = new GenericsDemo<>(12.0); // To avoid invariance, smartly infers Number for <> rather than Double 
				
		List<Integer> intList1 = Arrays.asList(1, 2);
		List<Integer> intList2 = Arrays.asList(3, 4);	
		List<Integer> intList3 = new ArrayList<>();
		aggregate(intList1, intList2, intList3);
		System.out.println("intList3: " +  intList3);		
	}
	
	static void boundedWildcards() {
		System.out.println("\n Inside boundedWildcards");
		List<Integer> intList = Arrays.asList(11, 21, 32);
		display(intList);
		List<Double> doubleList = Arrays.asList(11.5, 12.3);
		display(doubleList);
		
		List<Number> numList = new ArrayList<>();
		aggregateWithConsumer(intList, doubleList, numList);
		System.out.println(numList);
		
		Collections.addAll(new ArrayList<Object>(), 1, 2, 3);
		Collections.copy(numList, doubleList);
		System.out.println("---------------------------------------");
		System.out.println("numList after adding: " + numList);
		
		Collections.replaceAll(numList, 11.5, 44);
		
	}
	
	static void display(List<? extends Number> list) {
		for(Number element: list) {
			System.out.println("display() / element: " + element);
		}
		//get() doesn't have class level type parameters as arguments, so we are good but add will fail
		list.get(0);
//		list.add(0);
	}
	
	//generic method which is same as the above wildcard method
//	static <T extends Number> void display(List<T> list) {
//		for(Number el: list) System.out.println(el);
//	}
	
	// Invariance
	static void go(List<Number> list) {}
	
	// Covariance
	static void go(Number[] list) {
		list[0] = 24.4;
	}	
	
	public static <E> void aggregate(List<E> l1, List<E> l2, List<E> l3) {
		l3.addAll(l1);
		l3.addAll(l2);
	}
	
	public static void aggregateWithConsumer(List<? extends Number> l1, List<? extends Number> l2, List<? super Number> l3) {
		//? extends Number says pass anything number or its subtypes
		//that means the list is either List<Number> or List<Integer> or List<Double>
		//? super Number says this list will contain Number or any of its supertype
		//that means the list is either List<Number>, or List<Object>
		l3.addAll(l1);
		l3.addAll(l2);
	}
	
	//Invariance workaround where type safety is not a concern
	static <T extends Number> void invarianceWorkaround(List<T> list) {
		//can't do this because it has to be T and not it's subclass, class-level type parameter here is T
		//list.add(new Double(23.3));
		
		T element = (T) new Double(23.3);
		list.add(element);
		
	}
	
	//the above method can also be written as
//	static void invarianceWorkaround(List<? extends Number> list) {
//		
//	}
	public static void invalidAggregate(List<?> l1, List<?> l2, List<?> l3) {
		//l3.addAll(l1); // null ok
		//l3.addAll(l2);
	}
	
	public static int getCommonElementsCountWithWildcard(List<?> list1, List<?> list2) {
		int count = 0;
		for (Object element : list1) {
			//contains() can be accessed as it doesn't have class level type parameters in its arguments
			//check the method signature it reads 
			//void contains(Object o)
			//object can be anything so you can access it from anywhere
			if (list2.contains(element)) {
				count++;
			}
		}
		//void add(int index, E element)
		//E is the class level parameter
		//can't use this
		//try to do the below thing and you'll fail
		//can't invoke methods that use class level type parameters with any arguments except null
		//if such was allowed, the compiler would also allow us to add a double type, or an int type or whatever type
		
//		list1.add(25);
		System.out.println("Common elements count: " + count);
		return count;
	}
	
	public static int getCommonElementsCount(List list1, List list2) {
		int count = 0;
		for (Object element : list1) {
			if (list2.contains(element)) {
				count++;
			}
		}
		//add() can be accessed here, throwing an error at runtime, (we want errors at runtime)
		//that is why use wildcards, you can't access such methods which invoke class level type parameters 
//		list1.add(25);
//		list2.add(25.5);
		System.out.println("Common elements count: " + count);
		return count;
	}
	
	public static void rawTypeTest() {
		System.out.println("\n\nInside rawTypeTest ...");
		int ISBN = 1505297729;
	    List<Double> prices = new ArrayList<>();
	    
	    HalfIntegrator.getPrice(ISBN, prices);
	    Double price = prices.get(0);	    
	}	
}

class HalfIntegrator {
	
	public static void getPrice(int ISBN, List prices) {
		prices.add(45);
	}
	
}

interface Container<T> {
	void set(T a);
	T get();
}

class Store<T> implements Container<T> {
	private T a;
	
	public void set(T a) {
		this.a = a;
	}
	
	public T get() {
		return a;
	}
}