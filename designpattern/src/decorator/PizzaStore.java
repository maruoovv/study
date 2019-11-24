package decorator;

class PizzaStore {
    public static void main(String[] args) {
        // 평범한 씬도우 피자
        Pizza pizza = new ThinDough();
        System.out.println(pizza.name() + ", cost : " + pizza.cost());

        // 페퍼로니, 치즈를 추가한 씬도우 피자
        Pizza pizza2 = new ThinDough();
        pizza2 = new Pepperoni(pizza2);
        pizza2 = new Cheese(pizza2);
        System.out.println(pizza2.name() + ", cost : " + pizza2.cost());

        // 시금치를 매우 좋아하는 사람이 주문한 오리지널 도우 피자
        Pizza pizza3 = new OriginalDough();
        pizza3 = new Spinach(pizza3);
        pizza3 = new Spinach(pizza3);
        pizza3 = new Spinach(pizza3);
        pizza3 = new Spinach(pizza3);
        pizza3 = new Spinach(pizza3);
        pizza3 = new Spinach(pizza3);
        pizza3 = new Spinach(pizza3);
        pizza3 = new Spinach(pizza3);
        pizza3 = new Spinach(pizza3);
        pizza3 = new Spinach(pizza3);
        pizza3 = new Spinach(pizza3);
        pizza3 = new Spinach(pizza3);
        System.out.println(pizza3.name() + ", cost : " + pizza3.cost());
    }
}