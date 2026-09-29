package com.example.docotubu.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import lombok.RequiredArgsConstructor;
import com.example.docotubu.dao.MuttersDAO;

@Controller
@RequiredArgsConstructor
public class DeleteController {

    private final MuttersDAO muttersDAO;

    @GetMapping("/Delete")
    public String doGet(@RequestParam("id") String idStr) {
        if (idStr != null && !idStr.isEmpty()) {
            try {
                int id = Integer.parseInt(idStr);
                muttersDAO.delete(id);
            } catch (NumberFormatException e) {
                e.printStackTrace();
            }
        }
        return "redirect:/Main";
    }
}
