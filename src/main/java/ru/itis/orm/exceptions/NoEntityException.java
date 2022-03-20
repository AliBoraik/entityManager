package ru.itis.orm.exceptions;

public class NoEntityException extends Exception {
    public NoEntityException(String error) {
        super(error);
    }
}
