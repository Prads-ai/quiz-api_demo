package com.cooksys.quiz_api.dtos;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

import com.cooksys.quiz_api.entities.Answer;

@NoArgsConstructor
@Data
public class QuestionRequestDto {
    private String text;
    private List<Answer> answers;
}
