package ma.ac.esi.gameverseacademy.model;

public class Game {
    private int id;
    private String title;
    private String description;
    private String publisher;
    private String developer;
    private String platform;
    private String releaseDate;
    private int metacritic;

    public Game() {}

    public Game(int id, String title, String description, String publisher, String developer, String platform, String releaseDate, int metacritic) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.publisher = publisher;
        this.developer = developer;
        this.platform = platform;
        this.releaseDate = releaseDate;
        this.metacritic = metacritic;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    
    public String getPublisher() { return publisher; }
    public void setPublisher(String publisher) { this.publisher = publisher; }
    
    public String getDeveloper() { return developer; }
    public void setDeveloper(String developer) { this.developer = developer; }
    
    public String getPlatform() { return platform; }
    public void setPlatform(String platform) { this.platform = platform; }
    
    public String getReleaseDate() { return releaseDate; }
    public void setReleaseDate(String releaseDate) { this.releaseDate = releaseDate; }
    
    public int getMetacritic() { return metacritic; }
    public void setMetacritic(int metacritic) { this.metacritic = metacritic; }
}
