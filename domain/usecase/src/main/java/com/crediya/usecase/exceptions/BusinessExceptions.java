package com.crediya.usecase.exceptions;


import java.util.logging.Logger;

public class BusinessExceptions extends RuntimeException{

    private static final Logger LOGGER = Logger.getLogger(BusinessExceptions.class.getName());

    public BusinessExceptions(String message){

        super(message);
        LOGGER.info("Entering to BusinessExceptionsconstructor method");
    }
}
