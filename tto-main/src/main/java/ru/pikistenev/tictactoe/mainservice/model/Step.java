package ru.pikistenev.tictactoe.mainservice.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.SuperBuilder;

/**
 * Сущность для хранения информации о шаге.
 */

@Entity
@Table(name = "step")
@SuperBuilder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString(exclude = "game", callSuper = true)
public class Step extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    private Game game;
    private int cell;

    @Column(name = "is_user_step")
    private boolean isUserStep;

}
