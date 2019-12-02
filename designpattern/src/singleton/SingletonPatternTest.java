package singleton;

class Singleton {
    private static Singleton instance = new Singleton();

    private int count = 0;
    private Singleton() {
        count++;
    }

    public static Singleton getInstance() {
        return instance;
    }

    public int getCount() {
        return count;
    }
}


public class SingletonPatternTest {
    public static void main(String[] args) {
        // always print 1
        System.out.println(Singleton.getInstance().getCount());
        System.out.println(Singleton.getInstance().getCount());
        System.out.println(Singleton.getInstance().getCount());

        // Singleton singleton = new Singleton(); // Compile error.
    }
}
