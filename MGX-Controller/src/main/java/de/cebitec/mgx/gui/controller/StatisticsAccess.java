package de.cebitec.mgx.gui.controller;

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
import de.cebitec.mgx.gui.datamodel.Attribute;
import de.cebitec.mgx.gui.datamodel.misc.Distribution;
import de.cebitec.mgx.gui.datamodel.misc.PCAResult;
import de.cebitec.mgx.gui.datamodel.misc.Pair;
import de.cebitec.mgx.gui.datamodel.misc.Point;
import de.cebitec.mgx.gui.datamodel.misc.Task;
import de.cebitec.mgx.gui.dtoconversion.PCAResultDTOFactory;
import de.cebitec.mgx.gui.dtoconversion.PointDTOFactory;
import de.cebitec.mgx.gui.groups.VisualizationGroup;
import de.cebitec.mgx.gui.util.BaseIterator;
import de.cebitec.mgx.newick.NewickParser;
import de.cebitec.mgx.newick.NodeI;
import de.cebitec.mgx.newick.ParserException;
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
import org.openide.util.Exceptions;

/**
 *
 * @author sj
 */
public class StatisticsAccess extends AccessBase<Point> {

    public Iterator<Point> Rarefaction(Distribution dist) {
        try {
            Iterator<PointDTO> fetchall = getDTOmaster().Statistics().Rarefaction(dist.values());
            return new BaseIterator<PointDTO, Point>(fetchall) {
                @Override
                public Point next() {
                    return PointDTOFactory.getInstance().toModel(iter.next());
                }
            };
        } catch (MGXServerException | MGXClientException ex) {
            Exceptions.printStackTrace(ex);
        }

        return null;
    }

    public NodeI Clustering(Collection<Pair<VisualizationGroup, Distribution>> groups, String distMethod, String aggloMethod) {

        // map to hold obfuscated group name mapping
        Map<String, String> tmpNames = new HashMap<>();
        MGXMatrixDTO matrix = buildMatrix(groups, tmpNames, false);

        String nwk = null;
        try {
            nwk = getDTOmaster().Statistics().Clustering(matrix, distMethod, aggloMethod);

            // de-obfuscate group names
            for (Entry<String, String> e : tmpNames.entrySet()) {
                nwk = nwk.replace(e.getKey(), e.getValue());
            }
            return NewickParser.parse(nwk);
        } catch (MGXServerException | MGXClientException | ParserException ex) {
            if (ex instanceof ParserException) {
                System.err.println("Parser error for Newick string " + nwk);
            }
            Exceptions.printStackTrace(ex);
        }
        return null;
    }

    public PCAResult PCA(Collection<Pair<VisualizationGroup, Distribution>> groups, int pc1, int pc2) {

        // map to hold obfuscated group name mapping
        Map<String, String> tmpNames = new HashMap<>();
        MGXMatrixDTO matrix = buildMatrix(groups, tmpNames, true);

        try {
            PCAResultDTO ret = getDTOmaster().Statistics().PCA(matrix, pc1, pc2);
            PCAResult pca = PCAResultDTOFactory.getInstance().toModel(ret);
            // de-obfuscate group names
            for (Point p : pca.getDatapoints()) {
                p.setName(tmpNames.get(p.getName()));
            }
            return pca;
        } catch (MGXServerException | MGXClientException ex) {
            Exceptions.printStackTrace(ex);
        }
        return null;
    }

    public List<Point> PCoA(Collection<Pair<VisualizationGroup, Distribution>> groups) {

        // map to hold obfuscated group name mapping
        Map<String, String> tmpNames = new HashMap<>();
        MGXMatrixDTO matrix = buildMatrix(groups, tmpNames, true);

        List<Point> pcoa = new LinkedList<>();
        try {
            PointDTOList ret = getDTOmaster().Statistics().PCoA(matrix);
            for (PointDTO pdto : ret.getPointList()) {
                Point p = PointDTOFactory.getInstance().toModel(pdto);
                p.setName(tmpNames.get(p.getName())); // de-obfuscate group name
                pcoa.add(p);
            }
            return pcoa;
        } catch (MGXServerException | MGXClientException ex) {
            Exceptions.printStackTrace(ex);
        }
        return null;
    }

    private static MGXMatrixDTO buildMatrix(Collection<Pair<VisualizationGroup, Distribution>> groups, Map<String, String> tmpNames, boolean includeColNames) {
        MGXMatrixDTO.Builder b = MGXMatrixDTO.newBuilder();

        // collect all attributes first
        Set<Attribute> attrs = new HashSet<>();
        for (Pair<VisualizationGroup, Distribution> dataset : groups) {
            attrs.addAll(dataset.getSecond().keySet());
        }
        Attribute[] ordered = attrs.toArray(new Attribute[]{});
        attrs.clear();

        if (includeColNames) {
            MGXStringList.Builder sb = MGXStringList.newBuilder();
            for (Attribute attr : ordered) {
                sb.addString(MGXString.newBuilder().setValue(attr.getValue()).build());
            }
            b.setColNames(sb.build());
        }

        for (Pair<VisualizationGroup, Distribution> dataset : groups) {
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

    private static MGXDoubleList buildVector(Attribute[] attrs, Distribution dist) {
        MGXDoubleList.Builder b = MGXDoubleList.newBuilder();
        for (Attribute a : attrs) {
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
        StringBuilder sb = new StringBuilder();
        Random random = new Random();
        for (int i = 0; i < len; i++) {
            char c = chars[random.nextInt(chars.length)];
            sb.append(c);
        }
        return sb.toString();
    }

    @Override
    public long create(Point obj) {
        throw new UnsupportedOperationException("Not supported.");
    }

    @Override
    public Point fetch(long id) {
        throw new UnsupportedOperationException("Not supported.");
    }

    @Override
    public Iterator<Point> fetchall() {
        throw new UnsupportedOperationException("Not supported.");
    }

    @Override
    public void update(Point obj) {
        throw new UnsupportedOperationException("Not supported.");
    }

    @Override
    public Task delete(Point obj) {
        throw new UnsupportedOperationException("Not supported.");
    }

    public PCAResult PCoA(List<Pair<VisualizationGroup, Distribution>> data) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

}
