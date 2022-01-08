package eg.edu.alexu.csd.oop.draw.cs23.drawing.model;

public class Drawing {
    private int id;
    private String name;

    public Drawing(int id, String name) {
        this.id = id;
        this.name = name;
    }

    public Drawing(){

    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
