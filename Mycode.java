package TextSummary;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonParseException;
import okhttp3.*;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

public class Mycode {

    private static final String API_KEY = "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";
    private static final String API_URL = "https://api.ollama.com/v3/qwen2-0.5B/summarize";

    public static void main(String[] args) {
        if (args.length != 1) {
            System.out.println("Usage: java Mycode <input-file>");
            System.exit(1);
        }

        String inputFile = args[0];

        try {
            String inputText = readInputFile(inputFile);
            String summary = summarizeWithOllamaAPI(inputText);
            System.out.println("Summary:");
            System.out.println(summary);
        } catch (IOException e) {
            System.err.println("Error reading file: " + e.getMessage());
        }
    }

    private static String readInputFile(String inputFile) throws IOException {
        StringBuilder content = new StringBuilder();
        try (BufferedReader reader = new BufferedReader(new FileReader(inputFile))) {
            String line;
            while ((line = reader.readLine()) != null) {
                content.append(line).append("\n");
            }
        }
        return content.toString();
    }

    private static String summarizeWithOllamaAPI(String inputText) throws IOException {
        OkHttpClient client = new OkHttpClient();

        JsonObject jsonBody = new JsonObject();
        jsonBody.addProperty("model", "qwen2");
        jsonBody.addProperty("query", inputText);

        RequestBody requestBody = RequestBody.create(jsonBody.toString(), MediaType.parse("application/json"));

        Request request = new Request.Builder()
                .url(API_URL)
                .addHeader("Authorization", "Bearer " + API_KEY)
                .post(requestBody)
                .build();

        try (Response response = client.newCall(request).execute()) {
            if (!response.isSuccessful()) {
                throw new IOException("Unexpected code " + response);
            }

            String responseBody = response.body().string();
            return parseJsonResponse(responseBody);
        }
    }

    private static String parseJsonResponse(String responseBody) {
        Gson gson = new Gson();
        try {
            JsonObject jsonResponse = gson.fromJson(responseBody, JsonObject.class);
            return jsonResponse.get("summary").getAsString();
        } catch (JsonParseException e) {
            System.err.println("Error parsing JSON response: " + e.getMessage());
            return null;
        }
    }
}
