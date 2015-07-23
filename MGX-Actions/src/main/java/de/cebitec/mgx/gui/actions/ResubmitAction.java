///*
// * To change this license header, choose License Headers in Project Properties.
// * To change this template file, choose Tools | Templates
// * and open the template in the editor.
// */
//package de.cebitec.mgx.gui.actions;
//
//import de.cebitec.mgx.api.exception.MGXException;
//import de.cebitec.mgx.api.misc.TaskI;
//import de.cebitec.mgx.api.model.JobI;
//import de.cebitec.mgx.api.model.JobState;
//import de.cebitec.mgx.gui.controller.RBAC;
//import de.cebitec.mgx.gui.swingutils.NonEDT;
//import de.cebitec.mgx.gui.taskview.MGXTask;
//import de.cebitec.mgx.gui.taskview.TaskManager;
//import java.awt.event.ActionEvent;
//import javax.swing.AbstractAction;
//import org.openide.util.Utilities;
//
///**
// *
// * @author sjaenick
// */
//public class ResubmitAction extends AbstractAction {
//
//    public ResubmitAction() {
//        putValue(NAME, "Restart");
//
//    }
//
//    @Override
//    public void actionPerformed(ActionEvent e) {
//        final JobI job = Utilities.actionsGlobalContext().lookup(JobI.class);
//        
//        final MGXTask restartTask = new MGXTask("Restart " + job.getTool().getName()) {
//            @Override
//            public boolean process() {
//                setStatus("Restarting job..");
//                TaskI<JobI> task = null;
//                try {
//                    task = job.getMaster().Job().restart(job);
//                } catch (MGXException ex) {
//                    setStatus(ex.getMessage());
//                    failed();
//                    return false;
//                }
//                while (task != null && !task.done()) {
//                    setStatus(task.getStatusMessage());
//                    try {
//                        job.getMaster().<JobI>Task().refresh(task);
//                    } catch (MGXException ex) {
//                        setStatus(ex.getMessage());
//                        failed();
//                        return false;
//                    }
//                    sleep();
//                }
//                if (task != null) {
//                    task.finish();
//                    job.modified();
//                    return task.getState() == TaskI.State.FINISHED;
//                }
//                return false;
//            }
//        };
//        NonEDT.invoke(new Runnable() {
//            @Override
//            public void run() {
//                TaskManager.getInstance().addTask(restartTask);
//            }
//        });
//    }
//
//    @Override
//    public boolean isEnabled() {
//        final JobI job = Utilities.actionsGlobalContext().lookup(JobI.class);
//        JobState status = job.getStatus();
//        return super.isEnabled() && RBAC.isUser() && (status.equals(JobState.FAILED) || status.equals(JobState.ABORTED));
//    }
//
//}
