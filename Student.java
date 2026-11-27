/**
 * A normal student enemy. Weak, and only asks easy Java questions.
 */
public class Student extends Enemy {

    private static final int MAX_HP = 30;
    private static final int ATTACK_POWER = 12;

    private final String spriteId;

    public Student(String name) {
        this(name, spriteForName(name));
    }

    public Student(String name, String spriteId) {
        super(name, MAX_HP, ATTACK_POWER);
        this.spriteId = spriteId;
    }

    private static String spriteForName(String name) {
        int bucket = Math.abs(name.hashCode()) % 4;
        switch (bucket) {
            case 0: return "student";
            case 1: return "student_2";
            case 2: return "student_3";
            default: return "student_4";
        }
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
        String fallback = ""
                + "        ___             \n"
                + "       ╱   ╲            \n"
                + "      │     │           \n"
                + "      │     │    ╔═════╗\n"
                + "      │ o o │    ║ []  ║\n"
                + "      │  .  │    ║     ║\n"
                + "     ╱│═════│╲   ╚═════╝\n"
                + "    ╱ │ ### │ ╲          \n"
                + "   ╱  │     │  ╲         \n"
                + "      d     b           \n";
        return SpriteLoader.load(spriteId, SpriteLoader.load("student", fallback));
    }
}
