# Spam Detector

## Group Members:

Abbas Akbar

Brandon Viaje

Ethan Warriner

## Project Information

For this Assignment we created a Spam Detector that classifies emails as either SPAM or HAM, using a probability model. It analyzes word frequency to calculate the probablity of whether the email is spam or not. The program initially trains itself using marked spam and ham emails to create two maps in which it stores word frequencies. Then it tests new emails by calculating the spam probabiliy based on the two previously created maps. The results are then shown in a JTable with the categories File Name, Actual Class, and Spam Probability. The accuracy and precision are then expressed above.

The following concepts were utilized:

- **Basic Parallel Programming**
- **Build Tools (Natural Language Processing)**
- **File I/O**
- **Logistic Regression**
- **Recursion**

## Enhancements
### Model Enhancements:

- **Stop Words Removal**: We enhanced model performance by implementing a collection of stop words to filter out noise, thus ensuring more accuracy and focused estimations.

- **Stemming with Apache OpenNLP**: Utilized Apache OpenNLP, a powerful NLP tool for the processing of natural language text. We used this tool in applying advanced stemming techniques. This allows for our model to normalize words into their root forms, therefore improving its ability to recognize variations and  boosting overall efficiency.

- **Laplace Smoothing**: Implemented Laplace smoothing to address the key issue of zero-frequency occurrences in the probability map, drastically reducing quantified errors and improving the model’s robustness.

- **Threading**: Implemented thread managing for back end processes for optimal UI responsiveness, even during heavy computational tasks. This results in a seamless user experience without delays/freezing.

- **Log Spam Probabilities**: Implemented practical handling of log(0) and log(1) edge cases when computing if a file is spam, eliminating potential NaN errors and ensuring dependable spam probability calculations, thus guaranteeing consistent results.

- **Spam Classification Threshold Adjustments**: We adjusted the spam classificaiton threshold to 0.7, rather than the default 0.5 value. This change was made to improve spam classification performance by reducing false positives and enhancing accuracy based on performance metrics.

- **Word Frequency Thresholding**: We attempted to add a function to filter out rare words (words that are seen < 5 times) from the training data, with the intention of improving model performance such as overfitting and noise reduction. However, after testing, we found that this adjustment caused a significant drop in both accuracy and precision, which is why we decided not to move forward with it. 

### UI Enhancements:

- **Data Integrity**: Integrated a heightened user experience by utilizing advanced JTable models that prevent accidental data edits. This safeguards the application’s critical data.

- **Aesthetic Overhaul**: Renovated the UI with precisely chosen colour schemes and fonts, providing a professional and visually appealing interface.

- **User Interaction/Experience**: Improved user interaction and experience by implementing a fully dynamic interface. Key features include navigation buttons, resizability, load progress bars, interactive menus, and clickable cells to access detailed information. This creates a highly engaging and responsive experience that empowers users to explore the data effortlessly.

## How to run

NOTE: Make sure you have java installed on your local machine or an IDE such as VSCode/Intellij.

1. Clone this repository
   ```bash
   git clone https://github.com/OntarioTech-CS-program/w25-csci2020u-assignment01-a1-akbar-viaje-warriner.git
   ```
2. Navigate to the project directory
   ```bash
   cd w25-csci2020u-assignment01-a1-akbar-viaje-warriner
   ```
3. Go to the source folder
   ```bash
   cd src/main/java/csci2020u/assignment01
   ```
4. Compile and run the application
   ```bash
   javac SpamDetectorGUI.java
   java SpamDetectorGUI

## Demo

![SpamDetectorDemo](https://github.com/user-attachments/assets/5a30f6e4-b6f4-4ad6-995f-bd49b3b95d1c)

## References 
[1] [Bag of Words](https://en.wikipedia.org/wiki/Bag-of-words_model)

[2] [Naive Bayes Spam Filtering](https://en.wikipedia.org/wiki/Naive_Bayes_spam_filtering)

[3] [Naive Bayes Spam Filter – From Scratch](https://towardsdatascience.com/naive-bayes-spam-filter-from-scratch-12970ad3dae7/)

[4] [Enhancing the efficiency of learning-based spam filters](https://www.virusbulletin.com/virusbulletin/2007/03/enhancing-efficiency-learning-based-spam-filters/)

[5] [What is Logistic Regression](https://www.spiceworks.com/tech/artificial-intelligence/articles/what-is-logistic-regression/#:~:text=Logistic%20regression%20is%20a%20supervised%20machine%20learning%20algorithm%20that%20accomplishes,1%2C%20or%20true%2Ffalse.)

[6] [Additive Smoothing Techniques](https://www.geeksforgeeks.org/additive-smoothing-techniques-in-language-models/)

[7] [Naive Bayes Log Probability](https://www.cs.rhodes.edu/~kirlinp/courses/ai/f18/projects/proj3/naive-bayes-log-probs.pdf)

[8] [Apache OpenNLP Docs](https://opennlp.apache.org/docs/)

> **Note:** This project was originally submitted as coursework for *CSCI2020U Software Systems Development & Integration* at Ontario Tech University, Winter 2025.

