package ve.com.brito.siscom;

public class Usuario {
    private String id_usuarios, usuario,pass,nombre_comp, acceso, zona, lineas_com;

    public String getId_usuarios() {
        return id_usuarios;
    }

    public void setId_usuarios(String Iduser) {
        this.id_usuarios = Iduser;
    }

    public String getUsuario() {
        return usuario;
    }

    public void setUsuario(String user) {
        this.usuario = user;
    }

    public String getPass() {
        return pass;
    }

    public void setPass(String pwd) {
        this.pass = pwd;
    }

    public String getNombre_comp() {
        return nombre_comp;
    }

    public void setNombre_comp(String names) {
        this.nombre_comp = names;
    }

    public String getAcceso() {
        return acceso;
    }

    public void setAcceso(String access) {
        this.acceso = access;
    }

    public String getZona() {
        return zona;
    }

    public void setZona(String zonete) {
        this.zona = zonete;
    }

    public String getLineas_com() {
        return lineas_com;
    }

    public void setLineas_com(String linete) {
        this.lineas_com = linete;
    }

}
