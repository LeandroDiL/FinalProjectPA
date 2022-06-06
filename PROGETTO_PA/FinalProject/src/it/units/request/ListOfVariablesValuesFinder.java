package it.units.request;

import it.units.exception.OutOfRegexException;
import it.units.exception.ParseErrorException;
import it.units.exception.TupleSizeException;

import java.util.ArrayList;
import java.util.List;

public class ListOfVariablesValuesFinder {
    private String[] variableNames;
    private final String request;

    public ListOfVariablesValuesFinder(String request) {
        this.request = request;
    }

    public List getListOfTuples() throws ParseErrorException, TupleSizeException, OutOfRegexException {
        String[] variableValuesFunctions = request.split(",");

        String[][] splittedVariableValues = new String[4][variableValuesFunctions.length];
        for (int y = 0; y < variableValuesFunctions.length; y++) {
            String[] tempSplittedVariableValues = variableValuesFunctions[y].split(":");
            if (tempSplittedVariableValues.length % 4 != 0)
                throw new ParseErrorException("Error has been reached unexpectedly while parsing");
            for (int x = 0; x < 4; x++) {
                splittedVariableValues[x][y] = tempSplittedVariableValues[x];
            }
        }
        variableNames = new String[variableValuesFunctions.length];
        int namesIndex = 0;
        List listOfTuples = new ArrayList<double[]>();
        for (int y = 0; y < variableValuesFunctions.length; y++) {
            double[] temp = new double[3];
            for (int x = 0; x < 4; x++) {
                if (x == 2) {
                    if (Double.parseDouble(splittedVariableValues[x][y]) <= 0.0) {
                        throw new TupleSizeException("Can't reach x_upper value if step is less or equal to 0");
                    }
                    temp[1] = Double.parseDouble(splittedVariableValues[x][y]);
                }
                if ((x == 1) | (x == 3)) temp[x - 1] = Double.parseDouble(splittedVariableValues[x][y]);
                if (x == 0) {
                    if (splittedVariableValues[x][y].matches("[a-z][a-z0-9]*")) {
                        variableNames[namesIndex] = splittedVariableValues[x][y];
                        namesIndex++;
                    } else
                        throw new OutOfRegexException("Variable name " + splittedVariableValues[x][y] + " out of regex");
                }
            }
            listOfTuples.add(buildTuple(temp));
        }
        return listOfTuples;
    }

    private static double[] buildTuple(double[] doubles) {
        List<Double> functionA = new ArrayList<>();
        int k = 0;
        String s = "" + doubles[0];
        String[] result = s.split("\\.");
        while ((doubles[0] + doubles[1] * k) < doubles[2]) {
            functionA.add(returnRoundedValue(result[1].length(), doubles[0] + doubles[1] * k));
            k++;
        }
        functionA.add(doubles[0] + doubles[1] * k);
        return functionA.stream().mapToDouble(Double::doubleValue).toArray();
    }

    private static double returnRoundedValue(int numberOfDigits, double value) {
        double multiplier = 0.0;
        for (int i = 0; i < numberOfDigits; i++) {
            multiplier = multiplier + 10.0;
        }
        value = (double) Math.round(value * multiplier);
        return value / multiplier;
    }

    public String[] getNames() {
        return variableNames;
    }
}
