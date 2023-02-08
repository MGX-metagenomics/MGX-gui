/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.cebitec.mgx.api.groups;

import static org.junit.Assert.*;
import org.junit.Test;

/**
 *
 * @author sj
 */
public class FileTypeTest {

    @Test
    public void testLoadPackage() {
        System.err.println("testLoadPackage");
        FileType ft = FileType.FAS;
        assertNotNull(ft);
    }

}
