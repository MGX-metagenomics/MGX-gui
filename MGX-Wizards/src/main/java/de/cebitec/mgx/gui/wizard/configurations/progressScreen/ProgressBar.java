
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import javax.swing.*;

/**
 *
 * @author belmann
 */
public class ProgressBar extends JFrame {

   private String message;
   private JPanel panel;
   private JLabel label;
   private int width = 0;
   private int height = 0;
   private final JPanel progressButtonPanel;

   public ProgressBar(String lMessage, int lWidth, int lHeight) {
	width = lWidth;
	height = lHeight;
	this.setName(" Waiting for Server... ");
	this.setTitle(" Waiting for Server... ");
	message = lMessage;
	setSize(lWidth, lHeight);
	setLayout(new GridBagLayout());
	GridBagConstraints con = new GridBagConstraints();
	con.gridy = 0;
	con.weighty = 20;
	con.weightx = 20;
	con.anchor = GridBagConstraints.LINE_START;
	con.fill = GridBagConstraints.BOTH;
	this.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
	setLocationRelativeTo(null);
	panel = new JPanel();
	add(panel, con);

	panel.setLayout(new GridBagLayout());
	GridBagConstraints c = new GridBagConstraints();
	c.fill = GridBagConstraints.BOTH;
	c.anchor = GridBagConstraints.LINE_START;
	c.gridy = 0;
	c.weighty = 20;
	c.weightx = 20;
	label = new JLabel("<html><table><div align=center><td width=" + width + ">" + message
	    + "</td></div></table></html>");
	label.setHorizontalTextPosition(SwingConstants.CENTER);
	label.setHorizontalAlignment(SwingConstants.CENTER);
//	label = new JLabel(message+ "\nWaiting for Server... ");
//	label.setBorder(BorderFactory.createLineBorder(Color.yellow));
//	label.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, Color.black));
//	label.setBorder(BorderFactory..createTitledBorder(""));
//	label.setBorder(BorderFactory.createCompoundBorder());
//	label.setBorder(BorderFactory.);
//	label.setBorder(BorderFactory.createMatteBorder(0, 1, 0, 0, null));
//	panel.add(new JSeparator(SwingConstants.HORIZONTAL));
	
	panel.add(label, c);
//	panel.setBorder(BorderFactory.createLineBorder(Color.BLUE));
//	panel.add(new JSeparator(SwingConstants.HORIZONTAL));
	c.gridy = 1;
	JProgressBar bar = new JProgressBar();
	bar.setIndeterminate(true);
//	bar.setMinimumSize(new Dimension(400,10));
//	bar.setMaximumSize(new Dimension(400,10));
//	bar.setSize(new Dimension(400,10));
	
	progressButtonPanel = new JPanel(new CardLayout());
	JPanel borderProgressPanel = new JPanel();
//	borderProgressPanel.setSize(200,5);
//	borderProgressPanel.setLayout(new BorderLayout());
	borderProgressPanel.add(bar, BorderLayout.CENTER);
	progressButtonPanel.add(borderProgressPanel, "progress");

	JPanel buttonBorderPanel = new JPanel();
	JButton b2 = new JButton("OK");
	b2.addActionListener(new ActionListener() {

	   @Override
	   public void actionPerformed(ActionEvent e) {

		dispose();
	   }
	});
	b2.setVerticalTextPosition(AbstractButton.CENTER);
	b2.setHorizontalTextPosition(AbstractButton.CENTER);
//	buttonBorderPanel.setLayout(new BoxLayout(panel, BoxLayout.PAGE_AXIS));
//	JPanel panel2 = new JPanel(new FlowLayout());
//	panel2.add(b2);
	buttonBorderPanel.add(b2);
//	button.setMaximumSize(new Dimension(20,30));
//	button.setSize(new Dimension(20,30));
//	button.setMinimumSize(new Dimension(20,30));
//	button.setPreferredSize(new Dimension(20,30));
//	buttonBorderPanel.add(button,BorderLayout.CENTER);
	progressButtonPanel.add(buttonBorderPanel, "button");
	CardLayout cl = (CardLayout) (progressButtonPanel.getLayout());
	cl.show(progressButtonPanel, "progress");

	c.fill = GridBagConstraints.NONE;
	c.fill = GridBagConstraints.HORIZONTAL;
	panel.add(progressButtonPanel, c);
	setVisible(true);
   }

   public void setUpdateText(String text) {
	label.setText("<html><table><div align=center><td width=" + width + ">"
	    + text + "" + "</td></div></table></html>");
   }

   public void setButton(String text) {
	label.setText("<html><table><div align=center><td width=" + width + ">"
	    + text + "</td></div></table></html>");
	CardLayout cl = (CardLayout) (progressButtonPanel.getLayout());
	cl.show(progressButtonPanel, "button");
   }

//   public static void main(String[] args) throws InterruptedException {
//	ProgressBar bar = new ProgressBar("Verifying toolparameters.", 300, 140);
//
////	JDialog dialog = new JDialog();
//	Thread.sleep(4000);
//	bar.setUpdateText("Execute Parameters");
//	Thread.sleep(4000);
//	bar.setButton("The installed tool you can find in your Project View.");
//   }
}
