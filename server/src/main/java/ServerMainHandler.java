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

                if(Files.exists(Paths.get(SERVER_STORAGE))){
                    walkFileTree(fileList, path);
                    if (fileList.isEmpty()){
                        noFilesMsg(ctx, "Dir is empty", fileList);
                    }
                    FileListMessage fileListMessage = new FileListMessage(fileList);
                    ctx.writeAndFlush(fileListMessage);
                } else {
                    Files.createDirectory(Paths.get(SERVER_STORAGE));
                    noFilesMsg(ctx, "No files", fileList);
                }
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

    private void noFilesMsg(ChannelHandlerContext ctx, String str, List<String> fileList) {
        fileList.add("No files");
        FileListMessage fileListMessage = new FileListMessage(fileList);
        ctx.writeAndFlush(fileListMessage);
    }

    private void walkFileTree(final List<String> fileList, Path path) throws IOException {

        Files.walkFileTree(path, new FileVisitor<Path>() {

            StringBuilder stringBuilder = new StringBuilder();

            @Override
            public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) throws IOException {
                stringBuilder.append(dir.getFileName().toString() + "/");
                return FileVisitResult.CONTINUE;
            }

            @Override
            public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
                int i = stringBuilder.length();
                stringBuilder.append(file.getFileName().toString());
                fileList.add(stringBuilder.toString());
                stringBuilder.delete(i, stringBuilder.length());
                return FileVisitResult.CONTINUE;
            }

            @Override
            public FileVisitResult visitFileFailed(Path file, IOException exc) throws IOException {
                return FileVisitResult.CONTINUE;
            }

            @Override
            public FileVisitResult postVisitDirectory(Path dir, IOException exc) throws IOException {
                stringBuilder.delete(stringBuilder.length() - 2, stringBuilder.length());
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