package io.example.bug;

import java.util.UUID;

import io.example.bug.model.Job;
import io.example.bug.model.Person;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;

public class PersistWithAnyBug {

	/**
	 * Reproduction example for bug:
	 * 
	 * https://hibernate.atlassian.net/browse/HHH-unknown
	 * 
	 */
	public static void main(String[] args) {
		EntityManager entityManager = openEntityManager();

		entityManager.getTransaction().begin();

		works(entityManager);
		failsWithAssertionError(entityManager);

		entityManager.getTransaction().commit();
	}

	/**
	 * The person is persisted first, so this case works
	 * 
	 * @param entityManager
	 */
	private static void works(EntityManager entityManager) {
		Job job = new Job();

		Person person = new Person();
		person.setId(1L);

		job.setPerson(person);

		// Person first
		entityManager.persist(person);
		entityManager.persist(job);
	}

	/**
	 * 
	 * The job is persisted first (and references person)
	 * 
	 * Fails with java.lang.AssertionError (or, if java assert not enabled
	 * java.lang.NullPointerException)
	 * 
	 * @param entityManager
	 */
	private static void failsWithAssertionError(EntityManager entityManager) {
		Job job = new Job();

		Person person = new Person();
		person.setId(2L);

		job.setPerson(person);

		// Job (referencing Person) first
		entityManager.persist(job);
		entityManager.persist(person);
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
