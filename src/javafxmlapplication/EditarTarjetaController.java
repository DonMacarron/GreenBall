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
import model.Member;

/**
 * FXML Controller class
 *
 * @author Usuario
 */
public class EditarTarjetaController implements Initializable {

    @FXML
    private VBox vboxazul;
    
    
    
    String userName;
    String password;
    String tarjeta,svc;
    
    private BooleanProperty nomBien;
    private BooleanProperty apellidosBien;
    private BooleanProperty telefonoBien;
    private BooleanProperty tarjetaBien;
    private BooleanProperty svcBien;
    @FXML
    private TextField formTarjeta;
    @FXML
    private TextField formSvc;
    @FXML
    private Button botonSiguiente;

    @FXML
    private Label MensajeErrorTarjeta;
    @FXML
    private Label MensajeErrorSvc;
    
    private Stage stage;
    private Scene scene;
    private Parent root;

    @FXML
    private Button botonCancelar;

    
    /**
     * Initializes the controller class.
     *
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        Club club;
        try {
            club = Club.getInstance();
            
            FXMLLoader loader = new FXMLLoader(getClass().getResource("Registro1.fxml"));
        
            try {
               Parent root = loader.load();
            } catch (IOException ex) {}
            Registro1Controller reg = loader.getController();
            userName = reg.getUserName();
            password = reg.getPassword();
            
            Member member = club.getMemberByCredentials(userName, password);
            tarjeta = member.getCreditCard();
            formTarjeta.setText(tarjeta);
            svc = member.getSvc()+"";
            formSvc.setText(svc);
            
        } catch (ClubDAOException ex) {
            System.out.println("Error");
        } catch (IOException ex) {
            System.out.println("Error");
        }
        FXMLLoader loader = new FXMLLoader(getClass().getResource("Registro1.fxml"));
        
        try {
            Parent root = loader.load();
        } catch (IOException ex) {
        }
        Registro1Controller reg = loader.getController();
        userName = reg.getUserName();
        password = reg.getPassword();
        
        
        tarjetaBien = new SimpleBooleanProperty();
        svcBien = new SimpleBooleanProperty();
        
        tarjetaBien.setValue(Boolean.TRUE);
        svcBien.setValue(Boolean.TRUE);
        
        
        
        BooleanBinding datosBien = Bindings.and(tarjetaBien,svcBien);
        
        botonSiguiente.disableProperty().bind( Bindings.not(datosBien));
        
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
            pasarVentana();
        }
        catch(IOException e){}
    }

    @FXML
    private void siguienteReg(MouseEvent event)  throws ClubDAOException{
        try{
            tarjeta = formTarjeta.getText();
            svc = formSvc.getText();            
            if(tarjeta == null){tarjeta ="";}
            if(svc == null){svc ="";}
            
            Club club = Club.getInstance();
            Member m = club.getMemberByCredentials(userName, password);
            m.setCreditCard(tarjeta);
            m.setSvc(Integer.parseInt(svc));
            
            pasarVentana();
        }
        catch(IOException e){System.out.println("error al cargar segunda pantalla");}
    }
     
    private void pasarVentana() throws IOException{
        root = FXMLLoader.load(getClass().getResource("EditarCuenta.fxml"));
        stage = (Stage) ((Node)vboxazul).getScene().getWindow(); 
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

    

