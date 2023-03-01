package es.upv.etsit.aatt.paco.trabajoaatt;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import es.dmoral.toasty.Toasty;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        getSupportActionBar().hide();
    }

    public void botonInfo(View view){
        Intent i = new Intent(getApplicationContext(), InfoActivity.class);
        startActivity(i);
    }

    public void registerUsuario(View view){
        Intent i = new Intent(getApplicationContext(), LoginActivity.class);
        i.putExtra("register/login",0);
        startActivity(i);
    }

    public void loginUsuario(View view) {
        Intent i = new Intent(getApplicationContext(), LoginActivity.class);
        i.putExtra("register/login", 1);
        startActivity(i);
    }

    public void noLogin(View view){
        Intent i = new Intent(getApplicationContext(), AppActivity.class);
        startActivity(i);
    }

}
