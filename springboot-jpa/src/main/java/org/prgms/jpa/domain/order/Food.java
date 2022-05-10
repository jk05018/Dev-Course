package org.prgms.jpa.domain.order;

import javax.persistence.Entity;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
public class Food extends Item{
	private String chef;
}
