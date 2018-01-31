package com.fxs.wenda.async.handler;

import com.fasterxml.jackson.databind.annotation.JsonAppend;
import com.fxs.wenda.async.EventHandler;
import com.fxs.wenda.async.EventModel;
import com.fxs.wenda.async.EventType;
import com.fxs.wenda.model.Message;
import com.fxs.wenda.model.User;
import com.fxs.wenda.service.MessageService;
import com.fxs.wenda.service.UserService;
import com.fxs.wenda.util.WendaUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.Date;
import java.util.List;

@Component
public class LikeHandler implements EventHandler{
    @Autowired
    private MessageService messageService;

    @Autowired
    private UserService userService;

    @Override
    public void doHandle(EventModel model) {
        Message message = new Message();
        message.setFromId(WendaUtil.SYSTEM_USERID);
        message.setToId(model.getEntityOwnerId());
        User user = userService.getUser(model.getActorId());
        message.setContent("用户" + user.getName() + "赞了你的评论，http://127.0.0.1/question/" + model.getExt("questionId"));
        message.setCreatedDate(new Date());
        messageService.addMessage(message);
    }

    @Override
    public List<EventType> getSupportEventTypes() {
        return Arrays.asList(EventType.LIKE);
    }
}
