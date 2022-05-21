package shopifyApplication.controller;

import shopifyApplication.model.Inventory;
import shopifyApplication.model.Warehouse;
import shopifyApplication.util.Web;

import java.util.List;

import static spark.Spark.get;
import static spark.Spark.post;

public class WarehouseController {
    public static void init() {
        /* CREATE */
        get("/warehouse/new", (req, resp) -> {
            Warehouse warehouse = new Warehouse();
            return Web.renderTemplate("templates/warehouse/new.vm", "warehouse", warehouse);
        });

        post("/warehouse/new", (req, resp) -> {
            Warehouse warehouse = new Warehouse();
            Web.putValuesInto(warehouse, "Address", "City", "State", "PostalCode");
            if (warehouse.create()) {
                Web.message("Created A Warehouse!");
                return Web.redirect("/warehouse/" + warehouse.getWarehouseId());
            } else {
                Web.error("Could Not Create A Warehouse!");
                return Web.renderTemplate("templates/warehouse/new.vm",
                        "warehouse", warehouse);
            }
        });

        /* READ */
        get("/warehouse", (req, resp) -> {
            List<Warehouse> warehouses;
            warehouses = Warehouse.all(Web.getPage(), Web.PAGE_SIZE);

            return Web.renderTemplate("templates/warehouse/index.vm",
                    "warehouses", warehouses);
        });

        get("/warehouse/:id", (req, resp) -> {
            Warehouse warehouse = Warehouse.find(Integer.parseInt(req.params(":id")));
            assert warehouse != null;
            List<Inventory> inventoryItems = warehouse.getInventory();
            return Web.renderTemplate("templates/warehouse/show.vm",
                    "warehouse", warehouse);
        });

        /* UPDATE */
        get("/warehouse/:id/edit", (req, resp) -> {
            Warehouse warehouse = Warehouse.find(Integer.parseInt(req.params(":id")));
            return Web.renderTemplate("templates/warehouse/edit.vm",
                    "warehouse", warehouse);
        });

        post("/warehouse/:id", (req, resp) -> {
            Warehouse warehouse = Warehouse.find(Integer.parseInt(req.params(":id")));
            Web.putValuesInto(warehouse, "Address", "City", "State", "PostalCode");
            if (warehouse.update()) {
                Web.message("Updated Warehouse!");
                return Web.redirect("/warehouse/" + warehouse.getWarehouseId());
            } else {
                Web.error("Could Not Update Warehouse!");
                return Web.renderTemplate("templates/warehouse/edit.vm",
                        "warehouse", warehouse);
            }
        });

        /* DELETE */
        get("/warehouse/:id/delete", (req, resp) -> {
            Warehouse warehouse = Warehouse.find(Integer.parseInt(req.params(":id")));
            warehouse.delete();
            Web.message("Deleted Warehouse " + warehouse.getWarehouseId());
            return Web.redirect("/warehouse");
        });
    }
}
