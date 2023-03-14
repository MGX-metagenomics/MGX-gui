package de.cebitec.mgx.gui.nodes;

import de.cebitec.mgx.gui.actions.EditSeqRun;
import de.cebitec.mgx.api.model.SeqRunI;
import de.cebitec.mgx.gui.actions.DownloadSeqRun;
import de.cebitec.mgx.gui.actions.OpenMappingBySeqRun;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Locale;
import javax.swing.Action;
import static org.apache.commons.text.StringEscapeUtils.escapeHtml4;
import org.openide.filesystems.FileUtil;
import org.openide.nodes.Children;
import org.openide.util.lookup.Lookups;

/**
 *
 * @author sj
 */
public class SeqRunNode extends MGXNodeBase<SeqRunI> {

    //
    public SeqRunNode(SeqRunI s, Children children) {
        super(children, Lookups.fixed(s.getMaster(), s), s);
        setIconBaseWithExtension("de/cebitec/mgx/gui/nodes/SeqRun.png");
        super.setShortDescription(getToolTipText(s));
        super.setDisplayName(s.getName());
    }

    private String getToolTipText(SeqRunI run) {
        DecimalFormat formatter = (DecimalFormat) NumberFormat.getInstance(Locale.US);
        String numSeqs = formatter.format(run.isPaired() ? run.getNumSequences() / 2
                : run.getNumSequences());

        return new StringBuilder("<html><b>Sequencing run: </b>").append(escapeHtml4(run.getName()))
                .append("<br><hr><br>")
                .append(run.getSequencingTechnology().getName()).append(" ")
                .append(run.getSequencingMethod().getName())
                .append("<br>")
                .append(numSeqs)
                .append(run.isPaired() ? " read pairs" : " reads")
                .append("</html>").toString();
    }

    @Override
    public Action[] getActions(boolean context) {
        Action analyze = FileUtil.getConfigObject("Actions/Edit/de-cebitec-mgx-gui-actions-ExecuteAnalysis.instance", Action.class);
        Action assemble = FileUtil.getConfigObject("Actions/Edit/de-cebitec-mgx-gui-actions-AssembleRuns.instance", Action.class);
        Action report = FileUtil.getConfigObject("Actions/File/de-cebitec-mgx-gui-reportcom-ReportAction.instance", Action.class);
        Action delete = FileUtil.getConfigObject("Actions/Edit/de-cebitec-mgx-gui-actions-DeleteSeqRun.instance", Action.class);
        Action goldstandard = FileUtil.getConfigObject("Actions/Edit/de-cebitec-mgx-gui-goldstandard-actions-AddGoldstandard.instance", Action.class);
        if (goldstandard != null) {
            return new Action[]{analyze, assemble, report, goldstandard, new OpenMappingBySeqRun(), new EditSeqRun(), delete, new DownloadSeqRun()};
        } else {
            return new Action[]{analyze, assemble, report, new OpenMappingBySeqRun(), new EditSeqRun(), delete, new DownloadSeqRun()};
        }
    }

    @Override
    public void updateModified() {
        setDisplayName(getContent().getName());
        setShortDescription(getToolTipText(getContent()));
    }
}
