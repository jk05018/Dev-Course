package org.prgms.jpa.item.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.annotation.JsonAppend;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ItemDto {

	private Long id;
	private int price;
	private int stockQuantity;

	private ItemType type;

	// FOOD
	private String chef;

	// CAR
	private Integer power;

	// FURNITURE
	private Integer width;
	private Integer height;

}
