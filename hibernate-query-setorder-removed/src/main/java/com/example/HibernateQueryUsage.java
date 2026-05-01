package com.example;

import org.hibernate.Session;
import org.hibernate.query.Order;
import org.hibernate.query.SelectionQuery;
import java.util.Collections;
import java.util.List;

public class HibernateQueryUsage {
    public static List<Product> getOrderedProducts(Session session) {
        SelectionQuery<Product> query = session.createSelectionQuery("from Product", Product.class);
        // This method setOrder(List<Order>) is removed in Hibernate 7.0
        return query.setOrder(Collections.singletonList(Order.asc(Product.class, "name"))).list();
    }
}
