/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.cebitec.mgx.api.access;

import de.cebitec.mgx.api.exception.MGXException;
import de.cebitec.mgx.api.model.HabitatI;

/**
 *
 * @author sj
 */
public interface HabitatAccessI extends AccessBaseI<HabitatI> {

    public HabitatI create(String name, double latitude, double longitude, String biome, String description) throws MGXException;
}
