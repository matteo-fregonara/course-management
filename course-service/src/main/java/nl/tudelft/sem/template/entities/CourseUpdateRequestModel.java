package nl.tudelft.sem.template.entities;

import javax.validation.constraints.NotNull;

public class CourseUpdateRequestModel {

    @NotNull
    private Integer courseCode;

    @NotNull
    private Integer maxSize;

    @NotNull
    private String description;

    @NotNull
    private boolean canEnroll;

    /** CourseUpdateRequestModel constructor. Used in course update endpoint.
     *
     * @param courseCode the unique code of the course
     * @param maxSize the maximum size
     * @param description the description of a course
     * @param canEnroll boolean describing whether the curse is open for enrollement
     */
    public CourseUpdateRequestModel(int courseCode, int maxSize,
                                    String description, boolean canEnroll) {
        this.courseCode = courseCode;
        this.maxSize = maxSize;
        this.description = description;
        this.canEnroll = canEnroll;
    }


    public CourseUpdateRequestModel() {
    }

    public int getCourseCode() {
        return courseCode;
    }

    public void setCourseCode(int courseCode) {
        this.courseCode = courseCode;
    }

    public int getMaxSize() {
        return maxSize;
    }

    public void setMaxSize(int maxSize) {
        this.maxSize = maxSize;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public boolean isCanEnroll() {
        return canEnroll;
    }

    public void setCanEnroll(boolean canEnroll) {
        this.canEnroll = canEnroll;
    }

}
