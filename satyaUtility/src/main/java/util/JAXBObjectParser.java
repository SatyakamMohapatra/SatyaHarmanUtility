package util;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.StringWriter;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;


/**
 * @author insramalin
 *
 */
public class JAXBObjectParser {

	/**
	 * API to serialize an object to XML file
	 * 
	 * @param obj
	 * @param classType
	 * @param absoluteOutputFilePath
	 * @throws Exception
	 */
	public static <T> void serializeToFile(Object obj, Class<T> classType,
			String absoluteOutputFilePath) throws Exception {
		if (absoluteOutputFilePath != null) {
			Marshaller m = getJAXBContext(classType).createMarshaller();
			m.setProperty("jaxb.fragment", Boolean.TRUE);
			m.marshal(obj, new FileOutputStream(new File(absoluteOutputFilePath)));
		} else
			throw new Exception("FilePath is null");
	}

	/**
	 * API to Serialize an object to XML String
	 * 
	 * @param obj
	 * @param classType
	 * @return
	 * @throws Exception
	 */
	public static <T> String serializeToString(Object obj, Class<T> classType)
			throws Exception {
		String retVal = null;
		StringWriter sw = new StringWriter();
		Marshaller m = getJAXBContext(classType).createMarshaller();
		m.setProperty("jaxb.fragment", Boolean.TRUE);
		m.setProperty(Marshaller.JAXB_ENCODING, "UTF-8");
		m.marshal(obj, sw);
		retVal = sw.toString();
		return retVal;
	}

	/**
	 * API to Deserialize an XML file to an object
	 * 
	 * @param classType
	 * @param absoluteInputFilePath
	 * @return
	 * @throws Exception
	 */
	@SuppressWarnings("unchecked")
	public static <T> T deserializeFileToObject(Class<T> classType,
			String absoluteInputFilePath) throws Exception {
		T retVal = null;
		File inputFile = new File(absoluteInputFilePath);
		if (inputFile.exists()) {
			Unmarshaller um = getJAXBContext(classType).createUnmarshaller();
			retVal = (T) um.unmarshal(inputFile);
		} else
			throw new Exception("FILE NOT FOUND");
		return retVal;
	}

	/**
	 * API to Deserialize an XML InputStream to an object
	 * 
	 * @param classType
	 * @param data
	 * @return
	 * @throws Exception
	 */
	@SuppressWarnings("unchecked")
	public static <T> T deserializeInputStreamToObject(Class<T> classType,
			InputStream data) throws Exception {
		T retVal = null;
		if (data != null) {
			Unmarshaller um = getJAXBContext(classType).createUnmarshaller();
			retVal = (T) um.unmarshal(data);
		} else
			throw new Exception("FILE NOT FOUND");
		return retVal;
	}

	/**
	 * API to Deserialize a XML String to an Object
	 * 
	 * @param classType
	 * @param data
	 * @return
	 * @throws Exception
	 */
	@SuppressWarnings({ "unchecked", "unused" })
	public static <T> T deserializeStringToObject(Class<T> classType,
			String data) throws Exception {
		InputStream dataStream = new ByteArrayInputStream(
				data.getBytes("UTF-8"));
		T retVal = null;
		if (data != null) {
			Unmarshaller um;
			um = getJAXBContext(classType).createUnmarshaller();
			retVal = (T) um.unmarshal(dataStream);
		} else
			throw new Exception("FILE NOT FOUND");
		return retVal;
	}

	/**
	 * Get Instance of JAXBContext
	 * 
	 * @param classType
	 * @return
	 * @throws JAXBException
	 */
	protected static <T> JAXBContext getJAXBContext(Class<T> classType)
			throws JAXBException {
		return JAXBContext.newInstance(classType.getCanonicalName());
	}
}	