package ru.pikistenev.tictactoe.mainservice.repository;

import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import ru.pikistenev.tictactoe.mainservice.model.Game;

public interface GameRepository extends JpaRepository<Game, UUID> {

}
