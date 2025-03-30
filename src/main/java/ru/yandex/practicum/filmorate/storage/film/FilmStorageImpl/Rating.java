package ru.yandex.practicum.filmorate.storage.film.FilmStorageImpl;

public enum Rating {
    G ("G"),
    PG ("PG"),
    PG_13 ("PG-13"),
    R ("R"),
    NC_17 ("NC-17");

    private final String title;

    Rating(String title) {
        this.title = title;
    }

    public String getTitle() {
        return title;
    }

    @Override
    public String toString() {
        return "Rating {" +
                "rating ='" + title + '\'' +
                '}';
    }
}
