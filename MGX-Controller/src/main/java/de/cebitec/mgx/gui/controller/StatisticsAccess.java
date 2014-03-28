package de.cebitec.mgx.gui.controller;

import de.cebitec.mgx.client.exception.MGXClientException;
import de.cebitec.mgx.client.exception.MGXServerException;
import de.cebitec.mgx.dto.dto.MGXLongList;
import de.cebitec.mgx.dto.dto.MGXMatrixDTO;
import de.cebitec.mgx.dto.dto.PointDTO;
import de.cebitec.mgx.dto.dto.ProfileDTO;
import de.cebitec.mgx.gui.datamodel.Attribute;
import de.cebitec.mgx.gui.datamodel.misc.Distribution;
import de.cebitec.mgx.gui.datamodel.misc.Pair;
import de.cebitec.mgx.gui.datamodel.misc.Point;
import de.cebitec.mgx.gui.datamodel.misc.Task;
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

    public Iterator<double[]> Rarefaction(Distribution dist) {
        try {
            Iterator<PointDTO> fetchall = getDTOmaster().Statistics().Rarefaction(dist.values());
            return new BaseIterator<PointDTO, double[]>(fetchall) {
                @Override
                public double[] next() {
                    return PointDTOFactory.getInstance().toModel(iter.next());
                }
            };
        } catch (MGXServerException | MGXClientException ex) {
            Exceptions.printStackTrace(ex);
        }

        return null;
    }

    public NodeI Clustering(Collection<Pair<VisualizationGroup, Distribution>> groups, String distMethod, String aggloMethod) {
        MGXMatrixDTO.Builder b = MGXMatrixDTO.newBuilder();

        // collect all attributes first
        Set<Attribute> attrs = new HashSet<>();
        for (Pair<VisualizationGroup, Distribution> dataset : groups) {
            attrs.addAll(dataset.getSecond().keySet());
        }
        Attribute[] ordered = attrs.toArray(new Attribute[]{});
        attrs.clear();

        // obfuscate group names
        Map<String, String> tmpNames = new HashMap<>();

        for (Pair<VisualizationGroup, Distribution> dataset : groups) {
            String obfusName = generateName();
            tmpNames.put(obfusName, dataset.getFirst().getName());

            ProfileDTO prof = ProfileDTO.newBuilder()
                    .setName(obfusName)
                    .setValues(buildVector(ordered, dataset.getSecond()))
                    .build();
            b.addRow(prof);
        }
        String nwk = null;
        try {
            nwk = getDTOmaster().Statistics().Clustering(b.build(), distMethod, aggloMethod);

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

    private static MGXLongList buildVector(Attribute[] attrs, Distribution dist) {
        MGXLongList.Builder b = MGXLongList.newBuilder();
        for (Attribute a : attrs) {
            Number n = dist.get(a);
            b.addLong(n != null ? n.longValue() : 0);
        }
        return b.build();
    }

    private static String generateName() {
        char[] chars = "abcdefghijklmnopqrstuvwxyz".toCharArray();
        StringBuilder sb = new StringBuilder();
        Random random = new Random();
        for (int i = 0; i < 8; i++) {
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

}
