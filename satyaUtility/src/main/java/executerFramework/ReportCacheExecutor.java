package executerFramework;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.List;
import java.util.concurrent.ThreadPoolExecutor;


public class ReportCacheExecutor {
	
	//private static AppLinkLogger logger = LogManager.getLogger(IModules.SERVER, ReportCacheExecutor.class.getName());
	//private static String URL_PATH = ConfigManager.getInstance().getPropertyAsString(ConfigurationConstants.RPM_PROPERTIES_FILE_NAME,ConfigurationConstants.LD_SERVICES_INTERNAL_URL_DR, "LDServicesInternalUrl");
	
	/**
	 * @author SMohapatra3
	 * Run runReportCache
	 */
/*	public void runReportCache(List<ReportCacheDTO> reports,final String modelName) {
		ThreadPoolExecutor executor = ReportCacheExecutorQueue.getInstance();
		for (ReportCacheDTO reportCacheDto : reports) {
			logger.info("<<ReportCacheUtility>>:runReportCache:modelName:: "+modelName+" submiting jop For "+reportCacheDto);
			executor.execute(new ReportCacheFutureTask(reportCacheDto));
		}
	}*/

	
/*	*//**
	 * @author SMohapatra3
	 * @param wspID
	 * @param orgName
	 * @param loadOptions
	 * @return
	 *//*
	private String loadReport(String wspID,String orgName,String loadOptions,int views,String modelName,String reportId,String reportName,String appName) {
		logger.info("<<ReportCacheUtility>>:loadReport:modelName::"+modelName+" :reportId:: "+wspID+" Starting loadReport Service call for orgName:: "+orgName);
		HttpURLConnection urlconnection = null;

		DataOutputStream wr = null;
		InputStream is  = null;
		BufferedReader rd  = null;
		String status = null;
		String message = null;
		int queueTaskId = 0;
		int oldPriority = 0;
		int newPriority = 0;
		String lastUpdBy = null;
		JSONObject queueStatusJson = null;
		try {
			String urlParameters = loadOptions;
			if(StringUtil.isNotEmpty(orgName)) {
				orgName = URLEncoder.encode(orgName, "UTF-8");
			}
			//client1-ld/Utility/loadReport/21590dcb16fd89c4:59d453d7:15ce5e26b0c:-7d41/perrigo_ld
			//URL_PATH= "http://win0246.ch3.dev.i.com:9088/LDServices_AppSrv09";
			//URL_PATH= "http://lnx1145.ch3.qa.i.com:9009/client1-ld";
			//URL_PATH= "http://localhost:8089/LDServices";
			//URL url = new URL(URL_PATH + "/Utility/loadReport/" + wspID +"/"+orgName);
			URL url = new URL(URL_PATH + "/Utility/loadReport/" + wspID +"/"+orgName+"/"+"true"+"/"+"true");
			logger.info("<<ReportCacheUtility>>:loadReport:modelName::"+modelName+" wspID ::"+wspID+" Calling POST request on :- "+URL_PATH + "/Utility/loadReport/" + wspID +"/"+orgName);

			urlconnection = (HttpURLConnection) url.openConnection();
			urlconnection.setRequestMethod("POST");
			urlconnection.setRequestProperty("Content-Type", "text/plain");
			urlconnection.setRequestProperty("Content-Length", "" + Integer.toString(urlParameters.getBytes().length));
			urlconnection.setUseCaches(false);
			urlconnection.setDoInput(true);
			urlconnection.setDoOutput(true);
			wr = new DataOutputStream(urlconnection.getOutputStream());
			wr.writeBytes(urlParameters);
			wr.flush();
			wr.close();

			is = urlconnection.getInputStream();
			rd = new BufferedReader(new InputStreamReader(is));

			String line;
			StringBuffer response = new StringBuffer();
			while ((line = rd.readLine()) != null) {
				response.append(line);
				response.append('\r');
			}
			//logger.info("<<ReportCacheUtility>>:loadReport:modelName::"+modelName+" :reportId::"+reportId+" inside loadReport Service call for method reportId:- " +reportId+" orgName:- "+orgName+" QUEUE Status "+response.toString());
			logger.info("<<ReportCacheUtility>>:loadReport:response::"+response.toString()); 
			String responseString = null;
			
			if(response!=null && response.length()>0){
				responseString = response.toString();
			}else{
				throw new Exception("<<ReportCacheUtility>>:loadReport:response Object can not be null ");
			}
			
			QueueManagerUtilityHelper queueManagerUtility;
			if (responseString!=null && responseString.length()>0) {
				
				lastUpdBy = null;
				int queueId = 0;
				int code = 0;
				JSONObject jsObj = new JSONObject(responseString);
				queueStatusJson = jsObj.optJSONObject("queueStatus");
				if(queueStatusJson!=null){
					queueId = queueStatusJson.optInt("queueId");
					message = queueStatusJson.optString("message");
					code = queueStatusJson.optInt("code");
				}else{
					code = 3;
				}
				
				JSONObject metaDataJson =jsObj.optJSONObject("metadata");
				if(metaDataJson!=null){
					lastUpdBy = metaDataJson.optString("lastUpdatedBy");
				}
				
				if (code == QueueConstants.FAILED) {
					String errMessage = queueStatusJson.optString("errorMessage");
					logger.error("<<ReportCacheUtility>>:loadReportResult:modelName:: "+modelName+" :: wspID:- "+wspID+" :: reportId:- "+reportId+" :: reportName:- "+reportName+" :: appName:- "+appName+" :: message:- "+message+" :: errorMsg:- "+errMessage+" :: errorCode:- "+code+" Cache Process got failed as queue status is FAILED");
					return "FAILED";
				}
				queueManagerUtility = new QueueManagerUtilityHelper();
				oldPriority = 0;
				newPriority = 0;
				queueTaskId = 0;
				if (queueId > 0) {
					oldPriority = queueId;
					queueTaskId = queueId;
					if (oldPriority > 0) {
						if (views > 0) {
							newPriority = Integer.MAX_VALUE - views;
						} else {
							newPriority = Integer.MAX_VALUE;
						}
					}
				}
				//reportId :: orgName :: views :: modelName :: queueTaskId :: oldPriority :: newPriority :: lastUpdBy
				int queueStatus = 0;
				if(queueId > 0){
					queueStatus =  queueManagerUtility.updateQueueTaskPriority(queueTaskId, newPriority, lastUpdBy, oldPriority, QueueConstants.QUEUED);
				}
				if(queueStatus == 0 ){
					logger.info("<<ReportCacheUtility>>:loadReportResult:modelName :- "+modelName+" :: wspID:- "+wspID+" :: reportId:- "+reportId+" :: reportName:- "+reportName+" :: appName:- "+appName+" :: orgName:- "+orgName+" :: views:- "+views+" :: QueueStatus:- "+"PROCESSING"+" :: message:- "+message+" :: queueTaskId:- "+queueTaskId+" :: oldPriority:- "+oldPriority+" :: newPriority:- Not Changed :: lastUpdBy:- "+lastUpdBy+" :: status :-SUCCESS Not Changed failed in updating of the Priority");
				}else{
					logger.info("<<ReportCacheUtility>>:loadReportResult:modelName :- "+modelName+" :: wspID:- "+wspID+" :: reportId:- "+reportId+" :: reportName:- "+reportName+" :: appName:- "+appName+" :: orgName:- "+orgName+" :: views:- "+views+" :: QueueStatus:- "+"QUEUED"+" :: message:- "+message+" :: queueTaskId:- "+queueTaskId+" :: oldPriority:- "+oldPriority+" :: newPriority:- "+newPriority+" :: lastUpdBy:- "+lastUpdBy+" :: status :-SUCCESS successfully updated the Priority");
				}
				status =  "SUCCESS";
			}else{
				logger.error("<<ReportCacheUtility>>:loadReportResult:modelName::"+modelName+" :wspID::"+wspID+" :: reportId:- "+reportId+" :: reportName:- "+reportName+" :: appName:- "+appName+" :: status :-FAILED invalid response: "+response);
				status =  "FAILED";
			}

		} catch (MalformedURLException em) {
			logger.error("<<ReportCacheUtility>>:loadReportResult:modelName::"+modelName+" :wspID::"+wspID+" :: reportId:- "+reportId+" :: reportName:- "+reportName+" :: appName:- "+appName+" Service Unable to connect to URL to load report", null, em);
			status =  "FAILED";
		} catch (IOException ei) {
			logger.error("<<ReportCacheUtility>>:loadReportResult:modelName::"+modelName+" :: wspID:- "+wspID+" :reportId::"+reportId+" :: reportName:- "+reportName+" :: appName:- "+appName, null, ei);
			status =  "FAILED";
		} catch (Exception ex) {
			logger.error("<<ReportCacheUtility>>:loadReportResult:modelName::"+modelName+" :: wspID:- "+wspID+" :reportId::"+reportId+" :: reportName:- "+reportName+" :: appName:- "+appName, null, ex);
			status =  "FAILED";
		} 
		finally {
			if (urlconnection != null) {
				urlconnection.disconnect();
			}
			try {
				if(rd!=null){
					rd.close();
				}
				if(is!=null){
					is.close();			
				}
				if(wr!=null){
					wr.close();
				}
			} catch (IOException e) {
				logger.error("<<ReportCacheUtility>>:loadReport:modelName::"+modelName+" :wspID::"+wspID+" :reportId::"+reportId+" while closing streams", null, e);
			}
		}
		return status;
	}*/
}
