import java.util.Random;

/**
 * Runs a single battle between the player and one enemy.
 * Each round: answer a question to strike the enemy; a wrong answer
 * lets the enemy strike back. Also handles the Lucky Guess perk.
 */
public class BattleManager {

    private static final String ELIMINATED_TEXT = "X (eliminated by Lucky Guess)";
    private static final String PLAYER_LABEL = "YOU: ";

    private final Player player;
    private final Enemy enemy;
    private final QuestionBank questionBank;
    private final Random random = new Random();

    public BattleManager(Player player, Enemy enemy, QuestionBank questionBank) {
        this.player = player;
        this.enemy = enemy;
        this.questionBank = questionBank;
    }

    /** Runs the battle loop. Returns true if the player won. */
    public boolean run() {
        printBattleIntro();

        while (true) {
            ConsoleUtil.clearConsole(); // fresh screen for every new question
            printBattleStatus();

            playerAttackTurn();
            if (enemy.isDefeated()) {
                System.out.println();
                System.out.println("  *** " + enemy.getName() + " is defeated! ***");
                return true;
            }
            if (player.isDefeated()) {
                System.out.println();
                System.out.println("  *** " + player.getName() + " has run out of HP! ***");
                return false;
            }

            // keep the result on screen until the player is ready for the next question
            ConsoleUtil.pressEnter("  Press ENTER for the next question...");
        }
    }

    // ------------------------------------------------------------------
    // Battle phases
    // ------------------------------------------------------------------

    /** The player's turn: answer a question to strike, or get hit for failing. */
    private void playerAttackTurn() {
        Question question = questionBank.getRandomQuestion(enemy.getQuestionDifficulty());
        System.out.println();
        System.out.println("  --- YOUR TURN: answer correctly to strike! ---");

        int chosenIndex = askQuestionWithLuck(question);
        if (chosenIndex == question.getAnswerIndex()) {
            int damage = player.getDamageRoll();
            enemy.takeDamage(damage);
            System.out.println();
            System.out.println("  >> CORRECT! You strike " + enemy.getName() + " for " + damage + " damage!");
        } else {
            System.out.println();
            System.out.println("  >> WRONG! The correct answer was: " + question.getChoices()[question.getAnswerIndex()]);
            player.takeDamage(enemy.getAttackPower());
            System.out.println("  >> " + enemy.getName() + " strikes back! You take " + enemy.getAttackPower() + " damage.");
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
            int useLuck = ConsoleUtil.readChoice(
                    "  Use a Lucky Guess? (removes 2 wrong choices) 1=Yes 2=No: ", 1, 2);
            if (useLuck == 1) {
                player.useLuckCharge();
                eliminateTwoWrongChoices(question, choices);
                System.out.println("  Two wrong choices were eliminated!");
                displayQuestion(question, choices); // show the question again with X marks
            }
        }

        return ConsoleUtil.readChoice("  Your answer (1-4): ", 1, 4) - 1;
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

    /** Prints the question and its choices inside a box frame. */
    private void displayQuestion(Question question, String[] choices) {
        String[] lines = new String[1 + choices.length];
        lines[0] = "Q: " + question.getText();
        for (int i = 0; i < choices.length; i++) {
            lines[1 + i] = "   " + (i + 1) + ") " + choices[i];
        }
        ConsoleUtil.printBox(lines);
    }

    // ------------------------------------------------------------------
    // Battle display
    // ------------------------------------------------------------------

    private void printBattleIntro() {
        System.out.println();
        ConsoleUtil.printLine();
        System.out.print(enemy.getArt());
        System.out.println("  " + enemy.getName() + " " + enemy.getIntroLine());
        ConsoleUtil.printLine();
        ConsoleUtil.pressEnter("  Press ENTER to start the battle...");
    }

    private void printBattleStatus() {
        String[] playerArt = artLines(player.getArt());
        String[] enemyArt = artLines(enemy.getArt());
        int artRows = Math.max(playerArt.length, enemyArt.length);

        // build the arena: hero art on the left, enemy art on the right, bars below
        String[] lines = new String[artRows + 4];
        lines[0] = ConsoleUtil.padToWidth(PLAYER_LABEL + player.getName(), 26)
                + "vs " + enemy.getName();
        for (int i = 0; i < artRows; i++) {
            String leftSide = (i < playerArt.length) ? playerArt[i] : "";
            String rightSide = (i < enemyArt.length) ? enemyArt[i] : "";
            lines[1 + i] = ConsoleUtil.padToWidth(leftSide, 26) + rightSide;
        }
        lines[artRows + 1] = "";
        lines[artRows + 2] = ConsoleUtil.buildHpBar(
                PLAYER_LABEL + player.getName(), player.getHp(), player.getMaxHp());
        lines[artRows + 3] = ConsoleUtil.buildHpBar(enemy.getName(), enemy.getHp(), enemy.getMaxHp());
        ConsoleUtil.printBox(lines);
    }

    /** Splits art into lines. split() already discards the trailing empty line. */
    private String[] artLines(String art) {
        return art.split("\n");
    }
}
