package demo.example.concurrency;

import java.util.concurrent.TimeUnit;

public class MyFirstThread {
	public static void main(String[] args) throws InterruptedException {
		Task task = new Task(); //work
		Thread thread = new Thread(task); //worker
		thread.start();
		
		//sleep is a static method and hence Thread class can access it without creating an instance of the class
		Thread.sleep(3000);
		TimeUnit.SECONDS.sleep(3);
		thread.interrupt();
		/*
		 * when you put the main thread to sleep, the processor took control of the user thread and went inside the task with it.
		 * in the run() of that method, in the try() block, you put that thread to sleep, changing its interrupted to true.
		 * but then main thread woke up after 3 seconds and interrupted userThread, changing it's interrupted to false again, and you went into catch block 
		 * stating "interrupted"
		 */
		//thread.sleep(4000);
		System.out.println("Inside main ...");
	}
}

class Task implements Runnable {
	@Override
	public void run() {
		
		System.out.println("Inside run ...");
		try {
//			Thread.sleep(4000);
			TimeUnit.SECONDS.sleep(9);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
//			e.printStackTrace();
			System.out.println("interrupted");
		}
		
		go();
	}
	
	private void go() {
		System.out.println("Inside go ...");
		more();
	}
	
	private void more() {
		System.out.println("Inside more ...");
	}
}
