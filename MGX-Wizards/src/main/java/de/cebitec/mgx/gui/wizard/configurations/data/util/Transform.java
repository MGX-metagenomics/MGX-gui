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
     * Giibt von einem Store die JobParameter zurueck.
     *
     * @param store
     * @return Liste von JobParametern
     */
    public static List<JobParameter> getFromNodeStoreJobParameter(Store store) {
        List<JobParameter> parameters = new ArrayList<>();


        Iterator<Entry<String, Node>> nodeIterator = store.getIterator();
        while (nodeIterator.hasNext()) {
            Entry<String, Node> nodeME = nodeIterator.next();
            String nodeId = nodeME.getKey();
            Node node = nodeME.getValue();
            Iterator<Entry<String, ConfigItem>> configItemIterator = node.getIterator();


            String configItemName;

            while (configItemIterator.hasNext()) {

                Map.Entry<String, ConfigItem> configItemME = configItemIterator.next();
                ConfigItem configItem = configItemME.getValue();
                configItemName = configItemME.getKey();

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

    /**
     * Gibt von einer Liste von JobParametern einen NodeStore zurueck.
     *
     * @param parameters
     * @return NodeStore.
     */
    public static Store getFromJobParameterNodeStore(Iterable<JobParameter> parameters) {
        Store store = new Store();

        for (JobParameter parameter : parameters) {
            Node node = null;
            if (store.getNode(Long.toString(parameter.getNodeId())) == null) {
                node = new Node(parameter.getClassName(), Long.toString(parameter.getNodeId()));
                store.addNode(node);
            } else {
                node = store.getNode(Long.toString(parameter.getNodeId()));
            }
            node.setDisplayName(parameter.getDisplayName());


            ConfigItem configItem = null;
            if (node.getConfigItem(parameter.getParameterName()) == null) {
                configItem = new ConfigItem(parameter.getUserName(), parameter.getUserDescription(), parameter.getParameterName());
                node.addConfigItem(configItem);

            } else {
                configItem = node.getConfigItem(parameter.getParameterName());
            }

            if (!parameter.getChoices().isEmpty()) {
                Set<Entry<String, String>> set = parameter.getChoices().entrySet();
                Iterator<Entry<String, String>> iterator = set.iterator();
                Choices choices = new Choices();
                while (iterator.hasNext()) {
                    Entry<String, String> choiceME = iterator.next();
                    String choiceName = choiceME.getKey();
                    String description = choiceME.getValue();

                    choices.addItem(choiceName, description);
                }
                configItem.setChoice(choices);

            }
            configItem.setConfigType(parameter.getType());
            configItem.setDefaultValue(parameter.getDefaultValue());
            configItem.setOptional(parameter.isOptional());
            configItem.setUserDescription(parameter.getUserDescription());
        }
        return store;
    }
}
