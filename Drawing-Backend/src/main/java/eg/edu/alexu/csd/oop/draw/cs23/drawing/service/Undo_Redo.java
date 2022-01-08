package eg.edu.alexu.csd.oop.draw.cs23.drawing.service;

import eg.edu.alexu.csd.oop.draw.cs23.drawing.database.*;
import eg.edu.alexu.csd.oop.draw.cs23.drawing.model.*;

import org.springframework.stereotype.Service;

import java.util.Stack;

@Service
public class Undo_Redo {
    private final Shapes_Container container = new Shapes_Container();
    private static Stack<State> undoStack = new Stack<State>();
    private static Stack<State> redoStack = new Stack<State>();

    public Shape undo(){
        if(undoStack.isEmpty())
            return null;
        State state = undoStack.pop();
        redoStack.push(state);
        Shape previousShape = state.getPreviousState();
        Shape currentShape = state.getCurrentState();
        if(previousShape.getType() == null)
            container.deleteShapeByID(previousShape.getId());
        else {
            if (currentShape.getType() != null)
                container.deleteShapeByID(currentShape.getId());
            container.insert(previousShape ,true);
        }
        return previousShape;
    }

    public Shape redo(){
        if(redoStack.isEmpty())
            return null;
        State state = redoStack.pop();
        undoStack.push(state);
        Shape previousShape = state.getPreviousState();
        Shape currentShape = state.getCurrentState();
        if(currentShape.getType() == null)
            container.deleteShapeByID(currentShape.getId());
        else {
            if(previousShape.getType() != null)
                container.deleteShapeByID(previousShape.getId());
            container.insert(currentShape ,true);
        }
        return currentShape;
    }

    public void insert(State state){
        undoStack.push(state);
        while(!redoStack.isEmpty()) {
            container.deleteShapeByID(redoStack.pop().getCurrentState().getId());
        }
    }
}
