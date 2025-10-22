package org.telegram.antischool.handlers;

import org.telegram.abilitybots.api.db.DBContext;
import org.telegram.abilitybots.api.sender.MessageSender;
import org.telegram.abilitybots.api.sender.SilentSender;
import org.telegram.antischool.Constants;
import org.telegram.antischool.UserState;
import org.telegram.antischool.dto.WordItem;
import org.telegram.antischool.services.WordService;
import org.telegram.telegrambots.meta.api.methods.send.SendAudio;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.InputFile;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardRemove;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import reactor.core.publisher.Mono;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import static org.telegram.antischool.Constants.CHANGE_PERIOD_TEXT;
import static org.telegram.antischool.Constants.START_TEXT;
import static org.telegram.antischool.UserState.*;

public class ResponseHandler {
    private final SilentSender sender;
    private final MessageSender defSender;
    private final DBContext db;
    private final WordService service;
    private final Map<Long, ChatContext> chatContextes;
    private ScheduledExecutorService commonExecutorService;


    public ResponseHandler(SilentSender sender, MessageSender defSender, DBContext db, WordService service) {
        this.sender = sender;
        this.defSender = defSender;
        this.service = service;
        this.db = db;
        chatContextes = new ConcurrentHashMap<>();
        commonExecutorService = Executors.newScheduledThreadPool(1);
        long delay = 0;
        LocalDateTime nineAM = LocalDate.now(ZoneId.of("Europe/Kyiv")).atTime(6, 0);
        LocalDateTime now = LocalDateTime.now(ZoneId.of("Europe/Kyiv"));
        if (nineAM.compareTo(now) < 1) {
            delay = 24 - (now.getHour() - nineAM.getHour());
        } else {
            delay = 24 + (nineAM.getHour() - now.getHour());
        }
        commonExecutorService.scheduleAtFixedRate(this::newDayWakeUp, delay, 24, TimeUnit.HOURS);
    }

    public void replyToStart(long chatId) {
        SendMessage message = new SendMessage();
        ChatContext ctx = new ChatContext(chatId);
        ctx.getExecutorService().scheduleAtFixedRate(() -> wakeUp(ctx),0, ctx.getDelay(), TimeUnit.HOURS);
        ctx.setChatState((UserState) db.getMap(Constants.CHAT_STATES).get(chatId));
        chatContextes.put(chatId, ctx);
        message.setChatId(chatId);
        message.setText(START_TEXT);
        KeyboardRow row = new KeyboardRow();
        row.add("10");
        row.add("15");
        row.add("20");
        row.add("30");
        message.setReplyMarkup(new ReplyKeyboardMarkup(List.of(row)));
        sender.execute(message);
        ctx.setChatState(AWAITING_COUNT);
    }

    public void replyToChangePeriod(ChatContext ctx) {
        SendMessage message = new SendMessage();
        ctx.setChatState((UserState) db.getMap(Constants.CHAT_STATES).get(ctx.getChatId()));
        chatContextes.put(ctx.getChatId(), ctx);
        message.setChatId(ctx.getChatId());
        message.setText(CHANGE_PERIOD_TEXT);
        KeyboardRow row1 = new KeyboardRow();
        row1.add("1");
        row1.add("2");
        KeyboardRow row2 = new KeyboardRow();
        row2.add("3");
        row2.add("4");
        KeyboardRow row3 = new KeyboardRow();
        row3.add("5");
        row3.add("6");
        message.setReplyMarkup(new ReplyKeyboardMarkup(List.of(row1, row2, row3)));
        sender.execute(message);
        ctx.setChatState(AWAITING_PERIOD);
    }

    public void newDayWakeUp() {
        try {
            chatContextes.forEach((chatId, ctx) -> {
                stopChat(ctx);
                replyToStart(chatId);
            });
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    public void replyToButtons(long chatId, Message message) {
        ChatContext ctx = chatContextes.get(chatId);
        if ("/stop".equals(message.getText()) || "stop".equals(message.getText())) {
            stopChat(ctx);
        } else if ("/start".equals(message.getText()) || "start".equals(message.getText())) {
            replyToStart(chatId);
        } else if ("to learned words".equals(message.getText())) {
            service.changeWordStatus(List.of(ctx.getCurrentWord()), 2).flatMap(m ->
                   service.getToLearnWords(1).map(w -> {
                       List<WordItem> list = new ArrayList<>(ctx.getWords());
                       list.add(w.getFirst());
                       ctx.setWords(list);
                       ctx.removeCurrentWord();
                       ctx.getWords().forEach(System.out::println);
                       return Mono.just(w);
                   })).subscribe();
            getNextWord(ctx);
        } else if ( "move to learned all words".equals(message.getText())) {
            service.changeWordStatus(ctx.getWords(), 2).doOnNext(m -> System.out.println(m.size())).subscribe();
        } else if ( "delete all words".equals(message.getText())) {
            service.deleteWords(ctx.getWords()).doOnNext(m -> System.out.println(m.size())).subscribe();
        } else if ("change period".equals(message.getText())) {
            replyToChangePeriod(ctx);
        } else if ("add word to antischool".equals(message.getText())) {
            requestWordToAdd(ctx);
        } else if (ctx.getChatState() == AWAITING_PERIOD) {
            ctx.setDelay(Integer.parseInt(message.getText()));
            ctx.getExecutorService().shutdown();
            ctx.setExecutorService(Executors.newScheduledThreadPool(1));
            ctx.getExecutorService().scheduleAtFixedRate(() -> wakeUp(ctx),0, ctx.getDelay(), TimeUnit.HOURS);
        } else if (ctx.getChatState() == AWAITING_COUNT) {
            receiveDataFromDB(ctx, message);
        } else if (ctx.getChatState() == AWAITING_TRANSLATE) {
            getTranslation(ctx);
        } else if (ctx.getChatState() == AWAITING_WORD_TO_ADD) {
            service.translateWord(message.getText()).doOnNext(m -> System.out.println(m.size())).subscribe();
        } else if (ctx.getChatState() == PROCESSING) {
            getNextWord(ctx);
        }
    }

    private void receiveDataFromDB(ChatContext ctx, Message message) {
        int count = Integer.parseInt(message.getText());
        if (ctx.getWords() == null) {
            service.getToLearnWords(count).doOnNext(m -> {
                ctx.setWords(m);
                ctx.setIterators(m.iterator());
                ctx.setChatState(PROCESSING);
                getNextWord(ctx);
            }).subscribe();
        } else {
            ctx.setIterators(ctx.getWords().iterator());
            ctx.setChatState(PROCESSING);
            getNextWord(ctx);
        }
    }

    private void unexpectedMessage(ChatContext ctx) {
        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(ctx.getChatId());
        sendMessage.setText("I did not expect that.");
        sender.execute(sendMessage);
    }

    private void stopChat(ChatContext ctx) {
        try {
            SendMessage sendMessage = new SendMessage();
            sendMessage.setChatId(ctx.getChatId());
            sendMessage.setText("Thank you for your order. See you soon!\nPress /start to order again");
            chatContextes.remove(ctx.getChatId());
            sendMessage.setReplyMarkup(new ReplyKeyboardRemove(true));
            sender.execute(sendMessage);
        } finally {
            ctx.getExecutorService().shutdown();
        }
    }

    public void getTranslation(ChatContext ctx) {
        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(ctx.getChatId());
        sendMessage.setText(ctx.getCurrentWord().getTranslation());
        KeyboardRow row = new KeyboardRow();
        row.add("next");
        row.add("to learned words");
        ctx.setChatState(PROCESSING);
        sendMessage.setReplyMarkup(new ReplyKeyboardMarkup(List.of(row)));
        sender.execute(sendMessage);
        try {
            defSender.sendAudio(getAudio(ctx));
        } catch (TelegramApiException e) {
            unexpectedMessage(ctx);
            throw new RuntimeException(e);
        }
    }

    public SendAudio getAudio(ChatContext ctx) {
        SendAudio audio = new SendAudio();
        audio.setAudio(new InputFile(ctx.getCurrentWord().getAudio()));
        audio.setChatId(ctx.getChatId());
        audio.setCaption(ctx.getCurrentWord().getWord() + "\n" + generateWaveform());
        audio.setTitle(ctx.getCurrentWord().getTranslation());
        return audio;
    }

    public void requestWordToAdd(ChatContext ctx) {
        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(ctx.getChatId());
        KeyboardRow row = new KeyboardRow();
        row.add("add word");
        ctx.setChatState(AWAITING_WORD_TO_ADD);
        sendMessage.setReplyMarkup(new ReplyKeyboardMarkup(List.of(row)));
    }

    public void getNextWord(ChatContext ctx) {
        var iter = ctx.getIterators();
        SendMessage sendMessage = new SendMessage();
        if (iter.hasNext()) {
            WordItem word = iter.next();
            ctx.setCurrentWord(word);
            sendMessage.setChatId(ctx.getChatId());
            sendMessage.setText(word.getWord());
            KeyboardRow row = new KeyboardRow();
            row.add("translate");
            row.add("to learned words");
            ctx.setChatState(AWAITING_TRANSLATE);
            sendMessage.setReplyMarkup(new ReplyKeyboardMarkup(List.of(row)));
        } else {
            sendMessage.setChatId(ctx.getChatId());
            sendMessage.setText("pause");
            KeyboardRow row1 = new KeyboardRow();
            row1.add("move to learned all words");
            row1.add("delete all words");
            KeyboardRow row2 = new KeyboardRow();
            row2.add("add word to antischool");
            row2.add("change period");
            KeyboardRow row3 = new KeyboardRow();
            row3.add("stop");
            row3.add("start");
            sendMessage.setReplyMarkup(new ReplyKeyboardMarkup(List.of(row1, row2, row3)));
        }
        sender.execute(sendMessage);
    }

    private void wakeUp(ChatContext ctx) {
        LocalDateTime tenPM = LocalDate.now(ZoneId.of("Europe/Kyiv")).atTime(22, 0); // 22:00 (10 PM)
        long date = tenPM.atZone(ZoneId.of("Europe/Kyiv")).toInstant().toEpochMilli();
        if (System.currentTimeMillis() < date) {
            ctx.setIterators(ctx.getWords().iterator());
            getNextWord(ctx);
        } else {
            ctx.getExecutorService().shutdown();
            ctx.setExecutorService(Executors.newScheduledThreadPool(ctx.getDelay()));
        }
    }

    private static String generateWaveform() {
        Random random = new Random();
        String[] bars = {"▁", "▂", "▃", "▄", "▅", "▆", "▇", "█"}; // From low to high
        StringBuilder waveform = new StringBuilder();

        for (int i = 0; i < 10; i++) { // 15 bars
            waveform.append(bars[random.nextInt(bars.length)]).append(" ");
        }

        return waveform.toString();
    }

    public boolean userIsActive(Long chatId) {
        return chatContextes.containsKey(chatId);
    }

}
