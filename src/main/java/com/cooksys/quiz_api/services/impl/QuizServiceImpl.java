package com.cooksys.quiz_api.services.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Random;

import com.cooksys.quiz_api.dtos.*;
import com.cooksys.quiz_api.entities.Answer;
import com.cooksys.quiz_api.entities.Question;
import com.cooksys.quiz_api.entities.Quiz;
import com.cooksys.quiz_api.mappers.QuestionMapper;
import com.cooksys.quiz_api.mappers.QuizMapper;
import com.cooksys.quiz_api.repositories.AnswerRepository;
import com.cooksys.quiz_api.repositories.QuestionRepository;
import com.cooksys.quiz_api.repositories.QuizRepository;
import com.cooksys.quiz_api.services.QuizService;

import javassist.NotFoundException;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class QuizServiceImpl implements QuizService {

  private final QuizRepository quizRepository;
  private final QuizMapper quizMapper;
  private final QuestionRepository questionRepository;
  private final QuestionMapper questionMapper;
  private final AnswerRepository answerRepository; // Declare AnswerRepository

  @Override
  public List<QuizResponseDto> getAllQuizzes() {
    return quizMapper.entitiesToDtos(quizRepository.findAll());
  }

  @Override
  public QuizResponseDto createQuiz(QuizRequestDto quizRequestDto) {
    Quiz quiz = new Quiz();
    quiz.setName(quizRequestDto.getName());
    List<Question> questions = quizRequestDto.getQuestions();
    quiz.setQuestions(questions); // Set the questions for the quiz
    Quiz savedQuiz = quizRepository.save(quiz);

    for (Question question : questions) {
      question.setQuiz(savedQuiz); // Set the quiz for each question
      List<Answer> answers = question.getAnswers();
      for (Answer answer : answers) {
        answer.setQuestion(question); // Set the question for each answer
        answerRepository.save(answer); // Save each answer
      }
      questionRepository.save(question); // Save each question
    }

    return quizMapper.entityToDto(savedQuiz);
  }
  @Override
  public QuizResponseDto deleteQuiz(Long id) throws NotFoundException {
    Optional<Quiz> optionalQuiz = quizRepository.findById(id);
    if (!optionalQuiz.isPresent()) {
      throw new NotFoundException("Quiz not found with id: " + id);
    }
    Quiz quizToDelete = optionalQuiz.get();
    quizRepository.delete(quizToDelete);
    return quizMapper.entityToDto(quizToDelete);
  }

  @Override
  public QuizResponseDto renameQuiz(Long id, String newName) throws NotFoundException {
    Optional<Quiz> optionalQuiz = quizRepository.findById(id);
    if (!optionalQuiz.isPresent()) {
      throw new NotFoundException("Quiz not found with id: " + id);
    }
    Quiz quiz = optionalQuiz.get();
    quiz.setName(newName);
    Quiz renamedQuiz = quizRepository.save(quiz);
    return quizMapper.entityToDto(renamedQuiz);
  }

  @Override
  public QuestionResponseDto getRandomQuestion(Long id) throws NotFoundException {
    Optional<Quiz> optionalQuiz = quizRepository.findById(id);
    if (!optionalQuiz.isPresent()) {
      throw new NotFoundException("Quiz not found with id: " + id);
    }
    Quiz quiz = optionalQuiz.get();
    List<Question> questions = quiz.getQuestions();
    if (questions.isEmpty()) {
      throw new NotFoundException("No questions found in the quiz with id: " + id);
    }
    Random random = new Random();
    Question randomQuestion = questions.get(random.nextInt(questions.size()));
    return questionMapper.entityToDto(randomQuestion);
  }

  @Override
  public QuizResponseDto addQuestion(Long id, QuestionRequestDto questionRequestDto) throws NotFoundException {
    Optional<Quiz> optionalQuiz = quizRepository.findById(id);
    if (!optionalQuiz.isPresent()) {
      throw new NotFoundException("Quiz not found with id: " + id);
    }
    Quiz quiz = optionalQuiz.get();
    Question question = new Question();
    question.setText(questionRequestDto.getText());
    List<Answer> answers = new ArrayList<>();
    for (AnswerRequestDto answerRequestDto : questionRequestDto.getAnswers()) {
      Answer answer = new Answer();
      answer.setText(answerRequestDto.getText());
      answer.setCorrect(answerRequestDto.isCorrect());
      answer.setQuestion(question);
      answers.add(answer);
    }
    question.setAnswers(answers);
    question.setQuiz(quiz);
    Question savedQuestion = questionRepository.save(question);
    quiz.getQuestions().add(savedQuestion);
    quizRepository.save(quiz);
    return quizMapper.entityToDto(quiz);
  }

  @Override
  public QuestionResponseDto deleteQuestion(Long id, Long questionId) throws NotFoundException {
    Optional<Quiz> optionalQuiz = quizRepository.findById(id);
    if (!optionalQuiz.isPresent()) {
      throw new NotFoundException("Quiz not found with id: " + id);
    }
    Quiz quiz = optionalQuiz.get();
    Optional<Question> optionalQuestion = quiz.getQuestions().stream()
            .filter(question -> question.getId().equals(questionId))
            .findFirst();
    if (!optionalQuestion.isPresent()) {
      throw new NotFoundException("Question not found with id: " + questionId + " in the quiz with id: " + id);
    }
    Question questionToDelete = optionalQuestion.get();
    quiz.getQuestions().remove(questionToDelete);
    questionRepository.delete(questionToDelete);
    return questionMapper.entityToDto(questionToDelete);
  }

}
