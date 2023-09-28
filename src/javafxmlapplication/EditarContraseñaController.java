/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/javafx/FXMLController.java to edit this template
 */
package javafxmlapplication;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
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
import model.Member;

/**
 * FXML Controller class
 *
 * @author Usuario
 */
public class EditarContraseñaController implements Initializable {

    
    private String con;
    
    @FXML
    private VBox vboxazul;
    @FXML
    private PasswordField formContraseña;
    @FXML
    private Label MensajeErrorContraseña;
    @FXML
    private PasswordField formRepetirContraseña;
    @FXML
    private Label MensajeErrorRepetirContraseña;
    @FXML
    private Button botonCancelar;
    @FXML
    private Button botonSiguiente;

    
    private BooleanProperty conBien;
    private BooleanProperty repConBien;    
    private BooleanProperty datosBien; 
    
    private Stage stage;
    private Scene scene;
    private Parent root;
    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // TODO
        con="";
        
        conBien = new SimpleBooleanProperty();
        repConBien = new SimpleBooleanProperty();
        
        conBien.setValue(Boolean.FALSE);
        repConBien.setValue(Boolean.FALSE);
        
        BooleanBinding datosBien = Bindings.and(repConBien,conBien);
        
        botonSiguiente.disableProperty().bind( Bindings.not(datosBien));
        
        
        
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
        con = formContraseña.getText();
        Club club;
        try {
            club = Club.getInstance();
            
            FXMLLoader loader = new FXMLLoader(getClass().getResource("Inicio.fxml"));
        
            try {
                Parent root = loader.load();
            } catch (IOException ex) {}
            Registro1Controller reg = loader.getController();
            String userName = reg.getUserName();
            String password = reg.getPassword();
        
            Member member = club.getMemberByCredentials(userName, password);
            member.setPassword(con);
            
        } catch (ClubDAOException ex) {
            System.out.println("Error");
        } catch (IOException ex) {
            System.out.println("Error");
        }
        
        
        
        
        try{
            irAInicio();
        }
        catch(IOException e){System.out.println("error al cargar segunda pantalla");}
    }
    
    private void irAInicio() throws IOException{
        root = FXMLLoader.load(getClass().getResource("Inicio.fxml"));
        stage = (Stage) ((Node)vboxazul).getScene().getWindow(); 
        scene = new Scene(root);
        stage.setScene(scene);
        stage.show();
    }
    
    
}

