package br.com.digitalhouse.firepizzaapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import br.com.digitalhouse.firepizzaapp.adapter.PizzaAdapter;
import br.com.digitalhouse.firepizzaapp.model.Pizza;
import de.hdodenhof.circleimageview.CircleImageView;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class PrincipalActivity extends AppCompatActivity {

    private TextView nomeTextView;
    private CircleImageView perfilImageView;
    private Button trocarFotoButton;
    private FirebaseUser user;
    private PizzaAdapter pizzaAdapter;
    private RecyclerView pizzaRecyclerView;

    FirebaseStorage storage = FirebaseStorage.getInstance();
    static final int REQUEST_IMAGE_CAPTURE = 1;

    FirebaseFirestore db = FirebaseFirestore.getInstance();
    private String TAG = "PrincipalActivity";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_principal);

        pizzaRecyclerView = findViewById(R.id.recycler_pizza_view);

        nomeTextView = findViewById(R.id.nome_bem_vindo_id);
        perfilImageView = findViewById(R.id.perfil_imageView_id);
        trocarFotoButton = findViewById(R.id.trocar_foto_button);


        user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            String name = user.getDisplayName();

            String email = user.getEmail();


            nomeTextView.setText(name);
        }

        trocarFotoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                tirarFoto();
            }
        });


        baixarFoto();

        pizzaAdapter = new PizzaAdapter();
        pizzaRecyclerView.setAdapter(pizzaAdapter);
        pizzaRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        Button mussarelaButton = findViewById(R.id.mussarela_button);

        Button calabresaButton = findViewById(R.id.calabresa_button);
        mussarelaButton.setOnClickListener(view -> {
            pedirPizza("mussarela",6.00);
        });

        calabresaButton.setOnClickListener(view -> {
            pedirPizza("calabrasa",8.00);
            atualizarDadosFireBase();
        });


        atualizarDadosFireBase();
    }

    private void pedirPizza (String descricao,double preco){

        Pizza pizza = new Pizza();
        pizza.setDescricao(descricao);
        pizza.setPreco(preco);
        pizza.setEntregue(false);
        pizza.setData(new Date());

        salvarFirebase(pizza);
    }

    private void salvarFirebase(Pizza pizza) {

        // Create a new user with a first, middle, and last name
        Map<String, Object> pizzaDb = new HashMap<>();
        pizzaDb.put("descricao", pizza.getDescricao());
        pizzaDb.put("preco", pizza.getPreco());
        pizzaDb.put("Entregue",pizza.isEntregue());
        pizzaDb.put("dasta",new Timestamp(pizza.getData()));



// Add a new document with a generated ID
        db.collection("users")
                .document(user.getUid())
                .collection("pizzas")
                .add(pizzaDb)
                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {
                        Log.d(TAG, "DocumentSnapshot added with ID: " + documentReference.getId());
                        atualizarDadosFireBase();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w(TAG, "Error adding document", e);
                    }
                });
    }

    private void atualizarDadosFireBase(){
        db.collection("users").document(user.getUid()).collection("pizzas")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {

                        List<Pizza> pizzaList = new ArrayList<>();

                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                Log.d(TAG, document.getId() + " => " + document.getData());
                                Pizza pizza = new Pizza();
                                pizza.setDescricao((String) document.get("descricao"));
                                pizza.setPreco((double) document.get("preco"));
                                pizza.setEntregue((Boolean) document.get("Entregue"));
                                pizzaList.add(pizza);

                                pizzaAdapter.atualizarPizzas(pizzaList);

                            }
                        } else {
                            Log.w(TAG, "Error getting documents.", task.getException());
                        }
                    }
                });
    }
    private void baixarFoto() {

        StorageReference reference  = storage.getReference("perfil/"+user.getUid());

        reference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {

                Picasso.get().load(uri).into(perfilImageView);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                // Handle any errors
            }
        });

    }

    private void tirarFoto() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            Bundle extras = intent.getExtras();
            Bitmap imageBitmap = (Bitmap) extras.get("data");
            perfilImageView.setImageBitmap(imageBitmap);

            StorageReference reference = storage.getReference("perfil/"+user.getUid());



            // Get the data from an ImageView as bytes

            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            imageBitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
            byte[] data = baos.toByteArray();

            UploadTask uploadTask = reference.putBytes(data);
            uploadTask.addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception exception) {
                    Toast.makeText(PrincipalActivity.this,"Upload Falhou",Toast.LENGTH_LONG).show();
                }
            }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    Toast.makeText(PrincipalActivity.this,"Upload Concluído",Toast.LENGTH_LONG).show();
                }
            });


        }
    }
}
