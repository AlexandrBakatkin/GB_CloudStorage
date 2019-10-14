import java.util.List;

public class FileListMessage extends AbstractMessage {

    private List<String> fileList;

    public List<String> getFileList() {
        return fileList;
    }

    public FileListMessage(List<String> list){
        this.fileList = list;
    }
}