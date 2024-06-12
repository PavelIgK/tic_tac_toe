package ru.pikistenev.tictactoe.mainservice.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;
import java.util.Set;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.DynamicUpdate;
import ru.pikistenev.tictactoe.mainservice.enums.GameStatus;
import ru.pikistenev.tictactoe.mainservice.enums.Winner;

@Entity
@Table(name = "game")
@SuperBuilder
@Getter
@Setter
@DynamicUpdate
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class Game extends BaseEntity {
    @Column(name = "is_user_start")
    private Boolean isUserStart;

    @OneToMany(mappedBy = "game", fetch = FetchType.EAGER)
    Set<Step> steps;

    @Transient
    @Builder.Default
    private String[] board = new String[9];

    @Enumerated(EnumType.STRING)
    private GameStatus status;

    @Enumerated(EnumType.STRING)
    private Winner winner;

    @Column(name = "ai_symbol")
    private String aiSymbol;

    @Column(name = "user_symbol")
    private String userSymbol;


    public String[] getBoard() {

        if (this.steps.isEmpty()) {
            return new String[9];
        }

        this.steps.forEach(step -> {
            Integer cell = step.getCell();
            String symbol = step.getIsUserStep() ? this.userSymbol : this.aiSymbol;
            this.board[cell] = symbol;
        });
        return this.board;
    }
}
