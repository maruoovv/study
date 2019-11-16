package strategy;

public interface PayType {
    int calculateExchangeFee(int pay);
}

class WonPayType implements PayType {
    private int exchangeRate = 1;

    @Override
    public int calculateExchangeFee(int pay) {
        System.out.println("Won PayType");
        return pay * exchangeRate;
    }
}

class YenPayType implements PayType {
    private int exchangeRate = 10;

    @Override
    public int calculateExchangeFee(int pay) {
        System.out.println("Yen PayType");
        return pay * exchangeRate;
    }
}

class DollarPayType implements PayType {
    private int exchangeRate = 1165;

    @Override
    public int calculateExchangeFee(int pay) {
        System.out.println("Dollar PayType");
        return pay * exchangeRate;
    }
}