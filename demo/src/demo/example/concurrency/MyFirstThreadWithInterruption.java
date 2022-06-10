package demo.example.concurrency;

import java.util.concurrent.TimeUnit;

public class MyFirstThreadWithInterruption {
	public static void main(String[] args) {
		MyTask task = new MyTask();
		Thread thread = new Thread(task);
		thread.start();
		
		try {
			TimeUnit.SECONDS.sleep(3);
			thread.interrupt();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		
		System.out.println("Inside main ...");
		
	}
}

class MyTask implements Runnable {

	@Override
	public void run() {
		// TODO Auto-generated method stub
		System.out.println("Inside run ...");
		try {
			TimeUnit.SECONDS.sleep(90);
			
		} catch (InterruptedException e) {
			System.out.println("Interrupted..");
		}
		
		go();
	}

	private void go() {
		// TODO Auto-generated method stub
		System.out.println("Inside go...");
		more();
	}

	private void more() {
		// TODO Auto-generated method stub
		System.out.println("Inside more..");
	}
	
}
