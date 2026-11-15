/**
 * A normal student enemy. Weak, and only asks easy Java questions.
 */
public class Student extends Enemy {

    private static final int MAX_HP = 30;
    private static final int ATTACK_POWER = 12;

    public Student(String name) {
        super(name, MAX_HP, ATTACK_POWER);
    }

    @Override
    public Question.Difficulty getQuestionDifficulty() {
        return Question.Difficulty.EASY;
    }

    @Override
    public String getIntroLine() {
        return "waves a crumpled cheat sheet: \"You will never beat me!\"";
    }

    @Override
    public String getArt() {
        return ""
                + "        ___           \n"
                + "       /   \\          \n"
                + "      |     |   +-----+\n"
                + "      | o o |   | []  |\n"
                + "      |  .  |   |     |\n"
                + "     /|=====|\\  +-----+\n"
                + "    / | ### | \\        \n"
                + "      d     b          \n";
    }
}
