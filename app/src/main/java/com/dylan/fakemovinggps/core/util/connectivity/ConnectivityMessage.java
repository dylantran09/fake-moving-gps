package com.dylan.fakemovinggps.core.util.connectivity;


import java.io.Serializable;

public final class ConnectivityMessage implements Serializable {

    private State state = State.DISCONNECTED;

    public ConnectivityMessage(State state) {
        this.state = state;
    }

    public State getState() {
        return state;
    }

    public enum State {
        CONNECTED,
        DISCONNECTED
    }
}
