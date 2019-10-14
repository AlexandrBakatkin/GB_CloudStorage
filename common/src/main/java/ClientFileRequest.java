public class ClientFileRequest extends AbstractMessage{
    private String path;

    public ClientFileRequest(String path){
        this.path = path;
    }

    public String getPath() {
        return path;
    }
}
