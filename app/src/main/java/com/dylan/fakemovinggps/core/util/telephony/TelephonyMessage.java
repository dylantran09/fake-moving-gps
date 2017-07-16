package com.dylan.fakemovinggps.core.util.telephony;

import java.io.Serializable;
import java.util.Date;


public final class TelephonyMessage implements Serializable {

    private State state = State.IDDLE;
    private String number;
    private Date start;
    private Date end;

    public TelephonyMessage(State state, String number, Date start, Date end) {
        this.state = state;
        this.number = number;
        this.start = start;
        this.end = end;
    }

    public State getState() {
        return state;
    }

    public String getNumber() {
        return number;
    }

    public Date getStart() {
        return start;
    }

    public Date getEnd() {
        return end;
    }

    public enum State {
        IDDLE,
        INCOMING_RECEIVED,
        INCOMING_ANSWERED,
        INCOMING_ENDED,
        OUTGOING_STARTED,
        OUTGOING_ENDED,
        MISSED
    }
}
