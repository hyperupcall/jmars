package edu.asu.jmars.swing;

import java.awt.*;
import java.awt.event.*;
import java.awt.datatransfer.*;

import javax.swing.*;
import javax.swing.text.*;

public class PopupEventQueue extends EventQueue {
	public JPopupMenu popup;
	public BasicAction cut, copy, paste, selectAll;

	public PopupEventQueue() {
	}

	public void createPopupMenu(JTextComponent text) {
		
		cut = new CutAction("Cut", null);
		copy = new CopyAction("Copy", null);
		paste = new PasteAction("Paste", null);
		selectAll = new SelectAllAction("Select All", null);
		cut.setTextComponent(text);
		copy.setTextComponent(text);
		paste.setTextComponent(text);
		selectAll.setTextComponent(text);

		popup = new JPopupMenu();
		popup.add(cut);
		popup.add(copy);
		popup.add(paste);
		popup.addSeparator();
		popup.add(selectAll);
	}

	public void showPopup(Component parent, MouseEvent me) {
		popup.validate();
		popup.show(parent, me.getX(), me.getY());
	}

	protected void dispatchEvent(AWTEvent event) {
		super.dispatchEvent(event);
		if (!(event instanceof MouseEvent)) {
			return;
		}
		MouseEvent me = (MouseEvent) event;
		if (!me.isPopupTrigger()) {
			return;
		}
		if (!(me.getSource() instanceof Component)) {
			return;
		}
		Component comp = SwingUtilities.getDeepestComponentAt(
				(Component) me.getSource(), me.getX(), me.getY());
		if (!(comp instanceof JTextComponent)) {
			return;
		}
		if (MenuSelectionManager.defaultManager().getSelectedPath().length > 0) {
			return;
		}
		createPopupMenu((JTextComponent) comp);
		showPopup((Component) me.getSource(), me);
	}

	public abstract class BasicAction extends AbstractAction {
		private static final long serialVersionUID = 1L;
		JTextComponent comp;

		public BasicAction(String text, Icon icon) {
			super(text, icon);
			putValue(Action.SHORT_DESCRIPTION, text);
		}

		public void setTextComponent(JTextComponent comp) {
			this.comp = comp;
		}

		public abstract void actionPerformed(ActionEvent e);
	}

	public class CutAction extends BasicAction {
		private static final long serialVersionUID = 1L;
		public CutAction(String text, Icon icon) {
			super(text, icon);
			putValue(Action.ACCELERATOR_KEY, KeyStroke.getKeyStroke(KeyEvent.VK_X, Toolkit.getDefaultToolkit().getMenuShortcutKeyMask()));
		}

		public void actionPerformed(ActionEvent e) {
			comp.cut();
		}

		public boolean isEnabled() {
			return comp != null && comp.isEditable()
					&& comp.getSelectedText() != null;
		}
	}

	public class CopyAction extends BasicAction {
		private static final long serialVersionUID = 1L;
		public CopyAction(String text, Icon icon) {
			super(text, icon);
			putValue(Action.ACCELERATOR_KEY, KeyStroke.getKeyStroke(KeyEvent.VK_C, Toolkit.getDefaultToolkit().getMenuShortcutKeyMask()));
		}

		public void actionPerformed(ActionEvent e) {
			comp.copy();
		}

		public boolean isEnabled() {
			return comp != null && comp.getSelectedText() != null;
		}
	}

	public class PasteAction extends BasicAction {
		private static final long serialVersionUID = 1L;
		public PasteAction(String text, Icon icon) {
			super(text, icon);
			putValue(Action.ACCELERATOR_KEY, KeyStroke.getKeyStroke(KeyEvent.VK_V, Toolkit.getDefaultToolkit().getMenuShortcutKeyMask()));
		}

		public void actionPerformed(ActionEvent e) {
			comp.paste();
		}

		public boolean isEnabled() {
			Transferable content = Toolkit.getDefaultToolkit()
					.getSystemClipboard().getContents(null);
			return comp != null && comp.isEnabled() && comp.isEditable()
					&& content.isDataFlavorSupported(DataFlavor.stringFlavor);
		}
	}

	public class SelectAllAction extends BasicAction {
		private static final long serialVersionUID = 1L;
		public SelectAllAction(String text, Icon icon) {
			super(text, icon);
			putValue(Action.ACCELERATOR_KEY, KeyStroke.getKeyStroke(KeyEvent.VK_A, Toolkit.getDefaultToolkit().getMenuShortcutKeyMask()));
		}

		public void actionPerformed(ActionEvent e) {
			comp.selectAll();
		}

		public boolean isEnabled() {
			return comp != null
					&& comp.isEnabled()
					&& comp.getText().length() > 0
					&& (comp.getSelectedText() == null || comp
							.getSelectedText().length() < comp.getText()
							.length());
		}
	}
}
