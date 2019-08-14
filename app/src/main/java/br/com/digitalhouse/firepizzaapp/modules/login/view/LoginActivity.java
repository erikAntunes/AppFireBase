package br.com.digitalhouse.firepizzaapp.modules.login.view;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProviders;
import br.com.digitalhouse.firepizzaapp.CadastrarActivity;
import br.com.digitalhouse.firepizzaapp.PrincipalActivity;
import br.com.digitalhouse.firepizzaapp.R;
import br.com.digitalhouse.firepizzaapp.modules.login.viewmodel.LoginViewModel;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class LoginActivity extends AppCompatActivity {

    private EditText emailEditText;
    private EditText senhaEditText;
    private Button loginButton;
    private Button registrarButton;
    private LoginViewModel loginViewModel;
    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        emailEditText = findViewById(R.id.email_edit_text);
        senhaEditText = findViewById(R.id.senha_edit_text);
        progressBar = findViewById(R.id.progressBar);

        registrarButton = findViewById(R.id.registrar_button);
        registrarButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                irParaCadastrar();
            }
        });

        loginButton = findViewById(R.id.login_button);
        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Logar();
            }
        });

        loginViewModel = ViewModelProviders.of(this).get(LoginViewModel.class);

        loginViewModel.getAutenticadoLiveData()
                .observe(this, autenticado ->{
                    if(autenticado){
                        irParaPrincipal();
                    }else {
                        Toast.makeText(this,"Falha na autenticação",Toast.LENGTH_SHORT).show();
                    }
                });
        loginViewModel.getLoaderLiveData()
                .observe(this, showLoader ->{
                    if ((showLoader)) {
                        progressBar.setVisibility(View.VISIBLE);
                    }else{
                        progressBar.setVisibility(View.GONE);
                    }
                });
    }


    private void Logar() {
        String email = emailEditText.getEditableText().toString();

        String senha = senhaEditText.getEditableText().toString();

        loginViewModel.autenticarUsuario(email,senha);

    }

    private void irParaPrincipal() {

        Intent intent = new Intent(this, PrincipalActivity.class);
        startActivity(intent);
    }

    private void irParaCadastrar() {

        Intent intent = new Intent(this, CadastrarActivity.class);
        startActivity(intent);
    }
}
