package decorator;

class ThinDough implements Pizza {

    @Override
    public String name() {
        return "Thin dough";
    }

    @Override
    public int cost() {
        return 10000;
    }
}

class OriginalDough implements Pizza {

    @Override
    public String name() {
        return "Original dough";
    }

    @Override
    public int cost() {
        return 12000;
    }
}