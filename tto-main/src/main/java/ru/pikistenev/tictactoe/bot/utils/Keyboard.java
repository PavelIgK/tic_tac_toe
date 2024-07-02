package ru.pikistenev.tictactoe.bot.utils;


import java.util.ArrayList;
import java.util.List;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import ru.pikistenev.tictactoe.bot.model.BotGame;
import ru.pikistenev.tictactoe.mainservice.mapper.GameMapper;
import ru.pikistenev.tictactoe.mainservice.model.Game;
import ru.pikistenev.tictactoe.mainservice.service.GameService;


/**
 * Формируем клавиатуру для сообщения с голосованием.
 */
@Component
@AllArgsConstructor
public class Keyboard {

    private final GameService gameService;
    private final GameMapper gameMapper;

    public InlineKeyboardMarkup getKeyboard(BotGame botGame) {
        switch (botGame.getGameStep()) {
            case NEW -> {
                return getStartButton();
            }
            case CHOICE_FIRST_PLAYER -> {
                return getLevelBoard();
            }
            case DURING, CHOICE_LEVEL -> {
                return getBoard(botGame);
            }
        }
        return getBoard(botGame);
    }

    private InlineKeyboardButton createButton(String text, String command) {
        InlineKeyboardButton button = new InlineKeyboardButton();
        button.setText(text.trim());
        button.setCallbackData(command);

        return button;
    }

    private InlineKeyboardMarkup getBoard(BotGame botGame) {
        Game game = gameService.getBoard(botGame.getGameId());

        List<String> board = gameMapper.toGameResponse(game).getBoard();
        InlineKeyboardMarkup inlineKeyboardMarkup = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> rows = new ArrayList<>();

        List<InlineKeyboardButton> lineButtons = new ArrayList<>();
        for (int i = 0; i < board.size(); i++) {
            if (i % 3 == 0) {
                rows.add(lineButtons);
                lineButtons = new ArrayList<>();
            }
            lineButtons.add(createButton(board.get(i).equals("") ? "_" : board.get(i), String.valueOf(i)));
        }
        rows.add(lineButtons);

        inlineKeyboardMarkup.setKeyboard(rows);
        return inlineKeyboardMarkup;
    }

    private InlineKeyboardMarkup getStartButton() {
        InlineKeyboardMarkup inlineKeyboardMarkup = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> rows = new ArrayList<>();
        List<InlineKeyboardButton> lineButtons = new ArrayList<>();

        lineButtons.add(createButton("Бот", "AI"));
        lineButtons.add(createButton("Я первый", "USER"));
        rows.add(lineButtons);
        inlineKeyboardMarkup.setKeyboard(rows);
        return inlineKeyboardMarkup;
    }

    private InlineKeyboardMarkup getLevelBoard() {
        InlineKeyboardMarkup inlineKeyboardMarkup = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> rows = new ArrayList<>();
        List<InlineKeyboardButton> lineButtons = new ArrayList<>();

        lineButtons.add(createButton("Легкий", "EASY"));
        lineButtons.add(createButton("Средний", "MEDIUM"));
        lineButtons.add(createButton("Сложный", "HARD"));
        rows.add(lineButtons);
        inlineKeyboardMarkup.setKeyboard(rows);
        return inlineKeyboardMarkup;
    }
}
