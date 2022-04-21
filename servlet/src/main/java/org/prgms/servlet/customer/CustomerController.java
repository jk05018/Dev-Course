package org.prgms.servlet.customer;

import java.time.LocalDateTime;
import java.util.Map;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

@Controller
public class CustomerController {

	@RequestMapping(value = "/customers", method = Requ	estMethod.GET)
	public ModelAndView findCustomers(){
		return new ModelAndView("customers", Map.of("serverTime", LocalDateTime.now()));
	}
}
