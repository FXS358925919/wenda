package com.fxs.wenda;

import com.fxs.wenda.dao.QuestionDAO;
import com.fxs.wenda.dao.UserDAO;
import com.fxs.wenda.model.EntityType;
import com.fxs.wenda.model.Question;
import com.fxs.wenda.model.User;
import com.fxs.wenda.service.FollowService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.Date;
import java.util.Random;
@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = WendaApplication.class)
@Sql("/init-schema.sql")
public class InitDatabaseTests {
	@Autowired
	UserDAO userDAO;

	@Autowired
	QuestionDAO questionDAO;

	@Autowired
	FollowService followService;
	@Test
	public void contextLoads() {
		Random random = new Random();
		for (int i = 0; i < 11; ++i) {
			User user = new User();
			user.setHeadUrl(String.format("http://images.nowcoder.com/head/%dt.png", random.nextInt(1000)));
			user.setName(String.format("USER%d", i));
			user.setPassword("");
			user.setSalt("");
			userDAO.addUser(user);


			Question question = new Question();
			question.setCommentCount(i);
			Date date = new Date();
			date.setTime(date.getTime() + 1000 * 3600 * 5 * i);
			question.setCreatedDate(date);
			question.setUserId(i + 1);
			question.setTitle(String.format("TITLE{%d}", i));
			question.setContent(String.format("Balaababalalalal Content %d", i));
			questionDAO.addQuestion(question);
			questionDAO.selectLatestQuestions(0, 10, 10);
			for (int j = 1; j < i; ++j) {
				followService.follow(j, EntityType.ENTITY_USER, i);
			}
		}
		followService.follow(12, EntityType.ENTITY_USER, 2);
	}

}
