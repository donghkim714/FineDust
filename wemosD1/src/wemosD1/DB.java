package wemosD1;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;

public class DB extends Thread {
	public void run()
	{
		try {
			sleep(1000);
		} catch (Exception e) {
			// TODO: handle exception
		}
	}
	public DB(String strt) {
		try {
			Class.forName("org.gjt.mm.mysql.Driver");
			System.out.println("드라이버 연결 성공");
		} catch (Exception e) {
			System.err.println("연결 실패");
		}
		Connection conn = null;
		PreparedStatement pstmt = null;
		
		String url = "jdbc:mysql://127.0.0.1:3306/wemos";
		String id = "root";
		String pwd = "abcd1234";
		String query = null;
		amain.ctnum ++;
		try {
			conn = DriverManager.getConnection(url, id, pwd);
			System.out.println("url 연결 성공");
		} catch (Exception e) {
			System.err.println("url 연결 실패");
		}
			query = "insert into w1 values(?,?);";
			
			String dust = strt;
			try {
				pstmt = conn.prepareStatement(query);
				pstmt.setInt(1, amain.ctnum);
				pstmt.setString(2, dust);
				pstmt.executeUpdate();
				run();
				pstmt.close();
			} catch (Exception e) {
				System.err.println("ㅗㅇ");
			}
	}
}

