package org.robovm.samples.quiz.model;

import java.util.ArrayList;
import java.util.List;

import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class Question {
    private static final String TEXT_KEY = "questionText";
    private static final String CORRECT_ANSWER_KEY = "correctAnswer";
    private static final String ANSWERS_KEY = "answers";
    private static final String ANSWER_KEY = "answer";

    /**
     * The text of the question.
     */
    private final String text;
    /**
     * Possible responses to the question.
     */
    private final List<String> answers = new ArrayList<String>();
    /**
     * The index of the correct response in the responses array.
     */
    private final int correctAnswer;
    /**
     * The index of the user's selected response or -1 if the user has not
     * responded to the question.
     */
    private int selectedAnswer;

    public Question(Element question) {
        this.text = getStringValue(question, TEXT_KEY);
        this.correctAnswer = getIntegerValue(question, CORRECT_ANSWER_KEY);
        this.selectedAnswer = -1;

        NodeList answerList = ((Element) question.getElementsByTagName(ANSWERS_KEY).item(0))
                .getElementsByTagName(ANSWER_KEY);
        for (int i = 0, n = answerList.getLength(); i < n; i++) {
            Node answer = answerList.item(i);
            this.answers.add(answer.getTextContent());
        }
    }

    private String getStringValue(Element element, String tag) {
        return element.getElementsByTagName(tag).item(0).getTextContent();
    }

    private int getIntegerValue(Element element, String tag) {
        return Integer.valueOf(getStringValue(element, tag));
    }

    public String getQuestionText() {
        return text;
    }

    public int getTotalAnswers() {
        return answers.size();
    }

    public String getAnswer(int index) {
        return answers.get(index);
    }

    public int getCorrectAnswer() {
        return correctAnswer;
    }

    public int getSelectedAnswer() {
        return selectedAnswer;
    }

    protected void setSelectedAnswer(int selectedAnswer) {
        this.selectedAnswer = selectedAnswer;
    }
}
