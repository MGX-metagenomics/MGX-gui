package de.cebitec.mgx.gui.controller;

import de.cebitec.mgx.api.MGXMasterI;
import de.cebitec.mgx.api.access.StatisticsAccessI;
import de.cebitec.mgx.api.exception.MGXException;
import de.cebitec.mgx.api.groups.VisualizationGroupI;
import de.cebitec.mgx.api.misc.DistributionI;
import de.cebitec.mgx.api.misc.PrincipalComponent;
import de.cebitec.mgx.api.misc.PCAResultI;
import de.cebitec.mgx.api.misc.Pair;
import de.cebitec.mgx.api.misc.Point;
import de.cebitec.mgx.api.model.AttributeI;
import de.cebitec.mgx.client.MGXDTOMaster;
import de.cebitec.mgx.client.exception.MGXClientException;
import de.cebitec.mgx.client.exception.MGXServerException;
import de.cebitec.mgx.dto.dto.MGXDoubleList;
import de.cebitec.mgx.dto.dto.MGXMatrixDTO;
import de.cebitec.mgx.dto.dto.MGXString;
import de.cebitec.mgx.dto.dto.MGXStringList;
import de.cebitec.mgx.dto.dto.PCAResultDTO;
import de.cebitec.mgx.dto.dto.PointDTO;
import de.cebitec.mgx.dto.dto.PointDTOList;
import de.cebitec.mgx.dto.dto.ProfileDTO;
import de.cebitec.mgx.gui.dtoconversion.PCAResultDTOFactory;
import de.cebitec.mgx.gui.dtoconversion.PointDTOFactory;
import de.cebitec.mgx.gui.util.BaseIterator;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Random;
import java.util.Set;

/**
 *
 * @author sj
 */
public class StatisticsAccess implements StatisticsAccessI {

    private final MGXMasterI master;
    private final MGXDTOMaster dtomaster;

    public StatisticsAccess(MGXMasterI master, MGXDTOMaster dtomaster) {
        this.master = master;
        this.dtomaster = dtomaster;
    }

    @Override
    public Iterator<Point> Rarefaction(DistributionI<Long> dist) throws MGXException {
        try {
            Iterator<PointDTO> fetchall = dtomaster.Statistics().Rarefaction(dist.values());
            return new BaseIterator<PointDTO, Point>(fetchall) {
                @Override
                public Point next() {
                    return PointDTOFactory.getInstance().toModel(master, iter.next());
                }
            };
        } catch (MGXServerException | MGXClientException ex) {
            throw new MGXException(ex);
        }
    }
    

    @Override
    public String Clustering(Collection<Pair<VisualizationGroupI, DistributionI<Double>>> dists, String distanceMethod, String agglomeration) throws MGXException {
        // map to hold obfuscated group name mapping
        Map<String, String> tmpNames = new HashMap<>();
        MGXMatrixDTO matrix = buildMatrix(dists, tmpNames, false);

        String nwk = null;
        try {
            nwk = dtomaster.Statistics().Clustering(matrix, distanceMethod, agglomeration);

            // de-obfuscate group names
            for (Entry<String, String> e : tmpNames.entrySet()) {
                nwk = nwk.replace(e.getKey(), e.getValue());
            }
            return nwk; // NewickParser.parse(nwk);
        } catch (MGXServerException | MGXClientException ex) {
            throw new MGXException(ex);
        }
    }

    @Override
    public PCAResultI PCA(Collection<Pair<VisualizationGroupI, DistributionI<Double>>> groups, PrincipalComponent pc1, PrincipalComponent pc2) throws MGXException {

        // map to hold obfuscated group name mapping
        Map<String, String> tmpNames = new HashMap<>();
        MGXMatrixDTO matrix = buildMatrix(groups, tmpNames, true);

        try {
            PCAResultDTO ret = dtomaster.Statistics().PCA(matrix, pc1.getValue(), pc2.getValue());
            PCAResultI pca = PCAResultDTOFactory.getInstance().toModel(master, ret);
            // de-obfuscate group names
            for (Point p : pca.getDatapoints()) {
                p.setName(tmpNames.get(p.getName()));
            }
            return pca;
        } catch (MGXServerException | MGXClientException ex) {
            throw new MGXException(ex);
        }
    }

    @Override
    public List<Point> PCoA(Collection<Pair<VisualizationGroupI, DistributionI<Double>>> groups) throws MGXException {

        // map to hold obfuscated group name mapping
        Map<String, String> tmpNames = new HashMap<>();
        MGXMatrixDTO matrix = buildMatrix(groups, tmpNames, true);

        List<Point> pcoa = new LinkedList<>();
        try {
            PointDTOList ret = dtomaster.Statistics().PCoA(matrix);
            for (PointDTO pdto : ret.getPointList()) {
                Point p = PointDTOFactory.getInstance().toModel(master, pdto);
                p.setName(tmpNames.get(p.getName())); // de-obfuscate group name
                pcoa.add(p);
            }
            return pcoa;
        } catch (MGXServerException | MGXClientException ex) {
            throw new MGXException(ex);
        }
    }

    private static <T extends Number> MGXMatrixDTO buildMatrix(Collection<Pair<VisualizationGroupI, DistributionI<T>>> groups, Map<String, String> tmpNames, boolean includeColNames) {
        MGXMatrixDTO.Builder b = MGXMatrixDTO.newBuilder();

        // collect all attributes first
        Set<AttributeI> attrs = new HashSet<>();
        for (Pair<VisualizationGroupI, DistributionI<T>> dataset : groups) {
            attrs.addAll(dataset.getSecond().keySet());
        }
        AttributeI[] ordered = attrs.toArray(new AttributeI[]{});
        attrs.clear();

        if (includeColNames) {
            MGXStringList.Builder sb = MGXStringList.newBuilder();
            for (AttributeI attr : ordered) {
                sb.addString(MGXString.newBuilder().setValue(attr.getValue()).build());
            }
            b.setColNames(sb.build());
        }

        for (Pair<VisualizationGroupI, DistributionI<T>> dataset : groups) {
            String obfusName = generateName();
            tmpNames.put(obfusName, dataset.getFirst().getName());

            ProfileDTO prof = ProfileDTO.newBuilder()
                    .setName(obfusName)
                    .setValues(buildVector(ordered, dataset.getSecond()))
                    .build();
            b.addRow(prof);
        }

        return b.build();
    }

    private static <T extends Number> MGXDoubleList buildVector(AttributeI[] attrs, DistributionI<T> dist) {
        MGXDoubleList.Builder b = MGXDoubleList.newBuilder();
        for (AttributeI a : attrs) {
            Number n = dist.get(a);
            b.addValue(n != null ? n.doubleValue() : 0);
        }
        return b.build();
    }

    private static String generateName() {
        return generateName(8);
    }

    private static String generateName(int len) {
        char[] chars = "abcdefghijklmnopqrstuvwxyz".toCharArray();
        StringBuilder sb = new StringBuilder(len);
        Random random = new Random();
        for (int i = 0; i < len; i++) {
            char c = chars[random.nextInt(chars.length)];
            sb.append(c);
        }
        return sb.toString();
    }


}
