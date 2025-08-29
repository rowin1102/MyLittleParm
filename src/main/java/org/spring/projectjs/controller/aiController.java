package org.spring.projectjs.controller;

import org.spring.projectjs.JPA.plants.CsvPlantService;
import org.spring.projectjs.JPA.plants.PlantView;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.util.UriComponentsBuilder;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.RequiredArgsConstructor;

import java.util.Base64;
import java.util.Comparator;
import java.util.List;

@Controller
@RequiredArgsConstructor
public class aiController {
   private final CsvPlantService plantService;

    @GetMapping("/plantList.do")
    public String plantList(Model model) {
        // 1) 전체 목록 가져오기
        List<PlantView> list = plantService.all().stream()
                // 보기 좋게 정렬 (한글명 → 영문명 보조)
                .sorted(Comparator
                        .comparing((PlantView p) -> ns(p.getName()))
                        .thenComparing(p -> ns(p.getEnglishName())))
                .toList();

        // 2) JSP로 전달 (리스트만)
        model.addAttribute("list", list);

        // 페이징/검색 관련 어떤 값도 넘기지 않음
        return "ai/plantList";
    }

    private static String ns(String s) { return s == null ? "" : s; }

    @GetMapping("/ai/plantRecommend.do")
    public String page(@RequestParam(name = "btn", required = false) String btn,
                       @RequestParam(name = "temp", required = false) Integer temp,
                       @RequestParam(name = "humidity", required = false) Integer humidity,
                       @RequestParam(name = "maxLight", required = false) Integer maxLight,
                       @RequestParam(name = "search", required = false) String search,
                       Model model) {

        boolean submitted = search != null || btn != null || temp != null || humidity != null || maxLight != null;
        if (!submitted) return "ai/plantRecommend";

        var base = (btn != null && !btn.isBlank())
                ? plantService.filterByButton(btn)
                : plantService.all();

        var list = plantService.applyEnvFilterSingle(base, temp, humidity, maxLight);

        model.addAttribute("plantList", list);
        model.addAttribute("selectedBtn", btn);
        model.addAttribute("temp", temp);
        model.addAttribute("humidity", humidity);
        model.addAttribute("maxLight", maxLight);

        return "ai/plantRecommendResult";
    }

    @GetMapping("/ai/environment.do")
    public String environment(@RequestParam(name = "plantName", required = false) String plantName, Model model) {
        if (plantName == null || plantName.isBlank()) return "ai/environment";

        List<PlantView> candidates = plantService.searchByName(plantName);
        model.addAttribute("query", plantName);
        model.addAttribute("candidates", candidates);

        if (candidates.isEmpty()) {
            model.addAttribute("notFound", true);
            return "ai/environment";
        }

        PlantView p = plantService.bestMatch(plantName).orElse(candidates.get(0));
        model.addAttribute("p", p);
        model.addAttribute("lightLabel", lightLabel(p.getAmountLight()));
        model.addAttribute("humidityLabel", humidityLabel(p.getMinHumidity(), p.getMaxHumidity()));
        model.addAttribute("tempLabel", tempLabel(p.getMinTemp(), p.getMaxTemp()));
        model.addAttribute("locationTip", windowTip(p.getAmountLight()));
        model.addAttribute("careTip", careTip(p));

        return "ai/environmentResult";
    }

    private String lightLabel(Integer lux) {
        if (lux == null) return "정보 없음";
        int v = lux;
        if (v <= 0) return "정보 없음";
        if (v < 100) return "아주 약한 빛(음지)";
        if (v < 300) return "약한 빛(간접광)";
        if (v < 800) return "보통 빛(밝은 실내)";
        if (v < 2000) return "강한 빛(창가 근처)";
        return "직사광";
    }

    private String humidityLabel(Integer min, Integer max) {
        if (nz(min) == 0 && nz(max) == 0) return "정보 없음";
        int lo = nz(min), hi = nz(max);
        if (hi <= 45) return lo + "~" + hi + "% (건조 선호)";
        if (hi <= 70) return lo + "~" + hi + "% (보통)";
        return lo + "~" + hi + "% (습도 높게)";
    }

    private String tempLabel(Integer min, Integer max) {
        if (nz(min) == 0 && nz(max) == 0) return "정보 없음";
        return nz(min) + " ~ " + nz(max) + "℃";
    }

    private String windowTip(Integer lux) {
        int v = nz(lux);
        if (v == 0) return "직사광만 피하면 대부분의 실내 창가에서 잘 자라요.";
        if (v < 150) return "북향/내부(간접광) 위치가 좋아요. 빛 보강이 필요할 수 있어요.";
        if (v < 500) return "동/서향 창가 1~2m 권장, 얇은 커튼으로 부드럽게.";
        if (v < 1200) return "동/서향 창가 근처 OK. 한여름 직사광은 1~2시간 이내로.";
        return "남향/직사광 가능. 한여름에는 과열·잎타는 증상만 주의!";
    }

    private String careTip(PlantView p) {
        StringBuilder sb = new StringBuilder();
        int diff = nz(p.getDifficulty());
        int lux = nz(p.getAmountLight());
        if (diff <= 2) sb.append("초보도 OK, ");
        else if (diff == 3) sb.append("보통 난이도, ");
        else sb.append("주의 필요, ");
        if (lux < 150) sb.append("빛 부족에 민감할 수 있어요. ");
        else if (lux > 1200) sb.append("강광에 잎 끝마름 주의. ");
        sb.append("흙 겉면 2~3cm가 마르면 충분히 관수하세요.");
        return sb.toString();
    }

    private int nz(Integer v) { return v == null ? 0 : v; }

    /* -------------------------------------------------------
     *  병해충 진단 (Flask 연동)
     * ----------------------------------------------------- */
    @Value("${flask.url:http://127.0.0.1:5000/getDecodeImage.fk}")
    private String flaskUrl;

    private final RestTemplate rest = new RestTemplate();
    private final ObjectMapper om = new ObjectMapper();

    /** 업로드 페이지 (GET) */
    @GetMapping("/ai/diagnosis.do")
    public String diagnosisPage() {
        return "ai/diagnosis"; // /WEB-INF/views/ai/diagnosis.jsp
    }

    /** 업로드 처리 (POST) → Redirect(결과) */
    @PostMapping("/ai/diagnosis.do")
    public String diagnosisPost(@RequestParam("pestImage") MultipartFile img,
                                RedirectAttributes ra) throws Exception {
        if (img == null || img.isEmpty()) {
            ra.addFlashAttribute("error", "이미지를 선택하세요.");
            return "redirect:/ai/diagnosis.do";
        }

        // 미리보기 data URL (큰 이미지면 플래시에 실리지 않을 수 있음)
        String ct = (img.getContentType() != null) ? img.getContentType() : "image/jpeg";
        String preview = "data:" + ct + ";base64," + Base64.getEncoder().encodeToString(img.getBytes());

        // Flask URL (?imageName=)
        String imageName = (img.getOriginalFilename() != null) ? img.getOriginalFilename() : "upload.jpg";
        String url = UriComponentsBuilder.fromHttpUrl(flaskUrl)
                .queryParam("imageName", imageName) // UriComponentsBuilder가 인코딩 처리
                .toUriString();

        // POST: text/plain(base64)
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.TEXT_PLAIN);
        String base64Body = Base64.getEncoder().encodeToString(img.getBytes());
        HttpEntity<String> entity = new HttpEntity<>(base64Body, headers);

        String body;
        try {
            ResponseEntity<String> res = rest.exchange(url, HttpMethod.POST, entity, String.class);
            System.out.println("[Flask status] " + res.getStatusCode());
            body = res.getBody();
        } catch (HttpStatusCodeException e) {
            System.out.println("[Flask error status] " + e.getStatusCode());
            System.out.println("[Flask error body] " + e.getResponseBodyAsString());
            ra.addFlashAttribute("error", "Flask 호출 실패: " + e.getStatusCode());
            return "redirect:/ai/diagnosis.do";
        }

        System.out.println("[Flask RAW] " + body);
        if (body == null || body.isBlank()) {
            ra.addFlashAttribute("error", "Flask 응답이 비었습니다.");
            return "redirect:/ai/diagnosis.do";
        }

        // 방어적 파싱
        JsonNode root = om.readTree(body);
        String status = root.path("result").asText("");
        String filePath = root.path("file_path").asText("");

        if (!"success".equalsIgnoreCase(status)) {
            // Flask가 실패 시 {"result":"fail","error":"..."} 형식을 권장
            String err = root.path("error").asText("처리 실패");
            ra.addFlashAttribute("error", "Flask 처리 실패: " + err);
            return "redirect:/ai/diagnosis.do";
        }

        JsonNode predict = root.path("predict");
        // 키 이름을 공백/언더스코어 둘 다 허용
        String plantAndDisease = predict.path("plant_and_disease").asText();
        double confidence = predict.path("confidence").asDouble(Double.NaN);

        if (plantAndDisease == null || plantAndDisease.isBlank() || Double.isNaN(confidence)) {
            ra.addFlashAttribute("error", "Flask 응답 형식 이상: " + body);
            return "redirect:/ai/diagnosis.do";
        }

        // RedirectAttributes (PRG)
        ra.addFlashAttribute("preview", preview);
        ra.addFlashAttribute("result", status);
        ra.addFlashAttribute("filePath", filePath);
        ra.addFlashAttribute("plantAndDisease", plantAndDisease);
        ra.addFlashAttribute("confidencePct", String.format("%.2f", confidence * 100));
        ra.addFlashAttribute("raw", root.toPrettyString());

        return "redirect:/ai/diagnosisResult.do";
    }

    /** 결과 페이지 (GET) — 직접 접근 시 업로드로 */
    @GetMapping("/ai/diagnosisResult.do")
    public String diagnosisResult(Model model) {
        if (!model.containsAttribute("plantAndDisease") && !model.containsAttribute("error")) {
            return "redirect:/ai/diagnosis.do";
        }
        return "ai/diagnosisResult"; // /WEB-INF/views/ai/diagnosisResult.jsp
    }
}
