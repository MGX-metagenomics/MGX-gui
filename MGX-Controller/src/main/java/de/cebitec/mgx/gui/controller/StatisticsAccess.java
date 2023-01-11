package de.cebitec.mgx.gui.controller;

import de.cebitec.mgx.api.MGXMasterI;
import de.cebitec.mgx.api.access.StatisticsAccessI;
import de.cebitec.mgx.api.exception.MGXException;
import de.cebitec.mgx.api.exception.MGXLoggedoutException;
import de.cebitec.mgx.api.groups.GroupI;
import de.cebitec.mgx.api.misc.DistributionI;
import de.cebitec.mgx.api.misc.PrincipalComponent;
import de.cebitec.mgx.api.misc.PCAResultI;
import de.cebitec.mgx.api.misc.Pair;
import de.cebitec.mgx.api.misc.Point;
import de.cebitec.mgx.api.model.AttributeI;
import de.cebitec.mgx.client.MGXDTOMaster;
import de.cebitec.mgx.client.exception.MGXClientLoggedOutException;
import de.cebitec.mgx.client.exception.MGXDTOException;
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
import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.UUID;

/**
 *
 * @author sj
 */
public class StatisticsAccess extends MasterHolder implements StatisticsAccessI {


    public StatisticsAccess(MGXMasterI master, MGXDTOMaster dtomaster) throws MGXException {
        super(master, dtomaster);
    }

    @Override
    public String Clustering(Collection<Pair<GroupI, DistributionI<Double>>> dists, String distanceMethod, String agglomeration) throws MGXException {
        MGXMatrixDTO matrix = buildMatrix(dists, false);

        try {
            String nwk = getDTOmaster().Statistics().Clustering(matrix, distanceMethod, agglomeration);

            for (Pair<GroupI, DistributionI<Double>> pair : dists) {
                GroupI vGrp = pair.getFirst();
                nwk = nwk.replaceFirst(vGrp.getUUID().toString(), vGrp.getName());
            }

            //NodeI newickRoot = NewickParser.parse(nwk);
            return nwk;
        } catch (MGXClientLoggedOutException mcle) {
            throw new MGXLoggedoutException(mcle);
        } catch (MGXDTOException ex) {
            throw new MGXException(ex.getMessage());
        }
    }
    
    @Override
    public String newickToSVG(String newick) throws MGXException {
        try {
            String svg = getDTOmaster().Statistics().newickToSVG(newick);
            return svg;
        } catch (MGXClientLoggedOutException mcle) {
            throw new MGXLoggedoutException(mcle);
        } catch (MGXDTOException ex) {
            throw new MGXException(ex.getMessage());
        }
    }

    @Override
    public PCAResultI PCA(Collection<Pair<GroupI, DistributionI<Double>>> groups, PrincipalComponent pc1, PrincipalComponent pc2) throws MGXException {

        MGXMatrixDTO matrix = buildMatrix(groups, true);

        try {
            PCAResultDTO ret = getDTOmaster().Statistics().PCA(matrix, pc1.getValue(), pc2.getValue());
            PCAResultI pca = PCAResultDTOFactory.getInstance().toModel(getMaster(), ret);

            // replace group uuids by group names
            for (Point p : pca.getDatapoints()) {
                for (Pair<GroupI, DistributionI<Double>> pair : groups) {
                    if (pair.getFirst().getUUID().toString().equals(p.getName())) {
                        p.setName(pair.getFirst().getDisplayName());
                        break;
                    }
                }
            }
            return pca;
        } catch (MGXClientLoggedOutException mcle) {
            throw new MGXLoggedoutException(mcle);
        } catch (MGXDTOException ex) {
            throw new MGXException(ex.getMessage());
        }
    }

    @Override
    public Collection<Point> NMDS(Collection<Pair<GroupI, DistributionI<Double>>> groups) throws MGXException {

        MGXMatrixDTO matrix = buildMatrix(groups, true);

        List<Point> pcoa = new LinkedList<>();
        try {
            PointDTOList ret = getDTOmaster().Statistics().NMDS(matrix);
            for (PointDTO pdto : ret.getPointList()) {
                Point p = PointDTOFactory.getInstance().toModel(getMaster(), pdto);

                // replace group uuids by group names
                for (Pair<GroupI, DistributionI<Double>> pair : groups) {
                    if (pair.getFirst().getUUID().toString().equals(p.getName())) {
                        p.setName(pair.getFirst().getDisplayName());
                        break;
                    }
                }
                pcoa.add(p);
            }
            return pcoa;
        } catch (MGXClientLoggedOutException mcle) {
            throw new MGXLoggedoutException(mcle);
        } catch (MGXDTOException ex) {
            throw new MGXException(ex.getMessage());
        }
    }

    @Override
    public double[] toCLR(double[] counts) throws MGXException {
        try {
            return getDTOmaster().Statistics().toCLR(counts);
        } catch (MGXClientLoggedOutException mcle) {
            throw new MGXLoggedoutException(mcle);
        } catch (MGXDTOException ex) {
            throw new MGXException(ex.getMessage());
        }
    }

    @Override
    public double aitchisonDistance(double[] d1, double[] d2) throws MGXException {
        try {
            return getDTOmaster().Statistics().aitchisonDistance(d1, d2);
        } catch (MGXClientLoggedOutException mcle) {
            throw new MGXLoggedoutException(mcle);
        } catch (MGXDTOException ex) {
            throw new MGXException(ex.getMessage());
        }
    }

    private static <T extends Number> MGXMatrixDTO buildMatrix(Collection<Pair<GroupI, DistributionI<T>>> groups, boolean includeColNames) {
        MGXMatrixDTO.Builder b = MGXMatrixDTO.newBuilder();

        // collect all attributes first
        Set<AttributeI> attrs = new HashSet<>();
        for (Pair<GroupI, DistributionI<T>> dataset : groups) {
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

        for (Pair<GroupI, DistributionI<T>> dataset : groups) {
            UUID grpUUID = dataset.getFirst().getUUID();
            ProfileDTO prof = ProfileDTO.newBuilder()
                    .setName(grpUUID.toString())
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

//    private static String generateName() {
//        return generateName(8);
//    }
//
//    private static String generateName(int len) {
//        char[] chars = "abcdefghijklmnopqrstuvwxyz".toCharArray();
//        StringBuilder sb = new StringBuilder(len);
//        Random random = new Random();
//        for (int i = 0; i < len; i++) {
//            char c = chars[random.nextInt(chars.length)];
//            sb.append(c);
//        }
//        return sb.toString();
//    }
}
