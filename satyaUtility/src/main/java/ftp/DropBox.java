package ftp;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import com.dropbox.core.DbxApiException;
import com.dropbox.core.DbxDownloader;
import com.dropbox.core.DbxException;
import com.dropbox.core.DbxRequestConfig;
import com.dropbox.core.v2.DbxClientV2;
import com.dropbox.core.v2.files.DownloadErrorException;
import com.dropbox.core.v2.files.FileMetadata;
import com.dropbox.core.v2.files.ListFolderContinueErrorException;
import com.dropbox.core.v2.files.ListFolderResult;
import com.dropbox.core.v2.files.Metadata;
import com.dropbox.core.v2.files.UploadErrorException;
import com.dropbox.core.v2.users.FullAccount;

public class DropBox {
	private static final String ACCESS_TOKEN = "E6OyqVRaDVAAAAAAAAAACOuNa64qducK4UdXyzdBzQdjTnanp1J1uaOxkKD2PHQe";
	private static DbxClientV2 client;
	static{
		DbxRequestConfig config = new DbxRequestConfig("dropbox/java-tutorial");
	    client = new DbxClientV2(config, ACCESS_TOKEN);
	}
	
	public static void main(String[] args) {
	    // Create Dropbox client
		DbxRequestConfig config = DbxRequestConfig.newBuilder("dropbox/java-tutorial")
						.withUserLocale("en_US")
						.build();
        DbxClientV2 client = new DbxClientV2(config, ACCESS_TOKEN);
        try {
			testConnection(client);
		} catch (DbxApiException e) {
			e.printStackTrace();
		} catch (DbxException e) {
			e.printStackTrace();
		}
	}
	

	/**
	 * @param client
	 * @throws DbxApiException
	 * @throws DbxException
	 */
	public static void testConnection(DbxClientV2 client)
			throws DbxApiException, DbxException {
		FullAccount account = client.users().getCurrentAccount();
        System.out.println(account.getName().getDisplayName());
	}
	
	// Create Dropbox client
	private DbxClientV2 getDropBoxClient(){
		DbxRequestConfig config = new DbxRequestConfig("dropbox/java-tutorial");
	    DbxClientV2 client = new DbxClientV2(config, ACCESS_TOKEN);
	    return client;
	}
	
	// Get current account info
	private String getAccountInfo() throws DbxApiException, DbxException{
		FullAccount account = client.users().getCurrentAccount();
	    return account.getName().getDisplayName();
	}	
	
	// Get files and folder metadata from Dropbox root directory
	private ListFolderResult getFilesAndFolderInfo() throws ListFolderContinueErrorException, DbxException{
		ListFolderResult result = client.files().listFolder("");
		while (true) {
			for (Metadata metadata : result.getEntries()) {
				System.out.println(metadata.getPathLower());
			}

			if (!result.getHasMore()) {
				break;
			}

			result = client.files().listFolderContinue(result.getCursor());
		}
		return result;
	}
	
	// Upload "test.txt" to Dropbox
	private void upload(String path) throws FileNotFoundException, IOException, UploadErrorException, DbxException{
		path = "test.txt";
		try (InputStream in = new FileInputStream(path)) {
	        FileMetadata metadata = client.files().uploadBuilder("/test.txt")
	            .uploadAndFinish(in);
	    }
	}
	
	// Download "test.txt" from Dropbox
	private void downlode(String path) throws IOException, DownloadErrorException, DbxException{
		path = "test.txt";
		DbxDownloader<FileMetadata> downloader = client.files().download(path);
		try {
	        FileOutputStream out = new FileOutputStream("test.txt");
	        downloader.download(out);
	        out.close();
	    } catch (DbxException ex) {
	        System.out.println(ex.getMessage());
	    }
	}
}
