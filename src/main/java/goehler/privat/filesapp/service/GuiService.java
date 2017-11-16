/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package goehler.privat.filesapp.service;

import goehler.privat.filesapp.controller.FileController;
import goehler.privat.filesapp.controller.GuiController;
import goehler.privat.filesapp.gui.CustomPanelDeleteFiles;
import goehler.privat.filesapp.gui.CustomPanelSortFiles;
import goehler.privat.filesapp.gui.Window;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.dnd.DragSource;
import java.awt.dnd.DropTarget;
import java.awt.dnd.DropTargetDragEvent;
import java.awt.dnd.DropTargetDropEvent;
import java.awt.dnd.DropTargetEvent;
import java.awt.dnd.DropTargetListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.File;
import java.io.IOException;
import java.math.RoundingMode;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.TransferHandler;

/**
 *
 * @author Heiko
 */
public class GuiService {

    GuiController gc;

    Window window;

    FileController fc;

    List<JPanel> itemList = new ArrayList<>();

    public GuiService(GuiController gc, Window window) {
        this.gc = gc;
        this.window = window;
    }

    List<JCheckBox> fileExtensionCheckboxes = new ArrayList<>();

    public List<JCheckBox> getFileExtensionCheckboxes() {
        return fileExtensionCheckboxes;
    }

    public void setFileExtensionCheckboxes(List<JCheckBox> fileExtensionCheckboxes) {
        this.fileExtensionCheckboxes = fileExtensionCheckboxes;
    }

    public void addFileExtensionCheckboxes(List<String> extensions) {
        int x = 10;
        int y = 20;
        int height = 24;
        int width = 100;
        fileExtensionCheckboxes.clear();
        window.getPanelExtenstionChooser().removeAll();
        window.repaint();
        int number = extensions.size();
        int xi = 1;
        int yi = 1;
        for(int i = 0; i < number; i++) {
            if(i < 6) {
                y = 7;
            } else {
                y = 20;
            }
            if(i == 0 || i % 6 == 0) {
                fileExtensionCheckboxes.add(addCheckbox(extensions.get(i), x, (i > 5 ? (y * yi) - 13 : (y * yi) - 0), width, height));
            } else {
                fileExtensionCheckboxes.add(addCheckbox(extensions.get(i), (x + ((width + 10) * (xi - 1))), (i > 5 ? (y * yi) - 13 : (y * yi) - 0), width, height));
            }
            if(xi == 6) {
                xi = 1;
                yi++;
            } else {
                xi++;
            }
        }

        x = 10;
        y = 20;

        window.getPanelExtenstionChooser().setPreferredSize(new Dimension(window.getPanelExtenstionChooser().getPreferredSize().width, (y * yi + 20)));
        window.repaint();
    }

    private JCheckBox addCheckbox(String text, int x, int y, int width, int height) {
        JCheckBox cb = new JCheckBox(text);
        cb.setBounds(x, y, width, height);
        cb.setOpaque(true);
        cb.setBackground(Color.lightGray);
        cb.setVisible(true);
        window.getPanelExtenstionChooser().add(cb);
        return cb;
    }

    public void addFilesToSort(List<File> filesToSort) {
        itemList = new ArrayList<>();
        int x = 0;
        int y = 10;
        int height = 30;
        int width = 760;
        for(int i = 0; i < filesToSort.size(); i++) {
            if(i == 0) {
                addFileToSort(filesToSort.get(i), x, y, width, height);
            } else {
                addFileToSort(filesToSort.get(i), x, (y + ((height + 5) * i)), width, height);
            }
        }
        window.getPanelSortFiles().removeAll();
        for(JPanel panel : itemList) {
            window.getPanelSortFiles().add(panel);
        }
        window.getPanelSortFiles().setPreferredSize(new Dimension(window.getPanelSortFiles().getPreferredSize().width, (y + (height + 5) * filesToSort.size() + 10)));
        window.repaint();
    }

    public void addFilesToDelete(List<File> filesToDelete) {
        itemList = new ArrayList<>();
        int x = 0;
        int y = 10;
        int height = 30;
        int width = 760;
        for(int i = 0; i < filesToDelete.size(); i++) {
            if(i == 0) {
                addFileToDelete(filesToDelete.get(i), x, y, width, height);
            } else {
                addFileToDelete(filesToDelete.get(i), x, (y + ((height + 5) * i)), width, height);
            }
        }
        window.getPanelDeleteFiles().removeAll();
        for(JPanel panel : itemList) {
            window.getPanelDeleteFiles().add(panel);
        }
        window.getPanelDeleteFiles().setPreferredSize(new Dimension(window.getPanelSortFiles().getPreferredSize().width, (y + (height + 5) * filesToDelete.size() + 10)));
        window.repaint();
    }

    public void addFilesInformations(List<String[]> filesInformations) {
        window.getTaFilesInformations().removeAll();
        addLineToFilesInformations(new String[] {"Type", "Count", "Size"});
        addLineToFilesInformations(new String[] {"", "", ""});
        for(String[] line : filesInformations) {
            addLineToFilesInformations(line);
        }
    }

    private void addLineToFilesInformations(String[] line) {
        String c1 = String.format("%-30s", line[0]);
        String c2 = String.format("%-20s", line[1]);
        String c3 = String.format("%-20s", line[2]);
        window.getTaFilesInformations().append(c1 + c2 + c3 + "\n");
    }

    private void addFileToSort(File file, int x, int y, int width, int height) {
        try {
            DecimalFormat df = new DecimalFormat("#.##");
            df.setRoundingMode(RoundingMode.CEILING);
            DecimalFormatSymbols dfs = new DecimalFormatSymbols(Locale.GERMANY);
            dfs.setDecimalSeparator('.');
            df.setDecimalFormatSymbols(dfs);
            CustomPanelSortFiles panel = new CustomPanelSortFiles(file, x, y, width, height, dfs, df, window);
            itemList.add(panel);
        } catch(IOException ex) {
            Logger.getLogger(GuiService.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private void addFileToDelete(File file, int x, int y, int width, int height) {
        try {
            DecimalFormat df = new DecimalFormat("#.##");
            df.setRoundingMode(RoundingMode.CEILING);
            DecimalFormatSymbols dfs = new DecimalFormatSymbols(Locale.GERMANY);
            dfs.setDecimalSeparator('.');
            df.setDecimalFormatSymbols(dfs);
            CustomPanelDeleteFiles panel = new CustomPanelDeleteFiles(file, x, y, width, height, dfs, df, window);
            itemList.add(panel);
        } catch(IOException ex) {
            Logger.getLogger(GuiService.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void setFc(FileController fc) {
        this.fc = fc;
    }
}
