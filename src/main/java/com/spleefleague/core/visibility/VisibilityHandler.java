package com.spleefleague.core.visibility;

/**
 * @author Josh Keighley
 */
public class VisibilityHandler {

    public enum Mode {

        ;

        private final String displayName;

        Mode(String displayName) {
            this.displayName = displayName;
        }

        public String getDisplayName() {
            return displayName;
        }

    }

}