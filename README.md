# Clash of Minds

A terminal quiz-battler written in Java. Four stages, four professors, one semester.

*OOP101 - Group 1*

Answer Java and OOP questions to damage your opponent. A wrong answer lets them hit back. Clear three students and a professor in each stage, then graduate.

## Requirements

- JDK 8 or newer
- A UTF-8 terminal with a font that supports box-drawing characters. Consolas, Cascadia Mono and JetBrains Mono all cover them.

## Run

Windows: run `run.bat` from the project root.

```bat
run.bat
```

The script switches the console to UTF-8, compiles with `-encoding UTF-8`, and starts the game.

Manual run:

```bat
chcp 65001
javac -encoding UTF-8 *.java
java -Dstdout.encoding=UTF-8 Main
```

If the screen shows `?` instead of borders, the console is not in UTF-8. Run `chcp 65001` in that window before `java Main`, or use `run.bat`.

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

<<<<<<< HEAD
=======
## Roster

All sprites are 10 lines tall, at most 25 columns wide, with the eyes fixed on line 5. Hero selection shows them in a 2x2 grid; battles place them in split panes with a center divider. The battle HUD shows the stage, labels your hero as `YOU`, tags enemy difficulty, and puts compact HP bars under each fighter.

### CodeKnight

```text
          ^
         ╱ ╲
        ╱___╲     ╔═════╗
       │     │    ║█████║
       │ o o │    ║█████║
       │  -  │    ║█████║
      ╱│═════│╲   ║█████║
     ╱ │ ### │ ╲  ╚═════╝
    ╱  │_____│  ╲
         d   b
```

### QuickCoder

```text
         .-.
          │
        ╱^^^╲
       │     │    ╔════╗
       │ o o │    ║[##]║
       │  >  │══> ╚════╝
      ╱│═════│╲
     ╱ │ ### │ ╲
    ╱  │     │  ╲
       d     b
```

### WiseScholar

```text
          ^
         ╱ ╲
        ╱___╲       ╔═══╗
       │     │      ║ ▓ ║
       │ o o │      ║ ▓ ║
       │  _  │      ║ ▓ ║
      ╱│═════│╲     ║ ▓ ║
     ╱ │ ### │ ╲    ╚═══╝
    ╱  │_____│  ╲
         d   b
```

### LuckyRookie

```text
       * * *
        ╲│╱
         │
        ╱ ╲       ╔════╗
       │ o o │    ║ o  ║
       │  w  │    ║  o ║
      ╱│═════│╲   ╚════╝
     ╱ │ ### │ ╲
    ╱  │_____│  ╲
         d   b
```

### Student

```text
        ___
       ╱   ╲
      │     │
      │     │    ╔═════╗
      │ o o │    ║ []  ║
      │  .  │    ║     ║
     ╱│═════│╲   ╚═════╝
    ╱ │ ### │ ╲
   ╱  │     │  ╲
      d     b
```

### Professor

```text
        ___
       ╱   ╲      ╔═══╗
      ╱     ╲     ║ ▓ ║
     │       │    ║ ▓ ║
     │ o   o │    ║ ▓ ║
     │  ~~~  │    ║ ▓ ║
    ╱│═══════│╲   ╚═══╝
   ╱ │  │#│  │ ╲
  ╱  │       │  ╲
     ╚══╤═╤══╝
```

>>>>>>> 35a25f8 (feat: upgrade battle arena HUD, 1x4 hero select, battle progress counter, and end-screen sprites)
## Question bank

- 55 easy questions covering Java basics.
- 35 hard questions covering OOP and deeper Java.
- Questions are drawn randomly without repeating until a pool is exhausted, then the pool refills.

## Project structure

| File | Responsibility |
|---|---|
| `Main.java` | Entry point; handles a closed input stream |
| `Game.java` | Run flow, hero grid, stages, healing, end screens |
| `BattleManager.java` | One battle: questions, turns, Lucky Guess, damage, arena HUD |
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
| `ConsoleUtil.java` | Shared input, Unicode boxes, HP bars, screen clearing |

## OOP concepts in use

- **Abstraction** - `Player` and `Enemy` define the shared contracts.
- **Inheritance** - Four hero subclasses, two enemy subclasses.
- **Polymorphism** - `getArt()`, `getDamageRoll()`, `takeDamage()` and `onBattleWon()` change behavior per subclass.
- **Encapsulation** - HP is private; damage and healing go through methods.
- **Composition** - `Game` owns a `QuestionBank`; `BattleManager` receives the player, enemy and bank.
- **Enums** - `Question.Difficulty` separates easy and hard pools.
- **Collections** - `QuestionBank` uses `ArrayList` pools and used-question tracking.

## Notes

- No build tool or external dependency. `run.bat` compiles and runs.
- The Unicode roster is the only UI asset; the terminal needs a font that covers box-drawing characters.
- Hero names are fixed; student names are drawn from a pool of eight.
