package ru.pikistenev.tictactoe.mainservice.repository;

import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import ru.pikistenev.tictactoe.mainservice.model.Step;

public interface StepRepository extends JpaRepository<Step, UUID> {

}
