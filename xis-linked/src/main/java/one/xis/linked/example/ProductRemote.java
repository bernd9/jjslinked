package one.xis.linked.example;

import one.xis.linked.ClientInvoker;
import one.xis.linked.ClientMethod;
import one.xis.linked.ClientService;
import one.xis.linked.ClientState;

import java.util.List;

@ClientService("ProductService")
public abstract class ProductRemote {

    private ProductService productService = new ProductService();

    @ClientMethod
    List<Object> getProductList(String category) {
        return null;
    }

    @ClientMethod
    Product onProductSelected1(long productId) {
        return productService.findById(productId);
    }

    @ClientMethod
    void onProductSelected2(long productId) {
        Product product = productService.findById(productId);
        Basket basket = setSelectProduct(product);
    }


    @ClientState("basket")
    abstract Basket setSelectProduct(@ClientState("product") Product product);


    @ClientInvoker("onNewOffer")
    abstract Object sendOffer(Object product);
}
