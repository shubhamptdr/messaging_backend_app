package com.messaging.app.repository;

import com.messaging.app.exceptions.SomethingWrongException;
import com.messaging.app.models.Group;
import com.messaging.app.models.Message;
import com.messaging.app.models.User;

import java.util.*;

@org.springframework.stereotype.Repository
public class Repository {

    private HashMap<Group, List<User>> groupUserMap;
    private HashMap<Group, List<Message>> groupMessageMap;
    private HashMap<Message, User> senderMap;
    private HashMap<Group, User> adminMap;
    private HashSet<String> userMobile;
    private int customGroupCount;
    private int messageId;

    public Repository(){
        this.groupMessageMap = new HashMap<Group, List<Message>>();
        this.groupUserMap = new HashMap<Group, List<User>>();
        this.senderMap = new HashMap<Message, User>();
        this.adminMap = new HashMap<Group, User>();
        this.userMobile = new HashSet<>();
        this.customGroupCount = 0;
        this.messageId = 0;
    }
    public String createUser(String name, String mobile) throws SomethingWrongException {
        if(userMobile.contains(mobile)){
            throw new SomethingWrongException("User already exists");
        }
        // create user
        User user= new User(name,mobile);
        userMobile.add(mobile);
        return "SUCCESS";
    }

    public Group createGroup(List<User> users) {
        int noOfMember = users.size();
        String groupName = "";

        if(noOfMember == 2){
            groupName = users.get(1).getName();
        }
        else {
            this.customGroupCount++;
            groupName = "Group " + this.customGroupCount;
        }

        User admin = users.get(0);
        Group group = new Group(groupName,noOfMember);
        adminMap.put(group,admin);
        groupUserMap.put(group,users);
        groupMessageMap.put(group,new ArrayList<Message>());
        return group;
    }

    public int createMessage(String content) {
        this.messageId++;
        Message message = new Message(this.messageId,content,new Date());
        return message.getId();
    }

    public int sendMessage(Message message, User sender, Group group) throws SomethingWrongException {
        //Throw "Group does not exist" if the mentioned group does not exist
        //Throw "You are not allowed to send message" if the sender is not a member of the group
        //If the message is sent successfully, return the final number of messages in that group.
        if(!groupUserMap.containsKey(group)){
            throw new SomethingWrongException("Group does not exist");
        }

        List<User> users = groupUserMap.get(group);
        if(!users.contains(sender)){
            throw new SomethingWrongException("You are not allowed to send message");
        }
        if (groupMessageMap.containsKey(group)) {
            groupMessageMap.get(group).add(message);
        }

        senderMap.put(message,sender);

        return groupMessageMap.get(group).size();
    }

    public String changeAdmin(User approver, User user, Group group) throws SomethingWrongException {
        if(!groupUserMap.containsKey(group)){
            throw new SomethingWrongException("Group does not exist");
        }

        if (!Objects.equals(adminMap.get(group),approver)){
            throw new SomethingWrongException("Approver does not have rights");
        }

        boolean isFound = false;
        List<User> users = groupUserMap.get(group);
        for(User user1 : users){
            if(Objects.equals(user1,user)){
                isFound = true;
                break;
            }
        }
        if(!isFound){
            throw new SomethingWrongException("User is not a participant");
        }
        adminMap.put(group,user);

        return "SUCCESS";
    }

    public int removeUser(User user) throws SomethingWrongException {

        Group group2 = null;
        boolean isFound = false;
        for(Group group1 : groupUserMap.keySet()){
            for (User user1 : groupUserMap.get(group1)){
                if(Objects.equals(user1,user)){
                    group2 = group1;
                    isFound = true;
                    break;
                }
            }
        }
        if(!isFound){
            throw new SomethingWrongException("User not found");
        }
        if(Objects.equals(adminMap.get(group2),user)){
            throw new SomethingWrongException("Cannot remove admin");
        }
        // remove user from group
        List<User> users = groupUserMap.get(group2);
        for (User user2 : users){
            if(Objects.equals(user2,user)){
                users.remove(user2);
                break;
            }
        }
        // update changes
        groupUserMap.put(group2,users);
        //
        for ( Message message : senderMap.keySet()){
            if(Objects.equals(senderMap.get(message),user)){
                senderMap.remove(message);
                List<Message> msgs = groupMessageMap.get(group2);
                for (Message msg : msgs){
                    if(Objects.equals(msg,message)){
                        msgs.remove(message);
                    }
                }
                groupMessageMap.put(group2,msgs);
            }
        }

        return groupMessageMap.get(group2).size() + groupUserMap.get(group2).size() + senderMap.size();
    }

    public String findMessage(Date start, Date end, int k) throws SomethingWrongException {
        List<Message> messages = new ArrayList<>();
        // wrap the all message in a list
        for(Group group: groupMessageMap.keySet()){
            messages.addAll(groupMessageMap.get(group));
        }
        //
        List<Message> filteredMessages = new ArrayList<>();
        for(Message message: messages){
            if(message.getTimestamp().after(start) && message.getTimestamp().before(end)){
                filteredMessages.add(message);
            }
        }

        if(filteredMessages.size() < k){
            throw new SomethingWrongException("K is greater than the number of messages");
        }
        // sort
        Collections.sort(filteredMessages, new Comparator<Message>(){
            public int compare(Message m1, Message m2){
                return m2.getTimestamp().compareTo(m1.getTimestamp());
            }
        });
        // return kth latest message content
        return filteredMessages.get(k-1).getContent();
    }
}
