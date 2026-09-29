package com.example.docotubu.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class Mutter {
    private int id;
    private String userName;
    private String text;

    public Mutter(String userName, String text) {
        this.userName = userName;
        this.text = text;
    }
}
