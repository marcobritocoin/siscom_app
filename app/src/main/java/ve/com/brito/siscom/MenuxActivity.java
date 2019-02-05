package ve.com.brito.siscom;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
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
import android.support.v7.widget.CardView;
import android.view.View;
import android.widget.AutoCompleteTextView;
import android.widget.GridLayout;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

public class MenuxActivity extends AppCompatActivity {

    // Declaracion de Variables
    //-------------------------------------------------
    Location location;
    LocationManager locationManager;
    LocationListener locationListener;
    AlertDialog alert = null;
    TextView etiqueta_usuario;
    TextView cerrar_sesion, eVisita, eVenta, eConsigna;
    public String Nusuario;
    public int id_usuario;
    public int acceso;

    private static int PERMISSION_REQUEST_CODE=1;
    //private Location mLocation;
    private static final String[]INITIAL_PERMS ={
            Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION
    };

    private static double latitud_now;
    private static double longitud_now;
    public int vVisita, vVenta, vConsigna;
    //-------------------------------------------------

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menux);

        LinearLayout menu1 = (LinearLayout )findViewById(R.id.Visitas);
        LinearLayout menu3 = (LinearLayout )findViewById(R.id.Rutas);
        etiqueta_usuario = (TextView)findViewById(R.id.e_usuario);
        cerrar_sesion = (TextView)findViewById(R.id.cerrar_sesion);
        eVisita = (TextView)findViewById(R.id.cont_visitas);
        eVenta = (TextView)findViewById(R.id.cont_ventas);
        eConsigna = (TextView)findViewById(R.id.cont_consigna);

        SharedPreferences datos= PreferenceManager.getDefaultSharedPreferences(this);
        Nusuario = datos.getString("usuario", "comercial");
        id_usuario = datos.getInt("id_usuario",0);
        etiqueta_usuario.setText("Bienvenido, "+Nusuario+"   ");
        vVisita=datos.getInt("visita",0);
        vVenta=datos.getInt("venta",0);
        vConsigna=datos.getInt("consigna",0);
        eVisita.setText(""+vVisita);
        eVenta.setText(""+vVenta);
        eConsigna.setText(""+vConsigna);

        //  OBLIGAR A INICIAR SESION
        if(id_usuario==0) {
            Intent LanzarActividad = new Intent(getApplicationContext(), MainActivity.class);
            startActivity(LanzarActividad);
        }


        menu1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent picture_intent = new Intent(MenuxActivity.this,Main2Activity.class);
                startActivity(picture_intent);
                //Toast.makeText(MenuxActivity.this, "Visitas", Toast.LENGTH_SHORT).show();
            }
        });

        menu3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent picture_intent2 = new Intent(MenuxActivity.this,MapsActivity.class);
                startActivity(picture_intent2);
                //Toast.makeText(MenuxActivity.this, "Visitas", Toast.LENGTH_SHORT).show();
            }
        });

        cerrar_sesion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CerrarSesion();
            }
        });


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


        /**** Cconsulto si esta encendido el GPS sino lo mando a prender ****/

        if ( !locationManager.isProviderEnabled( LocationManager.GPS_PROVIDER ) ) {
            AlertNoGps();
        }


    } // Fin del onCreate

    /*
    * -----------------------------------------------------------------------
    *   CON ESTO GUARDO INFORMACION SI QUEDA EN MEMORIA SEGUNDO PLANO - BUNDLE
    * -----------------------------------------------------------------------
    * */

    /*
    public void onSaveInstanceState(Bundle estado){

        estado.putDouble("latitud", latitud_now);
        estado.putDouble("longitud", longitud_now);

        super.onSaveInstanceState(estado);
    }

    public void onRestoreInstanceState(Bundle estado){

        super.onRestoreInstanceState(estado);

        latitud_now=estado.getDouble("latitud");
        longitud_now=estado.getDouble("longitud");
    }
    */
    //----------------------------------------------------------------------


    /*
     * -----------------------------------------------------------------------
     *   CON ESTO GUARDO INFORMACION FISICA ASI CIERREN LA APLICACION - SHARED PREFEREN
     * -----------------------------------------------------------------------
     * */

    /*
    public void onPause(){
        super.onPause();
        SharedPreferences datos= PreferenceManager.getDefaultSharedPreferences(this);
        SharedPreferences.Editor miEditor=datos.edit();
        miEditor.putInt("id_usuario",id_usuario);
        miEditor.apply();
    }
    */


    public void onResume(){
        super.onResume();
        SharedPreferences datos= PreferenceManager.getDefaultSharedPreferences(this);
        Nusuario = datos.getString("usuario", "comercial");
        id_usuario = datos.getInt("id_usuario",0);
        etiqueta_usuario.setText("Bienvenido, "+Nusuario+"   ");
        vVisita=datos.getInt("visita",0);
        vVenta=datos.getInt("venta",0);
        vConsigna=datos.getInt("consigna",0);
        eVisita.setText(""+vVisita);
        eVenta.setText(""+vVenta);
        eConsigna.setText(""+vConsigna);
    }


    public void onSaveInstanceState(Bundle estado){

        //estado.putDouble("latitud", latitud_now);
        //estado.putDouble("longitud", longitud_now);

        super.onSaveInstanceState(estado);
    }

    public void onRestoreInstanceState(Bundle estado){

        super.onRestoreInstanceState(estado);

        //latitud_now=estado.getDouble("latitud");
        //longitud_now=estado.getDouble("longitud");
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

    private void SalirApp() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Intenta salir de SISCOM, ¿Esta Seguro?")
                .setCancelable(false)
                .setPositiveButton("Si", new DialogInterface.OnClickListener() {
                    public void onClick(@SuppressWarnings("unused") final DialogInterface dialog, @SuppressWarnings("unused") final int id) {
                        //startActivity(new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS));
                        moveTaskToBack(true);
                        finish();
                        System.exit(0);
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

    private void EliminarDatosSesion() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("¿Desea Cerrar Sesión?")
                .setCancelable(false)
                .setPositiveButton("Si", new DialogInterface.OnClickListener() {
                    public void onClick(@SuppressWarnings("unused") final DialogInterface dialog, @SuppressWarnings("unused") final int id) {

                        SharedPreferences datos= PreferenceManager.getDefaultSharedPreferences(MenuxActivity.this);
                        SharedPreferences.Editor miEditor=datos.edit();
                        miEditor.putInt("id_usuario",0);
                        miEditor.putString("usuario","");
                        miEditor.putInt("acceso",0);
                        miEditor.apply();


                        moveTaskToBack(true);
                        finish();
                        System.exit(0);


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

    public void CerrarSesion(){ // Elimino la Sesion
        EliminarDatosSesion();
    }

    public void onBackPressed(){ // sale por completo del APP
        //moveTaskToBack(true);
        SalirApp();
    }



}// fin de class Principal
