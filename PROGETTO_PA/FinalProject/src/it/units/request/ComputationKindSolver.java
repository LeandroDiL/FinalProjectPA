package it.units.request;

import it.units.exception.NoResultFoundException;
import it.units.exception.VariableNotFoundException;
import it.units.expression.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ComputationKindSolver {
    private final ValuesKindSolver variables;
    private final String[] equations;

    public ComputationKindSolver(String[] equations, ValuesKindSolver variables) {
        this.variables = variables;
        this.equations = equations;
    }

    public double calculateResult(ComputationKind computationRequest) throws VariableNotFoundException, NoResultFoundException {
        double[][] solvedArray = new double[equations.length][variables.length()];
        for (int i = 0; i < equations.length; i++) {
            solvedArray[i] = solve(equations[i]);
        }

        return switch (computationRequest) {
            case MIN -> findMin(solvedArray);
            case MAX -> findMax(solvedArray);
            case AVG -> findAverage(solvedArray[0]);
            default -> Double.NaN;
        };
    }

    private double findMin(double[][] arrayOfResults) throws NoResultFoundException {
        List<Double> listOfAllResults = new ArrayList();
        for (double[] arrayOfResult : arrayOfResults) {
            if (arrayOfResult.length != 0) {
                for (double v : arrayOfResult) {
                    listOfAllResults.add(v);
                }
            }
        }
        if (listOfAllResults.size() == 0)
            throw new NoResultFoundException("All the points are out of the domain of the function");
        double[] arrayOfAllResults = getArrayFromList(listOfAllResults);
        Arrays.sort(arrayOfAllResults);
        return arrayOfAllResults[0];
    }

    private double findMax(double[][] arrayOfResults) throws NoResultFoundException {
        List<Double> listOfAllResults = new ArrayList();
        for (double[] arrayOfResult : arrayOfResults) {
            if (arrayOfResult.length != 0) {
                for (double v : arrayOfResult) {
                    listOfAllResults.add(v);
                }
            }
        }
        if (listOfAllResults.size() == 0)
            throw new NoResultFoundException("All the points are out of the domain of the function");
        double[] arrayOfAllResults = getArrayFromList(listOfAllResults);
        Arrays.sort(arrayOfAllResults);
        return arrayOfAllResults[arrayOfAllResults.length - 1];
    }

    private double findCount() {
        return variables.length();
    }

    private double findAverage(double[] result) {
        return Arrays.stream(result).sum() / result.length;
    }

    private static double[] getArrayFromList(List<Double> values) {
        double[] elements = new double[values.size()];
        for (int i = 0; i < values.size(); i++) {
            elements[i] = values.get(i);
        }
        return elements;
    }

    private double[] solve(String equation) throws VariableNotFoundException {
        Parser parser = new Parser(equation);
        Node rootNode = parser.parse();
        List<Double> result = new ArrayList<>();

        if (rootNode instanceof Variable) {
            for (double[] tuples : variables) {
                result.add(solveVariableCase((Variable) rootNode, variables.getVariableNames(), tuples));
            }
        } else if (rootNode instanceof Constant) {
            for (int i = 0; i < variables.length(); i++) {
                result.add(((Constant) rootNode).getValue());
            }
        } else {
            for (int i = 0, j = 0; j < variables.length(); j++, i++) {
                double[] arr = new double[2];
                try {
                    result.add(solveNodeCase(rootNode, arr, variables.getVariableNames(), variables.getLine(j)));
                } catch (ArithmeticException e) {
                    i -= 1;
                }
            }
        }

        if (result.isEmpty()) {
            return new double[0];
        }
        return getArrayFromList(result);
    }

    private double solveNodeCase(Node node, double[] result, String[] variableName, double[] variableValue) throws VariableNotFoundException, ArithmeticException {
        for (int i = 0; i < node.getChildren().size(); i++) {
            if (node.getChildren().get(i) instanceof Constant) {
                result[i] = Double.parseDouble(node.getChildren().get(i).toString());
            } else if (node.getChildren().get(i) instanceof Operator) {
                double[] array = new double[2];
                result[i] = solveNodeCase(node.getChildren().get(i), array, variableName, variableValue);
            } else if (node.getChildren().get(i) instanceof Variable) {
                result[i] = solveVariableCase((Variable) node.getChildren().get(i), variableName, variableValue);
            }
        }
        if (((Operator) node).getType() == Operator.Type.DIVISION && result[1] == 0) {
            throw new ArithmeticException("Divided by zero");
        }
        return ((Operator) node).getType().getFunction().apply(result);
    }

    private double solveVariableCase(Variable variableNode, String[] variableName, double[] variableValue) throws VariableNotFoundException {
        for (int j = 0; j < variableName.length; j++) {
            if (variableNode.getName().equals(variableName[j])) {
                return variableValue[j];
            }
        }
        throw new VariableNotFoundException(String.format("Variable %s not found", variableNode.getName()));
    }
}
