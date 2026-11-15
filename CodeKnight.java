/**
 * Tanky hero: biggest HP pool and takes 30% less damage from every hit.
 */
public class CodeKnight extends Player {

    private static final int MAX_HP = 140;
    private static final int ATTACK_POWER = 22;
    private static final int DAMAGE_REDUCTION_PERCENT = 30;

    public CodeKnight(String name) {
        super(name, MAX_HP, ATTACK_POWER,
                "Iron Focus - takes 30% less damage from every hit");
    }

    @Override
    public void takeDamage(int rawDamage) {
        int reducedDamage = rawDamage * (100 - DAMAGE_REDUCTION_PERCENT) / 100;
        super.takeDamage(reducedDamage);
    }

    @Override
    public String getArt() {
        return ""
                + "         ^             \n"
                + "        / \\            \n"
                + "       /___\\     +-----+\n"
                + "      | o o |    |#####|\n"
                + "      |  -  |    |#####|\n"
                + "     /|=====|\\   |#####|\n"
                + "    / | ### | \\  +-----+\n"
                + "      d     b          \n";
    }
}
