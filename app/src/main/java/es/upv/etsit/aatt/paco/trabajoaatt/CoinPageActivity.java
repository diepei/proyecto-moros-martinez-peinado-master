package es.upv.etsit.aatt.paco.trabajoaatt;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.jjoe64.graphview.DefaultLabelFormatter;
import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.GridLabelRenderer;
import com.jjoe64.graphview.helper.StaticLabelsFormatter;
import com.jjoe64.graphview.series.BarGraphSeries;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;

import es.dmoral.toasty.Toasty;
import es.upv.etsit.aatt.paco.trabajoaatt.retrofit.Datum;

import java.text.NumberFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

public class CoinPageActivity extends AppCompatActivity {

    FirebaseAuth firebaseAuth;
    TextView name, price, date, symbol, slug, date_add, market_pairs, cmc_rank, volume24h,
            circulating_supply, max_supply, total_supply, market_cap, change1h, change24h, change7d;

    boolean registrado = true;

    GraphView graph, graph2;
    LineGraphSeries<DataPoint> series;
    BarGraphSeries<DataPoint> series2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_coin_page);

        setTitle(R.string.titulo_activity_coin_page);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseAuth.addAuthStateListener(new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {

                if (firebaseAuth.getCurrentUser() != null){

                } else {
                    //cargar interfaz de usuario NO registrado
                    cargarInterfazNoRegistrado();
                    registrado = false;
                }
            }
        });

        Intent intent = getIntent();
        Datum datum = (Datum) intent.getSerializableExtra("coin");

        name = findViewById(R.id.name);
        price = findViewById(R.id.price);
        date = findViewById(R.id.date);

        symbol = findViewById(R.id.symbol);
        slug = findViewById(R.id.slug);
        date_add = findViewById(R.id.date_add);
        market_pairs = findViewById(R.id.market_pairs);
        cmc_rank = findViewById(R.id.cmc_rank);
        volume24h = findViewById(R.id.volume24h);
        circulating_supply = findViewById(R.id.circulating_supply);
        max_supply = findViewById(R.id.max_supply);
        total_supply = findViewById(R.id.total_supply);
        market_cap = findViewById(R.id.market_cap);
        change1h = findViewById(R.id.change1h);
        change24h = findViewById(R.id.change24h);
        change7d = findViewById(R.id.change7d);

        //Funcionalidades
        name.setText(datum.getName() + " (" + datum.getSymbol() + ")");
        price.setText(getResources().getString(R.string.precio) + String.format("%,f", datum.getQuote().getUSD().getPrice()));
        date.setText(getResources().getString(R.string.last_update) + " " + parseDateToddMMyyyy(datum.getLastUpdated()));

        symbol.setText(getResources().getString(R.string.simbolo) + " " + datum.getSymbol());
        slug.setText(getResources().getString(R.string.slug) + " " + datum.getSlug());
        date_add.setText(getResources().getString(R.string.init_date) + " " + parseDateToddMMyyyy(datum.getDateAdded()));
        market_pairs.setText(getResources().getString(R.string.pares_divisa) + " " + datum.getNumMarketPairs());
        cmc_rank.setText(getResources().getString(R.string.cmc) + " " + datum.getCmcRank());
        change1h.setText(String.format(getResources().getString(R.string.cambio_1h) + " " + "%.2f", datum.getQuote().getUSD().getPercentChange1h()) + "%");
        change24h.setText(String.format(getResources().getString(R.string.cambio_24h) + " " + "%.2f", datum.getQuote().getUSD().getPercentChange24h()) + "%");
        change7d.setText(String.format(getResources().getString(R.string.cambio_7d) + " " + "%.2f", datum.getQuote().getUSD().getPercentChange7d()) + "%");
        volume24h.setText(getResources().getString(R.string.volume24h) + String.format("%,d", Math.round(datum.getQuote().getUSD().getVolume24h())));
        circulating_supply.setText(getResources().getString(R.string.sum_circ) + " " + String.format("%.0f", datum.getCirculatingSupply()) + " " + datum.getSymbol());
        max_supply.setText(getResources().getString(R.string.sum_max) + " " + String.format("%.0f", datum.getMaxSupply()) + " " + datum.getSymbol());
        total_supply.setText(getResources().getString(R.string.sum_tot) + " " + String.format("%.0f", datum.getTotalSupply()) + " " + datum.getSymbol());
        market_cap.setText(getResources().getString(R.string.marketCap) + String.format("%,d", Math.round(datum.getQuote().getUSD().getMarketCap())));

        graph = (GraphView) findViewById(R.id.graph);
        series = new LineGraphSeries<DataPoint>();

        series.appendData(new DataPoint(1, datum.getQuote().getUSD().getPercentChange1h()), true, 3);
        series.appendData(new DataPoint(2, datum.getQuote().getUSD().getPercentChange24h()), true, 3);
        series.appendData(new DataPoint(3, datum.getQuote().getUSD().getPercentChange7d()), true, 3);

        graph.addSeries(series);
        graph.setTitle(getResources().getString(R.string.var_graph) + " " + datum.getName() + " (" + datum.getSymbol() + ")");
        graph.setTitleColor(getColor(R.color.colorSecondaryText));

        GridLabelRenderer gridLabel = graph.getGridLabelRenderer();
        gridLabel.setNumHorizontalLabels(3);

        StaticLabelsFormatter staticLabelsFormatter = new StaticLabelsFormatter(graph);
        staticLabelsFormatter.setHorizontalLabels(new String[] {"1h", "24h", "7d"});
        graph.getGridLabelRenderer().setLabelFormatter(staticLabelsFormatter);

        graph2 = (GraphView) findViewById(R.id.graph2);
        series2 = new BarGraphSeries<DataPoint>();

        if (datum.getMaxSupply()!=null){

            series2.appendData(new DataPoint(1, datum.getCirculatingSupply()), true, 3);
            series2.appendData(new DataPoint(2, datum.getTotalSupply()), true, 3);
            series2.appendData(new DataPoint(3, datum.getMaxSupply()), true, 3);

            graph2.addSeries(series2);
            graph2.setTitle(getResources().getString(R.string.sum_graph) + " " + datum.getName() + " (" + datum.getSymbol() + ")");
            graph2.setTitleColor(getColor(R.color.colorSecondaryText));
            series2.setSpacing(50);

            GridLabelRenderer gridLabel2 = graph2.getGridLabelRenderer();
            gridLabel2.setNumHorizontalLabels(3);

            StaticLabelsFormatter staticLabelsFormatter2 = new StaticLabelsFormatter(graph2);
            staticLabelsFormatter2.setHorizontalLabels(new String[] {"Circ", "Total", "Max"});
            graph2.getGridLabelRenderer().setLabelFormatter(staticLabelsFormatter2);
        } else{

            series2.appendData(new DataPoint(1, datum.getCirculatingSupply()), true, 3);
            series2.appendData(new DataPoint(2, datum.getTotalSupply()), true, 3);

            graph2.addSeries(series2);
            graph2.setTitle(getResources().getString(R.string.sum_graph) + " " + datum.getName() + " (" + datum.getSymbol() + ")");
            graph2.setTitleColor(getColor(R.color.colorSecondaryText));
            series2.setSpacing(25);

            GridLabelRenderer gridLabel2 = graph2.getGridLabelRenderer();
            gridLabel2.setNumHorizontalLabels(2);

            StaticLabelsFormatter staticLabelsFormatter2 = new StaticLabelsFormatter(graph2);
            staticLabelsFormatter2.setHorizontalLabels(new String[] {"Circ", "Total"});
            graph2.getGridLabelRenderer().setLabelFormatter(staticLabelsFormatter2);

        }

        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (!registrado){
                    Toasty.info(getApplicationContext(), R.string.registrate, Toasty.LENGTH_LONG).show();
                }
            }
        }, 100);

    }

    private String parseDateToddMMyyyy(String time) {
        //Analizar el timestamp del servidor. Asegurese que este en zona horaria UTC segun las especificaciones de la API.
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault());
        sdf.setTimeZone(TimeZone.getTimeZone("UTC"));

        //formatear la marca de tiempo del servidor UTC a la zona horaria local.
        SimpleDateFormat output = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss", Locale.getDefault());
        output.setTimeZone(TimeZone.getDefault());

        Date date = null;
        try {
            date = sdf.parse(time);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return output.format(date);
    }

    public void cargarInterfazNoRegistrado(){

        date_add.setVisibility(View.GONE);
        circulating_supply.setVisibility(View.GONE);
        max_supply.setVisibility(View.GONE);
        total_supply.setVisibility(View.GONE);
        market_cap.setVisibility(View.GONE);
        market_pairs.setVisibility(View.GONE);
        cmc_rank.setVisibility(View.GONE);
        graph.setVisibility(View.GONE);
        graph2.setVisibility(View.GONE);
    }
}