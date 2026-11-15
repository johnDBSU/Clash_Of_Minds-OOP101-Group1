/**
 * Lucky hero: normal stats, plus 3 "Lucky Guess" charges that can remove
 * two wrong choices from any question during a battle.
 */
public class LuckyRookie extends Player {

    private static final int MAX_HP = 100;
    private static final int ATTACK_POWER = 20;
    private static final int TOTAL_LUCK_CHARGES = 3;

    private int luckCharges = TOTAL_LUCK_CHARGES;

    public LuckyRookie(String name) {
        super(name, MAX_HP, ATTACK_POWER,
                "Lucky Guess - 3 charges per run: remove 2 wrong choices from a question");
    }

    public int getLuckCharges() {
        return luckCharges;
    }

    @Override
    public boolean canUseLuck() {
        return luckCharges > 0;
    }

    @Override
    public void useLuckCharge() {
        if (luckCharges > 0) {
            luckCharges--;
        }
    }

    @Override
    public String getArt() {
        return ""
                + "       * * *          \n"
                + "        \\|/           \n"
                + "         |      +----+\n"
                + "      | o o |   | o  |\n"
                + "      |  w  |   |  o |\n"
                + "     /|=====|\\  +----+\n"
                + "    / | ### | \\        \n"
                + "      |_____|          \n";
    }
}
