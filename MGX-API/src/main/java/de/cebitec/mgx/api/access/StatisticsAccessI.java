/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.cebitec.mgx.api.access;

import de.cebitec.mgx.api.exception.MGXException;
import de.cebitec.mgx.api.groups.GroupI;
import de.cebitec.mgx.api.misc.DistributionI;
import de.cebitec.mgx.api.misc.PrincipalComponent;
import de.cebitec.mgx.api.misc.PCAResultI;
import de.cebitec.mgx.api.misc.Pair;
import de.cebitec.mgx.api.misc.Point;
import java.util.Collection;

/**
 *
 * @author sj
 */
public interface StatisticsAccessI {

    public PCAResultI PCA(Collection<Pair<GroupI, DistributionI<Double>>> groups, PrincipalComponent pc1, PrincipalComponent pc2) throws MGXException;

    public Collection<Point> NMDS(Collection<Pair<GroupI, DistributionI<Double>>> groups) throws MGXException;

    public String Clustering(Collection<Pair<GroupI, DistributionI<Double>>> dists, String distanceMethod, String agglomeration) throws MGXException;

    // transform to centered log-ratios
    public double[] toCLR(double[] counts) throws MGXException;

    public double aitchisonDistance(double[] d1, double[] d2) throws MGXException;

    public String newickToSVG(String newick) throws MGXException;

}
