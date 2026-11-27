/**
 * A professor boss. Much tougher than a student and asks hard
 * Java / OOP questions.
 */
public class Professor extends Enemy {

    private static final int MAX_HP = 80;
    private static final int ATTACK_POWER = 22;

    private final String subject;

    public Professor(String name, String subject) {
        super(name, MAX_HP, ATTACK_POWER);
        this.subject = subject;
    }

    public String getSubject() {
        return subject;
    }

    @Override
    public Question.Difficulty getQuestionDifficulty() {
        return Question.Difficulty.HARD;
    }

    @Override
    public String getIntroLine() {
        return "adjusts glasses: \"" + getSubject()
                + " is MY domain. Show me what you know... or fail.\"";
    }

    @Override
    public String getArt() {
        String fallback = ""
                + "        ___             \n"
                + "       ╱   ╲      ╔═══╗\n"
                + "      ╱     ╲     ║ ▓ ║\n"
                + "     │       │    ║ ▓ ║\n"
                + "     │ o   o │    ║ ▓ ║\n"
                + "     │  ~~~  │    ║ ▓ ║\n"
                + "    ╱│═══════│╲   ╚═══╝\n"
                + "   ╱ │  │#│  │ ╲         \n"
                + "  ╱  │       │  ╲        \n"
                + "     ╚══╤═╤══╝          \n";
        String spriteId = spriteForBoss(getName());
        return SpriteLoader.load(spriteId, SpriteLoader.load("professor", fallback));
    }

    private static String spriteForBoss(String bossName) {
        if (bossName == null) {
            return "professor";
        }
        String lower = bossName.toLowerCase();
        if (lower.contains("loopwell")) {
            return "professor_loopwell";
        }
        if (lower.contains("stacksmann")) {
            return "professor_stacksmann";
        }
        if (lower.contains("nullpointer")) {
            return "professor_nullpointer";
        }
        return "professor";
    }
}
