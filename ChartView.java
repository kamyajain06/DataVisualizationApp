// ChartView.java
import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.Rectangle2D;
import java.util.List;

/**
 * JPanel subclass responsible for drawing the bar chart and handling tooltips.
 */
class ChartView extends JPanel {

    private List<Double> data;
    private double maxValue;
    private int barWidth = 15; // Width of each bar
    private int barSpacing = 5; // Spacing between bars
    private int currentZoomLevel = 1; // 1 = default, 2 = zoom in (half bars shown), etc.
    private int maxZoomLevel = 5; // Max zoom in level
    private int minZoomLevel = 1; // Min zoom out level (show all)

    public ChartView(List<Double> data) {
        this.data = data;
        calculateMaxValue();
        setupTooltip();
        setAutoscrolls(true); // Enable autoscrolling for horizontal bar
    }

    /**
     * Sets new data for the chart and recalculates max value.
     * @param newData The new list of data points.
     */
    public void setData(List<Double> newData) {
        this.data = newData;
        calculateMaxValue();
        resetZoom(); // Reset zoom when new data is set
        repaint(); // Redraw the chart
    }

    /**
     * Calculates the maximum value in the dataset for scaling the chart.
     */
    private void calculateMaxValue() {
        maxValue = 0;
        if (data != null && !data.isEmpty()) {
            for (Double value : data) {
                if (value != null && value > maxValue) {
                    maxValue = value;
                }
            }
        }
        if (maxValue == 0) maxValue = 1; // Avoid division by zero
    }

    /**
     * Implements basic zoom-in functionality by increasing bar width.
     */
    public void zoomIn() {
        if (currentZoomLevel < maxZoomLevel) {
            currentZoomLevel++;
            updateBarWidth();
            revalidate(); // Recalculate preferred size
            repaint();
        }
    }

    /**
     * Implements basic zoom-out functionality by decreasing bar width.
     */
    public void zoomOut() {
        if (currentZoomLevel > minZoomLevel) {
            currentZoomLevel--;
            updateBarWidth();
            revalidate();
            repaint();
        }
    }

    /**
     * Resets the zoom level to default.
     */
    public void resetZoom() {
        currentZoomLevel = 1;
        updateBarWidth();
        revalidate();
        repaint();
    }

    /**
     * Adjusts bar width based on current zoom level.
     */
    private void updateBarWidth() {
        // Adjust barWidth based on zoom level.
        // For simplicity, let's just scale the original barWidth.
        // A more sophisticated zoom would adjust the number of visible bars.
        barWidth = 15 + (currentZoomLevel - 1) * 5; // Example: 15, 20, 25, ...
        barSpacing = 5 + (currentZoomLevel - 1) * 2; // Adjust spacing too
    }

    @Override
    public Dimension getPreferredSize() {
        // Calculate the total width needed to display all bars at current zoom level
        int totalWidth = (data.size() * (barWidth + barSpacing)) + barSpacing;
        return new Dimension(Math.max(getParent().getWidth(), totalWidth), 400); // Ensure it's at least parent width
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g); // Call superclass method to clear background

        Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        int panelWidth = getWidth();
        int panelHeight = getHeight();
        int bottomMargin = 30; // Space for labels
        int leftMargin = 40; // Space for Y-axis labels
        int topMargin = 20;

        int chartHeight = panelHeight - bottomMargin - topMargin;
        int chartWidth = panelWidth - leftMargin - barSpacing; // Adjust for right padding

        // Draw Y-axis
        g2d.setColor(Color.BLACK);
        g2d.drawLine(leftMargin, topMargin, leftMargin, topMargin + chartHeight);

        // Draw X-axis
        g2d.drawLine(leftMargin, topMargin + chartHeight, panelWidth - barSpacing, topMargin + chartHeight);

        // Draw Y-axis labels and grid lines
        g2d.setColor(Color.GRAY);
        int numYLabels = 5;
        for (int i = 0; i <= numYLabels; i++) {
            double yValue = (maxValue / numYLabels) * i;
            int yPos = topMargin + chartHeight - (int) ((yValue / maxValue) * chartHeight);
            g2d.drawString(String.format("%.0f", yValue), leftMargin - 35, yPos + 5); // Y-axis label
            if (i > 0) { // Don't draw grid line at 0
                g2d.drawLine(leftMargin, yPos, panelWidth - barSpacing, yPos); // Horizontal grid line
            }
        }

        // Draw bars and X-axis labels
        int x = leftMargin + barSpacing;
        for (int i = 0; i < data.size(); i++) {
            Double value = data.get(i);
            if (value == null) continue;

            int barHeight = (int) ((value / maxValue) * chartHeight);
            int y = topMargin + chartHeight - barHeight;

            // Bar color
            g2d.setColor(new Color(60, 140, 220)); // A nice blue
            g2d.fillRect(x, y, barWidth, barHeight);

            // Bar border
            g2d.setColor(Color.DARK_GRAY);
            g2d.drawRect(x, y, barWidth, barHeight);

            // X-axis label (only for every 10th bar or if zoomed in significantly)
            if (i % 10 == 0 || barWidth > 20) { // Adjust frequency based on zoom/barWidth
                g2d.setColor(Color.BLACK);
                String label = "Item " + (i + 1);
                FontMetrics fm = g2d.getFontMetrics();
                int labelWidth = fm.stringWidth(label);
                g2d.drawString(label, x + (barWidth / 2) - (labelWidth / 2), topMargin + chartHeight + 20);
            }

            x += barWidth + barSpacing;
        }
    }

    /**
     * Sets up a MouseAdapter to listen for mouse movements and display tooltips.
     */
    private void setupTooltip() {
        setToolTipText(null); // Initialize with no tooltip

        addMouseMotionListener(new MouseAdapter() {
            @Override
            public void mouseMoved(MouseEvent e) {
                int mouseX = e.getX();
                int mouseY = e.getY();

                int leftMargin = 40;
                int topMargin = 20;
                int chartHeight = getHeight() - 30 - topMargin; // Adjust for margins

                // Determine which bar the mouse is over
                if (mouseX >= leftMargin && mouseY >= topMargin && mouseY <= topMargin + chartHeight) {
                    int relativeX = mouseX - leftMargin;
                    int barIndex = relativeX / (barWidth + barSpacing);

                    if (barIndex >= 0 && barIndex < data.size()) {
                        int barStartX = leftMargin + barIndex * (barWidth + barSpacing);
                        int barEndX = barStartX + barWidth;

                        if (mouseX >= barStartX && mouseX <= barEndX) {
                            Double value = data.get(barIndex);
                            if (value != null) {
                                setToolTipText("Item " + (barIndex + 1) + ": " + String.format("%.2f", value));
                            } else {
                                setToolTipText(null);
                            }
                            return; // Found a bar, set tooltip and exit
                        }
                    }
                }
                setToolTipText(null); // No bar found, clear tooltip
            }
        });
    }
}
