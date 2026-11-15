/**
 * Abstract base class for all playable heroes.
 *
 * It stores the shared stats (HP, attack power) and defines the "perk hooks"
 * that subclasses override to change how the hero behaves in battle:
 *
 *   getDamageRoll()  - damage dealt on a correct answer  (QuickCoder: crits)
 *   takeDamage()     - how much damage the hero receives (CodeKnight: reduce)
 *   onBattleWon()    - bonus effect after a won battle   (WiseScholar: heals)
 *   canUseLuck()     - ability to remove wrong choices   (LuckyRookie: luck)
 */
public abstract class Player {

    private final String name;
    private final int maxHp;
    private final int attackPower;
    private final String perkDescription;

    private int hp;

    protected Player(String name, int maxHp, int attackPower, String perkDescription) {
        this.name = name;
        this.maxHp = maxHp;
        this.attackPower = attackPower;
        this.perkDescription = perkDescription;
        this.hp = maxHp;
    }

    public String getName() {
        return name;
    }

    public int getHp() {
        return hp;
    }

    public int getMaxHp() {
        return maxHp;
    }

    public int getAttackPower() {
        return attackPower;
    }

    public String getPerkDescription() {
        return perkDescription;
    }

    /** True when HP has dropped to 0 or below. */
    public boolean isDefeated() {
        return hp <= 0;
    }

    /** Restores HP, capped at maxHp. */
    public void heal(int amount) {
        hp = Math.min(hp + amount, maxHp);
    }

    /** Damage dealt on one correct answer. Default: flat attack power. */
    public int getDamageRoll() {
        return attackPower;
    }

    /** Applies damage to this hero. Default: the full amount. */
    public void takeDamage(int rawDamage) {
        hp -= rawDamage;
    }

    /** Bonus effect after winning a battle. Default: none. */
    public void onBattleWon() {
        // most heroes have no post-battle effect
    }

    /** Whether this hero may use the "Lucky Guess" ability. Default: no. */
    public boolean canUseLuck() {
        return false;
    }

    /** Spends one "Lucky Guess" charge. Default: nothing to spend. */
    public void useLuckCharge() {
        // most heroes have no luck charges
    }

    /** ASCII art shown on the hero select screen. 8 lines, eyes on line 4. */
    public abstract String getArt();

    /** Prints the hero's art, stats and perk on the select screen. */
    public void printProfile() {
        System.out.println(getArt());
        System.out.println("     HP: " + maxHp + "   Attack: " + attackPower);
        System.out.println("     Perk: " + perkDescription);
    }
}
