import javax.swing.*;
import java.awt.*;

class ReviewPanel extends JPanel {
    private MainFrame parentFrame;
    private CalendarPanel calendarPanel;
    private JTextArea reviewsArea;
    private JComboBox<String> filterCombo;
    private JButton respondButton, refreshButton;

    public ReviewPanel(MainFrame parent, CalendarPanel calendarPanel) {
        this.parentFrame = parent;
        this.calendarPanel = calendarPanel;

        setLayout(new BorderLayout());


        JPanel controlPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        controlPanel.add(new JLabel("Filter by:"));

        String[] filters = {"All Reviews", "Venue Reviews", "Room Reviews", "Show Reviews", "Recent Reviews"};
        filterCombo = new JComboBox<>(filters);
        controlPanel.add(filterCombo);

        refreshButton = new JButton("Refresh");
        controlPanel.add(refreshButton);

        add(controlPanel, BorderLayout.NORTH);

        // Reviews to view
        reviewsArea = new JTextArea();
        reviewsArea.setEditable(false);
        reviewsArea.setLineWrap(true);
        reviewsArea.setWrapStyleWord(true);
        add(new JScrollPane(reviewsArea), BorderLayout.CENTER);




        filterCombo.addActionListener(e -> refresh());
        refreshButton.addActionListener(e -> refresh());



        loadSampleReviews();
    }

    private void loadSampleReviews() {
        StringBuilder sb = new StringBuilder();
        //to be Done

        reviewsArea.setText(sb.toString());
    }

    public void refresh() {
      //refresh to update reviews
        String filter = (String) filterCombo.getSelectedItem();

        StringBuilder sb = new StringBuilder();
        sb.append("== ").append(filter).append(" ==\n\n");

        switch (filter) {
            //To be done
            //case:
              //  break;

            default:
                loadSampleReviews();
                return;
        }

        //reviewsArea.setText(sb.toString());
    }


}
