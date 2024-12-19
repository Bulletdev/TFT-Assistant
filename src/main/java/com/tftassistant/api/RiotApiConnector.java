import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

class RiotApiConnector {
    private static final String API_BASE_URL = "https://na1.api.riotgames.com/tft/";
    private String apiKey;
    private OkHttpClient client;
    private ObjectMapper mapper;

    public RiotApiConnector(String apiKey) {
        this.apiKey = apiKey;
        this.client = new OkHttpClient();
        this.mapper = new ObjectMapper();
    }

    public SummonerInfo getSummonerInfo(String summonerName) throws IOException {
        String url = API_BASE_URL + "summoner/v1/summoners/by-name/" +
                URLEncoder.encode(summonerName, StandardCharsets.UTF_8);

        Request request = new Request.Builder()
                .url(url)
                .addHeader("X-Riot-Token", apiKey)
                .build();

        try (Response response = client.newCall(request).execute()) {
            if (!response.isSuccessful()) {
                throw new IOException("Unexpected response " + response);
            }
            return mapper.readValue(response.body().string(), SummonerInfo.class);
        }
    }

    public <MatchInfo> List<MatchInfo> getRecentMatches(String summonerId) throws IOException {
        String url = API_BASE_URL + "match/v1/matches/by-puuid/" + summonerId + "/ids";

        Request request = new Request.Builder()
                .url(url)
                .addHeader("X-Riot-Token", apiKey)
                .build();

        try (Response response = client.newCall(request).execute()) {
            if (!response.isSuccessful()) {
                throw new IOException("Unexpected response " + response);
            }

            List<String> matchIds = mapper.readValue(
                    response.body().string(),
                    new TypeReference<List<String>>() {}
            );

            List<Object> list = new ArrayList<>();
            long limit = 10;
            for (String matchId : matchIds) {
                if (limit-- == 0) break;
                Optional<Object> matchDetails = getMatchDetails(matchId);
                if (matchDetails.isPresent()) {
                    Object o = matchDetails.get();
                    list.add(o);
                }
            }
            return (List<MatchInfo>) list;
        }
    }

    private <MatchInfo> Optional<MatchInfo> getMatchDetails(String matchId) {
        String url = API_BASE_URL + "match/v1/matches/" + matchId;

        Request request = new Request.Builder()
                .url(url)
                .addHeader("X-Riot-Token", apiKey)
                .build();

        try (Response response = client.newCall(request).execute()) {
            if (!response.isSuccessful()) {
                return Optional.empty();
            }
            return Optional.of(mapper.readValue(response.body().string(), MatchInfo.class));
        } catch (IOException e) {
            return Optional.empty();
        }
    }
}