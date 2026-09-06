package jelena.etfbl;

import jelena.etfbl.database.MyConnection;
import jelena.etfbl.database.Timer;
import jelena.etfbl.service.FillerCoordinator;

import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.io.*;
import java.nio.file.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class MainWindow extends JFrame {
    private JComboBox<MyConnection.DatabaseType> dbTypeCombo;
    private JTextField hostField, portField, databaseField, usernameField;
    private JPasswordField passwordField;
    private JSpinner rowsSpinner, threadsSpinner, batchSpinner;

    private JTextArea logArea;

    private JButton startBtn, cancelBtn, saveLogBtn, clearBtn;
    private JProgressBar progressBar;
    private JLabel statusLabel;

    private FillerCoordinator coordinator;
    private Thread workerThread;

    public MainWindow() {
        super("Database Test Data Filler v2.0");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1050, 780);
        setMinimumSize(new Dimension(820, 620));
        setLocationRelativeTo(null);
        buildUI();
        setVisible(true);
    }


    private void buildUI() {
        setLayout(new BorderLayout(8, 8));
        JPanel root = new JPanel(new BorderLayout(8, 8));
        root.setBorder(new EmptyBorder(12, 12, 12, 12));
        add(root, BorderLayout.CENTER);

        root.add(buildConfigPanel(), BorderLayout.NORTH);
        root.add(buildLogPanel(),    BorderLayout.CENTER);
        root.add(buildBottomPanel(), BorderLayout.SOUTH);
    }

    private JPanel buildConfigPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBorder(createTitledBorder("Connection & Configuration"));
        GridBagConstraints g = new GridBagConstraints();
        g.insets = new Insets(4, 6, 4, 6);
        g.fill   = GridBagConstraints.HORIZONTAL;
        int row  = 0;

        // --- Tip baze ---
        addLabel(panel, g, row, 0, "Database Type:");
        dbTypeCombo = new JComboBox<>(MyConnection.DatabaseType.values());
        dbTypeCombo.addActionListener(e -> onDbTypeChanged());
        addWidget(panel, g, row, 1, dbTypeCombo);

        // --- Host ---
        addLabel(panel, g, row, 2, "Host:");
        hostField = new JTextField("localhost", 18);
        addWidget(panel, g, row, 3, hostField);
        row++;

        // --- Port ---
        addLabel(panel, g, row, 0, "Port:");
        portField = new JTextField("3306", 8);
        addWidget(panel, g, row, 1, portField);

        // --- Database ---
        addLabel(panel, g, row, 2, "Database Name:");
        databaseField = new JTextField("dbtest", 18);
        databaseField.setToolTipText("Naziv baze podataka / šeme");
        addWidget(panel, g, row, 3, databaseField);
        row++;

        // --- Username ---
        addLabel(panel, g, row, 0, "Username:");
        usernameField = new JTextField("root", 14);
        addWidget(panel, g, row, 1, usernameField);

        // --- Password ---
        addLabel(panel, g, row, 2, "Password:");
        passwordField = new JPasswordField(14);
        addWidget(panel, g, row, 3, passwordField);
        row++;

        // --- Rows per table ---
        addLabel(panel, g, row, 0, "Rows per table:");
        rowsSpinner = new JSpinner(new SpinnerNumberModel(10_000, 10_000, 10_000_000, 1_000));
        ((JSpinner.DefaultEditor) rowsSpinner.getEditor()).getTextField().setColumns(10);
        addWidget(panel, g, row, 1, rowsSpinner);

        // --- Parallel threads ---
        addLabel(panel, g, row, 2, "Parallel threads:");
        threadsSpinner = new JSpinner(new SpinnerNumberModel(4, 1, 32, 1));
        ((JSpinner.DefaultEditor) threadsSpinner.getEditor()).getTextField().setColumns(6);
        addWidget(panel, g, row, 3, threadsSpinner);
        row++;

        // --- Batch size ---
        addLabel(panel, g, row, 0, "Batch size:");
        batchSpinner = new JSpinner(new SpinnerNumberModel(500, 50, 5_000, 50));
        ((JSpinner.DefaultEditor) batchSpinner.getEditor()).getTextField().setColumns(8);
        addWidget(panel, g, row, 1, batchSpinner);

        return panel;
    }

    private JPanel buildLogPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(createTitledBorder("Execution Log"));

        logArea = new JTextArea();
        logArea.setEditable(false);
        logArea.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 12));
        logArea.setLineWrap(false);
        logArea.setBackground(new Color(28, 30, 38));
        logArea.setForeground(new Color(200, 220, 190));
        logArea.setCaretColor(Color.GREEN);

        JScrollPane scroll = new JScrollPane(logArea);
        scroll.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        panel.add(scroll, BorderLayout.CENTER);
        return panel;
    }

    private JPanel buildBottomPanel() {
        JPanel panel = new JPanel(new BorderLayout(6, 6));

        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));

        startBtn = new JButton("Start Filling");
        startBtn.setFont(startBtn.getFont().deriveFont(Font.BOLD, 13f));
        startBtn.setBackground(new Color(46, 125, 50));
        startBtn.setForeground(Color.WHITE);
        startBtn.setOpaque(true);
        startBtn.addActionListener(e -> onStart());

        cancelBtn = new JButton("Cancel");
        cancelBtn.setEnabled(false);
        cancelBtn.addActionListener(e -> onCancel());

        clearBtn = new JButton("Clear Log");
        clearBtn.addActionListener(e -> logArea.setText(""));

        saveLogBtn = new JButton("Save Log");
        saveLogBtn.addActionListener(e -> onSaveLog());

        btnPanel.add(startBtn);
        btnPanel.add(cancelBtn);
        btnPanel.add(clearBtn);
        btnPanel.add(saveLogBtn);

        JPanel progressPanel = new JPanel(new BorderLayout(4, 2));
        progressBar = new JProgressBar();
        progressBar.setIndeterminate(false);
        progressBar.setStringPainted(true);
        progressBar.setString("Ready");

        statusLabel = new JLabel("Configure connection parameters and click Start.");
        statusLabel.setFont(statusLabel.getFont().deriveFont(11f));

        progressPanel.add(progressBar, BorderLayout.CENTER);
        progressPanel.add(statusLabel, BorderLayout.SOUTH);

        panel.add(btnPanel,      BorderLayout.WEST);
        panel.add(progressPanel, BorderLayout.CENTER);
        return panel;
    }


    private void onDbTypeChanged() {
        MyConnection.DatabaseType selected =
                (MyConnection.DatabaseType) dbTypeCombo.getSelectedItem();

        if (selected != null) {
            portField.setText(String.valueOf(selected.getDefaultPort()));
        }
    }

    private void onStart() {
        MyConnection.DatabaseType selected =
                (MyConnection.DatabaseType) dbTypeCombo.getSelectedItem();

        if (selected == null) return;
        if (hostField.getText().isBlank()) {
            showError("Host je obavezan.");
            return;
        }
        if (databaseField.getText().isBlank()) {
            showError("Database Name je obavezan.");
            return;
        }

        MyConnection cfg = buildConfig();

        startBtn.setEnabled(false);
        cancelBtn.setEnabled(true);
        progressBar.setIndeterminate(true);
        progressBar.setString("Running...");
        statusLabel.setText("Connecting to " + cfg.getHost() + "...");
        logArea.setText("");

        coordinator = new FillerCoordinator(cfg, this::appendLog, this::onCompleted);
        workerThread = new Thread(coordinator::execute, "filler-main");
        workerThread.setDaemon(true);
        workerThread.start();
    }

    private void onCancel() {
        if (coordinator != null) coordinator.cancel();
        if (workerThread  != null) workerThread.interrupt();
        appendLog("\n[CANCELLED BY USER]");
        cancelBtn.setEnabled(false);
        progressBar.setIndeterminate(false);
        progressBar.setString("Cancelled");
        startBtn.setEnabled(true);
    }

    private void onCompleted(List<Timer> results) {
        SwingUtilities.invokeLater(() -> {
            startBtn.setEnabled(true);
            cancelBtn.setEnabled(false);
            progressBar.setIndeterminate(false);

            long success   = results.stream().filter(Timer::isSuccess).count();
            long totalRows = results.stream()
                    .filter(Timer::isSuccess)
                    .mapToLong(Timer::getRowsInserted).sum();

            progressBar.setString(String.format("Done: %d tables, %,d rows", success, totalRows));
            statusLabel.setText(String.format(
                    "Completed: %d/%d tables successful, %,d rows total",
                    success, results.size(), totalRows));
        });
    }


    private void appendLog(String message) {
        SwingUtilities.invokeLater(() -> {
            logArea.append(message + "\n");
            logArea.setCaretPosition(logArea.getDocument().getLength());
            String preview = message.length() > 90
                    ? message.substring(0, 90) + "..." : message;
            statusLabel.setText(preview);
        });
    }

    private void onSaveLog() {
        JFileChooser fc = new JFileChooser();
        fc.setSelectedFile(new File("result" +
                LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd-HHmmss")) + ".txt"));
        if (fc.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
            try {
                Files.writeString(fc.getSelectedFile().toPath(), logArea.getText());
                JOptionPane.showMessageDialog(this, "Log saved.", "Saved",
                        JOptionPane.INFORMATION_MESSAGE);
            } catch (IOException ex) {
                showError("Error saving log: " + ex.getMessage());
            }
        }
    }

    private MyConnection buildConfig() {
        MyConnection cfg = new MyConnection();
        cfg.setDbType((MyConnection.DatabaseType) dbTypeCombo.getSelectedItem());
        cfg.setHost(hostField.getText().trim());
        try {
            cfg.setPort(Integer.parseInt(portField.getText().trim()));
        } catch (NumberFormatException ignored) {}

        cfg.setDatabase(databaseField.getText().trim());
        cfg.setUsername(usernameField.getText().trim());
        cfg.setPassword(new String(passwordField.getPassword()));
        cfg.setRowsPerTable((int) rowsSpinner.getValue());
        cfg.setThreadPoolSize((int) threadsSpinner.getValue());
        cfg.setBatchSize((int) batchSpinner.getValue());
        return cfg;
    }


    private void showError(String msg) {
        JOptionPane.showMessageDialog(this, msg, "Validation Error",
                JOptionPane.WARNING_MESSAGE);
    }

    private static void addLabel(JPanel p, GridBagConstraints g, int row, int col, String text) {
        g.gridx = col; g.gridy = row; g.weightx = 0;
        p.add(new JLabel(text), g);
    }

    private static void addWidget(JPanel p, GridBagConstraints g, int row, int col, JComponent w) {
        g.gridx = col; g.gridy = row; g.weightx = 1;
        p.add(w, g);
    }

    private static TitledBorder createTitledBorder(String title) {
        TitledBorder b = BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(new Color(80, 100, 120), 1), title);
        b.setTitleFont(b.getTitleFont().deriveFont(Font.BOLD));
        return b;
    }
}