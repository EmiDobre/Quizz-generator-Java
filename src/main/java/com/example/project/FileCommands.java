package com.example.project;

import java.io.*;
import java.util.StringTokenizer;

public class FileCommands {
    public void append_in_file(String file_name, String[] text, int len) {

        FileWriter fwriter = null;
        BufferedWriter bwriter = null;

        //text = text.concat("\n");
        try {
            fwriter=  new FileWriter(file_name, true);
            bwriter = new BufferedWriter(fwriter);

            for ( int i = 0; i < len; i++ ) {
                bwriter.write(text[i]);
                bwriter.newLine();
            }

        } catch ( IOException e ) {
            e.printStackTrace();
        } finally {
            try {
                bwriter.close();
                fwriter.close();

            } catch ( IOException e ) {
                e.printStackTrace();
            }
        }
    }

    public String find_in_file(String file_name, String find, int poz) {
        File file = new File(file_name);
        FileReader freader = null;
        BufferedReader breader = null;
        try {
            freader = new FileReader(file);
            breader = new BufferedReader(freader);
            String line = breader.readLine();

            while ( line != null ) {

                String token = getRightToken(line, poz);
                if (token == null)
                    line = breader.readLine();
                else {
                    if (token.equals(find) == true)
                        return line;
                    line = breader.readLine();
                }
            }

        } catch ( IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if ( breader != null )
                    breader.close();
            } catch ( IOException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    //metoda care parcurge linia si ia tokenul cu care trb verificat
    //stringul de gasit
    public String getRightToken(String line, int poz ) {
        StringTokenizer tokenizer = new StringTokenizer(line, ",");
        String token = new String();
        int i = 0;


            while ( tokenizer.hasMoreTokens() ) {
                token = tokenizer.nextToken();
                if ( i == poz ) {
                    return token;
                }
                i++;
            }

        //pt poz >= 1 caut la raspunsuri val de adevar de ex
        //sau caut id ul la intrebari sau nr de rasp corecte si totale etc

        return null;
    }

    //fct intoarce un string vector cu textul intrebarilor
    //sau cu numele chestionarelor
    //ambele primesc un stirng pe care sa il evite din fisier
    public String[] readAll(String file_name, String avoid ) {
        File file = new File(file_name);
        FileReader freader = null;
        BufferedReader breader = null;
        String[] text = new String[1];
        int nr_quest = 0;

        try {
            freader = new FileReader(file);
            breader = new BufferedReader(freader);
            String line = breader.readLine();
            //linia cu urm intrebare -> pt a scrie corect cu virgula sau nu
            //la ultiml elem

            while ( line != null ) {

                if ( avoid != null ) {
                    if (line.contains(avoid) == true) {
                        line = breader.readLine();
                        continue;
                    }
                }

                int i;
                for ( i = 0; i < text.length; i++ ) {
                    if (text[i] == null)
                        break;
                }
                if ( i == text.length ) {
                    int len = text.length + 1;
                    String[] bigger = new String[len];
                    System.arraycopy(text, 0, bigger, 0, text.length);
                    text = bigger;
                }

                text[i] = line;
                line = breader.readLine();
            }
            return text;

        } catch ( IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if ( breader != null )
                    breader.close();
            } catch ( IOException e) {
                e.printStackTrace();
            }
        }
        return null;
    }


}



