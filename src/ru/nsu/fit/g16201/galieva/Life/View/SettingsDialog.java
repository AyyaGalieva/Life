package ru.nsu.fit.g16201.galieva.Life.View;

import ru.nsu.fit.g16201.galieva.Life.Model.CellParameters;
import ru.nsu.fit.g16201.galieva.Life.Model.Field;
import ru.nsu.fit.g16201.galieva.Life.Model.GameParameters;
import ru.nsu.fit.g16201.galieva.Life.View.Listeners.ChangeListener;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class SettingsDialog extends JDialog {
    private JPanel panel;
    private JTextField width, height, lineWidth, size;
    private JRadioButton xorButton, replaceButton;
    private JTextField lb, le, bb, be, fi, si;

    private static final int MIN_SIZE = 3;
    private static final int MAX_SIZE = 20;
    private static final int MIN_LINE_WIDTH = 1;
    private static final int MAX_LINE_WIDTH = 20;
    private static final int MAX_FIELD_SIZE = 50;
    private static final int MIN_FIELD_SIZE = 2;

    public SettingsDialog(Field field, CellParameters cellParameters, GameParameters gameParameters, boolean isXOR, ChangeListener changeListener) {
        setSize(400, 500);
        setResizable(false);
        setTitle("Settings");

        panel = new JPanel();
        panel.setBorder(new EmptyBorder(5, 5, 5, 5));
        add(panel);
        panel.setLayout(new GridLayout(21, 2, 10, 2));

        addModButton(isXOR);
        panel.add(new JLabel(""));
        panel.add(new JLabel(""));
        panel.add(new JLabel(""));
        addGameParameters(gameParameters);
        panel.add(new JLabel(""));
        panel.add(new JLabel(""));
        addSizeSlider(cellParameters);
        addLineWidthSlider(cellParameters);
        panel.add(new JLabel(""));
        panel.add(new JLabel(""));
        addFieldSizeParameters(field);
        panel.add(new JLabel(""));
        panel.add(new JLabel(""));

        JButton ok = new JButton("OK");
        ok.addMouseListener(new MouseAdapter() {
            boolean pressedOrEntered = false;

            @Override
            public void mouseReleased(MouseEvent e) {
                if (checkParameters() && pressedOrEntered) {
                    changeListener.run(
                            new CellParameters(Integer.parseInt(size.getText()), Integer.parseInt(lineWidth.getText())),
                            new GameParameters(Double.parseDouble(lb.getText()), Double.parseDouble(le.getText()), Double.parseDouble(bb.getText()),
                                    Double.parseDouble(be.getText()), Double.parseDouble(fi.getText()), Double.parseDouble(si.getText())),
                            Integer.parseInt(width.getText()),
                            Integer.parseInt(height.getText()),
                            xorButton.isSelected()
                    );
                    SettingsDialog.this.dispose();
                }
            }

            @Override
            public void mouseEntered(MouseEvent e) {
                pressedOrEntered = true;
            }

            @Override
            public void mouseExited(MouseEvent e) {
                pressedOrEntered = false;
            }
        });

        panel.add(ok);

        JButton cancel = new JButton("Cancel");
        cancel.addMouseListener(new MouseAdapter() {
            boolean pressedOrEntered = false;

            @Override
            public void mouseReleased(MouseEvent e) {
                if (pressedOrEntered)
                    SettingsDialog.this.dispose();
            }

            @Override
            public void mouseEntered(MouseEvent e) {
                pressedOrEntered = true;
            }

            @Override
            public void mouseExited(MouseEvent e) {
                pressedOrEntered = false;
            }
        });

        panel.add(cancel);
    }

    private void addFieldSizeParameters(Field field) {
        panel.add(new JLabel("Field size:"));
        panel.add(new JLabel(""));
        panel.add(new JLabel("Width:"));
        width = new JTextField();
        width.setText(Integer.toString(field.getWidth()));
        panel.add(width);
        panel.add(new JLabel("Height:"));
        height = new JTextField();
        height.setText(Integer.toString(field.getHeight()));
        panel.add(height);
    }

    private void addSizeSlider(CellParameters cellParameters) {
        panel.add(new JLabel("Cell size:"));
        panel.add(new JLabel(""));
        size = new JTextField();
        size.setText(Integer.toString(cellParameters.getSize()));
        panel.add(size);

        addSlider(panel, size, cellParameters.getSize(), MIN_SIZE, MAX_SIZE);
    }

    private void addLineWidthSlider(CellParameters cellParameters) {
        panel.add(new JLabel("Line Width:"));
        panel.add(new JLabel(""));
        lineWidth = new JTextField();
        lineWidth.setText(Integer.toString(cellParameters.getLineWidth()));
        panel.add(lineWidth);

        addSlider(panel, lineWidth, cellParameters.getLineWidth(), MIN_LINE_WIDTH, MAX_LINE_WIDTH);
    }

    private void addSlider(JPanel panel, JTextField textField, int paramValue, int minValue, int maxValue) {
        JSlider slider = new JSlider();
        slider.setMinimum(minValue);
        slider.setMaximum(maxValue);
        slider.setValue(paramValue);
        slider.addChangeListener(e -> {
            int value = ((JSlider)e.getSource()).getValue();
            if (textField.getText().equals(Integer.toString(value)))
                return;

            EventQueue.invokeLater(() -> textField.setText(Integer.toString(value)));
        });

        textField.addCaretListener(e -> {
            String value = textField.getText();
            if (value.isEmpty())
                return;
            if (value.equals(Integer.toString(slider.getValue())))
                return;
            slider.setValue(Integer.parseInt(value));
        });

        panel.add(slider);
    }

    private void addModButton(boolean isXOR) {
        xorButton = new JRadioButton("XOR");
        replaceButton = new JRadioButton("Replace");
        xorButton.addActionListener(e -> {
            xorButton.setSelected(true);
            replaceButton.setSelected(false);
        });
        replaceButton.addActionListener(e -> {
            xorButton.setSelected(false);
            replaceButton.setSelected(true);
        });
        panel.add(xorButton);
        panel.add(new JLabel(""));
        panel.add(replaceButton);
        if (isXOR)
            xorButton.setSelected(true);
        else replaceButton.setSelected(true);
    }

    private void addGameParameters(GameParameters gameParameters) {
        panel.add(new JLabel("Game settings:"));
        panel.add(new JLabel(""));
        panel.add(new JLabel("Life begin:"));
        lb = new JTextField();
        lb.setText(Double.toString(gameParameters.getLIVE_BEGIN()));
        panel.add(lb);
        panel.add(new JLabel("Life end:"));
        le = new JTextField();
        le.setText(Double.toString(gameParameters.getLIVE_END()));
        panel.add(le);
        panel.add(new JLabel("Birth begin:"));
        bb = new JTextField();
        bb.setText(Double.toString(gameParameters.getBIRTH_BEGIN()));
        panel.add(bb);
        panel.add(new JLabel("Birth end:"));
        be = new JTextField();
        be.setText(Double.toString(gameParameters.getBIRTH_END()));
        panel.add(be);
        panel.add(new JLabel("First impact:"));
        fi = new JTextField();
        fi.setText(Double.toString(gameParameters.getFST_IMPACT()));
        panel.add(fi);
        panel.add(new JLabel("Second impact:"));
        si = new JTextField();
        si.setText(Double.toString(gameParameters.getSND_IMPACT()));
        panel.add(si);
    }

    private boolean checkParameters() {
        try {
            if (width.getText().isEmpty()) {
                JOptionPane.showMessageDialog(SettingsDialog.this, "Field width is empty", "error", JOptionPane.WARNING_MESSAGE);
                return false;
            }
            if (height.getText().isEmpty()) {
                JOptionPane.showMessageDialog(SettingsDialog.this, "Field height is empty", "error", JOptionPane.WARNING_MESSAGE);
                return false;
            }
            if (size.getText().isEmpty()) {
                JOptionPane.showMessageDialog(SettingsDialog.this, "Cell size is empty", "error", JOptionPane.WARNING_MESSAGE);
                return false;
            }
            if (lineWidth.getText().isEmpty()) {
                JOptionPane.showMessageDialog(SettingsDialog.this, "Line width is empty", "error", JOptionPane.WARNING_MESSAGE);
                return false;
            }
            if (Integer.parseInt(width.getText()) > MAX_FIELD_SIZE || Integer.parseInt(width.getText()) < MIN_FIELD_SIZE) {
                JOptionPane.showMessageDialog(SettingsDialog.this, "Plese enter width >= " + MIN_FIELD_SIZE + " and <= " + MAX_FIELD_SIZE, "error", JOptionPane.WARNING_MESSAGE);
                return false;
            }
            if (Integer.parseInt(height.getText()) > MAX_FIELD_SIZE || Integer.parseInt(height.getText()) < MIN_FIELD_SIZE) {
                JOptionPane.showMessageDialog(SettingsDialog.this, "Please enter height >= " + MIN_FIELD_SIZE + " and <= " + MAX_FIELD_SIZE, "error", JOptionPane.WARNING_MESSAGE);
                return false;
            }
            if (Integer.parseInt(lineWidth.getText()) > MAX_LINE_WIDTH || Integer.parseInt(lineWidth.getText()) < MIN_LINE_WIDTH) {
                JOptionPane.showMessageDialog(SettingsDialog.this, "Please enter line width >= " + MIN_LINE_WIDTH + " and <= " + MAX_LINE_WIDTH, "error", JOptionPane.WARNING_MESSAGE);
                return false;
            }
            if (Integer.parseInt(size.getText()) > MAX_SIZE || Integer.parseInt(size.getText()) < MIN_SIZE) {
                JOptionPane.showMessageDialog(SettingsDialog.this, "Enter cell size >= " + MIN_SIZE + " and <= " + MAX_SIZE, "error", JOptionPane.WARNING_MESSAGE);
                return false;
            }
            if (lb.getText().isEmpty()) {
                JOptionPane.showMessageDialog(SettingsDialog.this, "Life begin is empty", "error", JOptionPane.WARNING_MESSAGE);
                return false;
            }
            if (le.getText().isEmpty()) {
                JOptionPane.showMessageDialog(SettingsDialog.this, "Life end is empty", "error", JOptionPane.WARNING_MESSAGE);
                return false;
            }
            if (bb.getText().isEmpty()) {
                JOptionPane.showMessageDialog(SettingsDialog.this, "Birth begin is empty", "error", JOptionPane.WARNING_MESSAGE);
                return false;
            }
            if (be.getText().isEmpty()) {
                JOptionPane.showMessageDialog(SettingsDialog.this, "Birth end is empty", "error", JOptionPane.WARNING_MESSAGE);
                return false;
            }
            if (fi.getText().isEmpty()) {
                JOptionPane.showMessageDialog(SettingsDialog.this, "First impact is empty", "error", JOptionPane.WARNING_MESSAGE);
                return false;
            }
            if (si.getText().isEmpty()) {
                JOptionPane.showMessageDialog(SettingsDialog.this, "Second impact is empty", "error", JOptionPane.WARNING_MESSAGE);
                return false;
            }

        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(SettingsDialog.this, e.getMessage(), "error", JOptionPane.WARNING_MESSAGE);
        }

        return true;
    }
}
