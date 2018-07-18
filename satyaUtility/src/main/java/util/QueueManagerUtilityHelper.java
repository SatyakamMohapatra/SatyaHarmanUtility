/**
 * 
 */
package util;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import com.symphonyrpm.applayer.common.constants.QueueConstants;
import com.symphonyrpm.applayer.common.coreservices.AppLinkLogger;
import com.symphonyrpm.applayer.common.coreservices.IModules;
import com.symphonyrpm.applayer.common.coreservices.LogManager;
import com.symphonyrpm.applayer.common.dao.QueueUtilityDAO;
import com.symphonyrpm.applayer.common.exceptions.QueueException;
import com.symphonyrpm.applayer.common.queue.QueueTask;
/**
 * @author spankaja
 *
 */
public class QueueManagerUtilityHelper {
	protected static AppLinkLogger logger = LogManager.getLogger(IModules.SERVER, QueueManagerUtilityHelper.class.getName());

	public static TreeSet getAllQueueTasks(String userName, String orgName, String status, boolean isMultitanency, List organizations) throws Exception{
		List<QueueTask> tasks = new ArrayList <QueueTask> ();
		Set<QueueTask> queueTasks = null;
		List<QueueTask> historyTasks = new ArrayList <QueueTask> ();
		QueueUtilityDAO queueDAO = new QueueUtilityDAO();
		TreeSet<QueueTask> queueSet = new TreeSet<QueueTask>();	
		try {
			tasks = queueDAO.getAllTasks(userName, orgName, status);		
			if("all".equals(status) || QueueConstants.PROCESSED == Integer.parseInt(status)){
				historyTasks = queueDAO.getAllProcessedHistoryTasks(userName, orgName);
				tasks.addAll(historyTasks);
			}
			queueTasks = new HashSet<QueueTask>();
			for(QueueTask task : tasks){
				if(!queueTasks.contains(task)){
					queueTasks.add(task);
				}
			}
			queueSet.addAll(queueTasks);
			
			if (isMultitanency && organizations != null) {
				queueSet = filterQueuedTaskForUser(queueSet, organizations);
			}
		} catch (Exception e) {
			logger.debug("Exception in getAllQueueTasks method of QueueManagerUtilityHelper: "+e);
			throw e;
		}
		
		return queueSet;	
	}
	
	private static TreeSet<QueueTask> filterQueuedTaskForUser(TreeSet<QueueTask> queueSet, List organizations){
		Collection<QueueTask> queuedTasks = (Collection<QueueTask>) queueSet;
		TreeSet<QueueTask> tempQeuedTaskList = new TreeSet<QueueTask>();
		for(QueueTask task : queuedTasks){
			String org = task.getOrgName();
			if (organizations != null && org != null && organizations.toString().toLowerCase().indexOf(org.toLowerCase()) > 0) {
				tempQeuedTaskList.add(task);
			}
		}
		return tempQeuedTaskList;
	}
	public static TreeSet getAllQueueTasksByUser(String userId){
		List<QueueTask> tasks = new ArrayList <QueueTask> ();
		QueueUtilityDAO queueDAO = new QueueUtilityDAO();
		TreeSet<QueueTask> queueSet = new TreeSet<QueueTask>();	
		try {
			tasks = queueDAO.getAllTasksByUser(userId);					
			queueSet.addAll(tasks);	
		} catch (Exception e) {
			logger.debug("Exception in getAllQueueTasks method of QueueManagerUtilityHelper: "+e);
		}		
		return queueSet;	
	}
	

	public int updateQueueTaskPriority(int queueTaskId, int newPriority, String lastUpdBy, int oldPriority, int status) throws Exception {
		QueueUtilityDAO queueDAO = new QueueUtilityDAO();
		try {
			logger.info("<<ReportCacheUtility>>:updateQueueTaskPriority: queueTaskId: "+queueTaskId+ " newPriority: "+queueTaskId+ " lastUpdBy: "+lastUpdBy+ " oldPriority: "+oldPriority+ " status: "+status);
			return queueDAO.updateQueueTaskPriority(queueTaskId,newPriority,lastUpdBy,oldPriority,status);
		} catch (Exception e) {
			throw e;
		}
	}
	public int removeQueueTask(int queueTaskId, int reportTypeValue, String lastUpdBy) throws Exception {
		QueueUtilityDAO queueDAO = new QueueUtilityDAO();
		int i = 0;
		try {
			String count = queueDAO.isTaskOwner(lastUpdBy, queueTaskId);
			if (Integer.parseInt(count)<1 ) {
				throw new QueueException("User is not the owner of the Task : " + queueTaskId);
			}
			//Changing the status of the item in Queue_Master table
			queueDAO.updateQueueTaskStatus(queueTaskId,QueueConstants.CANCELLED, lastUpdBy, false);
			i = queueDAO.deleteQueueTaskItem(queueTaskId);
			if(i == 0){
				return i;
			}
			if(reportTypeValue == QueueConstants.DESKTOP_REFRESH){
				//remove from DR table and updating status
				queueDAO.deleteDesktopRefreshQueueTask(queueTaskId);
				queueDAO.updateDesktopRefreshStatusForCancelled(queueTaskId);
			}
			else {
				//remove from async table
				queueDAO.deleteAsyncReportQueueTask(queueTaskId);
			}
		} catch (Exception e) {
			throw e;
		}
		return i;
	}
	
	public int removeQueueTask(String reportId, String userId)throws Exception{
		logger.info("Report");
		QueueUtilityDAO queueDAO = new QueueUtilityDAO();
		queueDAO.updateQueueTaskStatus(reportId,QueueConstants.CANCELLED, userId);
		queueDAO.deleteQueueTaskStatus(reportId, userId);
		return 0;
	}
	public static Set getAllUsersFromQueueMaster() throws Exception {
		QueueUtilityDAO queueDAO = new QueueUtilityDAO();
		try {
			return queueDAO.getAllUsersFromQueueMaster();
		} catch (Exception e) {
			throw e;
		}
	}
	
	public static Set getAllUsersFromQueueMasterHistory() throws Exception {
		QueueUtilityDAO queueDAO = new QueueUtilityDAO();
		try {
			return queueDAO.getAllUsersFromQueueMasterHistory();
		} catch (Exception e) {
			throw e;
		}
	}
	

	public static List getAllOrgsFromQueueMaster() throws Exception {
		QueueUtilityDAO queueDAO = new QueueUtilityDAO();
		try {
			return queueDAO.getAllOrgsFromQueueMaster();
		} catch (Exception e) {
			throw e;
		}
	}
	
	public static List getAllOrgsFromQueueMasterHistory() throws Exception {
		QueueUtilityDAO queueDAO = new QueueUtilityDAO();
		try {
			return queueDAO.getAllOrgsFromQueueMasterHistory();
		} catch (Exception e) {
			throw e;
		}
	}
	 /**
	  * removeReportQueue with threshold more then 2000000000
	  * @return
	  */
	public Integer removeReportQueue() {
		logger.info("inside ReportCacheUtility:removeReportQueue :- updating status of queued report to cancelled and Deleting");
		QueueUtilityDAO queueDAO = new QueueUtilityDAO();
		try {
			queueDAO.updateQueueTaskStatus(QueueConstants.CANCELLED,QueueConstants.DELETE_QUEUE_ID_THRESHOLD);
			queueDAO.deleteQueueTaskStatus(QueueConstants.DELETE_QUEUE_ID_THRESHOLD);
			return 0;
			} catch (Exception e) {
				logger.error("ReportCacheUtility:removeReportQueue :- Error while updating status of queued report to cancelled and Deleting");
				throw e;
		}
		
	}
}
