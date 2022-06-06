package it.units.request;

public enum ComputationKind {
    MIN("MIN"), MAX("MAX"), AVG("AVG"), COUNT("COUNT");
    public final String computationalKind;

    ComputationKind(String computationalKind) {
        this.computationalKind = computationalKind;
    }
}