package com.example.usecase;

import java.util.List;

import java.util.concurrent.Future;
import java.util.function.Function;
import java.util.concurrent.Callable;

public class UseCase {
	private <T, R> List<Future<R>> executeFutures(List<T> collection, Function<T, R> forEachItem)
	   {
	      final List<Future<R>> futures = Lists.newArrayList();
	      for (T item : collection)
	      {
	         futures.add(execute(item, forEachItem));
	      }
	      return futures;
	   }
}
 