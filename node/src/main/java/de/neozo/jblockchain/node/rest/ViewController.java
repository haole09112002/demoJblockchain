package de.neozo.jblockchain.node.rest;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;


@Controller

public class ViewController {
	@RequestMapping("home")
	public String showHome()
	{
		return "views/block.html";
	}
}
