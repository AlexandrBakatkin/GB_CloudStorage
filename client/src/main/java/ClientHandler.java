import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.List;

public class ClientHandler extends ChannelInboundHandlerAdapter {

    final private String CLIENT_STORAGE = "client_storage/";

    private List<String> list;
    private MainController mainController;

    public ClientHandler(MainController mainController){
        this.mainController = mainController;
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        if (msg == null) {
            return;
        }
        if (msg instanceof FileMessage) {
            FileMessage fm = (FileMessage) msg;
            Files.write(Paths.get(CLIENT_STORAGE + fm.getFilename()), fm.getData(), StandardOpenOption.CREATE);
        }
        if (msg instanceof String){
            String str = (String) msg;
            System.out.println(str);
        }
        if (msg instanceof FileListMessage){
            FileListMessage fileListMessage = (FileListMessage) msg;
            list = fileListMessage.getFileList();

            mainController.setList(list);
        }
    }
}