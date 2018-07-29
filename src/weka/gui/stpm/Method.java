package weka.gui.stpm;

import java.util.Vector;

abstract class Method{
    Vector<Parameter> param = new Vector<>();

    abstract public void run(Trajectory trajectory,
                             InterceptsG interestPoints,
                             String targetFeature);
}
