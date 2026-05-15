package ma.ac.esi.gameverseacademy.model;

public class ModImage {

    private int id;
    private int modId;
    private String imageName;
    private int position;

    public ModImage() {
    }

    public ModImage(int id, int modId,
                    String imageName,
                    int position) {

        this.id = id;
        this.modId = modId;
        this.imageName = imageName;
        this.position = position;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getModId() {
        return modId;
    }

    public void setModId(int modId) {
        this.modId = modId;
    }

    public String getImageName() {
        return imageName;
    }

    public void setImageName(String imageName) {
        this.imageName = imageName;
    }

    public int getPosition() {
        return position;
    }

    public void setPosition(int position) {
        this.position = position;
    }
}