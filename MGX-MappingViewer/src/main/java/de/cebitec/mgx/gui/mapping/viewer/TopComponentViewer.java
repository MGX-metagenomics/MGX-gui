/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.cebitec.mgx.gui.mapping.viewer;

import de.cebitec.mgx.api.MGXMasterI;
import de.cebitec.mgx.api.access.datatransfer.DownloadBaseI;
import de.cebitec.mgx.api.access.datatransfer.TransferBaseI;
import de.cebitec.mgx.api.exception.MGXException;
import de.cebitec.mgx.api.groups.FileType;
import de.cebitec.mgx.api.groups.ImageExporterI;
import de.cebitec.mgx.api.model.ModelBaseI;
import de.cebitec.mgx.gui.mapping.MappingCtx;
import de.cebitec.mgx.gui.mapping.ViewController;
import de.cebitec.mgx.gui.mapping.panel.FeaturePanel;
import de.cebitec.mgx.gui.mapping.panel.MappingPanel;
import de.cebitec.mgx.gui.mapping.panel.NavigationPanel;
import de.cebitec.mgx.gui.mapping.panel.RecruitmentIdentityPanel;
import de.cebitec.mgx.gui.mapping.panel.RecruitmentPanel;
import de.cebitec.mgx.gui.swingutils.NonEDT;
import de.cebitec.mgx.gui.taskview.MGXTask;
import de.cebitec.mgx.gui.taskview.TaskManager;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import javax.imageio.ImageIO;
import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JFileChooser;
import javax.swing.JPanel;
import org.apache.commons.math3.util.FastMath;
import org.openide.DialogDescriptor;
import org.openide.DialogDisplayer;
import org.openide.NotifyDescriptor;
import org.openide.awt.ActionID;
import org.openide.awt.ActionReference;
import org.openide.util.Exceptions;
import org.openide.util.Lookup;
import org.openide.util.NbBundle.Messages;
import org.openide.util.NbPreferences;
import org.openide.util.lookup.AbstractLookup;
import org.openide.util.lookup.InstanceContent;
import org.openide.windows.TopComponent;

@TopComponent.Description(
        preferredID = "TopComponentViewer",
        persistenceType = TopComponent.PERSISTENCE_NEVER)
@TopComponent.Registration(mode = "editor", openAtStartup = false)
@ActionID(category = "Window", id = "de.cebitec.mgx.gui.mapping.viewer")
@ActionReference(path = "Menu/Window", position = 334)
@Messages({
    "CTL_MappingAction=ReferenceView",
    "CTL_TopComponentViewer=Mapping Window",})
public final class TopComponentViewer extends TopComponent implements PropertyChangeListener {

    private final InstanceContent content = new InstanceContent();
    private final Lookup lookup;

    enum DisplayMode {

        ALIGNMENT,
        RECRUITMENT;
    };

    private DisplayMode currentMode = DisplayMode.ALIGNMENT;
    private MappingPanel mp;
    private FeaturePanel fp;
    private RecruitmentIdentityPanel rip;
    private RecruitmentPanel rp;
    private JPanel bottom;
    private final ViewController vc;

    public TopComponentViewer(MappingCtx ctx) {
        lookup = new AbstractLookup(content);
        associateLookup(lookup);
        content.add(new SaveView());
        vc = new ViewController(ctx);
        setName(Bundle.CTL_TopComponentViewer());
        createView();
    }

    private void createView() {
        vc.addPropertyChangeListener(this);
        removeAll();
        setLayout(new BorderLayout());

        // top panel: navigation and features
        JPanel top = new JPanel(new BorderLayout(), true);
        NavigationPanel np = new NavigationPanel(vc);
        top.add(np, BorderLayout.PAGE_START);
        fp = new FeaturePanel(vc);
        top.add(fp, BorderLayout.CENTER);
        top.setPreferredSize(new Dimension(500, 205));
        add(top, BorderLayout.PAGE_START);

        mp = new MappingPanel(vc, new SwitchToRecruitment(this));
        mp.setEnabled(true);
        add(mp, BorderLayout.CENTER);

        // bottom panel for fragment recruitments
        SwitchModeBase sm = new SwitchToAlignment(this);
        rip = new RecruitmentIdentityPanel(vc, sm);
        rp = new RecruitmentPanel(vc, sm);

        bottom = new JPanel(new BorderLayout(), true) {

            @Override
            public void setEnabled(boolean enable) {
                super.setEnabled(enable);
                rip.setEnabled(enable);
                rp.setEnabled(enable);
            }

        };
        bottom.add(rip, BorderLayout.NORTH);
        bottom.add(rp, BorderLayout.CENTER);
        bottom.setEnabled(false);

    }

    void switchMode() {
        switch (currentMode) {
            case ALIGNMENT:
                remove(mp);
                mp.setEnabled(false);
                bottom.setEnabled(true);
                add(bottom, BorderLayout.CENTER);
                currentMode = DisplayMode.RECRUITMENT;
                break;
            case RECRUITMENT:
                remove(bottom);
                mp.setEnabled(true);
                bottom.setEnabled(false);
                add(mp, BorderLayout.CENTER);
                currentMode = DisplayMode.ALIGNMENT;
                break;
        }
        revalidate();
        repaint();
    }

    void downloadBAM() {
        String bamName = null;
        try {
            bamName = vc.getSeqRun().getName() + "_vs_" + vc.getReference().getName() + ".bam";
        } catch (MGXException ex) {
            Exceptions.printStackTrace(ex);
        }

        JFileChooser fchooser = new JFileChooser();
        fchooser.setDialogType(JFileChooser.SAVE_DIALOG);

        // try to restore last directory selection
        String last = NbPreferences.forModule(JFileChooser.class).get("lastDirectory", null);
        if (last != null) {
            File f = new File(last);
            if (f.exists() && f.isDirectory() && f.canWrite()) {
                fchooser.setCurrentDirectory(f);
            }
        }

        // suggest a file name
        File suggestedName = new File(fchooser.getCurrentDirectory(), bamName);
        fchooser.setSelectedFile(suggestedName);

        if (fchooser.showSaveDialog(null) != JFileChooser.APPROVE_OPTION) {
            return;
        }

        NbPreferences.forModule(JFileChooser.class).put("lastDirectory", fchooser.getCurrentDirectory().getAbsolutePath());

        final File target = fchooser.getSelectedFile();
        if (target.exists()) {
            // ask if file should be overwritten, else return
            String msg = new StringBuilder("A file named ")
                    .append(target.getName())
                    .append(" already exists. Should this ")
                    .append("file be overwritten?")
                    .toString();
            NotifyDescriptor nd = new NotifyDescriptor(msg,
                    "Overwrite file?",
                    NotifyDescriptor.OK_CANCEL_OPTION,
                    NotifyDescriptor.WARNING_MESSAGE,
                    null, null);
            Object ret = DialogDisplayer.getDefault().notify(nd);
            if (!NotifyDescriptor.OK_OPTION.equals(ret)) {
                return;
            }
        }

        try {
            final OutputStream writer = new FileOutputStream(target);
            MGXMasterI master = vc.getMaster();
            final DownloadBaseI downloader = master.Mapping().createDownloader(vc.getMapping(), writer);

            final MGXTask run = new MGXTask("Save to " + fchooser.getSelectedFile().getName()) {
                @Override
                public boolean process() {
                    downloader.addPropertyChangeListener(this);
                    boolean ret = downloader.download();
                    downloader.removePropertyChangeListener(this);
                    if (!ret) {
                        setStatus(downloader.getErrorMessage());
                    }
                    return ret;
                }

                @Override
                public void finished() {
                    try {
                        writer.close();
                        super.finished();
                    } catch (IOException ex) {
                        setStatus(ex.getMessage());
                        failed(ex.getMessage());
                    }
                }

                @Override
                public void failed(String reason) {
                    if (target.exists()) {
                        target.delete();
                    }
                    super.failed(reason);
                }

                @Override
                public void propertyChange(PropertyChangeEvent pce) {
                    switch (pce.getPropertyName()) {
                        case TransferBaseI.NUM_ELEMENTS_TRANSFERRED:
                            setStatus(String.format("%1$d bytes received", pce.getNewValue()));
                            break;
                        case TransferBaseI.TRANSFER_FAILED:
                            failed(pce.getNewValue().toString());
                            break;
                        default:
                            super.propertyChange(pce);
                            break;
                    }
                }
            };

            NonEDT.invoke(new Runnable() {
                @Override
                public void run() {
                    TaskManager.getInstance().addTask(run);
                }
            });
        } catch (MGXException | FileNotFoundException ex) {
            Exceptions.printStackTrace(ex);
        }

    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 400, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 300, Short.MAX_VALUE)
        );
    }// </editor-fold>//GEN-END:initComponents

    // Variables declaration - do not modify//GEN-BEGIN:variables
    // End of variables declaration//GEN-END:variables
    @Override
    public void componentOpened() {
        //createView(ctx);
    }

    @Override
    public void componentClosed() {
        removeAll();

        NonEDT.invoke(new Runnable() {
            @Override
            public void run() {
                if (!vc.isClosed()) {
                    vc.close();
                }
            }
        });
    }

    void writeProperties(java.util.Properties p) {
        p.setProperty("version", "1.0");
    }

    void readProperties(java.util.Properties p) {
        String version = p.getProperty("version");
    }

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        if (evt.getSource().equals(vc) && evt.getPropertyName().equals(ViewController.VIEWCONTROLLER_CLOSED)) {
            vc.removePropertyChangeListener(this);
            EventQueue.invokeLater(new Runnable() {
                @Override
                public void run() {
                    close();
                }
            });
            return;
        }
        if (evt.getPropertyName().equals(ModelBaseI.OBJECT_DELETED)) {
            vc.removePropertyChangeListener(this);
            removeAll();
        }
    }

    private class SaveView implements ImageExporterI {

        @Override
        public FileType[] getSupportedTypes() {
            return new FileType[]{FileType.PNG, FileType.JPEG};
        }

        @Override
        public Result export(FileType type, String fName) throws Exception {
            JPanel form = new JPanel();
            form.setLayout(new BorderLayout());

            JCheckBox useFeatures = new JCheckBox("Reference features");
            useFeatures.setSelected(true);
            form.add(useFeatures, BorderLayout.PAGE_START);

            JCheckBox useAlignment = new JCheckBox("Alignment");
            useAlignment.setSelected(true);

            JCheckBox useIdentity = new JCheckBox("Identity bars");
            useIdentity.setSelected(true);

            JCheckBox useRecruitment = new JCheckBox("Recruitment");
            useRecruitment.setSelected(true);

            switch (currentMode) {
                case ALIGNMENT:
                    form.add(useAlignment, BorderLayout.CENTER);
                    break;
                case RECRUITMENT:
                    form.add(useIdentity, BorderLayout.CENTER);
                    form.add(useRecruitment, BorderLayout.SOUTH);
                    break;
            }

            DialogDescriptor dd = new DialogDescriptor(form, "Select components to include");
            Object result = DialogDisplayer.getDefault().notify(dd);
            if (result != NotifyDescriptor.OK_OPTION) {
                return Result.ABORT;
            }

            List<JComponent> useComponents = new ArrayList<>();
            if (useFeatures.isSelected()) {
                useComponents.add(fp);
            }

            switch (currentMode) {
                case ALIGNMENT:
                    if (useAlignment.isSelected()) {
                        useComponents.add(mp);
                    }
                    break;
                case RECRUITMENT:
                    if (useIdentity.isSelected()) {
                        useComponents.add(rip);
                    }
                    if (useRecruitment.isSelected()) {
                        useComponents.add(rp);
                    }
                    break;
            }

            if (useComponents.isEmpty()) {
                return Result.ABORT;
            }

            int width = 0;
            int height = 0;
            for (JComponent jc : useComponents) {
                height += jc.getHeight();
                width = FastMath.max(width, jc.getWidth());
            }

            BufferedImage bi = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
            int curY = 0;
            for (JComponent jc : useComponents) {
                BufferedImage subimage = bi.getSubimage(0, curY, jc.getWidth(), jc.getHeight());
                Graphics2D g2 = subimage.createGraphics();
                jc.paint(g2);
                g2.dispose();
                curY += jc.getHeight();
            }
            ImageIO.write(bi, type.getSuffices()[0], new File(fName));

            return Result.SUCCESS;
        }

    }
}
