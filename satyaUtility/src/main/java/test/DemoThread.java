package test;

import java.util.ArrayList;
import java.util.concurrent.CopyOnWriteArrayList;

public class DemoThread implements Runnable {
	private String name;
	public static CopyOnWriteArrayList<String> test = new CopyOnWriteArrayList<>();
	
	public DemoThread(String name) {
		this.name = name;
	}

	@Override
	public void run() {
		for (int i = 0; i < 5; i++) {
			try {
				Thread.sleep(1000);
				System.out.println(Thread.currentThread().getName()+" :- "+i);
				test.add(getName()+"----"+Thread.currentThread().getName()+" :- "+i);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
}
