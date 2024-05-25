package com.cooksys.quiz_api.entities;

import java.util.List;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.OneToMany;

import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@NoArgsConstructor
@Data
public class Quiz {

  @Id
  @GeneratedValue
  private Long id;

  private String name;

  @OneToMany(mappedBy = "quiz",fetch = FetchType.EAGER)
  private List<Question> questions;

}
