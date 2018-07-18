package util;

import java.sql.Connection;

import com.symphonyrpm.applayer.common.constants.ConfigurationConstants;
import com.symphonyrpm.applayer.common.coreservices.AppLinkLogger;
import com.symphonyrpm.applayer.common.coreservices.ConfigManager;
import com.symphonyrpm.applayer.common.coreservices.DBConnectionUtility;
import com.symphonyrpm.applayer.common.coreservices.IModules;
import com.symphonyrpm.applayer.common.coreservices.LogManager;
import com.symphonyrpm.applayer.common.dao.PreparedStatementLogable;
import com.symphonyrpm.applayer.common.dto.MigrationStatusDTO;

public class UserMigrationLogUtil implements Runnable{
	private AppLinkLogger logger = LogManager.getLogger(IModules.SERVER, this.getClass().getName());
	MigrationStatusDTO status = null;
	String datasource = null;
	Connection con = null;
	public UserMigrationLogUtil(MigrationStatusDTO status){
		this.status = status;
	}
	public void run() {
		try{
		 datasource = ConfigManager.getInstance().getProperty(ConfigurationConstants.SYSTEM_PROPERTIES_FILE_NAME, ConfigurationConstants.DATA_SOURCE_PROP_NAME);
         con = DBConnectionUtility.getInstance().getConnection(datasource);
         String sql = "INSERT INTO USER_MIGRATION_LOGS ( MIGRATION_ID, MODULE_NAME, SOURCE_USER, TARGET_USER, MIGRATED_BY, STATUS) VALUES (?,?,?,?,?,?)";
         PreparedStatementLogable psl = new PreparedStatementLogable(con, sql);
         psl.setInt(1, status.getMigrationId());
         psl.setString(2, status.getModuleName());
         psl.setString(3, status.getSourceUser());
         psl.setString(4, status.getTargetUser());
         psl.setString(5, status.getMigrationBy());
         psl.setString(6, status.getFinalStatus());
         psl.executeUpdate();
         
		}catch(Exception e){
			logger.error("Error occured while logging migration details");
		}
		finally{
			try
            {
               if(!con.isClosed()){
            	   con.close();
               }

            }
            catch (Exception e)
            {
                logger.error("Exception in WorkspaceDAO.createWorkspace finally block  : ", e);
            }
		}
	}
}
