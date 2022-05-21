package shopifyApplication.controller;

import shopifyApplication.model.Inventory;
import shopifyApplication.util.DB;
import shopifyApplication.util.Web;

import java.util.List;

import static spark.Spark.get;
import static spark.Spark.post;

public class InventoryController {
    public static void init() {
        /* CREATE */
        get("/inventory/new", (req, resp) -> {
            Inventory inventory = new Inventory();
            return Web.renderTemplate("templates/inventory/new.vm", "inventory", inventory);
        });

        post("/inventory/new", (req, resp) -> {
            Inventory inventory = new Inventory();
            Web.putValuesInto(inventory, "WarehouseId", "ProductName", "ProductCount");
            if (inventory.create()) {
                Web.message("Created A Inventory!");
                return Web.redirect("/inventory/" + inventory.getInventoryId());
            } else {
                Web.error("Could Not Create A Inventory!");
                return Web.renderTemplate("templates/inventory/new.vm",
                        "inventory", inventory);
            }
        });

        /* READ */
        get("/inventory", (req, resp) -> {
            List<Inventory> inventories;
            inventories = Inventory.all(Web.getPage(), Web.PAGE_SIZE);

            return Web.renderTemplate("templates/inventory/index.vm",
                    "inventories", inventories);
        });

        get("/inventory/:id", (req, resp) -> {
            Inventory inventory = Inventory.find(Integer.parseInt(req.params(":id")));
            return Web.renderTemplate("templates/inventory/show.vm",
                    "inventory", inventory);
        });

        /* UPDATE */
        get("/inventory/:id/edit", (req, resp) -> {
            Inventory inventory = Inventory.find(Integer.parseInt(req.params(":id")));
            return Web.renderTemplate("templates/inventory/edit.vm",
                    "inventory", inventory);
        });

        post("/inventory/:id", (req, resp) -> {
            Inventory inventory = Inventory.find(Integer.parseInt(req.params(":id")));
            Web.putValuesInto(inventory, "WarehouseId", "ProductCount");
            if (inventory.update()) {
                Web.message("Updated Inventory!");
                return Web.redirect("/inventory/" + inventory.getInventoryId());
            } else {
                Web.error("Could Not Update Inventory!");
                return Web.renderTemplate("templates/inventory/edit.vm",
                        "inventory", inventory);
            }
        });

        /* DELETE */
        get("/inventory/:id/delete", (req, resp) -> {
            Inventory inventory = Inventory.find(Integer.parseInt(req.params(":id")));
            inventory.delete();
            Web.message("Deleted Inventory " + inventory.getInventoryId());
            return Web.redirect("/inventory");
        });
    }
}
