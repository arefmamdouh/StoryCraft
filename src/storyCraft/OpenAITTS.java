package storyCraft;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.awt.Desktop;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class OpenAITTS {
    private static final String API_KEY = "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";
    private static final String TTS_ENDPOINT = "https://api.openai.com/v1/audio/speech";

    public static void convertTextToSpeech(String text, String outputPath) {
        try (CloseableHttpClient httpClient = HttpClients.createDefault()) {
            HttpPost request = new HttpPost(TTS_ENDPOINT);
            request.setHeader("Authorization", "Bearer " + API_KEY);
            request.setHeader("Content-Type", "application/json");

            Map<String, Object> data = new HashMap<>();
            data.put("model", "tts-1");
            data.put("input", text);
            data.put("voice", "fable");

            ObjectMapper mapper = new ObjectMapper();
            String json = mapper.writeValueAsString(data);

            request.setEntity(new StringEntity(json));

            HttpResponse response = httpClient.execute(request);
            byte[] audioBytes = EntityUtils.toByteArray(response.getEntity());

            // Save the audio to a file
            File audioFile = new File(outputPath);
            try (FileOutputStream fos = new FileOutputStream(audioFile)) {
                fos.write(audioBytes);
                System.out.println("Audio content written to: " + outputPath);
            }

            // Open and play the audio file using the system's default media player
            if (Desktop.isDesktopSupported()) {
                Desktop desktop = Desktop.getDesktop();
                if (audioFile.exists()) {
                    desktop.open(audioFile);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

