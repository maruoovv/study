package strategy;

import java.util.ArrayList;
import java.util.List;

public class Cart {
    private List<Item> items;
    private PayType payType;

    public Cart(PayType payType) {
        this.items = new ArrayList<>();
        this.payType = payType;
    }

    public void setPayType(PayType payType) {
        this.payType = payType;
    }

    public List<Item> getItems() {
        return items;
    }

    public void addItem(Item item) {
        this.items.add(item);
    }

    private int calculateTotalFee() {
        return items.stream().mapToInt(Item::getPrice).sum();
    }

    public int buy(int pay) {
        int sum = calculateTotalFee();

        double exchangedFee = payType.calculateExchangeFee(pay);
        if (exchangedFee < sum) throw new RuntimeException("요금이 부족합니다.");

        return pay - sum;
    }
}
