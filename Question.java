/**
 * A single quiz question with four multiple-choice answers.
 */
public class Question {

    /** How difficult a question is. Students ask EASY, professors ask HARD. */
    public enum Difficulty {
        EASY, HARD
    }

    private final String text;
    private final String[] choices;   // always 4 answer options
    private final int answerIndex;    // index (0-3) of the correct choice
    private final Difficulty difficulty;

    public Question(String text, String[] choices, int answerIndex, Difficulty difficulty) {
        this.text = text;
        this.choices = choices;
        this.answerIndex = answerIndex;
        this.difficulty = difficulty;
    }

    public String getText() {
        return text;
    }

    public String[] getChoices() {
        return choices;
    }

    public int getAnswerIndex() {
        return answerIndex;
    }

    public Difficulty getDifficulty() {
        return difficulty;
    }
}
