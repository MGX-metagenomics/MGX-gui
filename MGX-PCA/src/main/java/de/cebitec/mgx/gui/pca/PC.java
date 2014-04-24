package de.cebitec.mgx.gui.pca;

/**
 *
 * @author sjaenick
 */
public enum PC {

    PC1("PC1", 1),
    PC2("PC2", 2),
    PC3("PC3", 3);

    private final String name;

    private final int value;

    private PC(String name, int val) {
        this.name = name;
        this.value = val;
    }

    @Override
    public String toString() {
        return name;
    }

    public int getValue() {
        return value;
    }

}
