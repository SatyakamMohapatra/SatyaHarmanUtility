package util;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.io.StringReader;
import java.io.StringWriter;
import java.io.Writer;
import java.nio.charset.StandardCharsets;
import java.sql.Blob;
import java.sql.Clob;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Properties;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

//import com.google.gson.Gson;

public class DBTest{
	public static void main(String as[])throws Exception{
		Class.forName("oracle.jdbc.driver.OracleDriver");
		
		//Test
		/*String URL = "jdbc:oracle:thin:@//ex02-scan.ch3.prod.i.com:1521/SV01AVT";
		Properties props = new Properties();
		props.put("user", "VIS_AE_REP_T5");
		props.put("password", "Passw0rd");

		*/
		/*DEV - Unify DEV*/
		String URL = "jdbc:oracle:thin:@//ex02-scan.ch3.prod.i.com:1521/sv01dmt";
		Properties props = new Properties();
		
		//DEV - Unify DEV
		props.put("user", "VIS_AE_REP_T7");
		props.put("password", "Passw0rd");
		
		
		//DEV - Unify property
		/*props.put("user", "VIS_RPM_CFG_T1");
		props.put("password", "welcome1");*/
		
		//postgra
		//jdbc:postgresql://lnx0043.ch3.prod.i.com:5432/db_appsrepo_uat
		/*String URL = "jdbc:postgresql://10.106.9.117:5432/db_appsrepo_uat";
		Properties props = new Properties();
		props.put("user", "vis_rep_pepsicoext_p1");
		props.put("password", "Passw0rd");*/
		
		Connection con = DriverManager.getConnection(URL, props);
		String wspIds = "26a0f27e7d25e1e4:3f52f107:1626cdbb334:-7797"; //Unify
		
//		String wspIds = "039521bacc1c416b:519ea092:1594f519419:64a1"; //AP
		
		String paneType = "ChartBlox";
//		String paneType = "GridBlox";
//		String paneType = "DataBlox";
//		String paneType = "ValueConditionalFormatting";
//		String paneType = "MemberConditionalFormating";
		
//		selectQuery(con);
//		updateQuery(con);
		
		//selectAllPROPERTIES(con, wspIds);
		selectChartPROPERTIES(con,wspIds,paneType);
//		updateChartPROPERTIES(con, wspIds, paneType);
		
		//For Time ran
		//String modelName = "WBA_FSP_DEMO";;
		
		String AggID = "8";
		//String modelName = "WBA_FSP";
		String dim = "TXXX";
//		insertTimeArgPROPERTIES(con,AggID,modelName,dim);
//		SelectTimeArgPROPERTIES(con,modelName);
		
		System.out.println("OK");
	}

	private static void updateChartPROPERTIES(Connection con,String wspIds,String paneType)throws Exception {


		String select = "SELECT CP.PANE_ID FROM WSP_PANE_TBL PN ,CMN_REPORT_TBL RPT , WSP_PAGE_TBL P ,CMN_PANE_PROPS_TBL CP ,WSP_SUMM_TBL W WHERE PN.PAGE_ID = P.PAGE_ID AND W.WSP_ID IN (?) AND W.WSP_ID = P.WSP_ID AND PN.OBJECT_ID = RPT.REPORT_ID AND CP.PANE_ID = RPT.BLX_PROPS_ID AND CP.PANE_TYPE =?";
		PreparedStatement ps = con.prepareStatement(select);
		ps.setString(1, wspIds);
		ps.setString(2, paneType);

		ResultSet rs = ps.executeQuery();

		if(rs.next()){
			String chartMetaData = "D:/Eclipse_Workspace/TrunkUnify/RPMCommon/src/com/symphonyrpm/applayer/common/calc/ChartMetaData.xml";
			String paneId = rs.getString("PANE_ID");
			String update = "UPDATE CMN_PANE_PROPS_TBL SET PROPERTIES = ? WHERE PANE_ID = ? AND PANE_TYPE =?";
			
			PreparedStatement udpatePs = con.prepareStatement(update);
			byte[] outputArr = compressIt(read(chartMetaData));
			udpatePs.setBytes(1, outputArr);
			udpatePs.setString(2, paneId);
			udpatePs.setString(3, paneType);
			udpatePs.executeUpdate();
			
			System.out.println("Done");
		}
	}
	
	private static void insertTimeArgPROPERTIES(Connection con,String AggID,String modelName,String dim)throws Exception {
		StringBuffer TimeArgPROPERTIESData = new StringBuffer();
		for (int i = 0; i < 20000; i++) {
			TimeArgPROPERTIESData.append("1");
		}
		//Postgra
		//String Insert = "INSERT INTO vis_rep_pepsicoext_p1.\"VIS_MODEL_AGGREGATE_CONFIG\"(\"AGGREGATE_ID\", \"MODEL_NAME\", \"DIMENSION\", \"AGGREGATE_CONFIG\", \"IS_COMPARABLE\")VALUES (?, ?, ?, ?, ?)";
		//Oracle
		String Insert = "INSERT INTO VIS_MODEL_AGGREGATE_CONFIG (AGGREGATE_ID,MODEL_NAME,DIMENSION,AGGREGATE_CONFIG,IS_COMPARABLE) VALUES(?,?,?,?,?)";
		PreparedStatement ps = con.prepareStatement(Insert);
		ps.setInt(1, Integer.parseInt(AggID));
		ps.setString(2, modelName);
		ps.setString(3, dim);
		//String TimeArgPROPERTIESData = "C:/Users/smohapatra3/Desktop/Agrrigate/TSV_WB_SUMMIT_BLOB.txt";
		//String TimeArgPROPERTIESData = "C:/Users/smohapatra3/Desktop/Agrrigate/IRI_PNL_BLOB.txt";
		System.out.println(TimeArgPROPERTIESData);
		//String data = read(TimeArgPROPERTIESData.toString());
		ps.setString(4, TimeArgPROPERTIESData.toString());
		ps.setString(5, "true");
		ResultSet rs = ps.executeQuery();
		System.out.println("Done");
	}
	
	private static void SelectTimeArgPROPERTIES(Connection con,String modelName)throws Exception {

		//Postgra
		//String select = "SELECT \"AGGREGATE_ID\", \"MODEL_NAME\", \"DIMENSION\", \"AGGREGATE_CONFIG\", \"IS_COMPARABLE\" FROM vis_rep_pepsicoext_p1.\"VIS_MODEL_AGGREGATE_CONFIG\"";
		//Oracle
		String select = "SELECT MODEL_NAME,DIMENSION,AGGREGATE_CONFIG FROM VIS_MODEL_AGGREGATE_CONFIG WHERE MODEL_NAME = ?";
		PreparedStatement ps = con.prepareStatement(select);
		ps.setString(1, modelName);
		ResultSet rs = ps.executeQuery();
		if(rs != null) {
			while(rs.next()) {
				String config = rs.getString("AGGREGATE_CONFIG");
				//Gson gson = new Gson();
				//TimeAggregate timeAggregate = new TimeAggregate();
				String resourceString = rs.getString("AGGREGATE_CONFIG");
			//	timeAggregate = gson.fromJson(config, TimeAggregate.class);
				System.out.println(config);
			}
		}
		System.out.println("Done");
		
	}

	private static void updateQuery(Connection con)throws Exception {
		String fileName = "D:/data.txt";
		
		System.out.println("updateQuery");
		String sql = read("D:/query.txt") ;
		
		String data = read(fileName);
		Reader reader = new StringReader(data);
		PreparedStatement ps = con.prepareStatement(sql);
//		ps.setCharacterStream(1, reader, data.length());
		
		byte[] outputArr = compressIt(data);
		ps.setBytes(1, outputArr);
		
		
		ps.executeUpdate();
		System.out.println("Done");
		
	}
	
	private static void selectQuery(Connection con)throws Exception {
			
		System.out.println("selectQuery");
		String sql = read("D:/query.txt") ;
		PreparedStatement ps = con.prepareStatement(sql);
		
		ResultSet rs = ps.executeQuery();
		
		while(rs.next()){
//			convertBlobToString(rs.getBlob("VAL"));
			byte[] blobbytes = rs.getBytes("PROPERTIES");
			String paneProps = unCompressIt(blobbytes);
			System.out.println(paneProps);
		}
	}
	
	public static void convertBlobToString(Blob clobData)throws SQLException,IOException{
//    	char clobVal[] = new char[(int) clobData.length()];
		InputStream inputStream = clobData.getBinaryStream();
		System.out.println(convertStreamToString(inputStream));
		
    }

	static String convertStreamToString(InputStream is) {
	    java.util.Scanner s = new java.util.Scanner(is).useDelimiter("\\A");
	    return s.hasNext() ? s.next() : "";
	}
	private static void selectChartPROPERTIES(Connection con,String wspIds,String paneType)throws SQLException {
			
		
		String sql = "SELECT W.WSP_ID,CP.PROPERTIES FROM WSP_PANE_TBL PN ,CMN_REPORT_TBL RPT , WSP_PAGE_TBL P ,CMN_PANE_PROPS_TBL CP ,WSP_SUMM_TBL W WHERE PN.PAGE_ID = P.PAGE_ID AND W.WSP_ID IN (?) AND W.WSP_ID = P.WSP_ID AND PN.OBJECT_ID = RPT.REPORT_ID AND CP.PANE_ID = RPT.BLX_PROPS_ID AND CP.PANE_TYPE =?";
		//String sql = "SELECT REPORT_ID,REPORT_QUERY FROM CMN_REPORT_TBL  WHERE REPORT_ID = ?";
		PreparedStatement ps = con.prepareStatement(sql);
		ps.setString(1,wspIds);
		ps.setString(2, paneType);
		
		ResultSet rs = ps.executeQuery();
		
		while(rs.next()){
			byte[] blobbytes = rs.getBytes("PROPERTIES");
			String paneProps = unCompressIt(blobbytes);
			System.out.println(paneProps);
//			writeToFile("C:/Users/mredy/Desktop/DefectLogs/AP_2.xml", paneProps);
		}
	}

	private static void selectAllPROPERTIES(Connection con,String wspIds)throws SQLException {
			
		
		String sql = "SELECT W.WSP_ID,CP.PROPERTIES,CP.PANE_TYPE FROM WSP_PANE_TBL PN ,CMN_REPORT_TBL RPT , WSP_PAGE_TBL P ,CMN_PANE_PROPS_TBL CP ,WSP_SUMM_TBL W WHERE PN.PAGE_ID = P.PAGE_ID AND W.WSP_ID IN (?) AND W.WSP_ID = P.WSP_ID AND PN.OBJECT_ID = RPT.REPORT_ID AND CP.PANE_ID = RPT.BLX_PROPS_ID";
		PreparedStatement ps = con.prepareStatement(sql);
		ps.setString(1, wspIds);
		
		ResultSet rs = ps.executeQuery();
		String filePath = "C:/Users/mredy/Desktop/DefectLogs/Unify/";
		while(rs.next()){
			byte[] blobbytes = rs.getBytes("PROPERTIES");
			String paneProps = unCompressIt(blobbytes);
			String paneType = rs.getString("PANE_TYPE");
			String fileName = filePath + paneType +".xml";
			writeToFile(fileName, paneProps);
		}
		System.out.println("Done");
	}
	
 	public  static void writeToFile(String fileName,String str)
	{
 		File f = new File(fileName);
		try {
			f.getParentFile().mkdirs();
			f.createNewFile();
			Writer fw = new OutputStreamWriter(new FileOutputStream(f), StandardCharsets.UTF_8);
			fw.write(str);
			fw.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
 	
	public static String read(String fileName) throws Exception{
		StringBuilder sb = new StringBuilder();
		String sCurrentLine;

		BufferedReader br = new BufferedReader(new FileReader(fileName));

		while ((sCurrentLine = br.readLine()) != null) {
			sb.append(sCurrentLine);
		}
		return	sb.toString();
	}
	
	public static String convertClobToString(Clob clobData)throws SQLException,IOException{
    	char clobVal[] = new char[(int) clobData.length()];
		Reader paramValueReader = clobData.getCharacterStream();
		paramValueReader.read(clobVal);
		StringWriter sw = new StringWriter();
		sw.write(clobVal);
    	return sw.toString();
    }
	
	public static byte[] compressIt(String source)
	{
		try
		{
			byte[] sourceArr = null;
			if(source == null)
				return null;
			ByteArrayOutputStream bis = new ByteArrayOutputStream();
			
			sourceArr = source.getBytes("UTF-8");
			source.getBytes();
			GZIPOutputStream gOut = new GZIPOutputStream(bis);
			gOut.write(sourceArr,0,sourceArr.length);
			gOut.finish();
			byte[] outputArr = bis.toByteArray();
			gOut.close();
			bis.close();
			return outputArr;
		}
		catch(IOException ioe)
		{
			return null;	
		}
	}
	
	public static String unCompressIt(byte[] source)
	{
		try
		{
			if(source == null)
				return null;
				
			ByteArrayInputStream bis = new ByteArrayInputStream(source);
			GZIPInputStream gOut = new GZIPInputStream(bis);			
			ByteArrayOutputStream bout = new ByteArrayOutputStream();		
			int maxLength = 0;
			String returnString="";
			while(gOut.available()==1) 
			{
				byte[] uncompressedArr = new byte[1024];
				int read = gOut.read(uncompressedArr,0,uncompressedArr.length);
				if(read!=-1)bout.write(uncompressedArr,0,read);
			}
			returnString = bout.toString("UTF-8"); 	
			bout.close();
			gOut.close();
			bis.close();
			return returnString;
		}
		catch(IOException ioe)
		{
			return null;	
		}			
	}
	public static byte[] getByteArrayFromBlob(ResultSet rs, String columnName) throws Exception
	{
		byte[] byteArray = null ;
		Blob blobObj = rs.getBlob(columnName);
		
		if (blobObj != null && blobObj.length() > 0) 
			byteArray = blobObj.getBytes(1, (int) blobObj.length());
		
		return byteArray ;
	}
}
