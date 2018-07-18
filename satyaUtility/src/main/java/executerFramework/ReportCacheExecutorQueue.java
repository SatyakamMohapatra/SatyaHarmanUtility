package executerFramework;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.PriorityBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import com.symphonyrpm.applayer.common.constants.ConfigurationConstants;
import com.symphonyrpm.applayer.common.coreservices.AppLinkLogger;
import com.symphonyrpm.applayer.common.coreservices.ConfigManager;
import com.symphonyrpm.applayer.common.coreservices.IModules;
import com.symphonyrpm.applayer.common.coreservices.LogManager;


public final class ReportCacheExecutorQueue implements Serializable,Cloneable{
	
	private static final long serialVersionUID = 4945270167565360898L;
	private static AppLinkLogger logger = LogManager.getLogger(IModules.SERVER, ReportCacheExecutor.class.getName());
	private static int propCorePoolSize = ConfigManager.getInstance().getPropertyAsInt(ConfigurationConstants.RPM_PROPERTIES_FILE_NAME,ConfigurationConstants.REPORT_CACHE_CORE_POOLSIZE,5);
	private static int propMaximumPoolSize = ConfigManager.getInstance().getPropertyAsInt(ConfigurationConstants.RPM_PROPERTIES_FILE_NAME,ConfigurationConstants.REPORT_CACHE_MAXIMUM_POOLSIZE,50);
	private static int propKeepAliveTime = ConfigManager.getInstance().getPropertyAsInt(ConfigurationConstants.RPM_PROPERTIES_FILE_NAME,ConfigurationConstants.REPORT_CACHE_KEEPA_LIVE_TIME,0);
	private static int EXECUTATION_SHUTDOWN_TIME = ConfigManager.getInstance().getPropertyAsInt(ConfigurationConstants.RPM_PROPERTIES_FILE_NAME,ConfigurationConstants.EXECUTATION_SHUTDOWN_TIME,6);
	private static int EXECUTION_SHUTDOWN_TIME = ConfigManager.getInstance().getPropertyAsInt(ConfigurationConstants.RPM_PROPERTIES_FILE_NAME,ConfigurationConstants.EXECUTATION_SHUTDOWN_TIME,6);
	
	private static ThreadPoolExecutor executor = null;
	@SuppressWarnings("rawtypes")
	private static PriorityBlockingQueue blockingQueue;
	
	public ReportCacheExecutorQueue() {}
	
	protected ThreadPoolExecutor  readResolve() {
	    return getInstance();
	}
	
	@Override
	protected Object clone() throws CloneNotSupportedException {
		return getInstance();
	}
	
	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public static ThreadPoolExecutor getInstance(){
	    if(blockingQueue == null){
	        synchronized (ReportCacheExecutorQueue.class) {
	            if(blockingQueue == null){
	            	blockingQueue = new PriorityBlockingQueue();
	            	if(executor == null){
	            		int corePoolSize = propCorePoolSize;
		        		int maximumPoolSize = propMaximumPoolSize;
		        	    long keepAliveTime = propKeepAliveTime;
		            	executor = new ThreadPoolExecutor(corePoolSize,maximumPoolSize,keepAliveTime,TimeUnit.MILLISECONDS,blockingQueue);
		            	demonTimer();
	            	}
	            }
	        }
	    }
	    return executor;
	}
	
	public static int reset() throws InterruptedException{
		List<Runnable> list = new ArrayList<Runnable>();
		if(executor!=null){
			list.addAll(executor.shutdownNow());
			if(blockingQueue!=null){
				blockingQueue = null;
			}
			executor = null;
		}
		QueueManagerUtilityHelper queueManagerUtility = new QueueManagerUtilityHelper();
		queueManagerUtility.removeReportQueue();
		return list.size();
	}
	
	private static void demonTimer(){
		int shutDownTime = EXECUTION_SHUTDOWN_TIME;
		if(shutDownTime==0){
			shutDownTime = EXECUTATION_SHUTDOWN_TIME;
		}
		shutDownTime= 20;
		Timer timer = new Timer();
		Calendar today = Calendar.getInstance();
		int weekday = today.get(Calendar.DAY_OF_WEEK);
		if(weekday!=Calendar.MONDAY){
			int days =  (Calendar.SATURDAY-weekday + 2)%7;
			today.add(Calendar.DAY_OF_YEAR,days);
		}
		today.set(Calendar.HOUR_OF_DAY, shutDownTime);
		today.set(Calendar.MINUTE, 0);
		today.set(Calendar.SECOND, 0);
		timer.schedule(new TimerTask() {
			@Override
			public void run() {
				try {
					reset();
				} catch (InterruptedException e) {
					logger.error("<<ReportCacheUtility>>:ReportCacheExecutorQueue:: was forcely stoped as it max running time reached",e);
				}
			}
		},  today.getTime());
	}
}
