/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.cebitec.mgx.api.model;

import de.cebitec.mgx.api.MGXMasterI;
import java.awt.datatransfer.DataFlavor;

/**
 *
 * @author sj
 */
public abstract class MGXFileI extends MGXDataModelBase<MGXFileI> {

    public static final String ROOT_PATH = ".";
    public static final String separator = "|";
    public static final DataFlavor DATA_FLAVOR = new DataFlavor(MGXFileI.class, "MGXFileI");

    public static MGXFileI getRoot(final MGXMasterI m) {
        return new MGXFileI(m) {

            @Override
            public void setParent(MGXFileI parent) {
            }

            @Override
            public MGXFileI getParent() {
                return null;
            }

            @Override
            public boolean isDirectory() {
                return true;
            }

            @Override
            public long getSize() {
                return 0;
            }

            @Override
            public String getName() {
                int sepPos = getFullPath().lastIndexOf(separator);
                return getFullPath().substring(sepPos + 1);
            }

            @Override
            public String getFullPath() {
                return ROOT_PATH;
            }

            @Override
            public int compareTo(MGXFileI o) {
                if (isDirectory() == o.isDirectory()) {
                    return this.getFullPath().compareTo(o.getFullPath());
                } else {
                    if (isDirectory()) {
                        return -1;
                    }
                    return 1;
                }
            }
        };
    }
    
    public MGXFileI(MGXMasterI m) {
        super(m, DATA_FLAVOR);
    }

    public abstract void setParent(MGXFileI parent);

    public abstract MGXFileI getParent();

    public abstract boolean isDirectory();

    public abstract long getSize();

    public abstract String getName();

    public abstract String getFullPath();

    @Override
    public abstract int compareTo(MGXFileI o);

}
