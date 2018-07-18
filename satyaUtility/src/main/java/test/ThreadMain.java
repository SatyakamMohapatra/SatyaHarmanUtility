package test;

import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;



public class ThreadMain {
	public static void main(String... args) throws InterruptedException, ExecutionException{
		ExecutorService service = Executors.newFixedThreadPool(3);
		for (int i = 0; i < 10; i++) {
			System.out.println(service.submit(new DemoThread("ThreadTest - "+i)));
		}
		
		Thread.sleep(1000);
		System.out.println("Thread Going To start");
		service.shutdown();
		service.awaitTermination(10000, TimeUnit.MILLISECONDS);
		System.out.println("Start ittration");
		
		for(String name: DemoThread.test){
			System.out.println(name);
		}
		
	}
}
/*
*//**
 * Run runReportCache
 *//*
private void runReportCache(List<ReportCacheDto> reports) {
	for (ReportCacheDto reportCacheDto : reports) {
		loadReport(reportCacheDto);
	}
	
	BlockingQueue<ReportCacheDto> blockingQueue = new ArrayBlockingQueue<>(10, true);

	Thread providerThread = new Thread(new ReportCacheProvider(blockingQueue, reports));
	Thread consumerThread = new Thread(new ReportCacheConsumer(blockingQueue));
	//providerThread.start();
	//consumerThread.start();
	
}*/
/*
*//**
 * 
 * @author SMohapatra3
 * Producer
 *//*
class ReportCacheProvider implements Runnable{
	
	BlockingQueue<ReportCacheDto> blockingQueue = null;
	List<ReportCacheDto> reports  = null;
	
	public ReportCacheProvider(BlockingQueue<ReportCacheDto> blockingQueue,List<ReportCacheDto> reports) {
		this.blockingQueue = blockingQueue;
		this.reports = reports;
	}
	
	@Override
	public void run() {
		for (ReportCacheDto reportCacheDto : reports) {
			try {
				provideReportDtl(reportCacheDto);
			} catch (InterruptedException e) {
				logger.error("Exception occured in reportCacheProvider() : ", e);
			}
		}
	}
	
	public void provideReportDtl(ReportCacheDto reportCacheDto) throws InterruptedException{
		System.out.println("producer thread produce element ");
		blockingQueue.put(reportCacheDto);
	}
	
}

*//**
 * 
 * @author SMohapatra3
 * Consumer
 *//*
class ReportCacheConsumer implements Runnable{
	
	BlockingQueue<ReportCacheDto> blockingQueue = null;
	
	public ReportCacheConsumer(BlockingQueue<ReportCacheDto> blockingQueue) {
		this.blockingQueue = blockingQueue;
	}

	@Override
	public void run() {
		while(true){
			try {
				consumeReportDtl();
			} catch (InterruptedException e) {
				logger.error("Exception occured in reportCacheConsumer() : ", e);
			}
		}
	}
	
	public void consumeReportDtl() throws InterruptedException{
		System.out.println("producer thread produce element ");
		ReportCacheDto reportCacheDto = blockingQueue.take();
		loadReport(reportCacheDto);
	}
	
}*/
