/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.cebitec.mgx.gui.wizard.configurations.data.util;



import de.cebitec.mgx.gui.datamodel.JobParameter;
import de.cebitec.mgx.gui.wizard.configurations.data.Impl.Choices;
import de.cebitec.mgx.gui.wizard.configurations.data.Impl.ConfigItem;
import de.cebitec.mgx.gui.wizard.configurations.data.Impl.Node;
import de.cebitec.mgx.gui.wizard.configurations.data.Impl.Store;
import de.cebitec.mgx.gui.wizard.configurations.data.interf.NodeStore;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 *
 * @author belmann
 */
public class Transform {

   public static List<JobParameter> getFromNodeStoreJobParameter(NodeStore store) {
	List<JobParameter> parameters = new ArrayList<JobParameter>();

	Iterator nodeIterator = store.getIterator();
	Map.Entry nodeME;
	String nodeId;
	Iterator configItemIterator;
	while (nodeIterator.hasNext()) {

	   nodeME = (Map.Entry) nodeIterator.next();
	   nodeId = (String) nodeME.getKey();
           Node node = (Node) nodeME.getValue();
	   configItemIterator = (node).getIterator();
	   Map.Entry configItemME;

	 
	   String configItemName;


	   while (configItemIterator.hasNext()) {

		configItemME = (Map.Entry) configItemIterator.next();
		ConfigItem configItem = (ConfigItem) configItemME.getValue();
		configItemName = (String) configItemME.getKey();

		JobParameter jobParameter = new JobParameter();
		jobParameter.setConfigItemName(configItemName);
		jobParameter.setConfigItemValue(configItem.getAnswer());
                jobParameter.setClassName(node.getClassName());
                jobParameter.setDisplayName(node.getDisplayName());
		jobParameter.setDefaultValue(configItem.getDefaultValue());
		jobParameter.setNodeId(Integer.getInteger(nodeId));
		jobParameter.setOptional(configItem.isOptional());
		jobParameter.setType(configItem.getConfigType());
		jobParameter.setUserDescription(configItem.getUserDescription());
		jobParameter.setUserName(configItem.getUserName());
                jobParameter.setChoices(configItem.getChoice().getChoices());
                
		parameters.add(jobParameter);
	   }
	}
	return parameters;
   }

   public static Store getFromJobParameterNodeStore(List<JobParameter> parameters) {
	Store store = new Store();

	for (JobParameter parameter : parameters) {
	   Node node;

	   if (store.getNode(Long.toString(
		 parameter.getNodeId())) == null) {
		node = new Node(parameter.getClassName(), Long.toString(parameter.getNodeId()));
		store.addNode(node);
	   } else {
		node = store.getNode(Long.toString(parameter.getNodeId()));
	   }

	   node.setDisplayName(parameter.getDisplayName());
           
	   ConfigItem configItem;

	   if (node.getConfigItem(parameter.getConfigItemName()) == null) {


		configItem = new ConfigItem(parameter.getUserName(), parameter.getUserDescription(), parameter.getConfigItemName());

	   } else {

		configItem = node.getConfigItem(parameter.getConfigItemName());

	   }    
	   configItem.setChoice(new Choices(parameter.getChoices()));
	   configItem.setConfigType(parameter.getType());
	   configItem.setDefaultValue(parameter.getDefaultValue());
	   configItem.setOptional(parameter.isOptional());
	   configItem.setUserDescription(parameter.getUserDescription());

	}

	return store;
   }
}
