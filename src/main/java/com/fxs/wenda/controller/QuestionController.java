package com.fxs.wenda.controller;

import com.fasterxml.jackson.databind.annotation.JsonAppend;
import com.fxs.wenda.async.EventModel;
import com.fxs.wenda.async.EventProducer;
import com.fxs.wenda.async.EventType;
import com.fxs.wenda.dao.CommentDAO;
import com.fxs.wenda.model.*;
import com.fxs.wenda.service.CommentService;
import com.fxs.wenda.service.LikeService;
import com.fxs.wenda.service.QuestionService;
import com.fxs.wenda.service.UserService;
import com.fxs.wenda.util.WendaUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Controller
public class QuestionController {
    private static final Logger logger = LoggerFactory.getLogger(QuestionController.class);

    @Autowired
    private QuestionService questionService;

    @Autowired
    private UserService userService;

    @Autowired
    private HostHolder hostHolder;

    @Autowired
    private CommentService commentService;

    @Autowired
    private LikeService likeService;

    @Autowired
    private EventProducer eventProducer;

    @RequestMapping(value = "/question/add", method = RequestMethod.POST)
    @ResponseBody
    public String addQuestion(@RequestParam("title") String title,@RequestParam("content") String content){
        try {
            Question question = new Question();
            question.setContent(content);
            question.setTitle(title);
            question.setCommentCount(0);
            question.setCreatedDate(new Date());
            if (hostHolder.getUser() == null) {
                return WendaUtil.getJSONString(999);
            }else{
                question.setUserId(hostHolder.getUser().getId());
            }
            if(questionService.addQuestion(question) > 0){
                eventProducer.fireEvent(new EventModel(EventType.ADD_QUESTION)
                        .setActorId(question.getUserId()).setEntityId(question.getId())
                        .setExt("title", question.getTitle()).setExt("content", question.getContent()));
                return WendaUtil.getJSONString(0);
            }

        } catch (Exception e) {
            logger.error("增加题目失败"+e.getMessage());
        }
        return WendaUtil.getJSONString(1,"失败");
    }

    @RequestMapping(value = "/question/{qid}")
    public String questionDetail(@PathVariable("qid") int qid,Model model) {
        Question question = questionService.selectById(qid);
        model.addAttribute("question", question);
        model.addAttribute("user",userService.getUser(question.getUserId()));
        List<Comment> commentList = commentService.getCommentsByEntity(qid, EntityType.ENTITY_QUESTION);
        List<ViewObject> vos = new ArrayList<>();
        for (Comment comment : commentList) {
            ViewObject vo = new ViewObject();
            if (hostHolder.getUser() == null) {
                vo.set("liked", 0);
            }else{
                vo.set("liked",likeService.getLikeStatus(hostHolder.getUser().getId(),EntityType.ENTITY_COMMENT,comment.getId()));
            }
            vo.set("likeCount",likeService.getLikeCount(EntityType.ENTITY_COMMENT,comment.getId()));
            vo.set("comment", comment);
            vo.set("user", userService.getUser(comment.getUserId()));
            vos.add(vo);
        }
        model.addAttribute("comments", vos);
        return "detail";

    }
}
