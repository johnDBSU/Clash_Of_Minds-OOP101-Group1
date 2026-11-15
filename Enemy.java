/**
 * Abstract base class for all enemies: students (normal) and
 * professors (bosses). Subclasses set their stats and art.
 */
public abstract class Enemy {

    private final String name;
    private final int maxHp;
    private final int attackPower;

    private int hp;

    protected Enemy(String name, int maxHp, int attackPower) {
        this.name = name;
        this.maxHp = maxHp;
        this.attackPower = attackPower;
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

    /** True when HP has dropped to 0 or below. */
    public boolean isDefeated() {
        return hp <= 0;
    }

    /** Applies damage from the player. */
    public void takeDamage(int damage) {
        hp -= damage;
    }

    /** Difficulty of the questions this enemy asks. */
    public abstract Question.Difficulty getQuestionDifficulty();

    /** Line the enemy says when the battle starts. */
    public abstract String getIntroLine();

    /** ASCII art shown when the battle starts. 8 lines, eyes on line 4. */
    public abstract String getArt();
}
