package com.tftassistant.api;

import com.tftassistant.model.SummonerInfo;
import com.tftassistant.model.MatchInfo;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import okhttp3.OkHttpClient;
import okhttp3.Response;
import okhttp3.ResponseBody;

import java.io.IOException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RiotApiConnectorTest {

    private RiotApiConnector apiConnector;
    private static final String TEST_API_KEY = "RGAPI-test-key";
    private static final String TEST_SUMMONER_NAME = "TestSummoner";
    private static final String TEST_SUMMONER_ID = "test-summoner-id";

    @Mock
    private OkHttpClient httpClient;

    @BeforeEach
    void setUp() {
        apiConnector = new RiotApiConnector(TEST_API_KEY);
        // Inject mocked HTTP client
        apiConnector.setHttpClient(httpClient);
    }

    @Test
    void getSummonerInfo_Success() throws IOException {
        // Arrange
        String mockResponse = """
            {
                "id": "test-summoner-id",
                "accountId": "test-account-id",
                "puuid": "test-puuid",
                "name": "TestSummoner",
                "profileIconId": 1234,
                "summonerLevel": 100
            }
            """;
        mockHttpResponse(mockResponse);

        // Act
        SummonerInfo summonerInfo = apiConnector.getSummonerInfo(TEST_SUMMONER_NAME);

        // Assert
        assertNotNull(summonerInfo);
        assertEquals(TEST_SUMMONER_NAME, summonerInfo.getName());
        assertEquals(TEST_SUMMONER_ID, summonerInfo.getId());
        assertEquals(100, summonerInfo.getSummonerLevel());
    }

    @Test
    void getRecentMatches_Success() throws IOException {
        // Arrange
        String mockResponse = """
            [
                {
                    "metadata": {
                        "match_id": "TEST1"
                    },
                    "info": {
                        "game_datetime": 1234567890,
                        "participants": []
                    }
                }
            ]
            """;
        mockHttpResponse(mockResponse);

        // Act
        List<MatchInfo> matches = apiConnector.getRecentMatches(TEST_SUMMONER_ID);

        // Assert
        assertNotNull(matches);
        assertFalse(matches.isEmpty());
        assertEquals("TEST1", matches.get(0).getMatchId());
    }

    @Test
    void getLatestPatchNotes_Success() throws IOException {
        // Arrange
        String mockResponse = """
            {
                "version": "14.1",
                "notes": [
                    {
                        "type": "champion",
                        "changes": []
                    }
                ]
            }
            """;
        mockHttpResponse(mockResponse);

        // Act
        PatchNotes patchNotes = apiConnector.getLatestPatchNotes();

        // Assert
        assertNotNull(patchNotes);
        assertEquals("14.1", patchNotes.getVersion());
    }

    @Test
    void apiRequest_RateLimitExceeded() throws IOException {
        // Arrange
        mockHttpResponseWithCode(429);

        // Act & Assert
        assertThrows(RateLimitException.class, () ->
                apiConnector.getSummonerInfo(TEST_SUMMONER_NAME)
        );
    }

    @Test
    void apiRequest_InvalidApiKey() throws IOException {
        // Arrange
        mockHttpResponseWithCode(403);

        // Act & Assert
        assertThrows(InvalidApiKeyException.class, () ->
                apiConnector.getSummonerInfo(TEST_SUMMONER_NAME)
        );
    }

    private void mockHttpResponse(String responseBody) throws IOException {
        Response mockResponse = mock(Response.class);
        ResponseBody body = ResponseBody.create(responseBody, null);
        when(mockResponse.body()).thenReturn(body);
        when(mockResponse.isSuccessful()).thenReturn(true);
        when(httpClient.newCall(any())).thenReturn(mock(Call.class));
        when(httpClient.newCall(any()).execute()).thenReturn(mockResponse);
    }

    private void mockHttpResponseWithCode(int code) throws IOException {
        Response mockResponse = mock(Response.class);
        when(mockResponse.code()).thenReturn(code);
        when(mockResponse.isSuccessful()).thenReturn(false);
        when(httpClient.newCall(any())).thenReturn(mock(Call.class));
        when(httpClient.newCall(any()).execute()).thenReturn(mockResponse);
    }
}
