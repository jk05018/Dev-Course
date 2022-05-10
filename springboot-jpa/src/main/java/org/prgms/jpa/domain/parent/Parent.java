package org.prgms.jpa.domain.parent;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.IdClass;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@IdClass(ParentId.class)
public class Parent {

	@Id
	private String id1;
	@Id
	private String id2;
}
