package ru.nsu.fit.g16201.galieva.Life.Model;

public class GameParameters implements Cloneable{
    private double LIVE_BEGIN = 2.0;
    private double LIVE_END = 3.3;
    private double BIRTH_BEGIN = 2.3;
    private double BIRTH_END = 2.9;
    private double FST_IMPACT = 1.0;
    private double SND_IMPACT = 0.3;

    public GameParameters(double lb, double le, double bb, double be, double fi, double si) {
        LIVE_BEGIN = lb;
        LIVE_END = le;
        BIRTH_BEGIN = bb;
        BIRTH_END = be;
        FST_IMPACT = fi;
        SND_IMPACT = si;
    }

    public GameParameters(){}

    public double getLIVE_BEGIN() {
        return LIVE_BEGIN;
    }

    public void setLIVE_BEGIN(double LIVE_BEGIN) {
        this.LIVE_BEGIN = LIVE_BEGIN;
    }

    public double getLIVE_END() {
        return LIVE_END;
    }

    public void setLIVE_END(double LIVE_END) {
        this.LIVE_END = LIVE_END;
    }

    public double getBIRTH_BEGIN() {
        return BIRTH_BEGIN;
    }

    public void setBIRTH_BEGIN(double BIRTH_BEGIN) {
        this.BIRTH_BEGIN = BIRTH_BEGIN;
    }

    public double getBIRTH_END() {
        return BIRTH_END;
    }

    public void setBIRTH_END(double BIRTH_END) {
        this.BIRTH_END = BIRTH_END;
    }

    public double getFST_IMPACT() {
        return FST_IMPACT;
    }

    public void setFST_IMPACT(double FST_IMPACT) {
        this.FST_IMPACT = FST_IMPACT;
    }

    public double getSND_IMPACT() {
        return SND_IMPACT;
    }

    public void setSND_IMPACT(double SND_IMPACT) {
        this.SND_IMPACT = SND_IMPACT;
    }

    @Override
    public GameParameters clone() throws CloneNotSupportedException{
        return (GameParameters)super.clone();
    }
}
