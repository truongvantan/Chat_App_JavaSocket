package com.pbl4.event;

import com.socket.model.Model_Register;


public interface EventLogin {

    public abstract void login();

    public abstract void register(Model_Register data, EventMessage message);

    public abstract void goRegister();

    public abstract void goLogin();
}
