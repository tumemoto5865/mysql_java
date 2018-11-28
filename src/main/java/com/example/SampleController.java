package com.example;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@Controller
public class SampleController {

    
    @Autowired
    private JdbcTemplate jdbcTemplate;

    @RequestMapping(path = "/sample", method = RequestMethod.GET)
    String index(Model model) {
		List<User> userList = new ArrayList<>();
		//JdbcTemplate & ラムダ式 & 三項演算子
		jdbcTemplate.query("select * from user ", 
				new BeanPropertyRowMapper<User>(User.class)).forEach(n -> {
				      User user = new User();
				      user.setId(n.getId());
				      user.setName(n.getName());
				      userList.add(user);
				});
        model.addAttribute("list", userList);
        return "sample/index";
    }

    @RequestMapping(path = "/sample/{id}", method = RequestMethod.GET)
    String show(Model model, @PathVariable("id") int id) {
        List<User> list = jdbcTemplate.queryForObject("select * from user where id = ? ", new Object[] { id }, new UserMapper());
        model.addAttribute("list", list);
        return "sample/index";
    }
    


	@RequestMapping(path = "/sample", method = RequestMethod.POST)
	String create(Model model, @ModelAttribute UserForm userForm) {
		jdbcTemplate.update("INSERT INTO user (name) values (?)", userForm.getName());
		return "redirect:/sample";
	}

	@RequestMapping(path = "/sample/{id}", method = RequestMethod.PUT)
	String update(Model model, @ModelAttribute UserForm userForm, @PathVariable("id") int id) {
		jdbcTemplate.update("UPDATE user SET name = ? where id = ? ", userForm.getName(), id);
		return "redirect:/sample";
	}

	@RequestMapping(path = "/sample/{id}", method = RequestMethod.DELETE)
	String destory(Model model, @PathVariable("id") int id) {
		jdbcTemplate.update("delete from user where id = ? ", id);
		return "redirect:/sample";
	}

	@RequestMapping(path = "/sample/upload", method = RequestMethod.GET)
	String uploadview(Model model) {
		return "sample/upload";
	}

	@RequestMapping(path = "/sample/upload", method = RequestMethod.POST)
	String upload(Model model, UploadForm uploadForm) {
		if (uploadForm.getFile().isEmpty()) {
			return "sample/upload";
		}

		// check upload distination directory.If there was no directory, make
		// func.
		Path path = Paths.get("/Users/kusakai/Documents/workspace-sts-3.8.4.RELEASE/demo-kusa/image");
		if (!Files.exists(path)) {
			try {
				Files.createDirectory(path);
			} catch (NoSuchFileException ex) {
				System.err.println(ex);
			} catch (IOException ex) {
				System.err.println(ex);
			}
		}

		int dot = uploadForm.getFile().getOriginalFilename().lastIndexOf(".");
		String extention = "";
		if (dot > 0) {
			extention = uploadForm.getFile().getOriginalFilename().substring(dot).toLowerCase();
		}
		String filename = DateTimeFormatter.ofPattern("yyyyMMddHHmmssSSS").format(LocalDateTime.now());
		Path uploadfile = Paths
				.get("/Users/kusakai/Documents/workspace-sts-3.8.4.RELEASE/demo-kusa/image/" + filename + extention);

		try (OutputStream os = Files.newOutputStream(uploadfile, StandardOpenOption.CREATE)) {
			byte[] bytes = uploadForm.getFile().getBytes();
			os.write(bytes);
		} catch (IOException ex) {
			System.err.println(ex);
		}

		return "redirect:/sample";
	}
}
