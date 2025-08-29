// org.spring.projectjs.ai.CsvPlantService
package org.spring.projectjs.JPA.plants;

import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
public class CsvPlantService {

    private List<PlantView> catalog = List.of();

    @PostConstruct
    public void load() {
        final String path = "static/data/smartfarm_plant_list.csv";
        final List<String> encodings = List.of("MS949");

        for (String enc : encodings) {
            try (BufferedReader br = new BufferedReader(
                    new InputStreamReader(new ClassPathResource(path).getInputStream(),
                            java.nio.charset.Charset.forName(enc)))) {

                String headerLine = br.readLine();
                if (headerLine == null) throw new IllegalStateException("CSV header missing");

                String[] headers = splitCsv(headerLine);
                Map<String, Integer> idx = indexByHeader(headers);

                List<PlantView> list = new ArrayList<>();
                String line;
                while ((line = br.readLine()) != null) {
                    if (line.isBlank()) continue;
                    String[] c = splitCsv(line);

                    String korName  = get(c, idx, "korName");     // 식물명
                    if (korName.isBlank()) continue;

                    String enName   = get(c, idx, "enName");      // name
                    String series   = get(c, idx, "series");      // series
                    Integer grade   = toInt(get(c, idx, "grade"));// grade
                    Integer tMin    = toInt(get(c, idx, "tmin")); // temp_min
                    Integer tMax    = toInt(get(c, idx, "tmax")); // temp_max
                    Integer light   = toInt(get(c, idx, "light"));// amount_light
                    Integer hMin    = toInt(get(c, idx, "hmin")); // humidity_min
                    Integer hMax    = toInt(get(c, idx, "hmax")); // humidity_max

                    // ✅ grow_days_min / grow_days_max 우선 사용, 없으면 grow_days 단일값 폴백
                    Integer gMin = toInt(get(c, idx, "gmin"));    // grow_days_min
                    Integer gMax = toInt(get(c, idx, "gmax"));    // grow_days_max
                    if ((gMin == 0 && gMax == 0)) {
                        Integer g = toInt(get(c, idx, "grow"));   // grow_days (단일)
                        gMin = g; gMax = g;
                    }
                    // 값 뒤집힘 방어
                    if (gMin != 0 && gMax != 0 && gMin > gMax) {
                        int tmp = gMin; gMin = gMax; gMax = tmp;
                    }

                    list.add(new PlantView(
                            korName, enName, series, grade,
                            tMin, tMax,
                            light,
                            gMin, gMax,        // ⬅️ 여기!
                            hMin, hMax
                    ));
                }

                this.catalog = List.copyOf(list);
                log.info("CSV loaded: {} rows (enc={})", catalog.size(), enc);
                this.catalog.stream().limit(5).forEach(p -> log.info(
                        "sample => {} / {} / {}  T:{}~{}  G:{}~{}  H:{}~{}  L:{}",
                        p.getName(), p.getEnglishName(), p.getSeries(),
                        p.getMinTemp(), p.getMaxTemp(),
                        p.getMinGrowDays(), p.getMaxGrowDays(),
                        p.getMinHumidity(), p.getMaxHumidity(),
                        p.getAmountLight()
                ));
                return;
            } catch (Exception e) {
                log.warn("CSV read failed with {}. cause={}", enc, e.toString());
            }
        }

        log.error("CSV load failed: {}", path);
        this.catalog = List.of();
    }
    /* ---------------- 버튼 필터(기존) ---------------- */
    public List<PlantView> filterByButton(String btn) {
        if (btn == null || btn.isBlank()) return List.of();
        return switch (btn.toUpperCase()) {
            case "EASY" -> catalog.stream()
                    .filter(p -> p.getDifficulty() != null && p.getDifficulty() <= 2)
                    .sorted(Comparator.comparing(PlantView::getDifficulty, Comparator.nullsLast(Integer::compareTo))
                            .thenComparing(PlantView::getName))
                    .collect(Collectors.toList());
            case "RESISTANT" -> {
                var set = Set.of("로즈마리","타임","마조람","민트","라벤더","오레가노","파슬리");
                yield catalog.stream().filter(p -> set.contains(p.getName())).collect(Collectors.toList());
            }
            case "REPELLENT" -> {
                var set = Set.of("라벤더","로즈마리","민트","바질","타임");
                yield catalog.stream().filter(p -> set.contains(p.getName())).collect(Collectors.toList());
            }
            case "PRETTY", "INTERIOR" -> {
                var groups = Set.of("꽃","관엽/꽃","허브/꽃");
                yield catalog.stream().filter(p -> groups.contains(p.getSeries())).collect(Collectors.toList());
            }
            case "PRACTICAL" -> {
                var groups = Set.of("잎채소","뿌리채소","과채류","과일","허브");
                yield catalog.stream().filter(p -> groups.contains(p.getSeries())).collect(Collectors.toList());
            }
            default -> List.of();
        };
    }

    /* ---------------- 환경 필터(신규) ---------------- */

 // org.spring.projectjs.ai.CsvPlantService (추가 메서드들만 발췌)
    public List<PlantView> filterByEnvSingle(Integer temp, Integer humidity, Integer maxLight) {
        return applyEnvFilterSingle(catalog, temp, humidity, maxLight);
    }
    
 // 따옴표 안의 콤마는 무시하면서 split, 빈 칼럼도 보존
    private String[] splitCsv(String line) {
        String[] parts = line.split(",(?=(?:[^\"]*\"[^\"]*\")*[^\"]*$)", -1);
        for (int i = 0; i < parts.length; i++) {
            String s = parts[i].trim();
            if (s.startsWith("\"") && s.endsWith("\"") && s.length() >= 2) {
                s = s.substring(1, s.length() - 1).replace("\"\"", "\""); // "" -> "
            }
            parts[i] = s;
        }
        return parts;
    }

    // 스샷 컬럼명용 헤더 인덱싱
    private Map<String, Integer> indexByHeader(String[] h) {
        Map<String, Integer> m = new HashMap<>();
        for (int i = 0; i < h.length; i++) {
            String k = h[i] == null ? "" : h[i].toLowerCase().replaceAll("\\s", "");
            if (k.contains("식물명"))          m.put("korName", i);   // 국문명
            if (k.equals("name"))              m.put("enName", i);    // 영문명
            if (k.equals("series"))            m.put("series", i);
            if (k.equals("grade") || k.equals("difficulty")) m.put("grade", i);
            if (k.equals("amount_light"))      m.put("light", i);
            if (k.equals("temp_min"))          m.put("tmin", i);
            if (k.equals("temp_max"))          m.put("tmax", i);
            if (k.equals("grow_days"))         m.put("grow", i);
            if (k.equals("humidity_min"))      m.put("hmin", i);
            if (k.equals("humidity_max"))      m.put("hmax", i);
            // day_light_* 컬럼은 사용하지 않음
            if (k.equals("grow_days_min"))     m.put("gmin", i);
            if (k.equals("grow_days_max"))     m.put("gmax", i);

            if (k.equals("humidity_min"))      m.put("hmin", i);
            if (k.equals("humidity_max"))      m.put("hmax", i);
        }
        return m;
    }

    private String get(String[] c, Map<String, Integer> idx, String key) {
        Integer i = idx.get(key);
        return (i != null && i < c.length) ? c[i] : "";
    }

    
    public List<PlantView> applyEnvFilterSingle( List<PlantView> base,
            Integer temp, Integer humidity, Integer maxLight) {

        boolean useTemp = temp != null;
        boolean useHum  = humidity != null;
        boolean useLux  = maxLight != null;

        return base.stream()
            // 온도: 포함 판정 p.minTemp <= temp <= p.maxTemp
            .filter(p -> {
                if (!useTemp) return true;
                return within(p.getMinTemp(), p.getMaxTemp(), temp);
            })
            // 습도: 포함 판정 p.minHumidity <= humidity <= p.maxHumidity
            .filter(p -> {
                if (!useHum) return true;
                return within(p.getMinHumidity(), p.getMaxHumidity(), humidity);
            })
            // 광량: 단일값(amt) <= maxLight
            .filter(p -> {
                if (!useLux) return true;
                Integer amt = p.getAmountLight();
                if (amt == null || amt == 0) return true; // 정보없음은 통과(원하면 false로 변경)
                return amt <= maxLight;
            })
            .sorted(Comparator.comparing(PlantView::getName))
            .collect(java.util.stream.Collectors.toList());
    }

    /** 포함 체크: min/max가 비어있으면 아주 넓은 범위로 간주 */
    private boolean within(Integer min, Integer max, Integer val) {
        if (val == null) return true;
        int mn = (min == null ? Integer.MIN_VALUE : min);
        int mx = (max == null || max == 0 ? Integer.MAX_VALUE : max); // CSV max가 0이면 미기재로 간주
        return mn <= val && val <= mx;
    }
 // org.spring.projectjs.ai.CsvPlantService (변경/추가 부분만)
    public List<PlantView> all() {
        return catalog;
    }

    /** 포함 체크(보강): min/max 누락 또는 뒤바뀜까지 안전 처리 */
    private boolean withinSafe(Integer min, Integer max, Integer val) {
        if (val == null) return true;
        int mn = (min == null) ? Integer.MIN_VALUE : min;
        int mx = (max == null || max == 0) ? Integer.MAX_VALUE : max;
        if (mn > mx) { int t = mn; mn = mx; mx = t; } // ← CSV에 400~0 같은 케이스 방지
        return mn <= val && val <= mx;
    }


    /* ---------------- 유틸 ---------------- */

    private Integer toInt(String s) {
        try { return Integer.parseInt(s.replaceAll("[^0-9-]", "").trim()); }
        catch (Exception e) { return 0; }
    }
    private int[] range(String s) {
        if (s == null) return new int[]{0,0};
        String[] a = s.split("~");
        int min = a.length>0 ? toInt(a[0]) : 0;
        int max = a.length>1 ? toInt(a[1]) : 0;
        return new int[]{min, max};
    }

    /** 범위 겹침(overlap) 판정. 식물/사용자 값이 null/0이면 넓은 범위로 간주(통과). */
    private boolean overlap(Integer pMin, Integer pMax, Integer uMin, Integer uMax) {
        int pm = pMin == null ? Integer.MIN_VALUE : pMin;
        int pM = (pMax == null || pMax == 0) ? Integer.MAX_VALUE : pMax;
        int um = uMin == null ? Integer.MIN_VALUE : uMin;
        int uM = uMax == null ? Integer.MAX_VALUE : uMax;
        return pm <= uM && pM >= um;
    }

    private int[] normalizeRange(Integer min, Integer max) {
        if (min == null && max == null) return new int[]{Integer.MIN_VALUE, Integer.MAX_VALUE};
        int mn = (min == null) ? Integer.MIN_VALUE : min;
        int mx = (max == null) ? Integer.MAX_VALUE : max;
        if (mn > mx) { int t = mn; mn = mx; mx = t; }
        return new int[]{mn, mx};
    }

    private boolean notAllNull(Integer a, Integer b) {
        return !(a == null && b == null);
    }
    public List<PlantView> searchByName(String q) {
        if (q == null) return List.of();
        String qs = q.trim().toLowerCase();
        return catalog.stream()
                .filter(p -> contains(p.getName(), qs) || contains(p.getEnglishName(), qs))
                .sorted(Comparator.comparingInt(p -> score(p, qs))) // 가벼운 정렬
                .limit(10)
                .toList();
    }

    /** 완전일치 우선 반환 */
    public Optional<PlantView> bestMatch(String q) {
        if (q == null) return Optional.empty();
        String qs = q.trim().toLowerCase();
        return catalog.stream()
                .filter(p -> equals(p.getName(), qs) || equals(p.getEnglishName(), qs))
                .findFirst()
                .or(() -> searchByName(q).stream().findFirst());
    }

    private boolean contains(String s, String q) { return s != null && s.toLowerCase().contains(q); }
    private boolean equals(String s, String q) { return s != null && s.equalsIgnoreCase(q); }
    private int score(PlantView p, String q) {
        // 간단 점수: 포함 여부 + 길이 차이
        String n = p.getName() == null ? "" : p.getName().toLowerCase();
        if (n.equals(q)) return 0;
        if (n.contains(q)) return Math.abs(n.length() - q.length());
        String e = p.getEnglishName() == null ? "" : p.getEnglishName().toLowerCase();
        if (e.equals(q)) return 1;
        if (e.contains(q)) return Math.abs(e.length() - q.length()) + 2;
        return 1000;
    }
}
