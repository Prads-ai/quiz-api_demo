package com.cooksys.quiz_api.dtos;

import com.cooksys.quiz_api.entities.Question;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@NoArgsConstructor
@Data
public class QuizRequestDto {
    private String name;
    private List<Question> questions;
}
