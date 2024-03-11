package io.example.bug;

import static org.junit.Assert.assertEquals;

import java.util.HashMap;
import java.util.Map;

import org.hibernate.jpa.HibernatePersistenceProvider;
import org.junit.Rule;
import org.junit.Test;
import org.postgresql.ds.PGSimpleDataSource;

import io.example.bug.model.Cat;
import io.example.bug.model.Person;
import io.zonky.test.db.postgres.junit.EmbeddedPostgresRules;
import io.zonky.test.db.postgres.junit.SingleInstancePostgresRule;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.TypedQuery;

/**
 * Reproduction test for bug:
 * 
 * https://hibernate.atlassian.net/browse/HHH-
 * 
 */
public class GroupByBug {

	@Rule
	public SingleInstancePostgresRule pg = EmbeddedPostgresRules.singleInstance();

	/**
	 * Test selection of specific columns of entity {@link Person} on a postgres
	 * database
	 */
	@Test
	public void testSelectColumnsOnPostgres() {
		// Has been fixed in 6.2.0 and reintroduced in 6.2.4
		PGSimpleDataSource ds = (PGSimpleDataSource) pg.getEmbeddedPostgres().getPostgresDatabase();
		EntityManager entityManager = createEntityManager(ds.getUser(), ds.getPassword(), ds.getUrl());
		testWithDefinedColumns(entityManager);
	}

	/**
	 * Test selection of specific columns of entity {@link Person} on a h2 database
	 */
	@Test
	public void testSelectColumnsOnH2() {
		EntityManager entityManager = createEntityManager("sa", "", "jdbc:h2:mem:db1;DB_CLOSE_DELAY=-1;");
		testWithDefinedColumns(entityManager);
	}

	/**
	 * Test selection of full entity {@link Person} on a postgres database
	 */
	@Test
	public void testSelectPersonEntityOnPostgres() {
		// Has been fixed in 6.2.0 and reintroduced in 6.2.4
		PGSimpleDataSource ds = (PGSimpleDataSource) pg.getEmbeddedPostgres().getPostgresDatabase();
		EntityManager entityManager = createEntityManager(ds.getUser(), ds.getPassword(), ds.getUrl());
		testWithFullPersonEntitySelect(entityManager);
	}

	/**
	 * Test selection of full entity {@link Person} on a h2 database
	 */
	@Test
	public void testSelectPersonEntityOnH2() {
		EntityManager entityManager = createEntityManager("sa", "", "jdbc:h2:mem:db1;DB_CLOSE_DELAY=-1;");
		testWithFullPersonEntitySelect(entityManager);
	}

	private void testWithFullPersonEntitySelect(EntityManager entityManager) {

		createContents(entityManager);
		entityManager.getTransaction().begin();

		String queryString = "SELECT owner FROM Cat cat JOIN cat.owners owner GROUP BY owner";
		TypedQuery<Person> query = entityManager.createQuery(queryString, Person.class);

		assertEquals(1, query.getResultList().size());

		entityManager.getTransaction().commit();
	}

	private void testWithDefinedColumns(EntityManager entityManager) {

		createContents(entityManager);
		entityManager.getTransaction().begin();

		String queryString = "SELECT owner.id, owner.name FROM Cat cat JOIN cat.owners owner GROUP BY owner";
		TypedQuery<Object[]> query = entityManager.createQuery(queryString, Object[].class);

		assertEquals(1, query.getResultList().size());

		entityManager.getTransaction().commit();
	}

	/**
	 * Create basic contents
	 * 
	 * @param entityManager
	 * @return
	 */
	private static void createContents(EntityManager entityManager) {
		entityManager.getTransaction().begin();

		Cat cat = new Cat();
		Person owner = new Person();
		cat.addToOwners(owner);
		entityManager.persist(owner);
		entityManager.persist(cat);

		entityManager.getTransaction().commit();

	}

	/**
	 * Create an entity manager based on given data source properties
	 *
	 * @return the entity manager
	 */
	private EntityManager createEntityManager(String user, String password, String url) {

		Map<String, Object> settings = new HashMap<>();
		settings.put("jakarta.persistence.jdbc.user", user);
		settings.put("jakarta.persistence.jdbc.password", password);
		settings.put("jakarta.persistence.jdbc.url", url);
		EntityManagerFactory entityManagerFactory = new HibernatePersistenceProvider()
				.createEntityManagerFactory("Demo", settings);

		return entityManagerFactory.createEntityManager();
	}
}
