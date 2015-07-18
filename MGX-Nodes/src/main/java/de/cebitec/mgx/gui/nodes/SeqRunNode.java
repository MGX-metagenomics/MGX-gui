package de.cebitec.mgx.gui.nodes;

import de.cebitec.mgx.gui.actions.DeleteSeqRun;
import de.cebitec.mgx.gui.actions.EditSeqRun;
import de.cebitec.mgx.api.model.SeqRunI;
import de.cebitec.mgx.gui.actions.DownloadSeqRun;
import de.cebitec.mgx.gui.actions.OpenMappingBySeqRun;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Locale;
import javax.swing.Action;
import org.openide.filesystems.FileUtil;
import org.openide.nodes.Children;
import org.openide.util.lookup.Lookups;

/**
 *
 * @author sj
 */
public class SeqRunNode extends MGXNodeBase<SeqRunI> {

    //
    //public static final DataFlavor DATA_FLAVOR = new DataFlavor(SeqRunNode.class, "SeqRunNode");
    public SeqRunNode(SeqRunI s, Children children) {
        super(s.getMaster(), children, Lookups.fixed(s.getMaster(), s), s);
        setIconBaseWithExtension("de/cebitec/mgx/gui/nodes/SeqRun.png");
        setShortDescription(getToolTipText(s));
        setDisplayName(s.getName());
    }

    private String getToolTipText(SeqRunI run) {
        DecimalFormat formatter = (DecimalFormat) NumberFormat.getInstance(Locale.US);
        String numSeqs = formatter.format(run.getNumSequences());
        
        return new StringBuilder("<html><b>Sequencing run: </b>").append(run.getName())
                .append("<br><hr><br>")
                .append(run.getSequencingTechnology().getName()).append(" ")
                .append(run.getSequencingMethod().getName())
                .append("<br>")
                .append(numSeqs).append(" reads")
                .append("</html>").toString();
    }

    @Override
    public Action[] getActions(boolean context) {
        Action analyze = FileUtil.getConfigObject("Actions/Edit/de-cebitec-mgx-gui-actions-ExecuteAnalysis.instance", Action.class);
        return new Action[]{analyze, new OpenMappingBySeqRun(), new EditSeqRun(), new DeleteSeqRun(), new DownloadSeqRun()};
    }

    @Override
    public void updateModified() {
        setDisplayName(getContent().getName());
        setShortDescription(getToolTipText(getContent()));
    }
}
