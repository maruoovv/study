package factory;

interface Weapon {
    void attack();
}

class LongSword implements Weapon {

    @Override
    public void attack() {
        System.out.println("Long Sword Attack!");
    }
}

class ShortSword implements Weapon {

    @Override
    public void attack() {
        System.out.println("short Sword Attack!");
    }
}

class FireStaff implements Weapon {

    @Override
    public void attack() {
        System.out.println("fire ball");
    }
}

class IceStaff implements Weapon {

    @Override
    public void attack() {
        System.out.println("ice age");
    }
}