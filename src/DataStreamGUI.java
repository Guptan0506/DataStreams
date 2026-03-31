import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.io.IOException;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

public class DataStreamGUI {
    JFrame frame;
    JPanel panel;
    JTextField input;
    JButton start;
    JButton stop;
    JButton reset;
    JButton clear;
    JTextArea inputArea;
    JTextArea outputArea;
    JTextArea searchArea;
    JButton select;
    JButton search;
    List<String> dataItems;

    private void runSafely(String actionName, Runnable action) {
        try {
            action.run();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(
                    frame,
                    actionName + " failed: " + ex.getMessage(),
                    "Unexpected Error",
                    JOptionPane.ERROR_MESSAGE
            );
        }
    }

    public DataStreamGUI() {
        frame = new JFrame("Data Stream");
        panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(new EmptyBorder(12, 12, 12, 12));
        input = new JTextField();
        input.setColumns(30);
        start = new JButton("Start");
        stop = new JButton("Stop");
        reset = new JButton("Reset");
        clear = new JButton("Clear");
        inputArea = new JTextArea(10, 40);
        outputArea = new JTextArea(10, 40);
        searchArea = new JTextArea(2, 20);
        inputArea.setEditable(false);
        inputArea.setLineWrap(true);
        inputArea.setWrapStyleWord(true);
        outputArea.setLineWrap(true);
        outputArea.setWrapStyleWord(true);
        outputArea.setEditable(false);
        select = new JButton("Select File");
        search = new JButton("Search");
        dataItems = new ArrayList<>();

        JPanel controlsPanel = new JPanel(new GridLayout(3, 1, 0, 8));

        JPanel filePanel = new JPanel(new BorderLayout(8, 0));
        filePanel.add(new JLabel("Selected File:"), BorderLayout.WEST);
        filePanel.add(input, BorderLayout.CENTER);
        filePanel.add(select, BorderLayout.EAST);
        controlsPanel.add(filePanel);

        JPanel searchPanel = new JPanel(new BorderLayout(8, 0));
        searchPanel.add(new JLabel("Search Query:"), BorderLayout.WEST);
        searchPanel.add(new JScrollPane(searchArea), BorderLayout.CENTER);
        searchPanel.add(search, BorderLayout.EAST);
        controlsPanel.add(searchPanel);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        Dimension buttonSize = new Dimension(92, 30);
        start.setPreferredSize(buttonSize);
        stop.setPreferredSize(buttonSize);
        reset.setPreferredSize(buttonSize);
        clear.setPreferredSize(buttonSize);
        buttonPanel.add(start);
        buttonPanel.add(stop);
        buttonPanel.add(reset);
        buttonPanel.add(clear);
        controlsPanel.add(buttonPanel);

        JScrollPane inputScroll = new JScrollPane(inputArea);
        inputScroll.setBorder(BorderFactory.createTitledBorder("File Content"));
        JScrollPane outputScroll = new JScrollPane(outputArea);
        outputScroll.setBorder(BorderFactory.createTitledBorder("Search Results"));

        JSplitPane splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT, inputScroll, outputScroll);
        splitPane.setResizeWeight(0.55);
        splitPane.setContinuousLayout(true);

        panel.add(controlsPanel, BorderLayout.NORTH);
        panel.add(splitPane, BorderLayout.CENTER);

        frame.setContentPane(panel);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setMinimumSize(new Dimension(720, 560));
        frame.pack();
        frame.setLocationRelativeTo(null);

        start.addActionListener(e -> runSafely("Start", () -> {
            String source = inputArea.getText().isBlank() ? input.getText() : inputArea.getText();
            if (source == null || source.isBlank()) {
                outputArea.setText("Enter comma-separated values or load a file first.\n");
                return;
            }

            dataItems = java.util.regex.Pattern.compile("[,\\n\\r]+")
                    .splitAsStream(source)
                    .map(String::trim)
                    .filter(token -> !token.isEmpty())
                    .collect(Collectors.toCollection(ArrayList::new));

            outputArea.setText(dataItems.stream().collect(Collectors.joining("\n")));
            if (!dataItems.isEmpty()) {
                outputArea.append("\n");
            }
        }));
        stop.addActionListener(e -> runSafely("Stop", () -> {
            outputArea.append("Stream stopped.\n");
        }));
        reset.addActionListener(e -> runSafely("Reset", () -> {
            outputArea.setText(dataItems.stream().collect(Collectors.joining("\n")));
            if (!dataItems.isEmpty()) {
                outputArea.append("\n");
            }
        }));
        clear.addActionListener(e -> runSafely("Clear", () -> {
            input.setText("");
            inputArea.setText("");
            outputArea.setText("");
            searchArea.setText("");
            dataItems.clear();
        }));
        select.addActionListener(e -> runSafely("Select File", () -> {
            JFileChooser fileChooser = new JFileChooser();
            int result = fileChooser.showOpenDialog(frame);
            if (result == JFileChooser.APPROVE_OPTION) {
                String filePath = fileChooser.getSelectedFile().getAbsolutePath();
                input.setText(filePath);
                try {
                    String content = Files.readString(Path.of(filePath));
                    inputArea.setText(content);
                } catch (IOException ex) {
                    JOptionPane.showMessageDialog(frame, "Could not read file: " + ex.getMessage(), "Read Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        }));

        search.addActionListener(e -> runSafely("Search", () -> {
            String query = searchArea.getText().trim();
            if (query.isEmpty()) {
                outputArea.append("Enter a search term in the search area.\n");
                return;
            }

            String fileContent = inputArea.getText();
            if (fileContent.isBlank()) {
                outputArea.setText("Load a file first, then search.\n");
                return;
            }

            outputArea.setText("");
            String lowerQuery = query.toLowerCase(Locale.ROOT);
            List<String> matches = fileContent.lines()
                    .filter(line -> line.toLowerCase(Locale.ROOT).contains(lowerQuery))
                    .collect(Collectors.toList());

            if (matches.isEmpty()) {
                outputArea.append("No matches found for: " + query + "\n");
                return;
            }

            outputArea.setText(matches.stream().collect(Collectors.joining("\n")));
            outputArea.append("\n");
        }));

        frame.setVisible(true);
    }
}
