package pims.model;

import java.math.BigDecimal;

public class CartItem {
    private final int medicineId;
    private final String medicineName;
    private final BigDecimal unitPrice;
    private int quantity;

    public CartItem(int medicineId, String medicineName, BigDecimal unitPrice, int quantity) {
        this.medicineId = medicineId;
        this.medicineName = medicineName;
        this.unitPrice = unitPrice;
        this.quantity = quantity;
    }

    public int getMedicineId() {
        return medicineId;
    }

    public String getMedicineName() {
        return medicineName;
    }

    public BigDecimal getUnitPrice() {
        return unitPrice;
    }

    public int getQuantity() {
        return quantity;
    }

    public void addQuantity(int amount) {
        this.quantity += amount;
    }

    public BigDecimal getLineTotal() {
        return unitPrice.multiply(BigDecimal.valueOf(quantity));
    }
}
