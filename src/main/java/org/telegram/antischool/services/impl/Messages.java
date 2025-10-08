package org.telegram.antischool.services.impl;

import org.telegram.antischool.dto.SendRequestModel;
import org.telegram.antischool.dto.WordItem;

public class Messages {

    public static SendRequestModel getToLearnVocabularyMessage(int count) {
            SendRequestModel msg = new SendRequestModel();
            msg.setController("VocabularyWsController");
            msg.setMethod("LoadVocabulary");
            msg.setProjectName("Vocabulary");
            msg.setValue(new SendRequestModel.WordsValue(2248251, "",count, true));
            return msg;
    }

    protected static SendRequestModel getLearnedVocabularyMessage(int count) {
            SendRequestModel msg = new SendRequestModel();
            msg.setController("VocabularyWsController");
            msg.setMethod("GetLearnedVocabulary");
            msg.setProjectName("Vocabulary");
            msg.setValue(new SendRequestModel.WordsValue(2248251, "", count, true));
            return msg;
    }

    protected static SendRequestModel getPupilStatMessage() {
        SendRequestModel msg = new SendRequestModel();
        msg.setController("VocabularyWsController");
        msg.setMethod("GetPupilVocabularyStatistics");
        msg.setProjectName("Vocabulary");
        msg.setValue(new SendRequestModel.StatValue(2248251, "", 0, 0, false));
        return msg;
    }

    protected static SendRequestModel getOnePupilStatMessage() {
        SendRequestModel msg = new SendRequestModel();
        msg.setController("VocabularyWsController");
        msg.setMethod("GetPupilVocabularyStatistics");
        msg.setProjectName("Vocabulary");
        msg.setValue(new SendRequestModel.StatValue(2248251, "", 0, 0, false));
        return msg;
    }

    protected static SendRequestModel getChangeStatusMessage(Integer[] wordsId, int status) {
        SendRequestModel msg = new SendRequestModel();
        msg.setController("VocabularyWsController");
        msg.setMethod("ChangeWordStatuses");
        msg.setProjectName("Vocabulary");
        msg.setValue(new SendRequestModel.ChangeStatusValue(wordsId, status));
        return msg;
    }

    protected static SendRequestModel deleteWordMessage(Integer[] wordIds) {
        SendRequestModel msg = new SendRequestModel();
        msg.setController("WordWsController");
        msg.setMethod("DeleteWords");
        msg.setProjectName("Vocabulary");
        msg.setValue(new SendRequestModel.ValueForDelete(wordIds));
        return msg;
    }

    protected static SendRequestModel translateWordMessage(String text) {
        SendRequestModel msg = new SendRequestModel();
        msg.setController("TranslateWordWsController");
        msg.setMethod("GetWordTranslate");
        msg.setProjectName("TranslateWords");
        msg.setValue(new SendRequestModel.TranslateWordValue(text, "En", 1, "Ru", 0, true, 0));
        return msg;
    }

    protected static SendRequestModel addWordMessage(WordItem word) {
        SendRequestModel msg = new SendRequestModel();
        msg.setController("VocabularyWsController");
        msg.setMethod("SaveWord");
        msg.setProjectName("Vocabulary");
        msg.setValue(new SendRequestModel.SaveWordValue(0, 0, Integer.parseInt(word.getFileId()), word.getAudio(), 2248251, word.getTranslation(), word.getWord()));
        return msg;
    }
}
