package org.qo.tinymsg;

public class Exceptions extends Exception{
    static class IllegalConfigurationException extends Exception {
        public IllegalConfigurationException(String message) {
            super(message);
        }
    }

}
