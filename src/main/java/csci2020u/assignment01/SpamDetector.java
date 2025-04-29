package csci2020u.assignment01;

import opennlp.tools.stemmer.PorterStemmer;
import java.io.File;
import java.io.IOException;
import java.util.*;

public class SpamDetector {

    private final Map<String, Integer> trainHamFreq;
    private final Map<String, Integer> trainSpamFreq;
    private final Map<String, Double> wordProb;
    private final Set<String> words ;
    private final List<TestFile> testResults;
    private final Set<String> stopWords;
    private final PorterStemmer stemmer;
    private int spamFileCount;
    private int hamFileCount;
    private int truePos;
    private int falsePos;
    private int trueNeg;

    public SpamDetector() {
        //Initialize data structures
        stemmer = new PorterStemmer();
        trainHamFreq = new TreeMap<>();
        trainSpamFreq = new TreeMap<>();
        words = new HashSet<>();
        wordProb = new TreeMap<>();
        testResults = new ArrayList<>();
        stopWords = new HashSet<>();

        //Initialize var
        spamFileCount = 0;
        hamFileCount = 0;
        trueNeg = 0;
        truePos = 0;
        falsePos = 0;

        //Stop Words to reduce noise
        stopWords.addAll(Set.of(
                "the", "a", "am", "an", "and", "but", "by", "of", "in", "to", "for", "is", "are", "on", "at", "this",
                "that", "with", "as", "it", "be", "was", "were", "has", "have", "had", "not", "you", "we", "they", "he",
                "she", "them", "their", "there", "which", "who", "whom", "how", "where", "when", "why", "i", "me", "my",
                "ours", "ourselves", "yours", "yourself", "yourselves", "all", "any", "can", "could", "did", "do", "does",
                "doing", "don't", "hadn't", "hasn't", "haven't", "having", "how's", "i'm", "i've", "i'll", "i'd", "if",
                "isn't", "it'd", "it'll", "it's", "let's", "mustn't", "needn't", "shouldn't", "that'll", "that'd", "that's",
                "there's", "they'd", "they'll", "they're", "they've", "wasn't", "weren't", "what's", "where's",  "who's", "why's"
        ));
    }

    /**
     * Reads each file in the folder
     *
     * @param file either a file or a directory
     * @param spamFlag  flag that determines whether a file is spam
     */
    public void parseFolder(File file,  boolean spamFlag) throws IOException {
        if (file.isDirectory()) {
            File[] files = file.listFiles();
            assert files != null;
            for (File currentFile : files) {
                parseFolder(currentFile, spamFlag);
            }
        }else{
            Scanner scanner = new Scanner(file);
            //Creates a new empty set for every file, helps when ignoring the same word
            Set<String> processedWords = new HashSet<>();
            while (scanner.hasNext()) {
                // Token by token, ignore case.
                String word = scanner.next().toLowerCase();
                    if (isValidWord(word)) {
                        word = stemWord(word);
                        words.add(word); //Add to set
                        countFreq(word,spamFlag,processedWords);
                    }
            }
            scanner.close();
            //Checks if it's a spam or ham file
            if (spamFlag) {
                spamFileCount++;  // Increment spam file count
            } else {
                hamFileCount++;  // Increment ham file count
            }
        }
    }

    /**
     * Counts how many files have each word in spom and ham folders
     *
     * @param word              word for which the number of files containing at least one appearance of it will be computed
     * @param spamFlag          determines whether file is classified as ham or spam
     * @param processedWords    set of non-duplicate words from files that have been read
     */
    private void countFreq(String word, boolean spamFlag, Set<String> processedWords) {
        // Avoids double counting
        if (!processedWords.contains(word)){
            if (spamFlag) {
                trainSpamFreq.put(word, trainSpamFreq.getOrDefault(word, 0) + 1);
            } else {
                trainHamFreq.put(word, trainHamFreq.getOrDefault(word, 0) + 1);
            }
            processedWords.add(word); // Word processed in current file
        }
    }

    /**
     * Takes the stem of a word
     * @param word The word to be stemmed
     * @return stemmed word
     */
    private String stemWord(String word) {return stemmer.stem(word);}

    /**
     * Creates a probability map using the two map frequencies
     */
    public void probMap() {
        for (String word : words) {
            // Apply Laplace smoothing: add 1 to numerator, add 2 to denominator
            double appearSpam = (double) (trainSpamFreq.getOrDefault(word, 0)+1) / (spamFileCount + 2); //add 2 to the denominator for the number of classes we have: spam,ham
            double appearHam = (double) (trainHamFreq.getOrDefault(word, 0)+1)  / (hamFileCount + 2); //add 2 to the denominator for the number of classes we have: spam,ham
            double probSpam = appearSpam / (appearSpam + appearHam);
            wordProb.put(word, probSpam);
        }
    }

    /**
     * Computes the probability of a file being spam
     *
     * @return spam probability of a file
     */
    //Compute File being Spam
    public double computeSpamProb() {
        double logOdds = 0.0;
        double probWordGivenSpam = 0.0;
        final double epsilon = 1e-6;  // Prevents 0 and 1 probabilities

        // Get initial probabilities based on class distribution in the training data
        double initialSpam = (double) spamFileCount / (spamFileCount + hamFileCount);
        double initialHam = (double) hamFileCount / (spamFileCount + hamFileCount);

        for (String word : words) {
            //If word is in the wordProb map use their prob value, else make default value of 0.5
            probWordGivenSpam = wordProb.getOrDefault(word, 0.5);

            //Ensure probability is never exactly 0 or 1 to avoid math errors. Prevents log(0) (NaN) and log(1)
            //If prSW is too close to 1, set it slightly lower (1 - epsilon). If prSW is too close to 0, set it slightly higher (epsilon)
            probWordGivenSpam = Math.max(Math.min(probWordGivenSpam, 1 - epsilon), epsilon);

            // Update odds with the log of the word probabilities
            logOdds += Math.log(1 - probWordGivenSpam) - Math.log(probWordGivenSpam);
        }

        //Create log of initial probabilities
        double logInitialProbabilities = Math.log(initialSpam / initialHam);

        //Final spam probability with log of initial prob factored in
        return 1 / (1 + Math.pow(Math.E, logOdds + logInitialProbabilities));
    }

    /**
     * Stores the data for the test results
     *
     * @return  a 2d array storing the file names, file class, and spam probability
     */
    public String[][] getTestResults() {
        String[][] data = new String[getTotalFile()][3];
        int row = 0;
        for (TestFile testFile : testResults) {
            data[row][0] = testFile.getFilename();
            data[row][1] = testFile.getActualClass();
            data[row][2] = String.valueOf(testFile.getSpamProbRounded());
            row++;
        }
        return data;
    }

    /**
     * Tests the file to see if it is spam
     *
     * @param file          either a file or a directory
     * @param actualClass   the dictation of the file; ham or spam
     */
    public void test(File file, String actualClass) throws IOException {
        if(file.isDirectory()){
            File[] files = file.listFiles();
            assert files != null;
            for(File currentFile : files){
                test(currentFile,actualClass);
            }
        }else{
            Scanner scanner = new Scanner(file);
            words.clear(); //Clear previous words
            while (scanner.hasNext()){
                String word = scanner.next().toLowerCase();
                if(isValidWord(word)){
                    words.add(word);
                }
            }
            scanner.close();
            double spamProb = computeSpamProb();
            String predictClass = (spamProb >= 0.7) ? "Spam" : "Ham"; //Threshold is 0.7 to be classified as spam
            testResults.add(new TestFile(file.getName(),spamProb, actualClass));
            //Update truePositives, trueNegatives and falsePositives
            if(predictClass.equals("Spam") && actualClass.equals("Spam")) {
                truePos++;
            }else if(predictClass.equals("Ham") && actualClass.equals("Ham")){
                trueNeg++;
            }else{
                falsePos++;
            }
        }
    }

    /**
     * Calls train() and test() on the ham and spam folders
     *
     * @param trainHamFolder    folder containing ham files for training
     * @param trainHam2Folder   second folder containing ham files for training
     * @param trainSpamFolder   folder containing spam files for training
     * @param testHamFolder     folder containing ham files for testing
     * @param testSpamFolder    folder containing spam files for testing
     * @throws IOException      handles loading/reading file errors
     */
    public void startProcess(File trainHamFolder, File trainHam2Folder, File trainSpamFolder, File testHamFolder, File testSpamFolder) throws IOException {
        // Train and Test Files
        train(trainHamFolder,trainHam2Folder, trainSpamFolder);
        test(testHamFolder, "Ham");
        test(testSpamFolder, "Spam");
    }

    /**
     * Trains the software to detect spam
     *
     * @param trainHamFolder    files inside ham folder
     * @param trainHam2Folder   files inside ham2 folder
     * @param trainSpamFolder   files inside spam folder
     * @throws IOException      handles reading/loading file errors
     */
    public void train(File trainHamFolder,File trainHam2Folder, File trainSpamFolder) throws IOException {
        //Add words, then create probabilityMap
        parseFolder(trainHamFolder, false);
        parseFolder(trainHam2Folder, false);
        parseFolder(trainSpamFolder, true);
        probMap();
    }

    /**
     * Helper function for verifying a word is valid. Verifies using regex, and check if it isn't in our stop words set
     *
     * @param word  a word to be verified
     * @return      boolean value
     */
    private boolean isValidWord(String word){return word.matches("^[a-zA-Z]+$") && !stopWords.contains(word);}

    /**
     * Get the total number of files
     *
     * @return  total number of files
     */
    protected int getTotalFile(){return trueNeg + truePos + falsePos;}

    /**
     * Percentage of correct guesses
     *
     * @return  Accuracy
     */
    public double getAccuracy(){ return (double) (truePos + trueNeg) / getTotalFile(); }

    /**
     * Ratio of correct positives to spam guesses
     *
     * @return  Precision
     */
    public double getPrecision(){return (double) truePos / (truePos + falsePos);}
}