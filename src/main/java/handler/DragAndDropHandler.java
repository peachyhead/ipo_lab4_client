package handler;

import javax.swing.*;
import java.awt.datatransfer.DataFlavor;
import java.awt.dnd.*;
import java.awt.event.ActionEvent;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.util.List;

public class DragAndDropHandler implements DropTargetListener {

    private final Action dragEnterAction = new AbstractAction() {
        @Override
        public void actionPerformed(ActionEvent e) {
            
        }
    };

    private final Action dragExitAction = new AbstractAction() {
        @Override
        public void actionPerformed(ActionEvent e) {

        }
    };

    private final Action dropAction = new AbstractAction() {
        @Override
        public void actionPerformed(ActionEvent e) {

        }
    };
    
    @Override
    public void dragEnter(DropTargetDragEvent dtde) {
        dragEnterAction.putValue("enter", true);
    }

    @Override
    public void dragOver(DropTargetDragEvent dtde) {

    }

    @Override
    public void dropActionChanged(DropTargetDragEvent dtde) {

    }

    @Override
    public void dragExit(DropTargetEvent dte) {
        dragExitAction.putValue("exit", true);
    }

    @Override
    public void drop(DropTargetDropEvent event) {
        event.acceptDrop(DnDConstants.ACTION_COPY);
        var transferable = event.getTransferable();
        var flavors = transferable.getTransferDataFlavors();
        var dropped = "";
        
        for (DataFlavor flavor : flavors) {
            try {
                // If the drop items are files
                if (flavor.isFlavorJavaFileListType()) {
                    var files = (List)transferable.getTransferData(flavor);
                    
                    for (Object file : files) {
                        JOptionPane.showMessageDialog(null, file);
                        var castedFile = (File)file;
                        dropped = castedFile.getAbsolutePath();
                    }
                }
            } catch (Exception e) {
                // Print out the error stack
                e.printStackTrace();
            }
        }

        // Inform that the drop is complete
        event.dropComplete(true);
        dropAction.putValue("drop", dropped);
    }
    
    public void subscribeOnDragEnter(PropertyChangeListener listener) {
        dragEnterAction.addPropertyChangeListener(listener);
    }
    
    public void subscribeOnDragExit(PropertyChangeListener listener) {
        dragExitAction.addPropertyChangeListener(listener);
    }
    
    public void subscribeOnDrop(PropertyChangeListener listener) {
        dropAction.addPropertyChangeListener(listener);
    }
}
