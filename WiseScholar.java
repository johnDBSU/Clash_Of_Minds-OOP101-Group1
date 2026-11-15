/**
 * Smart hero: medium stats and recovers HP after every won battle.
 */
public class WiseScholar extends Player {

    private static final int MAX_HP = 110;
    private static final int ATTACK_POWER = 20;
    private static final int HEAL_PERCENT = 20;

    public WiseScholar(String name) {
        super(name, MAX_HP, ATTACK_POWER,
                "Deep Focus - recovers 20% of max HP after every won battle");
    }

    @Override
    public void onBattleWon() {
        int healed = getMaxHp() * HEAL_PERCENT / 100;
        heal(healed);
        System.out.println("  >> " + getName() + " meditates and recovers " + healed + " HP.");
    }

    @Override
    public String getArt() {
        return ""
                + "         ^             \n"
                + "        / \\            \n"
                + "       /___\\     +---+\n"
                + "      | o o |    | = |\n"
                + "      |  _  |    | = |\n"
                + "     /|=====|\\   | = |\n"
                + "    / | ### | \\  +---+\n"
                + "      |_____|          \n";
    }
}
