package com.jtspringproject.JtSpringProject.dao;

import java.util.List;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.jtspringproject.JtSpringProject.models.Category;
import com.jtspringproject.JtSpringProject.models.Product;

@Repository
public class productDao {
	public SessionFactory getSessionFactory() {
		return sessionFactory;
	}

	@Autowired
    private SessionFactory sessionFactory;
	
	public void setSessionFactory(SessionFactory sf) {
        this.sessionFactory = sf;
    }
	
	@Transactional
	public List<Product> getProducts(){
		return this.sessionFactory.getCurrentSession().createQuery("from PRODUCT").list();
	}
	
	@Transactional
	public Product addProduct(Product product) {
		this.sessionFactory.getCurrentSession().save(product);
		return product;
	}
	
	@Transactional
	public Product getProduct(int id) {
		return this.sessionFactory.getCurrentSession().get(Product.class, id);
	}

	@Transactional
	public Product updateProduct(Product product){
		this.sessionFactory.getCurrentSession().update(product); //Fix: Product update button is not working
		return product;
	}

	@Transactional
	public Boolean deletProduct(int id) {

		Session session = this.sessionFactory.getCurrentSession();
		Object persistanceInstance = session.load(Product.class, id);

		if (persistanceInstance != null) {
			session.delete(persistanceInstance);
			return true;
		}
		return false;
	}


	@Transactional
	public List<Product> searchProducts(String query) {

		return this.sessionFactory.getCurrentSession()
				.createQuery("FROM PRODUCT WHERE name LIKE :query OR description LIKE :query")
				.setParameter("query", "%" + query + "%")
				.list();
	}

}
