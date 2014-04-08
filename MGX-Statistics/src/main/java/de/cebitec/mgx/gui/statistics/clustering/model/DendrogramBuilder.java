/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.cebitec.mgx.gui.statistics.clustering.model;

import de.cebitec.mgx.newick.NodeI;
import java.text.DecimalFormat;
import prefuse.data.Edge;
import prefuse.data.Node;
import prefuse.data.Table;
import prefuse.data.Tree;
import prefuse.visual.VisualItem;

/**
 * This class builds the model tree for a dendrogram.
 *
 * @author belmann
 */
public class DendrogramBuilder implements ITreeBuilder {

    private Tree g;
    private final String X_COORD;
    private final String NODE_NAME;
    private final NodeI root;

    public DendrogramBuilder(String nodeNameKey, String xCoordKey, NodeI root) {
        this.X_COORD = xCoordKey;
        this.NODE_NAME = nodeNameKey;
        this.root = root;
    }

    @Override
    public Tree getTree() {
        // Create tables for node and edge data, and configure their columns.
        Table nodeData = new Table();
        Table edgeData = new Table();

        nodeData.addColumn(X_COORD, int.class);
        nodeData.addColumn(NODE_NAME, String.class);
        edgeData.addColumn(Tree.DEFAULT_SOURCE_KEY, int.class);
        edgeData.addColumn(Tree.DEFAULT_TARGET_KEY, int.class);
        edgeData.addColumn(VisualItem.LABEL, String.class);

        g = new Tree(nodeData, edgeData);
        this.addNodes(null, root);
        return g;
    }

    private void addNodes(Node parentNode, NodeI modelNode) {
        boolean nullParentNode = false;
        Node n = null;
        if (parentNode == null) {
            nullParentNode = true;
            parentNode = g.addNode();
            parentNode.setInt(X_COORD, (int) modelNode.getWeight());
            parentNode.setString(NODE_NAME, modelNode.getName());
        } else {
            n = g.addNode();
            n.setInt(X_COORD, (int) modelNode.getWeight());
            n.setString(NODE_NAME, modelNode.getName());
            Edge e = g.addEdge(parentNode, n);

            DecimalFormat f = new DecimalFormat("#0.00");
            String weight = modelNode.getWeight() == 0 ? "0" : f.format(modelNode.getWeight());

            e.setString(VisualItem.LABEL, weight);
        }
        if (modelNode.getChildren() != null) {
            for (NodeI child : modelNode.getChildren()) {
                if (nullParentNode) {
                    this.addNodes(parentNode, child);
                } else {
                    this.addNodes(n, child);
                }
            }
        }
    }
}
