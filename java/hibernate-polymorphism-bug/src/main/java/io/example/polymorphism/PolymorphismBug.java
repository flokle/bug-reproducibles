package io.example.polymorphism;

import java.util.List;

import io.example.polymorphism.model.Person;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;

public class PolymorphismBug {

	public static void main(String[] args) {
		EntityManager entityManager = openEntityManager();
		entityManager.getTransaction().begin();

		List<Person> owners = entityManager
				.createQuery("SELECT p FROM io.example.polymorphism.model.Animal an JOIN an.owners p", Person.class)
				.getResultList();
		System.err.println(owners.size());
		entityManager.getTransaction().commit();

	}

	/** The entity manager factory. */
	private static EntityManagerFactory entityManagerFactory = null;

	/**
	 * Open entity manager.
	 *
	 * @return the entity manager
	 */
	private static EntityManager openEntityManager() {
		if (entityManagerFactory == null) {
			entityManagerFactory = Persistence.createEntityManagerFactory("Demo");
		}
		return entityManagerFactory.createEntityManager();
	}
}
