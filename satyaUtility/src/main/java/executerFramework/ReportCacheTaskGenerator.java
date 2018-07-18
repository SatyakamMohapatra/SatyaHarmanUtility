package executerFramework;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.TreeSet;

import com.symphonyrpm.applayer.common.coreservices.AppLinkLogger;
import com.symphonyrpm.applayer.common.coreservices.IModules;
import com.symphonyrpm.applayer.common.coreservices.LogManager;

public class ReportCacheTaskGenerator {
	
	private static AppLinkLogger logger = LogManager.getLogger(IModules.SERVER, ReportCacheTaskGenerator.class.getName());
	//private static String URL_PATH = ConfigManager.getInstance().getPropertyAsString(ConfigurationConstants.RPM_PROPERTIES_FILE_NAME,ConfigurationConstants.LD_SERVICES_INTERNAL_URL_DR, "LDServicesInternalUrl");
	
	/**
	 * @status Under Development
	 * @author SMohapatra3
	 * @since UNIFY 3.3
	 */
	public String generateRptCache(String modelName,Date dataLoadDate){
		logger.info("<<ReportCacheUtility>>  generateRptCache Starting cache Process for Cube id: "+modelName+" ...");
		ReportCacheDAO reportCacheDAO = null;
		TreeSet<ReportCacheDTO> sortedReports = null;
		Date dayAfterDataLoadDate = null;
		ReportCacheExecutor cacheExecutor = new ReportCacheExecutor();
		try{
		
			Calendar calendar = Calendar.getInstance();
			calendar.setTime(dataLoadDate);
			int dataLoadDay = calendar.get(Calendar.DATE);
			calendar.set(Calendar.DATE, dataLoadDay+1);
			dayAfterDataLoadDate =  calendar.getTime();
			
			reportCacheDAO = new ReportCacheDAO();
			
			/*
			 * Get All Report From DB For Particular modelID
			 */
			logger.info("<<ReportCacheUtility>>  generateRptCache:modelName::  "+modelName+" Starting cache Get All Report From DB For Particular modelName: "+modelName+" ...");
			sortedReports = reportCacheDAO.getRptWithModelInfoFromUsages(modelName,dataLoadDate,dayAfterDataLoadDate);
			
			List<ReportCacheDTO> reports=  new ArrayList<ReportCacheDTO>(sortedReports);
			
			//String[] storyReportIds =  getStoryReportID(reports.size(),modelName);
		/*	int counter =0;
			for (ReportCacheDTO reportCacheDto : reports) {
				reportCacheDto.setStoryReportId(storyReportIds[counter].replaceAll("\"", ""));
				counter++;
			}*/
			

			logger.info("++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++");
			logger.info("<<ReportCacheUtility>>  generateRptCache:modelName::  "+modelName+" End of generating info for report to be processed for"
					+ " modelName : "+modelName+" ..."+ " total " /*+storyReportIds.length*/ +" report to be processed" );
			logger.info("++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++");
			cacheExecutor.runReportCache(reports,modelName);
			
			
		} catch (Exception e) {
			logger.error("<<ReportCacheUtility>>  generateRptCache:modelName::  "+modelName+" Exception occured in generateRptCache() : ", e);
		}
		return null;
	}
	
/*	*//**
	 * 
	 * @param count
	 * @return
	 *//*
	@Deprecated
	private String[] getStoryReportID(int count,String modelName){
		logger.info("<<ReportCacheUtility>>:getStoryReportID:modelName::"+modelName+" inside getStoryReportID collecting "+count+" no of StoryReportID");
		HttpURLConnection urlconnection = null;
		String[] StoryReportIDs =null;
		
		try {
			
			//URL_PATH= "http://lnx1145.ch3.qa.i.com:9009/client1-ld";
			URL url = new URL(URL_PATH + "/Utility/getStoryReportIds/"+count);
			urlconnection = (HttpURLConnection) url.openConnection();
			urlconnection.setRequestMethod("GET");
			urlconnection.setReadTimeout(0);
			
			// Get Response
			InputStream is = urlconnection.getInputStream();
			BufferedReader rd = new BufferedReader(new InputStreamReader(is));
			String line;
			StringBuffer response = new StringBuffer();
			while ((line = rd.readLine()) != null) {
			    response.append(line);
			    response.append('\r');
			}
			rd.close();
			String responseString = response.toString();
			responseString = responseString.substring(responseString.indexOf("[")+1,responseString.indexOf("]"));
			StoryReportIDs = responseString.toString().split(",");
			logger.info("<<ReportCacheUtility>>:getStoryReportID:modelName::"+modelName+" Ended collecting"+count+" no of StoryReportID");
		} catch (MalformedURLException e) {
			logger.error("<<ReportCacheUtility>>:getStoryReportID:modelName::"+modelName+" Unable to connect to URL to getStoryReportID", null, e);
		} catch (IOException e) {
			logger.error("<<ReportCacheUtility>>:getStoryReportID:modelName::"+modelName+" Unable to connect to URL to getStoryReportID", null, e);
		} finally {
			if (urlconnection != null) {
				urlconnection.disconnect();
			}
		}
		return StoryReportIDs;
	}
	*/
	public static Integer getCacheReportCount(String cubeName,Date dataLoadDate) {
		logger.info("<<ReportCacheUtility>>:getCacheReportCount inside getCacheReportCount cubeName: "+cubeName+ " dataLoadDate : "+dataLoadDate);
		ReportCacheDAO reportCacheDAO = null;
		Integer count = null;
		Date dayAfterDataLoadDate = null;
		try {
			Calendar calendar = Calendar.getInstance();
			calendar.setTime(dataLoadDate);
			int dataLoadDay = calendar.get(Calendar.DATE);
			calendar.set(Calendar.DATE, dataLoadDay+1);
			dayAfterDataLoadDate =  calendar.getTime();
			reportCacheDAO = new ReportCacheDAO();
			count = reportCacheDAO.getReportCacheCount(cubeName,dataLoadDate,dayAfterDataLoadDate);
			logger.info("<<ReportCacheUtility>>:getCacheReportCount report count: "+count);
		} catch (SQLException e) {
			logger.error("<<ReportCacheUtility>>:getCacheReportCount Error in fatching getCacheReportCount "+ e.getMessage(), e);
		}
		return count;
	}

}


