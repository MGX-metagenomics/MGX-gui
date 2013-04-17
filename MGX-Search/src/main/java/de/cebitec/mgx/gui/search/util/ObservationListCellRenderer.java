package de.cebitec.mgx.gui.search.util;

import de.cebitec.mgx.gui.controller.MGXMaster;
import de.cebitec.mgx.gui.datamodel.Sequence;
import de.cebitec.mgx.gui.search.Layer;
import de.cebitec.mgx.gui.search.ObservationViewPanel;
import de.cebitec.mgx.gui.search.OrderedObservations;
import de.cebitec.mgx.gui.search.ServerDataWrapper;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JList;
import javax.swing.ListCellRenderer;
import org.openide.util.RequestProcessor;

/**
 *
 * @author sjaenick
 */
public final class ObservationListCellRenderer implements ListCellRenderer<Sequence> {

    private List<List<Layer>> toolTips = new ArrayList<>();
    private List<String> readNames = new ArrayList<>();
    private ServerDataWrapper model = new ServerDataWrapper();
    private MGXMaster currentMaster;
    private RequestProcessor proc = new RequestProcessor("MGX-ObservationFetch-Pool", Runtime.getRuntime().availableProcessors() + 4);
    
    public void setMaster(MGXMaster m) {
        currentMaster = m;
    }

    @Override
    public ObservationViewPanel getListCellRendererComponent(JList<? extends Sequence> list,
            final Sequence seq, int index, boolean isSelected, boolean cellHasFocus) {

        OrderedObservations compute = model.getOrderedObervations(currentMaster, seq, proc);
        readNames.add(seq.getName());
        toolTips.add(compute.getLayers());

        return new ObservationViewPanel(compute, seq, currentMaster);
    }

    public List<List<Layer>> getToolTips() {
        return toolTips;
    }

    public List<String> getReadNames() {
        return readNames;
    }
}
