package io.example.polymorphism.model;

import java.util.Set;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToMany;

@Entity
public class Cat implements Animal {

    @Id
    private long id;

    @ManyToMany
    private Set<Person> owners;

	@Override
	public Set<Person> getOwners() {
		return this.owners;
	}

}
