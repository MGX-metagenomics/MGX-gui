/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package excluded;

import de.cebitec.vamp.view.dataVisualisation.MousePositionListener;

/**
 *
 * @author belmann
 */
public class ViewController implements MousePositionListener{
	public void addMousePositionListener(MousePositionListener listener) {
//        mousePosListener.add(listener);
	}

	@Override
	public void setCurrentMousePosition(int logPos) {
	}

	@Override
	public void setMouseOverPaintingRequested(boolean requested) {
	}
}
