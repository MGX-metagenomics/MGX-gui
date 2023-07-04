//package de.cebitec.mgx.gui.nodefactory;
//
//import de.cebitec.mgx.api.exception.MGXException;
//import de.cebitec.mgx.api.model.assembly.BinI;
//import de.cebitec.mgx.api.model.assembly.ContigI;
//import de.cebitec.mgx.gui.nodes.ContigNode;
//import java.util.Iterator;
//import java.util.List;
//import org.openide.util.Exceptions;
//
///**
// *
// * @author sj
// */
//public class ContigNodeFactory extends MGX2NodeFactoryBase<BinI, ContigI> {
//
//    public ContigNodeFactory(BinI a) {
//        super(a);
//    }
//
//    @Override
//    protected boolean addKeys(List<ContigI> toPopulate) {
//        try {
//            Iterator<ContigI> iter = getMaster().Contig().ByBin(getContent());
//            while (iter.hasNext()) {
//                toPopulate.add(iter.next());
//            }
//            return true;
//        } catch (MGXException ex) {
//            Exceptions.printStackTrace(ex);
//        }
//        return true;
//    }
//
//    @Override
//    protected ContigNode createNodeFor(ContigI key) {
//        return new ContigNode(key);
//    }
//}
