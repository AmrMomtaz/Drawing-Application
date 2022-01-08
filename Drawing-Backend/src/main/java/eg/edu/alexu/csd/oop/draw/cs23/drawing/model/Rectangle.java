package eg.edu.alexu.csd.oop.draw.cs23.drawing.model;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

public class Rectangle extends Shape{
    private int x, y, width, height;
    private String fillStyle;



    public Rectangle(int ID, int line_Width, String type, String stroke,
                     int x, int y, int width, int height, String fill) {
        super(ID, line_Width, type, stroke);
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.fillStyle = fill;
    }

    @Override
    public void shift() {
        this.x += 10;
        this.y += 10;
    }

    public Rectangle(){

    }

    @Override
    public void save_JSON(String path) throws IOException {
        JSONObject obj = new JSONObject();
        obj.put("id", this.id);
        obj.put("linewidth", this.linewidth);
        obj.put("type", this.type);
        obj.put("strokeStyle", this.strokeStyle);
        obj.put("x", this.x);
        obj.put("y", this.y);
        obj.put("width", this.width);
        obj.put("height",this.height);
        obj.put("fillStyle",this.fillStyle);
        FileWriter fw_JSON = new FileWriter(path);
        fw_JSON.write(obj.toJSONString());
        fw_JSON.close();
    }


    public int getX() {
        return x;
    }

    public void setX(int x) {
        this.x = x;
    }

    public int getY() {
        return y;
    }

    public void setY(int y) {
        this.y = y;
    }

    public int getWidth() {
        return width;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    public String getFillStyle() {
        return fillStyle;
    }

    public void setFillStyle(String fillStyle) {
        this.fillStyle = fillStyle;
    }
}
