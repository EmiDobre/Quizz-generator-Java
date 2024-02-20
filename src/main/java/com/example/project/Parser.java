package com.example.project;

import java.io.File;

public class Parser extends ParserBase {

    private String action;

    public Parser(int count, String[] args) {
        super(count, args);
    }

    public void parseArgs() {

        if ( count >= 1)
            this.action = args[0];
        else
            this.action = null;

        //de user voi avea mereu nevoie la toate comenzile
        if (authentication() == '0') {
            user = new User(args[1].substring(3),
                    args[2].substring(3));

        }

        //init intrebarea sau chestionarul in fct de argumentul 4 args
        if ( count >= 4 ) {
            int len = args[3].length();
            int indx = 5;

            if (args[3].contains("text") == true) {
                String textfull = String.copyValueOf(args[3].toCharArray(), indx, len - indx);
                String text = textfull.substring(2, textfull.length() - 1);

                question = new Question(text);

                //intrebarea primeste si tipul in parser
                if (count >= 5) {
                    len = args[4].length();
                    indx = 5;
                    String type = String.copyValueOf(args[4].toCharArray(), indx, len - indx);
                    question.setType(type);
                }
            } else {
                if (args[3].contains("name") == true) {
                    parseQuizz();
                }
            }
        }
    }

    public void parseQuizz() {
        int len = args[3].length();
        int indx = 5;


        String textfull = String.copyValueOf(args[3].toCharArray(), indx, len - indx);
        String text = textfull.substring(2, textfull.length() - 1);

        quizz = new Quizz(user, text);

        //nu initializez campul de raspunsuri
        //pana nu verific erorile
    }


    public void checkAct() {

        if ( action == null )
            return;

        if ( action == "-cleanup-all" ) {
            cleanup_all();

            return;
        }

        if ( action.equals(new String("-create-user")) == true ) {
            if ( authentication() == 'u' ) {
                System.out.println("{'status':'error','message':'Please provide username'}");
                return;
            }

            if ( authentication() == 'p' ) {
                System.out.println("{'status':'error','message':'Please provide password'}");
                return;
            }

            create_user();
            return;
        }

        //check la erori:
        if ( checkUserErrors() == 0 )
            return;
        if ( checkLogin() == 0) {
            System.out.println("{ 'status' : 'error', 'message' : 'Login failed'}");
            return;
        }

        if ( action.equals(new String("-create-question")) == true  ) {
            create_question();
        }
        if ( action.equals(new String("-get-question-id-by-text")) == true ) {
            get_question_id();
        }

        if ( action.equals(new String("-get-all-questions")) == true ) {
            get_all_quest();
        }

        if ( action.equals(new String("-create-quizz")) == true ) {
            create_quizz();
        }

        if ( action.equals(new String("-get-quizz-by-name")) == true ) {
            get_quizzByName();
        }

        if (  action.equals(new String("-get-all-quizzes")) == true ) {
            get_all_quizz();
        }

        if ( action.equals(new String("-get-quizz-details-by-id")) == true ) {
            get_quizzDetails();
        }

        if ( action.equals(new String("-submit-quizz")) == true ) {
            submit_quizz();
        }

        if ( action.equals(new String("-delete-quizz-by-id")) == true ) {
            deleteQuizzById();
        }

        if ( action.equals(new String("-get-my-solutions")) == true ) {
            getMySolutions();
        }
    }

    public void cleanup_all() {
        //sterge toate fis
        File file = new File("users.csv");
        if (file.delete()) {
            System.out.print("{ 'status' : 'ok', 'message' : 'Cleanup finished successfully'}");
        }

        File file2 = new File("questions.csv");
        if (file2.delete()) {
            if ( question != null )
            System.out.print("{ 'status' : 'ok', 'message' : 'Cleanup finished successfully'}");
        }

        File file3 = new File("quizz.csv");
        if (file3.delete()) {
            if ( question != null )
                System.out.print("{ 'status' : 'ok', 'message' : 'Cleanup finished successfully'}");
        }

        File file4 = new File("submit.csv");
        if (file4.delete()) {
            if ( question != null )
                System.out.print("{ 'status' : 'ok', 'message' : 'Cleanup finished successfully'}");
        }

        File file5 = new File("answers.csv");
        if (file5.delete()) {
            if ( question != null )
                System.out.print("{ 'status' : 'ok', 'message' : 'Cleanup finished successfully'}");
        }
    }

    public void create_user() {

        String[] user_info = new String[1];
        user_info[0] = user.toString();

        //vf daca fis exista sau nu
        File file = new File("users.csv");
        if ( file.exists() == false ) {
            command.append_in_file("users.csv", user_info, 1);
            System.out.println("{ 'status' : 'ok', 'message' : 'User created successfully'}");
            return;
        }

        //poz este indicele tokenului de pe linia de verificat
        if ( command.find_in_file("users.csv", user.getName(), 0) != null ) {
            System.out.println("{ 'status' : 'error', 'message' : 'User already exists'}");
            return;
        } else {
            command.append_in_file("users.csv", user_info, 1);
            System.out.println("{ 'status' : 'ok', 'message' : 'User created successfully'}");
        }
    }

    public void create_question () {

        if ( checkQuestionErr() == 1 ) {
            //nu am erori si pot crea o intrebare cu rapsunsurile aferente

            //de vf duplicat inainte de scriere in fis si init raspunsuri
            int start_idx = 5;
            int len = args.length;
            len = len - start_idx;
            String[] ans_input = new String[len];
            System.arraycopy(args, start_idx, ans_input, 0, len );

            if ( question.checkDuplicates(ans_input) == 0 )
                return;

            question.initAnswers(ans_input, nr_correct);

            File file = new File("questions.csv");

            if ( file.exists() == false ) {
                write(question);
                System.out.println("{'status':'ok','message':'Question added successfully'}");
                return;
            }

            //vf daca exista deja intrebarea cu acel text-> textul se afla pe prima poz
            if ( command.find_in_file("questions.csv", question.getText(), 0) != null ) {
                System.out.println("{'status':'error','message':'Question already exists'}");
            } else {
                write(question);
                System.out.println("{'status':'ok','message':'Question added successfully'}");
            }
        }
    }

    //id ul se seteaza doar la crearea unei noi intrebari
    public void write(Object object ) {

        if ( object instanceof Question ) {

            //id ul-> pe prima linie din info despre intrb
            int id = generateId("questions.csv", "answer");

            //aflu id ul real:
            String[] answers = ((Question) object).getAnswers();
            for (int  i = 0; i < answers.length; i++ ) {
                if ( answers[i] == null )
                    break;
                //aflu id ul real;
                int realId = findRealId(answers[i]);

                //modific liniile la answer : pun answer-id real
                question.RealAnsId(id, realId, i);
            }

            String[] quest_info = question.toStringArray();
            int len = quest_info.length;
            quest_info[0] = quest_info[0].concat("," + Integer.toString(id));

            command.append_in_file("questions.csv", quest_info, len);
        }

        if ( object instanceof Quizz ) {
            String[] quizz_info = quizz.toStringArray();
            int len = quizz_info.length;

            //id ul-> pe prima linie din info despre intrb
            int id = generateId("quizz.csv", "question");
            quizz_info[0] = quizz_info[0].concat(","+ Integer.toString(id));

            command.append_in_file("quizz.csv", quizz_info, len );
        }

    }

    //id ul urmator este dimensiunea array ului de intrebari + 1
    //id ul real pt raspunsuri se afla la fel
    public int generateId(String fileName, String avoid) {
        File file = new File(fileName);
        if ( file.exists() == false ) {
            return 1;
        }

        //toate intrebarile din fisier sunt in textFile string
        String[] textFile = command.readAll(fileName, avoid);
        return textFile.length + 1;
    }

    //answer info cu id ul initial
    public int findRealId(String answer) {
        String[] answerInit = new String[1];
        answerInit[0] = answer;

        File file = new File("answers.csv");
        command.append_in_file("answers.csv", answerInit, 1);

        int idReal = generateId("answers.csv", null);
        idReal = idReal - 1;

        return idReal;
    }


    //by name
    public void get_question_id() {

        File file = new File("questions.csv");
        if ( file.exists() == false ) {
            System.out.println("{ 'status' : 'error', 'message' : 'Question does not exist'}");
            return;
        }

        //textul e primul pe linie => poz = 0
        String line = command.find_in_file("questions.csv", question.getText(), 0);
        if ( line == null ) {
            System.out.println("{ 'status' : 'error', 'message' : 'Question does not exist'}");
            return;
        }

        //linia are: text,nr_rasp,nr_corecte,id
        String id = command.getRightToken(line, 3);
        System.out.println("{'status':'ok','message':'" + id + "'}");
    }

    //se ajunge aici daca autentificarea a mers ok
    public void get_all_quest() {
        File file = new File("questions.csv");
        if ( file.exists() == false ) {
            return;
        }

        System.out.print("{ 'status' : 'ok', 'message' :'[" );
        String[] textFile = command.readAll("questions.csv", "answer");


        for ( int i = 0; i < textFile.length; i++ ) {
            String line = textFile[i];

            String text = command.getRightToken(line,0);

            String id = command.getRightToken(line, 3);

            System.out.print("{\"question_id\" : " + "\"" + id + "\"" + ", " + "\"question_name\" : " );
            System.out.print("\"" +  text + "\"");

            if ( i == textFile.length - 1 )
                System.out.print("}");
            else
                System.out.print("}, ");
        }
        System.out.print("]'}");
    }

    //prima data check la erori
    //daca nu am erori voi scrie in fisier aici:
    public void create_quizz() {
        if ( checkQuizzErr() == 0 )
            return;

        quizz.initQuizzQuest(args);

        //quizzul trebuie vf daca exista deja sau nu :
        File file = new File("quizz.csv");
        if ( file.exists() == false ) {
            write(quizz);
            System.out.println("{'status':'ok','message':'Quizz added succesfully'}");
            return;
        }

        //vf daca exista deja quizz cu acel nume-> numele se afla pe a 2a poz
        if ( command.find_in_file("quizz.csv", quizz.getName(), 1) != null ) {
            System.out.println("{'status':'error','message':'Quizz name already exists'}");
        } else {
            write(quizz);
            System.out.println("{'status':'ok','message':'Quizz added succesfully'}");
        }

    }

    public void get_quizzByName() {
        File file = new File("quizz.csv");
        if ( file.exists() == false ) {
            System.out.println("{ 'status' : 'error', 'message' : 'Quizz does not exist'}");
            return;
        }

        //textul e al doilea pe linie => poz = 1
        String line = command.find_in_file("quizz.csv", quizz.getName(), 1);
        if ( line == null ) {
            System.out.println("{ 'status' : 'error', 'message' : 'Quizz does not exist'}");
            return;
        }

        //linia are: user,nume,id
        String id = command.getRightToken(line, 3);
        System.out.println("{'status':'ok','message':'" + id + "'}");
    }


    public void get_all_quizz() {

        File fileAll = new File("quizz.csv");
        if ( fileAll.exists() == false ) {
            return;
        }

        System.out.print("{ 'status' : 'ok', 'message' :'[" );
        String[] firstLines = command.readAll("quizz.csv", "question");


        for ( int i = 0; i < firstLines.length; i++ ) {
            String line = firstLines[i];

            String text = command.getRightToken(line,1);

            String id = command.getRightToken(line, 3);

            System.out.print("{\"quizz_id\" : " + "\"" + id + "\"" + ", " + "\"quizz_name\" : " );
            System.out.print("\"" +  text + "\", \"is_completed\" :");

            //caut numele quizzului in submit.csv
            File quizzSubmitted = new File("submit.csv");
            if ( quizzSubmitted.exists() == true ) {
                String lineToFind = command.find_in_file("submit.csv", text, 1);
                if ( lineToFind != null ) {
                    System.out.print(" \"True\"");
                } else
                    System.out.print(" \"False\"");
            } else
                System.out.print(" \"False\"");

            //restul:
            if ( i == firstLines.length - 1 )
                System.out.print("}");
            else
                System.out.print("}, ");
        }
        System.out.print("]'}");
    }

    //afiseaza quizz in fct de id-ul primit
    public void get_quizzDetails(){

        int start = args[3].indexOf("'");
        int len = args[3].length();
        String id = args[3].substring(start+1, len-1);

        //caut in quizz.csv dupa id
        File fileAll = new File("quizz.csv");
        if ( fileAll.exists() == false ) {
            return;
        }
        System.out.print("{ 'status' : 'ok', 'message' :'[" );

        //aflu nr intrebari ale quizzului daca exista
        String line = command.find_in_file("quizz.csv", id, 3);
        if ( line == null )
            return;
        int nrQuestions = nrQuestQuizz(id, line);

        //fac un StringArray care va avea tot fisierul mai putin prima linie a quizzului cautat
        //din acest string array parcurg doar cate intrebari are quizzul
        String[] quizzQuestions = command.readAll("quizz.csv", line);
        String[] allQuest = command.readAll("questions.csv", null);

        for ( int i = 0; i < nrQuestions; i ++ ) {
            start = quizzQuestions[i].indexOf("'");
            len = quizzQuestions[i].length();
            String idQuest = quizzQuestions[i].substring(start+1, len-1);
            String qfirstLine = command.find_in_file("questions.csv",idQuest,3);
            Question currentQ = questionDetails(quizzQuestions[i], qfirstLine);

            System.out.print("{\"question-name\":\""+ currentQ.getText() +"\", \"question_index\":\""+
                    idQuest + "\", " + "\"question_type\":\""+ currentQ.getType() +"\", \"answers\":\"[");

            //pt afisare rasp:
            //folosesc vect de string care e tot fis parsat
            int j = 0;
            String lineInFile = allQuest[j];
            while ( j < allQuest.length ) {
                if ( lineInFile.equals(qfirstLine) == true )
                    break;
                j++;
                lineInFile = allQuest[j];
            }

            //de la j+1 pana la nr rasp se gasesc raspunsurile acelei intrebari
            int p = 0;
            j++;
            while ( p < currentQ.getNr_ans() ) {
                String[] answer = answerDetails(allQuest[j]);
                System.out.print("{\"answer_name\":\"" + answer[0] + "\", \"answer_id\":\"" +
                        answer[1] + "\"");

                if ( p == currentQ.getNr_ans() - 1 )
                    System.out.print("}]\"");
                else
                    System.out.print("}, ");

                j++;
                p++;
            }

            if ( i == nrQuestions - 1 )
                System.out.print("}");
            else
                System.out.print("}, ");

        }
        System.out.print("]'}");

    }

    //aflu nr de intrebari pe care le are un Quizz dupa id ul sau
    public int nrQuestQuizz(String id, String line) {
        int len = line.length();
        int start = line.indexOf(",") + 1;
        String substr = line.substring(start, len - 1);
        start = substr.indexOf(",") + 1;
        len = substr.length();
        String nr_questions = substr.substring(start, len - 1);
        int nrQuestions = Integer.parseInt(nr_questions);
        return nrQuestions;
    }


    //transform o linie intr un string array cu detalii despre raspuns
    public String[] answerDetails(String line) {
        String[] details = new String[3];
        String substr = line.substring(line.indexOf("'") + 1, line.length());
        String ans_name = substr.substring(0,substr.indexOf("'"));
        details[0] = ans_name;

        int start = line.indexOf("r");
        String ans_id = line.substring(start+2,start+3);
        details[1] = ans_id;

        start = line.indexOf(",");
        String ans_correct = line.substring(start+1,line.length());
        details[2] = ans_correct;

        return details;
    }

    //info despre intrebari:
    //linia fiind dintr un vector string care este fisierul
    //cu quizz uri
    public Question questionDetails(String quizzLine, String qfirstLine) {
        String name = command.getRightToken(qfirstLine,0);
        String nr_raspTotal = command.getRightToken(qfirstLine, 1);
        String nr_corecteStr = command.getRightToken(qfirstLine, 2);
        int nr_corecte = Integer.parseInt(nr_corecteStr);
        int nr_rasp = Integer.parseInt(nr_raspTotal);

        String type;
        if ( nr_corecte > 1 )
            type = "multiple";
        else
            type = "single";

        Question question = new Question(name);
        question.setType(type);
        question.setNr_ans(nr_rasp);
        question.setNr_correct(nr_corecte);
        return question;
    }


    //vect ansSumit este cel de rasp submitted
    public void submit_quizz() {
        if ( checkSubmitErr() == 0 )
            return;

        String[] ansSubmit = new String[count-4];
        float score = 0f;
        int nrQuestions = 1;

        int start = args[3].indexOf("'");
        int len = args[3].length();
        String quizzId = args[3].substring(start+1, len-1);
        //linia ce contne id ul chestionarului:
        String line = command.find_in_file("quizz.csv", quizzId, 3);
        if (line == null)
            return;
        nrQuestions = nrQuestQuizz(quizzId, line);

        //info din fisierul de quizz si question:
        String[] quizzQuestions = command.readAll("quizz.csv", line);
        String[] allQuest = command.readAll("questions.csv", null);

        for ( int nr = 0; nr < ansSubmit.length; nr++ ) {
            boolean found = false;

            ansSubmit[nr] = args[nr + 4];
            start = ansSubmit[nr].indexOf("'");
            int stop = ansSubmit[nr].length() - 1;
            String idAns = ansSubmit[nr].substring(start + 1, stop);

            //linia de unde incepe pt ca imediat dupa linie urmeaza raspunsurile
            for (int i = 0; i < nrQuestions; i++) {

                start = quizzQuestions[i].indexOf("'");
                len = quizzQuestions[i].length();
                String idQuest = quizzQuestions[i].substring(start + 1, len - 1);
                String qfirstLine = command.find_in_file("questions.csv", idQuest, 3);
                Question currentQ = questionDetails(quizzQuestions[i], qfirstLine);

                //pt afisare rasp:
                //folosesc vect de string car e tot fis parsat
                int j = 0;
                String lineInFile = allQuest[j];
                while (j < allQuest.length) {
                    if (lineInFile.equals(qfirstLine) == true)
                        break;
                    j++;
                    lineInFile = allQuest[j];
                }

                //de la j+1 pana la nr rasp se gasesc raspunsurile acelei intrebari
                int p = 0;
                j++;

                while (p < currentQ.getNr_ans()) {
                    String[] answer_info = answerDetails(allQuest[j]);

                    if ( answer_info[1].equals(idAns) == true) {

                        score = submitAnswer(answer_info[2], currentQ, score);
                        found = true;
                        break;
                    }
                    j++;
                    p++;
                }

                if ( found == true ) {
                    //am gasit match rasp intrebare
                    break;
                }
            }

            if ( found == false )
                System.out.println("{ “status” : “error”, “message” : “Answer ID for answer " + idAns +
                        " does not exist”}");
        }

        //la final scorul se imparte la nr  de intrebari pe care le are quizul
        score = score/nrQuestions;
        setScore(score, line);

    }


    public void  getMySolutions() {

        String userName = user.getName();
       //caz in plus: userul nu a submis nimic

        File file = new File("submit.csv");
        if ( file.exists() == false )
            return;

        // userul nu a submis nimic
        String userSubmitted = command.find_in_file("submit.csv", userName, 0);
        if ( userSubmitted == null )
            return;

        //userul exista in fisier -> afisez
        String[] submitAll = command.readAll("submit.csv",null);
        int index = 0;
        String[] quizzId = new String[submitAll.length];
        String[] quizzName = new String[submitAll.length];
        String[] score = new String[submitAll.length];

        for ( int i = 0; i < submitAll.length; i++ ) {
            if ( submitAll[i].contains(userName) == true ) {

                quizzId[index] = command.getRightToken(submitAll[i],3);
                quizzName[index] = command.getRightToken(submitAll[i],1);
                score[index] = command.getRightToken(submitAll[i], 4);
                index++;
            }
        }

        //afisare:
        System.out.print("{ 'status' : 'ok', 'message' : '[");
        for ( int i = 0; i < index; i++ ) {

            System.out.print("{\"quiz-id\" : \""+ quizzId[i] + "\", \"quiz-name\" : \""+ quizzName[i] +"\", " +
                    "\"score\" : \""+ score[i] +"\", \"index_in_list\" : \""+ (i+1) +"\"");

            if ( i == index - 1 )
                System.out.print("}");
            else
                System.out.print("}, ");
        }
        System.out.print("]'}");

    }

    public void deleteQuizzById() {

        //arg parsat corect/id gasit sau nu:
        String quizzLine = findQuizzIdErr();
        if ( quizzLine == null )
            return;

        //sterg intrebarea ce are informatiile de pe linie
        //am nevoie de tot continutul fisierului de quizz
        //aflu linia la care se afla id ul quizzului
        String[] quizzAll = command.readAll("quizz.csv",null);
        int i = 0;
        String[] firstHalf = new String[quizzAll.length];
        while ( true ) {
            if ( quizzAll[i].contains("question") == false ) {
                if ( quizzAll[i].contains(quizzLine) == true )
                    break;
            }
            firstHalf[i] = quizzAll[i];
            i++;
        }
        //pana la linia i inclusiv copiez in fisrthalf din fisier
        //i + nr intrebari quizz de sters apare a doua jumatate a fisierului
        int nr_quest = 0;
        String nrQuestStr = command.getRightToken(quizzLine,2);
        nr_quest = Integer.parseInt(nrQuestStr);

        int index = i;
        i = i + nr_quest + 1;

        while (i < quizzAll.length) {
            firstHalf[index] = quizzAll[i];
            index++;
            i++;
        }

        File file = new File("quizz.csv");
        if ( file.delete() ) {
            command.append_in_file("quizz.csv", firstHalf, index);
            System.out.println("{ 'status' : 'ok', 'message' : 'Quizz deleted successfully'}");
        }
    }

}

