package com.cooksys.quiz_api.dtos;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@NoArgsConstructor
@Data
public class QuestionRequestDto {
    private String text;
    private List<AnswerRequestDto> answers;
}
