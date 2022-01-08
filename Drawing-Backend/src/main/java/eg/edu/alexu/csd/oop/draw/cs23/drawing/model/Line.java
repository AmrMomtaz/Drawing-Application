package eg.edu.alexu.csd.oop.draw.cs23.drawing.model;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

public class Line extends Shape{
    private int x1, y1, x2, y2;

    public Line(int ID, int line_Width, String type, String stroke,
                int x_1, int y_1, int x_2, int y_2) {
        super(ID, line_Width, type, stroke);
        this.x1 = x_1;
        this.y1 = y_1;
        this.x2 = x_2;
        this.y2 = y_2;
    }

    @Override
    public void shift() {
        this.x1 += 10;
        this.y1 += 10;
        this.x2 += 10;
        this.y2 += 10;
    }

    public Line(){

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
        FileWriter fw_JSON = new FileWriter(path);
        fw_JSON.write(obj.toJSONString());
        fw_JSON.close();
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
}
