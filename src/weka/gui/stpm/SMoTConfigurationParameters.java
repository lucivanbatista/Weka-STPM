package weka.gui.stpm;

public class SMoTConfigurationParameters {
    double avgSpeed;
    int minTime;
    double speedLimit;

    public SMoTConfigurationParameters() {}

    public SMoTConfigurationParameters(double avgSpeed, int minTime, double speedLimit) {
        this.avgSpeed = avgSpeed;
        this.minTime = minTime;
        this.speedLimit = speedLimit;
    }
}
