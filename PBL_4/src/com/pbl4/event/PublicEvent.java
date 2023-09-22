package com.pbl4.event;

public class PublicEvent { // Singleton

    private static PublicEvent instance;
    private EventMain eventMain;
    private EventImageView eventImageView;
    private EventChat eventChat;
    private EventLogin eventLogin;

    public static PublicEvent getInstance() {
        if (instance == null) {
            instance = new PublicEvent();
        }
        return instance;
    }

    private PublicEvent() {

    }
    
    public void addEventMain(EventMain event) {
        this.eventMain = event;
    }

    public void addEventImageView(EventImageView event) {
        this.eventImageView = event;
    }

    public void addEventChat(EventChat event) {
        this.eventChat = event;
    }

    public void addEventLogin(EventLogin event) {
        this.eventLogin = event;
    }

    public EventMain getEventMain() {
        return this.eventMain;
    }
    public EventImageView getEventImageView() {
        return this.eventImageView;
    }

    public EventChat getEventChat() {
        return this.eventChat;
    }

    public EventLogin getEventLogin() {
        return this.eventLogin;
    }
}
