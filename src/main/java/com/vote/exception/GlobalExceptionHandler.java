package com.vote.exception;

import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(DuplicateVoteException.class)
    public String duplicateVoteException(DuplicateVoteException e, Model model) {
        model.addAttribute("errorMessage", e.getMessage());

        return "error/error";
    }

    @ExceptionHandler(CertificationException.class)
    public String certificationException(CertificationException e, Model model) {
        model.addAttribute("errorMessage", e.getMessage());

        return "error/error";
    }

    @ExceptionHandler(ValidateElectionStartException.class)
    public String electionEndException(ValidateElectionStartException e, Model model) {
        model.addAttribute("errorMessage", e.getMessage());

        return "error/error";
    }
}
