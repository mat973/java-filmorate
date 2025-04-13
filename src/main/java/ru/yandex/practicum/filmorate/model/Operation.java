package ru.yandex.practicum.filmorate.model;

public enum Operation {
    REMOVE("REMOVE"), ADD("ADD"), UPDATE("UPDATE");

    public String title;

    Operation(String title) {
        this.title = title;
    }

    public String getTitle() {
        return title;
    }
}