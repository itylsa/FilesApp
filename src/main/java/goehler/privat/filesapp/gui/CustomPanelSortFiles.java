/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package goehler.privat.filesapp.gui;

import goehler.privat.filesapp.controller.FileController;
import goehler.privat.filesapp.service.GuiService;
import java.awt.Color;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.dnd.DnDConstants;
import java.awt.dnd.DragGestureEvent;
import java.awt.dnd.DragGestureListener;
import java.awt.dnd.DragSource;
import java.awt.dnd.DragSourceDragEvent;
import java.awt.dnd.DragSourceDropEvent;
import java.awt.dnd.DragSourceEvent;
import java.awt.dnd.DragSourceListener;
import java.awt.dnd.DropTarget;
import java.awt.dnd.DropTargetDragEvent;
import java.awt.dnd.DropTargetDropEvent;
import java.awt.dnd.DropTargetEvent;
import java.awt.dnd.DropTargetListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.TransferHandler;

/**
 *
 * @author Heiko
 */
/**
 * Our custom JButton class that is Draggable. This JButton is Transferable (so
 * it can be Dragged), And listens for its own drags
 *
 */
public class CustomPanelSortFiles extends JPanel implements Transferable,
                                                   DragSourceListener, DragGestureListener {

    //marks this JButton as the source of the Drag
    private DragSource source;

    private TransferHandler t;

    CustomPanelSortFiles p;
    
    Window window;

    public CustomPanelSortFiles(File file, int x, int y, int width, int height, DecimalFormatSymbols dfs, DecimalFormat df, Window window) throws IOException {
        
        this.setBounds(x, y, width, height);
        this.setBackground(Color.lightGray);
        this.setVisible(true);
        this.setLayout(null);

        JLabel nameLabel = new JLabel(file.getName());
        nameLabel.setBounds(10, 5, 370, 20);
        nameLabel.setVisible(true);

        String[] modified = Files.getLastModifiedTime(file.toPath(), LinkOption.NOFOLLOW_LINKS).toString().split("T");
        String date = modified[0];
        date = date.split("-")[2] + "." + date.split("-")[1] + "." + date.split("-")[0];
        String time = modified[1];
        time = time.split("Z")[0];
        time = time.split(":")[0] + ":" + time.split(":")[1] + ":" + df.format(Double.parseDouble(time.split(":")[2]));

        JLabel dateLabel = new JLabel(date + " - " + time);
        dateLabel.setBounds(380, 5, 180, 20);
        dateLabel.setVisible(true);

        dfs.setDecimalSeparator(',');
        df.setDecimalFormatSymbols(dfs);
        double size;
        if(file.isDirectory()) {
            size = window.getFc().getDirSize(file);
        } else {
            size = file.length();
        }
        String fileSize = size <= 1024 ? df.format(size) + " B" : size <= 1048576 ? df.format(size / 1024) + " Kb" : df.format(size / 1024 / 1024) + " Mb";
        JLabel sizeLabel = new JLabel(fileSize);
        sizeLabel.setBounds(565, 5, 180, 20);
        sizeLabel.setVisible(true);

        this.add(nameLabel);
        this.add(dateLabel);
        this.add(sizeLabel);
        p = this;

        p.setDropTarget(new DropTarget(p, new DropTargetListener() {
                                   @Override
                                   public void dragEnter(DropTargetDragEvent dtde) {
                                   }

                                   @Override
                                   public void dragOver(DropTargetDragEvent dtde) {
                                   }

                                   @Override
                                   public void dropActionChanged(DropTargetDragEvent dtde) {
                                   }

                                   @Override
                                   public void dragExit(DropTargetEvent dte) {
                                   }

                                   @Override
                                   public void drop(DropTargetDropEvent e) {
                                       CustomPanelSortFiles panel1 = (CustomPanelSortFiles) e.getDropTargetContext().getComponent();
                                       try {
                                           CustomPanelSortFiles panel2 = (CustomPanelSortFiles) e.getTransferable().getTransferData(new DataFlavor(CustomPanelSortFiles.class, "CustomPanel"));
                                           JLabel dropFile = (JLabel) panel1.getComponents()[0];
                                           JLabel dragFile = (JLabel) panel2.getComponents()[0];
                                           window.getFc().getFs().changeFileDate(dragFile.getText(), dropFile.getText());
                                       } catch(UnsupportedFlavorException ex) {
                                           Logger.getLogger(GuiService.class.getName()).log(Level.SEVERE, null, ex);
                                       } catch(IOException ex) {
                                           Logger.getLogger(GuiService.class.getName()).log(Level.SEVERE, null, ex);
                                       }
                                   }
                               }));
        p.getDropTarget().setActive(true);
        p.addMouseListener(new MouseListener() {
            @Override
            public void mouseClicked(MouseEvent e) {
                //                    Do nothing
            }

            @Override
            public void mousePressed(MouseEvent e) {
                TransferHandler handler = p.getTransferHandler();
                handler.exportAsDrag(p, e, TransferHandler.MOVE);
            }

            @Override
            public void mouseReleased(MouseEvent e) {
            }

            @Override
            public void mouseEntered(MouseEvent e) {
                //                    Do nothing
            }

            @Override
            public void mouseExited(MouseEvent e) {
                //                    Do nothing
            }
        });

        //The TransferHandler returns a new DnDButton
        //to be transferred in the Drag
        t = new TransferHandler() {

            public Transferable createTransferable(JComponent c) {
                return p;
            }
        };
        setTransferHandler(t);

        //The Drag will copy the DnDButton rather than moving it
        source = new DragSource();
        source.createDefaultDragGestureRecognizer(this, DnDConstants.ACTION_COPY, this);
    }

    //The DataFlavor is a marker to let the DropTarget know how to
    //handle the Transferable
    public DataFlavor[] getTransferDataFlavors() {
        return new DataFlavor[] {new DataFlavor(CustomPanelSortFiles.class, "JPanel")};
    }

    public boolean isDataFlavorSupported(DataFlavor flavor) {
        return true;
    }

    public Object getTransferData(DataFlavor flavor) {
        return this;
    }

    public void dragEnter(DragSourceDragEvent dsde) {
    }

    public void dragOver(DragSourceDragEvent dsde) {
    }

    public void dropActionchanged(DragSourceDragEvent dsde) {
    }

    public void dragExit(DragSourceEvent dse) {
    }

    //when the drag finishes, then repaint the DnDButton
    //so it doesn't look like it has still been pressed down
    public void dragDropEnd(DragSourceDropEvent dsde) {
        repaint();
    }

    //when a DragGesture is recognized, initiate the Drag
    public void dragGestureRecognized(DragGestureEvent dge) {
        source.startDrag(dge, DragSource.DefaultMoveDrop, p, this);
    }

    @Override
    public void dropActionChanged(DragSourceDragEvent dsde) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

}//end outer class

