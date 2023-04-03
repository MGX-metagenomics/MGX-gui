/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.cebitec.mgx.gui.statistics.clustering.dendro;

import de.cebitec.mgx.newick.NewickParser;
import de.cebitec.mgx.newick.NodeI;
import de.cebitec.mgx.newick.ParserException;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.io.Serial;
import java.util.List;
import javax.swing.JComponent;
import javax.swing.JFrame;

/**
 *
 * @author sj
 */
public class Dendrogram extends JComponent {

    @Serial
    private static final long serialVersionUID = 1L;

    private SubTreeI tree = null;

    public Dendrogram() {
    }

    public void showTree(NodeI root, final double xOffset) {

        tree = createNode(null, root, xOffset);
        tree.layout();
        repaint();
    }

    private SubTreeI createNode(SubTree parent, NodeI node, final double xOffset) {

        double xOff = xOffset;

        if (node.isLeaf()) {
            return new Leaf(parent, node.getName(), node.getWeight(), xOff);
        } else {
            // only two children possible
            List<NodeI> children = node.getChildren();

            SubTree ret = new SubTree(parent, node.getWeight(), xOff);
            SubTreeI st1 = createNode(ret, children.get(0), xOff + ret.getWidth());
            SubTreeI st2 = createNode(ret, children.get(1), xOff + ret.getWidth());
            ret.setChildren(st1, st2);
            return ret;
        }
    }

    @Override
    public int getHeight() {
        return (int) tree.getBounds().getHeight();
    }

    @Override
    public int getWidth() {
        return (int) tree.getWidth();
    }

    @Override
    public Dimension getSize() {
        return new Dimension(getWidth(), getHeight());
    }

    @Override
    public Dimension getPreferredSize() {
        return getSize();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        BufferedImage bi = new BufferedImage(getSize().width, getSize().height, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2 = bi.createGraphics();
        RenderingHints antiAlias = new RenderingHints(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setRenderingHints(antiAlias);
        g2.setBackground(Color.WHITE);
        g2.clearRect(0, 0, getSize().width, getSize().height);
        g2.setColor(Color.BLACK);
        g2.setStroke(new BasicStroke(SubTreeI.LINE_THICKNESS));
        if (tree != null) {
            tree.plot(g2);
        }
        g.drawImage(bi, 0, 0, null);
        g2.dispose();
    }

    @Override
    public void print(Graphics g) {
        Graphics2D g2 = (Graphics2D) g;
        g2.setBackground(Color.WHITE);
        g2.clearRect(0, 0, getSize().width, getSize().height);
        g2.setColor(Color.BLACK);
        g2.setStroke(new BasicStroke(SubTreeI.LINE_THICKNESS));
        if (tree != null) {
            tree.plot(g2);
        }

        g2.dispose();
    }

    public static void main(String[] args) throws ParserException {
        String newick
                = "((((ML19-1-apr:0.728096294704428,ML20-1-apr:0.728096294704428):0,(ML18-1-apr:0.109828616251945,(ML01-1-apr:0.469153320423688,(ML02-1-apr:1.1254975425337,ML17-1-apr:1.1254975425337):0.469153320423688):0.109828616251945):0):0,((ML19-0-apr:3.8394621168471,ML29-1-apr:3.8394621168471):0,(ML20-0-apr:1.53292509177335,(ML24-0-apr:0.196835281770088,((ML03-0-apr:1.25469139265139,ML18-0-apr:1.25469139265139):0,(ML02-0-apr:0.153782278965603,(ML01-0-apr:0.499866929884893,(ML08-1-apr:1.07890750194934,ML04-0-apr:1.07890750194934):0.499866929884893):0.153782278965603):0):0.196835281770088):1.53292509177335):0):0):0,(((((ML09-1-apr:1.34076568621791,(ML07-0-apr:1.16865557875768,(ML06-0-apr:3.2045199120432,ML05-1-apr:3.2045199120432):1.16865557875768):1.34076568621791):0,(((ML05-0-apr:3.06171692356428,ML12-1-apr:3.06171692356428):0,((ML28-1-apr:0.21756267051392,((ML11-1-apr:1.72235253478789,ML22-0_apr:1.72235253478789):0,(ML26-1-apr:1.9457191878341,ML27-1-apr:1.9457191878341):0):0.21756267051392):0,(ML06-1-apr:0.908581281718701,(ML30-1-apr:1.99083484132707,ML04-1-apr:1.99083484132707):0.908581281718701):0):0):0,(ML03-1-apr:0.251567730013335,(ML23-1-apr:5.34809870517008,ML21-1-apr:5.34809870517008):0.251567730013335):0):0):0,((ML08-0-apr:2.65691250101799,ML24-1-apr:2.65691250101799):0,(ML25-1-apr:1.28244970725926,(ML10-1-apr:3.04588370938805,ML22-1_apr:3.04588370938805):1.28244970725926):0):0):0,((ML31-1-apr:4.88183288598203,ML32-1-apr:4.88183288598203):0,(ML16-1-apr:6.20616573758076,ML17-0-apr:6.20616573758076):0):0):0,(ML14-1-apr:0.811274241464437,(ML13-1-apr:1.02137701891036,((ML07-1-apr:7.3694483983459,ML23-0-apr:7.3694483983459):0,(ML21-0-apr:8.1467353148675,ML15-1-apr:8.1467353148675):0):1.02137701891036):0.811274241464437):0):0);";
        //newick ="(ML03-1-apr:0.251567730013335,(ML23-1-apr:5.34809870517008,ML21-1-apr:5.34809870517008):0.251567730013335);";

        //newick="(ML28-1-apr:0.21756267051392,((ML11-1-apr:1.72235253478789,ML22-0_apr:1.72235253478789):0.0,(ML26-1-apr:1.9457191878341,ML27-1-apr:1.9457191878341):0.0):0.21756267051392):0.0;";
        NodeI root = NewickParser.parse(newick);

        JFrame f = new JFrame();
        f.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        f.setSize(300, 600);
        Dendrogram d = new Dendrogram();
        d.showTree(root, 20);
        f.add(d);
        f.setVisible(true);
    }
}
