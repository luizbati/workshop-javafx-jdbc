package gui;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
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
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import model.entities.Department;
import model.exception.ValidationException;
import model.services.SellerService;

public class DepartmentFormController implements Initializable{
	
	private Department entity;
	
	private SellerService service;
	
	private List<DataChangeListener> dataChangeListener = new ArrayList<>();
	

	@FXML private TextField txtId;
	@FXML private TextField txtName;
	@FXML Label labelErrorName;
	@FXML private Button btnSave;
	@FXML private Button btnCancel;

	private ResourceBundle errors;
	
	public void setDepartment(Department entity) {
		this.entity = entity;
	}
	
	public void setDepartmentService (SellerService service) {
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

		private Department getFormData() {
		Department obj = new Department();
		
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
		Constraints.setTextFieldMaxLength(txtName, 30);
	}
	
	public void updateFormDate() {
		if(entity == null) {
			throw new IllegalStateException("Entity was null");
		}
		txtId.setText(String.valueOf(entity.getId()));
		txtName.setText(entity.getName());
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
