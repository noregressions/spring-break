package com.example;

import org.hibernate.EmptyInterceptor;
import org.hibernate.type.Type;
import java.io.Serializable;

public class MyHibernateInterceptor extends EmptyInterceptor {
    @Override
    public boolean onLoad(Object entity, Serializable id, Object[] state, String[] propertyNames, Type[] types) {
        // In Hibernate 6.x this method exists on EmptyInterceptor
        return super.onLoad(entity, id, state, propertyNames, types);
    }
}
