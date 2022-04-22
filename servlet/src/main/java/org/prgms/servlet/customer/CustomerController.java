package org.prgms.servlet.customer;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.ModelAndView;

@Controller
public class CustomerController {

	private final CustomerService customerService;

	public CustomerController(CustomerService customerService) {
		this.customerService = customerService;
	}

	@GetMapping(value = "/customers")
	public ModelAndView findCustomers() {
		final List<Customer> allCustomers = customerService.getAllCustomers();
		return new ModelAndView("views/customers",
			Map.of("serverTime", LocalDateTime.now(), "customers", allCustomers));
	}

	@GetMapping("/customers/{customerId}")
	public String findCustomer(@PathVariable("customerId") UUID customerId, Model model) {
		final Optional<Customer> maybeCustomer = customerService.getCustomer(customerId);
		if (maybeCustomer.isPresent()) {
			model.addAttribute("customer", maybeCustomer.get());
			return "views/customer-details";
		} else {
			return "views/404";
		}
	}

	@GetMapping("/customers/new")
	public String viewNewCustomerPage() {
		return "views/new-customers";
	}

	// controller 단까지 Entity를 참조하게 하지는 말자
	// DTO를 받아와서 메서드에 정보를 넣어주자
	@PostMapping("/customers/new")
	public String addNewCustomer(CreateCustomerRequest createCustomerRequest) {
		customerService.createCustomer(createCustomerRequest.email(), createCustomerRequest.name());
		return "redirect:/customers";

	}

}
