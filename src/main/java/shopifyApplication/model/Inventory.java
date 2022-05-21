package shopifyApplication.model;

import shopifyApplication.util.DB;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.LinkedList;
import java.util.List;

public class Inventory extends Model {

    private Long inventoryId;
    private Long productId;
    private Long productCount;
    private Long warehouseId;
    private String productName;


    // Customer Constructor
    public Inventory() {
    }

    // Method to go from query results to album object
    Inventory(ResultSet results) throws SQLException {
        warehouseId = results.getLong("WarehouseId");
        inventoryId = results.getLong("InventoryId");
        productName = results.getString("ProductName");
        productCount = results.getLong("ProductCount");
    }

    // Helper to inventory for specific warehouse

    public static List<Inventory> getInventoryForWarehouseId(Long warehouseId) {
        try (Connection conn = DB.connect();
             PreparedStatement stmt = conn.prepareStatement("    SELECT WarehouseId, InventoryID, ProductName, ProductCount, p.ProductId from Inventory\n" +
                     "    JOIN products p on Inventory.ProductId = p.ProductId\n" +
                     "    WHERE WarehouseId=?"
             )) {
            stmt.setLong(1, warehouseId);
            ResultSet results = stmt.executeQuery();
            List<Inventory> resultList = new LinkedList<>();
            while (results.next()) {
                resultList.add(new Inventory(results));
            }
            return resultList;
        } catch (SQLException sqlException) {
            throw new RuntimeException(sqlException);
        }
    }

    //---------------------- Getter / Setters -----------

    // Setter and  Getter for inventoryId

    public void setInventoryId(Long inventoryId) {this.inventoryId = inventoryId;}
    public Long getInventoryId(){return inventoryId;}

    // Setter and Getter for productId

    public void setProductId(Long productId) {this.productId = productId;}
    public Long getProductId() {return productId;}
    public Product findProductByName(String productName){return Product.findByName(productName);}


    // Setter and Getter for productCount

    public void setProductCount(Long productCount) {this.productCount = productCount;}
    public Long getProductCount() {return productCount;}

    // Setter and Getter for warhouseId

    public void setWarehouseId(Long warehouseId) {this.warehouseId = warehouseId;}
    public Long getWarehouseId() {return warehouseId;}

    // Setter and Getter for productName

    public void setProductName(String productName) {this.productName = productName;}
    public String getProductName() {return productName;}

    /**
     * Get all the customers with paging
     * @return a resultList of customers using sql query
     */
    public static List<Inventory> all() {
        return all(0, Integer.MAX_VALUE);
    }

    public static List<Inventory> all(int page, int count) {
        try (Connection conn = DB.connect();
             PreparedStatement stmt = conn.prepareStatement(
                     "SELECT Inventory.WarehouseId, ProductName, ProductCount, InventoryID from Inventory\n" +
                             "JOIN products p on Inventory.ProductId = p.ProductId LIMIT ? OFFSET ?"
             )) {
            stmt.setInt(1, count);
            stmt.setInt(2, (page*count)-count );
            ResultSet results = stmt.executeQuery();
            List<Inventory> resultList = new LinkedList<>();
            while (results.next()) {
                resultList.add(new Inventory(results));
            }
            return resultList;
        } catch (SQLException sqlException) {
            throw new RuntimeException(sqlException);
        }
    }
    /**
     * Find Inventory by warehouseId
     * @param inventoryId the warehouseId in search
     * @return A Inventory
     */
    public static Inventory find(long inventoryId) {
        try (Connection conn = DB.connect();
             PreparedStatement stmt = conn.prepareStatement("SELECT * FROM Inventory " +
                     "JOIN products p on Inventory.ProductId = p.ProductId " +
                     " WHERE InventoryID=?")) {
            stmt.setLong(1, inventoryId);
            ResultSet results = stmt.executeQuery();
            if (results.next()) {
                return new Inventory(results);
            } else {
                return null;
            }
        } catch (SQLException sqlException) {
            throw new RuntimeException(sqlException);
        }
    }

    /* Crud Apps below */
    @Override
    public boolean create() {
        if ((findProductByName(this.productName)) == null){
            return false;
        }
        else{

            this.setProductId(findProductByName(this.productName).getProductId());
            if (verify() && this.getProductId() != null) {
                try (Connection conn = DB.connect();
                    PreparedStatement stmt = conn.prepareStatement(
                         "INSERT INTO Inventory (WarehouseId, ProductId, ProductCount) " +
                                 "VALUES (?,?,?)")) {
                    stmt.setLong(1, this.getWarehouseId());
                    stmt.setLong(2, this.getProductId());
                    stmt.setLong(3, this.getProductCount());
                    stmt.executeUpdate();
                    this.setInventoryId(DB.getLastID(conn));
                    return true;
                } catch (SQLException sqlException) {
                    throw new RuntimeException(sqlException);
                }
            } else {
                return false;
            }
        }
    }

    @Override
    public boolean update() {
        if (verify()) {
            try (Connection conn = DB.connect();
                 PreparedStatement stmt = conn.prepareStatement(
                         "UPDATE Inventory SET " +
                                 "ProductCount=?, WarehouseId=? WHERE InventoryID=?")) {
                stmt.setLong(1, this.getProductCount());
                stmt.setLong(2, this.getWarehouseId());
                stmt.setLong(3, this.getInventoryId());

                int updatedRowsCount = stmt.executeUpdate();
                return (updatedRowsCount > 0);
            } catch (SQLException sqlException) {
                throw new RuntimeException(sqlException);
            }
        } else {
            return false;
        }
    }


    @Override
    public void delete() {
        try (Connection conn = DB.connect();
             PreparedStatement stmt = conn.prepareStatement(
                     "DELETE FROM Inventory WHERE InventoryID=?")) {
            stmt.setLong(1, this.getInventoryId());
            stmt.executeUpdate();
        } catch (SQLException sqlException) {
            throw new RuntimeException(sqlException);
        }
    }

    // Verify that customer first name, last name, email,
    // billing country, billing city, billing address, and
    // support rep id are not null or blank
    @Override
    public boolean verify() {
        _errors.clear(); // clear any existing errors
        if (getProductCount() == null || "".equals(getProductCount())) {
            addError("Product Count can't be null or blank!");
        }
        if (getWarehouseId() == null || "".equals(getWarehouseId())) {
            addError("Warehouse Id can't be null or blank!");
        }
        return !hasErrors();
    }

}
