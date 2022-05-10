package org.prgms.jpa.domain.order;

import javax.persistence.Entity;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
public class Furniture extends Item{
	private int width;
	private int height;
}
