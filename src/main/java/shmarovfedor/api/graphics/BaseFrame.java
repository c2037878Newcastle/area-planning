package shmarovfedor.api.graphics;

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.Timer;

import shmarovfedor.api.problem.Problem;
import shmarovfedor.api.background.BackgroundWorker;

import static java.lang.System.currentTimeMillis;

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
                rightPanel.getAddButton().setEnabled(true);
            }
            case INITIALIZATION -> {
                startButton.setEnabled(false);
                skipButton.setEnabled(false);
                terminateButton.setEnabled(true);
                settingsButton.setEnabled(false);
                rightPanel.getAddButton().setEnabled(false);
            }
            case IN_PROGRESS -> {
                startButton.setEnabled(false);
                skipButton.setEnabled(problem.worker().isBinarySearch());
                terminateButton.setEnabled(true);
                settingsButton.setEnabled(false);
                rightPanel.getAddButton().setEnabled(false);
            }
        }
    }
}
