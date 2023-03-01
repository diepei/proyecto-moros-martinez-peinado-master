package es.upv.etsit.aatt.paco.trabajoaatt;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

import es.dmoral.toasty.Toasty;


public class LoginActivity extends AppCompatActivity {

    EditText nombreEditText, emailEditText, passEditText;
    String nombre, email, pass;
    Button accion;
    FirebaseAuth firebaseAuth;
    int log_reg;

    TextView olvidada;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        firebaseAuth = FirebaseAuth.getInstance();

        nombreEditText = (EditText) findViewById(R.id.nombre);
        emailEditText = (EditText) findViewById(R.id.email);
        passEditText = (EditText) findViewById(R.id.pass);
        accion = (Button) findViewById(R.id.button);

        olvidada = (TextView) findViewById(R.id.olvidada);

        log_reg = getIntent().getIntExtra("register/login",0);

        if (log_reg == 0){
            //Registrar nuevo usuraio
            getSupportActionBar().setTitle(R.string.titulo_registro);
            olvidada.setVisibility(View.GONE);
            accion.setText(R.string.boton_register);
        } else {
            //Login usuraio
            getSupportActionBar().setTitle(R.string.titulo_login);
            nombreEditText.setVisibility(View.GONE);
            accion.setText(R.string.boton_login);
        }

        accion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (log_reg == 0){
                    //Registrar nuevo usuraio
                    registrarUsuario();
                } else {
                    //Login usuraio
                    loginUsuario();
                }
            }
        });
    }

    public void registrarUsuario(){

        nombre = nombreEditText.getText().toString();
        email = emailEditText.getText().toString();
        pass = passEditText.getText().toString();

        if (!nombre.isEmpty() && !email.isEmpty() && !pass.isEmpty()){
            if (pass.length() >= 6){

                firebaseAuth.createUserWithEmailAndPassword(email,pass).addOnCompleteListener( new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull final Task<AuthResult> task) {

                        if (task.isSuccessful()){
                            Toasty.success(getApplicationContext(), R.string.registro_ok, Toasty.LENGTH_SHORT).show();
                            finish();
                            Intent i = new Intent(getApplicationContext(), AppActivity.class);
                            startActivity(i);

                        } else{
                            Toasty.error(getApplicationContext(), R.string.error, Toasty.LENGTH_LONG).show();
                        }
                    }
                });

            } else{
                Toasty.warning(getApplicationContext(), R.string.error_pass, Toasty.LENGTH_LONG).show();
            }
        } else{
            Toasty.warning(getApplicationContext(), R.string.error_campos, Toasty.LENGTH_LONG).show();
        }
    }
    public void loginUsuario(){

        email = emailEditText.getText().toString();
        pass = passEditText.getText().toString();

        if (!email.isEmpty() && !pass.isEmpty()){
            if (pass.length() >= 6){

                firebaseAuth.signInWithEmailAndPassword(email,pass).addOnCompleteListener( new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull final Task<AuthResult> task) {

                        if (task.isSuccessful()){
                            Toasty.success(getApplicationContext(), R.string.login_ok, Toasty.LENGTH_SHORT).show();
                            finish();
                            Intent i = new Intent(getApplicationContext(), AppActivity.class);
                            startActivity(i);

                        } else{
                            Toasty.error(getApplicationContext(), R.string.error_datos, Toasty.LENGTH_LONG).show();
                        }
                    }
                });

            } else{
                Toasty.warning(getApplicationContext(), R.string.error_pass, Toasty.LENGTH_LONG).show();
            }
        } else{
            Toasty.warning(getApplicationContext(), R.string.error_campos, Toasty.LENGTH_LONG).show();
        }
    }

    public void recuperarPass(View view){

        final EditText editText = new EditText(this);
        editText.requestFocus();

        LinearLayout container = new LinearLayout(this);
        container.setOrientation(LinearLayout.VERTICAL);
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
        layoutParams.setMargins(32,0,32,0);

        editText.setLayoutParams(layoutParams);
        container.addView(editText,layoutParams);

        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        builder.setMessage(R.string.introduce_mail);
        builder.setTitle(R.string.titulo_recuperar);
        builder.setView(container);
        builder.setPositiveButton(R.string.enviar, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String mail = editText.getText().toString();
                if (!mail.isEmpty()){

                    firebaseAuth.setLanguageCode("es");
                    firebaseAuth.sendPasswordResetEmail(mail).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {

                            if (task.isSuccessful()){
                                Toasty.success(getApplicationContext(),R.string.correo_enviado, Toasty.LENGTH_LONG).show();
                            } else {
                                Toasty.error(getApplicationContext(),R.string.error, Toasty.LENGTH_SHORT).show();
                            }
                        }
                    });

                } else{
                    Toasty.warning(getApplicationContext(),R.string.error_mail, Toasty.LENGTH_LONG).show();

                }
            }
        });
        builder.setNegativeButton(R.string.cancelar, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });
        builder.show();
    }
}
