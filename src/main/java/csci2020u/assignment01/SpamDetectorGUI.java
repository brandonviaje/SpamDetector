package csci2020u.assignment01;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.io.*;

public class SpamDetectorGUI {
    public static void main(String[] args) {
        SpamDetector spamDetector = new SpamDetector();
        JFrame frame = new JFrame("Spam Detector");
        //To stack panels on top of each other
        CardLayout cardLayout = new CardLayout();
        JPanel container = new JPanel(cardLayout);

        //Menu Bar
        JMenuBar menuBar = createMenuBar();
        JMenu fileMenu = createFileMenu();
        menuBar.setVisible(false);

        //Create welcome panel
        JPanel mainMenuPanel = createMainMenuPanel(cardLayout,container,menuBar);

        //Create Choose File Panel
        JPanel chooseFilePanel = createChooseFilePanel(spamDetector,container,cardLayout,frame,menuBar, fileMenu);

        //Add panels to container
        container.add(mainMenuPanel, "MainMenu");
        container.add(chooseFilePanel, "ChooseFile");

        //Set frame properties
        frame.add(container);
        frame.setSize(600, 400);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLocationRelativeTo(null);
        frame.setResizable(true);
        frame.setVisible(true);
    }

    /**
     * Creates the main menu panel
     *
     * @param cardLayout        organizes all major panels into card layout: MainMenu, Choosefile and Results
     * @param container         panel that holds the starting elements
     * @param menuBar           allows user to open the file dialog
     *
     * @return                  welcome panel
     */
    private static JPanel createMainMenuPanel(CardLayout cardLayout, JPanel container, JMenuBar menuBar) {
        //Create Welcome Panel
        JPanel mainMenuPanel = new JPanel(new BorderLayout());
        mainMenuPanel.setBackground(new Color(202,207,214));

        //Create Welcome Label
        JLabel welcomeLabel = new JLabel("Welcome to Spam Detector!", SwingConstants.CENTER);
        welcomeLabel.setFont(new Font("Inter", Font.BOLD, 34));
        welcomeLabel.setForeground(new Color(34,34,34));

        //Create Information Label
        JLabel infoLabel = new JLabel("This application allows you to filter and analyze spam messages",SwingConstants.CENTER);
        infoLabel.setFont(new Font("Inter", Font.BOLD, 18));
        infoLabel.setForeground(new Color(34,34,34));

        //Create labelPanels
        JPanel labelPanel = new JPanel();
        labelPanel.setBackground(new Color(202,207,214));
        labelPanel.setBorder(BorderFactory.createEmptyBorder(10, 0, 90, 0)); //Apply padding to space button
        labelPanel.add(welcomeLabel);

        JPanel labelPanel2 = new JPanel();
        labelPanel2.setBackground(new Color(202,207,214));
        labelPanel2.add(infoLabel);

        //Create Button in Welcome Panel
        JButton button = new JButton("Detect Spam");
        button.setFont(new Font("Inter", Font.BOLD, 28));
        button.setForeground(new Color(237,242,239));
        button.setBackground(new Color(20,92,158));
        button.setFocusPainted(false);
        button.setPreferredSize(new Dimension(250, 60));

        // Create a button panel for the button
        JPanel buttonPanel = new JPanel();
        buttonPanel.setBackground(new Color(202,207,214));
        buttonPanel.add(button);

        //Adding UI Components to Welcome Panel
        mainMenuPanel.add(labelPanel, BorderLayout.NORTH);
        mainMenuPanel.add(labelPanel2, BorderLayout.SOUTH);
        mainMenuPanel.add(buttonPanel,BorderLayout.CENTER);

        //Button Action Listener
        button.addActionListener(_-> {
            cardLayout.show(container, "ChooseFile"); //Show chooseFile Panel
            menuBar.setVisible(true);
        });
        return mainMenuPanel;
    }

    /**
     * allows user to choose the directory that holds the train and test folders
     *
     * @param spamDetector      object that allows core function calls
     * @param container         holds all the panels
     * @param cardLayout        organizes panels
     * @param frame             gui window
     * @param menuBar           nav bar that holds the file button
     * @param fileMenu          allows user to navigate file dialog
     * @return                  panel responsible for opening file directory
     */
    private static JPanel createChooseFilePanel(SpamDetector spamDetector, Container container,CardLayout cardLayout,JFrame frame, JMenuBar menuBar, JMenu fileMenu) {
        JPanel chooseFilePanel = new JPanel(new BorderLayout());
        chooseFilePanel.setBackground(new Color(202,207,214));

        //Add Menu Item
        JMenuItem openItem = new JMenuItem("Open");
        openItem.setFont(new Font("Inter", Font.BOLD, 16));
        openItem.setForeground(new Color(34,34,34));
        openItem.setBackground(new Color(20,92,158));

        //Create Instruction Label
        JLabel instructionLabel = new JLabel("Select a directory to start. (File/Open)", SwingConstants.CENTER);
        instructionLabel.setFont(new Font("Inter", Font.BOLD, 30));
        instructionLabel.setForeground(new Color(34,34,34));

        //Create Note Label
        JLabel noteLabel = new JLabel(" NOTE: May take time to load all files.", SwingConstants.CENTER);
        noteLabel.setFont(new Font("Inter", Font.BOLD, 20));
        noteLabel.setForeground(new Color(34,34,34));

        //Add Menu Bar to Frame
        fileMenu.add(openItem);
        menuBar.add(fileMenu);
        frame.setJMenuBar(menuBar);

        //Adding UI components to chooseFilePanel
        chooseFilePanel.add(instructionLabel, BorderLayout.CENTER);
        chooseFilePanel.add(noteLabel, BorderLayout.SOUTH);

        // Call Helper Function
        openItem.addActionListener(e -> openDirectory(spamDetector, container, cardLayout, frame,menuBar,instructionLabel,noteLabel,chooseFilePanel));
        return chooseFilePanel;
    }

    /**
     * integrates selected files with spam detection process (on another thread), updates UI accordingly.
     *
     * @param spamDetector          used to call the spam detection process
     * @param container             holds all the panels
     * @param cardLayout            organizes panels
     * @param frame                 gui window
     * @param menuBar               menuBar to hide menu after opening directory
     * @param instructionLabel      tells user how to get started
     * @param noteLabel             warns the user about runtime
     */
    private static void openDirectory(SpamDetector spamDetector, Container container, CardLayout cardLayout, JFrame frame,JMenuBar menuBar, JLabel instructionLabel, JLabel noteLabel, JPanel chooseFilePanel) {
        JFileChooser directoryChooser = new JFileChooser();
        directoryChooser.setBackground(new Color(20, 92, 158));
        directoryChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        directoryChooser.setCurrentDirectory(new File("."));

        int returnValue = directoryChooser.showOpenDialog(frame);

        if (returnValue == JFileChooser.APPROVE_OPTION) {
            //Change instruction label, hide other labels/menu bar.
            instructionLabel.setText("Loading Files...");
            noteLabel.setVisible(false);
            menuBar.setVisible(false);

            //Adding progress bar
            JProgressBar progressBar = new JProgressBar();
            progressBar.setIndeterminate(true); // No exact percentage
            progressBar.setPreferredSize(new Dimension(400, 25));
            progressBar.setBackground(new Color(20,92,158));

            //Add progress bar to Panel
            chooseFilePanel.add(progressBar, BorderLayout.SOUTH);

            //Get selected directory
            File mainDirectory = directoryChooser.getSelectedFile();
            File trainHamFolder = new File(mainDirectory, "train/ham");
            File trainHam2Folder = new File(mainDirectory, "train/ham2");
            File trainSpamFolder = new File(mainDirectory, "train/spam");
            File testHamFolder = new File(mainDirectory, "test/ham");
            File testSpamFolder = new File(mainDirectory, "test/spam");

            //Use Swing Worker to have back end processes execute in the background. Improves user quality performance, doesn't freeze the screen
            new SwingWorker<Void, Void>() {
                @Override
                protected Void doInBackground() throws Exception {
                    // Call backend task (spam detection)
                    spamDetector.startProcess(trainHamFolder, trainHam2Folder, trainSpamFolder, testHamFolder, testSpamFolder);
                    return null;
                }

                @Override
                protected void done() {
                    // Update UI after backend done
                    try {
                        chooseFilePanel.remove(progressBar);
                        // Create and show result panel
                        JPanel resultPanel = createResultPanel(spamDetector);
                        container.add(resultPanel, "Results");
                        cardLayout.show(container, "Results");
                    } catch (Exception err) {
                        err.printStackTrace();
                    }
                }
            }.execute(); // Starting task in background
        }
    }

    /**
     * creates the menu bar which holds the file buttons
     *
     * @return      menu bar
     */
    private static JMenuBar createMenuBar() {
        JMenuBar menuBar = new JMenuBar();
        menuBar.setBackground(new Color(20,92,158));
        menuBar.setBorderPainted(true);
        return menuBar;
    }

    /**
     * creates the file menu which lets the user choose a directory
     *
     * @return      file menu
     */
    private static JMenu createFileMenu(){
        JMenu fileMenu = new JMenu("File");
        fileMenu.setBackground(new Color(20,92,158));
        fileMenu.setFont(new Font("Inter", Font.BOLD, 20));
        fileMenu.setForeground(Color.WHITE);
        return fileMenu;
    }

    /**
     * creates panel that shows test results and statistics
     *
     * @param spamDetector      object that allows core function calls
     * @return                  panel containing all results and insights (accuracy and precision)
     */
    private static JPanel createResultPanel(SpamDetector spamDetector) {
        //Initialize columnNames and data
        JPanel resultPanel = new JPanel();
        resultPanel.setLayout(new BoxLayout(resultPanel, BoxLayout.Y_AXIS));

        //Header Panel Properties
        JPanel headerPanel = new JPanel();
        headerPanel.setBackground(new Color(202,207,214));

        //Header Label Properties
        JLabel headerLabel = new JLabel("Insights:");
        headerLabel.setForeground(new Color(34,34,34));
        headerLabel.setFont(new Font("Inter", Font.BOLD, 18));
        headerLabel.setLocation(10, 100);
        headerPanel.add(headerLabel);

        //Create Table
        JTable table = createJTable(spamDetector);
        table.setBounds(30, 40, 200, 300);
        JScrollPane scrollPane = new JScrollPane(table);

        //Create insights
        JPanel labelPanel = createInsightPanel(spamDetector);

        //Add UI Components to Result Panel
        resultPanel.add(headerPanel);
        resultPanel.add(labelPanel);
        resultPanel.add(scrollPane);

        return resultPanel;
    }

    /**
     * create table and update table depending on the files spam process test results
     *
     * @param spamDetector      gets test results and displays them on the table
     * @return                  JTable for the result panel
     */
    private static JTable createJTable(SpamDetector spamDetector) {
        String[] columnNames = {"FILE NAME", "ACTUAL CLASS", "SPAM PROBABILITY"};
        String[][] data = spamDetector.getTestResults(); //Calls helper function in the SpamDetector file

        //Create table
        JTable table = new JTable(data, columnNames);

        //Table Properties
        table.setBackground(new Color(202,207,214));
        table.setForeground(new Color(34,34,34));
        table.setBorder(BorderFactory.createLineBorder(new Color(45, 49, 66)));
        table.setFont(new Font("Inter", Font.BOLD, 14));

        //Table Header Properties
        table.getTableHeader().setOpaque(false);
        table.getTableHeader().setBackground(new Color(20,92,158));
        table.getTableHeader().setForeground(Color.WHITE);
        table.getTableHeader().setBorder(BorderFactory.createLineBorder(new Color(45, 49, 66)));
        table.getTableHeader().setFont(new Font("Inter", Font.BOLD, 16));
        table.getTableHeader().setResizingAllowed(false);

        //Allows for user to not edit changes in cell
        DefaultTableModel tableModel = new DefaultTableModel(data, columnNames) {
            @Override
            public boolean isCellEditable(int row, int column) {return false;}
        };

        //Set Table Model to Default Table Model
        table.setModel(tableModel);

        // To open cell content on click
        table.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                int row = table.getSelectedRow();
                int column = table.getSelectedColumn();

                // Get values of the clicked cell
                String cellValue = table.getValueAt(row, column).toString();
                String columnHeader = table.getColumnName(column);

                // Display the column name and cell value
                JOptionPane.showMessageDialog(null, "Header: " + columnHeader + "\nCell Info: " + cellValue, "Information", JOptionPane.PLAIN_MESSAGE);
            }
        });

        return table;
    }

    /**
     * creates the panel that shows accuracy and precision statistics
     *
     * @param spamDetector      calls back end function
     * @return                  insight Panel
     */
    private static JPanel createInsightPanel(SpamDetector spamDetector) {
        JPanel insightPanel = new JPanel();

        //Label Panel Properties
        insightPanel.setBackground(new Color(202,207,214));

        // Accuracy component
        JLabel accuracyLabel = new JLabel("Accuracy:");
        accuracyLabel.setFont(new Font("Inter", Font.BOLD, 18));
        accuracyLabel.setForeground(new Color(34,34,34));

        //Accuracy Field Properties
        JTextField accuracyField = new JTextField(7);
        accuracyField.setEditable(false);
        accuracyField.setForeground(new Color(34,34,34));
        accuracyField.setBackground(new Color(202,207,214));
        accuracyField.setBorder(BorderFactory.createLineBorder(new Color(202,207,214)));
        accuracyField.setFont(new Font("Inter", Font.BOLD, 18));
        accuracyField.setText(String.format("%.5f", spamDetector.getAccuracy()));

        // Precision component
        JLabel precisionLabel = new JLabel("Precision:");
        precisionLabel.setFont(new Font("Inter", Font.BOLD, 18));
        precisionLabel.setForeground(new Color(34,34,34));

        //Precision Field Properties
        JTextField precisionField = new JTextField(7);
        precisionField.setEditable(false);
        precisionField.setBackground(new Color(202,207,214));
        precisionField.setForeground(new Color(34,34,34));
        precisionField.setBorder(BorderFactory.createLineBorder(new Color(202,207,214)));
        precisionField.setFont(new Font("Inter", Font.BOLD, 18));
        precisionField.setText(String.format("%.5f", spamDetector.getPrecision()));

        //Add Accuracy and Precision Labels to labelPanel
        insightPanel.add(accuracyLabel);
        insightPanel.add(accuracyField);
        insightPanel.add(precisionLabel);
        insightPanel.add(precisionField);
        insightPanel.setSize(20, 20);
        return insightPanel;
    }
}