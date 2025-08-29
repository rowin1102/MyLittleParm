// utils/PagingUtil.java
package utils;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

public class PagingUtil {

    public static String pagingImg(
            int totalRecordCount,
            int pageSize,
            int blockPage,
            int pageNum,
            String page,          // ex) cp + "/boardList.do?"
            String searchField,
            String searchKeyword,
            String cp             // <-- 추가: req.getContextPath()
    ) {

        String pagingStr = "";

        int totalPage = (int) Math.ceil(((double) totalRecordCount / pageSize));
        int intTemp = (((pageNum - 1) / blockPage) * blockPage) + 1;

        // 파라미터 인코딩
        String sf = url(searchField);
        String sk = url(searchKeyword);

        // 1) 처음 / 이전블록
        if (intTemp != 1) {
            pagingStr += "<a href='" + page + "pageNum=1&searchField=" + sf + "&searchKeyword=" + sk + "'>"
                       + "<img src='" + cp + "/img/paging1.gif' alt='처음'></a>&nbsp;";

            pagingStr += "<a href='" + page + "pageNum=" + (intTemp - blockPage) + "&searchField=" + sf + "&searchKeyword=" + sk + "'>"
                       + "<img src='" + cp + "/img/paging2.gif' alt='이전블록'></a>";
        }

        // 2) 페이지 번호들
        int blockCount = 1;
        while (blockCount <= blockPage && intTemp <= totalPage) {
            if (intTemp == pageNum) {
                pagingStr += "&nbsp;<span style='color:red;'>" + intTemp + "</span>&nbsp;";
            } else {
                pagingStr += "&nbsp;<a href='" + page + "pageNum=" + intTemp + "&searchField=" + sf + "&searchKeyword=" + sk + "'>"
                           + intTemp + "</a>&nbsp;";
            }
            intTemp++;
            blockCount++;
        }

        // 3) 다음블록 / 마지막
        if (intTemp <= totalPage) {
            pagingStr += "<a href='" + page + "pageNum=" + intTemp + "&searchField=" + sf + "&searchKeyword=" + sk + "'>"
                       + "<img src='" + cp + "/img/paging3.gif' alt='다음블록'></a>&nbsp;";

            pagingStr += "<a href='" + page + "pageNum=" + totalPage + "&searchField=" + sf + "&searchKeyword=" + sk + "'>"
                       + "<img src='" + cp + "/img/paging4.gif' alt='마지막'></a>";
        }

        return pagingStr;
    }

    private static String url(String v) {
        if (v == null) v = "";
        return URLEncoder.encode(v, StandardCharsets.UTF_8);
    }
}
