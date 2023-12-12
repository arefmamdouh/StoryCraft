package storyCraft;


import java.io.IOException;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class StoryCraft extends JFrame {
	private JTextField topicField = new JTextField(10);
    private JTextField ageField = new JTextField(10);
    private JTextField countryField = new JTextField(10);
    private JButton generateButton = new JButton("Generate Story");
    private JButton playButton = new JButton("Play Story");
    private JTextArea storyArea = new JTextArea();
    private JProgressBar progressBar = new JProgressBar();
    private GPT3Service gpt3Service;
    private final String outputPath = "story.mp3";

    public StoryCraft() {
        gpt3Service = new GPT3Service();
        initComponents();
    }

    private void initComponents() {
    	setDefaultCloseOperation(EXIT_ON_CLOSE);
        setTitle("StoryCraft");

        // Set window size to 70% of the screen size and center it
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        setSize(new Dimension((int) (screenSize.width * 0.7), (int) (screenSize.height * 0.7)));
        setLocationRelativeTo(null);

     // Panel for input fields, using GridBagLayout for equal width
        JPanel inputFieldsPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridwidth = GridBagConstraints.REMAINDER;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1;
        gbc.insets = new Insets(5, 5, 5, 5);

        // Adjust the preferred width of the text fields
        Dimension fieldDimension = new Dimension(250, 20); // Increased width, same height
        topicField.setPreferredSize(fieldDimension);
        ageField.setPreferredSize(fieldDimension);
        countryField.setPreferredSize(fieldDimension);

        // Add input fields to the panel
        inputFieldsPanel.add(new JLabel("Topic:"), gbc);
        inputFieldsPanel.add(topicField, gbc);
        inputFieldsPanel.add(new JLabel("Age:"), gbc);
        inputFieldsPanel.add(ageField, gbc);
        inputFieldsPanel.add(new JLabel("Country:"), gbc);
        inputFieldsPanel.add(countryField, gbc);

        // Panel for buttons, side by side using FlowLayout
        JPanel buttonsPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 1, 0));
        buttonsPanel.add(generateButton);
        buttonsPanel.add(playButton);

        // Left panel to stack inputFieldsPanel and buttonsPanel on top of each other
        JPanel leftPanel = new JPanel();
        leftPanel.setLayout(new BorderLayout());
        leftPanel.add(inputFieldsPanel, BorderLayout.NORTH);
        
        // Create a panel that sticks to the bottom for the buttons
        JPanel bottomContainer = new JPanel(new BorderLayout());
        bottomContainer.add(buttonsPanel, BorderLayout.PAGE_START);
        bottomContainer.add(new JPanel(), BorderLayout.CENTER); // Filler panel

        leftPanel.add(bottomContainer, BorderLayout.SOUTH);

        // Scrollable story area
        storyArea.setLineWrap(true);
        storyArea.setWrapStyleWord(true);
        JScrollPane storyScrollPane = new JScrollPane(storyArea);
        storyScrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);

        // Layout configuration
        setLayout(new BorderLayout());
        add(leftPanel, BorderLayout.WEST);
        add(storyScrollPane, BorderLayout.CENTER);
        add(progressBar, BorderLayout.SOUTH);

        generateButton.addActionListener(e -> generateStory());
        playButton.addActionListener(e -> playStory());
        
        setVisible(true);

    }
    
    private void generateStory() {
        String topic = topicField.getText().trim();
        String age = ageField.getText().trim();
        String country = countryField.getText().trim();
        String prompt = createPrompt(topic, age, country);

        // Simulate a loading animation with indeterminate JProgressBar
        progressBar.setIndeterminate(true);
        generateButton.setEnabled(false);

        // Run the story generation in a separate thread to keep the GUI responsive
        new Thread(() -> {
            try {
                String story = gpt3Service.getGPT3Response(prompt);
                storyArea.setText(story);
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                progressBar.setIndeterminate(false);
                generateButton.setEnabled(true);
            }
        }).start();
    }

    private void playStory() {
        String story = storyArea.getText();
        progressBar.setIndeterminate(true);
        playButton.setEnabled(false);
        new Thread(() -> {
        	try {
        		OpenAITTS.convertTextToSpeech(story, outputPath);
        	}
        	finally {
        		progressBar.setIndeterminate(false);
                playButton.setEnabled(true);
        	}
        }).start();
        // Implement OpenAITTS to handle playing the audio directly
    }

    private String createPrompt(String topic, String age, String country) {
        if (topic.isEmpty()) {
            return "Write a short story that appeals to the age " + age + " and the country " + country + ", this will be read to a person of the age mentioned";
        } else {
            return "Write a short story about " + topic + " that appeals to the age " + age + " and the country " + country + ", this will be read to a person of the age mentioned";
        }
    }  

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new StoryCraft().setVisible(true));
    }
}