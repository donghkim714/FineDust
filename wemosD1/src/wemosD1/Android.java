package wemosD1;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class Android extends Thread {
	ServerSocket serversocket = null;
	Socket socket = null;
	static int ctnum = 0;

	public Android() {
		try {
			serversocket= new ServerSocket(9000); 
			socket = serversocket.accept();
		} catch (Exception e) {
			// TODO: handle exception
		}
		
	}

	public void run() {

		try {
			BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
			if (in.readLine().equals("request")) {
				Class.forName("org.gjt.mm.mysql.Driver");
				ctnum = ctnum + 1;
				Connection conn = null;
				PreparedStatement pstmt = null;

				String url = "jdbc:mysql://127.0.0.1:3306/wemos";
				String id = "root";
				String pwd = "abcd1234";
				String query = null;
				conn = DriverManager.getConnection(url, id, pwd);
				query = "select * from w1 where ctnum = ?";
				pstmt = conn.prepareStatement(query);

				pstmt.setInt(1, ctnum);
				ResultSet rs = pstmt.executeQuery();
				if (rs.next()) {

					OutputStream out = socket.getOutputStream();
					String str = Integer.toString(rs.getInt("ctnum")) + ";" + rs.getString("dust");
					out.write(str.getBytes());

				}
			}
		} catch (Exception e) {

		}
		finally {
			try {
				socket.close();
			} catch (Exception e2) {
				// TODO: handle exception
			}
			try {
				serversocket.close();
			} catch (Exception e2) {
				// TODO: handle exception
			}
		}
	}
}
