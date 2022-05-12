package org.prgms.jpa.order.controller;

import org.prgms.jpa.order.ApiResponse;
import org.prgms.jpa.order.dto.OrderDto;
import org.prgms.jpa.order.service.OrderService;
import org.springframework.data.crossstore.ChangeSetPersister;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
public class OrderController {

	private final OrderService orderService;

	@ExceptionHandler(ChangeSetPersister.NotFoundException.class)
	public ApiResponse<String> notFoundHandler(ChangeSetPersister.NotFoundException e){
		return ApiResponse.fail(404, e.getMessage());
	}

	@ExceptionHandler(Exception.class)
	public ApiResponse<String> internalServerErrorHandler(Exception e){
		return ApiResponse.fail(500, e.getMessage());
	}

	@PostMapping("/orders")
	public ApiResponse<String> save(@RequestBody OrderDto orderDto) {
		String uuid = orderService.save(orderDto);
		return ApiResponse.ok(uuid);
	}

	@GetMapping("/orders/{uuid}")
	public ApiResponse<OrderDto> getOne(@PathVariable String uuid) throws ChangeSetPersister.NotFoundException {
		final OrderDto findOne = orderService.findOne(uuid);
		return ApiResponse.ok(findOne);
	}

	@GetMapping("/orders")
	public ApiResponse<Page<OrderDto>> getAll(Pageable pageable) {
		final Page<OrderDto> all = orderService.findOrders(pageable);
		return ApiResponse.ok(all);
	}

}
