# Clash of Minds

A terminal quiz-battler written in Java. Four stages, four professors, one semester.

*OOP101 - Group 1*

Answer Java and OOP questions to damage your opponent. A wrong answer lets them hit back. Clear three students and a professor in each stage, then graduate.

## Requirements

- JDK 8 or newer
- Any terminal; the game uses plain ASCII, no external libraries

## Run

From the project root:

```bash
javac *.java
java Main
```

## Controls

| Input | Action |
|---|---|
| `1` - `4` | Pick a hero, answer a question, or choose a menu option |
| `1` / `2` | Accept or decline the Lucky Guess prompt |
| `ENTER` | Continue past a battle or stage screen |

## How a run works

1. Choose one of four heroes.
2. Each stage contains three random students, then a professor.
3. Students ask easy questions; professors ask hard questions.
4. A correct answer deals your attack damage. A wrong answer costs you the enemy's attack power.
5. Clearing a stage restores 30% of your max HP.
6. Defeat all four professors to win. Lose all HP and the run ends, with the option to retry.

## Heroes

| Hero | HP | ATK | Perk |
|---|---:|---:|---|
| CodeKnight | 140 | 22 | Iron Focus - takes 30% less damage from every hit |
| QuickCoder | 100 | 20 | Critical Mind - 25% chance to deal double damage |
| WiseScholar | 110 | 20 | Deep Focus - recovers 20% of max HP after every won battle |
| LuckyRookie | 100 | 20 | Lucky Guess - 3 charges per run, each removes 2 wrong choices |

## Stages

| Stage | Topic | Boss |
|---:|---|---|
| 1 | Java Basics | Professor Syntax |
| 2 | Control Flow | Professor Loopwell |
| 3 | Methods and Arrays | Professor Stacksmann |
| 4 | Object-Oriented Programming | Dr. NullPointer |

## Roster

All sprites are 8 lines tall, at most 25 columns wide, with the eyes fixed on line 4. Hero selection shows them in a 2x2 grid; battles place the hero and enemy side by side.

### CodeKnight

```text
         ^
        / \
       /___\     +-----+
      | o o |    |#####|
      |  -  |    |#####|
     /|=====|\   |#####|
    / | ### | \  +-----+
      d     b
```

### QuickCoder

```text
        .-.
         |
       /^^^\
      | o o |     +----+
      |  >  |===> >|[##]|
     /|=====|\     +----+
    / | ### | \
      d     b
```

### WiseScholar

```text
         ^
        / \
       /___\     +---+
      | o o |    | = |
      |  _  |    | = |
     /|=====|\   | = |
    / | ### | \  +---+
      |_____|
```

### LuckyRookie

```text
       * * *
        \|/
         |      +----+
      | o o |   | o  |
      |  w  |   |  o |
     /|=====|\  +----+
    / | ### | \
      |_____|
```

### Student

```text
        ___
       /   \
      |     |   +-----+
      | o o |   | []  |
      |  .  |   |     |
     /|=====|\  +-----+
    / | ### | \
      d     b
```

### Professor

```text
        ___
       /   \     +---+
      /     \    | = |
     | o   o |   | = |
     |  ~~~  |   | = |
    /|=======|\  +---+
   / |  |#|  | \
     +--+ +--+
```

## Question bank

- 40 easy questions covering Java basics.
- 20 hard questions covering OOP and deeper Java.
- Questions are drawn randomly without repeating until a pool is exhausted, then the pool refills.

## Project structure

| File | Responsibility |
|---|---|
| `Main.java` | Entry point; handles a closed input stream |
| `Game.java` | Run flow, hero grid, stages, healing, end screens |
| `BattleManager.java` | One battle: questions, turns, Lucky Guess, damage |
| `Player.java` | Abstract hero: HP, attack, perk hooks, art contract |
| `Enemy.java` | Abstract enemy: HP, attack, difficulty, art contract |
| `Student.java` | Normal enemy: 30 HP, 12 ATK, easy questions |
| `Professor.java` | Boss: 80 HP, 22 ATK, hard questions, subject line |
| `CodeKnight.java` | Tank hero: 30% damage reduction |
| `QuickCoder.java` | Critical hero: 25% chance of double damage |
| `WiseScholar.java` | Sustain hero: heals after each win |
| `LuckyRookie.java` | Utility hero: three Lucky Guess charges |
| `Question.java` | One multiple-choice question and its difficulty |
| `QuestionBank.java` | Question pools and no-repeat random draw |
| `ConsoleUtil.java` | Shared input, box frames, HP bars, screen clearing |

## OOP concepts in use

- **Abstraction** - `Player` and `Enemy` define the shared contracts.
- **Inheritance** - Four hero subclasses, two enemy subclasses.
- **Polymorphism** - `getArt()`, `getDamageRoll()`, `takeDamage()` and `onBattleWon()` change behavior per subclass.
- **Encapsulation** - HP is private; damage and healing go through methods.
- **Composition** - `Game` owns a `QuestionBank`; `BattleManager` receives the player, enemy and bank.
- **Enums** - `Question.Difficulty` separates easy and hard pools.
- **Collections** - `QuestionBank` uses `ArrayList` pools and used-question tracking.

## Notes

- No build tool or external dependency. `javac *.java` is enough.
- The ASCII roster is the only UI asset; there are no images or fonts to install.
- Hero names are fixed; student names are drawn from a pool of eight.
