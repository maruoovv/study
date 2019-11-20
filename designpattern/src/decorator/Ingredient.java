package decorator;

abstract class Ingredient implements Pizza {
}

class Pepperoni extends Ingredient {
    private Pizza pizza;

    public Pepperoni(Pizza pizza) {
        this.pizza = pizza;
    }

    @Override
    public String name() {
        return pizza.name() + ", pepperoni";
    }

    @Override
    public int cost() {
        return pizza.cost() + 2000;
    }
}

class Cheese extends Ingredient {
    private Pizza pizza;

    public Cheese(Pizza pizza) {
        this.pizza = pizza;
    }

    @Override
    public String name() {
        return pizza.name() + ", cheese";
    }

    @Override
    public int cost() {
        return pizza.cost() + 2500;
    }
}

class Spinach extends Ingredient {
    private Pizza pizza;

    public Spinach(Pizza pizza) {
        this.pizza = pizza;
    }

    @Override
    public String name() {
        return pizza.name() + ", spinach";
    }

    @Override
    public int cost() {
        return pizza.cost() + 1000;
    }
}
