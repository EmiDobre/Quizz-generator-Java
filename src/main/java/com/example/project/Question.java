package com.example.project;
import java.util.HashSet;
import java.util.Set;

public class Question {
    private String text;
    private String type;
    private String[] answers;
    private int nr_ans;
    private int nr_correct;

    //un elem al lui answer este
    //raspunsul alaturi de val de adevar

    public Question ( String text) {
        this.text = text;
        this.answers = new String[5];
    }
    public String getText() {
        return text;
    }
    public void setType(String type){ this.type = type; }
    public void setNr_ans(int nr_ans){ this.nr_ans = nr_ans; }
    public void setNr_correct(int nr_correct){ this.nr_correct = nr_correct; }

    public String getType(){ return  this.type; }
    public int getNr_ans() {
        return nr_ans;
    }

    public int getNr_correct(){
        return nr_correct;
    }

    public String[] getAnswers(){
        return answers;
    }

    //functia vf si daca exista duplicate
    //in plus va initializa campul de raspunsuri
    //in modul in care va fi si scris in fisier


    public void initAnswers(String[] ans_input, int nr_correct) {
        this.nr_correct = nr_correct;

        int len = ans_input.length;
        int i = 0;
        len = len / 2;
        nr_ans = 0;

        while ( len != 0 ) {
            answers[nr_ans] = ans_input[i];
            answers[nr_ans] = answers[nr_ans].concat(",");

            if ( ans_input[i+1].contains("'1'") == true )
                answers[nr_ans] = answers[nr_ans].concat("correct");
            else
                answers[nr_ans] = answers[nr_ans].concat("no");

            i = i + 2;
            nr_ans++;
            len --;
        }
    }

    //check doar la text
    //gasesc duplicate cu un hash set care permite inserare
    //doar daca nu exista deja elem
    //amcomplexitate liniara astfel

    public int checkDuplicates(String[] ans_input) {
        Set<String> set = new HashSet<>();
        int i = 0;
        int start_idx = 9; //de aici am textul raspunsului
        for ( String aux : ans_input ) {
            String elem = ans_input[i].substring(start_idx);
            if ( elem.contains("is-correct") == true ) {
                i = i + 1;
                continue;
            }

            if ( set.add(elem) == false ) {
                System.out.println("{ 'status' : 'error', 'message' : 'Same answer provided " +
                        "more than once'}");
                return 0;
            } else {
                i = i + 1;
            }
        }
        return 1;
    }

    //cand voi scrie in fis prima linie coresp intrebarii va fi:
    //liniile urm voi fi repr de val vect ans pe cate o line


    //prima linie: intrebare, nr_rasp, nr_corecte, id (adaugat dupa)
    //restul liniilor: answer-id-'text',correct/no
    public String[] toStringArray () {

        String firstLine = text;
        firstLine = firstLine.concat("," + Integer.toString(nr_ans) +
                "," + Integer.toString(nr_correct) );

        String[] quest_info = new String[nr_ans + 1];
        quest_info[0] = firstLine;
        System.arraycopy(answers,0, quest_info,1, nr_ans);

        return quest_info;
    }

    //primeste id ul intrebarii
    public void RealAnsId(int id, int realId, int poz) {

        if ( id == 1 )
            return;

        int start = answers[poz].indexOf("r");
        start = start + 2;
        int stop = answers[poz].indexOf("'");
        String idStr = answers[poz].substring(start,stop-1);
        int idAns = Integer.parseInt(idStr);

        //nu  e ok-> write answ[i]

        start = stop;
        stop = answers[poz].length();
        String info = answers[poz].substring(start,stop);

        String newAns = "-answer-";
        newAns = newAns.concat(Integer.toString(realId) + " ");
        newAns = newAns.concat(info);
        answers[poz] = newAns;

    }


}
