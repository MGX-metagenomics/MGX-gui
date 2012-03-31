//package de.cebitec.mgx.gui.treeview.renderer;
//
//import java.awt.Color;
//import java.awt.Font;
//import java.awt.Paint;
//import prefuse.util.FontLib;
//
///**
// *
// * @author rbisdorf
// */
//public class TreeConfiguration {
//    protected static final boolean COLLAPSETREEDEF = false;
//    protected static final boolean AUTOZOOMDEF = false;
//    protected static final boolean AUTOFOCUSDEF = true;
//    protected static final String EDGERENDERERDEF = RendererManager.evoAbsRenderer;
//    protected static final int HORIZONTALDISTANCEDEF = 300;
//    protected static final int LEAFDISTANCEDEF = 30;
//    protected static final int BRANCHDISTANCEDEF = 8;
//    protected static final int EDGEWIDTHDEF = 90;
//    protected static final Font LABELFONTDEF = FontLib.getFont("Tahoma", 16);
//    protected static final Color BACKGROUNDCOLORDEF = Color.WHITE;
//    protected static final Color FONTCOLORDEF = Color.BLACK;
//    protected static final Color EDGESCOLORDEF = new Color(200, 200, 200);
//    protected static final Color NODEFOCUSCOLORDEF = new Color(198, 229, 229);
//    protected static final Color NODECOLORDEF = Color.WHITE;
//    protected static final Color NODESEARCHCOLORDEF = new Color(255, 190, 190);
//    protected static final Color CIRCLECOLORDEF = Color.WHITE;
//    
//    private boolean collapseTree = COLLAPSETREEDEF;
//    private boolean autoZoom = AUTOZOOMDEF;
//    private boolean autoFocus = AUTOFOCUSDEF;
//    private String edgeRenderer = EDGERENDERERDEF;
//    
//    private int horizontalDistance = HORIZONTALDISTANCEDEF;
//    private int leafDistance = LEAFDISTANCEDEF;
//    private int branchDistance = BRANCHDISTANCEDEF;
//    private int edgeWidth = EDGEWIDTHDEF;
//    
//    private Font labelFont = LABELFONTDEF;
//    
//    private Color backgroundColor = BACKGROUNDCOLORDEF;
//    private Color fontColor = FONTCOLORDEF;
//    private Color edgesColor = EDGESCOLORDEF;
//    private Color nodeFocusColor = NODEFOCUSCOLORDEF;
//    private Color nodeColor = NODECOLORDEF;
//    private Color nodeSearchColor = NODESEARCHCOLORDEF;
//    private Paint circleColor = CIRCLECOLORDEF;
//
//    public void setAutoFocus(boolean autoFocus) {
//        this.autoFocus = autoFocus;
//    }
//
//    public void setAutoZoom(boolean autoZoom) {
//        this.autoZoom = autoZoom;
//    }
//
//    public void setBackgroundColor(Color backgroundColor) {
//        this.backgroundColor = backgroundColor;
//    }
//
//    public void setBranchDistance(int branchDistance) {
//        this.branchDistance = branchDistance;
//    }
//
//    public void setCircleColor(Paint circleColor) {
//        this.circleColor = circleColor;
//    }
//
//    public void setCollapseTree(boolean collapseTree) {
//        this.collapseTree = collapseTree;
//    }
//
//    public void setEdgeRenderer(String edgeRenderer) {
//        this.edgeRenderer = edgeRenderer;
//    }
//
//    public void setEdgeWidth(int edgeWidth) {
//        this.edgeWidth = edgeWidth;
//    }
//
//    public void setEdgesColor(Color edgesColor) {
//        this.edgesColor = edgesColor;
//    }
//
//    public void setFontColor(Color fontColor) {
//        this.fontColor = fontColor;
//    }
//
//    public void setHorizontalDistance(int horizontalDistance) {
//        this.horizontalDistance = horizontalDistance;
//    }
//
//    public void setLabelFont(Font labelFont) {
//        this.labelFont = labelFont;
//    }
//
//    public void setLeafDistance(int leafDistance) {
//        this.leafDistance = leafDistance;
//    }
//
//    public void setNodeColor(Color nodeColor) {
//        this.nodeColor = nodeColor;
//    }
//
//    public void setNodeFocusColor(Color nodeFocusColor) {
//        this.nodeFocusColor = nodeFocusColor;
//    }
//
//    public void setNodeSearchColor(Color nodeSearchColor) {
//        this.nodeSearchColor = nodeSearchColor;
//    }
//
//    public boolean isAutoFocus() {
//        return autoFocus;
//    }
//
//    public boolean isAutoZoom() {
//        return autoZoom;
//    }
//
//    public Color getBackgroundColor() {
//        return backgroundColor;
//    }
//
//    public int getBranchDistance() {
//        return branchDistance;
//    }
//
//    public Paint getCircleColor() {
//        return circleColor;
//    }
//
//    public boolean isCollapseTree() {
//        return collapseTree;
//    }
//
//    public String getEdgeRenderer() {
//        return edgeRenderer;
//    }
//
//    public int getEdgeWidth() {
//        return edgeWidth;
//    }
//
//    public Color getEdgesColor() {
//        return edgesColor;
//    }
//
//    public Color getFontColor() {
//        return fontColor;
//    }
//
//    public int getHorizontalDistance() {
//        return horizontalDistance;
//    }
//
//    public Font getLabelFont() {
//        return labelFont;
//    }
//
//    public int getLeafDistance() {
//        return leafDistance;
//    }
//
//    public Color getNodeColor() {
//        return nodeColor;
//    }
//
//    public Color getNodeFocusColor() {
//        return nodeFocusColor;
//    }
//
//    public Color getNodeSearchColor() {
//        return nodeSearchColor;
//    }
//}
