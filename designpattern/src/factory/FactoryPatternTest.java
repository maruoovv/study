package factory;

public class FactoryPatternTest {
    public static void main(String[] args) {
        Attack swordAttack = new Attack(new SwordFactory());
        swordAttack.attack("LongSword");
        swordAttack.attack("ShortSword");

        Attack magicAttack = new Attack(new StaffFactory());
        magicAttack.attack("fire");
        magicAttack.attack("ice");

    }
}
