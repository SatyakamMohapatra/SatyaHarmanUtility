package util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.StringTokenizer;

import com.symphonyrpm.applayer.common.coreservices.AppLinkLogger;
import com.symphonyrpm.applayer.common.coreservices.IModules;
import com.symphonyrpm.applayer.common.coreservices.LogManager;


public class CmdLineUtil {
	private static AppLinkLogger logger = LogManager.getLogger(IModules.SERVER, CmdLineUtil.class.getName());
	public CmdLineUtil() {

	}
	
	public static void main(String as[]){
	}
	
	public static void terminatePort(int portNo){
		try{
			String osName = System.getProperty("os.name");
			osName = osName.toLowerCase();
			
			
			String pid = getPIDRunningOnPort(String.valueOf(portNo),osName);
			logger.info(">>PortNo:\t"+portNo+"\tassociate with PID:\t"+pid);

			if(pid !=null){
				killProcess(osName, pid);
			}
		}catch(Exception exception){
			logger.error(">>Error while terminate Socket Inside PhantomSocket \t dispose:",exception);
		}
	}
	
	public static void terminatePhantomPort(int portNo){
		try{
			String osName = System.getProperty("os.name");
			osName = osName.toLowerCase();
			
			String pid = getPIDRunningOnPort(String.valueOf(portNo),osName);
			logger.info(">>PortNo:\t"+portNo+"\tassociate with PID:\t"+pid);
			
			if(pid !=null){
				boolean isPIDBinded = isPhantomRunningOnPID(pid,osName);
				if(isPIDBinded){
					killProcess(osName, pid);
				}
			}
		}catch(Exception exception){
			logger.error(">>Error while terminate Socket Inside PhantomSocket \t dispose:",exception);
		}
	}
	
	public static List<String> getAllPhantomRunningPID(String osName){
		List<String> list = new ArrayList<String>();
		
		BufferedReader  input = null; Process process = null ;
		try{
			String command = null;

			if(osName.toLowerCase().contains("windows")) {
				command = "TASKLIST /FI \"IMAGENAME eq phantomjs.exe\"";
			} else {
				command = "ps -A | grep \"phantom*\"";
			}

			String line;
			process = Runtime.getRuntime().exec(command);
			input = new BufferedReader(new InputStreamReader(process.getInputStream()));
			while ((line = input.readLine()) != null) {
				if(line != null && line.toLowerCase().contains("phantomjs")) {
					String [] words = split(line, " ", true);
					logger.info(">>"+Arrays.toString(words));
					if(osName.toLowerCase().contains("windows")) {
						list.add(words[1].trim());
					}else{
						//need to handle for Linux
					}
					
				}
			}
		}catch(Exception exception){
			logger.error(">>Error while getting phantomjs.exe running on PIDs:",exception);
		}finally{
			if(input !=null){
				try {
					input.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			if(process !=null){
				process.destroy();
			}
		}
		return list;
	}
	
	public static boolean isPhantomRunningOnPID(String pid,String osName){
		BufferedReader  input = null; Process process = null ;
		try{
			String command = null;

			if(osName.toLowerCase().contains("windows")) {
				command = "TASKLIST /FI \"IMAGENAME eq phantomjs.exe\"";
			} else {
				command = "ps -A | grep \"phantom*\"";
			}

			String line;
			process = Runtime.getRuntime().exec(command);
			input = new BufferedReader(new InputStreamReader(process.getInputStream()));
			while ((line = input.readLine()) != null) {
				if(line != null && line.toLowerCase().contains("phantomjs") && line.contains(pid)) {
					String [] words = split(line, " ", true);
					logger.info(">>PID:\t"+pid+"\tBinded to phantomjs\t"+Arrays.toString(words));
					return true ;
				}
			}
		}catch(Exception exception){
			logger.error(">>Error while validate phantomjs.exe running on PID:\t"+pid,exception);
		}finally{
			if(input !=null){
				try {
					input.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			if(process !=null){
				process.destroy();
			}
		}
		return false;
	}

	public static void killAllProcess(String osName, List<String> pids){
		for (String pid : pids) {
			killProcess(osName, pid);
		}
	}
	
	public static void killProcess(String osName, String pid){
		BufferedReader input = null;Process process = null ;
		try{
			String command = null;
			if(osName.toLowerCase().contains("windows")) {
				command = "Taskkill /PID "+pid + " /F";
			} else {
				command = "kill -9 "+pid;
			}
			logger.info(">>Kill process!! On Command:\t"+command);

			String line;
			process = Runtime.getRuntime().exec(command);
			input = new BufferedReader(new InputStreamReader(process.getInputStream()));
			while ((line = input.readLine()) != null) {
				logger.info(">>"+line);
			}
		}catch(Exception exception){
			logger.error(">>Error while kill PID:\t"+pid,exception);
		}finally{
			if(input !=null){
				try {
					input.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			if(process !=null){
				process.destroy();
			}
		}
	}

	public static String getPIDRunningOnPort(String port,String osName){
		String pid = null; BufferedReader input = null; Process process = null ;
		try{
			String command = null;
			if(osName.toLowerCase().contains("windows")) {
				command = "cmd.exe /C netstat -a -n -o | findstr :"+port ;
			} else {
				command = "netstat -nlp | grep :"+port;//command for linux OS
			}

			String line;
			process = Runtime.getRuntime().exec(command);
			input = new BufferedReader(new InputStreamReader(process.getInputStream()));
			while ((line = input.readLine()) != null) {
				if(line.contains(port)){
					String words[] = split(line, " ", true);
					logger.info(">>Found PID running on Post:\t"+port+"\t"+Arrays.toString(words));
					if(osName.toLowerCase().contains("windows")) {
						pid = words[words.length-1];
					}else{
						String pidWord = words[words.length-1];
						pid = split(pidWord, "/", true)[0];
					}
					break;
				}
			}
		}catch(Exception exception){
			logger.error(">>Error while get PID running on port "+port,exception);
		}finally{
			if(input !=null){
				try {
					input.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			if(process !=null){
				process.destroy();
			}
		}
		return pid;
	}
	
	static String[] split(String str, String delim, boolean trim){
	    StringTokenizer stok = new StringTokenizer(str, delim);
	    String tokens[] = new String[stok.countTokens()];
	   
	    for(int i=0; i<tokens.length; i++){
	        tokens[i] = stok.nextToken();
	        if(trim){
	            tokens[i] = tokens[i].trim();
	        }
	    }
	    return tokens;
	}
}
