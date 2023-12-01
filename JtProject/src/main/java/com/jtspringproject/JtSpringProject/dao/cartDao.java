package com.jtspringproject.JtSpringProject.dao;

import com.jtspringproject.JtSpringProject.models.Cart;
import com.jtspringproject.JtSpringProject.models.User;
import com.jtspringproject.JtSpringProject.models.Product;
import org.hibernate.SessionFactory;
import org.hibernate.query.Query;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
public class cartDao {
    @Autowired
    private SessionFactory sessionFactory;

    @Transactional
    public Cart addCart(Cart cart) {
        this.sessionFactory.getCurrentSession().save(cart);
        return cart;
    }

    @Transactional
    public List<Cart> getCarts() {
        return this.sessionFactory.getCurrentSession().createQuery("from CART", Cart.class).list();
    }

    @Transactional
    public Cart findByUser(User user) {
        Query<Cart> query = this.sessionFactory.getCurrentSession().createQuery("from CART c where c.customer = :user", Cart.class);
        query.setParameter("user", user);
        return query.uniqueResult();
    }

    @Transactional
    public void updateCart(Cart cart) {
        this.sessionFactory.getCurrentSession().update(cart);
    }

    @Transactional
    public void deleteCart(Cart cart) {
        this.sessionFactory.getCurrentSession().delete(cart);
    }
    @Transactional
    public void removeProductFromCart(User user, int productId) {
        Query<Cart> cartQuery = sessionFactory.getCurrentSession().createQuery("from CART c where c.customer = :user", Cart.class);
        cartQuery.setParameter("user", user);
        Cart cart = cartQuery.uniqueResult();

        if (cart != null) {
            Product productToRemove = new Product();
            productToRemove.setId(productId);

            cart.getProducts().remove(productToRemove);

            sessionFactory.getCurrentSession().update(cart); // Updating the cart
        } else {
            System.err.println("No cart found");
        }
    }
}
