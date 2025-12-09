package nl.hva.election_backend.service.ai;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import nl.hva.election_backend.dto.ModerationResponse;
import okhttp3.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;

@Service
public class GoogleGenAiClient implements AiModerationClient {

    @Value("${gemini.api.key}")
    private String apiKey;


    private static final String GEMINI_URL =
            "https://generativelanguage.googleapis.com/v1beta/models/gemini-flash-latest:generateContent";

    private final OkHttpClient http = new OkHttpClient();
    private final ObjectMapper mapper = new ObjectMapper();

    @Override
    public ModerationResponse analyzeText(String text) {

        String cleanInput = text.replace("\"", "'").replace("\n", "\\n");

        String prompt = """
You are a strict AI content moderator for a Dutch discussion platform.

RULES:
- The word "homo" MUST be flagged as offensive if used alone or as an insult.
- Detect all insults, slurs, hate speech, harassment (Dutch + English).
- If unsure, classify as offensive.

Return STRICT JSON ONLY:

{
  "isToxic": true/false,
  "toxicityReason": "string or null",
  "flaggedWords": ["word1","word2"],
  "isMisinformation": true/false,
  "misinformationDetails": "string or null"
}

Analyze this message: "%s"
""".formatted(cleanInput);

        String requestJson = """
{
  "contents": [
    {
      "parts": [
        { "text": "%s" }
      ]
    }
  ]
}
""".formatted(prompt.replace("\"", "'"));

        Request request = new Request.Builder()
                .url(GEMINI_URL + "?key=" + apiKey)
                .addHeader("Content-Type", "application/json")
                .post(RequestBody.create(requestJson, MediaType.get("application/json")))
                .build();

        try (Response response = http.newCall(request).execute()) {

            if (!response.isSuccessful()) {
                System.err.println("❌ Gemini API error: " + response);
                return new ModerationResponse();
            }

            String body = response.body().string();
            JsonNode root = mapper.readTree(body);

            JsonNode textNode = root
                    .path("candidates").path(0)
                    .path("content").path("parts").path(0)
                    .path("text");

            if (textNode.isMissingNode()) {
                System.err.println("⚠ Gemini returned no usable text: " + body);
                return new ModerationResponse();
            }

            // Strip code fencing if needed
            String jsonString = textNode.asText()
                    .trim()
                    .replace("```json", "")
                    .replace("```", "")
                    .trim();

            JsonNode json = mapper.readTree(jsonString);

            boolean toxic = json.path("isToxic").asBoolean(false);
            String reason = json.path("toxicityReason").isNull() ? null : json.path("toxicityReason").asText();

            boolean misinfo = json.path("isMisinformation").asBoolean(false);
            String misinfoDetails = json.path("misinformationDetails").isNull()
                    ? null : json.path("misinformationDetails").asText();

            List<String> flaggedWords =
                    json.has("flaggedWords") && json.get("flaggedWords").isArray()
                            ? mapper.convertValue(json.get("flaggedWords"), List.class)
                            : Collections.emptyList();

            return new ModerationResponse(
                    toxic,
                    flaggedWords,
                    reason,
                    misinfo,
                    misinfoDetails
            );

        } catch (Exception e) {
            System.err.println("⚠ AI Moderation parsing error: " + e.getMessage());
            return new ModerationResponse();
        }
    }
}
