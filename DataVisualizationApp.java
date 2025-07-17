// DataVisualizationApp.java
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Main application class for the Data Visualization Tool using Swing.
 * Sets up the JFrame and integrates the ChartView.
 */
public class DataVisualizationApp extends JFrame {

    private ChartView chartView;
    private List<Double> data;
    private int totalDataPoints = 200; // Number of data points to generate

    public DataVisualizationApp() {
        setTitle("Simple Bar Chart Visualization (Swing)");
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null); // Center the window

        // Generate sample data
        generateSampleData(totalDataPoints);

        chartView = new ChartView(data);
        chartView.setPreferredSize(new Dimension(800, 400)); // Preferred size for the chart panel

        // Add chart to a JScrollPane for basic scrolling if content exceeds view
        // Declare scrollPane as final to be accessed from inner classes
        final JScrollPane scrollPane = new JScrollPane(chartView);
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_NEVER); // Only horizontal scroll
        scrollPane.getHorizontalScrollBar().setUnitIncrement(20); // Make scrolling smoother

        // Control panel for interactive options
        JPanel controlPanel = new JPanel();
        controlPanel.setLayout(new FlowLayout(FlowLayout.CENTER, 10, 10)); // Center alignment with spacing
        controlPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10)); // Padding

        JButton zoomInButton = new JButton("Zoom In (Show Less)");
        JButton zoomOutButton = new JButton("Zoom Out (Show More)");
        JButton resetZoomButton = new JButton("Reset Zoom");
        JButton regenerateDataButton = new JButton("Regenerate Data");

        // Styling buttons
        Font buttonFont = new Font("Arial", Font.BOLD, 12);
        Color buttonBg = new Color(70, 130, 180); // SteelBlue
        Color buttonFg = Color.WHITE;

        zoomInButton.setFont(buttonFont);
        zoomInButton.setBackground(buttonBg);
        zoomInButton.setForeground(buttonFg);
        zoomInButton.setFocusPainted(false); // Remove border around text when focused

        zoomOutButton.setFont(buttonFont);
        zoomOutButton.setBackground(buttonBg);
        zoomOutButton.setForeground(buttonFg);
        zoomOutButton.setFocusPainted(false);

        resetZoomButton.setFont(buttonFont);
        resetZoomButton.setBackground(buttonBg);
        resetZoomButton.setForeground(buttonFg);
        resetZoomButton.setFocusPainted(false);

        regenerateDataButton.setFont(buttonFont);
        regenerateDataButton.setBackground(new Color(60, 179, 113)); // MediumSeaGreen
        regenerateDataButton.setForeground(buttonFg);
        regenerateDataButton.setFocusPainted(false);

        // Action Listeners for controls
        zoomInButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                chartView.zoomIn();
                scrollPane.revalidate(); // Revalidate scroll pane after zoom
                scrollPane.repaint();
            }
        });

        zoomOutButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                chartView.zoomOut();
                scrollPane.revalidate();
                scrollPane.repaint();
            }
        });

        resetZoomButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                chartView.resetZoom();
                scrollPane.revalidate();
                scrollPane.repaint();
            }
        });

        regenerateDataButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                generateSampleData(totalDataPoints);
                chartView.setData(data); // Update data in chart view
                chartView.resetZoom(); // Reset zoom after new data
                scrollPane.revalidate();
                scrollPane.repaint();
            }
        });


        controlPanel.add(zoomInButton);
        controlPanel.add(zoomOutButton);
        controlPanel.add(resetZoomButton);
        controlPanel.add(regenerateDataButton);

        // Add components to the frame
        Container contentPane = getContentPane();
        contentPane.setLayout(new BorderLayout());
        contentPane.add(scrollPane, BorderLayout.CENTER);
        contentPane.add(controlPanel, BorderLayout.SOUTH);
    }

    /**
     * Generates a list of random double values for demonstration.
     * @param numPoints The number of data points to generate.
     */
    private void generateSampleData(int numPoints) {
        data = new ArrayList<Double>();
        Random random = new Random();
        for (int i = 0; i < numPoints; i++) {
            data.add(50 + random.nextDouble() * 150); // Values between 50 and 200
        }
    }

    public static void main(String[] args) {
        // Schedule a job for the event-dispatching thread:
        // creating and showing this application's GUI.
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                new DataVisualizationApp().setVisible(true);
            }
        });
    }
}
