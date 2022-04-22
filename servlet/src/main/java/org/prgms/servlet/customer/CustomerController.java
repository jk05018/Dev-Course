package org.prgms.servlet.customer;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

@Controller
public class CustomerController {

	private final CustomerService customerService	;

	public CustomerController(CustomerService customerService) {
		this.customerService = customerService;
	}

	@RequestMapping(value = "/customers", method = RequestMethod.GET)
	public ModelAndView findCustomers(){
		final List<Customer> allCustomers = customerService.getAllCustomers();
		return new ModelAndView("views/customers", Map.of("serverTime", LocalDateTime.now(), "customers", allCustomers));
	}

}
