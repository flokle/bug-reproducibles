package io.example.polymorphism;

import java.util.List;

import io.example.polymorphism.model.Animal;
import io.example.polymorphism.model.Cat;
import io.example.polymorphism.model.Dog;
import io.example.polymorphism.model.Person;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;
import jakarta.persistence.TypedQuery;

public class PolymorphismBug {

	/**
	 * Reproduction example for bug:
	 * 
	 * https://hibernate.atlassian.net/browse/HHH-15944
	 * 
	 * @param args
	 */
	public static void main(String[] args) {
		// runCase1();
		runCase2();
		// runCase3();
	}

	// Case 1
	public static void runCase1() {
		EntityManager entityManager = openEntityManager();

		createContents(entityManager);
		entityManager.getTransaction().begin();

		List<Animal> animals = entityManager
				.createQuery("SELECT an FROM io.example.polymorphism.model.Animal an", Animal.class).getResultList();

		System.err.println("Found animals: " + animals.size());
		
		entityManager.getTransaction().commit();

	}

	// Case 2
	public static void runCase2() {
		EntityManager entityManager = openEntityManager();

		createContents(entityManager);
		entityManager.getTransaction().begin();

		List<Person> owners = entityManager
				.createQuery("SELECT p FROM io.example.polymorphism.model.Animal an JOIN an.owners p", Person.class)
				.getResultList();

		System.err.println("Found owners: " + owners.size());
		
		entityManager.getTransaction().commit();

	}

	// Case 3
	public static void runCase3() {
		EntityManager entityManager = openEntityManager();

		long ownerId = createContents(entityManager);
		entityManager.getTransaction().begin();
		Person owner = entityManager.find(Person.class, ownerId);

		TypedQuery<Animal> query = entityManager.createQuery(
				"SELECT an FROM io.example.polymorphism.model.Animal an WHERE :owner = some elements(an.owners)", Animal.class);
		query.setParameter("owner", owner);
		List<Animal> animals = query.getResultList();
		
		System.err.println("Found animals of owner: " + animals.size());
		
		entityManager.getTransaction().commit();

	}

	/**
	 * ############################################## ################### Utility
	 * ################## ##############################################
	 */

	/**
	 * Create default contents. Returns id of created owner {@link Person}
	 * 
	 * @param entityManager
	 * @return
	 */
	private static long createContents(EntityManager entityManager) {
		entityManager.getTransaction().begin();

		Person owner = new Person();

		Cat cat = new Cat();
		Dog dog = new Dog();

		cat.addToOwners(owner);
		dog.addToOwners(owner);

		entityManager.persist(owner);
		entityManager.persist(cat);
		entityManager.persist(dog);

		entityManager.getTransaction().commit();

		return owner.getId();

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
