package ru.pikistenev.tictactoe.mainservice.repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import ru.pikistenev.tictactoe.mainservice.enums.GameStatus;
import ru.pikistenev.tictactoe.mainservice.model.Step;

public interface StepRepository extends JpaRepository<Step, UUID> {

    Optional<List<Step>> findFirst2ByGameIdAndGame_Status_NotOrderByUpdatedDesc(UUID gameId, GameStatus status);

    Optional<List<Step>> findByIsUserStepAndUpdatedBeforeAndGame_Status_NotOrderByUpdatedDesc(Boolean isUserStep,
            LocalDateTime updatedBefore, GameStatus status);

    void deleteByIdIn(List<UUID> ids);

}
