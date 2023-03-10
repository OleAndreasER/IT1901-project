package minesweeper.ui;

import java.net.URI;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.net.http.HttpRequest.BodyPublisher;
import java.net.http.HttpRequest.BodyPublishers;
import java.net.http.HttpClient;

import minesweeper.core.HighscoreEntry;
import minesweeper.core.HighscoreList;
import minesweeper.json.HighscoresFileHandler;

import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * This class gives access to the server by sending HTTP-requests.
 */
public class HighscoresAccess {
    private final URI uri;
    private ObjectMapper mapper;

    /**
     * Constructor for HighscoresAccess.
     * @param uri the URI-address of the server
     */
    public HighscoresAccess(final URI uri) {
        this.uri = uri;
        mapper = new HighscoresFileHandler().getMapper();
    }

    /**
     * Gets the highscore list with an HTTP-request from server.
     * @param difficulty the highscore list's difficulty
     * @return the highscore list
     */
    public HighscoreList getHighscoreList(final String difficulty) {
        HttpRequest request = HttpRequest
            .newBuilder(uri.resolve(
                String.format("highscorelist/%s", difficulty)))
            .header("accept", "application/json")
            .GET()
            .build();

        HighscoreList highscoreList;
        try {
            final HttpResponse<String> response =
                HttpClient.newHttpClient().send(
                    request,
                    HttpResponse.BodyHandlers.ofString()
                );

            highscoreList = mapper.readValue(
                response.body(),
                HighscoreList.class
            );
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return highscoreList;
    }

    /**
     * Posts highscore entry to highscore list on the server
     * with an HTTP-request.
     * @param difficulty the highscore list's difficulty
     * @param entry to be saved to server
     */
    public void saveScore(
        final HighscoreEntry entry,
        final String difficulty
    ) {
        try {
            BodyPublisher bodyPublisher = BodyPublishers.ofString(
                mapper.writeValueAsString(entry)
            );
            HttpRequest request = HttpRequest
                .newBuilder(uri.resolve(
                    String.format("highscorelist/%s/save",
                    difficulty))
                )
                .header("Content-Type", "application/json")
                .POST(bodyPublisher)
                .build();

            //The response isn't needed.
            HttpClient
                .newBuilder()
                .build()
                .send(request, HttpResponse.BodyHandlers.ofString());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
