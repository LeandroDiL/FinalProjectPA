package it.units.request;

import it.units.server.ServerResponse;
import it.units.server.ServerStatRequestData;

import java.util.Objects;

public class StatisticalRequest implements ServerResponse {

    private final String request;

    public StatisticalRequest(String request) {
        this.request = request;
    }

    public String resolveStatRequest() {
        final long startTime = System.currentTimeMillis();
        double response;
        switch (request) {
            case "STAT_REQS":
                response = ServerStatRequestData.getNumberOfResponses();
                break;
            case "STAT_AVG_TIME":
                response = ServerStatRequestData.findAverageResponseTime();
                break;
            case "STAT_MAX_TIME":
                response = ServerStatRequestData.findMaximumResponseTime();
                break;
            default:
                return ServerResponse.processErrorResponse("The STAT request doesn't match the protocol specifications");
        }
        double requestProcessingTime = ((double) System.currentTimeMillis() - startTime) / 1000;
        ServerStatRequestData.addResponseTime(requestProcessingTime);
        return ServerResponse.processOkResponse(requestProcessingTime, response);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        StatisticalRequest that = (StatisticalRequest) o;
        return Objects.equals(request, that.request);
    }

    @Override
    public int hashCode() {
        return Objects.hash(request);
    }
}
