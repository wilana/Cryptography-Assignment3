import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

/**
 *  Assignment 3    COMP 4476
 *  Author:         Wilana Matthews
 *  Last Modified:  Oct. 28, 2021
 */

public class Main {

    private static final ArrayList<Character> alphabetChars = new ArrayList<>();    // store all characters

    public static void main(String[] args) {
        // fill the alphabet array list
        alphabetChars.addAll(Arrays.asList('A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J', 'K', 'L', 'M', 'N',
                'O', 'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z', ' '));

        //Intro documentation
        System.out.println("\nAssignment 3\t\tCOMP 4476");
        System.out.println("Wilana Matthews\t\t1120464");
        System.out.println("----------------------------------------------------\n\n");

        // Problem 1:
        System.out.print("Problem 1: Hash Function\n");
        System.out.print("----------------------------------------------------\n");
        // first attempt
        String input = "abcdefghi jklmnopqrstuvwx";
        System.out.print("Input:\t\t" + input + "\n");
        String output = performHash(input);
        System.out.println("Output:\t\t" + output + "\n");
        // second attempt
        input = "the birthday attack can be performed for any hash functions including sha three";
        System.out.print("Input:\t\t" + input + "\n");
        output = performHash(input);
        System.out.println("Output:\t\t" + output + "\n");

        // Problem 2:
        System.out.print("\nProblem 2: MAC Function\n");
        System.out.print("----------------------------------------------------\n");
        String key = "UDKGNEUDHSKEUFG";     // secret key, 15 english letters
        System.out.print("Input:\t\t" + input + "\n");
        output = performMAC(key, input);
        System.out.println("Output:\t\t" + output + "\n");


        // Problem 3:
        System.out.print("\nProblem 3: Attack Hash Function \n");
        System.out.print("----------------------------------------------------\n");
        attackHash();

    }

    // Problem 1
    /**
     * perform a hash function on the input to condense it to 5 letter hash
     * @param input english phrase (will be made into multiple of 25)
     * @return output in english letters, only 5 letters
     */
    public static String performHash(String input) {
        input = input.toUpperCase();    // make sure the input is all capitalized

        // Initialize all placeholders and values
        int[][] in = new int [5][5];    // to work through the input hashing
        int[] out = {0, 0, 0, 0, 0};    // to calculate the output

        int letterPos = 0;              // iterate through all letters in phrase
        int[][] copyBlock = new int[5][5]; // to copy the whole block into
        int[] copyRow = new int [5];    // to copy rows into

        int[] order = {1, 2, 3, 4, 0};  // order for round 2
        int newIndex;               // to save the new index when shifting

        // pad with space to make sure length is a multiple of 25
        int padding = 25 - (input.length() % 25);
        if (padding != 25) {
            for (int i = 0; i < padding; i++) {
                input = input + " ";
            }
        }

        // divide into blocks of 25
        int numBlocks = input.length() / 25;

        // work through blocks individually
        for (int blocks = 0; blocks < numBlocks; blocks++) {

            // ROUND 1: fill block with numerical equivalent of letter
            for (int r = 0; r < 5; r++){        // row
                for (int c = 0; c < 5; c++) {   // columns
                    // add letter to current block as the corresponding number
                    in [r][c] = alphabetChars.indexOf(input.charAt(letterPos));
                    letterPos++;                        // next round will look at next letter
                    out[c] = (out[c] + in[r][c]) % 27;  // update output, may hold last block's values
                } // columns
            } // rows

            // ROUND 2: shift left i times
            for (int r = 0; r < 5; r++) {       // Rows
                // Copy the row
                copyRow[0] = in[r][0];
                copyRow[1] = in[r][1];
                copyRow[2] = in[r][2];
                copyRow[3] = in[r][3];
                copyRow[4] = in[r][4];

                for (int c = 0; c < 5; c++) {   // Columns
                    newIndex = (c + order[r])%5;            // which index to shift to
                    in[r][c] = copyRow[newIndex];           // shift current index to value at newIndex
                    out[c] = (out[c] + in[r][c]) % 27;      // update output
                    copyBlock[r][c] = in[r][c];             // copy for the next round
                } // columns
            } // rows

            // ROUND 3: shift down i times
            for (int r = 0; r < 5; r++) {       // rows
                for (int c = 0; c < 5; c++) {   // columns
                    newIndex = (r - order[c] + 5)%5;            // which index to shift to
                    in[r][c] = copyBlock[newIndex][c];           // shift current index to value at newIndex
                } // columns
                out[r] = (out[r] + in[r][0] + in[r][1] + in[r][2] + in[r][3] + in[r][4]) % 27;      // update output
            } // rows
        }

        StringBuilder output = new StringBuilder();
        for (int i = 0; i < 5; i++) {
            output.append(alphabetChars.get(out[i]));
        }

        return output.toString();
    }

    /**
     * perform a MAC function using hash function
     * @param key secret key with 15 english characters
     * @param plaintext plaintext in english
     * @return MAC value of plaintext
     */
    public static String performMAC (String key, String plaintext) {
        // concatenate key with plaintext
        String output = key + plaintext;
        // concatenate key with hashed concatenation of key and plaintext
        output = key + performHash(output);
        // hash above concatenation
        output = performHash(output);
        // return final hash
        return output;
    }


    public static void attackHash () {
        char[] plainArray = {'A', 'A', 'A', 'A', 'A', 'A', 'A', 'A', 'A', 'A',
                            'A', 'A', 'A', 'A', 'A', 'A', 'A', 'A', 'A', 'A',
                            'A', 'A', 'A', 'A', 'A'};

        changeAndCheck(0, plainArray, false);
    }

    // HashMap for       key = hashValue, value = plaintext
    private static final HashMap<String, String> outputMap = new HashMap<>();

    /**
     * Change letter and check for matching hash, recursively
     * @param column the column to change the letter in next
     * @param textArray the plaintext as a character array
     * @param matchFound is there a matching hash value in the hash map
     * @return if a matching hash value has been found
     */
    private static boolean changeAndCheck (int column, char[] textArray, boolean matchFound) {
        if (!matchFound) {

            if (column < textArray.length) {        // make sure column is less than the allowed columns

                for (int letter = 0; letter < 27; letter++) {   // iterate through all letters
                    // Change letter in current column
                    textArray[column] = alphabetChars.get(letter);
                    // Change and check all letters in next columns recursively
                    matchFound = changeAndCheck(column + 1, textArray, matchFound);

                    // if those letter changes do not find a match
                    if (!matchFound) {
                        // Check current hash with this column's letter change
                        String plaintext = String.valueOf(textArray);
                        String hashValue = performHash(plaintext);

                        // if there is a matching hash value (key) already calculated
                        if (outputMap.containsKey(hashValue)) {
                            // Print out information
                            System.out.println( plaintext + " and " + outputMap.get(hashValue) + " share the hash " + hashValue);
                            System.out.println("\t->\tThere have been " + outputMap.size() + " plaintexts used to obtain collision");
                            return true;
                        }
                        // Otherwise, no match found, add to the output map
                        else {
                            outputMap.put(hashValue, plaintext);
                        }
                    }
                    // match was found
                    else
                        return true;
                }
            }
        }
        // match was found
        else
            return true;

        return false; // if it hasn't returned yet, the match has not been found
    }
}
