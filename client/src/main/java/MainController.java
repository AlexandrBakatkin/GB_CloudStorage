import io.netty.channel.Channel;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;

import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.ResourceBundle;

public class MainController implements Initializable {

    private static final String CLIENT_STORAGE = "client_storage/";
    private Channel currentChannel;
    private MainController mainController;
    private List<String> serverFileList;

    @FXML
    TextField tfFileName;

    @FXML
    ListView<String> filesList;

    @FXML
    ListView<String> filesServerList;

    public void setList(List<String> list) {
        this.serverFileList = list;
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {

        mainController = this;

        new Thread(new Runnable() {

            /*@Override
            public void run() {
                EventLoopGroup group = new NioEventLoopGroup();
                try {
                    Bootstrap clientBootstrap = new Bootstrap();
                    clientBootstrap.group(group);
                    clientBootstrap.channel(NioSocketChannel.class);
                    clientBootstrap.remoteAddress(new InetSocketAddress("localhost", 8180));
                    clientBootstrap.handler(new ChannelInitializer<SocketChannel>() {
                        protected void initChannel(SocketChannel socketChannel) throws Exception {
                            socketChannel.pipeline().addLast(
                                    new ObjectDecoder(50 * 1024 * 1024, ClassResolvers.cacheDisabled(null)),
                                    new ObjectEncoder(),
                                    new ClientHandler(mainController));
                            currentChannel = socketChannel;
                        }
                    });
                    ChannelFuture channelFuture = clientBootstrap.connect().sync();
                    channelFuture.channel().closeFuture().sync();
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    try {
                        group.shutdownGracefully().sync();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }*/

            @Override
            public void run(){
                NettyNetwork.getInstance().start(mainController);
            }
        }).start();

        /*t.setDaemon(true);
        t.start();*/
        refreshLocalFilesList();
        System.out.println(this.toString());

    }

    public void pressOnDownloadBtn(ActionEvent actionEvent) throws IOException {
        if (tfFileName.getLength() > 0) {
            send(tfFileName.getText());
            tfFileName.clear();
        }
        refreshLocalFilesList();
    }

    private void refreshLocalFilesList() {
        if (Platform.isFxApplicationThread()) {
            try {
                if(!Files.exists(Paths.get(CLIENT_STORAGE))){
                    Files.createDirectory(Paths.get(CLIENT_STORAGE));
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
        currentChannel.writeAndFlush(new FileRequest(fileName));
    }

    public void getServerFiles(ActionEvent actionEvent){
        filesServerList.getItems().clear();
        NettyNetwork.getInstance().getServerFiles();
        if (serverFileList != null){
            for (String o: serverFileList
            ) {
                filesServerList.getItems().add(o);
            }
        } else {
            System.out.println("Список файлов в MainController'е пустой");
        }
    }
}