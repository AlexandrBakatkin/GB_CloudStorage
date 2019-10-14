import io.netty.channel.Channel;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ListView;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.TextField;
import org.apache.log4j.Logger;

import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.ResourceBundle;

public class MainController implements Initializable {

    private static final String CLIENT_STORAGE = "client_storage/";
    private List<String> serverFileList;
    private static final Logger log = Logger.getLogger(MainController.class);

    @FXML
    TextField tfFileName;

    @FXML
    TextField tfFileNameServer;

    @FXML
    ListView<String> filesList;

    @FXML
    ListView<String> filesServerList;

    public void setList(List<String> list) {
        this.serverFileList = list;
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        new Thread(() -> NettyNetwork.getInstance().start(this)).start();
        refreshLocalFilesList();
    }

    public void pressOnDownloadBtn() throws IOException {
        if (tfFileName.getLength() > 0) {
            send(tfFileName.getText());
            tfFileName.clear();
        }
    }

    public void refreshLocalFilesList() {
        if (Platform.isFxApplicationThread()) {
            try {
                if(!Files.exists(Paths.get(CLIENT_STORAGE))){
                    Files.createDirectory(Paths.get(CLIENT_STORAGE));
                    log.info("Create dir CLIENT_STORAGE");
                }
                filesList.getItems().clear();
                Files.list(Paths.get(CLIENT_STORAGE)).map(p -> p.getFileName().toString()).forEach(o -> filesList.getItems().add(o));

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        else {
            Platform.runLater(() -> {
                try {
                    filesList.getItems().clear();
                    Files.list(Paths.get(CLIENT_STORAGE)).map(p -> p.getFileName().toString()).forEach(o -> filesList.getItems().add(o));
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });
        }
    }

    public void send(String fileName){
        NettyNetwork.getInstance().getFile(fileName);
    }

    public void getServerFilesList(){
        filesServerList.getItems().clear();
        NettyNetwork.getInstance().getServerFilesList();
        refresh();
    }

    public void pressedServerFileList(){
        tfFileName.setText(filesServerList.getSelectionModel().selectedItemProperty().getValue());
    }

    public void pressedClientFileList(){
        tfFileNameServer.setText(filesList.getSelectionModel().selectedItemProperty().getValue());
    }

    public void sendFile() throws IOException {
        String path = CLIENT_STORAGE + tfFileNameServer.getText();
        if (Files.exists(Paths.get(path))){
            NettyNetwork.getInstance().sendFile(path);
        }
    }

    public void refresh(){
        if (serverFileList != null){
            filesServerList.getItems().clear();
            for (String o: serverFileList
            ) {
                filesServerList.getItems().add(o);
            }
        }

    }
}