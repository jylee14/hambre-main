package com.example.jun.hambre_main;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class Login extends AppCompatActivity {
    private EditText email;
    private EditText password;
    private Button button;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        email = (EditText)findViewById(R.id.email);
        password = (EditText)findViewById(R.id.password);
        button = (Button)findViewById(R.id.login);

        Bundle bundle = getIntent().getExtras();
        final String loginType = bundle.getString("type");
        button.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v){
                if(email.getText().length() == 0)
                    Toast.makeText(Login.this, "Please enter an E-Mail address", Toast.LENGTH_SHORT).show();
                else if(password.getText().length() == 0)
                    Toast.makeText(Login.this, "Please enter a Password", Toast.LENGTH_SHORT).show();
                else{
                    //logging in happens here
                    if(loginType.equalsIgnoreCase("google")){
                        //google Login
                    }else{
                        //fb login
                    }

                }
            }
        });
    }
}
