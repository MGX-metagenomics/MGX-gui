
/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.cebitec.mgx.gui.wizard.configurations.action;

//~--- non-JDK imports --------------------------------------------------------





import de.cebitec.mgx.gui.datamodel.JobParameter;
import de.cebitec.mgx.gui.datamodel.Tool;
import de.cebitec.mgx.gui.wizard.configurations.data.Impl.Node;
import de.cebitec.mgx.gui.wizard.configurations.data.Impl.Store;
import de.cebitec.mgx.gui.wizard.configurations.data.interf.NodeStore;
import de.cebitec.mgx.gui.wizard.configurations.data.util.Transform;
import de.cebitec.mgx.gui.wizard.configurations.menu.MenuController;
import de.cebitec.mgx.gui.wizard.configurations.menu.MenuView;
import de.cebitec.mgx.gui.wizard.configurations.start.StartController;
import de.cebitec.mgx.gui.wizard.configurations.summary.MenuSummaryController;
import de.cebitec.mgx.gui.wizard.configurations.utilities.ActionCommands;
import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.text.MessageFormat;
import java.util.*;
import java.util.logging.Logger;
import javax.swing.*;
import org.openide.DialogDescriptor;
import org.openide.DialogDisplayer;
import org.openide.NotifyDescriptor;
import org.openide.WizardDescriptor;
import org.openide.awt.ActionID;
import org.openide.awt.ActionReference;
import org.openide.awt.ActionRegistration;
import org.openide.util.Lookup;

//An example action demonstrating how the wizard could be called from within
//your code. You can move the code below wherever you need, or register an action:
//@ActionID(category = "test2",
//id = "de.wizard.view.ViewWizardAction")
//@ActionRegistration(displayName = "Open View Wizard")
//@ActionReference(path = "Menu/Tools")
public final class MenuAction implements ActionListener {

   private final static Logger LOGGER =
	 Logger.getLogger(MenuView.class.getName());
   /**
    * Der Store in dem die konfigurierbaren Nodes enthalten sind.
    */
   private Store store;
   /**
    * In diesem Parser werden die XML Dateien bearbeitet.
    */
   //private Parser parser;
   private JButton button;

   private HashMap<String,Tool> tools;
   private List<Tool> globalTools;
   private List<Tool> projectTools;
   /**
    * Der Konstruktor, der für die initialisation des Parsers verantwortlich
    * ist.
    */
   public MenuAction() { 
	//parser = Lookup.getDefault().lookup(Parser.class);
   }
boolean showTools;
   public void setTools(Collection<Tool> lGlobalTools,List<Tool> lProjectTools){
       showTools = true;
       
       tools = new HashMap<String, Tool>();
       
       projectTools = lProjectTools;
     // store = Transform.getFromJobParameterNodeStore(lParameter);
       globalTools = new ArrayList<>();
       
       for(Tool tool : lGlobalTools){
       
       tools.put("Global-Tools"+";"+tool.getName(), tool);
       globalTools.add(tool);
       
       }
       for(Tool tool : lProjectTools){
       
       tools.put("Project-Tools"+";"+tool.getName(), tool);
       }
   }
   
   public void setStore(Store lStore){
   showTools = false;
   this.store = lStore;
   
   }
   
   
   /**
    * Sobald der Wizard gestartet wird, wird diese Methode aufgerufen.
    *
    *
    * @param e Das Event zum starten des Wizards
    */
   @Override
   public void actionPerformed(ActionEvent e) {
      
       if(showTools){
       showTools();
       }else {
       showConfigurations(); 
       }
   }

    private void showConfigurations() {
        List<WizardDescriptor.Panel<WizardDescriptor>> panels =
            new ArrayList<WizardDescriptor.Panel<WizardDescriptor>>();

        Iterator iterator =
            store.getIterator();

        Map.Entry me;
        int nodeIndex = 1;

        while (iterator.hasNext()) {
           me = (Map.Entry) iterator.next();
           panels.add(
                 new MenuController(nodeIndex,
                 store.getNode((String) me.getKey())));
           nodeIndex++;
        }
        JobParameter parameter  = new JobParameter();

        panels.add(new MenuSummaryController(store));


        String[] steps = new String[panels.size()];

        for (int i = 0; i < panels.size(); i++) {
           Component c = panels.get(i).getComponent();

           steps[i] = c.getName();

           if (c instanceof JComponent) {
                JComponent jc = (JComponent) c;
                jc.putClientProperty(
                    WizardDescriptor.PROP_CONTENT_SELECTED_INDEX, i);
                jc.putClientProperty(WizardDescriptor.PROP_CONTENT_DATA, steps);
                jc.putClientProperty(WizardDescriptor.PROP_AUTO_WIZARD_STYLE,
                    true);
                jc.putClientProperty(WizardDescriptor.PROP_CONTENT_DISPLAYED,
                    true);
                jc.putClientProperty(WizardDescriptor.PROP_CONTENT_NUMBERED,
                    true);
           }
        }

        final WizardDescriptor wiz =
            new WizardDescriptor(
            new WizardDescriptor.ArrayIterator<WizardDescriptor>(panels));



        JButton setAllDefaultbutton = new JButton("Set default");
        setAllDefaultbutton.setToolTipText("Sets available default values.");
        setAllDefaultbutton.setActionCommand(ActionCommands.Default);


        Object[] optionButtons = {setAllDefaultbutton, WizardDescriptor.PREVIOUS_OPTION, WizardDescriptor.NEXT_OPTION, WizardDescriptor.FINISH_OPTION, WizardDescriptor.CANCEL_OPTION};

        wiz.setOptions(optionButtons);
        wiz.setTitleFormat(new MessageFormat("{0}"));
        wiz.setTitle("Configurations");

        Object[] objects = wiz.getOptions();

        button = (JButton) objects[2];
        button.setText("mein Text");

Action previousClickAction = new AbstractAction() {
           @Override
    public void actionPerformed(ActionEvent e) {
                wiz.doPreviousClick();
           }
};
button.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke("F2"),
                            "PreviousKey");
button.getActionMap().put("PreviousKey",
                             previousClickAction);
      //  startWizardConfigurations(wiz);
   this.wiz = wiz;
    
    }
WizardDescriptor wiz;
    public Tool startWizardTools() {
        if (DialogDisplayer.getDefault().notify(wiz)
            == WizardDescriptor.FINISH_OPTION) {
      
       return tools.get(wiz.getProperty("TOOL"));	    
        }
        
        return null;
    }


    public Store startWizardConfigurations() {
        if (DialogDisplayer.getDefault().notify(wiz)
            == WizardDescriptor.FINISH_OPTION) {
           removeNodesAndConfigItems(wiz);
       return store;
        }
        
        return null;
    }

    private void showTools() {
        List<WizardDescriptor.Panel<WizardDescriptor>> panels =
            new ArrayList<WizardDescriptor.Panel<WizardDescriptor>>();
        
        panels.add(new StartController(globalTools, projectTools));
        

        String[] steps = new String[panels.size()];

        for (int i = 0; i < panels.size(); i++) {
           Component c = panels.get(i).getComponent();

           steps[i] = c.getName();

           if (c instanceof JComponent) {
                JComponent jc = (JComponent) c;
                jc.putClientProperty(
                    WizardDescriptor.PROP_CONTENT_SELECTED_INDEX, i);
                jc.putClientProperty(WizardDescriptor.PROP_CONTENT_DATA, steps);
                jc.putClientProperty(WizardDescriptor.PROP_AUTO_WIZARD_STYLE,
                    true);
                jc.putClientProperty(WizardDescriptor.PROP_CONTENT_DISPLAYED,
                    true);
                jc.putClientProperty(WizardDescriptor.PROP_CONTENT_NUMBERED,
                    true);
           }
        }

      
         final WizardDescriptor wiz =
            new WizardDescriptor(
            new WizardDescriptor.ArrayIterator<WizardDescriptor>(panels));

         Object[] optionButtons = {WizardDescriptor.PREVIOUS_OPTION,  WizardDescriptor.FINISH_OPTION,
              WizardDescriptor.CANCEL_OPTION};
         
         wiz.setOptions(optionButtons);
         Object[] objects = wiz.getOptions();
         JButton button = (JButton) objects[1];
         button.setText("Next >");
         button.setMnemonic(KeyEvent.VK_N);
        wiz.setTitleFormat(new MessageFormat("{0}"));
        wiz.setTitle("Configurations");
       this.wiz = wiz;
    }

    
    
   /**
    * Nach dem Beenden des Wizards werden, alle Antworten in den Store zu den
    * jeweiligen Nodes und ConfigItems gegeben und dann alle Nodes und
    * configItems entfernt, die keine Antworten des Users enthalten.
    *
    * @param lWiz WizardDescriptor
    */
   private void removeNodesAndConfigItems(WizardDescriptor lWiz) {


	LOGGER.info("StoreSize: " + Integer.toString(store.storeSize()));


	Iterator nodeIterator = store.getIterator();
	Map.Entry nodeME;
	String nodeId;
	Iterator configItemIterator;
	while (nodeIterator.hasNext()) {



	   nodeME = (Map.Entry) nodeIterator.next();
	   nodeId = (String) nodeME.getKey();
	   configItemIterator = ((Node) nodeME.getValue()).getIterator();
	   Map.Entry configItemME;

	   String localProperty;
	   String configItemName;
	   String nodeClassName;

	   while (configItemIterator.hasNext()) {

		configItemME = (Map.Entry) configItemIterator.next();
		configItemName = (String) configItemME.getKey();
		nodeClassName = store.getNode(nodeId).getClassName();


		localProperty =
		    (String) lWiz.getProperty(
		    nodeId
		    + nodeClassName
		    + configItemName);

		store.getNode(nodeId).getConfigItem(configItemName).setAnswer(localProperty);
	   }
	}
	store.deleteEmptyNodes();
	test(lWiz);
   }

   private void test(WizardDescriptor wiz) {
	LOGGER.info("StoreSize After: "
	    + Integer.toString(store.storeSize()));

	ArrayList<ArrayList<String>> propertys =
	    new ArrayList<ArrayList<String>>();


	HashMap<String, HashMap<String, String>> nodeMap = store.getAllAnswers();

	for (String nodeKey : nodeMap.keySet()) {

	   ArrayList<String> property = new ArrayList<String>();

	   HashMap<String, String> map = nodeMap.get(nodeKey);

	   for (String s : map.keySet()) {
		property.add(map.get(s));
	   }

	   DialogDisplayer.getDefault().notify(
		 new NotifyDescriptor.Message(
		 store.getNode(nodeKey).getDisplayName() + " "
		 + property));
	}
   }
}


//~ Formatted by Jindent --- http://www.jindent.com
