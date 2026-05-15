package ma.ac.esi.gameverseacademy.model;

public class Tag {
    private int id;
    private String name;
    private String color;

    public Tag() {}

    public Tag(int id, String name, String color) {
        this.id = id;
        this.name = name;
        this.color = color;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getColor() { return color; }
    public void setColor(String color) { this.color = color; }

    /**
     * Converts hex color to RGB string "r, g, b"
     */
    public String getRgb() {
        if (color == null || color.isEmpty()) return "92, 255, 176";
        String hex = color.replace("#", "");
        try {
            if (hex.length() == 6) {
                int r = Integer.parseInt(hex.substring(0, 2), 16);
                int g = Integer.parseInt(hex.substring(2, 4), 16);
                int b = Integer.parseInt(hex.substring(4, 6), 16);
                return r + ", " + g + ", " + b;
            }
        } catch (Exception e) {}
        return "92, 255, 176";
    }
}
