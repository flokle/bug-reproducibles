package io.example.bug;

import io.example.bug.model.Cat;
import io.example.bug.model.Leg;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;
import jakarta.persistence.TypedQuery;

public class InheritanceSelectBug {

	/**
	 * Reproduction example for bug:
	 * 
	 * https://hibernate.atlassian.net/browse/HHH-17832
	 * 
	 */
	public static void main(String[] args) {
		EntityManager entityManager = openEntityManager();

		long catId = createContents(entityManager);
		entityManager.getTransaction().begin();

		TypedQuery<Leg> query = entityManager
				.createQuery("SELECT leg FROM Cat cat JOIN cat.legs leg WHERE cat.id = :catId", Leg.class);

		query.setParameter("catId", catId);

		System.err.println("Found legs: " + query.getResultList().size());

		entityManager.getTransaction().commit();
	}

	/**
	 * Create default contents. Returns id of created {@link Cat}
	 * 
	 * @param entityManager
	 * @return
	 */
	private static long createContents(EntityManager entityManager) {
		entityManager.getTransaction().begin();

		Leg owner = new Leg();

		Cat cat = new Cat();
		cat.addToLegs(owner);
		entityManager.persist(owner);
		entityManager.persist(cat);

		entityManager.getTransaction().commit();

		return cat.getId();

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
