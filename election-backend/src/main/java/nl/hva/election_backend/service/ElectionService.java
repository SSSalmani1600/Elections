package nl.hva.election_backend.service;

import nl.hva.election_backend.model.Election;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

@Service
public class ElectionService {

    public List<Election> fetchUpcomingElections() throws IOException {
        // URL of the Dutch Electoral Council (Kiesraad) activities page
        String url = "https://www.kiesraad.nl/actueel/activiteiten";

        // Fetch and parse the HTML using Jsoup
        Document doc = Jsoup.connect(url).get();

        List<Election> elections = new ArrayList<>();
        Elements activities = doc.select(".activity");

        for (Element el : activities) {
            String title = el.select("h3").text().trim();
            String date = el.select(".meta time").attr("datetime");

            // In de for-loop:
            if (title.toLowerCase().contains("verkiezing")) {
                elections.add(new Election("", title, date, "confirmed"));
            }


            elections.add(new Election("", "Municipal elections", "2026-03-18", "projected"));
            elections.add(new Election("", "Provincial elections", "2027-03-17", "projected"));

            elections.sort(Comparator.comparing(Election::getDate));
            return elections;

        }
        return elections;
    }
}
