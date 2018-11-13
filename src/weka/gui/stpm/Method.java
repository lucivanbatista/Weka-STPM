package weka.gui.stpm;

abstract class Method{
    SMoTConfigurationParameters parameters = new SMoTConfigurationParameters();

    abstract public void run(Trajectory trajectory,
                             InterceptsG interestPoints,
                             String targetFeature);
}
