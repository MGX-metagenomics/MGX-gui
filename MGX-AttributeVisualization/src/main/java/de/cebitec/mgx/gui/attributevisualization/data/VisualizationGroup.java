package de.cebitec.mgx.gui.attributevisualization.data;

import de.cebitec.mgx.client.exception.MGXServerException;
import de.cebitec.mgx.gui.controller.MGXMaster;
import de.cebitec.mgx.gui.datamodel.Attribute;
import de.cebitec.mgx.gui.datamodel.SeqRun;
import java.awt.Color;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.openide.util.Exceptions;

/**
 *
 * @author sj
 */
public class VisualizationGroup {

    private String name;
    private Color color = Color.RED;
    private Set<SeqRun> seqruns = new HashSet<SeqRun>();
    private final Set<Attribute> attributes = Collections.synchronizedSet(new HashSet<Attribute>());
    private List<Thread> threads = new ArrayList<Thread>();

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Color getColor() {
        return color;
    }

    public void setColor(Color color) {
        this.color = color;
    }

    public Set<SeqRun> getSeqRuns() {
        return seqruns;
    }

    public void addSeqRun(SeqRun sr) {
        if (!seqruns.contains(sr)) {
            seqruns.add(sr);
            prefetchAttributes(sr);
        }
    }

    public Set<Attribute> getAttributes() {
        while (threads.size() > 0) {
            for (Thread t : threads) {
                try {
                    t.join();
                    threads.remove(t);
                } catch (InterruptedException ex) {
                    threads.add(t);
                }
            }
        }
        return attributes;
    }

    private void prefetchAttributes(final SeqRun sr) {
        assert sr.getMaster() != null;
        Runnable r = new Runnable() {

            @Override
            public void run() {
                try {
                    MGXMaster master = (MGXMaster) sr.getMaster();
                    Collection<Attribute> types = master.Attribute().listTypesBySeqRun(sr.getId());
                    synchronized (attributes) {
                        attributes.addAll(types);
                    }
                } catch (MGXServerException ex) {
                    Exceptions.printStackTrace(ex);
                }
            }
        };
        Thread t = new Thread(r, "attribute-prefetch " + sr.getSequencingMethod());
        t.start();
        threads.add(t);
    }
}