package ru.yandex.practicum.filmorate.model;

public enum EventType {
    LIKE ("LIKE"),
    REVIEW ("REVIEW"),
    FRIEND ("FRIEND");

    EventType(String title) {
        this.title = title;
    }

    public String getTitle() {
        return title;
    }

    private String title;
}