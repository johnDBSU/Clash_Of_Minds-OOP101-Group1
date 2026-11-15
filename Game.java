import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Controls the whole game: hero select, the 4 stages (3 students + 1
 * professor each), healing between stages and the win/lose screens.
 */
public class Game {

    private static final int TOTAL_STAGES = 4;
    private static final int STUDENTS_PER_STAGE = 3;
    private static final int HEAL_PERCENT_BETWEEN_STAGES = 30;

    // Bosses, in stage order.
    private static final String[] PROFESSOR_NAMES = {
            "Professor Syntax", "Professor Loopwell", "Professor Stacksmann", "Dr. NullPointer"
    };
    private static final String[] STAGE_TOPICS = {
            "Java Basics", "Control Flow", "Methods and Arrays", "Object-Oriented Programming"
    };
    private static final String[] STUDENT_NAMES = {
            "Chad the Crammer", "Mika the Mnemonic", "Ravi the Rote-Learner",
            "Tessa the Terminology", "Ben the Blueprint", "Nina the Notetaker",
            "Omar the Overconfident", "Lia the Last-Minute"
    };

    private final QuestionBank questionBank = new QuestionBank();
    private final Random random = new Random();

    /** Starts the game, offering retries after each defeat. */
    public void start() {
        ConsoleUtil.printGameTitle();

        while (true) {
            Player player = chooseHero();

            playAllStages(player);

            if (!player.isDefeated()) {
                printVictoryScreen(player);
                return; // game won
            }

            printDefeatScreen(player);
            int choice = ConsoleUtil.readChoice("  1) Try again   2) Quit  > ", 1, 2);
            if (choice == 2) {
                System.out.println("  Thanks for playing!");
                return;
            }
        }
    }

    // ------------------------------------------------------------------
    // Hero selection
    // ------------------------------------------------------------------

    private Player chooseHero() {
        Player[] heroes = {
                new CodeKnight("Sir Compilealot"),
                new QuickCoder("Zippy the Quick"),
                new WiseScholar("Sage Mindwell"),
                new LuckyRookie("Dice the Lucky")
        };

        ConsoleUtil.printBanner("CHOOSE YOUR HERO");
        printHeroGrid(heroes);

        int choice = ConsoleUtil.readChoice("\n  Choose a hero (1-4): ", 1, 4);
        Player hero = heroes[choice - 1];

        System.out.println();
        System.out.println("  You chose " + hero.getClass().getSimpleName() + ": " + hero.getName());
        ConsoleUtil.pressEnter("  Press ENTER to begin your exams...");
        return hero;
    }

    /** Prints the four heroes in a 2x2 grid so the taller sprites fit on one screen. */
    private void printHeroGrid(Player[] heroes) {
        final int columnWidth = 34;

        String[][] arts = new String[heroes.length][];
        int artHeight = 0;
        for (int i = 0; i < heroes.length; i++) {
            arts[i] = heroes[i].getArt().split("\n");
            artHeight = Math.max(artHeight, arts[i].length);
        }

        for (int row = 0; row < heroes.length; row += 2) {
            Player left = heroes[row];
            Player right = heroes[row + 1];

            System.out.println();
            System.out.println(ConsoleUtil.padToWidth(
                    "  [" + (row + 1) + "] " + left.getClass().getSimpleName(), columnWidth)
                    + "  [" + (row + 2) + "] " + right.getClass().getSimpleName());

            for (int line = 0; line < artHeight; line++) {
                String leftLine = line < arts[row].length ? arts[row][line] : "";
                String rightLine = line < arts[row + 1].length ? arts[row + 1][line] : "";
                System.out.println(ConsoleUtil.padToWidth(leftLine, columnWidth) + rightLine);
            }

            System.out.println(ConsoleUtil.padToWidth(
                    "   HP " + left.getMaxHp() + "   ATK " + left.getAttackPower(), columnWidth)
                    + "   HP " + right.getMaxHp() + "   ATK " + right.getAttackPower());

            List<String> leftPerk = wrapText("   " + left.getPerkDescription(), columnWidth - 2);
            List<String> rightPerk = wrapText("   " + right.getPerkDescription(), columnWidth - 2);
            int perkLines = Math.max(leftPerk.size(), rightPerk.size());
            for (int line = 0; line < perkLines; line++) {
                String leftLine = line < leftPerk.size() ? leftPerk.get(line) : "";
                String rightLine = line < rightPerk.size() ? rightPerk.get(line) : "";
                System.out.println(ConsoleUtil.padToWidth(leftLine, columnWidth) + rightLine);
            }
        }
    }

    /** Breaks text on spaces so each line fits the given width, keeping the indent. */
    private List<String> wrapText(String text, int width) {
        int indentEnd = 0;
        while (indentEnd < text.length() && text.charAt(indentEnd) == ' ') {
            indentEnd++;
        }
        String indent = text.substring(0, indentEnd);
        String body = text.substring(indentEnd);

        List<String> lines = new ArrayList<>();
        String remaining = indent + body;
        while (remaining.length() > width) {
            int breakAt = remaining.lastIndexOf(' ', width);
            if (breakAt <= indent.length()) {
                breakAt = width;
            }
            lines.add(remaining.substring(0, breakAt));
            remaining = indent + remaining.substring(breakAt).trim();
        }
        lines.add(remaining);
        return lines;
    }

    // ------------------------------------------------------------------
    // Stage progression
    // ------------------------------------------------------------------

    private void playAllStages(Player player) {
        for (int stage = 1; stage <= TOTAL_STAGES; stage++) {
            boolean stageCleared = playStage(player, stage);
            if (!stageCleared) {
                return; // player was defeated: end this run
            }
            if (stage < TOTAL_STAGES) {
                restBetweenStages(player, stage);
            }
        }
    }

    /** Runs one stage: 3 normal students, then the professor boss. */
    private boolean playStage(Player player, int stage) {
        ConsoleUtil.printBanner("STAGE " + stage + " of " + TOTAL_STAGES
                + " - " + STAGE_TOPICS[stage - 1].toUpperCase());

        for (int i = 1; i <= STUDENTS_PER_STAGE; i++) {
            String studentName = STUDENT_NAMES[random.nextInt(STUDENT_NAMES.length)];
            if (!runBattle(player, new Student(studentName))) {
                return false;
            }
        }

        Professor boss = new Professor(PROFESSOR_NAMES[stage - 1], STAGE_TOPICS[stage - 1]);
        ConsoleUtil.printBanner("BOSS FIGHT: " + boss.getName()
                + " (subject: " + boss.getSubject() + ")");
        return runBattle(player, boss);
    }

    private boolean runBattle(Player player, Enemy enemy) {
        BattleManager battle = new BattleManager(player, enemy, questionBank);
        boolean playerWon = battle.run();

        if (playerWon) {
            player.onBattleWon();
            ConsoleUtil.pressEnter("  Press ENTER to continue...");
        }
        return playerWon;
    }

    private void restBetweenStages(Player player, int stageCleared) {
        int healed = player.getMaxHp() * HEAL_PERCENT_BETWEEN_STAGES / 100;
        player.heal(healed);
        ConsoleUtil.printBanner("STAGE " + stageCleared + " CLEARED! You rest and recover "
                + healed + " HP. (HP: " + player.getHp() + "/" + player.getMaxHp() + ")");
        ConsoleUtil.pressEnter("  Press ENTER for the next stage...");
    }

    // ------------------------------------------------------------------
    // End screens
    // ------------------------------------------------------------------

    private void printVictoryScreen(Player player) {
        ConsoleUtil.printBanner("VICTORY!");
        System.out.println("           ___________");
        System.out.println("          '._==_==_=._'");
        System.out.println("          .-\\:      /-.");
        System.out.println("         | (|:.     |) |");
        System.out.println("          '-|:.     |-'");
        System.out.println("            \\::.    /");
        System.out.println("             '::::.'");
        System.out.println("               )(");
        System.out.println("             _.'~~'._");
        System.out.println();
        System.out.println("  " + player.getName()
                + " defeated all four professors and graduates with honors!");
        System.out.println();
    }

    private void printDefeatScreen(Player player) {
        ConsoleUtil.printBanner("GAME OVER");
        System.out.println("        _______");
        System.out.println("       /       \\");
        System.out.println("      |  x   x  |");
        System.out.println("      |    L    |");
        System.out.println("       \\_______/");
        System.out.println();
        System.out.println("  " + player.getName()
                + " failed the semester. The professors showed no mercy.");
        System.out.println();
    }
}
