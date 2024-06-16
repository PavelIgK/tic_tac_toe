package ru.pikistenev.tictactoe.mainservice.utils;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import ru.pikistenev.tictactoe.mainservice.config.TtoConfig;
import ru.pikistenev.tictactoe.mainservice.model.Game;
import ru.pikistenev.tictactoe.mainservice.repository.GameRepository;

/**
 * Класс для удаления брошенных игр.
 */

@Slf4j
@Component
@RequiredArgsConstructor
public class RemoveGame {

    private final GameRepository gameRepository;
    private final TtoConfig ttoConfig;

    /**
     * Удаляем игры брошенные более чем заданное параметрами приложения время.
     */
    @Scheduled(fixedRateString = "${tto.inactive-time}")
    public void removeInactiveGames() {
        log.debug("Стартуем удаление игр брошенных более чем: {} минут назад.",
                ttoConfig.getInactiveTime() / 1000 / 60);

        LocalDateTime oldTime = LocalDateTime.now().minusSeconds(ttoConfig.getInactiveTime() / 1000);

        log.debug("Ишем игры брошенные до {}", oldTime);
        Optional<List<Game>> idsToRemoveTwo = gameRepository.inactiveGame(oldTime);
        if (idsToRemoveTwo.isPresent()) {
            log.debug("Найдено {} игр для удаления", idsToRemoveTwo.get().size());
            gameRepository.deleteAllInBatch(idsToRemoveTwo.get());
        }
        log.debug("Закончили удаление брошенных игр");
    }

}
