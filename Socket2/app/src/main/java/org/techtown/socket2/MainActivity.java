package org.techtown.socket2;

import android.app.Activity;
import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.os.Vibrator;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.LinkedList;

public class MainActivity extends AppCompatActivity {
// handler //메인과 서버 스레드간 메세지를 주거니받거니 하기 위하여 사용.
//msg.what(int)  <-- 1111(고유 아이디값)이면 서버 스레드에서 메세지 핫이 있어야하고 1111이 있어야함 고유한 아이디값으로 메인 에서 값을 줘도됨. 22 23같은값을 1111이 가지고 있다가 핸들러로 메인으로 넘겨중
//LinkedList : 객체 생성시 데이터 저장 영역이 생기지 않으며 서로 인접 데이터를 가리킨다. 연결만해줌 저장해주지 않고
//sendMessage() : 생성자 handler 안에 구현해 주어야함 이안에는 msh.whar(int) 가 있어야한다.
    TextView showText;
    Button connectBtn;
    Button Button_send;
    EditText ip_EditText;
    EditText port_EditText;
    EditText editText_message;
    Handler msghandler;     //핫아이디를 받기위해

    SocketClient client;        //서버 접속을 위한 클래스
    ReceiveThread receive;      //서버에서 보내온 데이터 수신
    SendThread send;            //핸드폰에서 서버로 전송
    Socket socket;              //네트워크

    Vibrator mvib;
    LinkedList<SocketClient> threadList;    //객체 생성시 데이터 저장 영역이 생기지 않으며, 서로 인접한 데이터를 가리킨다.
                                            /*
                                                1. Handler 핸들러를 이용하여 쓰레드간 메세지 전달
                                                2.1 client 접속됨가 동시에 ip번호 와 port 번호가 linkedlist에 담김
                                                2.2 동시에 receive 쓰레드가 동작됨
                                            */

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);         //엑티비티 초기화
        setContentView(R.layout.activity_main);     //레이아웃 설정
        ip_EditText=(EditText)findViewById(R.id.ip_EditText);
        connectBtn = (Button)findViewById(R.id.connect_Button);
        port_EditText = (EditText)findViewById(R.id.port_EditText);
        showText=(TextView)findViewById(R.id.showText_TextView);
        editText_message  = (EditText)findViewById(R.id.editText_massage);
        Button_send = (Button)findViewById(R.id.Button_send);
        threadList = new LinkedList<MainActivity.SocketClient>(); //메인 엑티비티클래스에 소켓 클라이언트는 ip와 포트번호를 가지고있는데 그걸 연결리스트로
        mvib = (Vibrator)getSystemService(Context.VIBRATOR_SERVICE);
        ip_EditText.setText("192.168.0.1");
        msghandler = new Handler(){
            @Override
            public void handleMessage(Message hdmsg) {
                if(hdmsg.what == 1111){     //구분자 핫 아이디값 1111
                    if(hdmsg.obj.toString().length() > 0){  //핸들러가 문자열을 가지고있으면
                        String[] data = hdmsg.obj.toString().split(";");
                        int ctnum = Integer.parseInt(data[0]);
                        int dust = Integer.parseInt(data[1]);
                        String temp_msg = ";";

                    }
                }
            }
        };

        //연결
            connectBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    client = new SocketClient(ip_EditText.getText().toString(), port_EditText.getText().toString());    //클라안에는 ip와 포트번호존재.
                    threadList.add(client);
                    client.start();
                }
            });

//전송
        Button_send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(editText_message.getText().toString()  != null){
                    send = new SendThread(socket);
                    send.start();
                    editText_message.setText("");
                }
            }
        });


    }
    class SocketClient extends Thread{      //ip 포트번호를 입력받아 연결
        boolean thraedAlive; //스레드가 살아있는지 죽었는지 쳌
        String ip;
        String port;
        DataOutputStream output = null;
        public SocketClient(String ip, String port){
            thraedAlive = true;
            this.ip = ip;
            this.port = port;

        }

        @Override
        public void run() {     //맨먼저 가리킴
            try{
                socket = new Socket(ip,Integer.parseInt(port));
                output = new DataOutputStream(socket.getOutputStream());
                receive = new ReceiveThread(socket);
                receive.start();        //리시브 클래스의 런메소드를 바로 시작.
            }catch (IOException e){
                e.printStackTrace();
            }
        }
    }
    class ReceiveThread extends Thread{
        private Socket sock = null;
        DataInputStream input;
        public ReceiveThread(Socket socket){
            this.sock = socket;
            try{
                input = new DataInputStream(sock.getInputStream()); //서버쪽에서 보내오면 얘가 가지고 있음.
            }catch (Exception e){

            }
        }

        public void run(){
            try{
                while(input != null){
                    String msg;
                    int count = input.available();  //데이터를 카운트를 해줌.
                    byte[] rcv = new byte[count];
                    input.read(rcv);
                    msg = new String(rcv);
                    //메시지 수신후 Handler 로 전달.
                    if(count > 0){
                        Log.d(ACTIVITY_SERVICE, "test" + msg);
                        Message hdmsg = msghandler.obtainMessage();
                        hdmsg.what = 1111;      //이걸보고 메인에서 아이디 1111을 찾아서 값을 가져올수 있다.
                        hdmsg.obj = msg;
                        msghandler.sendMessage(hdmsg);
                        Log.d(ACTIVITY_SERVICE,hdmsg.obj.toString());
                    }
                }
            }catch(IOException e){
                e.printStackTrace();
            }

        }



    }
    class SendThread extends Thread{    //request 보내기
        Socket socket;
        String sendmsg = editText_message.getText().toString() + "\n";
        DataOutputStream output;
        public SendThread(Socket socket){
            this.socket = socket;
            try{
                output = new DataOutputStream(socket.getOutputStream());
            }catch(Exception e) {

            }
        }
        public void run(){
            try{
                Log.d(ACTIVITY_SERVICE, "11111111");
                if(output != null){
                    if(sendmsg != null){
                        output.write(sendmsg.getBytes());
                    }
                }
            }catch (IOException e){
                e.printStackTrace();
            }
        }
    }

}
