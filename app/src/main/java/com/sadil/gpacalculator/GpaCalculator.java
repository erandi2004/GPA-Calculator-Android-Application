package com.sadil.gpacalculator;

import java.util.List;

/** Pure GPA calculation logic kept separate from the Android UI. */
public final class GpaCalculator {

    private GpaCalculator() {
        // Utility class.
    }

    public static Result calculate(List<Course> courses) {
        double totalCredits = 0.0;
        double totalQualityPoints = 0.0;
        int countedCourses = 0;

        for (Course course : courses) {
            if (course.isExcluded()) {
                continue;
            }

            totalCredits += course.getCredits();
            totalQualityPoints += course.getCredits() * course.getGradePoints();
            countedCourses++;
        }

        double gpa = totalCredits == 0.0 ? 0.0 : totalQualityPoints / totalCredits;
        return new Result(gpa, totalCredits, totalQualityPoints, countedCourses);
    }

    public static final class Course {
        private final double credits;
        private final double gradePoints;
        private final boolean excluded;

        public Course(double credits, double gradePoints, boolean excluded) {
            this.credits = credits;
            this.gradePoints = gradePoints;
            this.excluded = excluded;
        }

        public double getCredits() {
            return credits;
        }

        public double getGradePoints() {
            return gradePoints;
        }

        public boolean isExcluded() {
            return excluded;
        }
    }

    public static final class Result {
        private final double gpa;
        private final double totalCredits;
        private final double qualityPoints;
        private final int countedCourses;

        public Result(double gpa, double totalCredits, double qualityPoints, int countedCourses) {
            this.gpa = gpa;
            this.totalCredits = totalCredits;
            this.qualityPoints = qualityPoints;
            this.countedCourses = countedCourses;
        }

        public double getGpa() {
            return gpa;
        }

        public double getTotalCredits() {
            return totalCredits;
        }

        public double getQualityPoints() {
            return qualityPoints;
        }

        public int getCountedCourses() {
            return countedCourses;
        }
    }
}
