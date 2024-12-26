package com.jennyduarte.sis.manejadores;

import com.jennyduarte.sis.exception.RecursoNoEncontradoException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.ModelAndView;

//@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(RecursoNoEncontradoException.class)
    public ModelAndView manejarRecursoNoEncontrado(RecursoNoEncontradoException ex) {
        ModelAndView modelAndView = new ModelAndView("error");
        modelAndView.addObject("mensaje", ex.getMessage());
        return modelAndView;
    }

    @ExceptionHandler(Exception.class)
    public ModelAndView manejarExcepcionesGenerales(Exception ex) {
        ModelAndView modelAndView = new ModelAndView("error");
        modelAndView.addObject("mensaje", "Ocurrió un error inesperado. Por favor, intenta más tarde.");
        return modelAndView;
    }
}