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
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.io.ByteArrayOutputStream;
import java.util.Date;


public class PrincipalActivity extends AppCompatActivity {

    private TextView nomeTextView;
    private CircleImageView perfilImageView;
    private Button trocarFotoButton;
    private FirebaseUser user;
    private PizzaAdapter pizzaAdapter;
    private RecyclerView pizzaRecyclerView;


    FirebaseStorage storage = FirebaseStorage.getInstance();
    static final int REQUEST_IMAGE_CAPTURE = 1;



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

        Button mussarela = findViewById(R.id.mussarela_button);
        Button calabresa = findViewById(R.id.calabresa_button);

        


    }

    private void PedirPizza (String descricao,float preco){

        Pizza pizza = new Pizza();
        pizza.setDescricao(descricao);
        pizza.setPreco(preco);
        pizza.setEntregue(false);
        pizza.setData(new Date());
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
