package com.example.project;

//un quizz e facut de un user
//mai multi useri pot raspunde la el
public class Quizz {
    private String name;
    private String[] quest_text;
    private User user;

    private int score;

    public int getScore() {
        return score;
    }

    public void setScore(int score) {
        this.score = score;
    }
    public String getName() {
        return name;
    }

    public String[] getQuest_text() {
        return quest_text;
    }

    public User getUser() {
        return user;
    }


    public Quizz(User user, String name) {
        this.user = user;
        this.name = name;
    }

    public void initQuizzQuest(String[] args) {
        //de aici din args sunt intrebarile din quizz
        int start = 4;
        int len = args.length - start;
        this.quest_text = new String[len];
        System.arraycopy(args, start, this.quest_text, 0, len);

    }

    //quizz prima linie: userName,nume_quizz,nr_intrebari,id (de adaugat dupa)

    public String[] toStringArray() {

        String info = user.getName();
        int len = this.quest_text.length;
        info = info.concat("," + this.name + "," + len);

        //de adaugat id la info

        String[] quizz_info = new String[len + 1];
        quizz_info[0] = info;
        System.arraycopy(this.quest_text,0, quizz_info,1, len);
        return  quizz_info;
    }



}
