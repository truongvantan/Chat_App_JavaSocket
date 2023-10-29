
package com.socket.model;

import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;

/**
 *
 * @author Dell
 */
public class MyObjectInputStream extends ObjectInputStream {
    public MyObjectInputStream() throws IOException {

        // Super keyword refers to parent class instance
        super();
    }

    public MyObjectInputStream(InputStream o) throws IOException {
        super(o);
    }

    // Method of this class
    public void readStreamHeader() throws IOException {
        return;
    }
}
