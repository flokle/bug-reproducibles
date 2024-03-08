package org.acme;

import java.util.List;

import org.acme.persistence.MyEntity;
import org.hibernate.Session;

import io.quarkus.runtime.StartupEvent;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.event.Observes;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;

@ApplicationScoped
public class GreetingResource {

	@PersistenceContext
	protected EntityManager entityManager;

	@Transactional
	public void onStart(@Observes StartupEvent ev) {
		System.out.println("Doing");

		Session session = entityManager.unwrap(Session.class);
		session.enableFilter("filterDeleted");
		List<MyEntity> resultList = entityManager.createQuery("FROM MyEntity", MyEntity.class).getResultList();
		for (MyEntity myEntity : resultList) {
			System.out.println(myEntity);
		}
	}
}
