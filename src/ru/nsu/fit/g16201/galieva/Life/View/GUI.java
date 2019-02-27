package ru.nsu.fit.g16201.galieva.Life.View;

import ru.nsu.fit.g16201.galieva.Life.Model.CellParameters;
import ru.nsu.fit.g16201.galieva.Life.Model.Field;
import ru.nsu.fit.g16201.galieva.Life.Model.Model;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.IOException;
import java.util.Map;
import java.util.TreeMap;

public class GUI extends JFrame {
    private FieldPanel fieldPanel;
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
        fieldPanel = new FieldPanel(model.getField(), p -> model.clickCell(p.x, p.y));
        setTitle("Life");
        setSize(1000, 800);

        setLocationByPlatform(true);

        addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent event) {
                close();
            }
        });

        menuBar = new MenuBar();
        toolBar = new JToolBar();
        this.setMenuBar(menuBar);

        statusBar = new JLabel();
        statusBar.setPreferredSize(new Dimension(150, 15));
        statusBar.setBackground(Color.white);

        scrollPane = new JScrollPane(fieldPanel);
        add(scrollPane, BorderLayout.CENTER);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);

        addButton("Save", "File", "Save current state", true, "/resources/save.png", () -> {
            JFileChooser fileChooser = new JFileChooser(System.getProperty("user.dir") + "/test/");
            fileChooser.setDialogTitle("Save state");
            int f = fileChooser.showSaveDialog(GUI.this);
            if (f == JFileChooser.APPROVE_OPTION) {
                File file = fileChooser.getSelectedFile();
                model.saveParametersInFile(file.getAbsolutePath(), fieldPanel.getCellParameters());
                fieldPanel.setStateChanged(false);
            }
        });

        addButton("Load", "File", "Load state", true, "/resources/load.png", () -> {
            JFileChooser fileChooser = new JFileChooser(System.getProperty("user.dir") + "/test/");
            fileChooser.setDialogTitle("Load state");
            int f = fileChooser.showOpenDialog(GUI.this);
            if (f == JFileChooser.APPROVE_OPTION) {
                File file = fileChooser.getSelectedFile();
                model.loadParametersFromFile(file.getAbsolutePath());
            }
        });

        toolBar.addSeparator();

        addButton("Replace", "Edit", "Set states of clicked/dragged cells to alive", false, "", () -> {
            buttonMap.get("XOR").setSelected(false);
            ((CheckboxMenuItem)menuItemMap.get("XOR")).setState(false);
            buttonMap.get("Replace").setSelected(true);
            ((CheckboxMenuItem)menuItemMap.get("Replace")).setState(true);

            model.turnOnReplaceMode();
        });

        buttonMap.get("Replace").doClick();
        ((CheckboxMenuItem)menuItemMap.get("Replace")).setState(true);

        addButton("XOR", "Edit", "Invert states of clicked/dragged cells", false, "", () -> {
            buttonMap.get("Replace").setSelected(false);
            ((CheckboxMenuItem)menuItemMap.get("Replace")).setState(false);
            buttonMap.get("XOR").setSelected(true);
            ((CheckboxMenuItem)menuItemMap.get("XOR")).setState(true);

            model.turnOnXORMode();
        });

        toolBar.addSeparator();

        addButton("Clear", "Edit", "Clear field", true, "/resources/clear.png", () -> {
            model.clearField();
        });

        addButton("Impact", "View", "Show impacts of cells", false, "", new Runnable() {
            boolean pressed = false;

            @Override
            public void run() {
                pressed = !pressed;
                buttonMap.get("Impact").setSelected(pressed);
                ((CheckboxMenuItem)menuItemMap.get("Impact")).setState(pressed);

                fieldPanel.showImpacts(pressed);
                fieldPanel.prepareImage();
                fieldPanel.repaint();
            }
        });

        addButton("Step", "Edit", "Make one step of Life Cycle", true, "/resources/step.png", ()-> {
            model.makeStep();
        });

        addButton("Run", "Edit", "Run Life Cycle", false, "/resources/run.png", new Runnable() {
            boolean pressed = false;

            @Override
            public void run() {
                pressed = !pressed;

                buttonMap.get("Save").setEnabled(!pressed);
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

        addButton("Settings", "View", "Show game settings", true, "/resources/settings.jpg", () -> {
            SettingsDialog settingsDialog = new SettingsDialog(model.getField(), fieldPanel.getCellParameters(), model.getParameters(), buttonMap.get("XOR").isSelected(), (cellParams, gameParams, width, height, isXOR) -> {
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

                fieldPanel.prepareImage();
                fieldPanel.repaint();
            });
            settingsDialog.setLocationRelativeTo(this);
            settingsDialog.setVisible(true);
        });

        addButton("Info", "Help", "Show author's info", true, "/resources/info.jpg", () ->
            JOptionPane.showMessageDialog(null, "Life v.1.0\n" + "Author:\t Ayya Galieva, gr. 16201",
                "Author info", JOptionPane.INFORMATION_MESSAGE));
        add(toolBar, BorderLayout.NORTH);
        add(statusBar, BorderLayout.SOUTH);
    }

    private void addButton(String name, String menuName, String toolTipText, boolean shutdown, String imagePath, Runnable action) {
        AbstractButton button;
        MenuItem item;

        Image toolImage = null;
        try {
            toolImage = ImageIO.read(getClass().getResource(imagePath));
        } catch (IOException e) {
            System.err.println(e.getMessage());
        }

        if (shutdown) {
            if (toolImage != null) {
                button = new JButton();
                button.setIcon(new ImageIcon(toolImage));
            }
            else {
                button = new JButton(name);
            }
            item = new MenuItem(name);
            item.addActionListener(e -> {
                if (item.isEnabled()) {
                    action.run();
                }
            });
        }
        else {
            if (toolImage != null) {
                button = new JToggleButton();
                button.setIcon(new ImageIcon(toolImage));
            }
            else {
                button = new JToggleButton(name);
            }
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

    private void close() {
        if (fieldPanel.isStateChanged()) {
            int confirmation = JOptionPane.showConfirmDialog(null, "Save changes?", "Exit", JOptionPane.YES_NO_OPTION);

            if (confirmation == JOptionPane.OK_OPTION) {
                JFileChooser fileChooser = new JFileChooser(System.getProperty("user.dir") + "/test/");
                fileChooser.setDialogTitle("Save state");
                int f = fileChooser.showSaveDialog(GUI.this);
                if (f == JFileChooser.APPROVE_OPTION) {
                    File file = fileChooser.getSelectedFile();
                    if (model != null)
                        model.saveParametersInFile(file.getAbsolutePath(), fieldPanel.getCellParameters());
                }
            }
        }
        System.exit(0);
    }

    public void updateCellState(Field field) {
        fieldPanel.updateField(field);

        EventQueue.invokeLater(() -> scrollPane.setViewportView(fieldPanel));
    }

    public void updateCellParameters(CellParameters cellParameters) {
        fieldPanel.setCellParameters(cellParameters);
    }

    public void showFileIncorrect() {
        JOptionPane.showMessageDialog(this, "File is incorrect", "error", JOptionPane.WARNING_MESSAGE);
    }
}
