package ve.com.brito.siscom;

import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.JsonRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class MainActivity extends AppCompatActivity implements Response.Listener<JSONObject>, Response.ErrorListener {

    //-------------------------------------------------------
    RequestQueue rq;
    JsonRequest jrq;
    AutoCompleteTextView cajaUser, cajaPwd;
    ProgressBar cargador;
    public int id_usuario;
    public String Nusuario;
    public int acceso;
    //-------------------------------------------------------


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTheme(R.style.AppTheme);
        setContentView(R.layout.activity_main);
        getWindow().setBackgroundDrawableResource(R.drawable.background);
        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        //-------------------------------------------------------------------------------------------
        Button btn_in = (Button)findViewById(R.id.btn_ingresar);
        cajaUser = (AutoCompleteTextView)findViewById(R.id.usuario);
        cajaPwd = (AutoCompleteTextView)findViewById(R.id.Clave);
        cargador = (ProgressBar)findViewById(R.id.progreso);
        rq = Volley.newRequestQueue(this);
        cargador.setVisibility(View.GONE); // Invisible el preloader de inicio de sesion

        SharedPreferences datos= PreferenceManager.getDefaultSharedPreferences(this);
        id_usuario=datos.getInt("id_usuario",0);

        //  PARA NO INICIAR SESION
        if(id_usuario!=0) {
            lanzaMenu();
        }


        btn_in.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) { // Boton y validaciones
                Validaciones();
            }
        });

        cajaPwd.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if(actionId == EditorInfo.IME_ACTION_DONE){ //do stuff
                    // Inicio la Sesion
                    Validaciones();

                    return true;
                }
                return false;
            }
        });

        //------------------- TAREAS PARA MAÑANA ----------
        //                  NO HAY POR LOS MOMENTOS
        //-------------------------------------------------


    }// FIN DE ONCREATE


    @Override
    public void onErrorResponse(VolleyError error) { // cuando devuelve error el servidor
        cargador.setVisibility(View.GONE);
        Toast.makeText(this,"No se ha encontrado el usuario "+cajaUser.getText().toString(), Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onResponse(JSONObject response) { // Cuando obtengo respuesta del servidor
        //Usuario usuario= new Usuario(); //Instancio la clase que me almacena las variables

        JSONArray jsonArray = null;
        try {
            jsonArray = response.getJSONArray("datos");
        } catch (JSONException e) {
            e.printStackTrace();
        }

        JSONObject jsonObject = null;
        try{
            jsonObject = jsonArray.getJSONObject(0);
            id_usuario=jsonObject.optInt("id_usuario");
            Nusuario=jsonObject.optString("usuario");
            acceso=jsonObject.optInt("acceso");
            /*
            usuario.setId_usuarios(jsonObject.optString("id_usuarios"));
            usuario.setUsuario(jsonObject.optString("usuario"));
            usuario.setPass(jsonObject.optString("pass"));
            usuario.setNombre_comp(jsonObject.optString("nombre_comp"));
            usuario.setAcceso(jsonObject.optString("acceso"));
            usuario.setZona(jsonObject.optString("zona"));
            usuario.setLineas_com(jsonObject.optString("lineas_com"));
            */
        }catch (JSONException e) {
            e.printStackTrace();
        }

        //-------------------------------------------------------------------------------------------
        // Almacenamos el ID_USUARIO para utilizarlo en los formularios y verificar la sesion
        //
        //                   INICIANDO SESION - Almacenando datos en XML (Eliminar al cerrar sesion)
        SharedPreferences datos= PreferenceManager.getDefaultSharedPreferences(this);
        SharedPreferences.Editor miEditor=datos.edit();
        miEditor.putInt("id_usuario",id_usuario);
        miEditor.putString("usuario",Nusuario);
        miEditor.putInt("acceso",acceso);
        miEditor.apply();
        //--------------------------------------------------------------------------------------------

        // llamamos la nueva activity y reseteamos
        cargador.setVisibility(View.GONE);
        cajaUser.setText("");
        cajaPwd.setText("");
        cajaUser.requestFocus();

        lanzaMenu();

    }

    private void lanzaMenu(){
        Intent LanzarActividad = new Intent(getApplicationContext(), MenuxActivity.class);
        startActivity(LanzarActividad);
    }

    private void iniciarSesion(){
        cargador.setVisibility(View.VISIBLE);
        //String url ="http://192.168.0.181/login_android_php/sesion.php?user="+cajaUser.getText().toString()+"&pwd="+cajaPwd.getText().toString();
        String url ="http://brito.com.ve/siscom_app/sesionremota.php?user="+cajaUser.getText().toString()+"&pwd="+cajaPwd.getText().toString();
        jrq = new JsonObjectRequest(Request.Method.GET, url, null, this, this);
        rq.add(jrq);
    }

    private void Validaciones(){
        // Inicio la Sesion
        if(cajaUser.getText().toString().equals("")){
            cajaUser.requestFocus();
            Toast.makeText(MainActivity.this, "Ingrese el Usuario ", Toast.LENGTH_LONG).show();
        }else if(cajaPwd.getText().toString().equals("")){
            cajaPwd.requestFocus();
            Toast.makeText(MainActivity.this, "Ingrese la Contraseña", Toast.LENGTH_LONG).show();
        }else{
            iniciarSesion();
        }
    }


}
