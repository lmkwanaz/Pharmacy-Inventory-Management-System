package pims.model;

import java.math.BigDecimal;
import java.time.LocalDate;

public class Medicine {
    private final int medicineId;
    private final String name;
    private final String company;
    private final String medicineType;
    private final BigDecimal price;
    private final int quantityInStock;
    private final int reorderLevel;
    private final LocalDate expiryDate;
    private final int supplierId;
    private final String supplierName;

    public Medicine(
            int medicineId,
            String name,
            String company,
            String medicineType,
            BigDecimal price,
            int quantityInStock,
            int reorderLevel,
            LocalDate expiryDate,
            int supplierId,
            String supplierName
    ) {
        this.medicineId = medicineId;
        this.name = name;
        this.company = company;
        this.medicineType = medicineType;
        this.price = price;
        this.quantityInStock = quantityInStock;
        this.reorderLevel = reorderLevel;
        this.expiryDate = expiryDate;
        this.supplierId = supplierId;
        this.supplierName = supplierName;
    }

    public int getMedicineId() {
        return medicineId;
    }

    public String getName() {
        return name;
    }

    public String getCompany() {
        return company;
    }

    public String getMedicineType() {
        return medicineType;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public int getQuantityInStock() {
        return quantityInStock;
    }

    public int getReorderLevel() {
        return reorderLevel;
    }

    public LocalDate getExpiryDate() {
        return expiryDate;
    }

    public int getSupplierId() {
        return supplierId;
    }

    public String getSupplierName() {
        return supplierName;
    }
}
