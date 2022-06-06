package it.units.server;

public interface ServerResponse {
    static String processErrorResponse(String errorMessage) {
        return String.format("ERR;%s", errorMessage);
    }

    static String processOkResponse(double requestProcessingTime, double response) {
        return String.format("OK;%.3f;%.6f", requestProcessingTime, response);
    }
}
