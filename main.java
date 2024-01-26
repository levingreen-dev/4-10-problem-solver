import java.util.*;

public class test {
    static String[] operators = {"+", "-", "/", "*"};
    private static test FindAllSolutions;

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        System.out.print("Enter numbers: ");
        int[] numbers = Arrays.stream(scanner.nextLine().split("")).mapToInt(Integer::parseInt).toArray();
        System.out.print("Enter wanted result: ");
        float result = Float.parseFloat(scanner.nextLine());
        List<String[]> operatorCombinations = generateOperatorCombinations(numbers.length);

        HashSet<PrimitiveArrayWrapper> permutationNumbers = new HashSet<>();
        createPermutations(numbers.length, numbers, permutationNumbers, new PrimitiveArrayWrapper(numbers));

        int possibleCombinations = permutationNumbers.size() * operatorCombinations.size();
        ArrayList<String> formulas = calculateFormulas(permutationNumbers, operatorCombinations, result);
        formulas.forEach(System.out::println);

        System.out.printf("Out of all %d combinations possible\nof numbers     %s\nwith operators %s\nthese %d combinations give result %d"
                , possibleCombinations
                , Arrays.toString(numbers)
                , Arrays.toString(operators)
                , formulas.size()
                , Math.round(result));
    }


    public static List<String[]> generateOperatorCombinations(int length) {
        List<String[]> combinations = new ArrayList<>();
        generateOperatorCombinationsRecursive(FindAllSolutions.operators.clone(), new String[length-1], 0, combinations);
        return combinations;
    }

    public static void generateOperatorCombinationsRecursive(String[] operators, String[] currentCombination, int index, List<String[]> combinations) {
        if (index == currentCombination.length) {
            combinations.add(currentCombination.clone());
        } else {
            for (String operator : operators) {
                currentCombination[index] = operator;
                generateOperatorCombinationsRecursive(operators, currentCombination, index + 1, combinations);
            }
        }
    }

    private static ArrayList<String> calculateFormulas(HashSet<PrimitiveArrayWrapper> numbers, List<String[]> operators, float result) {
        ArrayList<String> solutions = new ArrayList<>();
        for (PrimitiveArrayWrapper n : numbers) {
            for (String[] o : operators) {
                String formula = returnFormula(n.array(), o);
                if (Math.abs(evaluate(formula) - result) < 0.01) {
                    solutions.add(formula);
                }
            }
        }
        return solutions;
    }

    private static String returnFormula(int[] numbers, String[] operators) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < numbers.length - 1; i++) {
            sb.append(numbers[i]).append(operators[i]);
        }
        sb.append(numbers[numbers.length - 1]);
        return sb.toString();
    }

    private static float evaluate(String expression) {
        ArrayDeque<Float> numberStack = new ArrayDeque<>();
        ArrayDeque<Character> operatorStack = new ArrayDeque<>();
        StringBuilder numBuilder = new StringBuilder();

        for (int i = 0; i < expression.length(); i++) {
            char ch = expression.charAt(i);

            if (Character.isDigit(ch)) {
                numBuilder.delete(0, numBuilder.length());
                numBuilder.append(ch);

                while (i + 1 < expression.length() && Character.isDigit(expression.charAt(i + 1))) {
                    numBuilder.append(expression.charAt(i + 1));
                    i++;
                }

                float number = Float.parseFloat(numBuilder.toString());
                numberStack.push(number);
            } else if (isOperator(ch)) {
                while (!operatorStack.isEmpty() && precedence(operatorStack.peek()) >= precedence(ch)) {
                    processOperation(numberStack, operatorStack);
                }
                operatorStack.push(ch);
            }
        }

        while (!operatorStack.isEmpty()) {
            processOperation(numberStack, operatorStack);
        }

        return numberStack.pop();
    }


    private static void processOperation(ArrayDeque<Float> numberStack, ArrayDeque<Character> operatorStack) {
        char operator = operatorStack.pop();
        float operand2 = numberStack.pop();
        float operand1 = numberStack.pop();

        float result = performOperation(operand1, operand2, operator);
        numberStack.push(result);
    }

    private static float performOperation(float operand1, float operand2, char operator) {
        if (operator == '+') {
            return operand1 + operand2;
        } else if (operator == '-') {
            return operand1 - operand2;
        } else if (operator == '*') {
            return operand1 * operand2;
        } else if (operator == '/') {
            return operand1 / operand2;
        } else {
            throw new IllegalArgumentException("Invalid operator: " + operator);
        }
    }

    private static boolean isOperator(char ch) {
        return ch == '+' || ch == '-' || ch == '*' || ch == '/';
    }

    private static int precedence(char operator) {
        if (operator == '+' || operator == '-') {
            return 1;
        } else if (operator == '*' || operator == '/') {
            return 2;
        } else {
            return 0;
        }
    }


    private static void createPermutations(int n, int[] array, HashSet<PrimitiveArrayWrapper> set, PrimitiveArrayWrapper wrapper) {
        if (n == 1) {
            if (!set.contains(wrapper)) {
                set.add(new PrimitiveArrayWrapper(array.clone()));
            }
        } else {
            for (int i = 0; i < n - 1; i++) {
                createPermutations(n - 1, array, set, wrapper);
                int tmp;
                if ((n & 2) == 0) {
                    tmp = array[i];
                    array[i] = array[n - 1];
                } else {
                    tmp = array[0];
                    array[0] = array[n - 1];
                }
                array[n - 1] = tmp;
            }
            createPermutations(n - 1, array, set, wrapper);
        }
    }
}

record PrimitiveArrayWrapper(int[] array) {
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PrimitiveArrayWrapper that = (PrimitiveArrayWrapper) o;
        return Arrays.equals(array, that.array);
    }

    @Override
    public int hashCode() {
        return Arrays.hashCode(array);
    }
}
