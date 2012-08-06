/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.cebitec.mgx.gui.wizard.configurations.data.util;

import de.cebitec.mgx.gui.datamodel.JobParameter;
import de.cebitec.mgx.gui.wizard.configurations.data.impl.Choices;
import de.cebitec.mgx.gui.wizard.configurations.data.impl.ConfigItem;
import de.cebitec.mgx.gui.wizard.configurations.data.impl.Node;
import de.cebitec.mgx.gui.wizard.configurations.data.impl.Store;

import java.util.*;
import java.util.Map.Entry;
import java.util.logging.Logger;

/**
 * Sorgt fuer die Umwandlung zwischne Store und JobParameter.
 *
 * @author belmann
 */
public class Transform {

    /**
     * Giibt von einem Stoer die JobParameter zurueck.
     *
     * @param store
     * @return Liste von JobParametern
     */
    public static List<JobParameter> getFromNodeStoreJobParameter(Store store) {
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
                jobParameter.setParameterName(configItemName);
                jobParameter.setParameterValue(configItem.getAnswer());
                jobParameter.setClassName(node.getClassName());
                jobParameter.setDisplayName(node.getDisplayName());
                jobParameter.setDefaultValue(configItem.getDefaultValue());
                jobParameter.setNodeId(Integer.parseInt(nodeId));
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
    private final static Logger LOGGER =
            Logger.getLogger(Transform.class.getName());

    /**
     * Gibt von einer Liste von JobParametern einen NodeStore zurueck.
     *
     * @param parameters
     * @return NodeStore.
     */
    public static Store getFromJobParameterNodeStore(Iterable<JobParameter> parameters) {
        Store store = new Store();
        LOGGER.info("Get from Parameters is started.");

        for (JobParameter parameter : parameters) {
            boolean newNode = false;
            boolean newConfig = false;
            Node node;


            if (store.getNode(Long.toString(
                    parameter.getNodeId())) == null) {
                node = new Node(parameter.getClassName(),
                        Long.toString(parameter.getNodeId()));
                newNode = true;
            } else {
                node = store.getNode(Long.toString(parameter.getNodeId()));
            }
            node.setDisplayName(parameter.getDisplayName());

            ConfigItem configItem;

            if (node.getConfigItem(parameter.getParameterName()) == null) {


                configItem = new ConfigItem(parameter.getUserName(),
                        parameter.getUserDescription(),
                        parameter.getParameterName());
                newConfig = true;

            } else {
                configItem = node.getConfigItem(parameter.getParameterName());
            }

            if (!parameter.getChoices().isEmpty()) {


                Set set = parameter.getChoices().entrySet();
                Iterator iterator = set.iterator();
                Choices choices = new Choices();
                while (iterator.hasNext()) {
                    Entry choiceME = (Map.Entry) iterator.next();
                    String choiceName = (String) choiceME.getKey();
                    String description = (String) choiceME.getValue();

                    choices.addItem(choiceName, description);


                }
                configItem.setChoice(choices);

            }
            configItem.setConfigType(parameter.getType());
            configItem.setDefaultValue(parameter.getDefaultValue());
            configItem.setOptional(parameter.isOptional());
            configItem.setUserDescription(parameter.getUserDescription());

            if (newConfig) {
                node.addConfigItem(configItem);
            }

            if (newNode) {
                store.addNode(node);
            }


        }

        LOGGER.info("Get from Parameters is finished.");

        return store;
    }
}
