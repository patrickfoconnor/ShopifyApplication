package shopifyApplication.model;

import shopifyApplication.util.DB;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.LinkedList;
import java.util.List;

public class Warehouse extends Model {

    private Long warehouseId;
    private String address;
    private String city;
    private String state;
    private String postalCode;


    // Customer Constructor
    public Warehouse() {
    }

    // Method to go from query results to album object
    private Warehouse(ResultSet results) throws SQLException {
        warehouseId = results.getLong("WarehouseID");
        address = results.getString("Address");
        city = results.getString("City");
        state = results.getString("State");
        postalCode = results.getString("PostalCode");
    }



    //---------------------- Getter / Setters -----------

    // Setter and  Getter for wareHouseId
    public void setWarehouseId(Long warehouseID) {
        this.warehouseId = warehouseID;
    }
    public Long getWarehouseId() {
        return warehouseId;
    }

    // Setter and Getter for address
    public void setAddress(String address){this.address = address;}
    public String getAddress() {
        return address;
    }

    // Setter and Getter for city
    public void setCity(String city){this.city = city;}
    public String getCity() {
        return city;
    }

    // Setter and Getter for State

    public void setState(String state) {this.state = state;}
    public String getState(){return state;}

    // Setter and Getter for Postal Code

    public void setPostalCode(String postalCode) {this.postalCode = postalCode;}
    public String getPostalCode(){return postalCode;}

    public List<Inventory> getInventory(){
        return Inventory.getInventoryForWarehouseId(warehouseId);
    }

    /**
     * Get all the customers with paging
     * @return a resultList of customers using sql query
     */
    public static List<Warehouse> all() {
        return all(0, Integer.MAX_VALUE);
    }

    public static List<Warehouse> all(int page, int count) {
        try (Connection conn = DB.connect();
             PreparedStatement stmt = conn.prepareStatement(
                     "SELECT * FROM warehouse LIMIT ? OFFSET ?"
             )) {
            stmt.setInt(1, count);
            stmt.setInt(2, (page*count)-count );
            ResultSet results = stmt.executeQuery();
            List<Warehouse> resultList = new LinkedList<>();
            while (results.next()) {
                resultList.add(new Warehouse(results));
            }
            return resultList;
        } catch (SQLException sqlException) {
            throw new RuntimeException(sqlException);
        }
    }
    /**
     * Find Warehouse by warehouseId
     * @param warehouseId the warehouseId in search
     * @return A Warehouse
     */
    public static Warehouse find(long warehouseId) {
        try (Connection conn = DB.connect();
             PreparedStatement stmt = conn.prepareStatement("SELECT *, p.ProductName as ProductName, Inventory.ProductId as ProduictId\n" +
                     "FROM Inventory\n" +
                     "JOIN warehouse w on w.WarehouseID = Inventory.WarehouseId\n" +
                     "JOIN products p on Inventory.ProductId = p.ProductId\n" +
                     "WHERE w.WarehouseID=?")) {
            stmt.setLong(1, warehouseId);
            ResultSet results = stmt.executeQuery();
            if (results.next()) {
                return new Warehouse(results);
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
                         "INSERT INTO warehouse (Address, City, State, PostalCode) " +
                                 "VALUES (?,?,?,?)")) {
                stmt.setString(1, this.getAddress());
                stmt.setString(2, this.getCity());
                stmt.setString(3, this.getState());
                stmt.setString(4, this.getPostalCode());
                stmt.executeUpdate();
                warehouseId = DB.getLastID(conn);
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
                         "UPDATE warehouse SET " +
                                 "Address=?,City=?, State=?, " +
                                 "PostalCode=? WHERE WarehouseID=?")) {
                stmt.setString(1, this.getAddress());
                stmt.setString(2, this.getCity());
                stmt.setString(3, this.getState());
                stmt.setString(4, this.getPostalCode());
                stmt.setLong(5, this.getWarehouseId());
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
                     "DELETE FROM customers WHERE WarehouseID=?")) {
            stmt.setLong(1, this.getWarehouseId());
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
        if (getAddress() == null || "".equals(getAddress())) {
            addError("Warehouse Address can't be null or blank!");
        }
        if (getCity() == null || "".equals(getCity())) {
            addError("Warehouse City can't be null or blank!");
        }
        if (getState() == null || "".equals(getState())) {
            addError("Warehouse State can't be null or blank!");
        }
        if (getPostalCode() == null || "".equals(getPostalCode())) {
            addError("Warehouse Postal Code can't be null or blank!");
        }
        return !hasErrors();
    }

}
