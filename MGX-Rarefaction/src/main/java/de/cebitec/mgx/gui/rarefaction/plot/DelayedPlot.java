package de.cebitec.mgx.gui.rarefaction.plot;

import java.awt.AWTKeyStroke;
import java.awt.Color;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Insets;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.beans.VetoableChangeListener;
import java.util.EventListener;
import java.util.Set;
import javax.accessibility.AccessibleContext;
import javax.swing.InputVerifier;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JRootPane;
import javax.swing.JToolTip;
import javax.swing.KeyStroke;
import javax.swing.TransferHandler;
import javax.swing.border.Border;
import javax.swing.event.AncestorListener;

/**
 *
 * @author sj
 */
public class DelayedPlot extends JPanel {

    private JComponent delegate;

    public DelayedPlot(JComponent delegate) {
        this.delegate = delegate;
    }
    
    public void setTarget(JComponent newTarget) {
        delegate = newTarget;
        repaint(); // trigger update
    }
    
    public JComponent getTarget() {
        return delegate;
    }

    @Override
    public void updateUI() {
        delegate.updateUI();
    }

    @Override
    public String getUIClassID() {
        return delegate.getUIClassID();
    }

    @Override
    public AccessibleContext getAccessibleContext() {
        return delegate.getAccessibleContext();
    }

    @Override
    public void setInheritsPopupMenu(boolean value) {
        delegate.setInheritsPopupMenu(value);
    }

    @Override
    public boolean getInheritsPopupMenu() {
        return delegate.getInheritsPopupMenu();
    }

    @Override
    public void setComponentPopupMenu(JPopupMenu popup) {
        delegate.setComponentPopupMenu(popup);
    }

    @Override
    public JPopupMenu getComponentPopupMenu() {
        return delegate.getComponentPopupMenu();
    }

    @Override
    public void update(Graphics g) {
        delegate.update(g);
    }

    @Override
    public void paint(Graphics g) {
        delegate.paint(g);
    }

    @Override
    public void printAll(Graphics g) {
        delegate.printAll(g);
    }

    @Override
    public void print(Graphics g) {
        delegate.print(g);
    }

    @Override
    public boolean isPaintingTile() {
        return delegate.isPaintingTile();
    }

    @Override
    public void setRequestFocusEnabled(boolean requestFocusEnabled) {
        delegate.setRequestFocusEnabled(requestFocusEnabled);
    }

    @Override
    public boolean isRequestFocusEnabled() {
        return delegate.isRequestFocusEnabled();
    }

    @Override
    public void requestFocus() {
        delegate.requestFocus();
    }

    @Override
    public boolean requestFocus(boolean temporary) {
        return delegate.requestFocus(temporary);
    }

    @Override
    public boolean requestFocusInWindow() {
        return delegate.requestFocusInWindow();
    }

    @Override
    public void grabFocus() {
        delegate.grabFocus();
    }

    @Override
    public void setVerifyInputWhenFocusTarget(boolean verifyInputWhenFocusTarget) {
        delegate.setVerifyInputWhenFocusTarget(verifyInputWhenFocusTarget);
    }

    @Override
    public boolean getVerifyInputWhenFocusTarget() {
        return delegate.getVerifyInputWhenFocusTarget();
    }

    @Override
    public FontMetrics getFontMetrics(Font font) {
        return delegate.getFontMetrics(font);
    }

    @Override
    public void setPreferredSize(Dimension preferredSize) {
        delegate.setPreferredSize(preferredSize);
    }

    @Override
    public Dimension getPreferredSize() {
        return delegate.getPreferredSize();
    }

    @Override
    public void setMaximumSize(Dimension maximumSize) {
        delegate.setMaximumSize(maximumSize);
    }

    @Override
    public Dimension getMaximumSize() {
        return delegate.getMaximumSize();
    }

    @Override
    public void setMinimumSize(Dimension minimumSize) {
        delegate.setMinimumSize(minimumSize);
    }

    @Override
    public Dimension getMinimumSize() {
        return delegate.getMinimumSize();
    }

    @Override
    public boolean contains(int x, int y) {
        return delegate.contains(x, y);
    }

    @Override
    public void setBorder(Border border) {
        delegate.setBorder(border);
    }

    @Override
    public Border getBorder() {
        return delegate.getBorder();
    }

    @Override
    public Insets getInsets() {
        return delegate.getInsets();
    }

    @Override
    public Insets getInsets(Insets insets) {
        return delegate.getInsets(insets);
    }

    @Override
    public float getAlignmentY() {
        return delegate.getAlignmentY();
    }

    @Override
    public void setAlignmentY(float alignmentY) {
        delegate.setAlignmentY(alignmentY);
    }

    @Override
    public float getAlignmentX() {
        return delegate.getAlignmentX();
    }

    @Override
    public void setAlignmentX(float alignmentX) {
        delegate.setAlignmentX(alignmentX);
    }

    @Override
    public void setInputVerifier(InputVerifier inputVerifier) {
        delegate.setInputVerifier(inputVerifier);
    }

    @Override
    public InputVerifier getInputVerifier() {
        return delegate.getInputVerifier();
    }

    @Override
    public Graphics getGraphics() {
        return delegate.getGraphics();
    }

    @Override
    public void setDebugGraphicsOptions(int debugOptions) {
        delegate.setDebugGraphicsOptions(debugOptions);
    }

    @Override
    public int getDebugGraphicsOptions() {
        return delegate.getDebugGraphicsOptions();
    }

    @Override
    public void registerKeyboardAction(ActionListener anAction, String aCommand, KeyStroke aKeyStroke, int aCondition) {
        delegate.registerKeyboardAction(anAction, aCommand, aKeyStroke, aCondition);
    }

    @Override
    public void registerKeyboardAction(ActionListener anAction, KeyStroke aKeyStroke, int aCondition) {
        delegate.registerKeyboardAction(anAction, aKeyStroke, aCondition);
    }

    @Override
    public void unregisterKeyboardAction(KeyStroke aKeyStroke) {
        delegate.unregisterKeyboardAction(aKeyStroke);
    }

    @Override
    public KeyStroke[] getRegisteredKeyStrokes() {
        return delegate.getRegisteredKeyStrokes();
    }

    @Override
    public int getConditionForKeyStroke(KeyStroke aKeyStroke) {
        return delegate.getConditionForKeyStroke(aKeyStroke);
    }

    @Override
    public ActionListener getActionForKeyStroke(KeyStroke aKeyStroke) {
        return delegate.getActionForKeyStroke(aKeyStroke);
    }

    @Override
    public void resetKeyboardActions() {
        delegate.resetKeyboardActions();
    }

    @Override
    public int getBaseline(int width, int height) {
        return delegate.getBaseline(width, height);
    }

    @Override
    public BaselineResizeBehavior getBaselineResizeBehavior() {
        return delegate.getBaselineResizeBehavior();
    }

    @Override
    public void setVisible(boolean aFlag) {
        delegate.setVisible(aFlag);
    }

    @Override
    public void setEnabled(boolean enabled) {
        delegate.setEnabled(enabled);
    }

    @Override
    public void setForeground(Color fg) {
        delegate.setForeground(fg);
    }

    @Override
    public void setBackground(Color bg) {
        delegate.setBackground(bg);
    }

    @Override
    public void setFont(Font font) {
        delegate.setFont(font);
    }

    @Override
    public void setToolTipText(String text) {
        delegate.setToolTipText(text);
    }

    @Override
    public String getToolTipText() {
        return delegate.getToolTipText();
    }

    @Override
    public String getToolTipText(MouseEvent event) {
        return delegate.getToolTipText(event);
    }

    @Override
    public Point getToolTipLocation(MouseEvent event) {
        return delegate.getToolTipLocation(event);
    }

    @Override
    public Point getPopupLocation(MouseEvent event) {
        return delegate.getPopupLocation(event);
    }

    @Override
    public JToolTip createToolTip() {
        return delegate.createToolTip();
    }

    @Override
    public void scrollRectToVisible(Rectangle aRect) {
        delegate.scrollRectToVisible(aRect);
    }

    @Override
    public void setAutoscrolls(boolean autoscrolls) {
        delegate.setAutoscrolls(autoscrolls);
    }

    @Override
    public boolean getAutoscrolls() {
        return delegate.getAutoscrolls();
    }

    @Override
    public void setTransferHandler(TransferHandler newHandler) {
        delegate.setTransferHandler(newHandler);
    }

    @Override
    public TransferHandler getTransferHandler() {
        return delegate.getTransferHandler();
    }

    @Override
    public void setFocusTraversalKeys(int id, Set<? extends AWTKeyStroke> keystrokes) {
        delegate.setFocusTraversalKeys(id, keystrokes);
    }

    @Override
    public Rectangle getBounds(Rectangle rv) {
        return delegate.getBounds(rv);
    }

    @Override
    public Dimension getSize(Dimension rv) {
        return delegate.getSize(rv);
    }

    @Override
    public Point getLocation(Point rv) {
        return delegate.getLocation(rv);
    }

    @Override
    public int getX() {
        return delegate.getX();
    }

    @Override
    public int getY() {
        return delegate.getY();
    }

    @Override
    public int getWidth() {
        return delegate.getWidth();
    }

    @Override
    public int getHeight() {
        return delegate.getHeight();
    }

    @Override
    public boolean isOpaque() {
        return delegate.isOpaque();
    }

    @Override
    public void setOpaque(boolean isOpaque) {
        delegate.setOpaque(isOpaque);
    }

    @Override
    public void computeVisibleRect(Rectangle visibleRect) {
        delegate.computeVisibleRect(visibleRect);
    }

    @Override
    public Rectangle getVisibleRect() {
        return delegate.getVisibleRect();
    }

    @Override
    public void firePropertyChange(String propertyName, boolean oldValue, boolean newValue) {
        delegate.firePropertyChange(propertyName, oldValue, newValue);
    }

    @Override
    public void firePropertyChange(String propertyName, int oldValue, int newValue) {
        delegate.firePropertyChange(propertyName, oldValue, newValue);
    }

    @Override
    public void firePropertyChange(String propertyName, char oldValue, char newValue) {
        delegate.firePropertyChange(propertyName, oldValue, newValue);
    }

    @Override
    public synchronized void addVetoableChangeListener(VetoableChangeListener listener) {
        delegate.addVetoableChangeListener(listener);
    }

    @Override
    public synchronized void removeVetoableChangeListener(VetoableChangeListener listener) {
        delegate.removeVetoableChangeListener(listener);
    }

    @Override
    public synchronized VetoableChangeListener[] getVetoableChangeListeners() {
        return delegate.getVetoableChangeListeners();
    }

    @Override
    public Container getTopLevelAncestor() {
        return delegate.getTopLevelAncestor();
    }

    @Override
    public void addAncestorListener(AncestorListener listener) {
        delegate.addAncestorListener(listener);
    }

    @Override
    public void removeAncestorListener(AncestorListener listener) {
        delegate.removeAncestorListener(listener);
    }

    @Override
    public AncestorListener[] getAncestorListeners() {
        return delegate.getAncestorListeners();
    }

    @Override
    public <T extends EventListener> T[] getListeners(Class<T> listenerType) {
        return delegate.getListeners(listenerType);
    }

    @Override
    public void addNotify() {
        delegate.addNotify();
    }

    @Override
    public void removeNotify() {
        delegate.removeNotify();
    }

    @Override
    public void repaint(long tm, int x, int y, int width, int height) {
        delegate.repaint(tm, x, y, width, height);
    }

    @Override
    public void repaint(Rectangle r) {
        delegate.repaint(r);
    }

    @Override
    public void revalidate() {
        delegate.revalidate();
    }

    @Override
    public boolean isValidateRoot() {
        return delegate.isValidateRoot();
    }

    @Override
    public boolean isOptimizedDrawingEnabled() {
        return delegate.isOptimizedDrawingEnabled();
    }

    @Override
    public void paintImmediately(int x, int y, int w, int h) {
        delegate.paintImmediately(x, y, w, h);
    }

    @Override
    public void paintImmediately(Rectangle r) {
        delegate.paintImmediately(r);
    }

    @Override
    public void setDoubleBuffered(boolean aFlag) {
        delegate.setDoubleBuffered(aFlag);
    }

    @Override
    public boolean isDoubleBuffered() {
        return delegate.isDoubleBuffered();
    }

    @Override
    public JRootPane getRootPane() {
        return delegate.getRootPane();
    }

}
