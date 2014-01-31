package de.cebitec.mgx.gui.datamodel.misc;

import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 *
 * @author sj
 */
public class Matrix<T, U>{

    private final List<T> rows = new LinkedList<>();
    private final List<U> columns = new LinkedList<>();
    private int[][] _data = new int[0][0];

    public void addData(T rowAttr, U colAttr, int value) {
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

        _data[rowIdx][colIdx] += value;
    }

    public List<T> getRowHeaders() {
        return Collections.unmodifiableList(rows);
    }

    public List<U> getColumnHeaders() {
        return Collections.unmodifiableList(columns);
    }

    public int getRowSum(T a) {
        int ret = 0;
        int rowIdx = rows.indexOf(a);
        for (int i = 0; i < _data[rowIdx].length; i++) {
            ret += _data[rowIdx][i];
        }
        return ret;
    }

    public int getColumnSum(U a) {
        int ret = 0;
        int colIdx = columns.indexOf(a);
        for (int i = 0; i < _data.length; i++) {
            ret += _data[i][colIdx];
        }
        return ret;
    }

    public int getValue(T rowAttr, U colAttr) {
        int rowIdx = rows.indexOf(rowAttr);
        int colIdx = columns.indexOf(colAttr);
        return _data[rowIdx][colIdx];
    }

    public int[] getSize() {
        return new int[]{_data.length, _data[0].length};
    }

    public void add(Matrix<T, U> m) {
        for (T rAttr : m.getRowHeaders()) {
            for (U cAttr : m.getColumnHeaders()) {
                addData(rAttr, cAttr, m.getValue(rAttr, cAttr));
            }
        }
    }

    private void addRow() {
        int[][] newData = new int[rows.size()][columns.size()];

        // copy over old content
        for (int row = 0; row < _data.length; row++) {
            System.arraycopy(_data[row], 0, newData[row], 0, _data[0].length);
        }

        // initialize last row to zero
        Arrays.fill(newData[rows.size() - 1], 0);

        _data = newData;
    }

    private void addColumn() {
        for (int row = 0; row < rows.size(); row++) {
            // missing values are internally zeroed
            _data[row] = Arrays.copyOf(_data[row], columns.size());
        }
    }
}
