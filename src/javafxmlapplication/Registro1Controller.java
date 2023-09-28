/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/javafx/FXMLController.java to edit this template
 */
package javafxmlapplication;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import static javafx.application.Application.launch;
import javafx.beans.binding.Bindings;
import javafx.beans.binding.BooleanBinding;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import model.Club;
import model.ClubDAOException;

/**
 * FXML Controller class
 *
 * @author Usuario
 */
public class Registro1Controller implements Initializable {

    @FXML
    private VBox vboxazul;
    @FXML
    private Label MensajeErrorNombreDeUsuario;
    @FXML
    private Label MensajeErrorContraseña;
    @FXML
    private Label MensajeErrorRepetirContraseña;
    @FXML
    private TextField formNombreUsuario;
    @FXML
    private PasswordField formContraseña;
    @FXML
    private PasswordField formRepetirContraseña;
    @FXML
    private Button botonCancelar;
    @FXML
    private Button botonSiguiente;

    
    private BooleanProperty nomBien;
    private BooleanProperty conBien;
    private BooleanProperty repConBien;
    
    
    private String nom, con;
    
    
    private Stage stage;
    private Scene scene;
    private Parent root;
    
    
    
    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // TODO
        
        try{
            Club club = Club.getInstance();
            club.setInitialData();
        }
        catch(ClubDAOException e){}
        catch(IOException e2){}
        
        nom ="";
        con="";
        
        
        nomBien = new SimpleBooleanProperty();
        conBien = new SimpleBooleanProperty();
        repConBien = new SimpleBooleanProperty();
        
        nomBien.setValue(Boolean.FALSE);
        conBien.setValue(Boolean.FALSE);
        repConBien.setValue(Boolean.FALSE);
        
        BooleanBinding datosBien = Bindings.and(nomBien,conBien).and(repConBien);
        
        botonSiguiente.disableProperty().bind( Bindings.not(datosBien));
        
        
        formNombreUsuario.focusedProperty().addListener((observable, oldValue, newValue) -> {
            
                checkUser();
                formNombreUsuario.textProperty().addListener(new ChangeListener<String>() {
                    
                    @Override
                    public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
                        checkUserValid();
                        if(!checkUserLong(newValue)){
                            MensajeErrorNombreDeUsuario.setText("");
                            MensajeErrorNombreDeUsuario.setStyle("-fx-text-fill: red;"); 
                        }
                    }
                }); 
        });
        
        formContraseña.focusedProperty().addListener((observable, oldValue, newValue) -> {
            
                checkPass();
                formContraseña.textProperty().addListener(new ChangeListener<String>() {
                    
                    @Override
                    public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
                        checkPassValid();
                        checkRepPassLongValid();
                        if(!checkPassLong(newValue)){
                            MensajeErrorContraseña.setText("");
                            MensajeErrorContraseña.setStyle("-fx-text-fill: red;"); }
                    }
                }); 
        });
        formRepetirContraseña.focusedProperty().addListener((observable, oldValue, newValue) -> {
            
                checkRepPass();
                formRepetirContraseña.textProperty().addListener(new ChangeListener<String>() {
                    
                    @Override
                    public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
                        checkRepPassLongValid();
                        if(!checkRepPassLong(newValue)){
                            MensajeErrorRepetirContraseña.setText("");
                            MensajeErrorRepetirContraseña.setStyle("-fx-text-fill: red;"); }
                    }
                }); 
        });
    }    

    private boolean checkUser(){
        String nombre = formNombreUsuario.getText();
        if(nombre.length()<4&&(!nombre.equals(""))){
            MensajeErrorNombreDeUsuario.setText("Nombre demasiado corto");
            return false;
        }
        if(nombre.equals(nombre.toLowerCase())){
            MensajeErrorNombreDeUsuario.setText("El nombre debe contener al menos una letra mayúscula");
            return false;
        }
        MensajeErrorNombreDeUsuario.setText("");
        return true;
    }
    private boolean checkUserValid(){
        String nombre = formNombreUsuario.getText();
        if(nombre.length()>=4&&(!nombre.equals(nombre.toLowerCase())) && nombre.length()<41){
            nomBien.setValue(Boolean.TRUE);
            return true;
        }else{
            nomBien.setValue(Boolean.FALSE);
            return false;
        }
    }
    private boolean checkUserLong(String e){
        if(e.length()>=40){MensajeErrorNombreDeUsuario.setText("El nombre es demasiado largo");
        return true;
        }
        return false;
    }
    
    
    
    
    private boolean checkPass(){
        String nombre = formContraseña.getText();
        if(nombre.length()<6&&(!nombre.equals(""))){MensajeErrorContraseña.setText("Contraseña demasiado corta");
        return false;}
        if(!tieneNum(nombre)){
            MensajeErrorContraseña.setText("La contraseña debe contener al menos un numero");
            return false;
        }
        MensajeErrorContraseña.setText("");
        return true;
    }
    private boolean checkPassLong(String e){
        if(e.length()>=40){MensajeErrorContraseña.setText("La contraseña es demasiado larga");
        return true;
        }
        
        return false;
    }
    private boolean tieneNum(String t){
        for(int i =0; i<t.length();i++){
            char a = t.charAt(i);
            if(a>'0'&&a<'9'){
                return true;
            }
        }
        return false;
    }
    private boolean checkPassValid(){
        String nombre = formContraseña.getText();
        if(nombre.length()>=6&&tieneNum(nombre) && nombre.length()<41){
            conBien.setValue(Boolean.TRUE);
            return true;
        }else{
            conBien.setValue(Boolean.FALSE);
            return false;
        }
    }
    
    
    
    
    private boolean checkRepPass(){
        String nombre = formRepetirContraseña.getText();
        if(!nombre.equals(formContraseña.getText())){
            MensajeErrorRepetirContraseña.setText("Las contraseñas no coinciden");
            return false;
        }
        MensajeErrorRepetirContraseña.setText("");
        repConBien.setValue(Boolean.TRUE);
        return true;
    }
    private boolean checkRepPassLong(String e){
        
        if(e.length()>=20){MensajeErrorRepetirContraseña.setText("Demasiado larga");
        return true;
        }
        return false;
    }
    private boolean checkRepPassLongValid(){
        if(formRepetirContraseña.getText().equals(formContraseña.getText())){
            repConBien.setValue(Boolean.TRUE);
            return true;
        }else{repConBien.setValue(Boolean.FALSE);
        return false;}
    }
    
    
    
    @FXML
    private void cancelarReg(MouseEvent event) {
        try{
            irAInicio();
        }
        catch(IOException e){}
    }

    @FXML
    private void siguienteReg(MouseEvent event) {
        try{
            nom = formNombreUsuario.getText();
            con = formContraseña.getText();
            pasarVentana();
        }
        catch(IOException e){System.out.println("error al cargar segunda pantalla");}
    }
    public String getUserName(){
        return nom;
    }
    public String getPassword(){
        return con;
    }
    
    
    
    private void pasarVentana() throws IOException{
        root = FXMLLoader.load(getClass().getResource("Registro2.fxml"));
        stage = (Stage) ((Node)vboxazul).getScene().getWindow(); 
        scene = new Scene(root);
        stage.setScene(scene);
        stage.show();
    }
    private void irAInicio() throws IOException{
        root = FXMLLoader.load(getClass().getResource("Inicio.fxml"));
        stage = (Stage) ((Node)vboxazul).getScene().getWindow(); 
        scene = new Scene(root);
        stage.setScene(scene);
        stage.show();
    }
    
    
}
