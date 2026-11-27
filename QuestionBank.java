import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Stores the full pool of quiz questions and hands out random questions
 * without repeating any question until its pool is exhausted.
 */
public class QuestionBank {

    private final List<Question> easyPool = new ArrayList<>();
    private final List<Question> hardPool = new ArrayList<>();

    private final List<Question> usedEasy = new ArrayList<>();
    private final List<Question> usedHard = new ArrayList<>();

    private final Random random = new Random();

    public QuestionBank() {
        fillEasyPool();
        fillHardPool();
    }

    /**
     * Returns a random question of the given difficulty that has not been
     * used yet in this run. If the whole pool was asked, it starts over.
     */
    public Question getRandomQuestion(Question.Difficulty difficulty) {
        List<Question> pool = easyPool;
        List<Question> used = usedEasy;
        if (difficulty == Question.Difficulty.HARD) {
            pool = hardPool;
            used = usedHard;
        }

        if (used.size() >= pool.size()) {
            used.clear(); // every question was asked once: refill the pool
        }

        Question question;
        do {
            question = pool.get(random.nextInt(pool.size()));
        } while (used.contains(question));

        used.add(question);
        return question;
    }

    // ------------------------------------------------------------------
    // Helper factories: one readable line per question.
    // ------------------------------------------------------------------

    private static Question easy(String text, String a, String b, String c, String d, int answerIndex) {
        return new Question(text, new String[]{a, b, c, d}, answerIndex, Question.Difficulty.EASY);
    }

    private static Question hard(String text, String a, String b, String c, String d, int answerIndex) {
        return new Question(text, new String[]{a, b, c, d}, answerIndex, Question.Difficulty.HARD);
    }

    // ------------------------------------------------------------------
    // EASY pool: Java basics (55 questions)
    // ------------------------------------------------------------------

    private void fillEasyPool() {
        easyPool.add(easy("What does JDK stand for?", "Java Development Kit", "Java Deployment Kit", "Java Debug Kernel", "Java Design Kit", 0));
        easyPool.add(easy("Which keyword creates a new object?", "make", "create", "new", "object", 2));
        easyPool.add(easy("How many bits does an int use in Java?", "8", "16", "32", "64", 2));
        easyPool.add(easy("Which of these is NOT a primitive type?", "int", "boolean", "char", "String", 3));
        easyPool.add(easy("Which symbol ends a Java statement?", ";", ",", ".", ":", 0));
        easyPool.add(easy("Which keyword makes a value constant?", "static", "final", "const", "fixed", 1));
        easyPool.add(easy("What is the default value of an int field that is never assigned?", "null", "-1", "1", "0", 3));
        easyPool.add(easy("Which loop always runs at least once?", "for", "while", "do-while", "foreach", 2));
        easyPool.add(easy("What does JVM stand for?", "Java Visual Machine", "Java Virtual Machine", "Just Virtual Machine", "Java Verified Machine", 1));
        easyPool.add(easy("Which method is the entry point of a Java program?", "start()", "init()", "main()", "run()", 2));
        easyPool.add(easy("How do you start a single-line comment?", "/*", "//", "<!--", "#", 1));
        easyPool.add(easy("Which type can only store true or false?", "int", "String", "boolean", "char", 2));
        easyPool.add(easy("What is the result of 7 / 2 when both numbers are int?", "3.5", "3", "4", "2", 1));
        easyPool.add(easy("Which keyword immediately exits a loop?", "exit", "stop", "end", "break", 3));
        easyPool.add(easy("What do we call an instance of a class?", "a method", "an object", "a field", "a package", 1));
        easyPool.add(easy("Which of these is a valid variable name?", "2cool", "my-var", "myVar", "my var", 2));
        easyPool.add(easy("Which package is imported automatically in every Java file?", "java.util", "java.lang", "java.io", "java.math", 1));
        easyPool.add(easy("For int[] scores = new int[5]; what does scores.length return?", "4", "5", "6", "10", 1));
        easyPool.add(easy("Which keyword refers to the current object inside a class?", "self", "me", "this", "here", 2));
        easyPool.add(easy("What does a void method return?", "0", "null", "an empty String", "nothing", 3));
        easyPool.add(easy("Which statement prints text to the console?", "echo(...)", "print.line(...)", "System.out.println(...)", "console.log(...)", 2));
        easyPool.add(easy("What is the result of \"5\" + 3 in Java?", "8", "\"53\"", "a compile error", "5", 1));
        easyPool.add(easy("Which operator gives the remainder of a division?", "%", "/", "*", "^", 0));
        easyPool.add(easy("How many bits does a char use in Java?", "8", "16", "32", "64", 1));
        easyPool.add(easy("Which keyword makes one variable shared by ALL objects of a class?", "final", "private", "static", "public", 2));
        easyPool.add(easy("How many bytes does the byte type hold?", "1", "2", "4", "8", 0));
        easyPool.add(easy("Which statement chooses between many cases?", "switch", "select", "choose", "case-of", 0));
        easyPool.add(easy("A public class name must always match...", "the file name", "the package name", "the main method name", "any name you like", 0));
        easyPool.add(easy("Which converts the String \"123\" into an int?", "Integer.parseInt(\"123\")", "(int)\"123\"", "int(\"123\")", "\"123\".toInt()", 0));
        easyPool.add(easy("Which keyword skips to the next loop iteration?", "skip", "next", "pass", "continue", 3));
        easyPool.add(easy("What is the index of the FIRST element of an array?", "1", "0", "-1", "it depends", 1));
        easyPool.add(easy("Which operator compares two int values for equality?", "=", "===", "==", "equals", 2));
        easyPool.add(easy("What does an abstract method NOT have?", "a name", "a return type", "a body", "parameters", 2));
        easyPool.add(easy("Which type stores decimal numbers with the most precision?", "float", "double", "int", "byte", 1));
        easyPool.add(easy("What is the default value of a boolean field?", "true", "false", "null", "0", 1));
        easyPool.add(easy("Which block always runs after try/catch?", "finally", "end", "always", "ensure", 0));
        easyPool.add(easy("Which of these is NOT a Java access modifier?", "public", "private", "protected", "friend", 3));
        easyPool.add(easy("When does (a || b) return true?", "only if both are true", "if at least one is true", "only if both are false", "never", 1));
        easyPool.add(easy("Which command compiles a Java source file?", "java Hello.java", "javac Hello.java", "compile Hello.java", "run Hello.java", 1));
        easyPool.add(easy("What does i += 2 mean?", "i = 2", "i = i + 2", "i = i * 2", "i = 2 + 2", 1));
        easyPool.add(easy("Which keyword is used to inherit from a class?", "implements", "inherits", "extends", "super", 2));
        easyPool.add(easy("Which class is commonly used to read user input from the keyboard?", "Printer", "Scanner", "Writer", "Keyboard", 1));
        easyPool.add(easy("What does the length() method of a String return?", "the number of words", "the last index", "the size in bytes", "the number of characters", 3));
        easyPool.add(easy("Which operator increases a variable by 1?", "++", "+++", "+1", "^^", 0));
        easyPool.add(easy("Which method compares the content of two Strings?", "==", "equals()", "compare()", "same()", 1));
        easyPool.add(easy("Which keyword is used to manually throw an exception?", "raise", "throw", "catch", "error", 1));
        easyPool.add(easy("Which class is the parent of all classes in Java?", "Object", "Class", "Main", "Base", 0));
        easyPool.add(easy("How do you write a multi-line comment?", "//", "#", "<!-- -->", "/* ... */", 3));
        easyPool.add(easy("Which keyword is used to bring in a package or class from another package?", "include", "using", "import", "require", 2));
        easyPool.add(easy("Which expression gets the first character of the String s?", "s[0]", "s.charAt(0)", "s.get(0)", "s.at(0)", 1));
        easyPool.add(easy("Which keyword is used to create a subclass in Java?", "extends", "inherits", "subclass", "implements", 0));
        easyPool.add(easy("Which data type stores either true or false?", "char", "boolean", "byte", "String", 1));
        easyPool.add(easy("Which symbol is used for a single-line comment in Java?", "/*", "#", "//", "<!--", 2));
        easyPool.add(easy("Which keyword is used to stop a loop immediately?", "stop", "exit", "skip", "break", 3));
        easyPool.add(easy("Which method is used to convert a String into an integer?", "Integer.parseInt()", "Integer.toString()", "String.valueOf()", "Integer.convert()", 0));    
    }

    // ------------------------------------------------------------------
    // HARD pool: OOP and deeper Java (36 questions) - asked by professors
    // ------------------------------------------------------------------

    private void fillHardPool() {
        hardPool.add(hard("What is encapsulation?", "Making all fields public", "Hiding data and exposing it through methods", "Copying one object into another", "Writing code faster", 1));
        hardPool.add(hard("Which statement about an abstract class is TRUE?", "It can be instantiated directly", "It cannot have constructors", "It can hold both abstract and normal methods", "It cannot have fields", 2));
        hardPool.add(hard("Method overloading is resolved at...", "runtime", "compile time", "garbage collection time", "program shutdown", 1));
        hardPool.add(hard("Overriding a method requires the subclass method to have...", "a different name", "the same signature", "the static keyword", "a different return type", 1));
        hardPool.add(hard("Which keyword calls the parent class constructor?", "parent()", "base()", "super()", "this()", 2));
        hardPool.add(hard("Which keyword prevents a class from being subclassed?", "sealed", "final", "locked", "private", 1));
        hardPool.add(hard("A superclass reference pointing to a subclass object demonstrates...", "encapsulation", "polymorphism", "abstraction", "serialization", 1));
        hardPool.add(hard("Which collection does NOT allow duplicate elements?", "ArrayList", "LinkedList", "HashSet", "Vector", 2));
        hardPool.add(hard("A member with no access modifier is visible to...", "everyone", "only its own class", "the same package", "no one", 2));
        hardPool.add(hard("What does the instanceof operator check?", "the memory size of an object", "whether an object is of a given type", "how many references point to an object", "whether a thread is running", 1));
        hardPool.add(hard("Which exception type must be caught or declared with throws?", "RuntimeException", "NullPointerException", "A checked exception", "StackOverflowError", 2));
        hardPool.add(hard("When does a finally block run?", "only when the try block succeeds", "only when an exception is thrown", "always, after try/catch (unless the JVM exits)", "never", 2));
        hardPool.add(hard("Before Java 8, an interface method is by default...", "private and final", "public and abstract", "protected and static", "package-private", 1));
        hardPool.add(hard("What does the static keyword mean on a method?", "the method runs faster", "the method belongs to the class, not to any object", "the method cannot be called twice", "the method must be private", 1));
        hardPool.add(hard("What is an anonymous class?", "a class with no package", "a class declared and instantiated at once, with no name", "a class that was deleted", "a class with no methods", 1));
        hardPool.add(hard("Which interface lets objects be sorted in their natural order?", "Runnable", "Comparator", "Comparable", "Serializable", 2));
        hardPool.add(hard("What is the difference between String and StringBuilder?", "there is no difference", "StringBuilder can be changed after creation, String cannot", "String can be changed, StringBuilder cannot", "StringBuilder is always slower", 1));
        hardPool.add(hard("If a subclass overrides a method, which version runs?", "always the superclass version", "the version of the actual object at runtime", "a random one", "both, one after the other", 1));
        hardPool.add(hard("What does this(...) do inside a constructor?", "calls another constructor of the same class", "creates a new object", "calls the parent constructor", "exits the constructor", 0));
        hardPool.add(hard("Which statement about constructors is TRUE?", "constructors declare a return type", "a class can only have one constructor", "constructors share the class name and have no return type", "constructors are always static", 2));
        hardPool.add(hard("What does the final keyword do on a method?", "It prevents the method from being overridden", "It makes the method static", "It makes the method abstract", "It prevents the method from being called", 0));
        hardPool.add(hard("Which collection stores key-value pairs?", "HashSet", "ArrayList", "HashMap", "Stack", 2));
        hardPool.add(hard("Can an interface be instantiated directly with new?", "Yes, always", "No, only a class implementing it can be", "Yes, if it has no methods", "Only inside the same package", 1));
        hardPool.add(hard("What is the difference between == and equals() for Strings?", "== compares references, equals() compares content", "== compares content, equals() compares references", "There is no difference", "== is faster, so it is always correct", 0));
        hardPool.add(hard("What happens if a concrete class does not implement all abstract methods it inherits?", "It compiles normally", "The missing methods are generated automatically", "It runs with a warning", "It must be declared abstract", 3));
        hardPool.add(hard("Which statement about interfaces and multiple inheritance is TRUE?", "A class can extend multiple classes", "Interfaces cannot be implemented", "A class can implement multiple interfaces", "Only abstract classes can have multiple parents", 2));
        hardPool.add(hard("What does the protected access modifier allow?", "Access only within the same class", "Access from the same package and from subclasses", "Access from everywhere", "Access only from other packages", 1));
        hardPool.add(hard("What is the purpose of the toString() method?", "To return a String representation of the object", "To convert a String into an object", "To compare two Strings", "To delete the object", 0));
        hardPool.add(hard("What is autoboxing?", "Putting objects inside arrays", "Converting a class into an interface", "Copying an object into a new box", "Automatic conversion between a primitive and its wrapper class", 3));
        hardPool.add(hard("What does the throws keyword do in a method declaration?", "It throws an exception immediately", "It catches an exception", "It declares that the method may pass an exception to its caller", "It stops the program", 2));
        hardPool.add(hard("Which OOP concept allows one interface to be used for different implementations?", "Encapsulation", "Polymorphism", "Inheritance", "Constructor", 1));
        hardPool.add(hard("What is the main purpose of an interface in Java?", "To store objects directly", "To create primitive variables", "To define methods that implementing classes must provide", "To prevent all inheritance", 2));
        hardPool.add(hard("Which keyword prevents a method from being overridden by a subclass?", "static", "private", "final", "protected", 2));
        hardPool.add(hard("What will happen if a class implements an interface but does not implement all of its abstract methods?", "The class must be declared abstract", "The interface is automatically deleted", "The methods become private", "The program ignores the missing methods", 0));
        hardPool.add(hard("Which statement correctly describes constructor overloading?", "A class has constructors with different parameter lists", "A constructor is inherited from another class", "A constructor is declared with a return type", "A constructor can only be called once", 0));
    }
}
