package shopifyApplication.controller;

import shopifyApplication.model.Product;
import shopifyApplication.util.Web;

import java.util.List;

import static spark.Spark.get;
import static spark.Spark.post;

public class ProductController {
    public static void init() {
        /* CREATE */
        get("/product/new", (req, resp) -> {
            Product product = new Product();
            return Web.renderTemplate("templates/product/new.vm", "product", product);
        });

        post("/product/new", (req, resp) -> {
            Product product = new Product();
            Web.putValuesInto(product, "UnitPrice", "Weight", "ProductName");
            if (product.create()) {
                Web.message("Created A Product!");
                return Web.redirect("/product/" + product.getProductId());
            } else {
                Web.error("Could Not Create A Product!");
                return Web.renderTemplate("templates/product/new.vm",
                        "product", product);
            }
        });

        /* READ */
        get("/product", (req, resp) -> {
            List<Product> products;
            products = Product.all(Web.getPage(), Web.PAGE_SIZE);

            return Web.renderTemplate("templates/product/index.vm",
                    "products", products);
        });

        get("/product/:id", (req, resp) -> {
            Product product = Product.find(Integer.parseInt(req.params(":id")));
            return Web.renderTemplate("templates/product/show.vm",
                    "product", product);
        });

        /* UPDATE */
        get("/product/:id/edit", (req, resp) -> {
            Product product = Product.find(Integer.parseInt(req.params(":id")));
            return Web.renderTemplate("templates/product/edit.vm",
                    "product", product);
        });

        post("/product/:id", (req, resp) -> {
            Product product = new Product();
            Web.putValuesInto(product,"ProductName", "UnitPrice", "Weight");
            product.setProductId((long) Integer.parseInt(req.params(":id")));
            if (product.update()) {
                Web.message("Updated Product!");
                return Web.redirect("/product/" + product.getProductId());
            } else {
                Web.error("Could Not Update Product!");
                return Web.renderTemplate("templates/product/edit.vm",
                        "product", product);
            }
        });

        /* DELETE */
        get("/product/:id/delete", (req, resp) -> {
            Product product = Product.find(Integer.parseInt(req.params(":id")));
            product.delete();
            Web.message("Deleted Product " + product.getProductId());
            return Web.redirect("/product");
        });
    }
}
