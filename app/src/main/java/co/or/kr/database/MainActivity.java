package co.or.kr.database;

import android.app.ProgressDialog;
import android.graphics.Color;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class MainActivity extends AppCompatActivity {

    EditText email, pw;
    LinearLayout background;
    ProgressDialog progressDialog;

    Handler handler = new Handler(){

        @Override
        public void handleMessage(Message msg) {
                try{

            JSONObject login = (JSONObject)msg.obj;
            if(login.getString("username").equals("null")){
                background.setBackgroundColor(Color.RED);
                Toast.makeText(MainActivity.this,"로그인실패",Toast.LENGTH_LONG).show();
            }else{
                background.setBackgroundColor(Color.GREEN);
                Toast.makeText(MainActivity.this, login.getString("nickname"),Toast.LENGTH_LONG).show();

            }
        }catch(Exception e){
                Log.e("핸들러 예외",e.getMessage());
            }
        progressDialog.dismiss();
       }
    };

    class ThreadEx extends Thread{
        @Override
        public void run() {

            String json ="";

            try{
             String addr = " http://192.168.0.23:9000/pk/login?";
             addr = addr+"useremail="+email.getText().toString().trim()+"&";
             addr = addr +"userpw="+pw.getText().toString().trim();

                URL url = new URL(addr);
                HttpURLConnection con = (HttpURLConnection)url.openConnection();
                con.setConnectTimeout(3000);
                con.setUseCaches(false);

                BufferedReader br = new BufferedReader(new InputStreamReader(con.getInputStream()));
                StringBuilder sb = new StringBuilder();
                while(true){
                    String line = br.readLine();
                    if(line ==null){
                        break;
                    }
                   sb.append(line);
                }
                br.close();
                con.disconnect();
                json= sb.toString();
            }catch (Exception e){
                Log.e("다운로드 예외",e.getMessage());
            }


            try{
                if(json!=null){
                    JSONObject root = new JSONObject(json);
                    JSONObject member = root.getJSONObject("member");
                    Message message = new Message();
                    message.obj = member;
                    handler.sendMessage(message);

                }
            }catch (Exception e){
                Log.e("파싱 예외",e.getMessage());
            }

        }
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        email = (EditText)findViewById(R.id.email);
        pw = (EditText)findViewById(R.id.pw);

        background = (LinearLayout)findViewById(R.id.backgroud);

        Button button = (Button)findViewById(R.id.btn);
        button.setOnClickListener(new Button.OnClickListener(){
            @Override
            public void onClick(View v) {
                progressDialog = ProgressDialog.show(MainActivity.this,"","로그인처리중");
                ThreadEx th = new ThreadEx();
                th.start();
            }
        });

    }
}
