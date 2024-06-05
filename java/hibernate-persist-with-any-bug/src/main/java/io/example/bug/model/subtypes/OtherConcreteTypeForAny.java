package io.example.bug.model.subtypes;

import java.util.UUID;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;

@Entity
public class OtherConcreteTypeForAny implements SuperTypeForAny {

	@Id
	protected UUID id;
	
	public UUID getId() {
		return id;
	}
	
	public void setId(UUID id) {
		this.id = id;
	}

}
