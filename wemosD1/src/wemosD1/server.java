package wemosD1;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;

public class server {
	static String strt = null;
	public server() {
		while(true) {
		
		Socket socket = null;
		ServerSocket serversocket = null;
		try {
			serversocket= new ServerSocket(9000); 
			socket = serversocket.accept();
			System.out.println("���� ����");
			
		} catch (Exception e) {
			System.err.println("���� ���� ����");
		}
		try {
			BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
			String str = in.readLine();
			strt = str;
			System.out.println("�̼����� �� : " + strt);
			DB db = new DB(strt);
			OutputStream out = socket.getOutputStream();
			String som = "d";
			out.write(som.getBytes());
			in.reset();
			in.close();
		} catch (Exception e) {
			
		}
		finally {
			try {
				serversocket.close();
			} catch (Exception e2) {
				// TODO: handle exception
			}
			try {
				socket.close();
			} catch (Exception e2) {
				// TODO: handle exception
			}
		}
		}
	}
}

