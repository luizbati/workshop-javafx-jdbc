package gui;

import java.io.IOException;
import java.net.URL;
import java.sql.Date;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;

import db.DbIntegrityException;
import gui.listeners.DataChangeListener;
import gui.util.Alerts;
import gui.util.Utils;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.Pane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import model.entities.Seller;
import model.entities.seller;
import model.services.SellerService;

public class SellerListController implements Initializable, DataChangeListener {

	private SellerService service;

	@FXML
	private TableView<seller> tableViewseller;

	@FXML
	private TableColumn<Seller, Integer> tableColumnId;

	@FXML
	private TableColumn<seller, String> tableColumnName;
	
	@FXML
	private TableColumn<seller, seller> tableColumnEmail;
	
	@FXML
	private TableColumn<seller, Date> tableColumnDate;
	
	@FXML
	private TableColumn<seller, Double> tableColumnBaseSalary;
	

	@FXML
	private TableColumn<seller, seller> tableColumnEDIT;

	@FXML
	private TableColumn<seller, seller> tableColumnREMOVE;

	@FXML
	private Button btnNew;

	private ObservableList<seller> obsList;

	public void onBtnNewAction(ActionEvent event) {
		Stage parentStage = Utils.currentStage(event);
		seller obj = new seller();
		createDialogForm(obj, "/gui/sellerForm.fxml", parentStage);

	}

	public void setsellerService(SellerService service) {
		this.service = service;
	}

	@Override
	public void initialize(URL url, ResourceBundle rb) {
		initializeNodes();

	}

	private void initializeNodes() {
		tableColumnId.setCellValueFactory(new PropertyValueFactory<>("id"));
		tableColumnName.setCellValueFactory(new PropertyValueFactory<>("name"));
		tableColumnEmail.setCellValueFactory(new PropertyValueFactory<>("email"));
		TableColumn tableColumnBirthDate;
		tableColumnBirthDate.setCellValueFactory(new PropertyValueFactory<>("birthDate"));
		Utils.formatTableColumnDate(tableColumnBirthDate, "dd/MM/yyyy");
		tableColumnBaseSalary.setCellValueFactory(new PropertyValueFactory<>("baseSalary"));
		Utils.formatTableColumnDouble(tableColumnBaseSalary, 2);
		
		
	}

	public void updateView() throws IllegalAccessException {
		if (service == null) {
			throw new IllegalAccessException("Service was null");
		}
		List<seller> list = service.findAll();
		obsList = FXCollections.observableArrayList(list);
		tableViewseller.setItems(obsList);
		initEditButtons();
		initRemoveButtons();
	}

	private void createDialogForm(seller obj, String absoluteName, Stage parentStage) {
		try {
			FXMLLoader loader = new FXMLLoader(getClass().getResource(absoluteName));
			Pane pane = loader.load();

			sellerFormController controller = loader.getController();
			controller.setseller(obj);
			controller.setsellerService(new SellerService());
			controller.subscribeDataChangeListener(this);
			controller.updateFormData();

			Stage dialogStage = new Stage();
			dialogStage.setTitle("Enter seller name data");
			dialogStage.setScene(new Scene(pane));
			dialogStage.setResizable(false);
			dialogStage.initOwner(parentStage);
			dialogStage.initModality(Modality.WINDOW_MODAL);
			dialogStage.showAndWait();

		} catch (IOException e) {
			Alerts.showAlert("IO Exception", "Error loading view", e.getMessage(), AlertType.ERROR);
		}
	}

	@Override
	public void ondataChanged() {
		updateTableView();

	}

	private void updateTableView() {

	}

	private void initEditButtons() {
		tableColumnEDIT.setCellValueFactory(param -> new ReadOnlyObjectWrapper<>(param.getValue()));
		tableColumnEDIT.setCellFactory(param -> new TableCell<seller, seller>() {
			private final Button button = new Button("edit");

			@Override
			protected void updateItem(seller obj, boolean empty) {
				super.updateItem(obj, empty);
				if (obj == null) {
					setGraphic(null);
					return;
				}
				setGraphic(button);
				button.setOnAction(
						event -> createDialogForm(obj, "/gui/sellerForm.fxml", Utils.currentStage(event)));
			}
		});
	}

	private void initRemoveButtons() {
		tableColumnREMOVE.setCellValueFactory(param -> new ReadOnlyObjectWrapper<>(param.getValue()));
		tableColumnREMOVE.setCellFactory(param -> new TableCell<seller, seller>() {
			private final Button button = new Button("remove");

			@Override
			protected void updateItem(seller obj, boolean empty) {
				super.updateItem(obj, empty);
				if (obj == null) {
					setGraphic(null);
					return;
				}
				setGraphic(button);
				button.setOnAction(event -> removeEntity(obj));
			}
		});
	}

	private void removeEntity(seller obj) {
		
		Optional<ButtonType> result = Alerts.showConfirmation("Confimation", "Are you sure to delelte");
	
		
		if(result.get()==ButtonType.OK) {
			if(service == null) {
				throw new IllegalStateException("Service was null");
			}
			try {
			
			service.remove(obj);
			updateTableView();
			
		}
		catch(DbIntegrityException e) {
			Alerts.showAlert("Error removing object", null,e.getMessage(),AlertType.ERROR);
		}
	}

	
}
}
