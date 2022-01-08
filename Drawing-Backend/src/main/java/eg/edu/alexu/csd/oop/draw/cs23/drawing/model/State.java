package eg.edu.alexu.csd.oop.draw.cs23.drawing.model;

public class State {
    private Shape previousState;
    private Shape currentState;

    public Shape getPreviousState() {
        return previousState;
    }

    public void setPreviousState(Shape previousState) {
        this.previousState = previousState;
    }

    public Shape getCurrentState() {
        return currentState;
    }

    public void setCurrentState(Shape currentState) {
        this.currentState = currentState;
    }
}
