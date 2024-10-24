import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import javax.swing.border.*;
import javax.imageio.ImageIO;
import java.io.InputStream;
import java.util.ArrayList;

public class WordleGUI extends JFrame implements GameDisplay {
    // Theme colors
    private static final Color PRIMARY_COLOR = new Color(212, 255, 223);
    private static final Color SECONDARY_COLOR = new Color(180, 235, 190);
    private static final Color TEXT_COLOR = new Color(45, 62, 80);
    private static final Color BUTTON_HOVER_COLOR = new Color(190, 245, 200);
    private static final Color CORRECT_LETTER_COLOR = new Color(80, 200, 120);
    private static final Color WRONG_POSITION_COLOR = new Color(255, 204, 100);
    private static final Color WRONG_LETTER_COLOR = new Color(160, 160, 160);

    // Background fade constants
    private static final float INITIAL_ALPHA = 0.1f;
    private static final float TARGET_ALPHA = 0.7f;
    private static final int FADE_DURATION = 1000; // milliseconds
    private float currentAlpha = INITIAL_ALPHA;
    private Timer fadeTimer;
    
    // Game components
    private WordGame game;
    private JTextField inputField;
    private JPanel displayPanel;
    private JLabel statusLabel;
    private JButton guessButton;
    private JButton newGameButton;
    private ArrayList<JPanel> guessRows;
    private JPanel gamePanel;
    private JPanel startPanel;
    private JPanel loadingPanel;
    private Timer loadingTimer;
    private BufferedImage backgroundImage;
    private CardLayout cardLayout;
    private JPanel mainPanel;

    // Custom rounded border inner class
    private static class RoundedBorder extends AbstractBorder {
        private int radius;

        RoundedBorder(int radius) {
            this.radius = radius;
        }

        @Override
        public void paintBorder(Component c, Graphics g, int x, int y, int width, int height) {
            Graphics2D g2d = (Graphics2D) g.create();
            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2d.setColor(c.getBackground());
            g2d.drawRoundRect(x, y, width - 1, height - 1, radius, radius);
            g2d.dispose();
        }
    }

    public WordleGUI() {
        initializeGame();
        guessRows = new ArrayList<>();
        loadBackgroundImage();
        setupStartScreen();
    }

    private void initializeGame() {
        game = new SimpleWordGame();
    }

    private void loadBackgroundImage() {
        try {
            InputStream inputStream = getClass().getResourceAsStream("/image/background.jpg");
            if (inputStream != null) {
                backgroundImage = ImageIO.read(inputStream);
            } else {
                System.err.println("Background image not found");
            }
        } catch (Exception e) {
            System.err.println("Error loading background image: " + e.getMessage());
        }
    }

    private void setupStartScreen() {
        setTitle("Enhanced Wordle");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(800, 900); // Increased window size
        setLocationRelativeTo(null);

        // Create main panel with CardLayout
        cardLayout = new CardLayout();
        mainPanel = new JPanel(cardLayout) {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION, 
                                   RenderingHints.VALUE_INTERPOLATION_BILINEAR);

                if (backgroundImage != null) {
                    double widthRatio = (double) getWidth() / backgroundImage.getWidth();
                    double heightRatio = (double) getHeight() / backgroundImage.getHeight();
                    double scale = Math.max(widthRatio, heightRatio) * 1.2; // Increased scale

                    int scaledWidth = (int) (backgroundImage.getWidth() * scale);
                    int scaledHeight = (int) (backgroundImage.getHeight() * scale);

                    int x = (getWidth() - scaledWidth) / 2;
                    int y = (getHeight() - scaledHeight) / 3; // Lowered position

                    g2d.drawImage(backgroundImage, x, y, scaledWidth, scaledHeight, this);
                    
                    // Enhanced background overlay with current alpha
                    g2d.setColor(new Color(0, 0, 0, currentAlpha));
                    g2d.fillRect(0, 0, getWidth(), getHeight());
                } else {
                    GradientPaint gradient = new GradientPaint(
                        0, 0, PRIMARY_COLOR,
                        getWidth(), getHeight(), SECONDARY_COLOR
                    );
                    g2d.setPaint(gradient);
                    g2d.fillRect(0, 0, getWidth(), getHeight());
                }
            }
        };

        setupStartPanel();
        setupLoadingPanel();
        setupGamePanel();

        mainPanel.add(startPanel, "start");
        mainPanel.add(loadingPanel, "loading");
        mainPanel.add(gamePanel, "game");

        add(mainPanel);
    }

    private void setupStartPanel() {
        startPanel = new JPanel(new GridBagLayout());
        startPanel.setOpaque(false);

        JLabel titleLabel = new JLabel("");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 48));
        titleLabel.setForeground(Color.WHITE);

        JButton playButton = createStyledButton("PLAY");
        // Set preferred size to make the button larger
        playButton.setPreferredSize(new Dimension(200, 50)); // Adjust the size as needed

        playButton.addActionListener(e -> showLoadingScreen());

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.insets = new Insets(0, 0, 20, 0);
        startPanel.add(titleLabel, gbc);

        // Move the playButton lower by increasing gbc.gridy and adding extra Insets
        gbc.gridy = 8; // Move it lower by setting a higher row index
        gbc.insets = new Insets(500, 0, 0, 0); // Add space above the button
        startPanel.add(playButton, gbc);

    }

    private void setupLoadingPanel() {
        loadingPanel = new JPanel(new GridBagLayout());
        loadingPanel.setOpaque(false);

        JLabel loadingLabel = new JLabel("Loading...");
        loadingLabel.setFont(new Font("Arial", Font.BOLD, 24));
        loadingLabel.setForeground(Color.WHITE);
        loadingPanel.add(loadingLabel);
    }

    private void setupGamePanel() {
        gamePanel = new JPanel(new BorderLayout(10, 10));
        gamePanel.setOpaque(false);

        // North panel
        JPanel northPanel = new JPanel(new FlowLayout());
        northPanel.setOpaque(false);
        statusLabel = new JLabel("Enter a 5-letter word!");
        statusLabel.setFont(new Font("Arial", Font.BOLD, 18));
        statusLabel.setForeground(Color.WHITE);
        
        newGameButton = createStyledButton("New Game");
        newGameButton.setPreferredSize(new Dimension(120, 40));
        newGameButton.addActionListener(e -> resetGame());
        
        northPanel.add(statusLabel);
        northPanel.add(Box.createHorizontalStrut(20));
        northPanel.add(newGameButton);
        gamePanel.add(northPanel, BorderLayout.NORTH);

        // Center panel
        displayPanel = new JPanel();
        displayPanel.setLayout(new BoxLayout(displayPanel, BoxLayout.Y_AXIS));
        displayPanel.setOpaque(false);
        JScrollPane scrollPane = new JScrollPane(displayPanel);
        scrollPane.setOpaque(false);
        scrollPane.getViewport().setOpaque(false);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        gamePanel.add(scrollPane, BorderLayout.CENTER);

        // South panel
        JPanel southPanel = new JPanel();
        southPanel.setOpaque(false);
        inputField = new JTextField(12);
        inputField.setFont(new Font("Arial", Font.BOLD, 16));
        inputField.setBorder(new RoundedBorder(5));
        inputField.setBackground(PRIMARY_COLOR);

        guessButton = createStyledButton("Guess");
        guessButton.setPreferredSize(new Dimension(100, 40));

        southPanel.add(inputField);
        southPanel.add(guessButton);
        gamePanel.add(southPanel, BorderLayout.SOUTH);

        // Add listeners
        guessButton.addActionListener(e -> makeGuess());
        inputField.addActionListener(e -> makeGuess());
    }

    private JButton createStyledButton(String text) {
        JButton button = new JButton(text);
        button.setFont(new Font("Arial", Font.BOLD, 16));
        button.setForeground(TEXT_COLOR);
        button.setBackground(PRIMARY_COLOR);
        button.setBorder(new RoundedBorder(10));
        button.setFocusPainted(false);
        
        button.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                button.setBackground(BUTTON_HOVER_COLOR);
            }
            
            @Override
            public void mouseExited(MouseEvent e) {
                button.setBackground(PRIMARY_COLOR);
            }
        });
        
        return button;
    }

    private void showLoadingScreen() {
        cardLayout.show(mainPanel, "loading");
        currentAlpha = INITIAL_ALPHA;
        
        // Setup fade animation
        fadeTimer = new Timer(50, new ActionListener() {
            private long startTime = -1;
            
            @Override
            public void actionPerformed(ActionEvent e) {
                if (startTime == -1) {
                    startTime = System.currentTimeMillis();
                }
                
                long elapsed = System.currentTimeMillis() - startTime;
                float progress = Math.min(1.0f, (float) elapsed / FADE_DURATION);
                
                currentAlpha = INITIAL_ALPHA + (TARGET_ALPHA - INITIAL_ALPHA) * progress;
                mainPanel.repaint();
                
                if (progress >= 1.0f) {
                    fadeTimer.stop();
                }
            }
        });
        fadeTimer.start();

        loadingTimer = new Timer(1500, e -> {
            cardLayout.show(mainPanel, "game");
            loadingTimer.stop();
        });
        loadingTimer.setRepeats(false);
        loadingTimer.start();
    }

    private void makeGuess() {
        String guess = inputField.getText().trim().toUpperCase();
        if (guess.isEmpty()) {
            showFeedback("Please enter a word!");
            return;
        }

        String result = game.checkGuess(guess);
        if (result.startsWith("INVALID")) {
            showFeedback("Invalid word! Please enter a 5-letter word.");
            return;
        }

        // Parse the result
        String[] parts = result.split(":");
        String feedback = parts[1];
        
        // Create guess row
        JPanel guessRow = new JPanel(new FlowLayout(FlowLayout.CENTER, 5, 5));
        guessRow.setOpaque(false);
        
        // Add letter boxes with appropriate colors
        for (int i = 0; i < 5; i++) {
            JLabel letterBox = new JLabel(String.valueOf(guess.charAt(i)));
            letterBox.setFont(new Font("Arial", Font.BOLD, 20));
            letterBox.setForeground(Color.WHITE);
            letterBox.setHorizontalAlignment(SwingConstants.CENTER);
            letterBox.setPreferredSize(new Dimension(40, 40));
            letterBox.setOpaque(true);
            
            // Set background color based on feedback
            switch (feedback.charAt(i)) {
                case 'G':
                    letterBox.setBackground(CORRECT_LETTER_COLOR);
                    break;
                case 'Y':
                    letterBox.setBackground(WRONG_POSITION_COLOR);
                    break;
                default:
                    letterBox.setBackground(WRONG_LETTER_COLOR);
            }
            
            guessRow.add(letterBox);
        }
        
        displayPanel.add(guessRow);
        displayPanel.revalidate();
        displayPanel.repaint();
        
        inputField.setText("");
        
        if (game.isGameOver()) {
            gameOver();
        }
    }

    private void resetGame() {
        initializeGame();
        displayPanel.removeAll();
        guessRows.clear();
        inputField.setEnabled(true);
        guessButton.setEnabled(true);
        statusLabel.setText("Enter a 5-letter word!");
        inputField.setText("");
        displayPanel.revalidate();
        displayPanel.repaint();
    }

    @Override
    public void updateDisplay(String message) {
        statusLabel.setText(message);
    }

    @Override
    public void showFeedback(String feedback) {
        updateDisplay(feedback);
    }

    @Override
    public void gameOver() {
        inputField.setEnabled(false);
        guessButton.setEnabled(false);
        statusLabel.setText(game.getGameStatus() + " - Click 'New Game' to play again!");
    }

    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }

        SwingUtilities.invokeLater(() -> {
            WordleGUI gui = new WordleGUI();
            gui.setVisible(true);
        });
    }
}