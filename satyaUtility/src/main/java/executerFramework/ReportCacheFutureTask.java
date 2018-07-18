package executerFramework;

import java.util.Date;
import java.util.concurrent.FutureTask;


public class ReportCacheFutureTask  extends FutureTask<ReportCacheFutureTask> implements Comparable<ReportCacheFutureTask> {

	private  ReportCacheDTO task = null;

	public  ReportCacheFutureTask(ReportCacheDTO task){
		super(task,null);
		this.task = task;
	}


	@Override
	public int compareTo(ReportCacheFutureTask o) {
		int view1 = task.getViews();
		Date date1 = o.task.getLastexecuted();
		int view2 = task.getViews();
		Date date2 = o.task.getLastexecuted();
		if(date1==null||date2==null){
			return -1;
		}
		if(view1 < view2){
			return 1;
		}else if(view1 > view2){
			return -1;
		}else{
			if(-date1.compareTo(date2) == 0){
				return -1;
			}else{
				return -date1.compareTo(date2);
			}
		}
	}

}