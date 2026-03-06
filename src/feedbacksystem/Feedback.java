package feedbacksystem;

public class Feedback {

    private int studentId;
    private int courseId;
    private int rating;
    private String comments;

    public Feedback(int studentId, int courseId, int rating, String comments) {
        this.studentId = studentId;
        this.courseId = courseId;
        this.rating = rating;
        this.comments = comments;
    }

    public int getStudentId() { return studentId; }
    public int getCourseId() { return courseId; }
    public int getRating() { return rating; }
    public String getComments() { return comments; }
}