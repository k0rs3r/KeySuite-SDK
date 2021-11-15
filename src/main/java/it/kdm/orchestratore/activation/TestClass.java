package it.kdm.orchestratore.activation;

public class TestClass {
    public Object execute(Object... args){
        if (args==null)
            return null;
        else
            return args.length;
    }
}
