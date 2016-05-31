/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.cebitec.mgx.api.access;

import de.cebitec.mgx.api.exception.MGXException;
import de.cebitec.mgx.api.groups.VisualizationGroupI;
import de.cebitec.mgx.api.misc.DistributionI;
import de.cebitec.mgx.api.misc.PrincipalComponent;
import de.cebitec.mgx.api.misc.PCAResultI;
import de.cebitec.mgx.api.misc.Pair;
import de.cebitec.mgx.api.misc.Point;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

/**
 *
 * @author sj
 */
public interface StatisticsAccessI {

    public Iterator<Point> Rarefaction(DistributionI<Long> dist) throws MGXException;

    public PCAResultI PCA(Collection<Pair<VisualizationGroupI, DistributionI<Double>>> groups, PrincipalComponent pc1, PrincipalComponent pc2) throws MGXException;

    public List<Point> PCoA(Collection<Pair<VisualizationGroupI, DistributionI<Double>>> groups) throws MGXException;

    public String Clustering(Collection<Pair<VisualizationGroupI, DistributionI<Double>>> dists, String distanceMethod, String agglomeration) throws MGXException;

}
