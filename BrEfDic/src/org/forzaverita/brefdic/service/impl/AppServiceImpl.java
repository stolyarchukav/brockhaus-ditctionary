package org.forzaverita.brefdic.service.impl;

import java.util.Map;
import java.util.Random;

import org.forzaverita.brefdic.data.WordsCache;
import org.forzaverita.brefdic.preferences.TextAlignment;
import org.forzaverita.brefdic.service.AppService;
import org.forzaverita.brefdic.service.DatabaseService;
import org.forzaverita.brefdic.service.PreferencesService;
import org.forzaverita.brefdic.widget.WidgetRefreshTask;

import android.app.Application;
import android.graphics.Typeface;

public class AppServiceImpl extends Application implements AppService {
	
	private Typeface font;
	private DatabaseService dataBaseService;
	private Random random = new Random();
	private WidgetRefreshTask widgetRefreshTask;
	private WordsCache wordsCache = new WordsCache();
	private PreferencesService preferencesService;
	
	@Override
	public void onCreate() {
		super.onCreate();
		font = Typeface.createFromAsset(getAssets(), "philosopher.otf");
		preferencesService = new PreferencesServiceImpl(this);
		dataBaseService = new DataBaseServiceImpl(this, preferencesService);
	}
	
	@Override
	public void onTerminate() {
		super.onTerminate();
		dataBaseService.close();
	}
	
	@Override
	public boolean isDatabaseReady() {
		return dataBaseService.isDatabaseReady();
	}
	
	@Override
	public void openDatabase() {
		dataBaseService.open();
	}
	
	@Override
	public Typeface getFont() {
		return font;
	}
	
	@Override
	public Map<Integer, String> getWordsBeginWith(char letter) {
		return dataBaseService.getWordsBeginWith(letter, preferencesService.isCapitalLetters());
	}
	
	@Override
	public Map<Integer, String> getWordsBeginWith(String begin) {
		return dataBaseService.getWordsBeginWith(begin, preferencesService.isCapitalLetters());
	}
	
	@Override
	public Map<Integer, String> getWordsFullSearch(String query) {
		return dataBaseService.getWordsFullSearch(query, preferencesService.isCapitalLetters());
	}
	
	@Override
	public String[] getDescription(Integer id) {
		return dataBaseService.getDescription(id);
	}
	
	@Override
	public String getNextWord() {
		String[] word = wordsCache.next();
		if (word == null) {
			word = generateRandomWord();
			wordsCache.addToEnd(word);
		}
		return word[1];
	}
	
	@Override
	public String getPreviuosWord() {
		String[] word = wordsCache.previuos();
		if (word == null) {
			word = generateRandomWord();
			wordsCache.addToBegin(word);
		}
		return word[1];
	}

	private String[] generateRandomWord() {
		int count = dataBaseService.getWordsCount();
		String[] wordAndDesc = null;
		while (wordAndDesc == null || wordAndDesc[1].contains("<")) {
			int id = random.nextInt(count) + 1;
			wordAndDesc = dataBaseService.getWordAndDescriptionById(id);
		}
		return wordAndDesc;
	}
	
	@Override
	public String[] getCurrentWord() {
		String[] word = wordsCache.current();
		if (word == null) {
			word = generateRandomWord();
			wordsCache.addToEnd(word);
		}
		return word;
	}
	
	@Override
	public WidgetRefreshTask getWidgetRefreshTask() {
		return widgetRefreshTask;
	}
	
	@Override
	public void setWidgetRefreshTask(WidgetRefreshTask task) {
		widgetRefreshTask = task;
	}
	
	@Override
	public boolean isAutoRefresh() {
		return preferencesService.isAutoRefresh();
	}
	
	@Override
	public int getRefreshInterval() {
		return preferencesService.getRefreshInterval();
	}
	
	@Override
	public TextAlignment getWordTextAlign() {
		return preferencesService.getWordTextAlign();
	}
	
	@Override
	public Map<Integer, String> getHistory() {
		return preferencesService.getHistory();
	}
	
	@Override
	public void addToHistory(Integer id, String word) {
		preferencesService.addToHistory(id, word);
	}
	
	@Override
	public String getWordById(Integer wordId) {
		return dataBaseService.getWordById(wordId);
	}
	
}
