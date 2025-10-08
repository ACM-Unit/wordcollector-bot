package org.telegram.antischool.handlers;

import lombok.Data;
import org.telegram.antischool.UserState;
import org.telegram.antischool.dto.WordItem;

import java.util.Iterator;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;


@Data
public class ChatContext {

    public ChatContext(long chatId) {
        this.chatId = chatId;
        this.executorService = Executors.newScheduledThreadPool(1);
    }
    private int delay = 1;
    private long chatId;
    private UserState chatState;
    private List<WordItem> words;
    private Iterator<WordItem> iterators;
    private WordItem currentWord;
    private ScheduledExecutorService executorService;
    private List<WordItem> ownWordsToLearn;

    public void removeCurrentWord() {
        this.words.remove(this.currentWord);
    }

}
