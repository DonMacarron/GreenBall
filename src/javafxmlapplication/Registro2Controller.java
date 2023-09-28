/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/javafx/FXMLController.java to edit this template
 */
package javafxmlapplication;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import javafx.scene.image.Image;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.beans.binding.Bindings;
import javafx.beans.binding.BooleanBinding;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.ComboBoxListCell;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Border;
import javafx.scene.layout.BorderStroke;
import javafx.scene.layout.BorderStrokeStyle;
import javafx.scene.layout.BorderWidths;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import model.Club;
import model.ClubDAOException;

/**
 * FXML Controller class
 *
 * @author Usuario
 */
public class Registro2Controller implements Initializable {

    @FXML
    private VBox vboxazul;
    
    
    
    String userName;
    String password;
    String nombre,apellidos,telefono,tarjeta,svc;
    Image imagen;
    
    private BooleanProperty nomBien;
    private BooleanProperty apellidosBien;
    private BooleanProperty telefonoBien;
    private BooleanProperty tarjetaBien;
    private BooleanProperty svcBien;
    @FXML
    private TextField formNombre;
    @FXML
    private TextField formApellidos;
    @FXML
    private TextField formTelefono;
    @FXML
    private TextField formTarjeta;
    @FXML
    private TextField formSvc;
    @FXML
    private Button botonSiguiente;
    @FXML
    private Label MensajeErrorNombre;
    @FXML
    private Label MensajeErrorApellidos;
    @FXML
    private Label MensajeErrorTelefono;
    @FXML
    private Label MensajeErrorTarjeta;
    @FXML
    private Label MensajeErrorSvc;
    
    private Stage stage;
    private Scene scene;
    private Parent root;
    @FXML
    private ImageView fotoPerfil;
    @FXML
    private Button botonCancelar;
    @FXML
    private ComboBox<String> combo;
    @FXML
    private VBox boxImagen;
    
    /**
     * Initializes the controller class.
     *
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        userName = "";
        password = "";
        nombre ="";
        telefono="";
        tarjeta="";
        svc="";
        imagen =null;
        
        
        

        
        ArrayList<String> l = new ArrayList<String>();
        ObservableList<String> lista = FXCollections.observableList(l);
        lista.add("/resources/avatars/men.png");
        lista.add("/resources/avatars/men2.png");
        lista.add("/resources/avatars/men3.png");
        lista.add("/resources/avatars/men4.png");
        lista.add("/resources/avatars/woman.png");
        lista.add("/resources/avatars/woman2.png");
        lista.add("/resources/avatars/woman3.png");
        lista.add("/resources/avatars/woman4.png");

        
        
        
        combo.setCellFactory((c)-> { return new LenguajeListCell();});
        combo.setItems(lista);
        
        
        FXMLLoader loader = new FXMLLoader(getClass().getResource("Registro1.fxml"));
        
        try {
            Parent root = loader.load();
        } catch (IOException ex) {
        }
        Registro1Controller reg = loader.getController();
        userName = reg.getUserName();
        password = reg.getPassword();
        
        
        
        
        
        
        nomBien = new SimpleBooleanProperty();
        apellidosBien = new SimpleBooleanProperty();
        telefonoBien = new SimpleBooleanProperty();
        tarjetaBien = new SimpleBooleanProperty();
        svcBien = new SimpleBooleanProperty();
        
        nomBien.setValue(Boolean.FALSE);
        apellidosBien.setValue(Boolean.FALSE);
        telefonoBien.setValue(Boolean.FALSE);
        tarjetaBien.setValue(Boolean.TRUE);
        svcBien.setValue(Boolean.TRUE);
        
        
        
        BooleanBinding datosBien = Bindings.and(nomBien,apellidosBien).and(telefonoBien).and(tarjetaBien).and(svcBien);
        
        botonSiguiente.disableProperty().bind( Bindings.not(datosBien));
        
        
        formNombre.focusedProperty().addListener((observable, oldValue, newValue) -> {
            
                checkUser();
                formNombre.textProperty().addListener(new ChangeListener<String>() {
                    
                    @Override
                    public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
                        checkUserValid();
                        if(!checkUserLong(newValue)){
                            MensajeErrorNombre.setText("");
                            MensajeErrorNombre.setStyle("-fx-text-fill: red;"); 
                        }
                    }
                }); 
        });
        
        formApellidos.focusedProperty().addListener((observable, oldValue, newValue) -> {
            
                checkApellidos();
                formApellidos.textProperty().addListener(new ChangeListener<String>() {
                    
                    @Override
                    public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
                        checkApellidosValid();
                        if(!checkApellidosLong(newValue)){
                            MensajeErrorApellidos.setText("");
                            MensajeErrorApellidos.setStyle("-fx-text-fill: red;"); 
                        }
                    }
                }); 
        });
        formTelefono.focusedProperty().addListener((observable, oldValue, newValue) -> {
            
                checkTelefono();
                formTelefono.textProperty().addListener(new ChangeListener<String>() {
                    
                    @Override
                    public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
                        checkTelefonoValid();
                        if(!checkTelefonoLong(newValue)){
                            MensajeErrorTelefono.setText("");
                            MensajeErrorTelefono.setStyle("-fx-text-fill: red;"); }
                    }
                }); 
        });
        formTarjeta.focusedProperty().addListener((observable, oldValue, newValue) -> {
            
                checkTarjeta();
                formTarjeta.textProperty().addListener(new ChangeListener<String>() {
                    
                    @Override
                    public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
                        checkTarjetaValid();
                        checkSvcValid();
                        if(!checkTarjetaLong(newValue)){
                            MensajeErrorTarjeta.setText("");
                            MensajeErrorTarjeta.setStyle("-fx-text-fill: red;"); }
                    }
                }); 
        });
        formSvc.focusedProperty().addListener((observable, oldValue, newValue) -> {
                checkSvc();
                formSvc.textProperty().addListener(new ChangeListener<String>() {
                    @Override
                    public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
                        checkSvcValid();
                        checkTarjetaValid();
                        if(!checkSvcLong(newValue)){
                            MensajeErrorSvc.setText("");
                            MensajeErrorSvc.setStyle("-fx-text-fill: red;"); }
                        }
                    });
                         
        });
        combo.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
                // Actualiza el ImageView con la imagen seleccionada
                fotoPerfil.setImage(new Image(newValue));
                combo.setButtonCell(new ListCell<String>() {
                    @Override
                    protected void updateItem(String item, boolean empty) {
                        super.updateItem(item, empty);
                        // Establece el texto y el gráfico en null para que no se muestre nada
                        setText("Cambiar foto de perfil");
                        setGraphic(null);
                        boxImagen.setBorder(new Border(new BorderStroke(Color.BLACK, BorderStrokeStyle.SOLID, CornerRadii.EMPTY, BorderWidths.DEFAULT)));
                    }
                });
            }
        });
    }
    
    
        private boolean checkUser(){
            if(formNombre.getText().length()<=0){
                MensajeErrorNombre.setText("Introduce tu nombre");
                return false;
            }
            MensajeErrorNombre.setText("");
            return true;
        }
        private boolean checkUserValid(){
            String nombre = formNombre.getText();
            if(nombre.length()>60||nombre.length()<=0){
                nomBien.setValue(Boolean.FALSE);
            }
            nomBien.setValue(Boolean.TRUE);
            return true;
        }
        private boolean checkUserLong(String e){
            if(e.length()>60){
                MensajeErrorNombre.setText("Introduce tu nombre de forma reducida");
                return true;}
            return false;
        }
        
        
        
        
        
        
        private boolean checkApellidos(){
            if(formApellidos.getText().length()<=0){
                MensajeErrorApellidos.setText("Introduce tu nombre");
                return false;
            }
            MensajeErrorApellidos.setText("");
            return true;
        }
        private boolean checkApellidosValid(){
            String nombre = formApellidos.getText();
            if(nombre.length()>60||nombre.length()<=0){
                apellidosBien.setValue(Boolean.FALSE);
            }
            apellidosBien.setValue(Boolean.TRUE);
            return true;
        }
        private boolean checkApellidosLong(String e){
            if(e.length()>60){
                MensajeErrorApellidos.setText("Introduce tu nombre de forma reducida");
                return true;}
            return false;
        }
        
        
        
        
        
        private boolean checkTelefono(){
            if(!(formTelefono.getText().length()==9&&todoNumeros(formTelefono.getText()))){
                MensajeErrorTelefono.setText("Introduce tu telefono de 9 digitos numéricos");
                return false;
            }
            MensajeErrorTelefono.setText("");
            return true;
        }
        private boolean checkTelefonoLong(String e){
            if(e.length()>9){
                MensajeErrorTelefono.setText("El teléfono debe tener 9 digitos");
                return true;}
            return false;
        }
        private boolean checkTelefonoValid(){
            if((formTelefono.getText().length()==9)&&todoNumeros(formTelefono.getText())){
                telefonoBien.setValue(Boolean.TRUE);
                return false;
            }
            telefonoBien.setValue(Boolean.FALSE);
            return true;
        }
        
        
        
        private boolean checkTarjeta(){
            if(!((formTarjeta.getText().length()==16 && todoNumeros(formTarjeta.getText()))||(formTarjeta.getText().length()==0))){
                MensajeErrorTarjeta.setText("Introduce tu tarjeta de 16 dígitos numéricos");
                return false;
            }
            MensajeErrorTarjeta.setText("");
            return true;
        }
        private boolean checkTarjetaLong(String e){
            if(e.length()>16){
                MensajeErrorTarjeta.setText("El número de tarjeta debe tener 16 digitos");
                return true;}
            return false;
        }
        private boolean checkTarjetaValid(){
            if(((formTarjeta.getText().length()==16)&& todoNumeros(formTarjeta.getText()))||((formTarjeta.getText().length()==0)&&formSvc.getText().equals(""))){
                tarjetaBien.setValue(Boolean.TRUE);
                return false;
            }
            tarjetaBien.setValue(Boolean.FALSE);
            return true;
        }
        private boolean todoNumeros(String e){
            for(int i = 0; i<e.length();i++){
                if(e.charAt(i)<'0' || e.charAt(i)>'9'){return false;}
            }
            return true;
        }
        
        
        
        private boolean checkSvc(){
            if(!((formSvc.getText().length()==3 && todoNumeros(formSvc.getText()))||(formSvc.getText().equals("")))){
                MensajeErrorSvc.setText("Introduce tu svc de 3 dígitos numéricos");
                return false;
            }
            MensajeErrorSvc.setText("");
            return true;
        }
        private boolean checkSvcLong(String e){
            if(e.length()>3){
                MensajeErrorSvc.setText("El número del svc debe tener 3 digitos");
                return true;}
            return false;
        }
        private boolean checkSvcValid(){
            if(((formSvc.getText().length()==3)&&todoNumeros(formSvc.getText()))||(formSvc.getText().equals("")&&formTarjeta.getText().length()==0)){
                svcBien.setValue(Boolean.TRUE);
                return false;
            }
            svcBien.setValue(Boolean.FALSE);
            return true;
        }
        
        
        
    @FXML
        private void cancelarReg(MouseEvent event) {
        try{
            irAInicio();
        }
        catch(IOException e){}
    }

    @FXML
    private void siguienteReg(MouseEvent event)  throws ClubDAOException{
        try{
            nombre = formNombre.getText();
            apellidos = formApellidos.getText();
            telefono = formTelefono.getText();
            tarjeta = formTarjeta.getText();
            svc = formSvc.getText();
            imagen = fotoPerfil.getImage();
            
            if(tarjeta == null){tarjeta ="";}
            if(svc == null){svc ="";}
            
            Club club = Club.getInstance();
            club.registerMember(nombre, apellidos, telefono, userName, password, telefono, Integer.parseInt(svc), imagen);
            
            pasarVentana();
        }
        catch(IOException e){System.out.println("error al cargar segunda pantalla");}
    }
     
    private void pasarVentana() throws IOException{
        root = FXMLLoader.load(getClass().getResource("Registro3.fxml"));
        stage = (Stage) ((Node)vboxazul).getScene().getWindow(); 
        scene = new Scene(root);
        stage.setScene(scene);
        stage.show();
    }
    private void irAInicio() throws IOException{
        root = FXMLLoader.load(getClass().getResource("Registro1.fxml"));
        stage = (Stage) ((Node)formNombre).getScene().getWindow(); 
        scene = new Scene(root);
        stage.setScene(scene);
        stage.show();
    }
    
    
    
    
    
        
}    


class LenguajeListCell extends ComboBoxListCell<String>
{
private ImageView view = new ImageView();
private Image imagen;
@Override
public void updateItem(String image, boolean empty)
{ super.updateItem(image, empty);
if (image==null || empty) {
setText("");
setGraphic(null);}
 else {
    imagen = new Image(image,90,90,true,true);
view.setImage(imagen);
setGraphic(view);}
setText("");
}
}

    

