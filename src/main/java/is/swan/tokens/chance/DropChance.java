package is.swan.tokens.chance;

public class DropChance {

    private double chance;
    private short data;

    public DropChance(double chance, short data) {
        this.chance = chance;
        this.data = data;
    }

    public double getChance() {
        return chance;
    }

    public short getData() {
        return data;
    }
}
