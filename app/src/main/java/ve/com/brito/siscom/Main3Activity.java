package ve.com.brito.siscom;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

public class Main3Activity extends AppCompatActivity {

    public static final String nombres="nombre_comp";
    TextView cajaBienvenido;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main3);

        cajaBienvenido=(TextView)findViewById(R.id.notaUsuario);
        String usuario = getIntent().getStringExtra("nombre_comp");
        cajaBienvenido.setText("¡Bienvenido "+usuario+" !");
    }
}
