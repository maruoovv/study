package strategy;

public class StrategyPattern {

    public static void main(String[] args) {
        Cart cart = new Cart(new WonPayType());

        cart.addItem(new Item(100));
        cart.addItem(new Item(2000));

        cart.buy(2500);

        cart.addItem(new Item(2000));

        try {
            // 원화가 모잘라 달러화로 지불
            cart.buy(2500);
        } catch (RuntimeException e) {
            cart.setPayType(new DollarPayType());
            cart.buy(250);
        }
    }
}
