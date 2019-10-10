import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.util.ReferenceCountUtil;

import java.io.IOException;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import java.util.List;

public class ServerMainHandler extends ChannelInboundHandlerAdapter {

    final private String SERVER_STORAGE = "./server_storage/";

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        try {
            if (msg == null) {
                return;
            }
            if (msg instanceof FileListRequest){

                List<String> fileList = new ArrayList<>();
                Path path = Paths.get(SERVER_STORAGE);

                walkFileTree(fileList, path);

                FileListMessage fileListMessage = new FileListMessage(fileList);
                ctx.writeAndFlush(fileListMessage);
            }
            if (msg instanceof FileRequest) {
                FileRequest fr = (FileRequest) msg;
                if (Files.exists(Paths.get(SERVER_STORAGE + fr.getFilename()))) {
                    FileMessage fm = new FileMessage(Paths.get(SERVER_STORAGE + fr.getFilename()));
                    ctx.writeAndFlush(fm);
                    String str = "Передаем файл";
                    ctx.writeAndFlush(str);
                }
            }
        } finally {
            ReferenceCountUtil.release(msg);
        }
    }

    private void walkFileTree(final List<String> fileList, Path path) throws IOException {
        Files.walkFileTree(path, new FileVisitor<Path>() {
            @Override
            public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) throws IOException {
                return FileVisitResult.CONTINUE;
            }

            @Override
            public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
                fileList.add(file.getFileName().toString());
                return FileVisitResult.CONTINUE;
            }

            @Override
            public FileVisitResult visitFileFailed(Path file, IOException exc) throws IOException {
                return FileVisitResult.CONTINUE;
            }

            @Override
            public FileVisitResult postVisitDirectory(Path dir, IOException exc) throws IOException {
                return FileVisitResult.CONTINUE;
            }
        });
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        cause.printStackTrace();
        ctx.close();
    }
}