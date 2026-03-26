import javax.swing.*;
import java.util.stream.Stream;

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
    Stream<String> dataStream;

    public DataStreamGUI() {
        frame = new JFrame("Data Stream");
        panel = new JPanel();
        input = new JTextField();
        start = new JButton("Start");
        stop = new JButton("Stop");
        reset = new JButton("Reset");
        clear = new JButton("Clear");
        inputArea = new JTextArea();
        outputArea = new JTextArea();
        frame.add(panel);
        panel.add(input);
        panel.add(start);
        panel.add(stop);
        panel.add(reset);
        panel.add(clear);
        frame.add(panel);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(400,400);
        frame.setVisible(true);
    }

    
}
