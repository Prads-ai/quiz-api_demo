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
    Quiz savedQuiz = quizRepository.saveAndFlush(quiz);

    for (Question question : questions) {
      question.setQuiz(savedQuiz); // Set the quiz for each question
      questionRepository.saveAndFlush(question); // Save each question
      List<Answer> answers = question.getAnswers();
      for (Answer answer : answers) {
        answer.setQuestion(question); // Set the question for each answer
        answerRepository.saveAndFlush(answer); // Save each answer
      }
      
    }

    return quizMapper.entityToDto(savedQuiz);
  }
  @Override
  public QuizResponseDto deleteQuiz(Long id) throws NotFoundException {
    Optional<Quiz> optionalQuiz = quizRepository.findById(id);
    if (!optionalQuiz.isPresent()) {
      throw new NotFoundException("Quiz not found with id: " + id);
    }
    Quiz quiz = optionalQuiz.get();
    List<Question>questions = quiz.getQuestions();
    
    for(Question question: questions) {
    	answerRepository.deleteByQuestionId(question.getId());
    }
    questionRepository.deleteByQuizId(id);
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
    question.setAnswers(questionRequestDto.getAnswers());
    List<Answer> answers = question.getAnswers();
    Question savedQuestion = questionRepository.save(question);
    
    for (Answer answer : answers) {
      answer.setQuestion(question);
      answerRepository.saveAndFlush(answer);
    }
    List<Question> questions = quiz.getQuestions(); 
    questions.add(savedQuestion);
    quiz.setQuestions(questions);
    quizRepository.save(quiz);
    return quizMapper.entityToDto(quiz);
  }

  @Override
  public QuestionResponseDto deleteQuestion(Long quizId, Long questionId) throws NotFoundException {
    Optional<Quiz> optionalQuiz = quizRepository.findById(quizId);
    if (!optionalQuiz.isPresent()) {
        throw new NotFoundException("Quiz not found with id: " + quizId);
    }
    Quiz quiz = optionalQuiz.get();

    List<Question> questions = quiz.getQuestions();
    Optional<Question> optionalQuestion = questions.stream()
            .filter(question -> question.getId().equals(questionId))
            .findFirst();
    if (!optionalQuestion.isPresent()) {
        throw new NotFoundException("Question not found with id: " + questionId + " in the quiz with id: " + quizId);
    }
    Question questionToDelete = optionalQuestion.get();

    // Remove answers associated with the question
    answerRepository.deleteByQuestionId(questionId);

    // Remove the question from the quiz
    questions.remove(questionToDelete);
    quiz.setQuestions(questions);
    quizRepository.save(quiz);

    // Delete the question
    questionRepository.delete(questionToDelete);

    return questionMapper.entityToDto(questionToDelete);
}

}
