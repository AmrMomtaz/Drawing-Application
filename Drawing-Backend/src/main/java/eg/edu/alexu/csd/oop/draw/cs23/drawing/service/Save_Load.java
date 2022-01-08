package eg.edu.alexu.csd.oop.draw.cs23.drawing.service;

import eg.edu.alexu.csd.oop.draw.cs23.drawing.database.Shapes_Container;
import eg.edu.alexu.csd.oop.draw.cs23.drawing.model.*;
import org.json.JSONException;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.ParseException;
import org.springframework.stereotype.Service;
import java.beans.XMLDecoder;
import java.beans.XMLEncoder;
import java.io.*;
import java.util.ArrayList;
import java.util.Scanner;


@Service
public class Save_Load
{
    public int number_of_drawings;

    public Save_Load() throws FileNotFoundException {
        Scanner fr = new Scanner(new File(".\\src\\main\\java\\eg\\edu\\alexu\\csd\\oop\\draw\\cs23\\" +
                "drawing\\database\\Data\\index.txt"));
        this.number_of_drawings = fr.nextInt();
        fr.close();
    }



    public void save(String Name,String ext) throws IOException, JSONException {

        //Initializing The Directory of the new Drawing
        File file = new File(".\\src\\main\\java\\eg\\edu\\alexu\\csd\\oop\\draw\\cs23\\" +
                "drawing\\database\\Data\\"+number_of_drawings);
        file.mkdir();

        //initializing the index of the new drawing
        FileWriter fw = new FileWriter(file+"\\index.txt");
        fw.write(Shapes_Container.shapes.size()+"\n");
        fw.write(Name+"\n");
        fw.write(ext);
        fw.close();

        //Saving all the shapes of the drawing in XML
        if (ext.equals("xml")) {
            for (int i = 0; i < Shapes_Container.shapes.size(); i++) {
                FileOutputStream fos = new FileOutputStream(new File(file.toString() + "\\" + i + ".xml"));
                XMLEncoder encoder = new XMLEncoder(fos, "ISO-8859-1", true, 0);
                encoder.writeObject(Shapes_Container.shapes.get(i));
                encoder.close();
                fos.close();
            }
        }

        //Saving all the shapes in JSON
        else {
            for (int i = 0; i < Shapes_Container.shapes.size(); i++) {
                Shapes_Container.shapes.get(i).save_JSON(file + "\\" + i + ".json");
            }
        }



        //updating number of drawings
        FileWriter fw_2 = new FileWriter(".\\src\\main\\java\\eg\\edu\\alexu\\csd\\oop\\draw\\" +
                "cs23\\drawing\\database\\Data\\index.txt",false);
        fw_2.write(""+(number_of_drawings+1));
        fw_2.close();
        number_of_drawings++;
    }

    //This function returns a list of drawings names and indices
    public Drawing [] get_list() throws IOException {
        Drawing result [] = new Drawing[number_of_drawings];
        for (int i = 0 ; i < number_of_drawings ; i++)
        {
            Scanner fr = new Scanner(new File(".\\src\\main\\java\\eg\\edu\\alexu\\csd\\oop\\draw\\" +
                    "cs23\\drawing\\database\\Data\\"+i+"\\index.txt"));
            int ID = fr.nextInt();
            String Name = fr.next();
            Drawing element = new Drawing(i,Name);
            result[i] = element;
            fr.close();
        }

        return result;
    }

    public Shape[] load(int index) throws Exception {
        File file = new File(".\\src\\main\\java\\eg\\edu\\alexu\\csd\\oop\\draw\\" +
                "cs23\\drawing\\database\\Data\\"+index);

        //Getting Number of shapes in the drawing
        Scanner fr = new Scanner(new File(".\\src\\main\\java\\eg\\edu\\alexu\\csd\\oop\\draw\\" +
                "cs23\\drawing\\database\\Data\\"+index+"\\index.txt"));
        int number_of_shapes = fr.nextInt();
        fr.next();
        String ext = fr.next();
        fr.close();

        //Initializing the result array and resetting the shapes database amd ctr
        Shape [] result = new Shape[number_of_shapes];
        Shapes_Container.shapes = new ArrayList<Shape>();
        Shapes_Container.ctr = number_of_shapes+1;

        //Loading Shapes from XML Directory
        if (ext.equals("xml")) {
            for (int i = 0; i < number_of_shapes; i++) {
                FileInputStream fis = new FileInputStream(new File(file.toString() + "\\" + i + ".xml"));
                XMLDecoder decoder = new XMLDecoder(fis);
                Shape element = (Shape) decoder.readObject();
                decoder.close();
                fis.close();
                Shapes_Container.shapes.add(element);
                result[i] = element;
            }
        }

        //Loading the Shapes as JSON files
        else{
            for (int i = 0; i < number_of_shapes; i++) {
                String path = ".\\src\\main\\java\\eg\\edu\\alexu\\csd\\oop\\draw\\" +
                        "cs23\\drawing\\database\\Data\\"+index +"\\" + i + ".json";
                Unknown_Shape unknown_shape = null;
                Shape element = Unknown_Shape.parse_JSON(path);
                Shapes_Container.shapes.add(element);
                result[i] = element;
            }
        }

        return result;
    }
}
