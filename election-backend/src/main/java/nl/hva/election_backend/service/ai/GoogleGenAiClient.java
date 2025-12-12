package nl.hva.election_backend.service.ai;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import nl.hva.election_backend.dto.ModerationResponse;
import okhttp3.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.List;

@Service
public class GoogleGenAiClient implements AiModerationClient {

    @Value("${gemini.api.key}")
    private String apiKey;

    @Value("${gemini.api.url}")
    private String geminiUrl;

    private final OkHttpClient http = new OkHttpClient();
    private final ObjectMapper mapper = new ObjectMapper();

    @Override
    public ModerationResponse analyzeText(String text) {

        String cleanInput = text.replace("\"", "'");

        String prompt = """
You are a strict AI content moderator for a Dutch discussion platform.

Return ONLY valid JSON:

{
  "isToxic": boolean,
  "toxicityReason": string | null,
  "flaggedWords": string[],
  "isMisinformation": boolean,
  "misinformationDetails": string | null
}

Analyze this message: "%s"
""".formatted(cleanInput);

        String requestJson = """
        {
          "contents": [
            {
              "role": "user",
              "parts": [
                { "text": "%s" }
              ]
            }
          ]
        }
        """.formatted(prompt.replace("\"", "'"));

        Request request = new Request.Builder()
                .url(geminiUrl + "?key=" + apiKey)
                .addHeader("Content-Type", "application/json; charset=utf-8")
                .post(RequestBody.create(requestJson, MediaType.get("application/json; charset=utf-8")))
                .build();

        try (Response response = http.newCall(request).execute()) {

            String body = response.body() != null ? response.body().string() : "";

            if (!response.isSuccessful()) {
                System.err.println("❌ Gemini error: " + response.code());
                System.err.println("❌ Gemini body: " + body);
                return null;
            }

            JsonNode root = mapper.readTree(body);

            JsonNode textNode = root
                    .path("candidates").path(0)
                    .path("content").path("parts").path(0)
                    .path("text");

            if (textNode.isMissingNode()) {
                System.err.println("⚠ Gemini returned no usable text: " + body);
                return null;
            }

            String jsonString = textNode.asText()
                    .replace("```json", "")
                    .replace("```", "")
                    .trim();

            JsonNode json = mapper.readTree(jsonString);

            return new ModerationResponse(
                    json.path("isToxic").asBoolean(false),
                    json.has("flaggedWords")
                            ? mapper.convertValue(json.get("flaggedWords"), List.class)
                            : List.of(),
                    json.path("toxicityReason").isNull() ? null : json.path("toxicityReason").asText(),
                    json.path("isMisinformation").asBoolean(false),
                    json.path("misinformationDetails").isNull() ? null : json.path("misinformationDetails").asText()
            );

        } catch (Exception e) {
            System.err.println("⚠ AI Moderation parse error: " + e.getMessage());
            return null;
        }
    }
}
