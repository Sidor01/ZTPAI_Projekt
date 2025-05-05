package org.example.skillwheel.exception;


public class LoginExceptions {
    public static class WrongPasswordException extends RuntimeException {
        public WrongPasswordException(String message) {
            super(message);
        }


    }
    public static class EmptyLoginException extends RuntimeException {
        public EmptyLoginException(String message) {
            super(message);
        }
    }
}
