package br.com.digitalhouse.firepizzaapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;

public class CadastrarActivity extends AppCompatActivity {

    private static final String TAG = "Cadastrar Activity" ;
    private FirebaseAuth firebaseAuth;
    private EditText emailEditText;
    private EditText senhaEditText;
    private EditText nomeEditText;
    private Button cadastrarButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cadastar);

        emailEditText = findViewById(R.id.cadastrar_email_edit_text);
        senhaEditText = findViewById(R.id.cadastrar_senha_edit_text);
        nomeEditText = findViewById(R.id.cadastrar_nome_edit_text);
        cadastrarButton = findViewById(R.id.registrar_button_id);

        firebaseAuth = FirebaseAuth.getInstance();


        cadastrarButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Cadastrar();
            }
        });

     }


    private void Cadastrar(){

        String email = emailEditText.getEditableText().toString();
        String senha = senhaEditText.getEditableText().toString();


        firebaseAuth.createUserWithEmailAndPassword(email, senha)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "Cadastrado com Sucesso");
                            FirebaseUser user = firebaseAuth.getCurrentUser();
                            AtualizarPerfil();


                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "createUserWithEmail:failure", task.getException());
                            Toast.makeText(CadastrarActivity.this, "Cadastro Falhou",
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                });}

                private void AtualizarPerfil(){

                    String nome = nomeEditText.toString();

                    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

                    UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                            .setDisplayName(nome)
                           // .setPhotoUri(Uri.parse("https://example.com/jane-q-user/profile.jpg"))
                            .build();

                    user.updateProfile(profileUpdates)
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()) {
                                        irParaPrincipal();
                                        Log.d(TAG, "User profile updated.");
                                    }
                                }
                            });

                }

    private void irParaPrincipal() {

        Intent intent = new Intent(this,LoginActivity.class);
        startActivity(intent);
    }

}
