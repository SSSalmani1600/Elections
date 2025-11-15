// Model van 1 discussie (titel, auteur, tekst, tijden, aantal reacties).
//Weet hoe het zichzelf moet aanmaken of updaten.
package nl.hva.election_backend.model;

import java.time.Instant;
import java.util.Objects;
import java.util.UUID;

// dit is een discussie, gewoon simpele data (nog geen database)
public final class Discussion {
    private final String id; // unieke id
    private final String title; // titel van discussie
    private final String author; // wie heeft gepost
    private final String body; // tekst zelf
    private final Instant createdAt; // wanneer gemaakt
    private final Instant lastActivityAt; // laatste activiteit
    private final int reactionsCount; // hoeveel reacties

    // constructor, maakt discussie aan met waardes
    private Discussion(
            String id,
            String title,
            String author,
            String body,
            Instant createdAt,
            Instant lastActivityAt,
            int reactionsCount
    ) {
        this.id = Objects.requireNonNull(id); // id mag niet null zijn
        this.title = validateNonBlank(title, "title"); // check of titel ingevuld is
        this.author = validateNonBlank(author, "author"); // check of naam er is
        this.body = validateNonBlank(body, "body"); // check of tekst niet leeg is
        this.createdAt = Objects.requireNonNull(createdAt); // check op null
        this.lastActivityAt = Objects.requireNonNull(lastActivityAt); // ook checken
        if (lastActivityAt.isBefore(createdAt)) { // kan niet eerder zijn dan gemaakt
            throw new IllegalArgumentException("lastActivityAt < createdAt");
        }
        this.reactionsCount = Math.max(0, reactionsCount); // geen negatieve reacties
    }

    // checkt of tekst leeg is
    private static String validateNonBlank(String s, String field) {
        if (s == null || s.isBlank()) throw new IllegalArgumentException(field + " is leeg");
        return s.trim(); // haalt extra spaties weg
    }

    // maakt nieuwe discussie aan
    public static Discussion create(String title, String author, String body) {
        Instant now = Instant.now(); // tijd van nu
        return new Discussion(UUID.randomUUID().toString(), title, author, body, now, now, 0);
    }

    // maakt nieuwe versie met nieuwe activiteit en reacties
    public Discussion withActivity(Instant lastActivityAt, int reactionsCount) {
        return new Discussion(
                this.id,
                this.title,
                this.author,
                this.body,
                this.createdAt,
                Objects.requireNonNull(lastActivityAt),
                Math.max(0, reactionsCount)
        );
    }

    // getters, halen info op
    public String getId() { return id; }
    public String getTitle() { return title; }
    public String getAuthor() { return author; }
    public String getBody() { return body; }
    public Instant getCreatedAt() { return createdAt; }
    public Instant getLastActivityAt() { return lastActivityAt; }
    public int getReactionsCount() { return reactionsCount; }

    // checkt of 2 discussies zelfde id hebben
    @Override public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Discussion d)) return false;
        return id.equals(d.id);
    }

    @Override public int hashCode() { return id.hashCode(); }

    @Override public String toString() {
        return "Discussion{id='%s', title='%s'}".formatted(id, title);
    }
}
