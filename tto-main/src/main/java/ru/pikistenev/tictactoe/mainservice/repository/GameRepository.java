package ru.pikistenev.tictactoe.mainservice.repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.pikistenev.tictactoe.mainservice.model.Game;

public interface GameRepository extends JpaRepository<Game, UUID> {

    @Query(nativeQuery = true,
            value = "select g.* " +
                    "from Game g " +
                    "         left join Step s on s.game_id = g.id and s.is_user_step = true " +
                    "where ((s.id is null and g.updated < ?1) " +
                    "   or (s.is_user_step = true and s.updated <= ?1)) AND g.status != 'FINISHED'")
    Optional<List<Game>> inactiveGame(LocalDateTime updatedBefore);

}

