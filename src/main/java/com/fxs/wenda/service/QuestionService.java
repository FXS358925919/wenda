package com.fxs.wenda.service;


import com.fxs.wenda.dao.QuestionDAO;
import com.fxs.wenda.model.Question;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.web.util.HtmlUtils;

import java.util.List;

/**
 * Created by nowcoder on 2016/7/15.
 */
@Service
public class QuestionService {
    @Autowired
    QuestionDAO questionDAO;

    @Autowired
    SensitiveService sensitiveService;

    public Question getById(int id) {
        return questionDAO.getById(id);
    }

    public List<Question> getLatestQuestions(int userId, int offset, int limit) {
        return questionDAO.selectLatestQuestions(userId, offset, limit);
    }

    public Question selectById(int id) {
        return questionDAO.selectById(id);
    }
    public int addQuestion(Question question) {
        question.setContent(HtmlUtils.htmlEscape(question.getContent()));
        question.setTitle(HtmlUtils.htmlEscape(question.getTitle()));

        question.setContent(sensitiveService.filter(question.getContent()));
        question.setTitle(sensitiveService.filter(question.getTitle()));
        return questionDAO.addQuestion(question) > 0 ? question.getId():0;
    }
    public int updateCommentCount(int id, int count) {
        return questionDAO.updateCommentCount(id, count);
    }
}
