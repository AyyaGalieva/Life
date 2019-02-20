package ru.nsu.fit.g16201.galieva.Life.View;

import ru.nsu.fit.g16201.galieva.Life.Model.CellParameters;
import ru.nsu.fit.g16201.galieva.Life.Model.Field;
import ru.nsu.fit.g16201.galieva.Life.Model.Model;
import ru.nsu.fit.g16201.galieva.Life.View.Listeners.FieldPanelClickListener;

import javax.swing.*;
import javax.swing.border.BevelBorder;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.util.Map;
import java.util.TreeMap;

public class GUI extends JFrame {
    private FieldPanel cellsPanel;
    //private Field field;
    private MenuBar menuBar;
    private JToolBar toolBar;
    private JScrollPane scrollPane;
    private JLabel statusBar;

    private Map<String, AbstractButton> buttonMap = new TreeMap<>();
    private Map<String, Menu> menuMap = new TreeMap<>();
    private Map<String, MenuItem> menuItemMap = new TreeMap<>();

    private Model model;

    public GUI(Model model) {
        this.model = model;
        cellsPanel = new FieldPanel(model.getField(), new FieldPanelClickListener() {
            @Override
            public void onClick(Point p) {
                if (model != null) {
                    model.clickCell(p.x, p.y);
                }
            }
        });
        setTitle("Life");
        setSize(1000, 800);

        setLocationByPlatform(true);

        addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent event) {
                close();
            }
        });

        toolBar = new JToolBar();
        menuBar = new MenuBar();
        this.setMenuBar(menuBar);

        statusBar = new JLabel();
        statusBar.setPreferredSize(new Dimension(150, 15));
        statusBar.setBorder(new BevelBorder(BevelBorder.LOWERED));
        statusBar.setBackground(Color.white);

        scrollPane = new JScrollPane(cellsPanel);
        add(scrollPane, BorderLayout.CENTER);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);

        addButton("Save", "File", "Save current state", true, () -> {
            JFileChooser fileChooser = new JFileChooser(System.getProperty("user.dir") + "\\Data\\");
            fileChooser.setDialogTitle("Save state");
            int f = fileChooser.showSaveDialog(GUI.this);
            if (f == JFileChooser.APPROVE_OPTION) {
                File file = fileChooser.getSelectedFile();
                model.saveParametersInFile(file.getAbsolutePath(), cellsPanel.getCellParameters());
                cellsPanel.setStateChanged(false);
            }
        });

        addButton("Load", "File", "Load state", true, () -> {
            JFileChooser fileChooser = new JFileChooser(System.getProperty("user.dir") + "\\Data\\");
            fileChooser.setDialogTitle("Load state");
            int f = fileChooser.showOpenDialog(GUI.this);
            if (f == JFileChooser.APPROVE_OPTION) {
                File file = fileChooser.getSelectedFile();
                model.loadParametersFromFile(file.getAbsolutePath());
            }
        });

        toolBar.addSeparator();

        addButton("Replace", "Edit", "Sets states of clicked/dragged cells to alive", false, () -> {
            buttonMap.get("XOR").setSelected(false);
            ((CheckboxMenuItem)menuItemMap.get("XOR")).setState(false);
            buttonMap.get("Replace").setSelected(true);
            ((CheckboxMenuItem)menuItemMap.get("Replace")).setState(true);

            if (model != null)
                model.turnOnReplaceMode();
        });

        buttonMap.get("Replace").doClick();
        ((CheckboxMenuItem)menuItemMap.get("Replace")).setState(true);

        addButton("XOR", "Edit", "Inverts states of clicked/dragged cells", false, () -> {
            buttonMap.get("Replace").setSelected(false);
            ((CheckboxMenuItem)menuItemMap.get("Replace")).setState(false);
            buttonMap.get("XOR").setSelected(true);
            ((CheckboxMenuItem)menuItemMap.get("XOR")).setState(true);

            model.turnOnXORMode();
        });

        toolBar.addSeparator();

        addButton("Clear", "Edit", "Clears field", true, () -> {
            model.clearField();
        });

        addButton("Impact", "View", "Shows impacts of cells", false, new Runnable() {
            boolean pressed = false;

            @Override
            public void run() {
                pressed = ! pressed;
                buttonMap.get("Impact").setSelected(pressed);
                ((CheckboxMenuItem)menuItemMap.get("Impact")).setState(pressed);

                cellsPanel.showImpacts(pressed);
                cellsPanel.repaint();
            }
        });

        addButton("Step", "Edit", "Make one step of living process", true, ()-> {
            model.makeStep();
        });

        addButton("Run", "Edit", "Runs process of living", false, new Runnable() {
            boolean pressed = false;

            @Override
            public void run() {
                pressed = !pressed;

                buttonMap.get("Save"). setEnabled(!pressed);
                menuItemMap.get("Save").setEnabled(!pressed);
                buttonMap.get("Load"). setEnabled(!pressed);
                menuItemMap.get("Load").setEnabled(!pressed);
                buttonMap.get("Clear"). setEnabled(!pressed);
                menuItemMap.get("Clear").setEnabled(!pressed);
                buttonMap.get("Step"). setEnabled(!pressed);
                menuItemMap.get("Step").setEnabled(!pressed);

                model.setRunMode(pressed);
            }
        });

        toolBar.addSeparator();

        addButton("Settings", "View", "Show game settings", true, () -> {
            SettingsDialog settingsDialog = new SettingsDialog(model.getField(), cellsPanel.getCellParameters(), model.getParameters(), buttonMap.get("XOR").isSelected(), (cellParams, gameParams, width, height, isXOR) -> {
                if (isXOR) {
                    buttonMap.get("Replace").setSelected(false);
                    ((CheckboxMenuItem)menuItemMap.get("Replace")).setState(false);
                    buttonMap.get("XOR").setSelected(true);
                    ((CheckboxMenuItem)menuItemMap.get("XOR")).setState(true);

                    model.turnOnXORMode();
                } else {
                    buttonMap.get("XOR").setSelected(false);
                    ((CheckboxMenuItem)menuItemMap.get("XOR")).setState(false);
                    buttonMap.get("Replace").setSelected(true);
                    ((CheckboxMenuItem)menuItemMap.get("Replace")).setState(true);

                    model.turnOnReplaceMode();
                }

                updateCellParameters(cellParams);

                model.setFieldSize(width, height);
                model.setParameters(gameParams);

                cellsPanel.repaint();
            });
            settingsDialog.setLocationRelativeTo(this);
            settingsDialog.setVisible(true);
        });

        addButton("Info", "Help", "Show author info", true, () ->
            JOptionPane.showMessageDialog(null, "Life v.1.0\n" + "Author:\t Ayya Galieva, gr. 16201",
                "Author info", JOptionPane.INFORMATION_MESSAGE));
        add(toolBar, BorderLayout.NORTH);
        add(statusBar, BorderLayout.SOUTH);
    }

    private void addButton(String name, String menuName, String toolTipText, boolean shutdown, Runnable action) {
        AbstractButton button;
        MenuItem item;

        if (shutdown) {
            button = new JButton(name);
            item = new MenuItem(name);
            item.addActionListener(e -> {
                if (item.isEnabled()) {
                    action.run();
                }
            });
        }
        else {
            button = new JToggleButton(name);
            CheckboxMenuItem checkboxMenuItem = new CheckboxMenuItem(name);
            checkboxMenuItem.addItemListener(e -> {
                if (checkboxMenuItem.isEnabled())
                    action.run();
            });
            item = checkboxMenuItem;
        }

        button.setToolTipText(toolTipText);

        MouseAdapter mouseAdapter = new MouseAdapter() {
            boolean pressedOrEntered = false;
            @Override
            public void mouseReleased(MouseEvent e) {
                if (button.isEnabled() && pressedOrEntered)
                    action.run();
            }

            @Override
            public void mouseEntered(MouseEvent e) {
                pressedOrEntered = true;
                statusBar.setText(toolTipText);
            }

            @Override
            public void mouseExited(MouseEvent e) {
                statusBar.setText("");
                pressedOrEntered = false;
            }
        };

        button.addMouseListener(mouseAdapter);
        toolBar.add(button);

        if (!menuMap.containsKey(menuName)) {
            Menu menu = new Menu(menuName);
            menuMap.put(menuName, menu);
            menuBar.add(menu);
        }
        menuMap.get(menuName).add(item);
        menuItemMap.put(name, item);
        buttonMap.put(name, button);
    }

    public void close() {
        if (cellsPanel.isStateChanged()) {
            int confirmation = JOptionPane.showConfirmDialog(null, "Save changes?", "Exit", JOptionPane.YES_NO_OPTION);

            if (confirmation == JOptionPane.OK_OPTION) {
                JFileChooser fileChooser = new JFileChooser(System.getProperty("user.dir") + "\\Data\\");
                fileChooser.setDialogTitle("Save state");
                int f = fileChooser.showSaveDialog(GUI.this);
                if (f == JFileChooser.APPROVE_OPTION) {
                    File file = fileChooser.getSelectedFile();
                    if (model != null)
                        model.saveParametersInFile(file.getAbsolutePath(), cellsPanel.getCellParameters());
                }
            }
        }
        System.exit(0);
    }

    public void updateCellState(Field field) {
        this.model.setField(field);
        cellsPanel.updateCellState(field);

        EventQueue.invokeLater(() -> scrollPane.setViewportView(cellsPanel));
    }

    public void updateCellParameters(CellParameters cellParameters) {
        cellsPanel.setCellParameters(cellParameters);
    }
}
