package shmarovfedor.api.graphics;

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.io.File;
import java.io.FileFilter;
import java.nio.file.Files;
import java.nio.file.Path;

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;

import shmarovfedor.api.problem.Problem;
import shmarovfedor.api.background.BackgroundWorker;
import uk.co.rhilton.api.persist.SettingStorage;

import static java.lang.System.currentTimeMillis;
import static uk.co.rhilton.api.persist.DefaultSettings.BINARY_SEARCH;

public abstract class BaseFrame extends JFrame {

    protected final Problem problem;

    protected MainPanel mainPanel;
    protected RightPanel rightPanel;
    protected JPanel buttonPanel;
    protected JButton startButton;
    protected JButton terminateButton;
    protected JButton saveButton;
    protected JButton settingsButton;
    protected JButton skipButton;

    protected final Timer updateTimer = new Timer(20, e -> setButtons());

    public BaseFrame(Problem problem) {
        super();
        this.problem = problem;

        setSize(new Dimension());
        setResizable(false);
        setTitle("Packing Manager for " + problem);

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        buttonPanel = new JPanel();
        startButton = new JButton("Start");
        skipButton = new JButton("Skip");
        skipButton.setEnabled(false);
        terminateButton = new JButton("Terminate");
        terminateButton.setEnabled(false);
        saveButton = new JButton("Save As");
        settingsButton = new JButton("Settings");

        mainPanel = new MainPanel();
        rightPanel = new RightPanel(problem,300, 500);

        saveButton.addActionListener(e ->
                mainPanel.saveToFile(String.valueOf(currentTimeMillis()))
        );

        updateTimer.start();

        startButton.addActionListener(e -> problem.start());
        skipButton.addActionListener(e -> problem.skip());
        terminateButton.addActionListener(e -> problem.terminate());
        settingsButton.addActionListener(e -> problem.openSettings());

        buttonPanel.add(startButton);
        buttonPanel.add(skipButton);
        buttonPanel.add(terminateButton);
        buttonPanel.add(saveButton);
        buttonPanel.add(settingsButton);


        setLayout(new GridBagLayout());
        GridBagConstraints constraints = new GridBagConstraints();

        constraints.fill = GridBagConstraints.VERTICAL;
        constraints.gridx = 0;
        constraints.gridy = 0;
        getContentPane().add(mainPanel, constraints);

        constraints.fill = GridBagConstraints.VERTICAL;
        constraints.gridx = 1;
        constraints.gridy = 0;
        getContentPane().add(rightPanel, constraints);

        constraints.fill = GridBagConstraints.HORIZONTAL;
        constraints.gridx = 0;
        constraints.gridy = 1;
        getContentPane().add(buttonPanel, constraints);

        var menuBar = new JMenuBar();

        var fileMenu = new JMenu("File");
        var open = new JMenuItem("Import Settings");
        open.addActionListener(e -> {
            var dialog = new JFileChooser(((File) null));
            dialog.setFileFilter(new FileNameExtensionFilter("JSON Settings Files", "json"));
            dialog.setDialogTitle("Select Settings File");
            dialog.setFileSelectionMode(JFileChooser.FILES_ONLY);
            dialog.setMultiSelectionEnabled(false);
            dialog.showOpenDialog(this);
            if (dialog.getSelectedFile() == null) return;
            var file = Path.of(dialog.getSelectedFile().toURI());
            if (!Files.isRegularFile(file)) return; // TODO error
            var config = SettingStorage.fromFile(file);
            if (config.isEmpty()) return; // TODO error
            problem.loadConfig(config.get());
        });
        fileMenu.add(open);

        var save = new JMenuItem("Export Settings");
        save.addActionListener(e -> {
            var dialog = new JFileChooser((File) null);
            dialog.setFileFilter(new FileNameExtensionFilter("JSON Settings Files", "json"));
            dialog.setDialogTitle("Select Settings File");
            dialog.setFileSelectionMode(JFileChooser.FILES_ONLY);
            dialog.setMultiSelectionEnabled(false);
            dialog.showSaveDialog(this);
            if (dialog.getSelectedFile() == null) return;
            var file = Path.of(dialog.getSelectedFile().toURI());
            if (Files.isDirectory(file)) return; // TODO error
            problem.saveConfig(file); // TODO success/error msg
        });
        fileMenu.add(save);

        menuBar.add(fileMenu);
        setJMenuBar(menuBar);

        createGUI();


        pack();
        setLocationRelativeTo(null); // fix for centre screen
        requestFocus();
    }

    public abstract void createGUI();

    public void setButtons() {
        switch (problem.optimizer().getStatus()) {
            case WAITING, TERMINATED -> {
                startButton.setEnabled(true);
                skipButton.setEnabled(false);
                terminateButton.setEnabled(false);
                settingsButton.setEnabled(true);
                if (problem.allowUserBuildings()) rightPanel.getAddButton().setEnabled(true);
            }
            case INITIALIZATION -> {
                startButton.setEnabled(false);
                skipButton.setEnabled(false);
                terminateButton.setEnabled(true);
                settingsButton.setEnabled(false);
                if (problem.allowUserBuildings()) rightPanel.getAddButton().setEnabled(false);
            }
            case IN_PROGRESS -> {
                startButton.setEnabled(false);
                skipButton.setEnabled(problem.config().valueOf(BINARY_SEARCH));
                terminateButton.setEnabled(true);
                settingsButton.setEnabled(false);
                if (problem.allowUserBuildings()) rightPanel.getAddButton().setEnabled(false);
            }
        }
    }
}
