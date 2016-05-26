package de.cebitec.mgx.api.misc;

/**
 *
 * @author sjaenick
 */
public enum PrincipalComponent {
    
    /*
     * principal components 
    */

    PC1("PC1", 1),
    PC2("PC2", 2),
    PC3("PC3", 3);

    private final String name;

    private final int value;

    private PrincipalComponent(String name, int val) {
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
