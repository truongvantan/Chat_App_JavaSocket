package com.socket.model;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.OutputStream;

public class MyObjectOutputStream extends ObjectOutputStream {

    public MyObjectOutputStream() throws IOException {

        // Super keyword refers to parent class instance
        super();
    }

    public MyObjectOutputStream(OutputStream o) throws IOException {
        super(o);
    }

    // Method of this class
    public void writeStreamHeader() throws IOException {
        return;
    }
}
