package ru.pikistenev.tictactoe.bot.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;
import java.time.LocalDateTime;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import ru.pikistenev.tictactoe.bot.enums.GameStep;
import ru.pikistenev.tictactoe.mainservice.enums.GameLevel;
import ru.pikistenev.tictactoe.mainservice.enums.GameStatus;
import ru.pikistenev.tictactoe.mainservice.enums.Winner;


/**
 * Сущность для хранения информации об игре.
 */

@Entity
@Table(name = "bot_game")
@SuperBuilder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString()
@EqualsAndHashCode(of = {"id"})
public class BotGame {

    @Id
    @Column(updatable = false)
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @CreationTimestamp
    @Column(updatable = false)
    private LocalDateTime created;

    @UpdateTimestamp
    private LocalDateTime updated;

    @Enumerated(EnumType.STRING)
    @Column(name = "game_step")
    private GameStep gameStep;

    @Column(name = "chat_id")
    private long chatId;

    @Column(name = "game_id")
    private UUID gameId;

    @Column(name = "message_id")
    private int messageId;

    @Column(name = "is_user_start")
    private boolean isUserStart;

    @Enumerated(EnumType.STRING)
    private GameLevel level;

    @Enumerated(EnumType.STRING)
    private GameStatus status;

    @Enumerated(EnumType.STRING)
    private Winner winner;

    @Transient
    private String textMessage;

}
