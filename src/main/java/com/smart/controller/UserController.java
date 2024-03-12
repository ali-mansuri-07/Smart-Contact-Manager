package com.smart.controller;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.security.Principal;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import com.smart.dao.ContactRepository;
import com.smart.dao.UserRepository;
import com.smart.entities.Contact;
import com.smart.entities.User;
import com.smart.helper.Message;

import jakarta.servlet.http.HttpSession;

@Controller
@RequestMapping("/user")
public class UserController {
	
	@Autowired
	private BCryptPasswordEncoder bCryptPasswordEncoder;

	@Autowired
	private UserRepository userRepository;
	
	@Autowired
	private ContactRepository contactRepository;

	@ModelAttribute
	public void addCommonData(Model model, Principal principal) {
		String userName = principal.getName();
		System.out.println("username:" + userName);
		User user = userRepository.getUserByUserName(userName);
		System.out.println("user:" + user);
		model.addAttribute("user", user);
	}

	@GetMapping("/index")
	public String dashboard(Model model, Principal principal) {
		model.addAttribute("title", "User Dashboard");
		return "normal/user_dashboard";
	}

	// open add from handler
	@GetMapping("/addContact")
	public String openAddContactForm(Model model) {
		model.addAttribute("title", "Add Contact");
		model.addAttribute("contact", new Contact());
		return "normal/add_contact_form";
	}
	
	@PostMapping("/process-contact")
	public String processContact(@ModelAttribute Contact contact,
			@RequestParam("profileImage") MultipartFile file,
			Principal principal, HttpSession session) {
		try {
			String name = principal.getName();
			User user = this.userRepository.getUserByUserName(name);
			
			//processing and uploading file
			
			if(file.isEmpty()) {
				System.out.println("File is empty");
				contact.setImage("contact.png");
			}
			else {
				contact.setImage(file.getOriginalFilename());
				File saveFile = new ClassPathResource("static/img").getFile();
				Path path = Paths.get(saveFile.getAbsolutePath()+File.separator+file.getOriginalFilename());
				Files.copy(file.getInputStream(), path, StandardCopyOption.REPLACE_EXISTING);
//				System.out.println("Image is uploaded");
				
			}
			
			contact.setUser(user);
			
			user.getContacts().add(contact);
			this.userRepository.save(user);
			System.out.println("Added to database");
			// message success......
			session.setAttribute("message",
								new Message("Your Contact is Successfully added!!! Add More...", "success"));

//			System.out.println("DATA : " + contact);
		} catch (Exception e) {
			System.out.println("ERROR" + e.getMessage());
			e.printStackTrace();
			// error message
			session.setAttribute("message", new Message("Something went wrong !! Try Again....", "danger"));
		}
		
		return "normal/add_contact_form";
	}
	
		 
		//show contacts
		@GetMapping("/show-contacts/{page}")
		public String showContact(@PathVariable("page") Integer page, Model m, Principal principal) {
			m.addAttribute("title", "Show User Contacts");
			String username = principal.getName();
			User user = this.userRepository.getUserByUserName(username);
			Pageable pageable = PageRequest.of(page, 5);
			Page<Contact> contacts = this.contactRepository.findContactsByUser(user.getId(), pageable);
			m.addAttribute("contacts", contacts);
			m.addAttribute("currentPage", page);
			m.addAttribute("totalPages", contacts.getTotalPages());
			return "normal/show_contacts";
		}
		
		
		//showing particular contact details
		@GetMapping("/{cId}/contact/")
		public String showContactDetail(@PathVariable("cId") Integer cId, Model model, Principal principal) {
			System.out.println("CID" + cId);

			Optional<Contact> contactOptional = this.contactRepository.findById(cId);
			Contact contact = contactOptional.get();

			String userName = principal.getName();
			User user = this.userRepository.getUserByUserName(userName);
			if (user.getId() == contact.getUser().getId()) {
				model.addAttribute("contact", contact);
				model.addAttribute("title", contact.getName());
			}
			return "normal/contact_detail";

		}
		
		
		@GetMapping("delete/{cid}")
		public String deleteContact(@PathVariable("cid") Integer cId, Model model,
				HttpSession session, Principal principal) {
			
			Optional<Contact> optionalContact = this.contactRepository.findById(cId);
			Contact contact = optionalContact.get();
			
//			contact.setUser(null); //unlinking the contact from user to delete it
			
			User user = this.userRepository.getUserByUserName(principal.getName());
			user.getContacts().remove(contact);
			this.userRepository.save(user);
			
			this.contactRepository.delete(contact);
			session.setAttribute("message", new Message("Contact deleted successfully!!", "success"));
			return "redirect:/user/show-contacts/0";			
		}
		
		//open update form handler
		@PostMapping("/update-contact/{cid}")
		public String updateForm(@PathVariable("cid") Integer cid, Model m) {
			m.addAttribute("title", "Update Contact");
			Contact contact = this.contactRepository.findById(cid).get();
			m.addAttribute("contact", contact);
			return "normal/update_contact";
		}
		
		//update contact handler
		@PostMapping("/process-update")
		public String updateHandler(@ModelAttribute Contact contact, @RequestParam("profileImage") MultipartFile file,
				Model m, HttpSession session, Principal principal) {

			try {
				// old contact details
				Contact oldcontactDetail = this.contactRepository.findById(contact.getcId()).get();
				// image
				if (!file.isEmpty()) {
					// file work
					// rewrite
					// delete old photo
					File deleteFile = new ClassPathResource("static/img").getFile();
					File file1 = new File(deleteFile, oldcontactDetail.getImage());
					file1.delete();

					// update new photo
					File saveFile = new ClassPathResource("static/img").getFile();
					Path path = Paths.get(saveFile.getAbsolutePath() + File.separator + file.getOriginalFilename());
					Files.copy(file.getInputStream(), path, StandardCopyOption.REPLACE_EXISTING);
					contact.setImage(file.getOriginalFilename());
				} else {
					contact.setImage(oldcontactDetail.getImage());
				}
				User user = this.userRepository.getUserByUserName(principal.getName());
				contact.setUser(user);
				this.contactRepository.save(contact);
				session.setAttribute("message", new Message("Your contact is updated...", "success"));

			} catch (Exception e) {
				e.printStackTrace();
			}
//			System.out.print("CONTACT NAME " + contact.getName());
//			System.out.print("CONTACT ID " + contact.getcId());
			return "redirect:/user/" + contact.getcId() + "/contact/";
		}
		
		
		//your profile handler
		@GetMapping("/profile")
		public String yourProfile(Model m) {
			m.addAttribute("title", "Profile Page");
			
			
			return "normal/profile";
		}
		
		
		//open settings handler
		@GetMapping("/settings")
		public String openSettings() {
			
			return "normal/settings";
		}
		
		// change password handler
		@PostMapping("/change-password")
		public String changePassword(@RequestParam("oldPassword") String oldPassword,
				@RequestParam("newPassword") String newPassword, Principal principal, HttpSession session) {
//			System.out.println("OLD PASSWORD " + oldPassword);
//			System.out.println("NEW PASSWORD " + newPassword);

			String userName = principal.getName();
			User currentUser = this.userRepository.getUserByUserName(userName);
//			System.out.println(currentUser.getPassword());

			if (this.bCryptPasswordEncoder.matches(oldPassword, currentUser.getPassword())) {
				currentUser.setPassword(this.bCryptPasswordEncoder.encode(newPassword));
				this.userRepository.save(currentUser);
				session.setAttribute("message", new Message("Your password is successfully changed...", "success"));

			} else {
				session.setAttribute("message", new Message("Please Enter correct old password !!!..", "danger"));
				return "redirect:/user/settings";
			}
			return "redirect:/user/index";
		}
		
}
























