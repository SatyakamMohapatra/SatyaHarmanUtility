package test;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

public class ProducerConsumerDemo1 {
	public static void main(String[] args) {
		BlockingQueue<Integer> blockingQueue = new ArrayBlockingQueue<>(10);
		Thread thread1 = new Thread(new Producer(blockingQueue));
		Thread thread2 = new Thread(new Consumer(blockingQueue));
		thread1.start();
		thread2.start();
		
		
	}
}
class Producer implements Runnable{
	BlockingQueue<Integer> blockingQueue = null;
	private int i =0;
	public Producer(BlockingQueue<Integer> blockingQueue) {
		super();
		this.blockingQueue = blockingQueue;
	}
	@Override
	public void run() {
		while(true){
			try {
				produce(i);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}
	
	public void produce(int i) throws InterruptedException{
		System.out.println("producer thread produce element "+ i);
		blockingQueue.put(i);
		Thread.sleep(100);
	}
	
}

class Consumer implements Runnable{
	BlockingQueue<Integer> blockingQueue = null;
	public Consumer(BlockingQueue<Integer> blockingQueue) {
		super();
		this.blockingQueue = blockingQueue;
	}
	@Override
	public void run() {
		while(true){
			try {
				consume();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}
	public void consume() throws InterruptedException{
		System.out.println("consumer thread consume element "+blockingQueue.take());
		Thread.sleep(10000);
	}
	
	
}