/*******************************************************************************
 * Copyright (c) 2010 Ale46.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v2.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/old-licenses/gpl-2.0.html
 ******************************************************************************/
import java.awt.Point;
import java.awt.Toolkit;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.AbstractAction;
import javax.swing.JPopupMenu;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.text.Document;
import javax.swing.text.JTextComponent;
 
 
/**
 * Extended JTextField which provides cut/copy/paste/delete/selectAll actions
 * via a popup menu. This provides similar operations to the windows system.
 *
 * @author steve.webb
 */
public class ExtendedTextField extends JTextField
{
    
    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/**
     * Constructs a new ExtendedTextField. A default model is created, the initial string is 
     * null, and the number of columns is set to 0.
     */
    public ExtendedTextField()
    {
        super();
        initialize();
    }
    
    /**
     * Constructs a new ExtendedTextField that uses the given text storage model and the given 
     * number of columns. This is the constructor through which the other constructors 
     * feed. If the document is null, a default model is created.
     * @param doc the text storage to use; if this is null, a default will be provided by calling the createDefaultModel method
     * @param text the initial string to display, or null
     * @param columns the number of columns to use to calculate the preferred width >= 0; if columns  is set to zero, the preferred 
     * width will be whatever naturally results from the component implementation.
     */
    public ExtendedTextField(Document doc, String text, int columns)
    {
        super(doc, text, columns);
        initialize();
    }
    
    /**
     * Constructs a new empty ExtendedTextField with the specified number of columns. A default 
     * model is created and the initial string is set to null.
     * @param columns the number of columns to use to calculate the preferred width; if columns is set 
     * to zero, the preferred width will be whatever naturally results from the component 
     * implementation.
     */
    public ExtendedTextField(int columns)
    {
        super(columns);
        initialize();
    }
    
    /**
     * Constructs a new ExtendedTextField initialized with the specified text. A default model 
     * is created and the number of columns is 0.
     * @param text the text to be displayed, or null
     */
    public ExtendedTextField(String text)
    {
        super(text);
        initialize();
    }
    
    /**
     * Constructs a new ExtendedTextField initialized with the specified text and columns. A default 
     * model is created.
     * @param text the text to be displayed, or null
     * @param columns the number of columns to use to calculate the preferred width; if columns 
     * is set to zero, the preferred width will be whatever naturally results 
     * from the component implementation
     */
    public ExtendedTextField(String text, int columns)
    {
        super(text, columns);
        initialize();
    }
    
    /**
     * Initialize's specific behaviour for this class.
     */
    protected void initialize()
    {
        // Setup the popup menu
        
        // Add the mouse listener
        this.addMouseListener(new MouseAdapter()
        {
            public void mouseClicked(MouseEvent evt)
            {
                requestFocus();
                dealWithMousePress(evt);
            }
        });
    }
    
    /**
     * The mouse has been pressed over this text field. Popup the cut/paste menu.
     * @param evt mouse event
     */
    protected void dealWithMousePress(MouseEvent evt)
    {
        // Only interested in the right button
        if(SwingUtilities.isRightMouseButton(evt))
        {
            //if(MenuSelectionManager.defaultManager().getSelectedPath().length>0)
            //return;
            
            JPopupMenu menu = new JPopupMenu();
            menu.add(new CutAction(this));
            menu.add(new CopyAction(this));
            menu.add(new PasteAction(this));
            menu.add(new DeleteAction(this));
            menu.addSeparator();
            menu.add(new SelectAllAction(this));
            
            // Display the menu
            Point pt = SwingUtilities.convertPoint(evt.getComponent(), evt.getPoint(), this);
            menu.show(this, pt.x, pt.y);
        }
    }
}
class SelectAllAction extends AbstractAction
{
    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	/**
     * Icon to displayed against this action.
     */
    //static final private ImageIcon icon =
    //        new ImageIcon(ClassLoader.getSystemResource("toolbarButtonGraphics/general/Import16.gif"));
    
    /**
     * The component the action is associated with.
     */
    protected JTextComponent comp;
    
    /**
     * Default constructor.
     * @param comp The component the action is associated with.
     */
    public SelectAllAction(JTextComponent comp)
    {
        super("Select All" /*,icon*/);
        this.comp = comp;
    }
    
    /**
     * Action has been performed on the component.
     * @param e ignored
     */
    public void actionPerformed(ActionEvent e)
    {
        comp.selectAll();
        /* Need to also selectAll() via a later because in the case of FormattedText fields
         * the field is re-drawn if the request is made durring a focusGained event.
         * This is a pain but there doesn't appear to be any need solution to this and it is
         * a known swing bug but it isn't going to be fixed anytime soon. */
        SwingUtilities.invokeLater(new Runnable()
        {
            public void run()
            {
                comp.selectAll();
            }
        });
        
    }
    
    /**
     * Checks if the action can be performed.
     * @return True if the action is allowed
     */
    public boolean isEnabled()
    {
        return comp.isEnabled()
        && comp.getText().length()>0;
    }


}

class PasteAction extends AbstractAction
{
    //static final private ImageIcon icon =
    //        new ImageIcon(ClassLoader.getSystemResource("toolbarButtonGraphics/general/Paste16.gif"));
    
    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	/**
     * The component the action is associated with.
     */
    JTextComponent comp;
    
    /**
     * Default constructor.
     * @param comp The component the action is associated with.
     */
    public PasteAction(JTextComponent comp)
    {
        super("Paste" /*,icon*/);
        this.comp = comp;
    }
    
    /**
     * Action has been performed on the component.
     * @param e ignored
     */
    public void actionPerformed(ActionEvent e)
    {
        comp.paste();
    }
    
    /**
     * Checks if the action can be performed.
     * @return True if the action is allowed
     */
    public boolean isEnabled()
    {
        if (comp.isEditable() && comp.isEnabled())
        {
            Transferable contents = Toolkit.getDefaultToolkit().getSystemClipboard().getContents(this);
            return contents.isDataFlavorSupported(DataFlavor.stringFlavor);
        }
        else
            return false;
    }
}

class DeleteAction extends AbstractAction
{
    /**
     * Icon to displayed against this action.
     */
    //static final private ImageIcon icon =
    //        new ImageIcon(ClassLoader.getSystemResource("toolbarButtonGraphics/general/Delete16.gif"));
    
    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	/**
     * The component the action is associated with.
     */
    JTextComponent comp;
    
    /**
     * Default constructor.
     * @param comp The component the action is associated with.
     */
    public DeleteAction(JTextComponent comp)
    {
        super("Delete" /*,icon*/);
        this.comp = comp;
    }
    
    /**
     * Action has been performed on the component.
     * @param e ignored
     */
    public void actionPerformed(ActionEvent e)
    {
        comp.replaceSelection(null);
    }
    
    /**
     * Checks if the action can be performed.
     * @return True if the action is allowed
     */
    public boolean isEnabled()
    {
        return comp.isEditable()
        && comp.isEnabled()
        && comp.getSelectedText()!=null;
    }
}

class CutAction extends AbstractAction
{
    /**
     * Icon to displayed against this action.
     */
    //static final private ImageIcon icon =
    //        new ImageIcon(ClassLoader.getSystemResource("toolbarButtonGraphics/general/Cut16.gif"));
    
    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	/**
     * The component the action is associated with.
     */
    JTextComponent comp;
    
    /**
     * Default constructor.
     * @param comp The component the action is associated with.
     */
    public CutAction(JTextComponent comp)
    {
        super("Cut" /*,icon*/);
        this.comp = comp;
    }
    
    /**
     * Action has been performed on the component.
     * @param e ignored
     */
    public void actionPerformed(ActionEvent e)
    {
        comp.cut();
    }
    
    /**
     * Checks if the action can be performed.
     * @return True if the action is allowed
     */
    public boolean isEnabled()
    {
        return comp.isEditable()
        && comp.isEnabled()
        && comp.getSelectedText()!=null;
    }
}

class CopyAction extends AbstractAction
{
    /**
     * Icon to displayed against this action.
     */
    //static final private ImageIcon icon =
    //        new ImageIcon(ClassLoader.getSystemResource("toolbarButtonGraphics/general/Copy16.gif"));
    
    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	/**
     * The component the action is associated with.
     */
    JTextComponent comp;
    
    /**
     * Default constructor.
     * @param comp The component the action is associated with.
     */
    public CopyAction(JTextComponent comp)
    {
        super("Copy" /*,icon*/);
        this.comp = comp;
    }
    
    /**
     * Action has been performed on the component.
     * @param e ignored
     */
    public void actionPerformed(ActionEvent e)
    {
        comp.copy();
    }
    
    /**
     * Checks if the action can be performed.
     * @return True if the action is allowed
     */
    public boolean isEnabled()
    {
        return comp.isEnabled()
        && comp.getSelectedText()!=null;
    }
}

