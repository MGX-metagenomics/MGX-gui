package de.cebitec.mgx.gui.charts.basic.util;

import java.util.List;
import org.jfree.data.category.CategoryDataset;
import org.jfree.data.category.DefaultCategoryDataset;
import org.jfree.data.general.DatasetChangeEvent;
import org.jfree.data.general.DatasetChangeListener;

public class SlidingCategoryDataset extends DefaultCategoryDataset implements DatasetChangeListener {

    private final CategoryDataset underlying;
    private double maxY;

    private int colOffset = 0;
    public static final int DEFAULT_WINSIZE = 25;
    private final int winSize;

    // rows = vgroups
    // columns = attribute
    //
    public SlidingCategoryDataset(CategoryDataset underlying) {
        this(underlying, DEFAULT_WINSIZE);
    }

    public SlidingCategoryDataset(CategoryDataset underlying, int windowSize) {
        this.underlying = underlying;
        this.underlying.addChangeListener(this);
        winSize = windowSize;

        maxY = Double.NEGATIVE_INFINITY;
        for (int row = 0; row < underlying.getRowCount(); row++) {
            for (int col = 0; col < underlying.getColumnCount(); col++) {
                Number n = underlying.getValue(row, col);
                if (n != null && n.doubleValue() > maxY) {
                    maxY = n.doubleValue();
                }
            }
        }
        //System.err.println("max value is " + maxY);
    }

    public int getWindowSize() {
        return winSize;
    }

    public void setOffset(int i) {
        //System.err.println("new offset "+ i);
        if (i < 0 || i > underlying.getColumnCount() - winSize) {
            throw new ArrayIndexOutOfBoundsException(i);
        }
        colOffset = i;
        fireDatasetChanged();
    }

    @Override
    public Number getValue(Comparable rowKey, Comparable columnKey) {
        return underlying.getValue(rowKey, columnKey);
    }

    @Override
    public List getColumnKeys() {
        List tmp = underlying.getColumnKeys();
        List ret = tmp.subList(colOffset, colOffset + winSize - 1);
        assert ret.size() <= winSize;
        return ret;
    }

    @Override
    public int getColumnIndex(Comparable key) {
        return underlying.getColumnIndex(key) - colOffset;
    }

    @Override
    public Comparable getColumnKey(int column) {
        return underlying.getColumnKey(column + colOffset);
    }

    @Override
    public int getRowIndex(Comparable key) {
        return underlying.getRowIndex(key);
    }

    @Override
    public Comparable getRowKey(int row) {
        return underlying.getRowKey(row);
    }

    @Override
    public Number getValue(int row, int column) {
        return underlying.getValue(row, column + colOffset);
    }

    @Override
    public int getColumnCount() {
        return winSize > underlying.getColumnCount() ? underlying.getColumnCount() : winSize;
    }

    public int getTotalColumnCount() {
        return underlying.getColumnCount();
    }

    @Override
    public List getRowKeys() {
        return underlying.getRowKeys();
    }

    @Override
    public int getRowCount() {
        return underlying.getRowCount();
    }

    @Override
    public void datasetChanged(DatasetChangeEvent dce) {
        this.fireDatasetChanged();
    }

    public double getMaxY() {
        return maxY;
    }
}
