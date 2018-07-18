/*
 * Created on Sep 21, 2007
 */
package util;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.StringTokenizer;

import com.symphonyrpm.applayer.common.coreservices.AppLinkLogger;
import com.symphonyrpm.applayer.common.coreservices.IModules;
import com.symphonyrpm.applayer.common.coreservices.LogManager;
/**
 *
 */
public class FileUtil {

	private int fileDeletedCount = 0;
	private static AppLinkLogger logger = LogManager.getLogger(IModules.SERVER,CommonUtility.class.getName());

	public static void copy(File src, File dest) throws IOException {
		InputStream in = null;
		OutputStream out = null;

		try {
			in = new BufferedInputStream(new FileInputStream(src));
			out = new BufferedOutputStream(new FileOutputStream(dest));

			byte[] bytes = new byte[2048];
			int len = 0;
			while( (len = in.read(bytes)) > 0) {
				out.write(bytes, 0, len);
			}

			out.flush();
		} 
		finally {
			if (in != null) {
				in.close();
			}
			if (out != null) {
				out.close();
			}
		}
	}

	public static String changeFilePathBasedOnPlatform(String filePath)
	{
		if ((System.getProperty("os.name").indexOf("Sun"))>=0)
		{
			StringTokenizer filePathStrToken = new StringTokenizer(filePath,"\\");
			filePath = "";
			while (filePathStrToken.hasMoreTokens()) {
				filePath = filePath+ File.separator + filePathStrToken.nextToken() ;
			}
		}
		return filePath;
	}

	public void purgeDirectory(File dir) {
		for (File file: dir.listFiles()) {
			if (file.isDirectory()) {
				purgeDirectory(file);
			}
			file.delete();
			fileDeletedCount ++;
		}
	}
	
	public boolean renameFile(File file,String newName){
		boolean result = false;
		try{
			File newFile =  new File(file.getParentFile(),newName);
			return file.renameTo(newFile);
		}
		catch(Exception ex){}
		return result;
	}

	public int getFileDeletedCount() {
		return fileDeletedCount;
	}
	
	public static void writeToFileFromStream(InputStream uploadedInputStream,String uploadedFileLocation) throws Exception{
		OutputStream out = null;

		try {
			out = new FileOutputStream(new File(uploadedFileLocation));
			int read = 0;
			byte[] bytes = new byte[1024];

			out = new FileOutputStream(new File(uploadedFileLocation));
			while ((read = uploadedInputStream.read(bytes)) != -1) {
				out.write(bytes, 0, read);
			}
		} catch (IOException e) {
			logger.error("Error occurred while writring file to the loaction " + uploadedFileLocation,e);
			throw e;
		}
		finally{
			try {
				out.flush();
				out.close();
			} catch (IOException e) {
				logger.error("Error occurred while closing Streams",e);
			}
		}

	}
	
}
