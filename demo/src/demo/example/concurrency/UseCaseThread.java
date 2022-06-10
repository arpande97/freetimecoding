package demo.example.concurrency;
import java.lang.System.*;

public class UseCaseThread {

}
import java.lang.Thread.UncaughtExceptionHandler;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;

import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.concurrent.DelegatingSecurityContextCallable;
import org.springframework.security.core.context.SecurityContextHolder;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.util.concurrent.ThreadFactoryBuilder;
import com.google.inject.Key;
import com.google.inject.Provider;
import com.google.inject.servlet.ScopingException;
import com.google.inject.servlet.ServletScopes;
import com.mycom.commons.infrastructure.security.UserAuthorizationInfo;

/*
* Used to run tasks in parallel
* @reference https://docs.oracle.com/javase/8/docs/api/java/util/concurrent/ThreadPoolExecutor.html
*/
public class AsyncTaskExecutor
{
   public static final Logger logger = LoggerFactory.getLogger(AsyncTaskExecutor.class);

   public interface Task<R>
   {
      R run() throws Exception;
   }

   private static final String THREAD_NAME_PREFIX = "thread-pool";

   private final ExecutorService executorService;
   private final Provider<UserAuthorizationInfo> userAuthProvider;

   @Inject
   public AsyncTaskExecutor(Provider<UserAuthorizationInfo> userAuthProvider)
   {
      this.userAuthProvider = userAuthProvider;
      this.executorService = initExecutorService();
   }

   protected ExecutorService initExecutorService()
   {
      int maxThreadCount = Runtime.getRuntime().availableProcessors();
      if(maxThreadCount <= 1)
      {
         maxThreadCount = 5;
         logger.info("Using default processors for AsyncTaskExecutor {}", maxThreadCount);
      }
      return new ThreadPoolExecutor(
         maxThreadCount - 1,
         maxThreadCount - 1,
         1,
         TimeUnit.HOURS,
         new LinkedBlockingQueue<Runnable>(maxThreadCount * 10),
         threadFactory(),
         new ThreadPoolExecutor.CallerRunsPolicy());
   }

   private ThreadFactory threadFactory()
   {
      return new ThreadFactoryBuilder().setNameFormat(THREAD_NAME_PREFIX + "-%d").setUncaughtExceptionHandler(new UncaughtExceptionHandler()
      {
         @Override
         public void uncaughtException(Thread t, Throwable e)
         {
            logger.error("Thread Pool Uncaught Exception", e);
         }
      }).build();
   }

   public interface AsyncTaskStatus<T, R>
   {
      void onFailure(List<R> result, T input, Throwable exception);
   }

   private static class DefaultAsyncTaskStatus<T, R> implements AsyncTaskStatus<T, R>
   {
      @Override
      public void onFailure(List<R> result, T input, Throwable e)
      {
         if (e instanceof RuntimeException)
            throw (RuntimeException) e;
         throw new RuntimeException(e);
      }
   }

   public <T, R> List<R> execute(List<T> collection, Function<T, R> forEachItem, AsyncTaskStatus<T, R> statusForEachItem)
   {
      List<Future<R>> futures = executeFutures(collection, forEachItem);
      return collectFutures(collection, futures, statusForEachItem);
   }

   public <T, R> List<R> execute(List<T> collection, Function<T, R> forEachItem)
   {
      List<Future<R>> futures = executeFutures(collection, forEachItem);
      return collectFutures(collection, futures, new DefaultAsyncTaskStatus<T, R>());
   }

   private <T, R> List<Future<R>> executeFutures(List<T> collection, Function<T, R> forEachItem)
   {
      final List<Future<R>> futures = Lists.newArrayList();
      for (T item : collection)
      {
         futures.add(execute(item, forEachItem));
      }
      return futures;
   }

   private <T, R> List<R> collectFutures(List<T> collection, List<Future<R>> futures, AsyncTaskStatus<T, R> statusForEachItem)
   {
      List<R> result = Lists.newArrayList();
      for (int i = 0; i < futures.size(); i++)
      {
         try
         {
            result.add(futures.get(i).get());
         }
         catch (Exception e)
         {
            statusForEachItem.onFailure(result, collection.get(i), e);
         }
      }
      return result;
   }

   public <T, R> Future<R> execute(final T item, final Function<T, R> forEachItem)
   {
      return execute(new Task<R>()
      {
         @Override
         public R run()
         {
            return forEachItem.apply(item);
         }
      });
   }

   public <R> Future<R> execute(Task<R> task)
   {
      return executeInFuture(task);
   }

   private <R> Future<R> runImmediatlyInCallersThread(Task<R> task)
   {
      try
      {
         return CompletableFuture.<R> completedFuture(task.run());
      }
      catch (Exception e)
      {
         if (e instanceof RuntimeException)
            throw (RuntimeException) e;
         throw new RuntimeException(e);
      }
   }

   private <R> Future<R> executeInFuture(final Task<R> task)
   {
      try
      {
         return executorService.submit(executeInServletRequestScope(task));
      }
      catch (ScopingException e)
      {
         logger.error("Failed to continue the http request in another thread. Fallback to run in callers thread.", e);
      }
      return runImmediatlyInCallersThread(task);
   }

   protected <R> Callable<R> executeInServletRequestScope(final Task<R> task)
   {
      return ServletScopes.continueRequest(new DelegatingSecurityContextCallable<>(new Callable<R>()
      {
         @Override
         public R call() throws Exception
         {
            return task.run();
         }
      }, SecurityContextHolder.getContext()), requestScopedValues());
   }

   private Map<Key<?>, Object> requestScopedValues()
   {
      final Map<Key<?>, Object> requestScopedValues = Maps.newHashMap();
      requestScopedValues.put(Key.get(UserAuthorizationInfo.class), userAuthProvider.get());
      return requestScopedValues;
   }

}


