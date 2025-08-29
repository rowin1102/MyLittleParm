package org.spring.projectjs.controller;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.spring.projectjs.jdbc.ITransaction;
import org.spring.projectjs.jdbc.ParameterDTO;
import org.spring.projectjs.jdbc.TransactionDTO;
import org.spring.projectjs.utils.MyFunctions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.ResourceUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.Part;
import utils.FileUtil;
import utils.PagingUtil;

@Controller
public class transactionController {

	@Autowired
	ITransaction dao;

	@GetMapping("/transactionWrite.do")
	public String transactionWriteGet(TransactionDTO transactionDTO, Model model) {
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		String userId = authentication.getName();
		transactionDTO.setWriter(userId);
		model.addAttribute("userId", userId);
		return "transaction/transactionWrite";
	}

	// 글쓰기 등록
	@PostMapping("/transactionWrite.do")
	public String transationWritePost(HttpServletRequest req, TransactionDTO transactionDTO, Model model) {
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		String userId = authentication.getName();
		transactionDTO.setWriter(userId);

		try {
			String uploadDir = ResourceUtils.getFile("classpath:static/uploads/").toPath().toString();

			int fileIdx = 1; // 1→2→3 순서대로 채움
			for (Part part : req.getParts()) {
				if (!"ofile".equals(part.getName()))
					continue;

				// header 파싱 대신 getSubmittedFileName() 사용
				String originalFileName = part.getSubmittedFileName();
				if (originalFileName == null || originalFileName.isBlank())
					continue; // 빈 파일은 스킵

				// 저장
				part.write(uploadDir + File.separator + originalFileName);
				String savedFileName = MyFunctions.renameFile(uploadDir, originalFileName);

				// 들어온 순서대로 DTO 슬롯 채우기
				if (fileIdx == 1) {
					transactionDTO.setOfile1(originalFileName);
					transactionDTO.setSfile1(savedFileName);
				} else if (fileIdx == 2) {
					transactionDTO.setOfile2(originalFileName);
					transactionDTO.setSfile2(savedFileName);
				} else if (fileIdx == 3) {
					transactionDTO.setOfile3(originalFileName);
					transactionDTO.setSfile3(savedFileName);
				}
				fileIdx++;
				if (fileIdx > 3)
					break; // 최대 3장
			}

			int result = dao.transaction_write(transactionDTO);
			if (result == 1)
				System.out.println("입력성공");

		} catch (Exception e) {
			System.out.println("등록 파일 업로드 에러" + e);
			e.printStackTrace();
		}

		return "redirect:transactionList.do";
	}

	// 목록가져오기
	@GetMapping("/transactionList.do")
	public String member2(Model model, HttpServletRequest req, ParameterDTO parameterDTO) {

		int totalCount = dao.transaction_totalCount(parameterDTO);
		int pageSize = 20;
		int blockPage = 5;
		int pageNum = (req.getParameter("pageNum") == null || req.getParameter("pageNum").equals("") ? 1
				: Integer.parseInt(req.getParameter("pageNum")));

		String searchField = (req.getParameter("searchField") == null || req.getParameter("searchField").equals("") ? ""
				: req.getParameter("searchField"));

		String searchKeyword = (req.getParameter("searchKeyword") == null
				|| req.getParameter("searchKeyword").equals("") ? "" : req.getParameter("searchKeyword"));

		System.out.println("필드 키워드 : " + searchField + " : " + searchKeyword + " : " + pageNum);

		int start = (pageNum - 1) * pageSize + 1;
		int end = pageNum * pageSize;
		parameterDTO.setStart(start);
		parameterDTO.setEnd(end);
		parameterDTO.setSearchField(searchField);
		parameterDTO.setSearchKeyword(searchKeyword);

		Map<String, Object> maps = new HashMap<>();
		maps.put("totalCount", totalCount);
		maps.put("pageSize", pageSize);
		maps.put("pageNum", pageNum);
		maps.put("searchField", searchField);
		maps.put("searchKeyword", searchKeyword);
		model.addAttribute("maps", maps);

		ArrayList<TransactionDTO> lists = dao.transaction_listPage(parameterDTO);
		model.addAttribute("lists", lists);

		String pagingImg = PagingUtil.pagingImg(totalCount, pageSize, blockPage, pageNum,
				req.getContextPath() + "/transactionList.do?", searchField, searchKeyword,req.getContextPath());
		System.out.println("컨텍스트패스 : " + req.getContextPath() + "/transactionList.do?");
		model.addAttribute("pagingImg", pagingImg);

		return "transaction/transactionList";
	}

	// 상세보기
	@GetMapping("/transactionView.do")
	public String memberGet3(HttpServletResponse resp, HttpServletRequest req, TransactionDTO transactionDTO,
			Model model) {

		req.getParameter("transaction_idx");

		String searchField = req.getParameter("searchField");
		String searchKeyword = req.getParameter("searchKeyword");
		String pageNum = req.getParameter("pageNum");

		System.out.println("트렌잭션 뷰 페이지 : " + searchField + " : " + searchKeyword + " :" + pageNum);

		Map<String, String> maps = new HashMap<>();
		maps.put("searchField", searchField);
		maps.put("searchKeyword", searchKeyword);
		maps.put("pageNum", pageNum);

		TransactionDTO transactionDTO2 = dao.transaction_view(transactionDTO);
		transactionDTO2.setTransaction_content(transactionDTO2.getTransaction_content().replace("\r\n", "<br>"));

		// sfile1~3 수집 + 중복 제거(순서 유지)
		List<String> imageList = new ArrayList<>();
		if (transactionDTO2.getSfile1() != null && !transactionDTO2.getSfile1().isEmpty())
			imageList.add(transactionDTO2.getSfile1());
		if (transactionDTO2.getSfile2() != null && !transactionDTO2.getSfile2().isEmpty())
			imageList.add(transactionDTO2.getSfile2());
		if (transactionDTO2.getSfile3() != null && !transactionDTO2.getSfile3().isEmpty())
			imageList.add(transactionDTO2.getSfile3());

		Set<String> uniq = new LinkedHashSet<>(imageList);
		imageList = new ArrayList<>(uniq);

		System.out.println("이미지 개수(중복제거 후) : " + imageList.size());

		try {
			String imageJson = new ObjectMapper().writeValueAsString(imageList);
			model.addAttribute("imageJson", imageJson);
		} catch (Exception e) {
			model.addAttribute("imageJson", "[]");
		}

		model.addAttribute("imageList", imageList);
		model.addAttribute("maps", maps);
		model.addAttribute("transactionDTO", transactionDTO2);

		return "transaction/transactionView";
	}

	// 수정화면 가기
	@GetMapping("/transactionEdit.do")
	public String transactionEditGet(TransactionDTO transactionDTO, Model model) {
		transactionDTO = dao.transaction_view(transactionDTO);
		model.addAttribute("transactionDTO", transactionDTO);
		return "transaction/transactionEdit";
	}

	// 수정처리하기
	@PostMapping("/transactionEdit.do")
	public String memberPost4(HttpServletRequest req, TransactionDTO transactionDTO, Model model) {
		String transaction_idx = req.getParameter("transaction_idx");
		String transaction_title = req.getParameter("transaction_title");
		String transaction_content = req.getParameter("transaction_content");
		String transaction_price = req.getParameter("transaction_price");

		try {
			String uploadDir = ResourceUtils.getFile("classpath:static/uploads/").toPath().toString();

			// 새로 업로드된 파일들을 (original, saved) 순서 리스트로 수집
			List<String[]> newFiles = new ArrayList<>();
			for (Part part : req.getParts()) {
				if (!"ofile".equals(part.getName()))
					continue;

				String originalFileName = part.getSubmittedFileName();
				if (originalFileName == null || originalFileName.isBlank())
					continue;

				part.write(uploadDir + File.separator + originalFileName);
				String savedFileName = MyFunctions.renameFile(uploadDir, originalFileName);

				newFiles.add(new String[] { originalFileName, savedFileName });
			}

			// 기존 글 상태 확인
			TransactionDTO cur = dao.transaction_view(transactionDTO);

			// 새 파일들을 빈 슬롯부터 채움 (1 -> 2 -> 3)
			for (String[] f : newFiles) {
				if (cur.getOfile1() == null || cur.getOfile1().isEmpty()) {
					cur.setOfile1(f[0]);
					cur.setSfile1(f[1]);
					cur.setNum(1);
					dao.transaction_updateFile(cur);
				} else if (cur.getOfile2() == null || cur.getOfile2().isEmpty()) {
					cur.setOfile2(f[0]);
					cur.setSfile2(f[1]);
					cur.setNum(2);
					dao.transaction_updateFile(cur);
				} else if (cur.getOfile3() == null || cur.getOfile3().isEmpty()) {
					cur.setOfile3(f[0]);
					cur.setSfile3(f[1]);
					cur.setNum(3);
					dao.transaction_updateFile(cur);
				} else {
					break; // 이미 3칸 찼으면 중단
				}
			}

			int result = dao.updateTitleContentPrice(transaction_price, transaction_title, transaction_content,
					transaction_idx);
			if (result == 1)
				System.out.println("타이틀 컨텐츠 수정성공");

		} catch (Exception e) {
			System.out.println("등록 파일 업로드 에러" + e);
			e.printStackTrace();
		}

		return "redirect:transactionList.do";
	}

	// 파일 하나 삭제
	@PostMapping("/transaction_DeleteFileOne.do")
	public String transaction_DeleteFileOne(HttpServletRequest req) {

		String transaction_idx = req.getParameter("transaction_idx");
		String sfile = req.getParameter("sfile");
		String imgCount = req.getParameter("imgCount");

		System.out.println("deleteFile " + transaction_idx + " : " + sfile + " : " + imgCount);

		int result = dao.transaction_deleteFileOne(transaction_idx, imgCount);
		if (result == 1)
			System.out.println("파일 업데이트 성공");

		try {
			String uploadDir = ResourceUtils.getFile("classpath:static/uploads/").toPath().toString();
			System.out.println("물리적 경로 : " + uploadDir);

			if (sfile != null && !sfile.equals("")) {
				FileUtil.deleteFile(req, uploadDir, sfile);
			}
		} catch (Exception e) {
			System.out.println("uploadDir 에러 발생 : " + e);
			e.printStackTrace();
		}

		return "redirect:transactionEdit.do?transaction_idx=" + transaction_idx;
	}

	// 파일 전체 삭제
	@PostMapping("/transaction_DeleteFileAll.do")
	public String boardDeleteFileAll(HttpServletRequest req) {

		String transaction_idx = req.getParameter("transaction_idx");

		TransactionDTO transactionDTO = new TransactionDTO();
		transactionDTO.setTransaction_idx(Integer.parseInt(transaction_idx));

		TransactionDTO transactionDTO2 = dao.transaction_view(transactionDTO);

		String sfile1 = transactionDTO2.getSfile1();
		String sfile2 = transactionDTO2.getSfile2();
		String sfile3 = transactionDTO2.getSfile3();

		try {
			String uploadDir = ResourceUtils.getFile("classpath:static/uploads/").toPath().toString();
			System.out.println("물리적 경로 : " + uploadDir);

			if (sfile1 != null && !sfile1.equals("")) {
				FileUtil.deleteFile(req, uploadDir, sfile1);
			}
			if (sfile2 != null && !sfile2.equals("")) {
				FileUtil.deleteFile(req, uploadDir, sfile2);
			}
			if (sfile3 != null && !sfile3.equals("")) {
				FileUtil.deleteFile(req, uploadDir, sfile3);
			}
		} catch (Exception e) {
			System.out.println("파일 전체 삭제 오류" + e);
			e.printStackTrace();
		}

		int result = dao.transaction_DeleteFileAll(transaction_idx);
		if (result == 1)
			System.out.println("전체삭제 성공");

		return "redirect:transactionEdit.do?transaction_idx=" + transaction_idx;
	}

	// 글 삭제하기
	@PostMapping("/transaction_Delete.do")
	public String member6(HttpServletRequest req) {

		String transaction_idx = req.getParameter("transaction_idx");
		String sfile1 = req.getParameter("sfile1");
		String sfile2 = req.getParameter("sfile2");
		String sfile3 = req.getParameter("sfile3");

		System.out
				.println("transaction_Delete.do " + transaction_idx + " : " + sfile1 + " : " + sfile2 + " : " + sfile3);

		int result = dao.transaction_delete(transaction_idx);
		if (result == 1)
			System.out.println("삭제 성공");

		try {
			String uploadDir = ResourceUtils.getFile("classpath:static/uploads/").toPath().toString();
			System.out.println("물리적 경로 : " + uploadDir);

			if (sfile1 != null && !sfile1.equals("")) {
				FileUtil.deleteFile(req, uploadDir, sfile1);
			}
			if (sfile2 != null && !sfile2.equals("")) {
				FileUtil.deleteFile(req, uploadDir, sfile2);
			}
			if (sfile3 != null && !sfile3.equals("")) {
				FileUtil.deleteFile(req, uploadDir, sfile3);
			}

		} catch (Exception e) {
			System.out.println("uploadDir 에러 발생 : " + e);
			e.printStackTrace();
		}

		return "redirect:transactionList.do";
	}

	// 파일 다운로드
	@GetMapping("/transactionDownload.do")
	public void memberGet5(HttpServletRequest req, HttpServletResponse res) {
		String transaction_idx = req.getParameter("transaction_idx");
		String ofile1 = req.getParameter("ofile1");
		String sfile1 = req.getParameter("sfile1");

		System.out.println("트렌젝션 파일 다운로드 : " + transaction_idx + " : " + ofile1 + " : " + sfile1);

		try {
			String uploadDir = ResourceUtils.getFile("classpath:static/uploads/").toPath().toString();
			System.out.println("물리적 경로 : " + uploadDir);
			FileUtil.download(req, res, uploadDir, sfile1, ofile1);
		} catch (Exception e) {
			System.out.println("download 에러 발생" + e);
			e.printStackTrace();
		}
	}

	// 구매하기
	@GetMapping("/transaction_purchase.do")
	public String transaction_purchaseGet(HttpServletRequest req) {
		String transaction_idx = req.getParameter("transaction_idx");
		int result = dao.transaction_purchase(transaction_idx);
		if (result == 1)
			System.out.println("구매 업데이트 성공");
		return "redirect:transactionList.do";
	}

	// 이건 아무것도 아님
	@GetMapping("/transactionPayment.do")
	public String transactionPaymentGet() {
		return "transaction/transactionPayment";
	}

	// 사용안함
	public String fileCheckName(String fileName) {
		String cate = "";
		String ext = "";

		if (fileName == null || fileName.isEmpty()) {
			ext = "etc";
		} else {
			ext = fileName.substring(fileName.lastIndexOf(".") + 1).toLowerCase();
		}

		String[] imgExts = { "jpg", "jpeg", "gif", "png", "bmp", "webp" };
		String[] videoExts = { "avi", "mp4", "mov", "wmv", "flv", "mkv" };
		String[] audioExts = { "mp3", "wav", "ogg", "aac", "flac" };

		List<String> imgList = new ArrayList<>(Arrays.asList(imgExts));
		List<String> videoList = new ArrayList<>(Arrays.asList(videoExts));
		List<String> audioList = new ArrayList<>(Arrays.asList(audioExts));

		if (imgList.contains(ext)) {
			cate = "img";
		} else if (videoList.contains(ext)) {
			cate = "video";
		} else if (audioList.contains(ext)) {
			cate = "audio";
		} else {
			cate = "etc";
		}
		System.out.println("카테고리: " + cate);

		return cate;
	}
}
