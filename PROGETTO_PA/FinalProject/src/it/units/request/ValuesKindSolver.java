package it.units.request;


import it.units.exception.OutOfRegexException;
import it.units.exception.ParseErrorException;
import it.units.exception.TupleSizeException;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class ValuesKindSolver implements Iterable<double[]> {
    private final String request;
    private List<double[]> listsOfTuples;
    private String[] variableNames;

    public ValuesKindSolver(String request, String variablesFunctions) throws ParseErrorException, TupleSizeException, OutOfRegexException {
        this.request = request;
        switch (request) {
            case "GRID" -> listsOfTuples = getCartesianProduct(variablesFunctions);
            case "LIST" -> listsOfTuples = getListsMerging(variablesFunctions);
            default -> {
            }
        }
    }

    public List<double[]> getCartesianProduct(String gridRequest) throws ParseErrorException, TupleSizeException, OutOfRegexException {
        ListOfVariablesValuesFinder var = new ListOfVariablesValuesFinder(gridRequest);
        List<double[]> listOfTuples = var.getListOfTuples();
        variableNames = var.getNames();
        List<List<Double>> listsOfDouble = new ArrayList<>();

        for (double[] firstTempListOfTuple : listOfTuples) {
            listsOfDouble.add((getListFromArray(firstTempListOfTuple)));
        }
        List<List<Double>> cp = listsOfDouble.stream()
                // represent each element of a list as a singleton list
                .map(list -> list.stream().map(Arrays::asList)
                        // Stream<List<List<String>>>
                        .collect(Collectors.toList()))
                // summation of pairs of list into a single list
                .reduce((list1, list2) -> list1.stream()
                        // combinations of inner lists
                        .flatMap(inner1 -> list2.stream()
                                // concatenate into a single list
                                .map(inner2 -> Stream.of(inner1, inner2)
                                        .flatMap(List::stream)
                                        .collect(Collectors.toList())))
                        // list of combinations
                        .collect(Collectors.toList()))
                // otherwise an empty list
                .orElse(Collections.emptyList());

        List<double[]> listOfDoubleArrays = new ArrayList<>();
        for (List<Double> doubles : cp) {
            listOfDoubleArrays.add(getArrayFromList(doubles));
        }
        return listOfDoubleArrays;
    }

    private static List<Double> getListFromArray(double[] values) {
        List<Double> elements = new ArrayList<>();
        for (double value : values) {
            elements.add(value);
        }
        return elements;
    }

    private static double[] getArrayFromList(List<Double> values) {
        double[] elements = new double[values.size()];
        for (int i = 0; i < values.size(); i++) {
            elements[i] = values.get(i);
        }
        return elements;
    }

    public List<double[]> getListsMerging(String listRequest) throws ParseErrorException, TupleSizeException, OutOfRegexException {
        ListOfVariablesValuesFinder var = new ListOfVariablesValuesFinder(listRequest);
        List<double[]> listOfTuples = var.getListOfTuples();
        variableNames = var.getNames();
        int size = listOfTuples.get(0).length;
        for (double[] values : listOfTuples) {
            if (values.length != size) throw new TupleSizeException("Can't merge lists of different size");
        }

        double[][] arrayOfDouble = new double[listOfTuples.get(0).length][listOfTuples.size()];
        for (int i = 0; i < listOfTuples.size(); i++) {
            double[] tempArrayOfDouble = listOfTuples.get(i);
            for (int j = 0; j < tempArrayOfDouble.length; j++) {
                arrayOfDouble[j][i] = tempArrayOfDouble[j];
            }
        }
        listOfTuples.clear();
        for (int i = 0; i < size; i++) {
            listOfTuples.add(arrayOfDouble[i]);
        }
        return listOfTuples;
    }

    public int length() {
        return listsOfTuples.size();
    }

    public String[] getVariableNames() {
        return variableNames;
    }

    public double[] getLine(int j) {
        return listsOfTuples.get(j);
    }


    @Override
    public Iterator<double[]> iterator() {
        return listsOfTuples.iterator();
    }
}
