// Model van 1 reactie (wie, wat, wanneer).
//Wordt gebruikt bij discussies.
package nl.hva.election_backend.model;

// dit is reactie op discussie
public class Reaction {
    private String author; // wie heeft gereageerd
    private String message; // wat is gezegd
    private String createdAt; // wanneer gepost

    // maakt nieuwe reactie aan
    public Reaction(String author, String message, String createdAt) {
        this.author = author;
        this.message = message;
        this.createdAt = createdAt;
    }

    // haalt naam op
    public String getAuthor() { return author; }

    // haalt tekst op
    public String getMessage() { return message; }

    // haalt tijd op
    public String getCreatedAt() { return createdAt; }
}
