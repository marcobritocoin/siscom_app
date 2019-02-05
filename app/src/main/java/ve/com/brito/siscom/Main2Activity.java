package ve.com.brito.siscom;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.preference.PreferenceManager;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Spinner;
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

import java.io.IOException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Main2Activity extends AppCompatActivity implements Response.Listener<JSONObject>, Response.ErrorListener {

    // Declaracion de Variables
    //-------------------------------------------------

    Location location;
    LocationManager locationManager;
    LocationListener locationListener;
    AlertDialog alert = null;

    private static int PERMISSION_REQUEST_CODE=1;
    private static final String[]INITIAL_PERMS ={
            Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION
    };

    public static double latitud_now = 0;
    public static double longitud_now = 0;


    AutoCompleteTextView comercio, nombre_contacto, tlf_contacto, email_contacto, cantidad_pedido, observacion;
    RequestQueue rq;
    JsonRequest jrq;
    ProgressBar cargador;
    public static String direccion, latitud, longitud = " ";
    public static int id_usuario, id_linea_comercial, id_tipo_visita, id_tipo_comercio= 0;
    public int vVisita, vVenta, vConsigna;



    //Variables para cargar Spinner desde un Array
    Spinner spTipoComercio, spFinVisita, spLineaExpositor;
    String[] strTipoComercio, strFinVisita, strLineaExpositor;
    List<String> listaTipoComercio, listaFinVisita, listaLineaExpositor;
    ArrayAdapter<String> comboAdapter1, comboAdapter2, comboAdapter3;
    String TipoComercio, FinVisita, LineaExpositor;

    TextView tSpinner;


    //-------------------------------------------------



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);

        //================Datos cargados desde Array=====================//
        //Hago referencia al spinner con el id
        spTipoComercio = (Spinner) findViewById(R.id.tipo_comercio);
        spFinVisita = (Spinner) findViewById(R.id.fin_visita);
        spLineaExpositor = (Spinner) findViewById(R.id.expositor);

        tSpinner = (TextView)findViewById(R.id.textoSpinner);

        //Convierto la variable List<> en un ArrayList<>()
        listaTipoComercio = new ArrayList<>();
        listaFinVisita = new ArrayList<>();
        listaLineaExpositor = new ArrayList<>();
        //Arreglo con nombre
        strTipoComercio = new String[] {"Tipo de Comercio:", "Panaderia", "Fruteria", "Carniceria", "Charcuteria", "Pescaderia", "Supermercado", "Bodega", "Tienda Goumet", "Ultra Marino", "Herbolario", "Farrmacia", "Gimnasio", "Centro de Dietetica", "Restaurant", "Hotel", "Alimentos en general", "Tienda especializada para mascota", "Clinica Veterinaria", "Peluqueria Canina", "Estanco", "Ferreteria", "Electricidad", "Tienda de Moviles", "Accesorios y reparacion de moviles"};
        strFinVisita = new String[] {"Finalidad de la Visita:", "Visita", "Venta", "Colocación"};
        strLineaExpositor = new String[] {"Linea del Expositor:", "Dumts", "Vulpi", "Naransol", "Restartphone", "Vulpi, Naransol"};

        //Agrego las frutas del arreglo `strFrutas` a la listaFrutas
        Collections.addAll(listaTipoComercio, strTipoComercio);
        Collections.addAll(listaFinVisita, strFinVisita);
        Collections.addAll(listaLineaExpositor, strLineaExpositor);
        //Implemento el adapter con el contexto, layout, listaFrutas
        comboAdapter1 = new ArrayAdapter<>(this,R.layout.spinner_item_siscom, listaTipoComercio);
        comboAdapter2 = new ArrayAdapter<>(this,R.layout.spinner_item_siscom, listaFinVisita);
        comboAdapter3 = new ArrayAdapter<>(this,R.layout.spinner_item_siscom, listaLineaExpositor);
        //Cargo el spinner con los datos
        spTipoComercio.setAdapter(comboAdapter1);
        spFinVisita.setAdapter(comboAdapter2);
        spLineaExpositor.setAdapter(comboAdapter3);
        //Instancio la Clase que Controla los SPINNER o ComboBox
        spTipoComercio.setOnItemSelectedListener(new MyOnItemSelectedListener());
        spFinVisita.setOnItemSelectedListener(new MyOnItemSelectedListener());
        spLineaExpositor.setOnItemSelectedListener(new MyOnItemSelectedListener());

        // PRUEBA
        //spTipoComercio.setAdapter(new Main2Activity.MyCustomAdapter(Main2Activity.this, R.layout.spinner_item_siscom, strTipoComercio));


        Button btn_in2 = (Button)findViewById(R.id.btn_guardar);
        comercio = (AutoCompleteTextView)findViewById(R.id.cliente);
        nombre_contacto = (AutoCompleteTextView)findViewById(R.id.persona_contacto);
        tlf_contacto = (AutoCompleteTextView)findViewById(R.id.tlf);
        observacion = (AutoCompleteTextView)findViewById(R.id.observacion);
        email_contacto = (AutoCompleteTextView)findViewById(R.id.email);
        cantidad_pedido = (AutoCompleteTextView)findViewById(R.id.cant_pedido);

        cargador = (ProgressBar)findViewById(R.id.progreso2);
        rq = Volley.newRequestQueue(this);
        cargador.setVisibility(View.GONE); // Invisible el preloader

        // Extraigo de la sesion el ID del usuario almacenado localmente en XML
        SharedPreferences datos= PreferenceManager.getDefaultSharedPreferences(this);
        id_usuario=datos.getInt("id_usuario",0);
        vVisita=datos.getInt("visita",0);
        vVenta=datos.getInt("venta",0);
        vConsigna=datos.getInt("consigna",0);

        //  OBLIGAR A INICIAR SESION
        if(id_usuario==0) {
            Intent LanzarActividad = new Intent(getApplicationContext(), MainActivity.class);
            startActivity(LanzarActividad);
        }


        btn_in2.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) { // Boton y Validaciones de campos de texto

                ValidaForm();
            }
        });

        observacion.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if(actionId == EditorInfo.IME_ACTION_DONE){ // Acciona el evento

                    ValidaForm();
                    return true;
                }
                return false;
            }
        });

        //---------------------------------------------------------------------------------


        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);

        //------------------------------------------------------------------------------------------------
        //                  ACTIVO LOS PERMISOS PARA USO DEL GPS
        //----------------------------------------------------------------------------------------------
        if (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) ==
                PackageManager.PERMISSION_GRANTED &&
                checkSelfPermission(android.Manifest.permission.ACCESS_COARSE_LOCATION) ==
                        PackageManager.PERMISSION_GRANTED) {
            //Toast.makeText(MainActivity.this, "Enhorabuena tienes los permisos", Toast.LENGTH_LONG).show();
        } else {
            //Toast.makeText(MainActivity.this, "No tienes los permisos para acceder al GPS", Toast.LENGTH_LONG).show();
            requestPermissions(INITIAL_PERMS,PERMISSION_REQUEST_CODE);
        }
        //-------------------------------------------------------------------------------------------------

        // Define a listener that responds to location updates
        LocationListener locationListener = new LocationListener() {

            public void onLocationChanged(Location location) {
                // Se llama cuando el proveedor de ubicación de red encuentra una nueva ubicación.

                latitud_now=location.getLatitude();
                longitud_now=location.getLongitude();

                //Toast.makeText(Main2Activity.this, "Iniciado: Latitud: "+location.getLatitude()+" / Longitud: "+location.getLongitude()+" Comercio:"+comercio.getText().toString(), Toast.LENGTH_LONG).show();
            }

            public void onStatusChanged(String provider, int status, Bundle extras) {}

            public void onProviderEnabled(String provider) {
                Toast.makeText(Main2Activity.this, "GPS Activado!", Toast.LENGTH_LONG).show();
            }

            public void onProviderDisabled(String provider) {
                Toast.makeText(Main2Activity.this, "GPS Desactivado!", Toast.LENGTH_LONG).show();
                AlertNoGps();
            }
        };
        // Registre al oyente con el Administrador de ubicaciones para recibir actualizaciones de ubicación
        try {
            int permissionCheck = ContextCompat.checkSelfPermission(Main2Activity.this, Manifest.permission.ACCESS_FINE_LOCATION);
            locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, locationListener);
        }catch (Exception e){
            //miron.setText(""+e);
        }
    } // fin del onCreate

    //-------------------------------------------------------------------------------------------------------------

    @Override
    public void onErrorResponse(VolleyError error) {
        cargador.setVisibility(View.GONE);
        Toast.makeText(this,"Error registrando la visita ", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onResponse(JSONObject response) {
        JSONArray jsonArray = null;
        try {
            jsonArray = response.getJSONArray("datos");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        // llamamos la nueva activity y reseteamos
        cargador.setVisibility(View.GONE);
        comercio.setText("");
        nombre_contacto.setText("");
        tlf_contacto.setText("");
        observacion.setText("");

        vVisita++;

        if(id_tipo_visita==2){ // Venta++
            vVenta++;
        }else if(id_tipo_visita==3){ // Consigna++
            vConsigna++;
        }

        //-------------------------------------------------------------------------------------------
        // Almacenamos los datos del formulario
        //-------------------------------------------------------------------------------------------
        SharedPreferences datos= PreferenceManager.getDefaultSharedPreferences(this);
        SharedPreferences.Editor miEditor=datos.edit();
        miEditor.putInt("visita",vVisita);
        miEditor.putInt("venta",vVenta);
        miEditor.putInt("consigna",vConsigna);
        miEditor.apply();
        //--------------------------------------------------------------------------------------------
        Toast.makeText(Main2Activity.this, "Datos Guardados Exitosamente!", Toast.LENGTH_LONG).show();

        Intent LanzarActividad = new Intent(getApplicationContext(), MenuxActivity.class);
        startActivity(LanzarActividad);

    }


    private void registrarVisita(){
        cargador.setVisibility(View.VISIBLE);

        try{
            String nComercio = URLEncoder.encode(comercio.getText().toString(), "UTF-8");
            String nContacto = URLEncoder.encode(nombre_contacto.getText().toString(), "UTF-8");
            String tContacto = URLEncoder.encode(tlf_contacto.getText().toString(), "UTF-8");
            String eContacto = URLEncoder.encode(email_contacto.getText().toString(), "UTF-8");
            String Cpedido = URLEncoder.encode(cantidad_pedido.getText().toString(), "UTF-8");
            String Observacion = URLEncoder.encode(observacion.getText().toString(), "UTF-8");

            String url ="https://brito.com.ve/siscom_app/registrar_visita.php?id_usuario="+id_usuario+"&direccion="+direccion+"&latitud="+latitud_now+"&longitud="+longitud_now+"&id_linea_comercial="+id_linea_comercial+"&id_tipo_visita="+id_tipo_visita+"&cantidad_pedido="+Cpedido+"&observacion="+Observacion+"&id_tipo_comercio="+id_tipo_comercio+"&comercio="+nComercio+"&nombre_contacto="+nContacto+"&tlf_contacto="+tContacto+"&email_contacto="+eContacto;
            jrq = new JsonObjectRequest(Request.Method.GET, url, null, this, this);
            rq.add(jrq);
        }
        catch(IOException ioe){
            Toast.makeText(Main2Activity.this, "Error: "+ioe, Toast.LENGTH_LONG).show();
        }

    }

    private void AlertNoGps() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("El sistema GPS esta desactivado, ¿Desea activarlo?")
                .setCancelable(false)
                .setPositiveButton("Si", new DialogInterface.OnClickListener() {
                    public void onClick(@SuppressWarnings("unused") final DialogInterface dialog, @SuppressWarnings("unused") final int id) {
                        startActivity(new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS));
                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    public void onClick(final DialogInterface dialog, @SuppressWarnings("unused") final int id) {
                        dialog.cancel();
                    }
                });
        alert = builder.create();
        alert.show();
    }

    private void ValidaForm() {
        if(comercio.getText().toString().equals("")){
            comercio.requestFocus();
            Toast.makeText(Main2Activity.this, "Ingrese un Comercio ", Toast.LENGTH_LONG).show();
        }else if(nombre_contacto.getText().toString().equals("")){
            nombre_contacto.requestFocus();
            Toast.makeText(Main2Activity.this, "Ingrese un Nombre de Contacto ", Toast.LENGTH_LONG).show();
        }else if(tlf_contacto.getText().toString().equals("")){
            tlf_contacto.requestFocus();
            Toast.makeText(Main2Activity.this, "Ingrese un Telefono de Contacto ", Toast.LENGTH_LONG).show();
        }else if((latitud_now==0.0)&&(longitud_now==0.0)){
            Toast.makeText(Main2Activity.this, "Espere mientras geolocalizamos su posicion: "+latitud_now+" / "+longitud_now, Toast.LENGTH_LONG).show();
        }else if(id_tipo_comercio==0){
            Toast.makeText(Main2Activity.this, "Seleccione el Tipo de Comercio", Toast.LENGTH_LONG).show();
            spTipoComercio.requestFocus();
        }else if(id_tipo_visita==0){
            Toast.makeText(Main2Activity.this, "Seleccione el Fin de la Visita", Toast.LENGTH_LONG).show();
            spFinVisita.requestFocus();
        }else if(id_linea_comercial==0){
            Toast.makeText(Main2Activity.this, "Seleccione Linea Comercial del Expositor", Toast.LENGTH_LONG).show();
            spLineaExpositor.requestFocus();
        }else if((id_tipo_visita>1)&&(cantidad_pedido.getText().toString().equals(""))){
            Toast.makeText(Main2Activity.this, "Ingrese una Cantidad", Toast.LENGTH_LONG).show();
            cantidad_pedido.requestFocus();
        }else{
            registrarVisita();
        }
    }

    @Override
    protected void onDestroy(){
        super.onDestroy();
        if(alert != null)
        {
            alert.dismiss ();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Extraigo de la sesion el ID del usuario almacenado localmente en XML
        SharedPreferences datos= PreferenceManager.getDefaultSharedPreferences(this);
        id_usuario=datos.getInt("id_usuario",0);
        vVisita=datos.getInt("visita",0);
        vVenta=datos.getInt("venta",0);
        vConsigna=datos.getInt("consigna",0);
    }




    private class MyOnItemSelectedListener implements android.widget.AdapterView.OnItemSelectedListener {
        public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {

            switch (parent.getId()){
                case R.id.tipo_comercio:
                    //Almaceno el nombre del elemento seleccionado
                    TipoComercio = strTipoComercio[pos]; //Nombre
                    id_tipo_comercio = pos; // ID

                    //   CAMBIAR EL COLOR AL SELECCIONAR REVISAR
                    /*
                    try {
                       // tSpinner.setTextColor(R.color.colorAqua);
                        ((TextView) parent.getChildAt(0)).setTextColor(Color.BLUE);
                        ((TextView) parent.getChildAt(0)).setTextSize(15);
                    }catch (Exception e){
                        Toast.makeText(Main2Activity.this, "Error: " + e, Toast.LENGTH_SHORT).show();
                    }

                    */
                    /*
                    if(pos!=0){
                        Toast.makeText(Main2Activity.this, "Seleccionaste: " + TipoComercio + " | Id: "+pos, Toast.LENGTH_SHORT).show();
                    }
                    */
                    break;
                case R.id.fin_visita:
                    //Almaceno el nombre del elemento seleccionado
                    FinVisita = strFinVisita[pos]; //Nombre
                    id_tipo_visita = pos; // ID
                    //tSpinner.setTextColor(R.color.colorAqua);
                    /*
                    if(pos!=0){
                        Toast.makeText(Main2Activity.this, "Seleccionaste: " + FinVisita + " | Id: "+pos, Toast.LENGTH_SHORT).show();
                    }
                    */
                    break;
                case R.id.expositor:
                    //Almaceno el nombre del elemento seleccionado
                    LineaExpositor = strLineaExpositor[pos]; //Nombre
                    id_linea_comercial = pos; // ID
                    //tSpinner.setTextColor(R.color.colorAqua);
                    /*
                    if(pos!=0){
                        Toast.makeText(Main2Activity.this, "Seleccionaste: " + LineaExpositor + " | Id: "+pos, Toast.LENGTH_SHORT).show();
                    }
                    */
                    break;
            }

        }

        public void onNothingSelected(AdapterView parent) {
            // Do nothing.
        }
    }


//----------------------------------------------------------------------------------

/*
    public class MyCustomAdapter extends ArrayAdapter<String>{

        public MyCustomAdapter(Context context, int textViewResourceId,
                               String[] objects) {
            super(context, textViewResourceId, objects);
            // TODO Auto-generated constructor stub
        }

        @Override
        public View getDropDownView(int position, View convertView,
                                    ViewGroup parent) {
            // TODO Auto-generated method stub
            return getCustomView(position, convertView, parent);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            // TODO Auto-generated method stub
            return getCustomView(position, convertView, parent);
        }

        public View getCustomView(int position, View convertView, ViewGroup parent) {
            // TODO Auto-generated method stub
            //return super.getView(position, convertView, parent);

            LayoutInflater inflater=getLayoutInflater();
            View row=inflater.inflate(R.layout.spinner_item_siscom, parent, false);
            TextView label=(TextView)row.findViewById(R.id.weekofday);
            label.setText(strTipoComercio[position]);

            ImageView icon=(ImageView)row.findViewById(R.id.icon);

            if (strTipoComercio[position]=="Panaderia"){
                icon.setImageResource(R.drawable.tienda3);
            }
            else{
                icon.setImageResource(R.drawable.ic_store_mall_directory);
            }

            return row;
        }
    }
*/
    //--------------------------------------------------------------------------------------

} // Fin de la clase Principal
