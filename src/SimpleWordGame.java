class SimpleWordGame extends WordGame {
    private String[] wordList;
    private boolean hasWon;
    
    public SimpleWordGame() {
        super(6);  // 6 attempts max
        this.hasWon = false;
        // Extended word list for more variety
        this.wordList = new String[]{
            "HAPPY", "SMILE", "SUNNY", "WORLD", "PIZZA",
            "BRAIN", "CLOUD", "DANCE", "EAGLE", "FLAME",
            "GREEN", "HEART", "LIGHT", "MUSIC", "OCEAN",
            "PEACE", "QUEEN", "RIVER", "SHINE", "TIGER"
        };
        this.secretWord = selectRandomWord();
    }
    
    private String selectRandomWord() {
        int index = (int)(Math.random() * wordList.length);
        return wordList[index];
    }
    
    public boolean isValidWord(String word) {
        if (word == null) return false;
        
        // Check if word is exactly 5 letters
        if (word.length() != 5) return false;
        
        // Check if word contains only letters
        return word.matches("[A-Za-z]{5}");
    }
    
    @Override
    public String checkGuess(String guess) {
        // Validate guess
        if (!isValidWord(guess)) {
            return "INVALID:XXXXX";  // Return invalid marker
        }
        
        // Convert guess to uppercase for comparison
        guess = guess.toUpperCase();
        currentAttempts++;
        
        // Create arrays to track letter occurrences
        int[] secretLetterCount = new int[26];
        int[] guessLetterCount = new int[26];
        char[] result = new char[5];
        
        // First pass: Mark exact matches (green)
        for (int i = 0; i < 5; i++) {
            char secretChar = secretWord.charAt(i);
            char guessChar = guess.charAt(i);
            
            if (guessChar == secretChar) {
                result[i] = 'G';
                secretLetterCount[secretChar - 'A']++;
                guessLetterCount[guessChar - 'A']++;
            } else {
                result[i] = 'X';  // Temporary mark for non-exact matches
            }
        }
        
        // Count remaining letters in secret word
        for (int i = 0; i < 5; i++) {
            if (result[i] != 'G') {
                secretLetterCount[secretWord.charAt(i) - 'A']++;
            }
        }
        
        // Second pass: Mark partial matches (yellow)
        for (int i = 0; i < 5; i++) {
            if (result[i] == 'X') {
                char guessChar = guess.charAt(i);
                int letterIndex = guessChar - 'A';
                
                if (secretLetterCount[letterIndex] > guessLetterCount[letterIndex]) {
                    result[i] = 'Y';
                    guessLetterCount[letterIndex]++;
                }
            }
        }
        
        // Check if won
        hasWon = guess.equals(secretWord);
        
        // Convert result array to string
        StringBuilder resultString = new StringBuilder();
        for (char c : result) {
            resultString.append(c);
        }
        
        return guess + ":" + resultString.toString();
    }
    
    @Override
    public boolean isGameOver() {
        return hasWon || currentAttempts >= maxAttempts;
    }
    
    @Override
    public String getGameStatus() {
        if (hasWon) {
            return "Congratulations!" + currentAttempts + " attempts!";
        }
        if (currentAttempts >= maxAttempts) {
            return "Game Over! The word was: " + secretWord;
        }
        return "Keep guessing! Attempts left: " + getRemainingAttempts();
    }
    
    // Helper method to get secret word (for testing)
    protected String getSecretWord() {
        return secretWord;
    }
    
    // Method to validate if a word exists in the word list
    public boolean isInWordList(String word) {
        if (word == null) return false;
        word = word.toUpperCase();
        for (String validWord : wordList) {
            if (validWord.equals(word)) {
                return true;
            }
        }
        return false;
    }
}