package de.cebitec.mgx.gui.datamodel.misc;

import de.cebitec.mgx.gui.datamodel.Attribute;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

/**
 *
 * @author sj
 */
public class Matrix {

    private List<Attribute> rows = new LinkedList<>();
    private List<Attribute> columns = new LinkedList<>();
    private double[][] _data = new double[0][0];

    public void addData(Attribute rowAttr, Attribute colAttr, double value) {
        if (!rows.contains(rowAttr)) {
            rows.add(rowAttr);
        }
        if (!columns.contains(colAttr)) {
            columns.add(colAttr);
        }
        int rowIdx = rows.indexOf(rowAttr);
        int colIdx = columns.indexOf(colAttr);

        while (_data.length < rows.size()) {
            addRow();
        }
        while (_data[0].length < columns.size()) {
            addColumn();
        }

        _data[rowIdx][colIdx] = value;
    }

    private void addRow() {
        double[][] newData = new double[rows.size()][columns.size()];

        // copy over old content
        for (int row = 0; row < rows.size(); row++) {
            for (int col = 0; col < columns.size(); col++) {
                newData[row][col] = _data[row][col];
            }
        }

        // initialize last row to zero
        Arrays.fill(newData[rows.size() - 1], 0);

        _data = newData;
    }

    private void addColumn() {
        for (int row = 0; row < rows.size(); row++) {
            _data[row] = Arrays.copyOf(_data[row], columns.size());
        }
    }
}
