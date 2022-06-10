package demo.example.concurrency;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

public class AtomicVariables {
	private String name;
	private AtomicInteger count = new AtomicInteger(1); //1 for organizer
	
	public AtomicVariables(String name) {
		this.name = name;
	}
	
	public void attending(int guestCount) {
		if(guestCount == 1) {
			count.incrementAndGet();
		} else {
			count.addAndGet(guestCount);
		}
	}
	
	public void notAttending(int guestCount) {
		if(guestCount == 1) {
			count.decrementAndGet();
		} else {
			boolean updated = false;
			
			while(!updated) {
				int currentCount = count.get();
				int newCount = currentCount - guestCount;
				updated = count.compareAndSet(currentCount, newCount);
			}
		}
	}
	
	public int getCount() {
		return count.get();
	}
	
	public static void main(String[] args) throws InterruptedException {
		AtomicVariables jugBoston = new AtomicVariables("The Boston Java User Group");
		
		Thread user1 = new Thread(new Runnable() {

			@Override
			public void run() {
				// TODO Auto-generated method stub
				jugBoston.attending(4);
				System.out.println(Thread.currentThread().getName() + " : " + jugBoston.getCount());
			}
			
		});
		
		Thread user2 = new Thread(new Runnable() {

			@Override
			public void run() {
				// TODO Auto-generated method stub
				jugBoston.attending(3);
				System.out.println(Thread.currentThread().getName() + " : " + jugBoston.getCount());
				jugBoston.notAttending(3);
				System.out.println(Thread.currentThread().getName() + " : " + jugBoston.getCount());
			}
			
		});
		
		user1.setName("User 1");
		user2.setName("User 2");
		
		
		user1.start();
		TimeUnit.SECONDS.sleep(1);
		user2.start();
		TimeUnit.SECONDS.sleep(2);
	}
}

//class IDGenerator {
//	private AtomicLong id = new AtomicLong();
//	
//	public long getNewId() {
//		return id.incrementAndGet();
//		//incrementAndGet implements optimistic locking
//		//it retries everytime it finds the id has changes by some other thread.
//		//this is optimistic locking or non-blocking method
//	}
//}
