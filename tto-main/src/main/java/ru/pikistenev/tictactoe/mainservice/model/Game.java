package ru.pikistenev.tictactoe.mainservice.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.SuperBuilder;
import ru.pikistenev.tictactoe.mainservice.enums.GameLevel;
import ru.pikistenev.tictactoe.mainservice.enums.GameStatus;
import ru.pikistenev.tictactoe.mainservice.enums.Winner;


/**
 * Сущность для хранения информации об игре.
 */

@Entity
@Table(name = "game")
@SuperBuilder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class Game extends BaseEntity {
    @Column(name = "is_user_start")
    private Boolean isUserStart;

    @OneToMany(mappedBy = "game", fetch = FetchType.EAGER)
    List<Step> steps;

    //TODO заготовка для разных алгоритмов подбора следующего хода, пока всегда EASY
    @Enumerated(EnumType.STRING)
    private GameLevel level;

    @Enumerated(EnumType.STRING)
    private GameStatus status;

    @Enumerated(EnumType.STRING)
    private Winner winner;

    @Column(name = "ai_symbol")
    private String aiSymbol;

    @Column(name = "user_symbol")
    private String userSymbol;
}
