package org.spring.projectjs.map;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.net.URI;
import java.net.URLEncoder;
import java.net.http.*;
import java.nio.charset.StandardCharsets;

public class KakaoLocalDao {
    private final String restApiKey;
    private final HttpClient http = HttpClient.newHttpClient();
    private final ObjectMapper om = new ObjectMapper();

    public KakaoLocalDao(String restApiKey) { this.restApiKey = restApiKey; }

    public Geo geocode(String query) throws Exception {
        String url = "https://dapi.kakao.com/v2/local/search/address.json?size=1&query="
                + URLEncoder.encode(query, StandardCharsets.UTF_8);
        HttpRequest req = HttpRequest.newBuilder(URI.create(url))
                .header("Authorization", "KakaoAK " + restApiKey)
                .GET().build();
        HttpResponse<String> resp = http.send(req, HttpResponse.BodyHandlers.ofString());
        if (resp.statusCode() != 200) return null;

        JsonNode docs = om.readTree(resp.body()).path("documents");
        if (docs.isEmpty()) return null;

        double lng = docs.get(0).path("x").asDouble(); // 경도
        double lat = docs.get(0).path("y").asDouble(); // 위도
        return new Geo(lat, lng);
    }

    public static class Geo {
        public final double lat, lng;
        public Geo(double lat, double lng){ this.lat=lat; this.lng=lng; }
    }
}