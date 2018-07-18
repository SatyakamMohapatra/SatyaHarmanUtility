package ftp;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;



public class TestDropBox {


	String URL_PATH = "https://advantage.iriworldwide.com/connectors_summit/transportFile";
	String connector_id ="ce186aab-bcdf-45c7-898b-3c9a858acb6e";

	public void adddToDropBox(File file){

		HttpURLConnection urlconnection = null;
		String boundaryString= "*******";

		try {

			String fileName =file.getName();
			String fileExtension ="";

			if(fileName.contains(".") && fileName.lastIndexOf(".")!= 0)
			{
				fileExtension=fileName.substring(fileName.lastIndexOf(".")+1);
				fileExtension = "."+fileExtension;
			}

			String URL_PARAMS ="?Transporter_Id="+connector_id+"&MIME_TYPE="+fileExtension+"&FILE_NAME="+file.getName();

			URL_PATH = URL_PATH+URL_PARAMS;

			System.out.println("URL "+ URL_PATH);

			URL connection = new URL(URL_PATH);
			urlconnection = (HttpURLConnection)connection.openConnection();
			urlconnection.setRequestMethod("POST");
			urlconnection.addRequestProperty("Content-Type", "multipart/form-data; boundary=" + boundaryString);
			urlconnection.setDoOutput(true);
			urlconnection.setConnectTimeout(10000);
			//urlconnection.connect();



			OutputStream outputStreamToRequestBody = urlconnection.getOutputStream();
			BufferedWriter httpRequestBodyWriter =
					new BufferedWriter(new OutputStreamWriter(outputStreamToRequestBody));

			httpRequestBodyWriter.write("\n--" + boundaryString + "\n");
			httpRequestBodyWriter.write("Content-Disposition: form-data;"
					+ "userfile=file;");


			// Write the actual file contents
			FileInputStream inputStreamToLogFile = new FileInputStream(file);

			int bytesRead;
			byte[] dataBuffer = new byte[1024];
			while((bytesRead = inputStreamToLogFile.read(dataBuffer)) != -1) {
				outputStreamToRequestBody.write(dataBuffer, 0, bytesRead);
			}

			outputStreamToRequestBody.flush();

			// Mark the end of the multipart http request
			httpRequestBodyWriter.write("\n--" + boundaryString + "--\n");
			httpRequestBodyWriter.flush();

			System.out.println(urlconnection.getResponseMessage());

			// Close the streams
			outputStreamToRequestBody.close();
			httpRequestBodyWriter.close();

		} catch (Exception e) {
			e.printStackTrace();
		}

		finally{
			System.out.println("test");
		}

	}




	public static void main(String[] args) {
		try{
			File file = new File("C:\\Users\\smohapatra3\\Downloads\\cl1\\Shared\\Save App.txt");

			TestDropBox test = new TestDropBox();
			test.adddToDropBox(file);
		}catch(Exception e){
			e.printStackTrace();
		}

	}

}

