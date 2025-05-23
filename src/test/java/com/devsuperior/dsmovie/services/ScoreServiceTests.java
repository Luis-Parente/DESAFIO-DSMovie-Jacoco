package com.devsuperior.dsmovie.services;

import static org.mockito.ArgumentMatchers.any;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import com.devsuperior.dsmovie.dto.MovieDTO;
import com.devsuperior.dsmovie.dto.ScoreDTO;
import com.devsuperior.dsmovie.entities.MovieEntity;
import com.devsuperior.dsmovie.entities.ScoreEntity;
import com.devsuperior.dsmovie.entities.UserEntity;
import com.devsuperior.dsmovie.repositories.MovieRepository;
import com.devsuperior.dsmovie.repositories.ScoreRepository;
import com.devsuperior.dsmovie.services.exceptions.ResourceNotFoundException;
import com.devsuperior.dsmovie.tests.MovieFactory;
import com.devsuperior.dsmovie.tests.ScoreFactory;
import com.devsuperior.dsmovie.tests.UserFactory;

@ExtendWith(SpringExtension.class)
public class ScoreServiceTests {

	@InjectMocks
	private ScoreService service;

	@Mock
	private UserService userService;

	@Mock
	private MovieRepository movieRepository;

	@Mock
	private ScoreRepository scoreRepository;

	private ScoreEntity score;

	private ScoreDTO scoreDTO;

	private UserEntity user;

	private MovieEntity movie;

	private Long existingMovieId;

	private Long nonExistingMovieId;

	private Double expectedScore;

	private Integer expectedCount;

	List<ScoreEntity> scoreList;

	@BeforeEach
	void setUp() throws Exception {
		score = ScoreFactory.createScoreEntity();
		user = UserFactory.createUserEntity();
		movie = MovieFactory.createMovieEntity();

		movie.getScores().add(score);

		existingMovieId = 1L;
		nonExistingMovieId = 2L;

		expectedScore = 4.5;

		expectedCount = 1;

		Mockito.when(userService.authenticated()).thenReturn(user);

		Mockito.when(movieRepository.findById(existingMovieId)).thenReturn(Optional.of(movie));
		Mockito.when(movieRepository.findById(nonExistingMovieId)).thenReturn(Optional.empty());

		Mockito.when(scoreRepository.saveAndFlush(any())).thenReturn(score);

		Mockito.when(movieRepository.save(any())).thenReturn(movie);

	}

	@Test
	public void saveScoreShouldReturnMovieDTO() {

		movie.setId(existingMovieId);
		score.setMovie(movie);
		scoreDTO = ScoreFactory.createCustomScoreDTO(score);

		MovieDTO result = service.saveScore(scoreDTO);

		Assertions.assertNotNull(result);
		Assertions.assertEquals(result.getId(), existingMovieId);
		Assertions.assertEquals(result.getTitle(), movie.getTitle());
		Assertions.assertEquals(result.getCount(), expectedCount);
		Assertions.assertEquals(result.getScore(), expectedScore);

	}

	@Test
	public void saveScoreShouldThrowResourceNotFoundExceptionWhenNonExistingMovieId() {
		movie.setId(nonExistingMovieId);
		score.setMovie(movie);
		scoreDTO = ScoreFactory.createCustomScoreDTO(score);

		Assertions.assertThrows(ResourceNotFoundException.class, () -> {
			service.saveScore(scoreDTO);
		});
	}
}
