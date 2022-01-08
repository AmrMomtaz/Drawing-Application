package eg.edu.alexu.csd.oop.draw.cs23.drawing.api;

import eg.edu.alexu.csd.oop.draw.cs23.drawing.model.*;
import eg.edu.alexu.csd.oop.draw.cs23.drawing.service.*;
import org.json.JSONException;
import org.json.simple.parser.ParseException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.io.FileNotFoundException;
import java.io.IOException;

@RestController
@CrossOrigin()
public class controller {

    Initialize creator = new Initialize();
    Modify modifier = new Modify();
    Delete_Shape delete = new Delete_Shape();
    Copy_Shape copy_shape = new Copy_Shape();
    Undo_Redo undo_redo = new Undo_Redo();
    public final Save_Load save_load;

    @Autowired
    public controller(Save_Load save_load) {
        this.save_load = save_load;
    }

    @PostMapping("/add")
    public int addShape(@RequestBody Unknown_Shape unknown_shape) throws Exception {
        return creator.initialize(unknown_shape);
    }

    @PostMapping("/modify")
    public void modify(@RequestBody Unknown_Shape unknown_shape) throws Exception {
        modifier.update(unknown_shape);
    }

    @DeleteMapping(path = "{ID}")
    public void deleteShape(@PathVariable("ID") int ID) throws CloneNotSupportedException {
        delete.deleteShape(ID);
    }

    @GetMapping(path = "/copy{ID}")
    public Shape copyShape(@PathVariable("ID") int ID) throws CloneNotSupportedException {
        return copy_shape.makeCopy(ID);
    }

    @GetMapping("/undo")
    public Shape undo(){
        return undo_redo.undo();
    }

    @GetMapping("redo")
    public Shape redo(){
        return undo_redo.redo();
    }

    @PostMapping("/save{Name}")
    public void save(@PathVariable("Name") String NameAndExt) throws IOException, JSONException {
        String Name ="";
        String ext = "";
        if (NameAndExt.endsWith("xml")){
            Name = NameAndExt.substring(0,(NameAndExt.length()-3));
            ext = "xml";
        }else{
            Name = NameAndExt.substring(0,(NameAndExt.length()-4));
            ext = "json";
        }
        save_load.save(Name,ext);
    }

    @GetMapping("/get_list")
    public Drawing [] get_data() throws IOException {
        return save_load.get_list();
    }

    @GetMapping("/load{path}")
    public Shape[] load(@PathVariable("path") int path) throws Exception {
        return save_load.load(path);
    }
}
