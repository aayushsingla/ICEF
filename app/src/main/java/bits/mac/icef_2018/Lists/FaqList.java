package bits.mac.icef_2018.Lists;

/**
 * Created by aayush on 10/2/18.
 */

public class FaqList {
    private String question;
    private String answer;


    public FaqList(){

    }

    public FaqList(String question,String answer){
        this.question=question;
        this.answer=answer;
    }

    public String getQuestion(){
        return question;
    }


    public String getAnswer(){
        return answer;
    }

    public void setQuestion(String question){
        this.question=question;
    }


    public void setAnswer(String answer){
        this.answer=answer;
    }


}