package com.messaging.app.services;

import com.messaging.app.models.Group;
import com.messaging.app.models.Message;
import com.messaging.app.models.User;
import com.messaging.app.repository.Repository;

import java.util.Date;
import java.util.List;

@org.springframework.stereotype.Service
public class Service {
    Repository repository = new Repository();;

    public String createUser(String name, String mobile) throws Exception {
        return repository.createUser(name,mobile);
    }

    public Group createGroup(List<User> users) {
        return repository.createGroup(users);
    }

    public int createMessage(String content) {
        return repository.createMessage(content);
    }

    public int sendMessage(Message message, User sender, Group group) throws Exception {
        return repository.sendMessage(message,sender,group);
    }

    public String changeAdmin(User approver, User user, Group group) throws Exception {
        return repository.changeAdmin(approver,user,group);
    }

    public int removeUser(User user) throws Exception {
        return repository.removeUser(user);
    }

    public String findMessage(Date start, Date end, int k) throws Exception {
        return repository.findMessage(start,end,k);
    }
}
