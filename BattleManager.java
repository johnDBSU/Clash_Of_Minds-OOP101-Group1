import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Runs a single battle between the player and one enemy.
 * Each round: answer a question to strike the enemy; a wrong answer
 * lets the enemy strike back. Also handles the Lucky Guess perk.
 */
public class BattleManager {

    private static final String ELIMINATED_TEXT = "X (eliminated by Lucky Guess)";
    private static final String GAME_TITLE = "C L A S H   O F   M I N D S";
    private static final int LEFT_PANE_WIDTH = 39;
    private static final int RIGHT_PANE_WIDTH = ConsoleUtil.BOX_WIDTH - LEFT_PANE_WIDTH - 3; // 40
    private static final String PANE_DIVIDER = ConsoleUtil.ANSI_RESET + ConsoleUtil.FG_MUTED + " │ " + ConsoleUtil.ANSI_RESET;

    private final Player player;
    private final Enemy enemy;
    private final QuestionBank questionBank;
    private final String stageLabel;
    private final Random random = new Random();

    public BattleManager(Player player, Enemy enemy, QuestionBank questionBank) {
        this(player, enemy, questionBank, "");
    }

    public BattleManager(Player player, Enemy enemy, QuestionBank questionBank, String stageLabel) {
        this.player = player;
        this.enemy = enemy;
        this.questionBank = questionBank;
        this.stageLabel = stageLabel;
    }

    /** Runs the battle loop. Returns true if the player won. */
    public boolean run() {
        printBattleIntro();

        while (true) {
            ConsoleUtil.clearConsole();
            ConsoleUtil.println();
            printBattleStatus();

            playerAttackTurn();
            if (enemy.isDefeated()) {
                ConsoleUtil.println();
                ConsoleUtil.printDoubleBox(
                        ConsoleUtil.FG_GREEN + ConsoleUtil.ANSI_BOLD + "VICTORY" + ConsoleUtil.ANSI_RESET,
                        new String[]{
                            ConsoleUtil.FG_GREEN + ConsoleUtil.ANSI_BOLD
                                + ConsoleUtil.centerLine("★  " + enemy.getName() + " is defeated!  ★")
                        },
                        ConsoleUtil.FG_GREEN);
                return true;
            }
            if (player.isDefeated()) {
                ConsoleUtil.println();
                ConsoleUtil.printDoubleBox(
                        ConsoleUtil.FG_RED + ConsoleUtil.ANSI_BOLD + "DEFEATED" + ConsoleUtil.ANSI_RESET,
                        new String[]{
                            ConsoleUtil.FG_RED + ConsoleUtil.ANSI_BOLD
                                + ConsoleUtil.centerLine("✖  " + player.getName() + " has run out of HP!  ✖")
                        },
                        ConsoleUtil.FG_RED);
                return false;
            }

            ConsoleUtil.println();
            ConsoleUtil.pressEnter("  Press ENTER for the next question... ");
        }
    }

    // ------------------------------------------------------------------
    // Battle phases
    // ------------------------------------------------------------------

    /** The player's turn: answer a question to strike, or get hit for failing. */
    private void playerAttackTurn() {
        Question question = questionBank.getRandomQuestion(enemy.getQuestionDifficulty());
        ConsoleUtil.println();

        int chosenIndex = askQuestionWithLuck(question);
        ConsoleUtil.println();
        if (chosenIndex == question.getAnswerIndex()) {
            int damage = player.getDamageRoll();
            enemy.takeDamage(damage);
            ConsoleUtil.printBox(null, new String[]{
                ConsoleUtil.FG_GREEN + ConsoleUtil.ANSI_BOLD
                    + ConsoleUtil.centerLine("✓ CORRECT!  You strike " + enemy.getName() + " for " + damage + " damage!")
            }, ConsoleUtil.FG_GREEN);
        } else {
            int hpBefore = player.getHp();
            player.takeDamage(enemy.getAttackPower());
            int damageTaken = hpBefore - player.getHp();
            String correct = question.getChoices()[question.getAnswerIndex()];
            ConsoleUtil.printBox(null, new String[]{
                ConsoleUtil.FG_RED + ConsoleUtil.ANSI_BOLD
                    + ConsoleUtil.centerLine("✖ WRONG!  Correct answer: " + correct),
                ConsoleUtil.FG_YELLOW
                    + ConsoleUtil.centerLine(enemy.getName() + " strikes back! You take " + damageTaken + " damage.")
            }, ConsoleUtil.FG_RED);
        }
    }

    // ------------------------------------------------------------------
    // Question display
    // ------------------------------------------------------------------

    /** Shows the question, offers Lucky Guess to LuckyRookie, returns chosen index 0-3. */
    private int askQuestionWithLuck(Question question) {
        String[] choices = question.getChoices().clone();
        displayQuestion(question, choices);

        if (player.canUseLuck()) {
            ConsoleUtil.println();
            int useLuck = ConsoleUtil.readChoice(
                    "  ★ Use a Lucky Guess? (removes 2 wrong choices) [1=Yes / 2=No] > ", 1, 2);
            if (useLuck == 1) {
                player.useLuckCharge();
                eliminateTwoWrongChoices(question, choices);
                ConsoleUtil.println(ConsoleUtil.FG_GOLD + ConsoleUtil.ANSI_BOLD
                        + "  ★ Lucky Guess activated! Two wrong choices eliminated.");
                displayQuestion(question, choices);
            }
        }

        ConsoleUtil.println();
        return ConsoleUtil.readChoice("  ▸ Your answer (1-4) > ", 1, 4) - 1;
    }

    /** Replaces two random wrong choices with the eliminated text. */
    private void eliminateTwoWrongChoices(Question question, String[] choices) {
        int eliminated = 0;
        while (eliminated < 2) {
            int index = random.nextInt(choices.length);
            if (index != question.getAnswerIndex() && !choices[index].equals(ELIMINATED_TEXT)) {
                choices[index] = ELIMINATED_TEXT;
                eliminated++;
            }
        }
    }

    /** Prints the question and its choices inside a clean word-wrapped card. */
    private void displayQuestion(Question question, String[] choices) {
        List<String> lines = new ArrayList<>();
        List<String> qWrapped = ConsoleUtil.wrapPlain("Q: " + question.getText(), ConsoleUtil.BOX_WIDTH - 4);
        for (String qLine : qWrapped) {
            lines.add("  " + ConsoleUtil.FG_WHITE + ConsoleUtil.ANSI_BOLD + qLine);
        }
        lines.add(ConsoleUtil.FG_MUTED + "  " + ConsoleUtil.repeat('─', ConsoleUtil.BOX_WIDTH - 4));

        for (int i = 0; i < choices.length; i++) {
            boolean eliminated = choices[i].equals(ELIMINATED_TEXT);
            if (eliminated) {
                lines.add("  " + ConsoleUtil.FG_MUTED + " [" + (i + 1) + "] " + ConsoleUtil.FG_RED + "✖ " + ELIMINATED_TEXT);
            } else {
                String badge = ConsoleUtil.FG_CYAN + ConsoleUtil.ANSI_BOLD + "[" + (i + 1) + "]" + ConsoleUtil.ANSI_RESET;
                List<String> cWrapped = ConsoleUtil.wrapPlain(choices[i], ConsoleUtil.BOX_WIDTH - 10);
                for (int w = 0; w < cWrapped.size(); w++) {
                    if (w == 0) {
                        lines.add("   " + badge + " " + ConsoleUtil.FG_WHITE + cWrapped.get(w));
                    } else {
                        lines.add("       " + ConsoleUtil.FG_WHITE + cWrapped.get(w));
                    }
                }
            }
        }

        String title = ConsoleUtil.FG_GOLD + ConsoleUtil.ANSI_BOLD + "YOUR TURN • ANSWER TO STRIKE" + ConsoleUtil.ANSI_RESET;
        ConsoleUtil.printBox(title, lines.toArray(new String[0]), ConsoleUtil.FG_BLUE);
    }

    // ------------------------------------------------------------------
    // Battle display
    // ------------------------------------------------------------------

    private void printBattleIntro() {
        ConsoleUtil.clearConsole();
        ConsoleUtil.println();

        boolean isBoss = enemy instanceof Professor;
        String borderColor = isBoss ? ConsoleUtil.FG_RED : ConsoleUtil.FG_CYAN;
        String badge = isBoss
                ? ConsoleUtil.FG_RED + ConsoleUtil.ANSI_BOLD + "★ BOSS ENCOUNTER ★" + ConsoleUtil.ANSI_RESET
                : ConsoleUtil.FG_GOLD + ConsoleUtil.ANSI_BOLD + "ENCOUNTER" + ConsoleUtil.ANSI_RESET;

        String[] enemyArt = artLines(enemy.getArt());
        List<String> lines = new ArrayList<>();
        lines.add(ConsoleUtil.FG_MUTED + ConsoleUtil.centerLine(stageLabel));
        lines.add("");
        for (String row : enemyArt) {
            lines.add(ConsoleUtil.centerLine(row) + ConsoleUtil.ANSI_RESET);
        }
        lines.add("");
        lines.add(ConsoleUtil.centerLine(
                (isBoss ? ConsoleUtil.FG_RED : ConsoleUtil.FG_GOLD) + ConsoleUtil.ANSI_BOLD
                        + enemy.getName() + ConsoleUtil.ANSI_RESET
                        + ConsoleUtil.FG_MUTED + "  [" + enemy.getQuestionDifficulty() + " • HP "
                        + enemy.getMaxHp() + " • ATK " + enemy.getAttackPower() + "]"));

        List<String> taunt = ConsoleUtil.wrapPlain(enemy.getIntroLine(), ConsoleUtil.BOX_WIDTH - 8);
        for (String t : taunt) {
            lines.add(ConsoleUtil.FG_WHITE + ConsoleUtil.centerLine(t));
        }
        lines.add("");

        ConsoleUtil.printDoubleBox(badge, lines.toArray(new String[0]), borderColor);
        ConsoleUtil.println();
        ConsoleUtil.pressEnter("  Press ENTER to start the battle... ");
    }

    private void printBattleStatus() {
        String[] playerArt = artLines(player.getArt());
        String[] enemyArt = artLines(enemy.getArt());
        int artRows = Math.max(playerArt.length, enemyArt.length);

        boolean isBoss = enemy instanceof Professor;
        String diffColor = (enemy.getQuestionDifficulty() == Question.Difficulty.HARD)
                ? ConsoleUtil.FG_RED : ConsoleUtil.FG_GREEN;

        String[] lines = new String[artRows + 5];
        lines[0] = ConsoleUtil.FG_GOLD + ConsoleUtil.ANSI_BOLD + ConsoleUtil.centerLine(stageLabel);
        lines[1] = ConsoleUtil.FG_MUTED + ConsoleUtil.repeat('─', ConsoleUtil.BOX_WIDTH);

        String leftHeader = ConsoleUtil.FG_CYAN + ConsoleUtil.ANSI_BOLD + "YOU: " + ConsoleUtil.FG_WHITE + player.getName();
        String rightHeader = (isBoss ? ConsoleUtil.FG_RED : ConsoleUtil.FG_YELLOW) + ConsoleUtil.ANSI_BOLD
                + enemy.getName() + " " + diffColor + "[" + enemy.getQuestionDifficulty() + "]";
        lines[2] = ConsoleUtil.centerInWidth(leftHeader, LEFT_PANE_WIDTH)
                + PANE_DIVIDER + ConsoleUtil.centerInWidth(rightHeader, RIGHT_PANE_WIDTH);

        for (int i = 0; i < artRows; i++) {
            String leftSide = (i < playerArt.length) ? playerArt[i] : "";
            String rightSide = (i < enemyArt.length) ? enemyArt[i] : "";
            lines[3 + i] = ConsoleUtil.centerInWidth(leftSide, LEFT_PANE_WIDTH)
                    + PANE_DIVIDER + ConsoleUtil.centerInWidth(rightSide, RIGHT_PANE_WIDTH);
        }

        lines[artRows + 3] = ConsoleUtil.FG_MUTED + ConsoleUtil.repeat('─', LEFT_PANE_WIDTH)
                + "─┼─" + ConsoleUtil.repeat('─', RIGHT_PANE_WIDTH);

        String leftHp = ConsoleUtil.FG_MUTED + "HP " + ConsoleUtil.buildBar(player.getHp(), player.getMaxHp());
        String rightHp = ConsoleUtil.FG_MUTED + "HP " + ConsoleUtil.buildBar(enemy.getHp(), enemy.getMaxHp());
        lines[artRows + 4] = ConsoleUtil.centerInWidth(leftHp, LEFT_PANE_WIDTH)
                + PANE_DIVIDER + ConsoleUtil.centerInWidth(rightHp, RIGHT_PANE_WIDTH);

        String title = ConsoleUtil.FG_GOLD + ConsoleUtil.ANSI_BOLD + GAME_TITLE + ConsoleUtil.ANSI_RESET;
        ConsoleUtil.printDoubleBox(title, lines, isBoss ? ConsoleUtil.FG_RED : ConsoleUtil.FG_CYAN);
    }

    /** Splits art into lines. split() already discards the trailing empty line. */
    private String[] artLines(String art) {
        return art.split("\n");
    }
}
