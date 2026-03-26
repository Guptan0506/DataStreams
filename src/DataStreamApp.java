import javax.swing.*;

public class DataStreamApp {
    public static void main(String[] args) {

        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                new DataStreamApp();
            }
        });
    }
}
