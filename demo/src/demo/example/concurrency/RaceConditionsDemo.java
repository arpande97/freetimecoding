package demo.example.concurrency;

public class RaceConditionsDemo {
	public static void main(String[] args) {
		BankAccount task = new BankAccount();
		task.setBalance(100);
		
		Thread john = new Thread(task);
		Thread anita = new Thread(task);
		john.setName("John");
		anita.setName("Anita");
		john.start();
		anita.start();
	}
}

//this task has a race condition ~ check-then-act (this is not thread safe)
//race condition occurs when two threads share some mutable data without a lock
class BankAccount implements Runnable {

	private int balance;
	
	public void setBalance(int balance) {
		this.balance = balance;
	}
	
	@Override
	public void run() {
		// TODO Auto-generated method stub
		makeWithdrawal(75);
		if(balance < 0) {
			System.out.println("Money overdrawn!!!");
		}
	}
	
	
	//As I am understanding, the synchronized makes the method atomic?
	//when synchronized is called in the below method, the intrinsic lock of BankAccount will be acquired
	//remember, every object has it's own built in lock (monitor locks)
	private synchronized void makeWithdrawal(int amount) {
		if(balance >= amount) {
			System.out.println(Thread.currentThread().getName() + " is about to withdraw ...");
			balance -= amount;
			System.out.println(Thread.currentThread().getName() + " has withdrawn " + amount);
		} else {
			System.out.println("Sorry, not enough balance for " + Thread.currentThread().getName());
		}
	}
	
}


// public class BankAccount implements Runnable {
//     private int balance;
//     public int getBalance() {
//         return balance;
//     }

//     public void run() {
//         makeWithdrawal(56);
//     }

// public synchronized void makeWithdrawal(int amount) {
//     if(balance >= amount)
//         balance -= amount;
// }

//In the above code snippet, we have used synchronized block.
//but consider this scenario:
//OUT-OF-ORDER ACTIONS:
//John's thread is inside makeWithdrawal() & does balance check
//goes into RUNNABLE before updating balance
//Anita's thread access getBalance()

//John's thread is in run(). It goes into runnable. Not yet in running.
//but anita accesses the balance. sees 100;
//and then john's thread goes in and withdraws
//then anita has wrong info.
//so make even getBalance() synchronized


