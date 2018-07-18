package util;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.SortedMap;
import java.util.TreeMap;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import com.symphonyrpm.applayer.common.constants.ModelScopeConfigConstants;
import com.symphonyrpm.applayer.common.coreservices.AppLinkLogger;
import com.symphonyrpm.applayer.common.coreservices.ConfigManager;
import com.symphonyrpm.applayer.common.coreservices.IModules;
import com.symphonyrpm.applayer.common.coreservices.LogManager;
import com.symphonyrpm.applayer.common.dto.ModelScopeCacheDefinitionDTO;


public class ModelScopeXMLReader {

	private static AppLinkLogger logger = LogManager.getLogger(IModules.SERVER, ModelScopeXMLReader.class.getName());
	public ModelScopeXMLReader(){
	}
	private static ModelScopeCacheDefinitionDTO mscDTO = null;

	/*
		<ModelScopeCache>
			<Model name="Model" />
			<Model dimension="complex.product" />
			<Model dimension="complex.Measures" />
			<Model dimension="complex.Time" />
		</ModelScopeCache>	 
	 */
	public static ModelScopeCacheDefinitionDTO loadModelScopeConfigData() {
		try {
			if(mscDTO == null){
				InputStream fileStream = ConfigManager.getInstance().getInputStream(ModelScopeConfigConstants.MODEL_SCOPE_CONFIG_XML_FILENAME);
				Document doc = XmlUtility.parse(fileStream);
				Element element = doc.getDocumentElement();
				NodeList modelList = element.getElementsByTagName("Model");
				if(modelList != null){
					mscDTO = new ModelScopeCacheDefinitionDTO();
					int numOfModel = modelList.getLength();
					for(int i=0;i<numOfModel;i++){
						Element modelNode = (Element)modelList.item(i);
						String modelName = modelNode.getAttribute("name");
						String dimensionName = modelNode.getAttribute("dimension");
						if( modelName != null && modelName.length() > 0){
							modelName = modelName.toLowerCase();
							mscDTO.setExcludeInfo(modelName, modelName);
						}
						if( dimensionName != null && dimensionName.length() > 0){
							String[] values = dimensionName.split("\\."); 
							dimensionName = dimensionName.toLowerCase();
							if(values.length == 2){
								mscDTO.setExcludeInfo(dimensionName, values[1]);
							}
						}
					}
				}
			}
		}
		catch(Exception e)
		{
			logger.error("Failed to read the "+ModelScopeConfigConstants.MODEL_SCOPE_CONFIG_XML_FILENAME, e);
		}
		return mscDTO;
	}
	

}
