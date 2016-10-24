package com.spleefleague.core.utils.debugger;

public class DebuggerHostFailedException extends Exception {

    private static final String text = "%s failed to handle key: %s";
    private static final String text_response =
            "Response:" + System.lineSeparator() + "\tCode: %d" + System.lineSeparator() + "\tResponse: %s";

    public DebuggerHostFailedException(DebuggerHost host, String key) {
        super(String.format(text, host.getClass().getName(), key));
    }

    public DebuggerHostFailedException(DebuggerHost host, String key, Exception e) {
        super(String.format(text, host.getClass().getName(), key), e);
    }

    public DebuggerHostFailedException(DebuggerHost host, String key, Response r) {
        super(String.format(text, host.getClass().getName(), key) + System.lineSeparator() +
              String.format(text_response, r.getCode(), r.getText()));
    }

    public DebuggerHostFailedException(DebuggerHost host, String key, String message) {
        super(String.format(text, host.getClass().getName(), key) + System.lineSeparator() + "Message: " + message);
    }

}
