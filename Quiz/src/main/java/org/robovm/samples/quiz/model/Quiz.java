/*
 * Copyright (C) 2013-2015 RoboVM AB
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.robovm.samples.quiz.model;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

public class Quiz {
    private static final String QUESTION_KEY = "question";

    private final List<Question> questions = new ArrayList<>();

    private int correctlyAnsweredQuestions;
    /**
     * The number of questions the user has answered.
     */
    private int answeredQuestions;

    public Quiz(String questionsPath) {
        try {
            DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder documentBuilder = documentBuilderFactory.newDocumentBuilder();
            Document doc = documentBuilder.parse(new File(questionsPath));
            doc.getDocumentElement().normalize();
            NodeList nodeList = doc.getElementsByTagName(QUESTION_KEY);
            for (int i = 0, n = nodeList.getLength(); i < n; i++) {
                questions.add(new Question((Element) nodeList.item(i)));
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

    }

    /**
     * Percentage of correctly answered questions of the total questions
     * answered.
     */
    public float getPercentageScore() {
        return correctlyAnsweredQuestions / (float) answeredQuestions;
    }

    /**
     * The number of questions the user has answered correctly.
     */
    public int getCorrectlyAnsweredQuestions() {
        return correctlyAnsweredQuestions;
    }

    /**
     * Total questions in the quiz.
     */
    public int getTotalQuestions() {
        return questions.size();
    }

    public Question getQuestion(int index) {
        return questions.get(index);
    }

    public void answerQuestion(int index, int selectedAnswer) {
        Question question = getQuestion(index);

        int previouslySelectedAnswer = question.getSelectedAnswer();
        if (previouslySelectedAnswer == -1 && selectedAnswer != -1) {
            // If the question had not been answered but now is, increment
            // answeredQuestions.
            answeredQuestions++;
        } else if (previouslySelectedAnswer != -1 && selectedAnswer == -1) {
            // If the question had been answered but no longer is, decrement
            // answeredQuestions. This will occur when the quiz is being reset.
            answeredQuestions--;
        }
        if (selectedAnswer == question.getCorrectAnswer() && previouslySelectedAnswer != question.getCorrectAnswer()) {
            // If the current response is correct and the previous response was
            // incorrect, increment correctlyAnsweredQuestions.
            correctlyAnsweredQuestions++;
        } else if (previouslySelectedAnswer == question.getCorrectAnswer()
                && selectedAnswer != question.getCorrectAnswer()) {
            // If the current response is incorrect and the previous response
            // was correct, decrement correctlyAnsweredQuestions.
            correctlyAnsweredQuestions--;
        }
        question.setSelectedAnswer(selectedAnswer);
    }

    public void resetQuiz() {
        for (int i = 0, n = getTotalQuestions(); i < n; i++) {
            answerQuestion(i, -1);
        }
    }
}
