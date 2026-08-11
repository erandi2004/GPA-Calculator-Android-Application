package com.sadil.gpacalculator;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class MainActivity extends Activity {

    private static final int INITIAL_COURSE_COUNT = 4;
    private static final int MAX_COURSE_COUNT = 20;
    private static final int PASS_GRADE_INDEX = 12;

    // Must match the order of grade_options in arrays.xml.
    private static final double[] GRADE_POINTS = {
            4.0, 4.0, 3.7, 3.3, 3.0, 2.7,
            2.3, 2.0, 1.7, 1.3, 1.0, 0.0, 0.0
    };

    private LinearLayout coursesContainer;
    private View resultCard;
    private TextView gpaValue;
    private TextView resultMessage;
    private TextView creditsValue;
    private TextView coursesValue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        getWindow().setStatusBarColor(getColor(R.color.primary_dark));
        getWindow().setNavigationBarColor(Color.WHITE);

        coursesContainer = findViewById(R.id.coursesContainer);
        resultCard = findViewById(R.id.resultCard);
        gpaValue = findViewById(R.id.gpaValue);
        resultMessage = findViewById(R.id.resultMessage);
        creditsValue = findViewById(R.id.creditsValue);
        coursesValue = findViewById(R.id.coursesValue);

        Button addCourseButton = findViewById(R.id.addCourseButton);
        Button calculateButton = findViewById(R.id.calculateButton);
        Button resetButton = findViewById(R.id.resetButton);

        addCourseButton.setOnClickListener(view -> addCourseRow("", "", 0));
        calculateButton.setOnClickListener(view -> calculateGpa());
        resetButton.setOnClickListener(view -> resetCalculator());

        if (savedInstanceState == null) {
            addInitialRows();
        } else {
            restoreRows(savedInstanceState);
        }
    }

    private void addInitialRows() {
        for (int i = 0; i < INITIAL_COURSE_COUNT; i++) {
            addCourseRow("", "", 0);
        }
    }

    private void addCourseRow(String courseName, String credits, int gradePosition) {
        if (coursesContainer.getChildCount() >= MAX_COURSE_COUNT) {
            Toast.makeText(this, R.string.maximum_courses, Toast.LENGTH_SHORT).show();
            return;
        }

        View row = LayoutInflater.from(this)
                .inflate(R.layout.item_course, coursesContainer, false);

        EditText courseNameInput = row.findViewById(R.id.courseNameInput);
        EditText creditsInput = row.findViewById(R.id.creditsInput);
        Spinner gradeSpinner = row.findViewById(R.id.gradeSpinner);
        Button removeButton = row.findViewById(R.id.removeCourseButton);

        ArrayAdapter<CharSequence> gradeAdapter = ArrayAdapter.createFromResource(
                this,
                R.array.grade_options,
                R.layout.spinner_item
        );
        gradeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        gradeSpinner.setAdapter(gradeAdapter);

        courseNameInput.setText(courseName);
        creditsInput.setText(credits);
        gradeSpinner.setSelection(Math.max(0, Math.min(gradePosition, GRADE_POINTS.length - 1)));

        removeButton.setOnClickListener(view -> {
            if (coursesContainer.getChildCount() == 1) {
                Toast.makeText(this, R.string.minimum_courses, Toast.LENGTH_SHORT).show();
                return;
            }
            coursesContainer.removeView(row);
            updateRowNumbers();
            resultCard.setVisibility(View.GONE);
        });

        coursesContainer.addView(row);
        updateRowNumbers();
    }

    private void updateRowNumbers() {
        for (int i = 0; i < coursesContainer.getChildCount(); i++) {
            View row = coursesContainer.getChildAt(i);
            TextView courseNumber = row.findViewById(R.id.courseNumber);
            Button removeButton = row.findViewById(R.id.removeCourseButton);
            courseNumber.setText(String.valueOf(i + 1));
            removeButton.setContentDescription(
                    getString(R.string.remove_course_description, i + 1)
            );
        }
    }

    private void calculateGpa() {
        List<GpaCalculator.Course> courses = new ArrayList<>();

        for (int i = 0; i < coursesContainer.getChildCount(); i++) {
            View row = coursesContainer.getChildAt(i);
            EditText creditsInput = row.findViewById(R.id.creditsInput);
            Spinner gradeSpinner = row.findViewById(R.id.gradeSpinner);

            String creditsText = creditsInput.getText().toString().trim();
            double credits;

            try {
                credits = Double.parseDouble(creditsText);
            } catch (NumberFormatException exception) {
                creditsInput.setError(getString(R.string.enter_valid_credits));
                creditsInput.requestFocus();
                return;
            }

            if (credits <= 0.0 || credits > 99.0) {
                creditsInput.setError(getString(R.string.credits_range_error));
                creditsInput.requestFocus();
                return;
            }

            int gradeIndex = gradeSpinner.getSelectedItemPosition();
            boolean excluded = gradeIndex == PASS_GRADE_INDEX;
            courses.add(new GpaCalculator.Course(
                    credits,
                    GRADE_POINTS[gradeIndex],
                    excluded
            ));
        }

        GpaCalculator.Result result = GpaCalculator.calculate(courses);
        if (result.getTotalCredits() == 0.0) {
            Toast.makeText(this, R.string.no_counted_courses, Toast.LENGTH_SHORT).show();
            return;
        }

        gpaValue.setText(String.format(Locale.US, "%.2f", result.getGpa()));
        creditsValue.setText(formatNumber(result.getTotalCredits()));
        coursesValue.setText(String.valueOf(result.getCountedCourses()));
        resultMessage.setText(getPerformanceMessage(result.getGpa()));
        resultCard.setVisibility(View.VISIBLE);
        hideKeyboard();

        resultCard.post(() -> resultCard.requestFocus());
    }

    private String getPerformanceMessage(double gpa) {
        if (gpa >= 3.70) {
            return getString(R.string.performance_excellent);
        } else if (gpa >= 3.30) {
            return getString(R.string.performance_very_good);
        } else if (gpa >= 3.00) {
            return getString(R.string.performance_good);
        } else if (gpa >= 2.00) {
            return getString(R.string.performance_satisfactory);
        }
        return getString(R.string.performance_improve);
    }

    private String formatNumber(double value) {
        if (value == Math.rint(value)) {
            return String.format(Locale.US, "%.0f", value);
        }
        return String.format(Locale.US, "%.1f", value);
    }

    private void resetCalculator() {
        coursesContainer.removeAllViews();
        resultCard.setVisibility(View.GONE);
        addInitialRows();
        hideKeyboard();
        Toast.makeText(this, R.string.calculator_reset, Toast.LENGTH_SHORT).show();
    }

    private void hideKeyboard() {
        View currentView = getCurrentFocus();
        if (currentView == null) {
            currentView = coursesContainer;
        }
        InputMethodManager inputMethodManager =
                (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(currentView.getWindowToken(), 0);
        currentView.clearFocus();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        ArrayList<String> courseNames = new ArrayList<>();
        ArrayList<String> credits = new ArrayList<>();
        ArrayList<Integer> grades = new ArrayList<>();

        for (int i = 0; i < coursesContainer.getChildCount(); i++) {
            View row = coursesContainer.getChildAt(i);
            EditText courseNameInput = row.findViewById(R.id.courseNameInput);
            EditText creditsInput = row.findViewById(R.id.creditsInput);
            Spinner gradeSpinner = row.findViewById(R.id.gradeSpinner);

            courseNames.add(courseNameInput.getText().toString());
            credits.add(creditsInput.getText().toString());
            grades.add(gradeSpinner.getSelectedItemPosition());
        }

        outState.putStringArrayList("course_names", courseNames);
        outState.putStringArrayList("credits", credits);
        outState.putIntegerArrayList("grades", grades);
    }

    private void restoreRows(Bundle savedInstanceState) {
        ArrayList<String> courseNames = savedInstanceState.getStringArrayList("course_names");
        ArrayList<String> credits = savedInstanceState.getStringArrayList("credits");
        ArrayList<Integer> grades = savedInstanceState.getIntegerArrayList("grades");

        if (courseNames == null || credits == null || grades == null || courseNames.isEmpty()) {
            addInitialRows();
            return;
        }

        int rowCount = Math.min(courseNames.size(), Math.min(credits.size(), grades.size()));
        for (int i = 0; i < rowCount; i++) {
            addCourseRow(courseNames.get(i), credits.get(i), grades.get(i));
        }
    }
}
