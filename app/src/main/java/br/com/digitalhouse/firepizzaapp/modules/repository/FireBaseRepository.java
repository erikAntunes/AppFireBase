package br.com.digitalhouse.firepizzaapp.modules.repository;

import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

import androidx.annotation.NonNull;
import io.reactivex.Completable;

public class FireBaseRepository {

    private static final String TAG = "LoginActivity";

    private FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();

    public Completable autenticar(String email,String senha){
        return Completable.create(emitter ->{
            firebaseAuth.signInWithEmailAndPassword(email, senha)
                    .addOnCompleteListener( new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                // Sign in success, update UI with the signed-in user's information
                                Log.d(TAG, "signInWithEmail:success");
                                emitter.onComplete();

                            } else {
                                // If sign in fails, display a message to the user.
                                Log.w(TAG, "signInWithEmail:failure", task.getException());
                                emitter.onError(task.getException());

                            }


                        }
                    });
        });
    }
}
