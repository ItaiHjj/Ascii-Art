package ascii_art;

import ascii_art.img_to_char.BrightnessImgCharMatcher;
import ascii_output.AsciiOutput;
import ascii_output.ConsoleAsciiOutput;
import ascii_output.HtmlAsciiOutput;
import image.Image;

import java.util.*;
import java.util.stream.Stream;


/**
 * Takes care on user commands by command line interface
 */
public class Shell {
    // commands
    private static final String CMD_EXIT = "exit";
    private static final String CMD_CHARS_PRINT = "chars";
    private static final String CMD_ADD_CHARS = "add";
    private static final String CMD_ADD_ALL = "all";
    private static final String CMD_ADD_SPACE = "space";
    private static final String CMD_RES = "res";
    private static final String RES_UP_CMD = "up";
    private static final String RES_DOWN_CMD = "down";
    private static final String CMD_CONSOLE = "console";
    private static final String CMD_RENDER = "render";
    private static final String CMD_REMOVE_CHARS = "remove";

    // Messages
    private static final String FORMAT_ADD_ERR = "Did not add due to incorrect format";
    private static final String FORMAT_REMOVE_ERR = "Did not remove due to incorrect format";
    private static final String INCORRECT_CMD_ERR = "Did not executed due to incorrect command";
    private static final String RES_SET_MSG = "Width set to ";
    private static final String RES_NOT_UPDATED_MSG = "Did not change due to exceeding boundaries";

    // Different constants
    private static final int ASCII_FIRST_CHAR = 32;
    private static final int ASCII_END_CHAR = 126;
    private static final String DEF_INIT_CHARS = "0-9";
    private static final int MIN_PIXELS_PER_CHAR = 2;
    private static final int INITIAL_CHARS_IN_ROW = 64;
    private static final String FONT_NAME = "Courier New";
    private static final String OUTPUT_FILENAME = "out.html";

    private final int minCharsInRow;
    private final int maxCharsInRow;
    private int charsInRow;
    private final BrightnessImgCharMatcher charMatcher;
    private AsciiOutput output;

    private final Set<String> cmdSet = new HashSet<>();
    private final Set<Character> charSet = new HashSet<>();

    /**
     * Construction of user interface
     *
     * @param img The image to work on
     */
    public Shell(Image img) {
        Collections.addAll(this.cmdSet, CMD_EXIT, CMD_CHARS_PRINT, CMD_REMOVE_CHARS,
                CMD_ADD_CHARS, CMD_RES, CMD_CONSOLE, CMD_RENDER);
        addRemoveChars(DEF_INIT_CHARS, CMD_ADD_CHARS);
        this.minCharsInRow = Math.max(1, img.getWidth() / img.getHeight());
        this.maxCharsInRow = img.getWidth() / MIN_PIXELS_PER_CHAR;
        this.charsInRow = Math.max(Math.min(INITIAL_CHARS_IN_ROW, maxCharsInRow), minCharsInRow);
        this.charMatcher = new BrightnessImgCharMatcher(img, FONT_NAME);
        this.output = new HtmlAsciiOutput(OUTPUT_FILENAME, FONT_NAME);
    }

    /**
     * Run the whole user interface - Ask for input from user and choose the cmd
     * If the input is not valid also take care of that
     * Commands:
     * exit - for exit the program
     * add ch/ add ch1-ch2/ add ch2-ch1 / all / space - Its add ch/ range ch1-ch2 / range ch2-ch1 / all chars
     * / space char
     * remove - Syntax is same as add - this given chars.
     * chars - prints the chars that been chosen
     * res up/ res down - makes res higher/lower
     * console - Change the render to output console
     * render - rendering the image as ascii art
     */
    public void run() {
        while (true) {
            Scanner scanner = new Scanner(System.in);
            System.out.print(">>> ");
            String cmd = scanner.nextLine().trim();
            String[] words = cmd.split("\\s+");

            if (inputFormatNotValid(words)) {
                printError(words[0]);
                continue;
            }

            switch (words[0]) {
                case CMD_ADD_CHARS:
                    addRemoveChars(words[1], CMD_ADD_CHARS);
                    break;
                case CMD_REMOVE_CHARS:
                    addRemoveChars(words[1], CMD_REMOVE_CHARS);
                    break;
                case CMD_CHARS_PRINT:
                    printCharsSet();
                    break;
                case CMD_RES:
                    resChange(words[1]);
                    break;
                case CMD_CONSOLE:
                    changeOutputToConsole();
                    break;
                case CMD_RENDER:
                    render();
                    break;
                case CMD_EXIT:
                    return;
            }
        }
    }

    // Change the way of output from html to console
    private void changeOutputToConsole() {
        this.output = new ConsoleAsciiOutput();
    }

    // Checks input is valid - this function takes care of different case hence a lot of not nice if's
    private boolean inputFormatNotValid(String[] words) {
        if (words[0].equals("") || !this.cmdSet.contains(words[0])) {
            return true;
        }

        if (words[0].equals(CMD_ADD_CHARS) || words[0].equals(CMD_REMOVE_CHARS)) {
            return ((words.length == 1) || (words.length > 2)) || !words[1].equals(CMD_ADD_ALL) &&
                    !words[1].equals(CMD_ADD_SPACE) && !words[1].matches("^[!-~][-][!-~]|[!-~]");
        }

        if (words[0].equals(CMD_EXIT) && 1 < words.length) {
            words[0] = " ";
            return true;
        }
        if (words[0].equals(CMD_CHARS_PRINT)) {
            return 1 < words.length;
        }

        if (words[0].equals(CMD_RES)) {
            return (2 < words.length || words.length == 1) ||
                    (!words[1].equals(RES_UP_CMD) && !words[1].equals(RES_DOWN_CMD));
        }
        return false;
    }

    // Printing Error messages
    private void printError(String cmd) {
        switch (cmd) {
            case CMD_ADD_CHARS:
                System.out.println(FORMAT_ADD_ERR);
                break;
            case CMD_REMOVE_CHARS:
                System.out.println(FORMAT_REMOVE_ERR);
                break;
            default:
                System.out.println(INCORRECT_CMD_ERR);
        }
    }

    // Printing to console the character the user choose
    private void printCharsSet() {
        charSet.stream().sorted().forEach(c -> System.out.print(c + " "));
        System.out.println();
    }

    // Add/Remove chars the user want to
    private void addRemoveChars(String param, String cmd) {
        char[] range = parseCharRange(param);
        if (cmd.equals(CMD_ADD_CHARS)) {
            Stream.iterate(range[0], c -> c <= range[1], c -> (char) ((int) c + 1))
                    .forEach(this.charSet::add);
        }
        if (cmd.equals(CMD_REMOVE_CHARS)) {
            Stream.iterate(range[0], c -> c <= range[1], c -> (char) ((int) c + 1))
                    .forEach(this.charSet::remove);
        }
    }

    // Get the user parameter of which chars to add and remove and return an array
    // of their range.  i.e. for param "z-a" will return: {'a','z'}
    private char[] parseCharRange(String param) {
        if (param.equals(CMD_ADD_SPACE)) {
            return new char[]{' ', ' '};
        }
        if (param.equals(CMD_ADD_ALL)) {
            return new char[]{ASCII_FIRST_CHAR, ASCII_END_CHAR};
        }
        if (param.length() == 1) { // Case of 1 char
            return new char[]{param.charAt(0), param.charAt(0)};
        }
        if (param.charAt(2) < param.charAt(0)) {
            return new char[]{param.charAt(2), param.charAt(0)};
        }
        return new char[]{param.charAt(0), param.charAt(2)};
    }

    // Change the resolution of image to ascii
    private void resChange(String param) {
        if (param.equals(RES_UP_CMD)) {
            if (this.charsInRow == this.maxCharsInRow) {
                System.out.println(RES_NOT_UPDATED_MSG);
                return;
            }
            this.charsInRow = 2 * this.charsInRow;
        }
        if (param.equals(RES_DOWN_CMD)) {
            if (this.charsInRow == this.minCharsInRow) {
                System.out.println(RES_NOT_UPDATED_MSG);
                return;
            }
            this.charsInRow = this.charsInRow / 2;
        }
        System.out.println(RES_SET_MSG + this.charsInRow);
    }

    // Render the output
    private void render() {
        Character[] charSetArr = charSet.toArray(new Character[0]);
        char[][] chars = this.charMatcher.chooseChars(this.charsInRow, charSetArr);
        this.output.output(chars);
    }
}

