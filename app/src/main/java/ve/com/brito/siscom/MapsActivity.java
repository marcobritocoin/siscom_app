package ve.com.brito.siscom;

import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Location;
import android.location.LocationListener;
import android.preference.PreferenceManager;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;


public class MapsActivity extends FragmentActivity implements OnMapReadyCallback,LocationListener,GoogleMap.OnMarkerClickListener {

    private GoogleMap mMap;
    public String URL;
    private JSONArray result;
    public int id_usuario;
    public String Nusuario;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        // Extraigo de la sesion el ID del usuario almacenado localmente en XML
        SharedPreferences datos= PreferenceManager.getDefaultSharedPreferences(this);
        id_usuario=datos.getInt("id_usuario",0);
        Nusuario = datos.getString("usuario", "comercial");

        //  OBLIGAR A INICIAR SESION
        if(id_usuario==0) {
            Intent LanzarActividad = new Intent(getApplicationContext(), MainActivity.class);
            startActivity(LanzarActividad);
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        //URL="http://brito.com.ve/siscom_app/marcadores_gps.php?id_usuario="+id_usuario; // Filtrar por usuario


        // Agregando marcadores en un lugar y moviendo la camara
        LatLng punto_inicial = new LatLng(40.419100, -3.674106); // latitud y longitud
        mMap.addMarker(new MarkerOptions()
                .position(punto_inicial)
                .title("Fruteria mi SUKU SUKU")
                .icon(BitmapDescriptorFactory.fromResource(R.drawable.tienda3))
        );

        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(punto_inicial, 15));



        /*
        RequestQueue requestQueue= Volley.newRequestQueue(getApplicationContext());
        StringRequest stringRequest=new StringRequest(Request.Method.GET, URL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.d("JSONResult" , response.toString());
                JSONObject j = null;
                try{
                    j =new JSONObject(response);
                    result = j.getJSONArray("FL");
                    int punto_inicial = 0;
                    String lat_inicial = "0";
                    String long_inicial = "0";
                    for(int i=0;i<result.length();i++){
                        JSONObject jsonObject1=result.getJSONObject(i);
                        String lat_i = jsonObject1.getString("latitud");
                        String long_i = jsonObject1.getString("longitud");
                        String tienda = jsonObject1.getString("descripcion"); //cliente
                        String fecha = jsonObject1.getString("fecha"); //Fecha Hora de Visita
                        String n_usuario = jsonObject1.getString("usuario"); //Fecha Hora de Visita


                        if(punto_inicial==0){ // Posicion Inicial de la Camara en el ultimo Registro
                            lat_inicial = lat_i;
                            long_inicial = long_i;
                            punto_inicial++;
                        }


                        mMap.addMarker(new MarkerOptions()
                                .position(new LatLng(Double.parseDouble(lat_i) , Double.parseDouble(long_i)))
                                //.title(Double.valueOf(lat_i).toString() + "," + Double.valueOf(long_i).toString())
                                //.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ROSE))
                                .title(tienda) // titulo
                                .snippet(n_usuario+": "+fecha) // contenido
                                .icon(BitmapDescriptorFactory.fromResource(R.drawable.tienda3))

                        );

                       // mMap.getUiSettings().setZoomControlsEnabled(true);
                        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(Double.parseDouble(lat_inicial) , Double.parseDouble(long_inicial)), 13));


                        Toast.makeText(MapsActivity.this, "Lat: "+Double.parseDouble(lat_i)+", Long: "+Double.parseDouble(long_i), Toast.LENGTH_LONG).show();
                    }

                }catch (NullPointerException e){
                    e.printStackTrace();
                    Toast.makeText(MapsActivity.this, "1 - "+e.getMessage(), Toast.LENGTH_LONG).show();

                }

                catch (JSONException e){ // Si devuelve el resultado vacio / cuando consultas por primera vez
                    e.printStackTrace();
                    //Toast.makeText(MapsActivity.this, "2 - "+e.getMessage(), Toast.LENGTH_LONG).show();
                    Toast.makeText(MapsActivity.this, "Necesitas Registrar una Visita", Toast.LENGTH_LONG).show();
                    finish();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
                Toast.makeText(MapsActivity.this, "3 - "+error.getMessage(), Toast.LENGTH_LONG).show();
            }
        });


        int socketTimeout = 10000;
        RetryPolicy policy = new DefaultRetryPolicy(socketTimeout, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
        stringRequest.setRetryPolicy(policy);
        requestQueue.add(stringRequest);
        */

    }


    @Override
    public void onLocationChanged(Location location) {

    }

    @Override
    public void onStatusChanged(String s, int i, Bundle bundle) {

    }

    @Override
    public void onProviderEnabled(String s) {

    }

    @Override
    public void onProviderDisabled(String s) {

    }

    @Override
    public boolean onMarkerClick(Marker marker) {
        return false;
    }
} // Fin de la clase principal

