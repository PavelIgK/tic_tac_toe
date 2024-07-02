package ru.pikistenev.tictactoe.bot.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import ru.pikistenev.tictactoe.bot.enums.GameStep;
import ru.pikistenev.tictactoe.bot.model.BotGame;
import ru.pikistenev.tictactoe.bot.repository.BotGameRepository;
import ru.pikistenev.tictactoe.bot.utils.Keyboard;
import ru.pikistenev.tictactoe.mainservice.config.TtoConfig;
import ru.pikistenev.tictactoe.mainservice.enums.GameLevel;
import ru.pikistenev.tictactoe.mainservice.enums.GameStatus;
import ru.pikistenev.tictactoe.mainservice.model.Game;
import ru.pikistenev.tictactoe.mainservice.service.GameService;


@Slf4j
@Component
@Transactional(readOnly = true)
public class TelegramBot extends TelegramLongPollingBot {

    private final TtoConfig ttoConfig;
    private final Keyboard keyboard;
    private final BotGameRepository botGameRepository;
    private final GameService gameService;

    public TelegramBot(@Value("${bot.token}") String botToken,
            TtoConfig ttoConfig,
            Keyboard keyboard,
            BotGameRepository botGameRepository,
            GameService gameService) {
        super(botToken);
        this.ttoConfig = ttoConfig;
        this.keyboard = keyboard;
        this.botGameRepository = botGameRepository;
        this.gameService = gameService;
    }

    @Override
    public String getBotUsername() {
        return ttoConfig.getBotName();
    }


    @Override
    public void onUpdateReceived(Update update) {

        if (update.hasCallbackQuery()) {
            Long userId = update.getCallbackQuery().getFrom().getId();
            callbackHandler(update);
        }

        if (update.hasMessage() && update.getMessage().hasText()) {
            Long userId = update.getMessage().getFrom().getId();
            log.debug("MESSAGE = " + update.getMessage().toString());
            log.debug("CHAT = " + update.getMessage().getChat().toString());
            messageHandler(update);
        }
    }


    /**
     * Отправка сообщения от бота.
     *
     * @param chatId     айди чата
     * @param textToSend текст для отправки
     */
    private void sendMessage(Long chatId, String textToSend) {
        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(String.valueOf(chatId));
        sendMessage.setText(textToSend);
        try {
            execute(sendMessage);
        } catch (TelegramApiException e) {
            log.error(e.getMessage());
        }
    }

    /**
     * Приветственное сообщение.
     *
     * @param chatId айди чата
     * @param name   имя пользователя
     */
    private void startCommandReceived(Long chatId, String name) {
        StringBuilder result = new StringBuilder();
        result.append("Привет, ")
                .append(name)
                .append(" это бот игры в крестики нолики.")
                .append(" Команда /play для начала игры.");

        sendMessage(chatId, result.toString());
    }


    /**
     * Метод обработки сообщения боту.
     *
     * @param update входящий апдейт.
     */
    private void messageHandler(Update update) {
        String defaultMessage = "Такой команды нет.";
        String messageText = update.getMessage().getText();
        String command = messageText.contains(" ") ? messageText.substring(0, messageText.indexOf(" ")) : messageText;
        command = command.contains("@") ? command.substring(0, command.indexOf("@")) : command;

        String commandText = messageText.contains(" ") ? messageText.substring(messageText.indexOf(" ") + 1) : "";

        long chatId = update.getMessage().getChatId();
        String userFirstName = update.getMessage().getFrom().getFirstName();
        Long userId = update.getMessage().getFrom().getId();

        switch (command) {
            case "/start":
                startCommandReceived(chatId, userFirstName);
                break;
            case "/play":
                startGame(chatId);
                break;
        }
    }

    @Transactional
    protected void startGame(long chatId) {
        log.debug("Стартуем игру для {}", chatId);

        Integer messageId = null;
        BotGame botGame = BotGame.builder()
                .chatId(chatId)
                .textMessage("Пожалуйста, выберите кто будет ходить первым.")
                .gameStep(GameStep.NEW)
                .build();;

        SendMessage message = SendMessage.builder()
                .chatId(chatId)
                .text(botGame.getTextMessage())
                .replyMarkup(keyboard.getKeyboard(botGame))
                .build();

        try {
            messageId = execute(message).getMessageId();
        } catch (TelegramApiException e) {
            log.error(e.getMessage());
        }
        botGame.setMessageId(messageId);
        botGame.setGameStep(GameStep.CHOICE_FIRST_PLAYER);
        botGame = botGameRepository.save(botGame);
        log.debug("START GAME: {}", botGame);
    }

    /**
     * Метод для обработки команды на уже имеющемся сообщении
     *
     * @param update входящий апдейт.
     */
    @Transactional
    protected void callbackHandler(Update update) {

        Long chatId = update.getCallbackQuery().getMessage().getChat().getId();
        int messageId = update.getCallbackQuery().getMessage().getMessageId();

        String buttonCommand = update.getCallbackQuery().getData();
        String userFirstName = update.getCallbackQuery().getFrom().getFirstName();
        String userLastName = update.getCallbackQuery().getFrom().getLastName();
        String userName = update.getCallbackQuery().getFrom().getUserName();

        log.debug("Пользователь {} {} ник {}, нажал команду {}", userFirstName, userLastName, userName, buttonCommand);
        BotGame botGame = botGameRepository.findByChatIdAndMessageId(chatId, messageId);
        if (botGame == null) {
            return;
        }

        InlineKeyboardMarkup keyboardMarkup = null;

        //Если игра завершена - выходим.
        if (GameStep.FINISHED.equals(botGame.getGameStep())) {
            return;
        }

        //Если игра в процессе - обрабатываем ход, если игра завершилась то обновляем статус.
        if (GameStep.DURING.equals(botGame.getGameStep())) {
            log.debug("Ход = {} ", buttonCommand);
            Game game = gameService.userStep(botGame.getGameId(), Integer.parseInt(buttonCommand));
            botGame.setTextMessage("Ваша очередь ходить. Вы играете символом: " + ttoConfig.getUserSymbol());
            if (GameStatus.FINISHED.equals(game.getStatus())) {
                botGame.setStatus(GameStatus.FINISHED);
                botGame.setGameStep(GameStep.FINISHED);
                botGame.setWinner(game.getWinner());

                botGame.setTextMessage(botGame.getWinner().getValue() + "\n Игра закончена. Приходите еще.");
            }
            keyboardMarkup = keyboard.getKeyboard(botGame);
        }


        //Обработчик выбора уровня игры, после чего игра переходит в статус в процессе
        if (GameStep.CHOICE_LEVEL.equals(botGame.getGameStep())) {
            switch (buttonCommand.toString()) {
                case "EASY" -> botGame.setLevel(GameLevel.EASY);
                case "MEDIUM" -> botGame.setLevel(GameLevel.MEDIUM);
                case "HARD" -> botGame.setLevel(GameLevel.HARD);
            }

            //Стартуем игру
            Game game = gameService.startGame(botGame.isUserStart(),botGame.getLevel());
            //Запоминаем id созданной игры
            botGame.setGameId(game.getId());
            botGame.setTextMessage("Игра началась, выберите свободную ячейку. Вы играете символом: " + ttoConfig.getUserSymbol());
            keyboardMarkup = keyboard.getKeyboard(botGame);
            botGame.setGameStep(GameStep.DURING);
        }

        //Если стартуем новую игру сначала выбираем кто ходит первым и потом переходим к выбору сложности
        if (GameStep.CHOICE_FIRST_PLAYER.equals(botGame.getGameStep())) {
            keyboardMarkup = keyboard.getKeyboard(botGame);
            botGame.setUserStart("USER".equals(buttonCommand));
            botGame.setTextMessage("Давайте теперь выберем сложность игры.");
            botGame.setGameStep(GameStep.CHOICE_LEVEL);
        }


        //Сохраняем игру
        botGameRepository.save(botGame);

        //Формируем обновленное сообщение.
        EditMessageText editMessageText = EditMessageText.builder()
                .chatId(botGame.getChatId())
                .text(botGame.getTextMessage())
                .messageId(botGame.getMessageId())
                .replyMarkup(keyboardMarkup)
                .build();

        try {
            execute(editMessageText);
        } catch (TelegramApiException e) {
            log.error(e.getMessage());
        }

        log.debug(botGame.toString());
    }

}