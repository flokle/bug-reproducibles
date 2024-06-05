package io.example.bug.model;

import java.util.UUID;

import org.hibernate.annotations.Any;
import org.hibernate.annotations.AnyDiscriminator;
import org.hibernate.annotations.AnyDiscriminatorValue;
import org.hibernate.annotations.AnyKeyJavaClass;

import io.example.bug.model.subtypes.ConcreteTypeForAny;
import io.example.bug.model.subtypes.SuperTypeForAny;
import jakarta.persistence.Column;
import jakarta.persistence.DiscriminatorType;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;

@Entity
public class Person {

	/**
	 * Changing the id to type long and adding
	 * 
	 * @GeneratedValue(strategy = GenerationType.IDENTITY)
	 * 
	 * fixes the error
	 */
	@Id
	protected long id;

	/**
	 * Setting this relation @Transient fixes the error
	 */
	@Any(fetch = FetchType.LAZY)
	@AnyDiscriminator(value = DiscriminatorType.STRING)
	@AnyDiscriminatorValue(discriminator = "ConcreteTypeForAny", entity = ConcreteTypeForAny.class)
	@AnyKeyJavaClass(value = UUID.class)
	@Column(name = "any_information_type")
	@JoinColumn(name = "any_information_id")
	protected SuperTypeForAny anyInformation;

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public SuperTypeForAny getAnyInformation() {
		return anyInformation;
	}

	public void setAnyInformation(SuperTypeForAny anyInformation) {
		this.anyInformation = anyInformation;
	}
}
