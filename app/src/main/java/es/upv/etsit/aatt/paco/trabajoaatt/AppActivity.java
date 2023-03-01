package es.upv.etsit.aatt.paco.trabajoaatt;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import es.dmoral.toasty.Toasty;
import es.upv.etsit.aatt.paco.trabajoaatt.retrofit.CryptoList;
import es.upv.etsit.aatt.paco.trabajoaatt.retrofit.Datum;

import android.content.DialogInterface;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.content.Intent;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import com.google.firebase.auth.FirebaseAuth;

public class AppActivity extends AppCompatActivity {
    CryptoListAdapter adapter;
    APIInterface apiInterface;
    private RecyclerView recyclerView;
    private List<Datum> cryptoList = null;

    EditText buscador;
    ProgressBar cargando;

    FirebaseAuth firebaseAuth;
    boolean registrado = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_app);

        setTitle(R.string.titulo_activity_app);

        apiInterface = APIClient.getClient().create(APIInterface.class);

        cargando = (ProgressBar) findViewById(R.id.progressBar);
        cargando.setVisibility(View.VISIBLE);

        initRecyclerView();
        getCoinList();

        buscador = findViewById(R.id.buscador);
        buscador.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                filtrar(s.toString());
            }
        });

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseAuth.addAuthStateListener(new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {

                if (firebaseAuth.getCurrentUser() != null){
                    //cargar interfaz de usuario registrado
                    registrado = true;
                } else {
                    //cargar interfaz de usuario NO registrado
                    registrado = false;
                }
            }
        });
    }

    private void filtrar (String s){
        ArrayList<Datum> listaFiltrada = new ArrayList<>();

        for (Datum datos : cryptoList){
            if (datos.getName().toLowerCase().contains(s.toLowerCase())){
                listaFiltrada.add(datos);
            }
        }
        adapter.filtrar(listaFiltrada);
    }

    private void initRecyclerView() {
        // Lookup the recyclerview in activity layout
        recyclerView = findViewById(R.id.recyclerView);

        // Initialize data
        cryptoList = new ArrayList<>();

        // Create adapter passing in the sample user data
        adapter = new CryptoListAdapter(cryptoList);

        // Attach the adapter to the recyclerview to populate items
        recyclerView.setAdapter(adapter);

        // Set layout manager to position the items
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        adapter.setClickListener(new CryptoListAdapter.ItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                Intent intent = new Intent(AppActivity.this, CoinPageActivity.class);
                intent.putExtra("coin", adapter.getItem(position));
                startActivity(intent);
            }
        });
    }

    private void getCoinList() {

        Call<CryptoList> call2 = apiInterface.doGetUserList("100");
        call2.enqueue(new Callback<CryptoList>() {
            @Override
            public void onResponse(Call<CryptoList> call, Response<CryptoList> response) {
                CryptoList list = response.body();

                // do not reinitialize an existing reference used by an adapter
                // add to the existing list
                cryptoList.clear();
                cryptoList.addAll(list.getData());

                adapter.notifyDataSetChanged();
                cargando.setVisibility(View.GONE);
            }

            @Override
            public void onFailure(Call<CryptoList> call, Throwable t) {
                Toasty.error(getApplicationContext(),R.string.error_internet,Toasty.LENGTH_LONG).show();
                //Log.d("XXXX", t.getLocalizedMessage());
                call.cancel();
            }
        });
    }

    public void showDialog(){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(R.string.titulo_logout);
        builder.setMessage(R.string.mensaje_logout);
        builder.setPositiveButton(R.string.si, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Toasty.success(getApplicationContext(),R.string.logout_ok,Toasty.LENGTH_SHORT).show();
                finish();
                FirebaseAuth.getInstance().signOut();
            }
        });
        builder.setNegativeButton(R.string.no, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });
        builder.show();
    }

    @Override
    public void onBackPressed() {
        //super.onBackPressed();
        if (registrado){
            showDialog();
        } else{
            finish();
        }

    }
}
