package eg.edu.alexu.csd.oop.draw.cs23.drawing.model;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

public class Circle extends Shape{
    private int x ,y ,raduis;
    private String fillStyle;
    public Circle(int ID, int line_Width, String type, String stroke,
                  int x, int y, int radius, String fill) {
        super(ID, line_Width, type, stroke);
        this.x = x;
        this.y = y;
        this.raduis = radius;
        this.fillStyle = fill;
    }

    public Circle() {
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
        obj.put("raduis", this.raduis);
        obj.put("fillStyle",this.fillStyle);
        FileWriter fw_JSON = new FileWriter(path);
        fw_JSON.write(obj.toJSONString());
        fw_JSON.close();
    }

    @Override
    public void shift() {
        this.x += 10;
        this.x += 10;
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

    public int getRaduis() {
        return raduis;
    }

    public void setRaduis(int raduis) {
        this.raduis = raduis;
    }

    public String getFillStyle() {
        return fillStyle;
    }

    public void setFillStyle(String fillStyle) {
        this.fillStyle = fillStyle;
    }
}
