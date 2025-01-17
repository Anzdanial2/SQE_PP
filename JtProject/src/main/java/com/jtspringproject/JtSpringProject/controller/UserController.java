package com.jtspringproject.JtSpringProject.controller;

import com.jtspringproject.JtSpringProject.models.Cart;
import com.jtspringproject.JtSpringProject.models.Product;
import com.jtspringproject.JtSpringProject.models.User;

import java.io.Console;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletRequest;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;


import com.jtspringproject.JtSpringProject.services.cartService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import com.jtspringproject.JtSpringProject.services.userService;
import com.jtspringproject.JtSpringProject.services.productService;
import com.jtspringproject.JtSpringProject.services.cartService;


import com.jtspringproject.JtSpringProject.models.Cart;
import com.jtspringproject.JtSpringProject.models.Product;
import com.jtspringproject.JtSpringProject.models.User;
import com.jtspringproject.JtSpringProject.services.cartService;
import com.jtspringproject.JtSpringProject.services.productService;
import com.jtspringproject.JtSpringProject.services.userService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;


@Controller
public class UserController{
	
	@Autowired
	private userService userService;

	@Autowired
	private productService productService;

	@Autowired
	private cartService cartService;

	@GetMapping("/register")
	public String registerUser()
	{
		return "register";
	}

	@GetMapping("/buy")
	public String buy()
	{
		return "buy";
	}


	@GetMapping({"/", "/login"})
	public String userlogin(Model model) {
		return "userLogin";
	}



	@GetMapping("/user")
	public ModelAndView getHomePage() {
		ModelAndView mv = new ModelAndView("index");

		List<Product> products = productService.getProducts();
		mv.addObject("products", products);
		return mv;
	}

	@RequestMapping(value = "userloginvalidate", method = RequestMethod.POST)
	public String userlogin(@RequestParam("username") String username, @RequestParam("password") String pass, HttpServletRequest request, HttpServletResponse response, RedirectAttributes redirectAttributes) {
		User u = this.userService.checkLogin(username, pass);

		if (u != null && u.getUsername() != null && u.getUsername().equals(username)) {
			HttpSession session = request.getSession();
			session.setAttribute("user", u);

			response.addCookie(new Cookie("username", u.getUsername()));

			redirectAttributes.addFlashAttribute("user", u);
			List<Product> products = this.productService.getProducts();
			redirectAttributes.addFlashAttribute("products", products);

			return "redirect:/user";
		} else {
			redirectAttributes.addFlashAttribute("msg", "Please enter correct email and password");
			return "redirect:/";
		}
	}

	@GetMapping("/logout")
	public String logout(HttpServletRequest request, HttpServletResponse response) {
		HttpSession session = request.getSession(false);
		if (session != null) {
			session.invalidate();
		}

		SecurityContextHolder.getContext().setAuthentication(null);

		Cookie cookie = new Cookie("username", null);
		cookie.setMaxAge(0);
		cookie.setPath("/");
		response.addCookie(cookie);

		return "redirect:/login";
	}




	@GetMapping("/user/products")
	public ModelAndView getproduct() {

		ModelAndView mView = new ModelAndView("uproduct");

		List<Product> products = this.productService.getProducts();

		if(products.isEmpty()) {
			mView.addObject("msg","No products are available");
		}else {
			mView.addObject("products",products);
		}

		return mView;
	}


	@GetMapping("/user/products/search")
	public ModelAndView searchProducts(@RequestParam("searchquery") String query) {
		ModelAndView mv = new ModelAndView("uproduct");

		List<Product> searchResults = productService.searchProducts(query);
		mv.addObject("products", searchResults);

		return mv;
	}

	@RequestMapping(value = "newuserregister", method = RequestMethod.POST)
	public String newUseRegister(@ModelAttribute User user)
	{
		
		System.out.println(user.getEmail());
		user.setRole("ROLE_NORMAL");
		this.userService.addUser(user);
		
		return "redirect:/";
	}

	   //for Learning purpose of model
		@GetMapping("/test")
		public String Test(Model model)
		{
			System.out.println("test page");
			model.addAttribute("author","jay g ajera");
			model.addAttribute("id",40);
			
			List<String> friends = new ArrayList<String>();
			model.addAttribute("f",friends);
			friends.add("xyz");
			friends.add("abc");
			
			return "test";
		}
		
		// for learning purpose of model and view ( how data is pass to view)
		
		@GetMapping("/test2")
		public ModelAndView Test2()
		{
			System.out.println("test page");
			//create modelandview object
			ModelAndView mv=new ModelAndView();
			mv.addObject("name","jay gajera 17");
			mv.addObject("id",40);
			mv.setViewName("test2");
			
			List<Integer> list=new ArrayList<Integer>();
			list.add(10);
			list.add(25);
			mv.addObject("marks",list);
			return mv;
		}

/*
	@GetMapping("user/carts")
	public ModelAndView getCarts() {
		ModelAndView mv = new ModelAndView("cartproduct");
		mv.addObject("carts", cartService.getCarts());
		return mv;
	}
*/

	@PostMapping("/user/carts/add")
	public String addCart(@ModelAttribute Cart cart) {
		System.out.println("Running @PostMapping(\"/user/carts/add\")\n" +
				"\tpublic String addCart");
		cartService.addCart(cart);

		return "redirect:/user/cart";
	}

	@GetMapping("/user/carts")
	public ModelAndView getUserCart(HttpSession session) {
		ModelAndView mv = new ModelAndView("cartproduct");

		User user = (User) session.getAttribute("user"); //Getting user from session
		if (user != null) {
			Cart cart = cartService.getOrCreateCart(user);
			mv.addObject("products", cart.getProducts()); // Passing products to view
			System.out.println(cart.getProducts().size());
		} else {
			mv.setViewName("redirect:/login");
		}

		return mv;
	}


	@GetMapping("/user/products/addtocart")
	public String addToCart(@RequestParam("id") int productId, HttpServletRequest request, RedirectAttributes redirectAttributes) {
		System.out.println("Running @GetMapping(\"/user/products/addtocart\")\n" +
				"\tpublic String addToCart");

		Product product = productService.getProduct(productId);
		User user = getUserFromSession(request.getSession());

		if (user == null) {
			System.out.println("User is null");
			return "redirect:/";
		}

		Cart cart = cartService.getOrCreateCart(user);

		System.out.println("Adding to cart");
		cart.addProduct(product);
		cartService.updateCart(cart);
		redirectAttributes.addFlashAttribute("successMessage", "Product added to cart successfully!");
		return "redirect:/user/products";
	}

	@PostMapping("/user/carts/remove")
	public String removeFromCart(@RequestParam("productId") int productId, HttpSession session, RedirectAttributes redirectAttributes) {
		User user = (User) session.getAttribute("user");
		if (user == null) {
			System.out.println("No user found");
			redirectAttributes.addFlashAttribute("error", "You need to log in to modify the cart");
			return "redirect:/login";
		}

		try {
			cartService.removeProductFromCart(user, productId);
			redirectAttributes.addFlashAttribute("success", "Product removed from cart");
		} catch (Exception e) {
			redirectAttributes.addFlashAttribute("error", "There was an issue removing the product from the cart");
		}

		return "redirect:/user/carts";
	}


	private User getUserFromSession(HttpSession session) {
		return (User) session.getAttribute("user");
	}

	private User getUserFromRequest() {
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		if (authentication != null && authentication.isAuthenticated()) {
			return (User) authentication.getPrincipal();
		}
		return null;
	}
}






//	@GetMapping("carts")
//	public ModelAndView  getCartDetail()
//	{
//		ModelAndView mv= new ModelAndView();
//		List<Cart>carts = cartService.getCarts();
//	}
	  

