package com.sg.floormaster.dao;

import com.sg.floormaster.model.Product;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;

public class FlooringMasteryProductDaoStubImpl implements FlooringMasteryProductDao {

    private Product onlyProduct;

    public FlooringMasteryProductDaoStubImpl() {
        onlyProduct = new Product("Carpet",
                new BigDecimal("2.25").setScale(2, RoundingMode.HALF_UP),
                new BigDecimal("2.10").setScale(2, RoundingMode.HALF_UP));
    }

    public FlooringMasteryProductDaoStubImpl(Product product) {
        onlyProduct = product;
    }

    @Override
    public List<Product> getAllProducts() {
        List<Product> products = new ArrayList<>();
        products.add(onlyProduct);
        return products;
    }
}
