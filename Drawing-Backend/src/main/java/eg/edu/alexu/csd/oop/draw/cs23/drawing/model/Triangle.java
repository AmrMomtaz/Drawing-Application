package eg.edu.alexu.csd.oop.draw.cs23.drawing.model;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

public class Triangle extends Shape{
    private int x1, y1, x2, y2, x3, y3;
    private String fillStyle;

    public Triangle(int ID, int line_Width, String type, String stroke,
                    int x_1, int y_1, int x_2, int y_2, int x_3, int y_3, String fill) {
        super(ID, line_Width, type, stroke);
        this.x1 = x_1;
        this.y1 = y_1;
        this.x2 = x_2;
        this.y2 = y_2;
        this.x3 = x_3;
        this.y3 = y_3;
        this.fillStyle = fill;
    }

    public Triangle(){

    }

    @Override
    public void save_JSON(String path) throws IOException {
        JSONObject obj = new JSONObject();
        obj.put("id", this.id);
        obj.put("linewidth", this.linewidth);
        obj.put("type", this.type);
        obj.put("strokeStyle", this.strokeStyle);
        obj.put("x1", this.x1);
        obj.put("y1", this.y1);
        obj.put("x2", this.x2);
        obj.put("y2", this.y2);
        obj.put("x3", this.x3);
        obj.put("y3", this.y3);
        obj.put("fillStyle",this.fillStyle);
        FileWriter fw_JSON = new FileWriter(path);
        fw_JSON.write(obj.toJSONString());
        fw_JSON.close();
    }


    @Override
    public void shift() {
        this.x1 += 10;
        this.y1 += 10;
        this.x2 += 10;
        this.y2 += 10;
        this.x3 += 10;
        this.y3 += 10;
    }

    public int getX1() {
        return x1;
    }

    public void setX1(int x1) {
        this.x1 = x1;
    }

    public int getY1() {
        return y1;
    }

    public void setY1(int y1) {
        this.y1 = y1;
    }

    public int getX2() {
        return x2;
    }

    public void setX2(int x2) {
        this.x2 = x2;
    }

    public int getY2() {
        return y2;
    }

    public void setY2(int y2) {
        this.y2 = y2;
    }

    public int getX3() {
        return x3;
    }

    public void setX3(int x3) {
        this.x3 = x3;
    }

    public int getY3() {
        return y3;
    }

    public void setY3(int y3) {
        this.y3 = y3;
    }

    public String getFillStyle() {
        return fillStyle;
    }

    public void setFillStyle(String fillStyle) {
        this.fillStyle = fillStyle;
    }
}
