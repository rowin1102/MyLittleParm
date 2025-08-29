package utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.io.OutputStream;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public class FileUtil {

	public static void download(HttpServletRequest req, HttpServletResponse resp,
            String directory, String sfileName, String ofileName) {
        //String sDirectory = req.getServletContext().getRealPath(directory);
        try {
        	//System.out.println("11111");

            // 파일을 찾아 입력 스트림 생성
            File file = new File(directory, sfileName);
            InputStream iStream = new FileInputStream(file);
            
            System.out.println("파일 : " + file.toString());
            
            //System.out.println("22222");
            // 한글 파일명 깨짐 방지
            String client = req.getHeader("User-Agent");
            if (client.indexOf("WOW64") == -1) {
                ofileName = new String(ofileName.getBytes("UTF-8"), "ISO-8859-1");
            }
            else {
                ofileName = new String(ofileName.getBytes("KSC5601"), "ISO-8859-1");
            }

            //System.out.println("3333");
            // 파일 다운로드용 응답 헤더 설정
            resp.reset();
            resp.setContentType("application/octet-stream");
            resp.setHeader("Content-Disposition",
                           "attachment; filename=\"" + ofileName + "\"");
            resp.setHeader("Content-Length", "" + file.length() );

            //System.out.println("4444");
            //out.clear();  // 출력 스트림 초기화

            // response 내장 객체로부터 새로운 출력 스트림 생성
            OutputStream oStream = resp.getOutputStream();

            // 출력 스트림에 파일 내용 출력
            byte b[] = new byte[(int)file.length()];
            int readBuffer = 0;
            while ( (readBuffer = iStream.read(b)) > 0 ) {
                oStream.write(b, 0, readBuffer);
            }
            
            //System.out.println("5555");

            // 입/출력 스트림 닫음
            iStream.close();
            oStream.close();
        }
        catch (FileNotFoundException e) {
            System.out.println("파일을 찾을 수 없습니다.");
            e.printStackTrace();
        }
        catch (Exception e) {
            System.out.println("예외가 발생하였습니다.");
            e.printStackTrace();
        }
    }
	
	//파일 삭제 
//	public static void deleteFile(HttpServletRequest req, String directory,
//		String filename) {
//		String sDirectory = req.getServletContext()
//				.getRealPath(directory);
//		
//		File file = new File(sDirectory + File.separator + filename);
//		if( file.exists() ) {
//			file.delete();
//		}
//		
//	}
	
	public static void deleteFile(HttpServletRequest req, String directory,
		String filename) {
		//String sDirectory = req.getServletContext().getRealPath(directory);
		
		File file = new File(directory + File.separator + filename);
		System.out.println( "deleteFile 파일명 : " + file.toString() );
		
		if( file.exists() ) {
			file.delete();
			System.out.println("파일 삭제됨");
		}
			
	}
	
	
	
}
