
package org.qo.tinymsg;

public class ColorfulText {
        // ANSI escape codes for different colors
        public static final String RESET = "\u001B[0m";
        public static final String RED = "\u001B[31m";
        public static final String GREEN = "\u001B[32m";
        public static final String YELLOW = "\u001B[33m";
        public static final String BLUE = "\u001B[34m";
        public static final String MAGENTA = "\u001B[35m";
        public static final String CYAN = "\u001B[36m";
        public static void test (){
                System.out.println(RED + "RED" + GREEN + "GREEN" + YELLOW + "YELLOW" + BLUE + "BLUE" + MAGENTA + "MAGENTA" + CYAN + "CYAN");
        }
        public static final String LOGO = """
                 ______ _             __  ___ ____ _____
                /_  __/(_)___  __ __ /  |/  // __// ___/
                 / /  / // _ \\/ // // /|_/ /_\\ \\ / (_ /
                /_/  /_//_//_/\\_, //_/  /_//___/ \\___/
                             /___/
                """;
}