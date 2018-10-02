/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.cebitec.mgx.gui.jscharts;

import de.cebitec.mgx.api.groups.VisualizationGroupI;
import de.cebitec.mgx.api.misc.Pair;
import de.cebitec.mgx.api.model.AttributeTypeI;
import de.cebitec.mgx.api.model.tree.TreeI;
import de.cebitec.mgx.common.visualization.ViewerI;
import java.util.List;
import org.openide.util.lookup.ServiceProvider;

/**
 *
 * @author sj
 */
@ServiceProvider(service = ViewerI.class)
public class D3Demo extends JSChartBase<TreeI<Long>> {

    @Override
    public String getName() {
        return "D3Demo";
    }

    @Override
    public boolean canHandle(AttributeTypeI valueType) {
        return valueType.getStructure() == AttributeTypeI.STRUCTURE_HIERARCHICAL;
    }

    @Override
    public Class getInputType() {
        return TreeI.class;
    }

    @Override
    public void show(List<Pair<VisualizationGroupI, TreeI<Long>>> dists) {
        Pair<VisualizationGroupI, TreeI<Long>> first = dists.get(0);
        TreeI<Long> tree = first.getSecond();
        String data = JSON.encode(tree);
        
        String html = loadResourceFile("de/cebitec/mgx/gui/jscharts/d3demo.html");
        html = html.replace("MGXCONTENT", data);
        setHTML(html);
    }

}
