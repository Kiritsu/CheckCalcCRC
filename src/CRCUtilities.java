import java.util.ArrayList;

public class CRCUtilities {
    /**
     * Encode a message with the following polynomial generator: x^4 + x^2 + x.
     * @param binarySentence Mots à encoder.
     * @return Return a model that contains the CRC, the message and the different calculation steps.
     */
    private static CRCModel encodeCRC(StringBuilder binarySentence) {
        StringBuilder crcFinal = new StringBuilder();
        // Ajout du mot et du polynome générateur
        crcFinal.append(binarySentence).append("0000");

        // Création du polynome générateur
        StringBuilder polynomialGenerator = new StringBuilder();
        polynomialGenerator.append("10110");

        ArrayList<StringBuilder> steps = new ArrayList<>();
        while (crcFinal.length() > 4) {
            steps.add(getStepString(crcFinal, polynomialGenerator));

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

            crcFinal = removeLeftZeroes(tempBinaryString);
        }

        steps.add(getStepString(crcFinal, polynomialGenerator));

        return new CRCModel(steps, binarySentence, crcFinal);
    }

    /**
     * Print the first binary then the polynomial generator.
     * @param binaryString Binary string
     * @param polynomialGenerator Polynomial generator string.
     */
    private static StringBuilder getStepString(StringBuilder binaryString, StringBuilder polynomialGenerator) {
        StringBuilder step = new StringBuilder();
        step.append(binaryString).append('\n')
            .append(polynomialGenerator).append('\n');

        for (int i = 0; i < binaryString.length(); ++i) {
            step.append('-');
        }

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
    private static CRCModel encodeMessage(String message) {
        StringBuilder builder = new StringBuilder();
        for (char c : message.toCharArray()) {
            builder.append(Integer.toBinaryString(c));
        }

        return encodeCRC(builder);
    }

    /**
     * Check whether a given binary message has properly been sent.
     * @param binary Binary message to check.
     */
    private static boolean checkMessage(String binary) {
        CRCModel crc = encodeCRC(new StringBuilder(binary));
        return crc.isValid();
    }

    public static void main(String... args) {
        CRCModel model = encodeMessage("ABC");
        System.out.println("Envoi du message " + model.getMessage());

        System.out.println();
        System.out.println();

        System.out.println("Etapes : ");
        for (StringBuilder builder : model.getSteps()) {
            System.out.print(builder);
        }

        System.out.println();
        System.out.println();

        System.out.println("CRC trouvé : " + model.getCrc());
        System.out.println("Message reçu : " + model.getFullMessage());
        System.out.println("Message reçu est-il valide ? " + checkMessage(model.getFullMessage()));
    }
}
