package demo.example.concurrency;

import java.util.concurrent.TimeUnit;

public class VolatileVariables {
	
	private static volatile boolean stop = false;
	//volatile keyword tells JVM to create a copy of the variable in the main memory and all the accesses see that value
	public static void main(String[] args) throws InterruptedException {
		new Thread(new Runnable() {

			@Override
			public void run() {
				// TODO Auto-generated method stub
				while(!stop) {
					System.out.println("In while ...");
				}
			}
			
		}).start();
		
		TimeUnit.MILLISECONDS.sleep(1);
		stop = true;
		
	}
}
