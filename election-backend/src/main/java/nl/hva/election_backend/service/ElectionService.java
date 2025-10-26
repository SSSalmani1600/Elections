package nl.hva.election_backend.service;

import nl.hva.election_backend.model.Election;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class ElectionService {

    public List<Election> fetchUpcomingElections() throws IOException {
        String url = "https://www.kiesraad.nl/actueel/activiteiten";
        System.out.println(" [ElectionService] Ophalen van: " + url);

        Document doc = Jsoup.connect(url).get();
        System.out.println(" [ElectionService] Pagina succesvol geladen");

        List<Election> elections = new ArrayList<>();
        Elements activities = doc.select(".activity, .views-row");
        System.out.println(" [ElectionService] Gevonden activiteiten: " + activities.size());

        for (Element el : activities) {

            String title = el.select("h3 a, h3").text().trim();
            if (title.isEmpty()) continue;

            Element timeElement = el.selectFirst("time");
            String dateAttr = "";
            if (timeElement != null) {
                dateAttr = timeElement.attr("datetime").trim();
                if (dateAttr.isEmpty()) {
                    dateAttr = extractDateFromTitle(timeElement.text().trim());
                }
            }

            // 🧹 Titel opschonen
            String cleanTitle = title
                    .replaceAll("(?i)\\b\\d{1,2}\\s*[a-zA-Zé]+\\s*\\d{4}\\b", "")
                    .replaceAll("(?i)\\b\\d{1,2}\\s*[a-zA-Zé]+\\b", "")
                    .replaceAll("(?i)\\b[a-zA-Zé]+\\s*\\d{4}\\b", "")
                    .replaceAll("(?i)\\b(januari|februari|maart|april|mei|juni|juli|augustus|september|oktober|november|december|jan|feb|mrt|apr|jun|jul|aug|sep|okt|nov|dec)\\b", "")
                    .replaceAll("\\s+", " ")
                    .trim();

            if (cleanTitle.toLowerCase().contains("verkiezing")) {
                System.out.println("🗳️ [ElectionService] Verkiezing gevonden: " + cleanTitle + " (" + dateAttr + ")");
                elections.add(new Election(
                        UUID.randomUUID().toString(),
                        capitalize(cleanTitle),
                        dateAttr,
                        "confirmed"
                ));
            }
        }

        elections.sort(Comparator.comparing(Election::getDate, Comparator.nullsLast(String::compareTo)));
        System.out.println("✅ [ElectionService] Aantal gevonden verkiezingen: " + elections.size());

        return elections;
    }




    // 🧠 Datum uit zichtbare tekst halen (zoals "29 oktober 2025")
    private String extractDateFromTitle(String text) {
        Pattern patternFull = Pattern.compile("(\\d{1,2})\\s*(\\p{L}+?)\\s*(\\d{4})", Pattern.CASE_INSENSITIVE);
        Matcher matcherFull = patternFull.matcher(text);

        Map<String, String> maanden = Map.ofEntries(
                Map.entry("januari", "01"), Map.entry("jan", "01"),
                Map.entry("februari", "02"), Map.entry("feb", "02"),
                Map.entry("maart", "03"), Map.entry("mrt", "03"),
                Map.entry("april", "04"), Map.entry("apr", "04"),
                Map.entry("mei", "05"),
                Map.entry("juni", "06"), Map.entry("jun", "06"),
                Map.entry("juli", "07"), Map.entry("jul", "07"),
                Map.entry("augustus", "08"), Map.entry("aug", "08"),
                Map.entry("september", "09"), Map.entry("sep", "09"),
                Map.entry("oktober", "10"), Map.entry("okt", "10"),
                Map.entry("november", "11"), Map.entry("nov", "11"),
                Map.entry("december", "12"), Map.entry("dec", "12")
        );

        if (matcherFull.find()) {
            String day = matcherFull.group(1);
            String month = matcherFull.group(2).toLowerCase();
            String year = matcherFull.group(3);
            String maandNummer = maanden.get(month);
            if (maandNummer != null) {
                return String.format("%s-%s-%s", year, maandNummer, day.length() == 1 ? "0" + day : day);
            }
        }

        return "";
    }

    private String capitalize(String text) {
        if (text == null || text.isEmpty()) return text;
        return text.substring(0, 1).toUpperCase() + text.substring(1);
    }
}
