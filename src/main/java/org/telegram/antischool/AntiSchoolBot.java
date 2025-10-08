package org.telegram.antischool;

import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.abilitybots.api.bot.AbilityBot;
import org.telegram.abilitybots.api.bot.BaseAbilityBot;
import org.telegram.abilitybots.api.objects.Ability;
import org.telegram.abilitybots.api.objects.Flag;
import org.telegram.abilitybots.api.objects.Reply;
import org.telegram.antischool.handlers.ResponseHandler;
import org.telegram.antischool.services.WordService;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.util.function.BiConsumer;

import static org.telegram.abilitybots.api.objects.Locality.USER;
import static org.telegram.abilitybots.api.objects.Privacy.PUBLIC;
import static org.telegram.abilitybots.api.util.AbilityUtils.getChatId;

@Setter
@Getter
@Component
public class AntiSchoolBot extends AbilityBot {

    private ResponseHandler responseHandler;
    private static final String TOKEN = "";
    private static final String TEST_TOKEN = "";
    private static final String BOT_USER_NAME = "acm_word_collector_bot";
    private static final String TEST_BOT_USER_NAME = "test_word_collector_bot";

    @Autowired
    public AntiSchoolBot(WordService service) {
        this(service, null);
    }

    // Extra constructor for testing
    public AntiSchoolBot(WordService service, ResponseHandler mockHandler) {
        super(TEST_TOKEN, TEST_BOT_USER_NAME);
        this.responseHandler = mockHandler != null ? mockHandler : new ResponseHandler(silent, sender, db, service);
    }
    @Override
    public long creatorId() {
        return 1L;
    }

    public Ability startBot() {
        return Ability
                .builder()
                .name("start")
                .info(Constants.START_DESCRIPTION)
                .locality(USER)
                .privacy(PUBLIC)
                .action(ctx -> responseHandler().replyToStart(ctx.chatId()))
                .build();
    }

    public Reply replyToButtons() {
        BiConsumer<BaseAbilityBot, Update> action = (abilityBot, upd) -> responseHandler().replyToButtons(getChatId(upd), upd.getMessage());
        return Reply.of(action, Flag.TEXT, upd -> responseHandler().userIsActive(getChatId(upd)));
    }

    public ResponseHandler responseHandler() {
        return this.responseHandler;
    }
}
