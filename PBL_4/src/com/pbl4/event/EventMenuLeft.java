package com.pbl4.event;

import com.socket.model.Model_User_Account;
import java.util.List;

public interface EventMenuLeft {
    public abstract void newUser(List<Model_User_Account> users);
}
