package com.tennisapp.util;

import com.tennisapp.config.DictionaryKeysConfig;

import java.util.ResourceBundle;

public class DictionaryUtil {

	public static String getDictionaryValue(DictionaryKeysConfig key) {
		return ResourceBundle.getBundle("dictionary").getString(key.name());
	}

	public static String getDictionaryValueWithParams(DictionaryKeysConfig key, String... strings) {
		return String.format(ResourceBundle.getBundle("dictionary").getString(key.name()), strings);
	}

}
