package ru.pikistenev.tictactoe.bot.repository;

import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import ru.pikistenev.tictactoe.bot.model.BotGame;

public interface BotGameRepository extends JpaRepository<BotGame, UUID> {
    BotGame findByChatIdAndMessageId(long chatId, int messageId);
}

