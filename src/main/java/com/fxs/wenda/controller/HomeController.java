package com.fxs.wenda.controller;

import com.fxs.wenda.model.EntityType;
import com.fxs.wenda.model.Question;
import com.fxs.wenda.model.ViewObject;
import com.fxs.wenda.service.FollowService;
import com.fxs.wenda.service.QuestionService;
import com.fxs.wenda.service.UserService;
import org.apache.coyote.RequestGroupInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by nowcoder on 2016/7/15.
 */
@Controller
public class HomeController {
    private static final Logger logger = LoggerFactory.getLogger(HomeController.class);
    @Autowired
    private UserService userService;

    @Autowired
    FollowService followService;

    @Autowired
    private QuestionService questionService;
    @RequestMapping(value = {"/","/index"},method =  {RequestMethod.GET,RequestMethod.POST})
    public String index(Model model) {
        List<ViewObject> vos = getQuestions(0, 0, 10);
        model.addAttribute("vos", vos);
        return "index";
    }

    @RequestMapping(value = "/user/{userId}", method = {RequestMethod.GET, RequestMethod.POST})
    public String userIndex(Model model,@PathVariable("userId") int userId) {
        List<ViewObject> vos = getQuestions(userId, 0, 10);
        model.addAttribute("vos", vos);
        return "index";
    }

    private List<ViewObject> getQuestions(int userId, int offset, int limit){
        List<Question> questions = questionService.getLatestQuestions(userId, offset, limit);
        List<ViewObject> vos = new ArrayList<>();
        for (Question question : questions) {
            ViewObject vo = new ViewObject();
            vo.set("question", question);
            vo.set("followCount", followService.getFollowerCount(EntityType.ENTITY_QUESTION, question.getId()));
            vo.set("user", userService.getUser(question.getUserId()));
            vos.add(vo);
        }
        return vos;
    }
}
