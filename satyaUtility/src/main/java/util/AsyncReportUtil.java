package util;



import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import com.symphonyrpm.applayer.common.coreservices.AppLinkLogger;
import com.symphonyrpm.applayer.common.coreservices.IModules;
import com.symphonyrpm.applayer.common.coreservices.LogManager;
import com.symphonyrpm.applayer.common.queue.AsyncReportDTO;

public class AsyncReportUtil {

	private static AppLinkLogger logger = LogManager.getLogger(IModules.SERVER, AsyncReportUtil.class.getName());
	
	public static String printQueueInfo(AsyncReportDTO asynObjectDTO){
		String tracerString = "";
		if (asynObjectDTO != null){
			if(asynObjectDTO.getQueueId() > 0){
				tracerString += " Queue id : "+asynObjectDTO.getQueueId()+" ";
			}
			if(asynObjectDTO.getReportId() != null){
				tracerString += " Report id : "+asynObjectDTO.getReportId()+" ";
			}
			if(asynObjectDTO.getReportQuery() != null){
				String reportName = getQueryTracerString(asynObjectDTO.getReportQuery());
				tracerString += " of name : "+reportName+" ";
			}
		}
		return tracerString;
	}
	
	public static String getQueryTracerString(String query){
		String objectName = "";
		if (query != null){
			try {
				try {
					Document queryDoc = XmlUtility.parseFromString(query);
					NodeList xmlQueryList = queryDoc.getElementsByTagName("startQuery");
					if(xmlQueryList != null && xmlQueryList.getLength() > 0){
						Element startEle = (Element)xmlQueryList.item(0);
						objectName = startEle.getAttribute("tracerString");
					}
				} catch (Exception e) {
					logger.error("Error occured while geting the tracer string from query");
				}
			} catch (Exception e) {
				logger.error("Error occured while getting the tracer string from a query");
			}
		}
		return objectName;
	}
}
