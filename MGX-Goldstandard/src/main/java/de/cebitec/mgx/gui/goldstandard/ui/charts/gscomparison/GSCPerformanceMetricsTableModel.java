package de.cebitec.mgx.gui.goldstandard.ui.charts.gscomparison;

import de.cebitec.mgx.gui.goldstandard.util.PerformanceMetrics;
import java.io.Serial;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Locale;
import javax.swing.table.AbstractTableModel;

/**
 *
 * @author patrick
 */
public class GSCPerformanceMetricsTableModel extends AbstractTableModel {

    @Serial
    private static final long serialVersionUID = 1L;

    private final PerformanceMetrics[] data;
    private final String[] header;

    public GSCPerformanceMetricsTableModel(String[] header, PerformanceMetrics[] data) {
        this.data = data;
        this.header = header;
    }

    @Override
    public int getRowCount() {
        return 13;
    }

    @Override
    public int getColumnCount() {
        return header.length;
    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        String value = null;
        if (columnIndex == 0) {
            switch (rowIndex) {
                case 0:
                    value = "True positive";
                    break;
                case 1:
                    value = "False positive";
                    break;
                case 2:
                    value = "False negative";
                    break;
                case 3:
                    value = "True negative";
                    break;
                case 4:
                    value = "Sensitivity";
                    break;
                case 5:
                    value = "Specificity";
                    break;
                case 6:
                    value = "Precision";
                    break;
                case 7:
                    value = "Negative predictive value";
                    break;
                case 8:
                    value = "False positive rate";
                    break;
                case 9:
                    value = "False negative rate";
                    break;
                case 10:
                    value = "False discovery rate";
                    break;
                case 11:
                    value = "Accuracy";
                    break;
                case 12:
                    value = "F1 score";
                    break;
//                case 13:
//                    value = "Matthews correlation coefficient";
//                    break;
            }
        } else {
            DecimalFormat formatter = (DecimalFormat) NumberFormat.getInstance(Locale.US);
            String format = "%.4f";
            switch (rowIndex) {
                case 0:
                    value = formatter.format(data[columnIndex - 1].getTP());
                    break;
                case 1:
                    value = formatter.format(data[columnIndex - 1].getFP());
                    break;
                case 2:
                    value = formatter.format(data[columnIndex - 1].getFN());
                    break;
                case 3:
                    value = formatter.format(data[columnIndex - 1].getTN());
                    break;
                case 4:
                    value = String.format(format, data[columnIndex - 1].getSensitivity());
                    break;
                case 5:
                    value = String.format(format, data[columnIndex - 1].getSpecificity());
                    break;
                case 6:
                    value = String.format(format, data[columnIndex - 1].getPrecision());
                    break;
                case 7:
                    value = String.format(format, data[columnIndex - 1].getNegativePredictiveValue());
                    break;
                case 8:
                    value = String.format(format, data[columnIndex - 1].getFalsePositiveRate());
                    break;
                case 9:
                    value = String.format(format, data[columnIndex - 1].getFalseNegativeRate());
                    break;
                case 10:
                    value = String.format(format, data[columnIndex - 1].getFalseDiscoveryRate());
                    break;
                case 11:
                    value = String.format(format, data[columnIndex - 1].getAccuracy());
                    break;
                case 12:
                    value = String.format(format, data[columnIndex - 1].getF1Score());
                    break;
//                case 13:
//                    value = String.format(format, data[columnIndex - 1].getMatthewsCorrelationCoefficient());
//                    break;
            }
        }
        return value;
    }

    @Override
    public Class<?> getColumnClass(int column) {
        return String.class;
    }

    @Override
    public String getColumnName(int col) {
        return header[col];
    }

    @Override
    public boolean isCellEditable(int row, int column) {
        return false;
    }
}
