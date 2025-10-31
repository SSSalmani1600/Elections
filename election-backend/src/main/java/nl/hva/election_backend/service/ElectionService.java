package nl.hva.election_backend.service;

import nl.hva.election_backend.model.Election;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class ElectionService {

    private static final Logger logger = LoggerFactory.getLogger(ElectionService.class);

    @Value("${election.source.url}")
    private String electionSourceUrl;

    private static final String STATUS_CONFIRMED = "confirmed";

    public List<Election> fetchUpcomingElections() throws IOException {
        logger.info("[ElectionService] Ophalen van: {}", electionSourceUrl);

        final Document doc = Jsoup.connect(electionSourceUrl).get();
        final Elements activities = doc.select(".activity, .views-row");
        logger.info("[ElectionService] Gevonden activiteiten: {}", activities.size());

        final List<Election> elections = new ArrayList<>();

        for (Element el : activities) {
            parseElection(el).ifPresent(elections::add);
        }

        elections.sort(Comparator.comparing(Election::getDate, Comparator.nullsLast(String::compareTo)));
        logger.info("✅ Aantal gevonden verkiezingen: {}", elections.size());

        return elections;
    }

    /** 🔍 Parse één HTML-element naar een Election, als die geldig is */
    private Optional<Election> parseElection(Element el) {
        String title = el.select("h3 a, h3").text().trim();
        if (title.isEmpty()) return Optional.empty();

        String date = extractDateFromElement(el);
        String cleanTitle = cleanTitle(title);

        if (!cleanTitle.toLowerCase().contains("verkiezing")) return Optional.empty();

        logger.info("🗳️ Verkiezing gevonden: {} ({})", cleanTitle, date);
        return Optional.of(new Election(
                UUID.randomUUID().toString(),
                capitalize(cleanTitle),
                date,
                STATUS_CONFIRMED
        ));
    }

    /** 🧠 Haalt datum uit <time> element of uit tekst */
    private String extractDateFromElement(Element el) {
        return Optional.ofNullable(el.selectFirst("time"))
                .map(time -> {
                    String datetime = time.attr("datetime").trim();
                    return datetime.isEmpty()
                            ? extractDateFromTitle(time.text().trim())
                            : datetime;
                })
                .orElse("");
    }

    /** 🧼 Maakt titel schoon door datums en maanden te verwijderen */
    private String cleanTitle(String title) {
        return title
                .replaceAll("(?i)\\b\\d{1,2}\\s*[a-zA-Zé]+\\s*\\d{4}\\b", "")
                .replaceAll("(?i)\\b\\d{1,2}\\s*[a-zA-Zé]+\\b", "")
                .replaceAll("(?i)\\b[a-zA-Zé]+\\s*\\d{4}\\b", "")
                .replaceAll("(?i)\\b(januari|februari|maart|april|mei|juni|juli|augustus|september|oktober|november|december|"
                        + "jan|feb|mrt|apr|jun|jul|aug|sep|okt|nov|dec)\\b", "")
                .replaceAll("\\s+", " ")
                .trim();
    }

    /** 📅 Datum extraheren uit tekst zoals '29 oktober 2025' */
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
