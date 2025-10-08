package org.telegram.antischool;


import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.telegram.abilitybots.api.db.DBContext;
import org.telegram.abilitybots.api.sender.MessageSender;
import org.telegram.abilitybots.api.sender.SilentSender;
import org.telegram.antischool.handlers.ResponseHandler;
import org.telegram.antischool.services.WordService;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import static org.mockito.Mockito.*;


class AntischoolApplicationTests {

    private WordService wordService;
    private SilentSender silent;
    private MessageSender sender;
    private DBContext db;
    private AntiSchoolBot bot;

    @BeforeEach
    public void setUp() {
        wordService = mock(WordService.class);
        silent = mock(SilentSender.class);
        sender = mock(MessageSender.class);
        db = mock(DBContext.class);

        // Create anonymous subclass that overrides the core AbilityBot dependencies
        bot = new AntiSchoolBot(wordService, new ResponseHandler(silent, sender, db, wordService));
    }

    @Test
    public void testStartAbility() throws TelegramApiException {
        long testChatId = 123L;

        // Create mock message
        Message message = mock(Message.class);
        when(message.getChatId()).thenReturn(testChatId);
        when(message.hasText()).thenReturn(true); // important for Flag.TEXT
        when(message.getText()).thenReturn("/start");
        // Create mock update
        Update update = mock(Update.class);
        when(update.getMessage()).thenReturn(message);
        when(update.hasMessage()).thenReturn(true);
        // Simulate user is active

        // --- Act ---
        bot.replyToButtons().action().accept(bot, update);
        // --- Assert ---
        verify(silent, atLeastOnce()).execute(any()); // adjust based on handler
    }

}
