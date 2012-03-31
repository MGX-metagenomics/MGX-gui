//package de.cebitec.mgx.gui.treeview.renderer;
//
//import prefuse.Constants;
//import prefuse.render.EdgeRenderer;
//
///**
// * managing the different renderers
// * @author rbisdorf
// */
//public class RendererManager {
//    public static String circleRenderer = "Circle edges";
//    public static String evoRelRenderer = "Evolution edges (relative)";
//    public static String evoAbsRenderer = "Evolution edges (absolute)";
//    
//    private static EvolutionRendererAbsolute evoAbs;
//    
//    public static String[] getRendererNames(){
//        String[] names = {circleRenderer, evoRelRenderer, evoAbsRenderer};
//        return names;
//    }
//    
//    public static EdgeRenderer getRenderer(String name){
//        if(name.equals(circleRenderer)) {
//            return new CircleRenderer(Constants.EDGE_TYPE_CURVE);
//        } else if(name.equals(evoRelRenderer)){
//            return new EvolutionRendererRelative(Constants.EDGE_TYPE_CURVE);
//        } else {
//            return evoAbs;
//        }
//    }
//    
//    public static String getNameForRenderer(EdgeRenderer renderer){
//        if(renderer instanceof CircleRenderer) return circleRenderer;
//        else if(renderer instanceof EvolutionRendererRelative) return evoRelRenderer;
//        else return evoAbsRenderer;
//    }
//    
//    public static void setEvoAbsRenderer(EvolutionRendererAbsolute evoAbsRenderer){
//        evoAbs = evoAbsRenderer;
//    }
//}
