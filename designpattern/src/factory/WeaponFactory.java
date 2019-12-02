package factory;

abstract class WeaponFactory {
    abstract Weapon createWeapon(String type);
}

class SwordFactory extends WeaponFactory {
    @Override
    public Weapon createWeapon(String type) {
        switch(type) {
            case "LongSword" :
                return new LongSword();
            case "ShortSword" :
                return new ShortSword();
            default:
                throw new IllegalArgumentException();
        }
    }
}

class StaffFactory extends WeaponFactory {
    @Override
    public Weapon createWeapon(String type) {
        switch (type) {
            case "fire" :
                return new FireStaff();
            case "ice" :
                return new IceStaff();
            default:
                throw new IllegalArgumentException();
        }
    }
}