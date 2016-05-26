/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.cebitec.mgx.gui.jscharts;

import de.cebitec.mgx.api.model.tree.NodeI;
import de.cebitec.mgx.api.model.tree.TreeI;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

/**
 *
 * @author sj
 */
public class JSON {

    public static <T> String encode(TreeI<T> tree) {
        NodeI<T> root = tree.getRoot();
        return encodeNode(root).toJSONString();
    }

    @SuppressWarnings("unchecked")
    private static <T> JSONObject encodeNode(NodeI<T> node) {
        JSONObject obj = new JSONObject();

        obj.put("name", node.getAttribute().getValue());
        obj.put("size", node.getContent());
        
        if (node.hasChildren()) {
            JSONArray arr = new JSONArray();
            for (NodeI<T> child : node.getChildren()) {
                arr.add(encodeNode(child));
            }
            obj.put("children", arr);
        }
        return obj;
    }

}
