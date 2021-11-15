package it.kdm.orchestratore.executor;

import com.google.common.base.Strings;
import it.kdm.doctoolkit.services.ToolkitConnector;
import org.kie.internal.executor.api.Command;
import org.kie.internal.executor.api.Reoccurring;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public abstract class RecurringCommand implements Command, Reoccurring {

    protected final static ThreadLocal<Integer> threadLocal = new ThreadLocal<>();

    public static boolean isRecurringCommand(String cmdClass){

        if (RecurringCommand.getRetries().size()>0 && cmdClass!=null){
            try{
                Class cls = Class.forName(cmdClass);
                if (RecurringCommand.class.isAssignableFrom(cls))
                    return true;
            } catch (ClassNotFoundException cnfExc){
                cnfExc.printStackTrace();
            }
        }
        return false;
    }

    public static void setIndex(Integer idx){
        threadLocal.set(idx);
    }

    /*public static Integer getIndex(){
        return threadLocal.get();
    }*/

    public static List<Integer> getRetries(){

        String recurringRetries = System.getProperty("recurringRetries", ToolkitConnector.getGlobalProperty("recurringRetries"));

        List<Integer> list = new ArrayList<>();
        if (!Strings.isNullOrEmpty(recurringRetries)){
            String[] items = recurringRetries.split(",");
            for( String item : items ){
                list.add(Integer.parseInt(item));
            }
        }
        return list;
    }

    @Override
    public Date getScheduleTime() {

        List<Integer> retries = getRetries();
        Integer idx = threadLocal.get();

        if (idx!=null && idx>=0 && idx<retries.size()) {
            return new Date(System.currentTimeMillis() + 1000 * retries.get(idx));
        }
        else
            return null;
    }


}
