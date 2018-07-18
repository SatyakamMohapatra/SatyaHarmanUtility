package test;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;

public class CompareDalQuery {
	public static void main(String[] args) throws Exception {
		
		String postgre = read("D:\\UNIFY_Workspace\\UNIFY_QA_TRUNK\\RPM-VB8.1.0 Trunk"
				+ "\\LDServices\\src\\dal-postgre-queries.xml");
		String oracle = read("D:\\UNIFY_Workspace\\UNIFY_QA_TRUNK\\RPM-VB8.1.0 Trunk\\"
				+ "LDServices\\src\\dal-oracle-queries.xml");
		compareQuery(postgre,oracle);
		System.out.println("Done!!");
	}
	
	private static void compareQuery(String postgre, String oracle) {
		List<String> postgreList = new ArrayList<>();
		String[] pString = postgre.split("</query>");
		
		for (String string : pString) {
			try {
				postgreList.add(string.substring(string.indexOf("\""),string.indexOf("\"",string.indexOf("\"")+1))+"\"");
			} catch (Exception e) {}
		}
		
		List<String> oracleList = new ArrayList<>();
		String[] oString = oracle.split("</query>");
		for (String string : oString) {
			try {
				oracleList.add(string.substring(string.indexOf("\""),string.indexOf("\"",string.indexOf("\"")+1))+"\"");
			} catch (Exception e) {}
		}
		
		for (String string : oracleList) {
			if(!postgreList.contains(string)){
				System.out.println(string);
			}
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
}
