package com.example.project;

import java.io.*;
import java.util.StringTokenizer;
import java.math.BigDecimal;

public class ParserBase {
    protected User user;
    protected Question question;
    protected Quizz quizz;
    protected int nr_correct;
    protected int count;
    protected String[] args;

    protected FileCommands command;
    public ParserBase(int count, String[] args) {
        this.count = count;
        this.args = args;
        this.command = new FileCommands();
    }


    //fct de baza:
    public char authentication() {

        if ( count == 1 )
            return 'u';

        if ( count == 2 ) {
            if (args[1].charAt(1) != 'u')
                return 'u';

            return 'p';
        }

        if (count >=3 ) {
            if (args[2].charAt(1) != 'p' )
                return 'p';
        }

        //pot initializa userul
        return '0';
    }

    public int checkLogin() {
        //user/parola gresite
        //find in file intoarce linia pe care s a gasit elementul
        //prima data caut userul sa vad daca exista apoi vf parola pe acea linie
        String line = command.find_in_file("users.csv", user.getName(), 0);
        if (  line == null )
            return 0;
        else {
            int len = user.getName().length();
            len = len + 2; //dupa nume urmeaza , si spatiu apoi parola
            String pasword = line.substring(len - 1);
            //System.out.println("parolaa: " + pasword + "ce am eu " + user.getPassword() + "linia " + line);

            if ( pasword.equals(user.getPassword()) == false )
                return 0;
        }

        //login a mers
        return 1;
    }

    //tratarea tuturor tipurilor de erori
    public int checkUserErrors() {
        //lipsa -u/-p
        if ( authentication() == 'u' ) {
            System.out.println("{'status':'error','message':'You need to be authenticated'}");
            return 0;
        }

        if ( authentication() == 'p' ) {
            System.out.println("{'status':'error','message':'You need to be authenticated'}");
            return 0;
        }
        return 1;

    }

    public int checkQuestionErr() {

        //erori din lipsa de text
        if ( count >= 4 ) {
            boolean ok = args[3].contains("-text");
            if ( ok == false ) {
                System.out.println("{ 'status' : 'error', 'message' : 'No question text provided'}");
                return 0;
            }
            if ( ok == true ) {
                if ( args[3].length() <= 6 ) {
                    System.out.println("{ 'status' : 'error', 'message' : 'No question text provided'}");
                    return 0;
                }
            }
        }

        //caz in plus: nu este mentionat tipul:
        if ( count >= 5 ) {
            boolean ok = args[4].contains("-type");
            if ( ok == false )
                return 0;

            if ( count == 5 ) {
                System.out.println("{ 'status' : 'error', 'message' : 'No answer provided'}");
                return 0;
            }
        }

        //erori de la raspunsuri:
        if( count >= 6 && count < 15 ) {

            //rasp apartin intervalului corect aici:

            int len = args[4].length();
            int indx = 5;
            String type;
            type = String.copyValueOf(args[4].toCharArray(),indx, len-indx );
            boolean single = args[4].contains("single");

            if ( answearErrors(single) == 0 )
                return 0;

        }

        if ( count >= 15 ) {
            System.out.println("{ 'status' : 'error', 'message' : 'More than 5 answers " +
                    "were submitted'}");
            return 0;
        }

        return 1;
    }

    public int answearErrors( boolean single ) {
        nr_correct = correctAns();

        //nr rasp mai mare sau mai mic decat trebuia:
        if ( single == true ) {
            if ( nr_correct > 1 ) {
                System.out.println("{ 'status' : 'error', 'message' : 'Single correct answer question " +
                        "has more than one correct answer'}");
                return 0;
            }

            //am doar un rasp corect dar poate nu mai exista alte rasp date
            if ( nr_correct == 1 ) {
                if ( count <= 7 ) {
                    System.out.println("{ 'status' : 'error', 'message' : 'Only one answer provided'}");
                    return 0;
                }

            }

        } else {
            //multiple dar doar un sg raspuns:
            if ( nr_correct == 1 )
                return 0;

        }

        //s au gasit erori de textul/flagul raspunsurilor:
        if ( nr_correct < 0 )
            return 0;


        return 1;

    }

    //fct numara cate rasp corecte sunt
    //daca nu gaseste is-correct va intoarce -1

    public int correctAns() {
        int i = 6;
        int nr_correct = 0;
        int nr_ans = 1;
        String is_correct = args[i];

        ////check primul rasp pt text si flag
        String answer = args[i-1];
        if ( answer.contains("is-correct") == true || answer.length() <= 11 ) {
            System.out.println("{ 'status' : 'error', 'message' : 'Answer " + nr_ans +
                            " has no answer description'}");
            return -1;
            //eroarea poate aparea si daca nu scrie nimic dupa '..'
        }

        int len = is_correct.length();
        if ( len <= 22 ) {
            System.out.println("{ 'status' : 'error', 'message' : 'Answer " + nr_ans +
                    " has no answer correct flag'}");
            return -1;
        }

        while ( i < count ) {
            nr_ans++;
            len = is_correct.length();

            if ( len <= 22 ) {
                System.out.println("{ 'status' : 'error', 'message' : 'Answer " + nr_ans +
                        " has no answer correct flag'}");
                return -1;
            }

            if ( is_correct.charAt(len - 2) == '1' )
                nr_correct++;

            i = i + 2;
            if ( i > count )
                break;
            is_correct = args[i];

            answer = args[i-1];
            if ( answer.contains("is-correct") == true || answer.length() <= 11 ) {
                System.out.println("{ 'status' : 'error', 'message' : 'Answer " + nr_ans +
                        " has no answer description'}");
                return -1;
            }

        }

        return nr_correct;
    }

    public int checkQuizzErr() {

        if ( count >= 14 ) {
            System.out.println("{ 'status' : 'error', 'message' : 'Quizz has more than 10 " +
                    "questions'}");
            return 0;
        }

        //de la args[4] pana la args[count-1] am textul
        //intrebarilor
        int start = 4;
        int i;
        for ( i = start; i < count; i++ ) {
            int index = args[i].indexOf("'");
            int stop = args[i].length();
            String id = args[i].substring(index + 1, stop-1);

            index = args[i].indexOf("n");
            index++;
            stop = args[i].indexOf("'");
            String nr_quest = args[i].substring(index+1, stop );

            //id_result -> linia pe care s a gasit id ul
            String id_result = command.find_in_file("questions.csv", id,3);
            if ( id_result == null ) {
                System.out.println("{ 'status' : 'error', 'message' : 'Question ID for question " +
                        nr_quest + "does not exist'}");
                return 0;
            }
        }
        return 1;
    }

    //daca totul e ok si id ul exista=>intoarce linia pe care s a gasit
    public String findQuizzIdErr() {
        if ( count < 4 ) {
            System.out.println("{ 'status' : 'error', 'message' : 'No quizz identifier was provided'}");
            return null;
        }

        int start = args[3].indexOf("'");
        int len = args[3].length();
        String quizzId = args[3].substring(start+1, len-1);

        //1)vf daca exista quizzul
        File file = new File("quizz.csv");
        if ( file.exists() == false ) {
            System.out.println("{ 'status' : 'error', 'message' : 'No quiz was found'}");
            return null;
        }
        String lineId = command.find_in_file("quizz.csv", quizzId, 3);
        if ( lineId == null ) {
            System.out.println("{ 'status' : 'error', 'message' : 'No quiz was found'}");
            return null;
        }

        return lineId;
    }
    public int checkSubmitErr() {

        //1)arg nu exista sau id ul
        String lineId = findQuizzIdErr();
        if ( lineId == null )
            return 0;

        int start = args[3].indexOf("'");
        int len = args[3].length();
        String quizzId = args[3].substring(start+1, len-1);

        //2)quizzul exista  - vf daca este printre cele submise
        File file2 = new File("submit.csv");
        if ( file2.exists() == true ) {
            if ( command.find_in_file("submit.csv", quizzId, 3) != null ) {
                System.out.println("{ 'status' : 'error', 'message' : 'You already submitted this " +
                        "quizz'}");
                return 0;
            }
        }

        //3)quizul nu a fost submis + vf userul care vrea sa submita
        //lineUser poate fi null daca userul nu a creat quizzuri
        String lineUser = command.find_in_file("quizz.csv", user.getName(), 0);
        if ( lineUser != null ) {
            if (lineUser.equals(lineId) == true) {
                System.out.println("{ 'status' : 'error', 'message' : 'You cannot answer your " +
                        "own quizz'}");
                return 0;
            }
        }

        return 1;
    }


    public float submitAnswer(String correct, Question currentQ, float score_perQ) {

        int nrCorrect = currentQ.getNr_correct();
        int nrTotal = currentQ.getNr_ans();

        //numar cate intrebari corecte amgasit
        float share_plus = 1f/nrCorrect;
        float share_minus = 1f/(nrTotal - nrCorrect);


        if ( correct.equals(new String("correct")) == true )
            score_perQ = score_perQ + share_plus;
        else
            score_perQ = score_perQ - share_minus;

        return score_perQ;
    }

    public void setScore(float score, String line) {

        //rotunjesc rezultatul
        score = BigDecimal.valueOf(score).
                setScale(2, BigDecimal.ROUND_HALF_DOWN).floatValue();
        score = score * 100;
        int points;
        if ( score <= 0 ) {
            score = 0f;
            points = 0;
        }
        else
            points = (int)score;

        System.out.println("{ 'status' : 'ok', 'message' : '" + points + " points'}");

        //din linia ce descrie quizzul ce a fost completat
        //pastrez numele quizzului cu id dul si in plus in loc de userul de pe linia
        //quizzzului aaug userul care a dat completat quizzul
        String pointsStr = Integer.toString(points);
        line = line.concat( "," + pointsStr);

        String restLine = line.substring(line.indexOf(","));
        String submitLine = user.getName();
        submitLine = submitLine.concat(restLine);

        String[] submit_info = new String[1];
        submit_info[0] = submitLine;
        command.append_in_file("submit.csv", submit_info, 1 );
    }



}
