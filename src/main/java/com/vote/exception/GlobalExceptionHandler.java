package com.vote.exception;

import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(DuplicateVoteException.class)
    public String duplicateVoteException(DuplicateVoteException e, Model model) {
        model.addAttribute("errorMessage", e.getMessage());

        return "error/error";
    }

    @ExceptionHandler(AccessAllowedException.class)
    public String AccessAllowedException(AccessAllowedException ex, RedirectAttributes redirectAttributes) {
        redirectAttributes.addFlashAttribute("errorMessage", ex.getMessage());
        return "redirect:/elections";
    }

    @ExceptionHandler(ElectionInProgressException.class)
    public String ElectionInProgressException(ElectionInProgressException ex, RedirectAttributes redirectAttributes) {
        redirectAttributes.addFlashAttribute("errorMessage", ex.getMessage());

        return "redirect:/election/" + ex.getId();
    }
}
