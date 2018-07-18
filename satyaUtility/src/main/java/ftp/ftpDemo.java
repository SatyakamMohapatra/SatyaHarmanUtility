package ftp;

import java.io.FileInputStream;
import java.io.IOException;

import org.apache.commons.net.ftp.FTPClient;

public class ftpDemo {
	public static void main(String[] args) {
		putFileIntoFTP("");
	}

	public static void putFileIntoFTP(String json){
		FTPClient client = new FTPClient();
		FileInputStream fis = null;

		try {
			client.connect("win0246.ch3.dev.i.com");
			client.login("rpmaadmin", "RPM4:4dm1n","CH3PROD");
			//
			// Create an InputStream of the file to be uploaded
			//
			String filename = "D:\\SymphonyRPM\\FileRepository\\ScheduledReports2\\cl1\\26a0f27e7d25e1e4_-1dcf1fdd_16254948f01_-7a63.xlsx";
			fis = new FileInputStream(filename);
			client.storeFile(filename, fis);
			client.logout();
			
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				if (fis != null) {
					fis.close();
				}
				client.disconnect();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

}
