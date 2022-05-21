package shopifyApplication.model;

import shopifyApplication.util.DB;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.LinkedList;
import java.util.List;

public class Product extends Model {

    private Long productId;
    private String unitPrice;
    private String weight;
    private String productName;



    // Customer Constructor
    public Product() {
    }

    // Method to go from query results to product object
    private Product(ResultSet results) throws SQLException {
        productId = results.getLong("ProductId");
        unitPrice = results.getString("UnitPrice");
        weight = results.getString("Weight");
        productName = results.getString("ProductName");
    }



    //---------------------- Getter / Setters -----------

    // Setter and  Getter for productId
    public void setProductId(Long productId) {
        this.productId = productId;
    }
    public Long getProductId() {return productId;}

    // Setter and Getter for unitPrice

    public void setUnitPrice(String unitPrice) {this.unitPrice = unitPrice;}
    public String getUnitPrice(){return unitPrice;}

    // Setter and Getter for weight

    public void setWeight(String weight) {this.weight = weight;}
    public String getWeight() {return weight;}

    // Setter and Getter for productName
    public void setProductName(String productName){this.productName = productName;}
    public String getProductName(){return productName;}


    public static Product findByName(String productName) {
        try (Connection conn = DB.connect();
             PreparedStatement stmt = conn.prepareStatement(
                     "SELECT *\n" +
                             "FROM products\n" +
                             "WHERE ProductName=?"
             )) {
            stmt.setString(1, productName);
            ResultSet results = stmt.executeQuery();
            if (results.next()) {
                return new Product(results);
            } else {
                return null;
            }
        } catch (SQLException sqlException) {
            throw new RuntimeException(sqlException);
        }
    }



    /**
     * Get all the customers with paging
     * @return a resultList of customers using sql query
     */
    public static List<Product> all() {
        return all(0, Integer.MAX_VALUE);
    }

    public static List<Product> all(int page, int count) {
        try (Connection conn = DB.connect();
             PreparedStatement stmt = conn.prepareStatement(
                     "SELECT * FROM products LIMIT ? OFFSET ?"
             )) {
            stmt.setInt(1, count);
            stmt.setInt(2, (page*count)-count );
            ResultSet results = stmt.executeQuery();
            List<Product> resultList = new LinkedList<>();
            while (results.next()) {
                resultList.add(new Product(results));
            }
            return resultList;
        } catch (SQLException sqlException) {
            throw new RuntimeException(sqlException);
        }
    }
    /**
     * Find Warehouse by warehouseId
     * @param productId the productId in search
     * @return A Warehouse
     */
    public static Product find(long productId) {
        try (Connection conn = DB.connect();
             PreparedStatement stmt = conn.prepareStatement("SELECT * FROM products WHERE ProductId=?")) {
            stmt.setLong(1, productId);
            ResultSet results = stmt.executeQuery();
            if (results.next()) {
                return new Product(results);
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
        if (verify()) {
            try (Connection conn = DB.connect();
                 PreparedStatement stmt = conn.prepareStatement(
                         "INSERT INTO products (UnitPrice, Weight, ProductName) " +
                                 "VALUES (?,?,?)")) {
                stmt.setString(1, this.getUnitPrice());
                stmt.setString(2, this.getWeight());
                stmt.setString(3, this.getProductName());
                stmt.executeUpdate();
                productId = DB.getLastID(conn);
                return true;
            } catch (SQLException sqlException) {
                throw new RuntimeException(sqlException);
            }
        } else {
            return false;
        }
    }

    @Override
    public boolean update() {
        if (verify()) {
            try (Connection conn = DB.connect();
                 PreparedStatement stmt = conn.prepareStatement(
                         "UPDATE products SET " +
                                 "UnitPrice=?,Weight=?, " +
                                 " ProductName=?  WHERE main.products.ProductId=?")) {
                stmt.setString(1, this.getUnitPrice());
                stmt.setString(2, this.getWeight());
                stmt.setString(3, this.getProductName());
                stmt.setLong(4, this.getProductId());
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
                     "DELETE FROM products WHERE ProductId=?")) {
            stmt.setLong(1, this.getProductId());
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
        if (getUnitPrice() == null || "".equals(getUnitPrice())) {
            addError("Product Unit Price can't be null or blank!");
        }
        if (getWeight() == null || "".equals(getWeight())) {
            addError("Product Weight can't be null or blank!");
        }
        if (getProductName() == null || "".equals(getProductName())) {
            addError("Product Name can't be null or blank!");
        }
        return !hasErrors();
    }

}
