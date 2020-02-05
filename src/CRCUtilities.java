import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;

@SuppressWarnings("Duplicates")
public class CRCUtilities {
    /**
     * Encode a message with the following polynomial generator: x^4 + x^2 + x.
     * @param binarySentence Words to encode.
     * @param polynomialGenerator Polynomial generator to use.
     * @return Return a model that contains the CRC, the message and the different calculation steps.
     */
    private static CRCModel encodeCRC(StringBuilder binarySentence, StringBuilder polynomialGenerator) {
        StringBuilder crcFinal = new StringBuilder();
        // Ajout du mot et du polynome générateur
        crcFinal.append(binarySentence);
        crcFinal.append("0".repeat(polynomialGenerator.length() - 1));

        int offset = 0;
        ArrayList<StringBuilder> steps = new ArrayList<>();
        while (crcFinal.length() > 4) {
            steps.add(getStepString(crcFinal, polynomialGenerator, offset));

            // calcul avec le polynome générateur :
            StringBuilder tempBinaryString = new StringBuilder();
            for (int i = 0; i < 5; ++i) {
                String c1 = String.valueOf(crcFinal.charAt(i));
                String c2 = String.valueOf(polynomialGenerator.charAt(i));

                tempBinaryString.insert(i, binaryAdd(c1, c2));
            }

            // on ajoute le reste du message initial
            for (int i = 5; i < crcFinal.length(); ++i) {
                tempBinaryString.insert(i, crcFinal.charAt(i));
            }

            int length = crcFinal.length();
            crcFinal = removeLeftZeroes(tempBinaryString);
            offset += (length - crcFinal.length());
        }

        steps.add(getStepString(crcFinal, polynomialGenerator, offset));

        return new CRCModel(steps, binarySentence, crcFinal);
    }

    /**
     * Print the first binary then the polynomial generator.
     * @param binaryString Binary string
     * @param polynomialGenerator Polynomial generator string.
     */
    private static StringBuilder getStepString(StringBuilder binaryString, StringBuilder polynomialGenerator, int offset) {
        StringBuilder step = new StringBuilder();
        step.append(" ".repeat(Math.max(0, offset)));
        step.append(binaryString).append('\n');
        step.append(" ".repeat(Math.max(0, offset)));
        step.append(polynomialGenerator).append('\n');

        step.append("-".repeat(Math.max(0, binaryString.length() + offset)));

        step.append('\n');
        return step;
    }

    /**
     * Trim left the zeroes of a StringBuilder.
     * @param tempBinaryString StringBuilder to mutate.
     */
    private static StringBuilder removeLeftZeroes(StringBuilder tempBinaryString) {
        StringBuilder builder = new StringBuilder();
        boolean ignoreZeroes = true;

        for (int i = 0; i < tempBinaryString.length(); ++i) {
            if (tempBinaryString.charAt(i) == '1') {
                ignoreZeroes = false;
                builder.append('1');
            } else {
                if (!ignoreZeroes) {
                    builder.append('0');
                }
            }
        }

        return builder;
    }

    /**
     * Return 0 when left and right are equals, else 1.
     * @param left Left operand.
     * @param right Right operand.
     */
    private static String binaryAdd(String left, String right) {
        return left.equals(right) ? "0" : "1";
    }

    /**
     * Encode a message with CRC from the given literal string.
     * @param message Message to encode.
     */
    private static CRCModel encodeMessage(String message, String generator) {
        StringBuilder builder = new StringBuilder();
        for (char c : message.toCharArray()) {
            builder.append(Integer.toBinaryString(Character.toUpperCase(c)));
        }

        return encodeCRC(builder, new StringBuilder(generator));
    }

    /**
     * Check whether a given binary message has properly been sent.
     * @param binary Binary message to check.
     */
    private static CRCModel checkMessage(String binary, String generator) {
        return encodeCRC(new StringBuilder(binary), new StringBuilder(generator));
    }

    public static void main(String... args) throws IOException {
        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
        String lastCommand;
        do {
            System.out.println("=== TP2 Réseau | Allan Mercou | CRC ===");
            System.out.println("> [Q]uit: quitte le programme");
            System.out.println("> [E]ncode: permet d'encoder un message avec le polynome générateur de son choix.");
            System.out.println("> [C]heck: permet de vérifier que le message envoyé ne contient pas d'erreurs.");
            System.out.println("Entrez votre choix.");

            lastCommand = reader.readLine();
            switch (lastCommand.toLowerCase()) {
                case "q":
                case "quit":
                    break;
                case "e":
                case "encode": {
                    System.out.println("Entrez le message à encoder :");
                    String message = reader.readLine();

                    System.out.println("Entrez le polynome générateur à utiliser :");
                    String polynome = reader.readLine();

                    CRCModel model = encodeMessage(message, polynome);
                    System.out.println("Message initial : " + model.getMessage());

                    System.out.println();
                    System.out.println();

                    System.out.println("Etapes : ");
                    for (StringBuilder builder : model.getSteps()) {
                        System.out.print(builder);
                    }

                    System.out.println();
                    System.out.println();

                    System.out.println("CRC trouvé : " + model.getCrc());
                    System.out.println("Message à envoyer : " + model.getFullMessage());
                    break;
                }
                case "c":
                case "check": {
                    System.out.println("Entrez le message à vérifier :");
                    String message = reader.readLine();

                    System.out.println("Entrez le polynome générateur à utiliser :");
                    String polynome = reader.readLine();

                    CRCModel model = checkMessage(message, polynome);

                    System.out.println();
                    System.out.println();

                    System.out.println("Etapes : ");
                    for (StringBuilder builder : model.getSteps()) {
                        System.out.print(builder);
                    }

                    System.out.println();
                    System.out.println();

                    System.out.println("Le message reçu est " + (model.isValid() ? "valide" : "invalide") + " !");
                    break;
                }
            }

            System.out.println();
            System.out.println();
            System.out.println();
        } while (!lastCommand.equals("quit") && !lastCommand.equals("q"));
    }
}
