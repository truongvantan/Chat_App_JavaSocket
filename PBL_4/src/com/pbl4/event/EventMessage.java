package com.pbl4.event;

import com.socket.model.Model_Message;


public interface EventMessage {
    public abstract void callMessage(Model_Message message);
}
