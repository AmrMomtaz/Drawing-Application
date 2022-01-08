package eg.edu.alexu.csd.oop.draw.cs23.drawing.model;

import org.json.simple.parser.ParseException;

import java.io.IOException;

public abstract class Shape implements Cloneable{
    protected int id;
    protected int linewidth;
    protected String type;
    protected String strokeStyle;


    public Shape(int ID, int line_Width, String type, String stroke) {
        this.id = ID;
        this.linewidth = line_Width;
        this.type = type;
        this.strokeStyle = stroke;
    }

    public Shape() {
    }

    public abstract void save_JSON(String path) throws IOException;

    @Override
    public Shape clone() throws CloneNotSupportedException {
        return (Shape) super.clone();
    }

    public abstract void shift();

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getLinewidth() {
        return linewidth;
    }

    public void setLinewidth(int linewidth) {
        this.linewidth = linewidth;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getStrokeStyle() {
        return strokeStyle;
    }

    public void setStrokeStyle(String strokeStyle) {
        this.strokeStyle = strokeStyle;
    }
}
