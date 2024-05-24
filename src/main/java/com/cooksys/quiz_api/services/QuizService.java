package com.cooksys.quiz_api.services;

import java.util.List;

import com.cooksys.quiz_api.dtos.QuestionRequestDto;
import com.cooksys.quiz_api.dtos.QuestionResponseDto;
import com.cooksys.quiz_api.dtos.QuizRequestDto;
import com.cooksys.quiz_api.dtos.QuizResponseDto;
import javassist.NotFoundException;

public interface QuizService {

  List<QuizResponseDto> getAllQuizzes();
  QuizResponseDto createQuiz(QuizRequestDto quizRequestDto);

  QuizResponseDto deleteQuiz(Long id) throws NotFoundException;

  QuizResponseDto renameQuiz(Long id, String newName) throws NotFoundException;

  QuestionResponseDto getRandomQuestion(Long id) throws NotFoundException;

  QuizResponseDto addQuestion(Long quizId, QuestionRequestDto questionRequestDto) throws NotFoundException;

  QuestionResponseDto deleteQuestion(Long quizId, Long questionId) throws NotFoundException;
}
