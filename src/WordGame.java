abstract class WordGame {
    protected String secretWord;
    protected int maxAttempts;
    protected int currentAttempts;
    protected boolean isGameActive;
    protected static final int DEFAULT_WORD_LENGTH = 5;
    
    public WordGame(int maxAttempts) {
        if (maxAttempts <= 0) {
            throw new IllegalArgumentException("Maximum attempts must be greater than 0");
        }
        this.maxAttempts = maxAttempts;
        this.currentAttempts = 0;
        this.isGameActive = true;
    }
    
    /**
     * Checks a guess against the secret word and returns feedback
     * @param guess The player's guess
     * @return A string in format "GUESS:FEEDBACK" where FEEDBACK uses G(green),Y(yellow),X(gray)
     */
    abstract String checkGuess(String guess);
    
    /**
     * Checks if the game is over (either won or out of attempts)
     * @return true if game is over, false otherwise
     */
    abstract boolean isGameOver();
    
    /**
     * Gets the current game status message
     * @return A string describing the current game state
     */
    abstract String getGameStatus();
    
    /**
     * Gets the number of remaining attempts
     * @return Number of attempts left
     */
    public int getRemainingAttempts() {
        return maxAttempts - currentAttempts;
    }
    
    /**
     * Gets the total number of attempts allowed
     * @return Maximum number of attempts
     */
    public int getMaxAttempts() {
        return maxAttempts;
    }
    
    /**
     * Gets the current attempt number
     * @return Current attempt number
     */
    public int getCurrentAttempt() {
        return currentAttempts;
    }
    
    /**
     * Checks if the game is currently active
     * @return true if game is active, false otherwise
     */
    public boolean isActive() {
        return isGameActive;
    }
    
    /**
     * Validates if a word is of the correct length
     * @param word Word to validate
     * @return true if word is correct length, false otherwise
     */
    protected boolean isCorrectLength(String word) {
        return word != null && word.length() == DEFAULT_WORD_LENGTH;
    }
    
    /**
     * Validates if a string contains only letters
     * @param word Word to validate
     * @return true if word contains only letters, false otherwise
     */
    protected boolean isOnlyLetters(String word) {
        return word != null && word.matches("[a-zA-Z]+");
    }
    
    /**
     * Standardizes input by converting to uppercase
     * @param input String to standardize
     * @return Uppercase version of input, or null if input was null
     */
    protected String standardizeInput(String input) {
        return input != null ? input.toUpperCase() : null;
    }
    
    /**
     * Resets the game state
     */
    public void resetGame() {
        currentAttempts = 0;
        isGameActive = true;
    }
    
    /**
     * Gets the length of words used in the game
     * @return Standard word length
     */
    public int getWordLength() {
        return DEFAULT_WORD_LENGTH;
    }
    
    /**
     * Ends the current game
     */
    protected void endGame() {
        isGameActive = false;
    }
    
    /**
     * Creates feedback string based on comparison arrays
     * @param feedbackArray Array of feedback characters (G, Y, or X)
     * @return Formatted feedback string
     */
    protected String createFeedbackString(char[] feedbackArray) {
        StringBuilder feedback = new StringBuilder();
        for (char c : feedbackArray) {
            feedback.append(c);
        }
        return feedback.toString();
    }
    
    /**
     * Validates a guess before processing
     * @param guess The player's guess
     * @return true if guess is valid, false otherwise
     */
    protected boolean isValidGuess(String guess) {
        return guess != null && 
               isCorrectLength(guess) && 
               isOnlyLetters(guess) && 
               isGameActive;
    }
    
    /**
     * Checks if an attempt can be made
     * @return true if attempt is allowed, false otherwise
     */
    protected boolean canMakeAttempt() {
        return currentAttempts < maxAttempts && isGameActive;
    }
}