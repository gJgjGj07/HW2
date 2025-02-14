
package entity;

public class Answer {
    private Long id;
    private String content;
    private Long questionId;
    private boolean isAccepted;
    private Long questionId;


    public void accept() {
        this.isAccepted = true;
    }

    public boolean updateContent(String newContent) {
        if (newContent == null || newContent.length() < 10) {
            return false;
        }
        this.content = newContent;
        return true;
    }

    
}
