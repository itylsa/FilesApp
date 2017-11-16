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
public class CustomPanelDeleteFiles extends JPanel {

    Window window;

    public CustomPanelDeleteFiles(File file, int x, int y, int width, int height, DecimalFormatSymbols dfs, DecimalFormat df, Window window) throws IOException {
        
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
        
        JButton deleteButton = new JButton("x");
        deleteButton.setBounds(700, 0, 40, 30);
        deleteButton.setVisible(true);
        
        deleteButton.addActionListener((e) -> {
            if(file.isDirectory()) {
                window.getFc().deleteDirectory(file);
                file.delete();
            } else {
                file.delete();
            }
            window.getFc().scanForFilesToDelete();
            window.getPanelDeleteFiles().repaint();
        });

        this.add(nameLabel);
        this.add(dateLabel);
        this.add(sizeLabel);
        this.add(deleteButton);
    }
}

