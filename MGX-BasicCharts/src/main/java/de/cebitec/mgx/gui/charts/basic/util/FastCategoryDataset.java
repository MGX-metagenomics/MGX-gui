/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.cebitec.mgx.gui.charts.basic.util;

import java.io.Serializable;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import org.jfree.chart.util.ParamChecks;
import org.jfree.data.DefaultKeyedValues;
import org.jfree.data.KeyedValues2D;
import org.jfree.data.UnknownKeyException;
import org.jfree.data.category.CategoryDataset;
import org.jfree.data.general.AbstractDataset;
import org.jfree.data.general.DatasetChangeEvent;
import org.jfree.util.ObjectUtilities;
import org.jfree.util.PublicCloneable;

/**
 *
 * @author sj
 */
public class FastCategoryDataset extends AbstractDataset
        implements CategoryDataset, PublicCloneable, Serializable {

    /**
     * For serialization.
     */
    private static final long serialVersionUID = -8168173757291644622L;

    /**
     * A storage structure for the data.
     */
    private FastKeyedValues2D data;

    /**
     * Creates a new (empty) dataset.
     */
    public FastCategoryDataset() {
        this.data = new FastKeyedValues2D();
    }

    /**
     * Returns the number of rows in the table.
     *
     * @return The row count.
     *
     * @see #getColumnCount()
     */
    @Override
    public int getRowCount() {
        return this.data.getRowCount();
    }

    /**
     * Returns the number of columns in the table.
     *
     * @return The column count.
     *
     * @see #getRowCount()
     */
    @Override
    public int getColumnCount() {
        return this.data.getColumnCount();
    }

    /**
     * Returns a value from the table.
     *
     * @param row the row index (zero-based).
     * @param column the column index (zero-based).
     *
     * @return The value (possibly <code>null</code>).
     *
     * @see #addValue(Number, Comparable, Comparable)
     * @see #removeValue(Comparable, Comparable)
     */
    @Override
    public Number getValue(int row, int column) {
        return this.data.getValue(row, column);
    }

    /**
     * Returns the key for the specified row.
     *
     * @param row the row index (zero-based).
     *
     * @return The row key.
     *
     * @see #getRowIndex(Comparable)
     * @see #getRowKeys()
     * @see #getColumnKey(int)
     */
    @Override
    public Comparable getRowKey(int row) {
        return this.data.getRowKey(row);
    }

    /**
     * Returns the row index for a given key.
     *
     * @param key the row key (<code>null</code> not permitted).
     *
     * @return The row index.
     *
     * @see #getRowKey(int)
     */
    @Override
    public int getRowIndex(Comparable key) {
        // defer null argument check
        return this.data.getRowIndex(key);
    }

    /**
     * Returns the row keys.
     *
     * @return The keys.
     *
     * @see #getRowKey(int)
     */
    @Override
    public List getRowKeys() {
        return this.data.getRowKeys();
    }

    /**
     * Returns a column key.
     *
     * @param column the column index (zero-based).
     *
     * @return The column key.
     *
     * @see #getColumnIndex(Comparable)
     */
    @Override
    public Comparable getColumnKey(int column) {
        return this.data.getColumnKey(column);
    }

    /**
     * Returns the column index for a given key.
     *
     * @param key the column key (<code>null</code> not permitted).
     *
     * @return The column index.
     *
     * @see #getColumnKey(int)
     */
    @Override
    public int getColumnIndex(Comparable key) {
        // defer null argument check
        return this.data.getColumnIndex(key);
    }

    /**
     * Returns the column keys.
     *
     * @return The keys.
     *
     * @see #getColumnKey(int)
     */
    @Override
    public List getColumnKeys() {
        return this.data.getColumnKeys();
    }

    /**
     * Returns the value for a pair of keys.
     *
     * @param rowKey the row key (<code>null</code> not permitted).
     * @param columnKey the column key (<code>null</code> not permitted).
     *
     * @return The value (possibly <code>null</code>).
     *
     * @throws UnknownKeyException if either key is not defined in the dataset.
     *
     * @see #addValue(Number, Comparable, Comparable)
     */
    @Override
    public Number getValue(Comparable rowKey, Comparable columnKey) {
        return this.data.getValue(rowKey, columnKey);
    }

    /**
     * Adds a value to the table. Performs the same function as setValue().
     *
     * @param value the value.
     * @param rowKey the row key.
     * @param columnKey the column key.
     *
     * @see #getValue(Comparable, Comparable)
     * @see #removeValue(Comparable, Comparable)
     */
    public void addValue(Number value, Comparable rowKey,
            Comparable columnKey) {
        this.data.addValue(value, rowKey, columnKey);
        fireDatasetChanged();
    }

    /**
     * Adds a value to the table.
     *
     * @param value the value.
     * @param rowKey the row key.
     * @param columnKey the column key.
     *
     * @see #getValue(Comparable, Comparable)
     */
    public void addValue(double value, Comparable rowKey,
            Comparable columnKey) {
        addValue(Double.valueOf(value), rowKey, columnKey);
    }

    /**
     * Adds or updates a value in the table and sends a
     * {@link DatasetChangeEvent} to all registered listeners.
     *
     * @param value the value (<code>null</code> permitted).
     * @param rowKey the row key (<code>null</code> not permitted).
     * @param columnKey the column key (<code>null</code> not permitted).
     *
     * @see #getValue(Comparable, Comparable)
     */
    public void setValue(Number value, Comparable rowKey,
            Comparable columnKey) {
        this.data.setValue(value, rowKey, columnKey);
        fireDatasetChanged();
    }

    /**
     * Adds or updates a value in the table and sends a
     * {@link DatasetChangeEvent} to all registered listeners.
     *
     * @param value the value.
     * @param rowKey the row key (<code>null</code> not permitted).
     * @param columnKey the column key (<code>null</code> not permitted).
     *
     * @see #getValue(Comparable, Comparable)
     */
    public void setValue(double value, Comparable rowKey,
            Comparable columnKey) {
        setValue(Double.valueOf(value), rowKey, columnKey);
    }

    /**
     * Adds the specified value to an existing value in the dataset (if the
     * existing value is <code>null</code>, it is treated as if it were 0.0).
     *
     * @param value the value.
     * @param rowKey the row key (<code>null</code> not permitted).
     * @param columnKey the column key (<code>null</code> not permitted).
     *
     * @throws UnknownKeyException if either key is not defined in the dataset.
     */
    public void incrementValue(double value,
            Comparable rowKey,
            Comparable columnKey) {
        double existing = 0.0;
        Number n = getValue(rowKey, columnKey);
        if (n != null) {
            existing = n.doubleValue();
        }
        setValue(existing + value, rowKey, columnKey);
    }

    /**
     * Removes a value from the dataset and sends a {@link DatasetChangeEvent}
     * to all registered listeners.
     *
     * @param rowKey the row key.
     * @param columnKey the column key.
     *
     * @see #addValue(Number, Comparable, Comparable)
     */
    public void removeValue(Comparable rowKey, Comparable columnKey) {
        this.data.removeValue(rowKey, columnKey);
        fireDatasetChanged();
    }

    /**
     * Removes a row from the dataset and sends a {@link DatasetChangeEvent} to
     * all registered listeners.
     *
     * @param rowIndex the row index.
     *
     * @see #removeColumn(int)
     */
    public void removeRow(int rowIndex) {
        this.data.removeRow(rowIndex);
        fireDatasetChanged();
    }

    /**
     * Removes a row from the dataset and sends a {@link DatasetChangeEvent} to
     * all registered listeners.
     *
     * @param rowKey the row key.
     *
     * @see #removeColumn(Comparable)
     */
    public void removeRow(Comparable rowKey) {
        this.data.removeRow(rowKey);
        fireDatasetChanged();
    }

    /**
     * Removes a column from the dataset and sends a {@link DatasetChangeEvent}
     * to all registered listeners.
     *
     * @param columnIndex the column index.
     *
     * @see #removeRow(int)
     */
    public void removeColumn(int columnIndex) {
        this.data.removeColumn(columnIndex);
        fireDatasetChanged();
    }

    /**
     * Removes a column from the dataset and sends a {@link DatasetChangeEvent}
     * to all registered listeners.
     *
     * @param columnKey the column key (<code>null</code> not permitted).
     *
     * @see #removeRow(Comparable)
     *
     * @throws UnknownKeyException if <code>columnKey</code> is not defined in
     * the dataset.
     */
    public void removeColumn(Comparable columnKey) {
        this.data.removeColumn(columnKey);
        fireDatasetChanged();
    }

    /**
     * Clears all data from the dataset and sends a {@link DatasetChangeEvent}
     * to all registered listeners.
     */
    public void clear() {
        this.data.clear();
        fireDatasetChanged();
    }

    /**
     * Tests this dataset for equality with an arbitrary object.
     *
     * @param obj the object (<code>null</code> permitted).
     *
     * @return A boolean.
     */
    @Override
    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (!(obj instanceof CategoryDataset)) {
            return false;
        }
        CategoryDataset that = (CategoryDataset) obj;
        if (!getRowKeys().equals(that.getRowKeys())) {
            return false;
        }
        if (!getColumnKeys().equals(that.getColumnKeys())) {
            return false;
        }
        int rowCount = getRowCount();
        int colCount = getColumnCount();
        for (int r = 0; r < rowCount; r++) {
            for (int c = 0; c < colCount; c++) {
                Number v1 = getValue(r, c);
                Number v2 = that.getValue(r, c);
                if (v1 == null) {
                    if (v2 != null) {
                        return false;
                    }
                } else if (!v1.equals(v2)) {
                    return false;
                }
            }
        }
        return true;
    }

    /**
     * Returns a hash code for the dataset.
     *
     * @return A hash code.
     */
    @Override
    public int hashCode() {
        return this.data.hashCode();
    }

    /**
     * Returns a clone of the dataset.
     *
     * @return A clone.
     *
     * @throws CloneNotSupportedException if there is a problem cloning the
     * dataset.
     */
    @Override
    public Object clone() throws CloneNotSupportedException {
        FastCategoryDataset clone = (FastCategoryDataset) super.clone();
        clone.data = (FastKeyedValues2D) this.data.clone();
        return clone;
    }

    private static class FastKeyedValues2D
            implements KeyedValues2D, PublicCloneable,
            Cloneable, Serializable {

        /**
         * For serialization.
         */
        private static final long serialVersionUID = -5514169970951994748L;

        /**
         * The row keys.
         */
        private List<Comparable> rowKeys;

        /**
         * The column keys.
         */
        private List<Comparable> columnKeys;

        /**
         * The row key index map
         */
        private Map<Object, Integer> rowKeyIndexMap;

        /**
         * The column key index map
         */
        private Map<Object, Integer> columnKeyIndexMap;

        /**
         * The row data.
         */
        private List<DefaultKeyedValues> rows;

        /**
         * If the row keys should be sorted by their comparable order.
         */
        private boolean sortRowKeys;

        /**
         * Creates a new instance (initially empty).
         */
        public FastKeyedValues2D() {
            this(false);
        }

        /**
         * Creates a new instance (initially empty).
         *
         * @param sortRowKeys if the row keys should be sorted.
         */
        public FastKeyedValues2D(boolean sortRowKeys) {
            this.rowKeys = new java.util.ArrayList<>();
            this.columnKeys = new java.util.ArrayList<>();
            this.rows = new java.util.ArrayList<>();
            this.rowKeyIndexMap = new java.util.HashMap<>();
            this.columnKeyIndexMap = new java.util.HashMap<>();
            this.sortRowKeys = sortRowKeys;
        }

        /**
         * Returns the row count.
         *
         * @return The row count.
         *
         * @see #getColumnCount()
         */
        @Override
        public int getRowCount() {
            return this.rowKeys.size();
        }

        /**
         * Returns the column count.
         *
         * @return The column count.
         *
         * @see #getRowCount()
         */
        @Override
        public int getColumnCount() {
            return this.columnKeys.size();
        }

        /**
         * Returns the value for a given row and column.
         *
         * @param row the row index.
         * @param column the column index.
         *
         * @return The value.
         *
         * @see #getValue(Comparable, Comparable)
         */
        @Override
        public Number getValue(int row, int column) {
            Number result = null;
            DefaultKeyedValues rowData = this.rows.get(row);
            if (rowData != null) {
                Comparable columnKey = this.columnKeys.get(column);
                // the row may not have an entry for this key, in which case the
                // return value is null
                int index = rowData.getIndex(columnKey);
                if (index >= 0) {
                    result = rowData.getValue(index);
                }
            }
            return result;
        }

        /**
         * Returns the key for a given row.
         *
         * @param row the row index (in the range 0 to {@link #getRowCount()} -
         * 1).
         *
         * @return The row key.
         *
         * @see #getRowIndex(Comparable)
         * @see #getColumnKey(int)
         */
        @Override
        public Comparable getRowKey(int row) {
            return this.rowKeys.get(row);
        }

        /**
         * Returns the row index for a given key.
         *
         * @param key the key (<code>null</code> not permitted).
         *
         * @return The row index.
         *
         * @see #getRowKey(int)
         * @see #getColumnIndex(Comparable)
         */
        @Override
        @SuppressWarnings("unchecked")
        public int getRowIndex(Comparable key) {
            ParamChecks.nullNotPermitted(key, "key");
            if (this.sortRowKeys) {
                List x = rowKeys;
                return Collections.binarySearch(x, key);
            } else {
                if (!this.rowKeyIndexMap.containsKey(key)) {
                    return -1;
                }
                return this.rowKeyIndexMap.get(key);
            }
        }

        /**
         * Returns the row keys in an unmodifiable list.
         *
         * @return The row keys.
         *
         * @see #getColumnKeys()
         */
        @Override
        public List<Comparable> getRowKeys() {
            return Collections.<Comparable>unmodifiableList(this.rowKeys);
        }

        /**
         * Returns the key for a given column.
         *
         * @param column the column (in the range 0 to {@link #getColumnCount()}
         * - 1).
         *
         * @return The key.
         *
         * @see #getColumnIndex(Comparable)
         * @see #getRowKey(int)
         */
        @Override
        public Comparable getColumnKey(int column) {
            return this.columnKeys.get(column);
        }

        /**
         * Returns the column index for a given key.
         *
         * @param key the key (<code>null</code> not permitted).
         *
         * @return The column index.
         *
         * @see #getColumnKey(int)
         * @see #getRowIndex(Comparable)
         */
        @Override
        public int getColumnIndex(Comparable key) {
            ParamChecks.nullNotPermitted(key, "key");
            if (!this.columnKeyIndexMap.containsKey(key)) {
                return -1;
            }
            return this.columnKeyIndexMap.get(key);
        }

        /**
         * Returns the column keys in an unmodifiable list.
         *
         * @return The column keys.
         *
         * @see #getRowKeys()
         */
        @Override
        public List getColumnKeys() {
            return Collections.unmodifiableList(this.columnKeys);
        }

        /**
         * Returns the value for the given row and column keys. This method will
         * throw an {@link UnknownKeyException} if either key is not defined in
         * the data structure.
         *
         * @param rowKey the row key (<code>null</code> not permitted).
         * @param columnKey the column key (<code>null</code> not permitted).
         *
         * @return The value (possibly <code>null</code>).
         *
         * @see #addValue(Number, Comparable, Comparable)
         * @see #removeValue(Comparable, Comparable)
         */
        @Override
        public Number getValue(Comparable rowKey, Comparable columnKey) {
            ParamChecks.nullNotPermitted(rowKey, "rowKey");
            ParamChecks.nullNotPermitted(columnKey, "columnKey");

            // check that the column key is defined in the 2D structure
            if (!(this.columnKeys.contains(columnKey))) {
                throw new UnknownKeyException("Unrecognised columnKey: "
                        + columnKey);
            }

            // now fetch the row data - need to bear in mind that the row
            // structure may not have an entry for the column key, but that we
            // have already checked that the key is valid for the 2D structure
            int row = getRowIndex(rowKey);
            if (row >= 0) {
                DefaultKeyedValues rowData
                        = this.rows.get(row);
                int col = rowData.getIndex(columnKey);
                return (col >= 0 ? rowData.getValue(col) : null);
            } else {
                throw new UnknownKeyException("Unrecognised rowKey: " + rowKey);
            }
        }

        /**
         * Adds a value to the table. Performs the same function as
         * #setValue(Number, Comparable, Comparable).
         *
         * @param value the value (<code>null</code> permitted).
         * @param rowKey the row key (<code>null</code> not permitted).
         * @param columnKey the column key (<code>null</code> not permitted).
         *
         * @see #setValue(Number, Comparable, Comparable)
         * @see #removeValue(Comparable, Comparable)
         */
        public void addValue(Number value, Comparable rowKey,
                Comparable columnKey) {
            // defer argument checking
            setValue(value, rowKey, columnKey);
        }

        /**
         * Adds or updates a value.
         *
         * @param value the value (<code>null</code> permitted).
         * @param rowKey the row key (<code>null</code> not permitted).
         * @param columnKey the column key (<code>null</code> not permitted).
         *
         * @see #addValue(Number, Comparable, Comparable)
         * @see #removeValue(Comparable, Comparable)
         */
        public void setValue(Number value, Comparable rowKey,
                Comparable columnKey) {

            DefaultKeyedValues row;
            int rowIndex = getRowIndex(rowKey);

            if (rowIndex >= 0) {
                row = this.rows.get(rowIndex);
            } else {
                row = new DefaultKeyedValues();
                if (this.sortRowKeys) {
                    rowIndex = -rowIndex - 1;
                    this.rowKeys.add(rowIndex, rowKey);
                    this.rows.add(rowIndex, row);
                    this.rowKeyIndexMap.put(rowKey, rowIndex);
                } else {
                    this.rowKeys.add(rowKey);
                    this.rows.add(row);
                    this.rowKeyIndexMap.put(rowKey, rowKeys.size() - 1);
                }
            }
            row.setValue(columnKey, value);

            if (!this.columnKeyIndexMap.containsKey(columnKey)) {
                this.columnKeys.add(columnKey);
                this.columnKeyIndexMap.put(columnKey, columnKeys.size() - 1);
            }
        }

        /**
         * Removes a value from the table by setting it to <code>null</code>. If
         * all the values in the specified row and/or column are now
         * <code>null</code>, the row and/or column is removed from the table.
         *
         * @param rowKey the row key (<code>null</code> not permitted).
         * @param columnKey the column key (<code>null</code> not permitted).
         *
         * @see #addValue(Number, Comparable, Comparable)
         */
        public void removeValue(Comparable rowKey, Comparable columnKey) {
            setValue(null, rowKey, columnKey);

            // 1. check whether the row is now empty.
            boolean allNull = true;
            int rowIndex = getRowIndex(rowKey);
            DefaultKeyedValues row = this.rows.get(rowIndex);

            for (int item = 0, itemCount = row.getItemCount(); item < itemCount;
                    item++) {
                if (row.getValue(item) != null) {
                    allNull = false;
                    break;
                }
            }

            if (allNull) {
                this.rowKeys.remove(rowIndex);
                this.rows.remove(rowIndex);
                this.rowKeyIndexMap.remove(rowKey);
            }

            // 2. check whether the column is now empty.
            allNull = true;
            //int columnIndex = getColumnIndex(columnKey);

            for (int item = 0, itemCount = this.rows.size(); item < itemCount;
                    item++) {
                row = this.rows.get(item);
                int columnIndex = row.getIndex(columnKey);
                if (columnIndex >= 0 && row.getValue(columnIndex) != null) {
                    allNull = false;
                    break;
                }
            }

            if (allNull) {
                for (int item = 0, itemCount = this.rows.size(); item < itemCount;
                        item++) {
                    row = this.rows.get(item);
                    int columnIndex = row.getIndex(columnKey);
                    if (columnIndex >= 0) {
                        row.removeValue(columnIndex);
                    }
                }
                this.columnKeys.remove(columnKey);
                this.columnKeyIndexMap.remove(columnKey);
            }
        }

        /**
         * Removes a row.
         *
         * @param rowIndex the row index.
         *
         * @see #removeRow(Comparable)
         * @see #removeColumn(int)
         */
        public void removeRow(int rowIndex) {
            this.rowKeyIndexMap.remove(rowKeys.get(rowIndex));
            this.rowKeys.remove(rowIndex);
            this.rows.remove(rowIndex);
        }

        /**
         * Removes a row from the table.
         *
         * @param rowKey the row key (<code>null</code> not permitted).
         *
         * @see #removeRow(int)
         * @see #removeColumn(Comparable)
         *
         * @throws UnknownKeyException if <code>rowKey</code> is not defined in
         * the table.
         */
        public void removeRow(Comparable rowKey) {
            ParamChecks.nullNotPermitted(rowKey, "rowKey");
            int index = getRowIndex(rowKey);
            if (index >= 0) {
                removeRow(index);
            } else {
                throw new UnknownKeyException("Unknown key: " + rowKey);
            }
        }

        /**
         * Removes a column.
         *
         * @param columnIndex the column index.
         *
         * @see #removeColumn(Comparable)
         * @see #removeRow(int)
         */
        public void removeColumn(int columnIndex) {
            Comparable columnKey = getColumnKey(columnIndex);
            removeColumn(columnKey);
        }

        /**
         * Removes a column from the table.
         *
         * @param columnKey the column key (<code>null</code> not permitted).
         *
         * @throws UnknownKeyException if the table does not contain a column
         * with the specified key.
         * @throws IllegalArgumentException if <code>columnKey</code> is
         * <code>null</code>.
         *
         * @see #removeColumn(int)
         * @see #removeRow(Comparable)
         */
        public void removeColumn(Comparable columnKey) {
            ParamChecks.nullNotPermitted(columnKey, "columnKey");
            if (!this.columnKeys.contains(columnKey)) {
                throw new UnknownKeyException("Unknown key: " + columnKey);
            }
            Iterator<DefaultKeyedValues> iterator = this.rows.iterator();
            while (iterator.hasNext()) {
                DefaultKeyedValues rowData = iterator.next();
                int index = rowData.getIndex(columnKey);
                if (index >= 0) {
                    rowData.removeValue(columnKey);
                }
            }
            this.columnKeys.remove(columnKey);
            this.columnKeyIndexMap.remove(columnKey);
        }

        /**
         * Clears all the data and associated keys.
         */
        public void clear() {
            this.rowKeys.clear();
            this.columnKeys.clear();
            this.rows.clear();
            this.columnKeyIndexMap.clear();
            this.rowKeyIndexMap.clear();
        }

        /**
         * Tests if this object is equal to another.
         *
         * @param o the other object (<code>null</code> permitted).
         *
         * @return A boolean.
         */
        @Override
        public boolean equals(Object o) {

            if (o == null) {
                return false;
            }
            if (o == this) {
                return true;
            }

            if (!(o instanceof KeyedValues2D)) {
                return false;
            }
            KeyedValues2D kv2D = (KeyedValues2D) o;
            if (!getRowKeys().equals(kv2D.getRowKeys())) {
                return false;
            }
            if (!getColumnKeys().equals(kv2D.getColumnKeys())) {
                return false;
            }
            int rowCount = getRowCount();
            if (rowCount != kv2D.getRowCount()) {
                return false;
            }

            int colCount = getColumnCount();
            if (colCount != kv2D.getColumnCount()) {
                return false;
            }

            for (int r = 0; r < rowCount; r++) {
                for (int c = 0; c < colCount; c++) {
                    Number v1 = getValue(r, c);
                    Number v2 = kv2D.getValue(r, c);
                    if (v1 == null) {
                        if (v2 != null) {
                            return false;
                        }
                    } else {
                        if (!v1.equals(v2)) {
                            return false;
                        }
                    }
                }
            }
            return true;
        }

        /**
         * Returns a hash code.
         *
         * @return A hash code.
         */
        @Override
        public int hashCode() {
            int result;
            result = this.rowKeys.hashCode();
            result = 29 * result + this.columnKeys.hashCode();
            result = 29 * result + this.rows.hashCode();
            return result;
        }

        /**
         * Returns a clone.
         *
         * @return A clone.
         *
         * @throws CloneNotSupportedException this class will not throw this
         * exception, but subclasses (if any) might.
         */
        @Override
        @SuppressWarnings("unchecked")
        public Object clone() throws CloneNotSupportedException {
            FastKeyedValues2D clone = (FastKeyedValues2D) super.clone();
            // for the keys, a shallow copy should be fine because keys
            // should be immutable...
            clone.columnKeys = new java.util.ArrayList<>(this.columnKeys);
            clone.rowKeys = new java.util.ArrayList<>(this.rowKeys);
            clone.columnKeyIndexMap
                    = new java.util.HashMap<>(this.columnKeyIndexMap);
            clone.rowKeyIndexMap
                    = new java.util.HashMap<>(this.rowKeyIndexMap);
            // but the row data requires a deep copy
            clone.rows = (List) ObjectUtilities.deepClone(this.rows);
            return clone;
        }
    }
}
