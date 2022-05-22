package shopifyApplication;

import shopifyApplication.controller.*;
import shopifyApplication.util.Web;

import static spark.Spark.*;
import spark.Filter;

class Server {

    public static void main(String[] args) {

        Web.init();

        /* ========================================================================= */
        /* Root Path                                                                 */
        /* ========================================================================= */
        get("/", (req, resp) -> {
            Web.message("Allowing Businesses to Focus on Business");
            return Web.renderTemplate("templates/index.vm");
        });


        /* ========================================================================= */
        /* Business
        /* ========================================================================= */
        WarehouseController.init();
        ProductController.init();
        InventoryController.init();

    }

}