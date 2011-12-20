package de.cebitec.mgx.gui.taskview;

import de.cebitec.mgx.client.upload.SeqUploader;
import javax.swing.JComponent;
import org.netbeans.api.progress.ProgressHandle;
import org.netbeans.api.progress.ProgressHandleFactory;

/**
 *
 * @author sjaenick
 */
public class TaskDescriptor extends TaskBase {

    private Runnable run;
    private ProgressHandle ph;
    private TaskEntry te;
    private long startTime;

    public TaskDescriptor(final SeqUploader su) {
        startTime = System.currentTimeMillis();
        this.run = new Runnable() {

            @Override
            public void run() {
                su.upload();
            }
        };
        te = new TaskEntry();
        ph = ProgressHandleFactory.createHandle("myName");
        ph.switchToIndeterminate();
        ph.start();
    }

    @Override
    public void run() {
        run.run();
        ph.finish();
    }

    @Override
    public ProgressHandle getProgressHandle() {
        return ph;
    }
    
    public JComponent getTaskEntry() {
        te.setMainText(ProgressHandleFactory.createMainLabelComponent(ph));
        te.setDetailText(ProgressHandleFactory.createDetailLabelComponent(ph));
        te.setProgressBar(ProgressHandleFactory.createProgressComponent(ph));
        te.build();
        return te; 
    }

    @Override
    public long getStartTime() {
        return startTime;
    }
}
