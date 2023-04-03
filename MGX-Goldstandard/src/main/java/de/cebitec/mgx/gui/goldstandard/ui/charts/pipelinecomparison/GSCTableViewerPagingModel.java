package de.cebitec.mgx.gui.goldstandard.ui.charts.pipelinecomparison;

import de.cebitec.mgx.api.MGXMasterI;
import de.cebitec.mgx.api.exception.MGXException;
import de.cebitec.mgx.api.model.SequenceI;
import gnu.trove.map.TLongObjectMap;
import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics;
import java.awt.Polygon;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.Serial;
import java.util.Arrays;
import java.util.Iterator;
import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.ScrollPaneConstants;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.TableModel;
import org.openide.util.Exceptions;

/**
 * Java Swing, 2nd Edition By Marc Loy, Robert Eckstein, Dave Wood, James
 * Elliott, Brian Cole ISBN: 0-596-00408-7 Publisher: O'Reilly
 *
 * @author pblumenk
 */
public class GSCTableViewerPagingModel extends AbstractTableModel {

    @Serial
    private static final long serialVersionUID = 1L;

    private int pageSize;

    private int pageOffset;

    private TLongObjectMap<String[]> data;
    private long[] keys;
    private String[] seqHeaders;

    private String[] colHeader;

    private MGXMasterI master;

    public GSCTableViewerPagingModel(TLongObjectMap<String[]> data, String[] colHeader, MGXMasterI master) {
        this(data, colHeader, master, 1_000);
    }

    public GSCTableViewerPagingModel(TLongObjectMap<String[]> data, String[] colHeader, MGXMasterI master, int pageSize) {
        this.data = data;
        this.keys = data.keys();
        Arrays.sort(keys);
        this.colHeader = colHeader;
        this.pageSize = pageSize;
        this.master = master;
        this.seqHeaders = new String[pageSize];
        updateSeqHeader();
    }

    public TLongObjectMap<String[]> getData() {
        return data;
    }

    // Return values appropriate for the visible table part.
    @Override
    public int getRowCount() {
        return Math.min(pageSize, data.size());
    }

    @Override
    public int getColumnCount() {
        return 4;
    }

    // Work only on the visible part of the table.
    @Override
    public Object getValueAt(int row, int col) {
        long id = keys[row + (getPageOffset() * getPageSize())];
        if (col == 0) {
            return seqHeaders[row];
        }
        String[] strList = data.get(id);
        if (strList[0].equals(strList[1])) {
            return (col == 2) ? strList[1] : "";
        } else {
            switch (col) {
                case 1:
                    return strList[0];
                case 2:
                    return "";
                case 3:
                    return strList[1];
            }
        }
        return "";
    }

    /*
    * returns ID if col == 0, not the header!
     */
    public Object getRealValueAt(int row, int col) {
        long id = keys[row];
        if (col == 0) {
            return id;
        }
        String[] strList = data.get(id);
        if (strList[0].equals(strList[1])) {
            return (col == 2) ? strList[1] : "";
        } else {
            switch (col) {
                case 1:
                    return strList[0];
                case 2:
                    return "";
                case 3:
                    return strList[1];
                default:
                    return null;
            }
        }
    }

    @Override
    public String getColumnName(int col) {
        return colHeader[col];
    }

    // Use this method to figure out which page you are on.
    public int getPageOffset() {
        return pageOffset;
    }

    public int getPageCount() {
        return (int) Math.ceil((double) data.size() / pageSize);
    }

    // Use this method if you want to know how big the real table is . . . we
    // could also write "getRealValueAt()" if needed.
    public int getRealRowCount() {
        return data.size();
    }

    public int getPageSize() {
        return pageSize;
    }

    public void setPageSize(int s) {
        if (s == pageSize) {
            return;
        }
        int oldPageSize = pageSize;
        pageSize = s;
        pageOffset = (oldPageSize * pageOffset) / pageSize;
        fireTableDataChanged();
        /*
     * if (pageSize < oldPageSize) { fireTableRowsDeleted(pageSize,
     * oldPageSize - 1); } else { fireTableRowsInserted(oldPageSize,
     * pageSize - 1); }
         */
    }

    // Update the page offset and fire a data changed (all rows).
    public void pageDown() {
        if (pageOffset < getPageCount() - 1) {
            pageOffset++;
            fireTableDataChanged();
        }
        updateSeqHeader();
    }

    // Update the page offset and fire a data changed (all rows).
    public void pageUp() {
        if (pageOffset > 0) {
            pageOffset--;
            fireTableDataChanged();
        }
        updateSeqHeader();
    }

    // We provide our own version of a scrollpane that includes
    // the page up and page down buttons by default.
    public static JScrollPane createPagingScrollPaneForTable(JTable jt) {
        JScrollPane jsp = new JScrollPane(jt);
        TableModel tmodel = jt.getModel();

        // Don't choke if this is called on a regular table . . .
        if (!(tmodel instanceof GSCTableViewerPagingModel)) {
            return jsp;
        }

        // Okay, go ahead and build the real scrollpane
        final GSCTableViewerPagingModel model = (GSCTableViewerPagingModel) tmodel;
        final JButton upButton = new JButton(new ArrowIcon(ArrowIcon.UP));
        upButton.setEnabled(false); // starts off at 0, so can't go up
        final JButton downButton = new JButton(new ArrowIcon(ArrowIcon.DOWN));
        if (model.getPageCount() <= 1) {
            downButton.setEnabled(false); // One page...can't scroll down
        }

        upButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent ae) {
                model.pageUp();

                // If we hit the top of the data, disable the up button.
                if (model.getPageOffset() == 0) {
                    upButton.setEnabled(false);
                }
                downButton.setEnabled(true);
            }
        });

        downButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent ae) {
                model.pageDown();

                // If we hit the bottom of the data, disable the down button.
                if (model.getPageOffset() == (model.getPageCount() - 1)) {
                    downButton.setEnabled(false);
                }
                upButton.setEnabled(true);
            }
        });

        // Turn on the scrollbars; otherwise we won't get our corners.
        jsp.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
        jsp.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_ALWAYS);

        // Add in the corners (page up/down).
        jsp.setCorner(ScrollPaneConstants.UPPER_RIGHT_CORNER, upButton);
        jsp.setCorner(ScrollPaneConstants.LOWER_RIGHT_CORNER, downButton);

        return jsp;
    }

    private void updateSeqHeader() {
        try {
            long[] ids;
            if (data.size() - pageOffset * pageSize < pageSize) {
                ids = Arrays.copyOfRange(keys, pageOffset * pageSize, data.size());
            } else {
                ids = Arrays.copyOfRange(keys, pageOffset * pageSize, pageOffset * pageSize + pageSize);
            }
            Iterator<SequenceI> it = master.Sequence().fetchByIds(ids);
            int i = 0;
            while (it.hasNext()) {
                seqHeaders[i++] = it.next().getName();
            }
            for (; i < pageSize; i++) {
                seqHeaders[i] = null;
            }
        } catch (MGXException ex) {
            Exceptions.printStackTrace(ex);
            seqHeaders = null;
        }
    }
}

class ArrowIcon implements Icon {

    public static final int UP = 0;

    public static final int DOWN = 1;

    private final int direction;

    private final Polygon pagePolygon = new Polygon(new int[]{2, 4, 4, 10, 10, 2},
            new int[]{4, 4, 2, 2, 12, 12}, 6);

    private final int[] arrowX = {4, 9, 6};

    private final Polygon arrowUpPolygon = new Polygon(arrowX,
            new int[]{10, 10, 4}, 3);

    private final Polygon arrowDownPolygon = new Polygon(arrowX,
            new int[]{6, 6, 11}, 3);

    public ArrowIcon(int which) {
        direction = which;
    }

    @Override
    public int getIconWidth() {
        return 14;
    }

    @Override
    public int getIconHeight() {
        return 14;
    }

    @Override
    public void paintIcon(Component c, Graphics g, int x, int y) {
        g.setColor(Color.black);
        pagePolygon.translate(x, y);
        g.drawPolygon(pagePolygon);
        pagePolygon.translate(-x, -y);
        if (direction == UP) {
            arrowUpPolygon.translate(x, y);
            g.fillPolygon(arrowUpPolygon);
            arrowUpPolygon.translate(-x, -y);
        } else {
            arrowDownPolygon.translate(x, y);
            g.fillPolygon(arrowDownPolygon);
            arrowDownPolygon.translate(-x, -y);
        }
    }
}
