package eg.edu.alexu.csd.oop.draw.cs23.drawing.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.FileReader;
import java.io.IOException;

public class Unknown_Shape
{
    private int ID,Line_Width,X,Y,Radius,X_1, Y_1, X_2, Y_2, X_3, Y_3, Width, Height,Radius_X, Radius_Y;
    private String Type,Stroke,Fill;

    public Unknown_Shape(@JsonProperty("id") int ID,
                         @JsonProperty("linewidth")int line_Width,
                         @JsonProperty("x")int x,
                         @JsonProperty("y")int y,
                         @JsonProperty("raduis")int radius,
                         @JsonProperty("x1")int x_1,
                         @JsonProperty("y1")int y_1,
                         @JsonProperty("x2")int x_2,
                         @JsonProperty("y2")int y_2,
                         @JsonProperty("x3")int x_3,
                         @JsonProperty("y3")int y_3,
                         @JsonProperty("width")int width,
                         @JsonProperty("height")int height,
                         @JsonProperty("raduisX")int radius_X,
                         @JsonProperty("raduisY")int radius_Y,
                         @JsonProperty("type")String type,
                         @JsonProperty("strokeStyle")String stroke,
                         @JsonProperty("fillStyle")String fill) {
        this.ID = ID;
        this.Line_Width = line_Width;
        this.X = x;
        this.Y = y;
        this.Radius = radius;
        this.X_1 = x_1;
        this.Y_1 = y_1;
        this.X_2 = x_2;
        this.Y_2 = y_2;
        this.X_3 = x_3;
        this.Y_3 = y_3;
        this.Width = width;
        this.Height = height;
        this.Radius_X = radius_X;
        this.Radius_Y = radius_Y;
        this.Type = type;
        this.Stroke = stroke;
        this.Fill = fill;
    }

    public static Shape parse_JSON(String path) throws Exception {
        JSONParser jsonParser = new JSONParser();
        FileReader reader = new FileReader(path);
        Object obj = jsonParser.parse(reader);
        JSONObject shapeObject = (JSONObject) obj;
        Unknown_Shape unknown_shape = new Unknown_Shape(parseInt("id",shapeObject),
                parseInt("linewidth",shapeObject),
                parseInt("x",shapeObject),
                parseInt("y",shapeObject),
                parseInt("raduis",shapeObject),
                parseInt("x1",shapeObject),
                parseInt("y1",shapeObject),
                parseInt("x2",shapeObject),
                parseInt("y2",shapeObject),
                parseInt("x3",shapeObject),
                parseInt("y3",shapeObject),
                parseInt("width",shapeObject),
                parseInt("height",shapeObject),
                parseInt("raduisX",shapeObject),
                parseInt("raduisY",shapeObject),
                (String)shapeObject.get("type"),
                (String)shapeObject.get("strokeStyle"),
                (String)shapeObject.get("fillStyle"));
        reader.close();
        return unknown_shape.map();
    }

    private static int parseInt(String att,JSONObject shapeObject){
        if (shapeObject.get(att)==null)
            return 0;
        else return
                Integer.parseInt(shapeObject.get(att).toString());
    }

    public Shape map() throws Exception {
        if (Type.equals("line")){
            return new Line(ID,Line_Width,Type,Stroke,X_1,Y_1,X_2,Y_2);
        }
        else if (Type.equals("triangle")){
            return new Triangle(ID,Line_Width,Type,Stroke,X_1,Y_1,X_2,Y_2,X_3,Y_3,Fill);
        }
        else if (Type.equals("square")){
            return new Square(ID,Line_Width,Type,Stroke,X,Y,Width,Height,Fill);
        }
        else if (Type.equals("rectangle")){
            return new Rectangle(ID,Line_Width,Type,Stroke,X,Y,Width,Height,Fill);
        }
        else if (Type.equals("circle")){
            return new Circle(ID,Line_Width,Type,Stroke,X,Y,Radius,Fill);
        }
        else if (Type.equals("ellipse")){
            return new Ellipse(ID,Line_Width,Type,Stroke,X,Y,Radius_X,Radius_Y,Fill);
        }
        throw new Exception("Error : The type sent from the front-end is not correct");
    }
}