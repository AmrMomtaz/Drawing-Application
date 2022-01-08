package eg.edu.alexu.csd.oop.draw.cs23.drawing.model;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

public class Ellipse extends Shape {
    private int x, y, raduisX, raduisY;
    private String fillStyle;


    public Ellipse(int ID, int line_Width, String type, String stroke,
                   int x, int y, int radius_X, int radius_Y, String fill) {
        super(ID, line_Width, type, stroke);
        this.x = x;
        this.y = y;
        this.raduisX = radius_X;
        this.raduisY = radius_Y;
        this.fillStyle = fill;
    }

    @Override
    public void shift() {
        this.x += 10;
        this.y += 10;
    }

    public Ellipse(){

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
        obj.put("raduisX", this.raduisX);
        obj.put("raduisY",this.raduisY);
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

    public int getRaduisX() {
        return raduisX;
    }

    public void setRaduisX(int raduisX) {
        this.raduisX = raduisX;
    }

    public int getRaduisY() {
        return raduisY;
    }

    public void setRaduisY(int raduisY) {
        this.raduisY = raduisY;
    }

    public String getFillStyle() {
        return fillStyle;
    }

    public void setFillStyle(String fillStyle) {
        this.fillStyle = fillStyle;
    }
}
