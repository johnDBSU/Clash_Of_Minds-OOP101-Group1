import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Controls the whole game aka. the actual main code. It has hero select, the 4 stages which is 3 students + 1
 * professor each stage, healing between stages and the win/lose screens.
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
            "Chad the Crammer", "Mika the Auditory Learner", "Ravi the Rote-Learner",
            "Tessa the Terminologist", "Ben the Blueprint Maker", "Nina the Notetaker",
            "Omar the Overconfident", "Lia the Last-Minute"
    };

    private final QuestionBank questionBank = new QuestionBank();
    private final Random random = new Random();

    /** Starts the game, offering retries after each defeat. */
    public void start() {
        ConsoleUtil.printGameTitle();
        ConsoleUtil.pressEnter("  Press ENTER to choose your hero... ");

        while (true) {
            Player player = chooseHero();

            playAllStages(player);

            if (!player.isDefeated()) {
                printVictoryScreen(player);
                return;
            }

            printDefeatScreen(player);
            int choice = ConsoleUtil.readChoice("  [1] Try again   [2] Quit  > ", 1, 2);
            if (choice == 2) {
                ConsoleUtil.println(ConsoleUtil.FG_CYAN + "  Thanks for playing Clash of Minds!");
                return;
            }
        }
    }

    // ------------------------------------------------------------------
    // Hero selection
    // ------------------------------------------------------------------

    private Player chooseHero() {
        Player[] heroes = {
                new CodeKnight("Sir Compile-lot"),
                new QuickCoder("Zippy the Quick"),
                new WiseScholar("Sage Mindwell"),
                new LuckyRookie("Dice the Lucky")
        };

        ConsoleUtil.clearConsole();
        ConsoleUtil.printBanner("CHOOSE YOUR HERO", ConsoleUtil.FG_GOLD);
        printHeroGrid(heroes);

        ConsoleUtil.println();
        int choice = ConsoleUtil.readChoice("  ▸ Choose a hero (1-4) > ", 1, 4);
        Player hero = heroes[choice - 1];

        ConsoleUtil.println();
        ConsoleUtil.printBox(null, new String[]{
            ConsoleUtil.FG_GREEN + ConsoleUtil.ANSI_BOLD
                + ConsoleUtil.centerLine("Selected: " + hero.getClass().getSimpleName() + " — " + hero.getName())
        }, ConsoleUtil.FG_GREEN);
        ConsoleUtil.println();
        ConsoleUtil.pressEnter("  Press ENTER to begin your exams... ");
        return hero;
    }

    /** Prints heroes side-by-side (1x4) in fullscreen so everything fits without scrolling; falls back to 2x2 on narrow terminals. */
    private void printHeroGrid(Player[] heroes) {
        final String[] accents = {
            ConsoleUtil.FG_CYAN, ConsoleUtil.FG_GREEN, ConsoleUtil.FG_PURPLE, ConsoleUtil.FG_GOLD
        };

        int termCols = ConsoleUtil.getTermCols();
        boolean singleRow = termCols >= 151;
        int cardsPerRow = singleRow ? 4 : 2;
        int cardInner = singleRow ? (termCols >= 160 ? 36 : 35) : 38;
        String gutter = singleRow ? (termCols >= 160 ? "  " : " ") : "    ";
        int totalRowWidth = cardsPerRow * (cardInner + 2) + (cardsPerRow - 1) * gutter.length();
        String rowMargin = ConsoleUtil.repeat(' ', Math.max(0, (termCols - totalRowWidth) / 2));

        String[][] arts = new String[heroes.length][];
        int artHeight = 0;
        for (int i = 0; i < heroes.length; i++) {
            String[] raw = heroes[i].getArt().split("\n");
            arts[i] = new String[raw.length];
            for (int j = 0; j < raw.length; j++) {
                arts[i][j] = ConsoleUtil.centerInWidth(ConsoleUtil.trimTrailingVisible(raw[j]), cardInner)
                        + ConsoleUtil.ANSI_RESET;
            }
            artHeight = Math.max(artHeight, raw.length);
        }

        for (int start = 0; start < heroes.length; start += cardsPerRow) {
            int end = Math.min(start + cardsPerRow, heroes.length);

            if (singleRow) {
                ConsoleUtil.println();
            }

            // Card top borders
            StringBuilder topRow = new StringBuilder();
            for (int i = start; i < end; i++) {
                if (i > start) topRow.append(gutter);
                String title = " [" + (i + 1) + "] " + heroes[i].getClass().getSimpleName() + " ";
                topRow.append(cardTop(title, cardInner, accents[i]));
            }
            System.out.println(rowMargin + topRow + ConsoleUtil.ANSI_RESET);

            // Sprite rows
            for (int line = 0; line < artHeight; line++) {
                StringBuilder sb = new StringBuilder();
                for (int i = start; i < end; i++) {
                    if (i > start) sb.append(gutter);
                    String art = (line < arts[i].length) ? arts[i][line] : ConsoleUtil.repeat(' ', cardInner);
                    sb.append(accents[i]).append("│").append(ConsoleUtil.ANSI_RESET)
                            .append(art)
                            .append(accents[i]).append("│").append(ConsoleUtil.ANSI_RESET);
                }
                System.out.println(rowMargin + sb + ConsoleUtil.ANSI_RESET);
            }

            // Stats row
            StringBuilder statsRow = new StringBuilder();
            for (int i = start; i < end; i++) {
                if (i > start) statsRow.append(gutter);
                String stats = ConsoleUtil.FG_GREEN + ConsoleUtil.ANSI_BOLD + "HP " + heroes[i].getMaxHp()
                        + ConsoleUtil.FG_MUTED + "  •  "
                        + ConsoleUtil.FG_YELLOW + ConsoleUtil.ANSI_BOLD + "ATK " + heroes[i].getAttackPower();
                statsRow.append(accents[i]).append("│").append(ConsoleUtil.ANSI_RESET)
                        .append(ConsoleUtil.centerInWidth(stats, cardInner))
                        .append(accents[i]).append("│").append(ConsoleUtil.ANSI_RESET);
            }
            System.out.println(rowMargin + statsRow + ConsoleUtil.ANSI_RESET);

            // Wrapped perk rows
            List<List<String>> perks = new ArrayList<>();
            int maxPerkLines = 0;
            for (int i = start; i < end; i++) {
                List<String> wrapped = ConsoleUtil.wrapPlain(heroes[i].getPerkDescription(), cardInner - 2);
                perks.add(wrapped);
                maxPerkLines = Math.max(maxPerkLines, wrapped.size());
            }
            for (int line = 0; line < maxPerkLines; line++) {
                StringBuilder sb = new StringBuilder();
                for (int idx = 0; idx < perks.size(); idx++) {
                    int i = start + idx;
                    if (idx > 0) sb.append(gutter);
                    String text = (line < perks.get(idx).size()) ? ConsoleUtil.FG_MUTED + perks.get(idx).get(line) : "";
                    sb.append(accents[i]).append("│").append(ConsoleUtil.ANSI_RESET)
                            .append(ConsoleUtil.centerInWidth(text, cardInner))
                            .append(accents[i]).append("│").append(ConsoleUtil.ANSI_RESET);
                }
                System.out.println(rowMargin + sb + ConsoleUtil.ANSI_RESET);
            }

            // Card bottom borders
            StringBuilder botRow = new StringBuilder();
            for (int i = start; i < end; i++) {
                if (i > start) botRow.append(gutter);
                botRow.append(accents[i]).append("└").append(ConsoleUtil.repeat('─', cardInner))
                        .append("┘").append(ConsoleUtil.ANSI_RESET);
            }
            System.out.println(rowMargin + botRow + ConsoleUtil.ANSI_RESET);
        }
    }

    private String cardTop(String title, int innerWidth, String color) {
        int leftLen = Math.max(0, (innerWidth - title.length()) / 2);
        int rightLen = Math.max(0, innerWidth - title.length() - leftLen);
        return color + "┌" + ConsoleUtil.repeat('─', leftLen)
                + ConsoleUtil.ANSI_BOLD + ConsoleUtil.FG_WHITE + title + ConsoleUtil.ANSI_RESET
                + color + ConsoleUtil.repeat('─', rightLen) + "┐" + ConsoleUtil.ANSI_RESET;
    }

    // ------------------------------------------------------------------
    // Stage progression
    // ------------------------------------------------------------------

    private void playAllStages(Player player) {
        for (int stage = 1; stage <= TOTAL_STAGES; stage++) {
            boolean stageCleared = playStage(player, stage);
            if (!stageCleared) {
                return;
            }
            if (stage < TOTAL_STAGES) {
                restBetweenStages(player, stage);
            }
        }
    }

    /** Runs one stage: 3 normal students, then the professor boss. */
    private boolean playStage(Player player, int stage) {
        ConsoleUtil.clearConsole();
        ConsoleUtil.printBanner("STAGE " + stage + " OF " + TOTAL_STAGES
                + "  •  " + STAGE_TOPICS[stage - 1].toUpperCase(), ConsoleUtil.FG_CYAN);
        ConsoleUtil.println();
        ConsoleUtil.pressEnter("  Press ENTER to enter the exam hall... ");

        List<String> pool = new ArrayList<>();
        for (String s : STUDENT_NAMES) {
            pool.add(s);
        }
        List<String> spritePool = new ArrayList<>();
        spritePool.add("student");
        spritePool.add("student_2");
        spritePool.add("student_3");
        spritePool.add("student_4");
        for (int i = 1; i <= STUDENTS_PER_STAGE; i++) {
            String studentName = pool.remove(random.nextInt(pool.size()));
            String spriteId = spritePool.remove(random.nextInt(spritePool.size()));
            String battleTag = "BATTLE " + i + "/" + (STUDENTS_PER_STAGE + 1);
            if (!runBattle(player, new Student(studentName, spriteId), stage, battleTag)) {
                return false;
            }
        }

        Professor boss = new Professor(PROFESSOR_NAMES[stage - 1], STAGE_TOPICS[stage - 1]);
        ConsoleUtil.clearConsole();
        ConsoleUtil.printBanner("★ BOSS FIGHT: " + boss.getName().toUpperCase()
                + " (" + boss.getSubject() + ") ★", ConsoleUtil.FG_RED);
        ConsoleUtil.println();
        ConsoleUtil.pressEnter("  Press ENTER to face the professor... ");
        String bossTag = "★ BOSS (" + (STUDENTS_PER_STAGE + 1) + "/" + (STUDENTS_PER_STAGE + 1) + ")";
        return runBattle(player, boss, stage, bossTag);
    }

    private boolean runBattle(Player player, Enemy enemy, int stage, String battleTag) {
        String stageLabel = "STAGE " + stage + "/" + TOTAL_STAGES + "  •  "
                + STAGE_TOPICS[stage - 1].toUpperCase() + "  •  " + battleTag;
        BattleManager battle = new BattleManager(player, enemy, questionBank, stageLabel);
        boolean playerWon = battle.run();

        if (playerWon) {
            player.onBattleWon();
            ConsoleUtil.println();
            ConsoleUtil.pressEnter("  Press ENTER to continue... ");
        }
        return playerWon;
    }

    private void restBetweenStages(Player player, int stageCleared) {
        int healed = player.getMaxHp() * HEAL_PERCENT_BETWEEN_STAGES / 100;
        player.heal(healed);
        ConsoleUtil.clearConsole();
        ConsoleUtil.printDoubleBox(
                ConsoleUtil.FG_GREEN + ConsoleUtil.ANSI_BOLD + "STAGE " + stageCleared + " CLEARED" + ConsoleUtil.ANSI_RESET,
                new String[]{
                    "",
                    ConsoleUtil.FG_GREEN + ConsoleUtil.ANSI_BOLD
                        + ConsoleUtil.centerLine("★ You rest between exams and recover " + healed + " HP! ★"),
                    "",
                    ConsoleUtil.centerLine(ConsoleUtil.FG_MUTED + "Current HP: " + ConsoleUtil.buildBar(player.getHp(), player.getMaxHp())),
                    ""
                },
                ConsoleUtil.FG_GREEN);
        ConsoleUtil.println();
        ConsoleUtil.pressEnter("  Press ENTER for the next stage... ");
    }

    // ------------------------------------------------------------------
    // End screens
    // ------------------------------------------------------------------

    private void printVictoryScreen(Player player) {
        ConsoleUtil.clearConsole();
        String fallback = ""
                + "  ___________\n"
                + " '._==_==_=._'\n"
                + " .-\\:      /-.\n"
                + "| (|:.     |) |\n"
                + " '-|:.     |-'\n"
                + "   \\::.    /\n"
                + "    '::::.'\n"
                + "      )(\n"
                + "    _.'~~'._\n";
        String art = SpriteLoader.load("victory_icon", fallback);
        List<String> lines = new ArrayList<>();
        lines.add("");
        for (String row : art.split("\n")) {
            lines.add(ConsoleUtil.centerLine(row) + ConsoleUtil.ANSI_RESET);
        }
        lines.add("");
        lines.add(ConsoleUtil.FG_GREEN + ConsoleUtil.ANSI_BOLD
                + ConsoleUtil.centerLine(player.getName() + " defeated all four professors and graduates with honors!"));
        lines.add("");
        ConsoleUtil.printDoubleBox(
                ConsoleUtil.FG_GOLD + ConsoleUtil.ANSI_BOLD + "★ VICTORY • GRADUATED ★" + ConsoleUtil.ANSI_RESET,
                lines.toArray(new String[0]), ConsoleUtil.FG_GOLD);
        ConsoleUtil.println();
    }

    private void printDefeatScreen(Player player) {
        ConsoleUtil.clearConsole();
        String fallback = ""
                + "  _______\n"
                + " /       \\\n"
                + "|  x   x  |\n"
                + "|    L    |\n"
                + " \\_______/\n";
        String art = SpriteLoader.load("gameover_icon", fallback);
        List<String> lines = new ArrayList<>();
        lines.add("");
        for (String row : art.split("\n")) {
            lines.add(ConsoleUtil.centerLine(row) + ConsoleUtil.ANSI_RESET);
        }
        lines.add("");
        lines.add(ConsoleUtil.FG_WHITE + ConsoleUtil.ANSI_BOLD
                + ConsoleUtil.centerLine(player.getName() + " failed the semester. The professors showed no mercy."));
        lines.add("");
        ConsoleUtil.printDoubleBox(
                ConsoleUtil.FG_RED + ConsoleUtil.ANSI_BOLD + "✖ GAME OVER ✖" + ConsoleUtil.ANSI_RESET,
                lines.toArray(new String[0]), ConsoleUtil.FG_RED);
        ConsoleUtil.println();
    }
}
