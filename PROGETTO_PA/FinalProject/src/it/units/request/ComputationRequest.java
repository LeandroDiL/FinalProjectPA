package it.units.request;

import it.units.exception.*;
import it.units.server.ServerResponse;
import it.units.server.ServerStatRequestData;


public class ComputationRequest implements ServerResponse {
    private final String input;

    public ComputationRequest(String input) {
        this.input = input;
    }

    public String getComputationRequest(String input) {
        try {
            final long startTime = System.currentTimeMillis();

            String[] allPartsOfARequest = input.split(";");

            String[] computationKindAndValuesPartOfTheRequest = allPartsOfARequest[0].split("_");
            String computationKindPartOfTheRequest = computationKindAndValuesPartOfTheRequest[0];
            if (!computationKindPartOfTheRequest.equals("MAX") && !computationKindPartOfTheRequest.equals("MIN") && !computationKindPartOfTheRequest.equals("AVG") && !computationKindPartOfTheRequest.equals("COUNT")) {
                throw new PoorlyWordedRequestException("The computation kind part of the request doesn't respect protocol specifications");
            }

            String valuesKindPartOfTheRequest = computationKindAndValuesPartOfTheRequest[1];
            if (!valuesKindPartOfTheRequest.equals("GRID") && !valuesKindPartOfTheRequest.equals("LIST")) {
                throw new PoorlyWordedRequestException("The values kind part of the request doesn't respect protocol specifications");
            }

            ValuesKindSolver solver = new ValuesKindSolver(valuesKindPartOfTheRequest, allPartsOfARequest[1]);

            if (allPartsOfARequest.length >= 3) {
                if (computationKindPartOfTheRequest.equals("COUNT")) {
                    double processTime = ((double) System.currentTimeMillis() - startTime) / 1000;
                    ServerStatRequestData.addResponseTime(processTime);
                    return ServerResponse.processOkResponse(processTime, solver.length());
                }
            } else {
                throw new PoorlyWordedRequestException("Expression part of the request is missing");
            }

            String[] functions = new String[allPartsOfARequest.length - 2];
            System.arraycopy(allPartsOfARequest, 2, functions, 0, allPartsOfARequest.length - 2);

            ComputationKindSolver functionsSolver = new ComputationKindSolver(functions, solver);

            ComputationKind type = ComputationKind.valueOf(computationKindAndValuesPartOfTheRequest[0]);

            double response = functionsSolver.calculateResult(type);

            double processTime = ((double) System.currentTimeMillis() - startTime) / 1000;
            ServerStatRequestData.addResponseTime(processTime);

            return ServerResponse.processOkResponse(processTime, response);
        } catch (PoorlyWordedRequestException | VariableNotFoundException | NoResultFoundException |
                 ParseErrorException | TupleSizeException | OutOfRegexException e) {
            return ServerResponse.processErrorResponse(String.format("(%s) %s", e.getClass().getSimpleName(), e.getMessage()));
        }
    }
}
