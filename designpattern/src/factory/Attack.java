package factory;

public class Attack {
    private WeaponFactory weaponFactory;

    public Attack(WeaponFactory weaponFactory) {
        this.weaponFactory = weaponFactory;
    }

    public void attack(String type) {
        Weapon weapon = weaponFactory.createWeapon(type);
        weapon.attack();
    }
}
