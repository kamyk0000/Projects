package sample;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.fxml.FXML;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.GridPane;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.UnaryOperator;

public class Controller {


    @FXML
    private Stage stage;
    @FXML
    private Scene scene;
    @FXML
    private Parent root;

    @FXML
    private Button btnDelete,btnEdit,btnList,btnNew,btnShow;

    @FXML
    private Button btnAddNewForm,btnUpdateForm;

    @FXML
    private Label createdL,editedL;


    @FXML
    private GridPane listWindow,addWindow;

    @FXML
    private TableView<Sheet> tableView;

    @FXML
    private TableColumn<Sheet, String> nazwaC,klasaC,atrybutyC,rasaC;
    @FXML
    private TableColumn<Sheet, Integer> poziomC,silaC,zrecznoscC,pancerzC,inicjatywaC,inteligencjaC,wiedzaC,charyzmaC,wytrzymaloscC;

    private List<TextField> textFields;

    @FXML
    private TextField biegloscF,charyzmaF,inicjatywaF,szybkoscF,inteligencjaF,nazwaF,panncerzF,percepcjaF,rasaF,silaF;

    @FXML
    private Label charyzmaL,inteligencjaL;
    @FXML
    private Label silaL;
    @FXML
    private TextField wiedzaF,bron1F,bron2F,bron3F;
    @FXML
    private TextArea otherF;
    @FXML
    private Label wiedzaL;
    @FXML
    private TextField wytrzymaloscF;
    @FXML
    private Label wytrzymaloscL;
    @FXML
    private Label zrecznoscL;
    @FXML
    private TextField zrecznoscF;
    @FXML
    private Label mocL;
    @FXML
    private TextField mocF;
    @FXML
    private Label zaklL;
    @FXML
    private TextField zaklF;

    @FXML
    private ChoiceBox<String> klasaF;
    private String[] klasy = {
            "Barbarzyńca",
            "Bard",
            "Czarodziej",
            "Druid",
            "Kapłan",
            "Łotrzyk",
            "Mnich",
            "Paladyn",
            "Tropiciel",
            "Wojownik",
            "Zaklinacz"
    };

    @FXML
    private Spinner<Integer> poziomF;

    @FXML
    private TextField searchF;
    private final ObservableList<Sheet> dataList = FXCollections.observableArrayList();

    private Alert alert;
    private String filename="sheets.txt";
    private Sheet current;

    @FXML
    private GridPane mainPane;
    private FileChooser fileChooser;

    @FXML
    private CheckBox akrobatykaC,historiaC,intuicjaC,naturaC,opiekaC,percepcjaC,medycynaC,zastraszanieC,wystepyC;
    @FXML
    private CheckBox perswazjaC,oszustwoC,atletykaC,religiaC,skradanieC,sledztwoC,wiedzaTajemnaC,zwinneReceC;
    private List<CheckBox> checkBoxes;

    public void initialize(){
        alert=new Alert(Alert.AlertType.NONE);

        loadData();

        // grupuje pola formularza
        groupFields();

        // połączenie pól z formularza z tablicą
        linkTableView();

        // pola updatowalne
        updateUpdatableFields();

        timeStamps();

        searchBar();

        ObservableList<Sheet> items = tableView.getSelectionModel().getSelectedItems();
        items.addListener((javafx.collections.ListChangeListener<? super Sheet>) change -> {
            timeStamps();
        });

    }

    public void changeFile() {
        Stage stage = (Stage) mainPane.getScene().getWindow();
        fileChooser = new FileChooser();
        fileChooser.setTitle("Wybierz miejsce zapisu");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("TXT","*.txt"));
        fileChooser.setInitialDirectory(new File(System.getProperty("user.home")));
        File selected = fileChooser.showOpenDialog(stage);
        saveData();
        dataList.clear();
        filename= String.valueOf(selected);
        loadData();
        tableView.refresh();
    }

    public void linkTableView() {
        nazwaC.setCellValueFactory(new PropertyValueFactory<>("nazwa"));
        klasaC.setCellValueFactory(new PropertyValueFactory<>("klasa"));
        poziomC.setCellValueFactory(new PropertyValueFactory<>("poziom"));
        atrybutyC.setCellValueFactory(new PropertyValueFactory<>("atrybuty"));
        rasaC.setCellValueFactory(new PropertyValueFactory<>("rasa"));
        pancerzC.setCellValueFactory(new PropertyValueFactory<>("pancerz"));
        silaC.setCellValueFactory(new PropertyValueFactory<>("sila"));
        zrecznoscC.setCellValueFactory(new PropertyValueFactory<>("zrecznosc"));
        inteligencjaC.setCellValueFactory(new PropertyValueFactory<>("inteligencja"));
        wiedzaC.setCellValueFactory(new PropertyValueFactory<>("wiedza"));
        wytrzymaloscC.setCellValueFactory(new PropertyValueFactory<>("wytrzymalosc"));
        charyzmaC.setCellValueFactory(new PropertyValueFactory<>("charyzma"));
        inicjatywaC.setCellValueFactory(new PropertyValueFactory<>("inicjatywa"));
    }

    public void unselectItem() {
        tableView.getSelectionModel().select(null);
    }

    public void searchBar() {
        FilteredList<Sheet> filterList = new FilteredList<>(dataList, b -> true);
        searchF.textProperty().addListener((observableValue, s, t1) -> {
            filterList.setPredicate(item -> {
                if (t1==null||t1.isEmpty()){
                    return true;
                }
                String filter=t1.toLowerCase();
                if (item.getNazwa().toLowerCase().contains(filter)) {
                    return true;
                } else if (nazwaC.isVisible()&&item.getRasa().toLowerCase().contains(filter)) {
                    return true;
                } else if (klasaC.isVisible()&&item.getKlasa().toLowerCase().contains(filter)) {
                    return true;
                } else if (poziomC.isVisible()&&String.valueOf(item.getPoziom()).toLowerCase().contains(filter)) {
                    return true;
                } else if (atrybutyC.isVisible()&&String.valueOf(item.getAtrybuty()).toLowerCase().contains(filter)) {
                    return true;
                } else if (pancerzC.isVisible()&&String.valueOf(item.getPancerz()).toLowerCase().contains(filter)) {
                    return true;
                } else if (inicjatywaC.isVisible()&&String.valueOf(item.getInicjatywa()).toLowerCase().contains(filter)) {
                    return true;
                } else if (silaC.isVisible()&&String.valueOf(item.getSila()).toLowerCase().contains(filter)) {
                    return true;
                } else if (wiedzaC.isVisible()&&String.valueOf(item.getWiedza()).toLowerCase().contains(filter)) {
                    return true;
                } else if (inteligencjaC.isVisible()&&String.valueOf(item.getInteligencja()).toLowerCase().contains(filter)) {
                    return true;
                } else if (zrecznoscC.isVisible()&&String.valueOf(item.getZrecznosc()).toLowerCase().contains(filter)) {
                    return true;
                } else if (charyzmaC.isVisible()&&String.valueOf(item.getCharyzma()).toLowerCase().contains(filter)) {
                    return true;
                } else if (wytrzymaloscC.isVisible()&&String.valueOf(item.getWytrzymalosc()).toLowerCase().contains(filter)) {
                    return true;
                } else
                    return false;
            });
        });


        SortedList<Sheet> sortedList = new SortedList<>(filterList);
        sortedList.comparatorProperty().bind(tableView.comparatorProperty());
        tableView.setItems(sortedList);

    }

    public void updateUpdatableFields() {

        klasaF.valueProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observableValue, String s, String t1) {
                if (klasaF.getValue()==null) {
                } else {
                    String klasa=klasaF.getValue();
                    if (klasa.equals("Bard") || klasa.equals("Czarodziej") || klasa.equals("Druid") || klasa.equals("Kapłan") || klasa.equals("Zaklinacz") || klasa.equals("Paladyn") || klasa.equals("Tropiciel")) {
                        zaklF.setVisible(true);
                        zaklL.setVisible(true);
                        mocF.setVisible(true);
                        mocL.setVisible(true);
                    } else {
                        zaklF.setVisible(false);
                        zaklF.setText("0");
                        zaklL.setVisible(false);
                        mocF.setVisible(false);
                        mocF.setText("0");
                        mocL.setVisible(false);
                    }
                }
            }
        });
        poziomF.valueProperty().addListener((observableValue, integer, t1) -> {
            int value = poziomF.getValue();
            if (value>0)
                biegloscF.setText("2");
            if (value>4)
                biegloscF.setText("3");
            if (value>8)
                biegloscF.setText("4");
            if (value>12)
                biegloscF.setText("5");
            if (value>16)
                biegloscF.setText("6");
        });
        percepcjaC.textProperty().addListener((observableValue, s, t1) -> {
            if (String.valueOf(percepcjaC.getText().charAt(0)).matches("[0-9]*")) {
                percepcjaF.setText(String.valueOf(10+Integer.parseInt(String.valueOf(percepcjaC.getText().charAt(0)))));
            } else if (String.valueOf(percepcjaC.getText().charAt(0)).equals("-")) {
                if (String.valueOf(percepcjaC.getText().charAt(1)).matches("[0-9]*")) {
                    percepcjaF.setText(String.valueOf(10-Integer.parseInt(String.valueOf(percepcjaC.getText().charAt(1)))));
                }
            }
        });

        zrecznoscL.textProperty().addListener((observableValue, s, t1) -> inicjatywaF.setText(zrecznoscL.getText()));

        wytrzymaloscF.textProperty().addListener((observableValue, s, t1) -> {
            if (wytrzymaloscF.getLength()<=2) {
                if (!Objects.equals(wytrzymaloscF.getText(), "")) {
                    int value = Integer.parseInt(wytrzymaloscF.getText())/2 - 5;
                    if (value >= 0) {
                        wytrzymaloscL.setText("+"+value+"");
                    }
                    wytrzymaloscL.setText(Integer.toString(value));

                } else if (wytrzymaloscF.getText().equals("")) {
                    wytrzymaloscL.setText("");

                }
            } else if ((wytrzymaloscF.getLength()>2)) { //||((Integer.parseInt(String.valueOf(field.getText().charAt(0))))>3)
                wytrzymaloscF.setText("30");
            }
        });


        updateAttributes(silaF,silaL,atletykaC,"Atletyka");


        updateAttributes(zrecznoscF,zrecznoscL,akrobatykaC,"Akrobatyka");
        updateAttributes(zrecznoscF,zrecznoscL,zwinneReceC,"Zwinne ręce");
        updateAttributes(zrecznoscF,zrecznoscL,skradanieC,"Skradanie");

        updateAttributes(wiedzaF,wiedzaL,intuicjaC,"Intuicja");
        updateAttributes(wiedzaF,wiedzaL,medycynaC,"Medycyna");
        updateAttributes(wiedzaF,wiedzaL,opiekaC,"Opieka zwierząt");
        updateAttributes(wiedzaF,wiedzaL,percepcjaC,"Percepcja");

        updateAttributes(inteligencjaF,inteligencjaL,wiedzaTajemnaC,"Arkana");
        updateAttributes(inteligencjaF,inteligencjaL,historiaC,"Historia");
        updateAttributes(inteligencjaF,inteligencjaL,sledztwoC,"Śledztwo");
        updateAttributes(inteligencjaF,inteligencjaL,naturaC,"Natura");
        updateAttributes(inteligencjaF,inteligencjaL,religiaC,"Religia");

        updateAttributes(charyzmaF,charyzmaL,oszustwoC,"Oszustwo");
        updateAttributes(charyzmaF,charyzmaL,perswazjaC,"Perswazja");
        updateAttributes(charyzmaF,charyzmaL,wystepyC,"Występy");
        updateAttributes(charyzmaF,charyzmaL,zastraszanieC,"Zastraszanie");

    }

    public String getCheckBoxes() {
        StringBuffer sb = new StringBuffer();
        for (CheckBox checkBox : checkBoxes) {
            if (checkBox.isSelected()){
                sb.append("1,");
            } else {
                sb.append("0,");
            }
        }
        return sb.toString();
    }

    public void setCheckBoxes() {
        Sheet current=tableView.getSelectionModel().getSelectedItem();
        if (current!=null) {
            String str = current.getBoxes();
            String[] strs = str.split(",");

            int i = 0;
            for (CheckBox checkBox : checkBoxes) {
                if (Objects.equals(strs[i], "1")) {
                    checkBox.setSelected(true);
                } else if (Objects.equals(strs[i], "0")) {
                    checkBox.setSelected(false);
                }
                i++;
            }
        }
    }

    public void updateAttributes(TextField field, Label label, CheckBox checkBox, String name) {
        field.textProperty().addListener((observableValue, s, t1) -> {
            if (field.getLength()<=2) {
                if (!Objects.equals(field.getText(), "")) {
                    int value = Integer.parseInt(field.getText())/2 - 5;
                    if (value >= 0) {
                        label.setText("+"+value+"");
                    }
                    label.setText(Integer.toString(value));
                    if (checkBox.isSelected()) {
                        value+=Integer.parseInt(biegloscF.getText());
                        checkBox.setText(value + " " + name);
                    }
                    checkBox.setText(value+" "+name);
                } else if (field.getText().equals("")) {
                    label.setText("");
                    checkBox.setText(name);
                }
            } else if ((field.getLength()>2)) { //||((Integer.parseInt(String.valueOf(field.getText().charAt(0))))>3)
                field.setText("30");
            }
        });
        checkBox.selectedProperty().addListener((observableValue, aBoolean, t1) -> {
            if (!Objects.equals(label.getText(), "")) {
                int value2=Integer.parseInt(label.getText());
                if (checkBox.isSelected()) {
                    value2+=Integer.parseInt(biegloscF.getText());
                    checkBox.setText(value2 + " " + name);
                }
                checkBox.setText(value2 + " " + name);
            } else if (label.getText().equals("")){
                checkBox.setText(name);
            }
        });
        biegloscF.textProperty().addListener((observableValue, aBoolean, t1) -> {
            if (!Objects.equals(label.getText(), "")) {
                int value2=Integer.parseInt(label.getText());
                if (checkBox.isSelected()) {
                    value2+=Integer.parseInt(biegloscF.getText());
                    checkBox.setText(value2 + " " + name);
                }
                checkBox.setText(value2 + " " + name);
            } else if (label.getText().equals("")){
                checkBox.setText(name);
            }
        });
    }

    public void groupFields() {
        UnaryOperator<TextFormatter.Change> filter = change -> {
            String text = change.getText();
            if (text.matches("[0-9]*")) {
                return change;
            }
            return null;
        };
        UnaryOperator<TextFormatter.Change> filterTab = change -> {
            String text = change.getText();
            if (text.matches("(?!.*\\t).*")) {
                return change;
            }
            return null;
        };
        this.checkBoxes = new ArrayList<>();
        checkBoxes.add(atletykaC);
        checkBoxes.add(akrobatykaC);
        checkBoxes.add(zwinneReceC);
        checkBoxes.add(skradanieC);
        checkBoxes.add(wiedzaTajemnaC);
        checkBoxes.add(historiaC);
        checkBoxes.add(sledztwoC);
        checkBoxes.add(naturaC);
        checkBoxes.add(religiaC);
        checkBoxes.add(intuicjaC);
        checkBoxes.add(medycynaC);
        checkBoxes.add(opiekaC);
        checkBoxes.add(percepcjaC);
        checkBoxes.add(oszustwoC);
        checkBoxes.add(perswazjaC);
        checkBoxes.add(wystepyC);
        checkBoxes.add(zastraszanieC);

        this.textFields = new ArrayList<>();
        textFields.add(panncerzF);
        textFields.add(szybkoscF);
        textFields.add(percepcjaF);
        textFields.add(silaF);
        textFields.add(wytrzymaloscF);
        textFields.add(zrecznoscF);
        textFields.add(wiedzaF);
        textFields.add(inteligencjaF);
        textFields.add(charyzmaF);
        textFields.add(mocF);
        textFields.add(zaklF);

        for (TextField field : textFields) {
            TextFormatter<String> formatter = new TextFormatter<>(filter);
            field.setTextFormatter(formatter);
        }

        otherF.setTextFormatter(new TextFormatter<>(filterTab));
        textFields.add(nazwaF);
        textFields.add(rasaF);
        textFields.add(inicjatywaF);

        klasaF.getItems().setAll(klasy);
        biegloscF.setText("2");

        SpinnerValueFactory<Integer> valueFactory = new SpinnerValueFactory.IntegerSpinnerValueFactory(1,20);
        valueFactory.setValue(1);
        poziomF.setValueFactory(valueFactory);
    }

    public void showData() {  // tableView.getSelectionModel().getSelectedItem() tak można przechowywać elementy listy i przekazywać dalej element jako argument np w metodach: editData(current);
        editData();
        editableData(false);
        btnAddNewForm.setVisible(false);
        btnUpdateForm.setVisible(false);
        setCheckBoxes();
        timeStamps();
    }

    public void addData() {
        if (checkDuplicateData()) {
            if (checkData()) {

                String moc;
                if (!Objects.equals(mocF.getText(), "")) {
                    moc = mocF.getText();
                } else {
                    moc = "0";
                }
                String zakl;
                if (!Objects.equals(zaklF.getText(), "")) {
                    zakl = zaklF.getText();
                } else {
                    zakl = "0";
                }
                String bron1;
                if (!Objects.equals(bron1F.getText(), "")) {
                    bron1 = bron1F.getText();
                } else {
                    bron1 = " ";
                }
                String bron2;
                if (!Objects.equals(bron2F.getText(), "")) {
                    bron2 = bron2F.getText();
                } else {
                    bron2 = " ";
                }
                String bron3;
                if (!Objects.equals(bron3F.getText(), "")) {
                    bron3 = bron3F.getText();
                } else {
                    bron3 = " ";
                }
                String other;
                if (!Objects.equals(otherF.getText(), "")) {
                    other = otherF.getText();
                } else {
                    other = " ";
                }
                String created = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss").format(LocalDateTime.now());

                Sheet sheet = new Sheet(
                        nazwaF.getText(),
                        rasaF.getText(),
                        klasaF.getValue(),
                        poziomF.getValue(),
                        Integer.parseInt(panncerzF.getText()),
                        Integer.parseInt(szybkoscF.getText()),
                        Integer.parseInt(inicjatywaF.getText()),
                        Integer.parseInt(percepcjaF.getText()),
                        Integer.parseInt(biegloscF.getText()),
                        Integer.parseInt(silaF.getText()),
                        Integer.parseInt(wytrzymaloscF.getText()),
                        Integer.parseInt(zrecznoscF.getText()),
                        Integer.parseInt(inteligencjaF.getText()),
                        Integer.parseInt(wiedzaF.getText()),
                        Integer.parseInt(charyzmaF.getText()),
                        Integer.parseInt(moc),
                        Integer.parseInt(zakl),
                        created,
                        created,
                        bron1,
                        bron2,
                        bron3,
                        other,
                        getCheckBoxes()

                );
                dataList.add(sheet);

                showList();
                timeStamps();
            }
        }
    }

    public void timeStamps() {
        Sheet current=tableView.getSelectionModel().getSelectedItem();
        if (current!=null) {
            createdL.setText(current.getCreated());
            editedL.setText(current.getEdited());
        }
    }

    public void clearData() {
        for (TextField field : textFields){
            field.setText("");
        }
        for (CheckBox checkBox : checkBoxes){
            checkBox.setSelected(false);
        }
        bron1F.setText("");
        bron2F.setText("");
        bron3F.setText("");
        klasaF.setValue(null);
        poziomF.getValueFactory().setValue(1);
        biegloscF.setText("2");
        otherF.setText("");
    }

    public boolean checkData() { // ----------TO DO--------------
        for (TextField field : textFields){
            if (Objects.equals(field.getText(), "")) {
                alert = new Alert(Alert.AlertType.NONE, "Pamiętaj aby wypełnić wszystkie pola!", ButtonType.OK);
                alert.setTitle("Wypełnij wszystkie pola!");
                alert.setHeaderText("Wypełnij wszystkie pola!");
                alert.setResizable(false);
                alert.showAndWait();
                return false;
            }
        }
        return true;
    }

    public boolean checkDuplicateData() {
        for (int i = 0; i < tableView.getItems().size(); i++) {
            if (nazwaF.getText().equals(tableView.getItems().get(i).getNazwa())){
                alert = new Alert(Alert.AlertType.NONE, "Czy chcesz nadpisać istniejący już rekord?", ButtonType.OK, ButtonType.CANCEL);
                alert.setTitle("Uwaga tworzysz duplikat!");
                alert.setHeaderText("Istnieje już rekord o takiej nazwie");
                alert.setResizable(false);
                Optional<ButtonType> result = alert.showAndWait();
                System.out.println("duplicate create");
                if (result.isPresent() && result.get()==ButtonType.OK){
                    tableView.getSelectionModel().select(i);
                    updateData();
                    unselectItem();
                    return true;
                } else if (result.isPresent() && result.get()==ButtonType.CANCEL){
                    return false;
                }
            }
        }
        return true;
    }

    public void editableData(boolean edit) {
        if (edit) {
            for (TextField field : textFields) {
                field.setEditable(true);
            }
            for (CheckBox checkBox : checkBoxes) {
                checkBox.setDisable(false);
            }
            bron1F.setEditable(true);
            bron2F.setEditable(true);
            bron3F.setEditable(true);
            otherF.setEditable(true);
            klasaF.setDisable(false);
            poziomF.setDisable(false);
        } else {
            for (TextField field : textFields) {
                field.setEditable(false);
            }
            for (CheckBox checkBox : checkBoxes) {
                checkBox.setDisable(true);
            }
            bron1F.setEditable(false);
            bron2F.setEditable(false);
            bron3F.setEditable(false);
            otherF.setEditable(false);
            klasaF.setDisable(true);
            poziomF.setDisable(true);
        }
    }

    public boolean checkDuplicateEdit() {
        for (int i = 0; i < tableView.getItems().size(); i++) {
            if (nazwaF.getText().equals(tableView.getItems().get(i).getNazwa())){
                alert = new Alert(Alert.AlertType.NONE, "Czy chcesz nadpisać istniejący już rekord?", ButtonType.OK, ButtonType.CANCEL);
                alert.setTitle("Uwaga tworzysz duplikat!");
                alert.setHeaderText("Istnieje już rekord o takiej nazwie");
                alert.setResizable(false);
                Optional<ButtonType> result = alert.showAndWait();
                System.out.println("duplicate edit");
                if (result.isPresent() && result.get()==ButtonType.OK){
                    tableView.getSelectionModel().select(i);
                    deleteData();
                    return true;
                } else if (result.isPresent() && result.get()==ButtonType.CANCEL){
                    return false;
                }
            }
        }
        return true;
    }

    public void updateData() { // -----------------TO DO------------------
        current=tableView.getSelectionModel().getSelectedItem();

        if (checkData()) {
            if (current.getNazwa().equals(nazwaF.getText())||checkDuplicateEdit()) {
                String edited = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss").format(LocalDateTime.now());

                current.setNazwa(nazwaF.getText());
                current.setRasa(rasaF.getText());
                current.setKlasa(klasaF.getValue());
                current.setPoziom(poziomF.getValue());
                current.setPancerz(Integer.parseInt(panncerzF.getText()));
                current.setSzybkosc(Integer.parseInt(szybkoscF.getText()));
                current.setInicjatywa(Integer.parseInt(inicjatywaF.getText()));
                current.setPercepcja(Integer.parseInt(percepcjaF.getText()));
                current.setBieglosc(Integer.parseInt(biegloscF.getText()));
                current.setSila(Integer.parseInt(silaF.getText()));
                current.setWytrzymalosc(Integer.parseInt(wytrzymaloscF.getText()));
                current.setZrecznosc(Integer.parseInt(zrecznoscF.getText()));
                current.setInteligencja(Integer.parseInt(inteligencjaF.getText()));
                current.setWiedza(Integer.parseInt(wiedzaF.getText()));
                current.setCharyzma(Integer.parseInt(charyzmaF.getText()));
                current.setMocZaklec(Integer.parseInt(mocF.getText()));
                current.setKlasaZaklec(Integer.parseInt(zaklF.getText()));
                current.setEdited(edited);
                current.setBron1(bron1F.getText());
                current.setBron2(bron2F.getText());
                current.setBron3(bron3F.getText());
                current.setOther(otherF.getText());
                current.setBoxes(getCheckBoxes());

                tableView.refresh();

                showList();
                timeStamps();

            }
        }
    }

    public void editData() {
        Sheet current=tableView.getSelectionModel().getSelectedItem();
        if (current!=null) {
            showForm();
            btnAddNewForm.setVisible(false);
            btnUpdateForm.setVisible(true);
            nazwaF.setText(current.getNazwa());
            rasaF.setText(current.getRasa());
            panncerzF.setText(Integer.toString(current.getPancerz()));
            szybkoscF.setText(Integer.toString(current.getSzybkosc()));
            inicjatywaF.setText(Integer.toString(current.getInicjatywa()));
            percepcjaF.setText(Integer.toString(current.getPercepcja()));
            biegloscF.setText(Integer.toString(current.getBieglosc()));
            silaF.setText(Integer.toString(current.getSila()));
            wytrzymaloscF.setText(Integer.toString(current.getWytrzymalosc()));
            zrecznoscF.setText(Integer.toString(current.getZrecznosc()));
            wiedzaF.setText(Integer.toString(current.getWiedza()));
            inteligencjaF.setText(Integer.toString(current.getInteligencja()));
            charyzmaF.setText(Integer.toString(current.getCharyzma()));
            mocF.setText(Integer.toString(current.getMocZaklec()));
            zaklF.setText(Integer.toString(current.getKlasaZaklec()));
            bron1F.setText(current.getBron1());
            bron2F.setText(current.getBron2());
            bron3F.setText(current.getBron3());
            otherF.setText(current.getOther());

            klasaF.setValue(current.getKlasa());
            poziomF.getValueFactory().setValue(current.getPoziom());

            setCheckBoxes();

            timeStamps();
        }
    }

    public void loadData() { //-------------------TO DO-----------------

        try {
            BufferedReader br = new BufferedReader(
                    new FileReader(filename));
            String line;
            while((line=br.readLine()) != null){
                String[]lines = line.split("\\t");

                dataList.add(new Sheet(
                        lines[0],
                        lines[1],
                        lines[2],
                        Integer.parseInt(lines[3]),
                        Integer.parseInt(lines[4]),
                        Integer.parseInt(lines[5]),
                        Integer.parseInt(lines[6]),
                        Integer.parseInt(lines[7]),
                        Integer.parseInt(lines[8]),
                        Integer.parseInt(lines[9]),
                        Integer.parseInt(lines[10]),
                        Integer.parseInt(lines[11]),
                        Integer.parseInt(lines[12]),
                        Integer.parseInt(lines[13]),
                        Integer.parseInt(lines[14]),
                        Integer.parseInt(lines[15]),
                        Integer.parseInt(lines[16]),
                        lines[17],
                        lines[18],
                        lines[19],
                        lines[20],
                        lines[21],
                        lines[22],
                        lines[23]
                ));
            }
            br.close();
        }catch (Exception ignored){ }
    }

    public void saveData() {
        try {
            FileWriter writer = new FileWriter(filename);
            for (Sheet sheet : dataList) {
                writer.write(
                        sheet.toString() + "\n"
                );
            }
            writer.close();
        } catch (Exception ignored) { }
    }

    public void deleteData() {
        Sheet current = tableView.getSelectionModel().getSelectedItem();
        if (current!=null){
            dataList.removeAll(tableView.getSelectionModel().getSelectedItem());
            tableView.refresh();
        }
    }

    public void showList() {
        clearData();
        listWindow.setVisible(true);
        addWindow.setVisible(false);
        listWindow.toFront();
    }

    public void showForm() {
        editableData(true);
        clearData();
        listWindow.setVisible(false);
        addWindow.setVisible(true);
        addWindow.toFront();
        btnAddNewForm.setVisible(true);
        btnUpdateForm.setVisible(false);
    }

      /*
    public void loadData() {
        if (tableView.getItems() != null)
        for (int i = 0; i < tableView.getItems().size(); i++) {
            tableView.getItems().clear();
        }
        for (int i = 0; i < contentsList.size(); i++) {
            System.out.println(contentsList.get(i));
            tableView.getItems().add(contentsList.get(i));
        }
    }

     */

    // poruszanie się po oknach jeżeli będzie potrzba

    /*
    public void switchToScene1(ActionEvent event) throws Exception {
        Parent root = FXMLLoader.load(getClass().getResource("scene1.fxml"));
        stage=(Stage)((Node)event.getSource()).getScene().getWindow();
        scene=new Scene(root);
        stage.setScene(scene);
        stage.show();
    }

     */

    // even keytyped do sprawdzenia czy wpisuje się same liczby?,
    //później może ustalać timestamp tworzenia i ostatniej edycji --- tylko gdzie go stworzyć (w jego klasie??)
    /*
    public void addNewRecord() {
        //sprawdzenie poprawnosći wypełnienia formularza (później można też petlą w osobnej metodzie na wzór czyszczenia)
        if (nameField.getText().equals("") || atributeField.getText().equals("")) {
            //alert
            System.out.println("błąd, wypełnij wszytskie pola");
        }
        else if (!(atributeField.getText().matches("\\d+"))){
            //alert
            System.out.println("błąd, podaj tylko liczby");
        }
        else {
            addData();
            showList();
            // czyszczenie, też mogę do nowej metody żeby było czytelnie
            for (TextField field : textFields){
                field.setText("");
            }
        }
    }

     */

}
