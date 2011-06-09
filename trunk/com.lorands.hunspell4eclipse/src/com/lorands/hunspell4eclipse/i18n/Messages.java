/*******************************************************************************
 * Copyright (c) 2011 isandlaTech, Thomas Calmant
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Thomas Calmant (isandlaTech) - initial API and implementation
 *    Olivier Gattaz (isandlaTech) - implementation of the chain of resources
 *******************************************************************************/

package com.lorands.hunspell4eclipse.i18n;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;
import java.util.Locale;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

import com.stibocatalog.hunspell.CLog;

/**
 * @author Olivier Gattaz < olivier dot gattaz at isandlatech dot com >
 * @date 09/06/2011 (dd/mm/yy)
 */
class CRessource {

	/** Base resource bundle */
	private static final String BUNDLE_NAME = "com.lorands.hunspell4eclipse.i18n.messages";

	/** Prefix of variables inside message to reference other keys values */
	private static final String VARIABLE_PREFIX = "${";

	/** Suffix of variables inside message to reference other keys values */
	private static final String VARIABLE_SUFFIX = "}";

	/**
	 * checks if the System property "hunspell.forced.locale" exists and sets
	 * its content as the default locale if any
	 * 
	 * @return the locale which must be used to instanciate the first CResource
	 *         instance
	 */
	private static Locale getDefaultLocale() {

		// get the System property "hunspell.forced.locale"
		String wHunspellLocale = System.getProperty("hunspell.forced.locale");
		// test if we need to do something
		if (wHunspellLocale != null && !wHunspellLocale.isEmpty()
				&& wHunspellLocale.contains("_")) {
			String[] wArgs = wHunspellLocale.split("_");
			Locale wLocale = new Locale(wArgs[0], wArgs[1], "");
			// force the HunspellLocale as the default.
			Locale.setDefault(wLocale);

			if (CLog.on())
				CLog.logOut(CRessource.class, "getDefaultLocale",
						"HunspellForcedLocale=[%s][%s]", wLocale.toString(),
						wLocale.getDisplayName());

		}
		return Locale.getDefault();
	}

	/**
	 * try to load a bundle, if not exists, try to load the english one
	 * delivered in the plugin
	 * 
	 * @param aLocale
	 *            the explicit locale of the ResourceBundle
	 * @return an instance of ResourceBundle
	 */
	private static ResourceBundle loadBundle(Locale aLocale) {
		try {
			return ResourceBundle.getBundle(BUNDLE_NAME, aLocale);
		} catch (MissingResourceException e) {
			return ResourceBundle.getBundle(BUNDLE_NAME, Locale.ENGLISH);
		}
	}

	/** Resource bundle to use */
	private ResourceBundle pBundle = null;

	/** The Exception thrown during the loading of the resource **/
	private Exception pLoadingError = null;

	/** the parent resource if any **/
	private CRessource pResourceParent = null;

	/**
	 * 
	 */
	CRessource() {
		this(getDefaultLocale());
	}

	/**
	 * @param aLocale
	 *            the explicit locale of the ResourceBundle
	 */
	private CRessource(Locale aLocale) {
		try {
			// Initialization of bundles
			pBundle = loadBundle(aLocale);

		} catch (Exception e) {
			pLoadingError = e;
			pLoadingError.printStackTrace();
		}

		// if the loaded bundle isn't the english one , load the english one as
		// the parent resource
		if (isInitOk()
				&& !pBundle.getLocale().getLanguage()
						.equals(Locale.ENGLISH.getLanguage())) {
			pResourceParent = new CRessource(Locale.ENGLISH);
		}

		if (CLog.on())
			CLog.logOut(
					this,
					CLog.LIB_CONSTRUCTOR,
					"initOk=[%b] LoadingError=[%s] Currentlanguage=[%s] hasResourceParent=[%b]",
					isInitOk(), getLoadingErrorInfos(), getCurrentlanguage(),
					hasResourceParent());
	}

	/**
	 * @param aError
	 *            the errror to dump
	 * @return a structured message containing
	 *         ErrorClassname,ErrorMessage,ClassWhereAppears
	 *         ,MethodWhereAppear,LineWhereAppear
	 */
	private String buildErrorInfos(Throwable aError) {
		StringBuilder wSB = new StringBuilder();
		wSB.append(aError.getClass().getSimpleName());
		String wMess = aError.getLocalizedMessage();
		if (wMess != null && !wMess.isEmpty())
			wSB.append(',').append(wMess);
		if (aError.getStackTrace() != null && aError.getStackTrace().length > 0) {
			StackTraceElement wSTE = aError.getStackTrace()[0];
			wSB.append(',').append(wSTE.getClassName());
			wSB.append(':').append(wSTE.getMethodName());
			wSB.append('(').append(wSTE.getLineNumber()).append(')');
		}
		return wSB.toString();
	}

	/**
	 * @param aKey
	 * @param aError
	 * @return
	 */
	private String buildStringError(String aKey, Throwable aError) {
		return "<str><key>" + aKey + "</key><error>" + buildErrorInfos(aError)
				+ "</error></str>";
	}

	/**
	 * Replaces variables inside the given message by their value
	 * 
	 * @param aStr
	 *            Message to be completed
	 * @return The transformed message
	 */
	private String completeString(final String aStr) {

		int posVarStart = 0;
		int posVarNameStart = 0;
		int posVarEnd = 0;

		StringBuilder builder = new StringBuilder(aStr);

		// For each variable...
		while ((posVarStart = builder.indexOf(VARIABLE_PREFIX, posVarStart)) != -1) {

			posVarNameStart = posVarStart + VARIABLE_PREFIX.length();

			// Search for the end of the variable
			posVarEnd = builder.indexOf(VARIABLE_SUFFIX, posVarNameStart);
			if (posVarEnd == -1) {
				break;
			}

			// Extract variable name and get its value
			String variableName = builder.substring(posVarNameStart, posVarEnd);
			String variableKey = getString(variableName);

			builder.replace(posVarStart, posVarEnd + 1, variableKey);
		}

		return builder.toString();
	}

	/**
	 * Tests whether the resource bundle contains the given key or not
	 * 
	 * @param aKey
	 *            The key to be tested
	 * @return True if the key is present, else false
	 */
	public boolean containsKey(final String aKey) {
		boolean wHasKey = pBundle.containsKey(aKey);
		if (!wHasKey && hasResourceParent())
			wHasKey = pResourceParent.containsKey(aKey);

		return wHasKey;
	}

	/**
	 * Retrieves the resource bundle
	 * 
	 * @return The resource bundle
	 */
	public ResourceBundle getBundle() {
		return pBundle;
	}

	/**
	 * @return
	 */
	String getCurrentlanguage() {
		return (isInitOk()) ? pBundle.getLocale().getLanguage() : "no resource";
	}

	/**
	 * @return
	 */
	String getLoadingErrorInfos() {
		return (!isInitOk()) ? buildErrorInfos(pLoadingError) : "no error";
	}

	/**
	 * Retrieves the localized string corresponding to this key
	 * 
	 * @param aKey
	 *            String key
	 * @param aArgs
	 *            some arguments to replace the % tokens present in the message
	 * @return The localized string
	 */
	public String getString(final String aKey, Object... aArgs) {

		if (!isInitOk())
			return buildStringError(aKey, pLoadingError);

		String wStr = null;

		try {

			try {
				// Raw message
				wStr = pBundle.getString(aKey);

			} catch (MissingResourceException e) {
				if (this.hasResourceParent())
					return pResourceParent.getString(aKey, aArgs);
			}

			// Trim right all lines
			String line;
			StringBuilder trimmedMessage = new StringBuilder(wStr.length());
			BufferedReader reader = new BufferedReader(new StringReader(wStr));

			try {

				while ((line = reader.readLine()) != null) {
					// Only way to do an rtrim in Java...
					trimmedMessage.append(line.replaceAll("\\s+$", ""));

					// The EOL marker has been deleted
					trimmedMessage.append("\n");
				}

				// Delete the last \n : it is an artificial one
				trimmedMessage.deleteCharAt(trimmedMessage.length() - 1);

				wStr = trimmedMessage.toString();

			} catch (IOException e) {
				// can't appear (StringReader) ! Do nothing...
			}

			wStr = completeString(wStr);

			// replace
			if (aArgs != null && aArgs.length > 0)
				wStr = String.format(wStr, aArgs);

		} catch (Exception ex) {
			ex.printStackTrace();
			return buildStringError(aKey, ex);
		}

		return wStr;
	}

	/**
	 * @return
	 */
	boolean hasResourceParent() {
		return pResourceParent != null;
	}

	/**
	 * @return
	 */
	boolean isInitOk() {
		return pLoadingError == null;
	}
}

/**
 * Internationalization handler. Loads strings from a resource bundle
 * (.properties file).
 * 
 * @author Thomas Calmant < thomas dot calmant at isandlatech dot com >
 * @author Olivier Gattaz < olivier dot gattaz at isandlatech dot com >
 * @date 09/06/2011 (dd/mm/yy)
 */
public class Messages {
	private final static Messages sMessage = new Messages();

	/**
	 * @return
	 */
	public static Messages getInstance() {
		return sMessage;
	}

	private final CRessource pRessource;

	/**
	 * No constructor allowed
	 */
	private Messages() {
		pRessource = new CRessource();
	}

	/**
	 * Tests whether the resource bundle contains the given key or not
	 * 
	 * @param aKey
	 *            The key to be tested
	 * @return True if the key is present, else false
	 */
	public boolean containsKey(final String aKey) {
		return pRessource.containsKey(aKey);
	}

	/**
	 * Retrieves the localized string corresponding to this key
	 * 
	 * @param aKey
	 *            String key
	 * @param aArgs
	 *            some arguments to replace the % tokens present in the message
	 * @return The localized string
	 */
	public String getString(final String aKey, Object... aArgs) {
		return pRessource.getString(aKey, aArgs);
	}
}
