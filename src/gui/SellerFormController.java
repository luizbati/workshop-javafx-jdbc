package gui;

import java.net.URL;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.Set;

import db.DbException;
import gui.listeners.DataChangeListener;
import gui.util.Alerts;
import gui.util.Constraints;
import gui.util.Utils;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import model.entities.Seller;
import model.exception.ValidationException;
import model.services.SellerService;

public class SellerFormController implements Initializable{
	
	private Seller entity;
	
	private SellerService service;
	
	private List<DataChangeListener> dataChangeListener = new ArrayList<>();
	

	@FXML private TextField txtId;
	@FXML private TextField txtName;
	@FXML private TextField txtEmail;
	@FXML private DatePicker dpBirthDate;
	@FXML private TextField txtBaseSalary;
	
	@FXML Label labelErrorName;
	@FXML Label labelErrorEmail;
	@FXML Label labelErrorBirthDate;
	@FXML Label labelErrorBaseSalary;
	@FXML private Button btnSave;
	@FXML private Button btnCancel;

	private ResourceBundle errors;
	
	public void setSeller(Seller entity) {
		this.entity = entity;
	}
	
	public void setSellerService (SellerService service) {
		this.service = service;
	}
	
	public void subscribeDataChangeListener(DataChangeListener listener) {
		dataChangeListener.add(listener);
	}
	
	
	@FXML private void onBtnSaveAction(ActionEvent event) {
		if(service == null){
		throw new IllegalStateException("Service was null");
		}
		
		if (service == null) {
			throw new IllegalStateException("Service was null");
		}
		
		
		
		try {
		entity = getFormData();
		service.saveOrUpdate(entity);
		notifyDataChangeListeners();
		Utils.currentStage(event).close();
			
		}
		catch(ValidationException e) {
			setErrorMessage(e.getErrors());
		}
		catch
			(DbException e){
				Alerts.showAlert("Error saving object", null, e.getMessage(),AlertType.ERROR);
			}
			}
	
		private void notifyDataChangeListeners() {
		for (DataChangeListener listener : dataChangeListener) {
			listener.ondataChanged();
		}
		
	}

		private Seller getFormData() {
		Seller obj = new Seller();
		
		obj.setId(Utils.tryParseToInt(txtId.getText()));
		obj.setName(txtName.getText());
		return obj;
		
	}

	@FXML public void onBtnCancelaction(ActionEvent event) {
		Utils.currentStage(event).close();
	}
	
	
	
	@Override
	public void initialize(URL url, ResourceBundle rb) {
		initializeNodes();
		
	}
	
	private void initializeNodes() {
		Constraints.setTextFieldInteger(txtId);
		Constraints.setTextFieldMaxLength(txtName, 70);
		Constraints.setTextFieldDouble(txtBaseSalary);
		Constraints.setTextFieldMaxLength(txtEmail, 60);
		Utils.formatDatePicker(dpBirthDate, "dd/MM/yyyy");
	}
	
	public void updateFormDate() {
		if(entity == null) {
			throw new IllegalStateException("Entity was null");
		}
		txtId.setText(String.valueOf(entity.getId()));
		txtName.setText(entity.getName());
		txtEmail.setText(entity.getEmail());
		Locale.setDefault(Locale.US);
		txtBaseSalary.setText(String.format("%.2f",entity.getBaseSalary()));
		
		if(entity.getBirthDate()!= null) {
		dpBirthDate.setValue(LocalDate.ofInstant( entity.getBirthDate().toInstant(), ZoneId.systemDefault()));
		}
	}


	public void updateFormData() {
		
		
	}
	
	private void setErrorMessage(Map<String,String>error) {
		Set<String>fields = errors.keySet();
		
		if(fields.contains("name")) {
			labelErrorName.setText(errors.getString("name"));
		}
		
	}

}
