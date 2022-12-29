package io.example.polymorphism.model;

import java.util.HashSet;
import java.util.Set;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToMany;

@Entity
public class Dog implements Animal {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private long id;

	@ManyToMany
	private Set<Person> owners = new HashSet<>();

	@Override
	public Set<Person> getOwners() {
		return this.owners;
	}
	
	@Override
	public void addToOwners(Person person) {
		owners.add(person);
	}
}
