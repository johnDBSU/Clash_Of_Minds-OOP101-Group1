import java.util.Random;

/**
 * Fast hero: lower HP, but a 25% chance to land a CRITICAL answer
 * that deals double damage.
 */
public class QuickCoder extends Player {

    private static final int MAX_HP = 100;
    private static final int ATTACK_POWER = 20;
    private static final int CRIT_CHANCE_PERCENT = 25;
    private static final int CRIT_MULTIPLIER = 2;

    private final Random random = new Random();

    public QuickCoder(String name) {
        super(name, MAX_HP, ATTACK_POWER,
                "Critical Mind - 25% chance to deal double damage on a correct answer");
    }

    @Override
    public int getDamageRoll() {
        if (random.nextInt(100) < CRIT_CHANCE_PERCENT) {
            System.out.println("  >> CRITICAL ANSWER! Double damage!");
            return getAttackPower() * CRIT_MULTIPLIER;
        }
        return getAttackPower();
    }

    @Override
    public String getArt() {
        return ""
                + "          │             \n"
                + "        ╱^^^╲    ~   ~   \n"
                + "       │ o o │          \n"
                + "       │  >  │═══>>     \n"
                + "      ╱│=====│╲         \n"
                + "     ╱ │ ### │ ╲   ┌────┐\n"
                + "       │     │    │[##]│\n"
                + "       d     b    └────┘\n";
    }
}
