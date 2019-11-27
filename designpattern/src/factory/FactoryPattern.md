### 팩토리패턴 

팩토리 패턴은 객체를 생성하기 위한 인터페이스를 정의한다. 어떤 클래스를 만들지를 결정하는 것을 클라이언트가 아닌,
서브클래스에서 결정하게 한다.


간단한 예제를 통해 팩토리 패턴을 구현해보자

#### 턴제게임 무기 시스템 

한 턴제 게임에서는 매 턴에 무기를 선택하고 공격을 한다.
초기에는 무기가 검 하나만 있었다.

```java
class Attack {
    public void attack() {
        Weapon weapon = new Sword();
        
        weapon.attack();
    }
}
```

무기 종류의 증가로, 단검과 롱소드, 스태프가 생겼다.
매턴 이중 하나를 선택해 공격한다.
```java
class Attack {
    public void attack(String type) {
        Weapon weapon;
        
        switch(type) {
            case "LongSword" : 
                weapon = new LongSowrd();
                break;
            case "ShortSword" :
                weapon = new ShortSword();
                break;
            case "Staff" :
                weapon = new Staff();
                break;
            default:
                throw new IllegalArgumentException();
        }
        
        weapon.attack();
    }
}
```
타입이 추가되고 제거됨에 따라, 수정이 일어나는 부분은 객체를 생성하는 부분뿐이다.  
이를 객체 생성을 처리하는 클래스로 분리하여 리팩토링을 하자.

```java
abstract class WeaponFactory {
    Weapon createWeapon(String type);
}

class SwordFactory extends WeaponFactory {
    @Override
    public Weapon createWeapon(String type) {
        switch(type) {
            case "LongSword" : 
                return new LongSowrd();
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

class Attack {
    private WeaponFactory weaponFactory;
    
    public Attack(WeaponFactory weaponFactory) {
        this.weaponFactory = weaponFactory;
    }
    public void attack(String type) {
        Weapon weapon = weaponFactory.createWeapon(type);
        weapon.attack();
    }
}
```

이것과 같이 객체를 생성하는 책임을 별도의 객체로 분리할수 있다.  


### 팩토리 메소드 패턴
- 객체를 생성하기 위한 인터페이스를 정의하고, 어떤 인스턴스를 만들지는 서브클래스에서 결정하게 만든다.

----

시스템에 모자와 직업이 추가되었다. 모자는 적의 공격을 일정 확률로 방어한다.
무기는 검/스태프로 통일되었다.
검사는 투구를 써야하고, 마법사는 마법모자를 써야한다.  


```java
abstract class EquipmentFactory {
    Weapon createWeapon();
    Hat createHat();
}

class WarriorEquipmentFactory extends EquipmentFactory {
    public Weapon createWeapon() {
        return new Sword();
    }
    public Hat createHat() {
        return new Helmet();
    }
}

class MagicianEquipmentFactory extends EquipmentFactory {
    public Weapon createWeapon() {
        return new Staff();
    }
    
    public Hat createHat() {
        return new MagicalHat();
    }
}

public abstract class Character {
    Weapon weapon;
    Hat hat;
    
    abstract void prepare();
}

class Warrior extends Character {
    private EquipmentFactory equipmentFactory;
    
    public Warrior(EquipmentFactory equipmentFactory) {
        this.equipmentFactory = equipmentFactory;
    }
    
    void prepare() {
        weapon = equipmentFactory.createWeapon();
        hat = equipmentFactory.createHat();
    }
    
    void attack() {
        weapon.attack();
    }
}

class Magician extends Character {
    private EquipmentFactory equipmentFactory;
        
    public Warrior(EquipmentFactory equipmentFactory) {
        this.equipmentFactory = equipmentFactory;
    }
    
    void prepare() {
        weapon = equipmentFactory.createWeapon();
        hat = equipmentFactory.createHat();
    }
    
    void attack() {
        weapon.attack();
    }
}
```

### 추상 팩토리 패턴 

- 인터페이스를 이용하여 일련의 제품들을 만들어 낼 수 있다. 클라이언트와 팩토리에서 생산되는 구체 클래스를 분리 시킬 수 있다.


---
솔직히 추상 팩토리 패턴은 이해가 잘 안간다.. 필요성에 대해서도 잘 모르겠다.