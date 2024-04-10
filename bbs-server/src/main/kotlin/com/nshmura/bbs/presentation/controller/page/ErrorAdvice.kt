package com.nshmura.bbs.presentation.controller.page

import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.http.HttpStatus
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.ControllerAdvice
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.ResponseStatus

@ControllerAdvice
class ErrorAdvice {

    private val logger: Logger = LoggerFactory.getLogger(ErrorAdvice::class.java)

    @ExceptionHandler(IllegalArgumentException::class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    fun illegalArgumentException(exception: IllegalArgumentException, model: Model): String {
        logger.error("IllegalArgumentException", exception)
        model.addAttribute("errorMessage", exception.message)
        return "error"
    }

    @ExceptionHandler(Throwable::class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    fun exception(throwable: Throwable?, model: Model): String {
        logger.error("Exception during execution of SpringSecurity application", throwable)
        val errorMessage = (if (throwable != null) throwable.message else "Unknown error")
        model.addAttribute("errorMessage", errorMessage)
        return "error"
    }
}
