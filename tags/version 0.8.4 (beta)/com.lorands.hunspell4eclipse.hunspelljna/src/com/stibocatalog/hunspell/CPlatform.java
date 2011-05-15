package com.stibocatalog.hunspell;

/**
 * 
 * Gives information about the context of the JVM.
 * 
 * <pre>
 *   java.class.version=[50.0]
 *    java.runtime.name=[Java(TM) SE Runtime Environment]
 * java.runtime.version=[1.6.0_22-b04-307-10M3261]
 * pecification.version=[1.6]
 *            java.home=[/System/Library/Java/JavaVirtualMachines/1.6.0.jdk/Contents/Home]
 *          java.vendor=[Apple Inc.]
 *      java.vendor.url=[http://www.apple.com/]
 *         java.version=[1.6.0_22]
 *            java.home=[/System/Library/Java/JavaVirtualMachines/1.6.0.jdk/Contents/Home]
 *              os.arch=[x86_64]
 *              os.name=[Mac OS X]
 *           os.version=[10.6.6]
 *             user.dir=[/Users/xxxxx/workspaces/hunspell/]
 *            user.home=[/Users/xxxxx]
 *        user.language=[fr]
 *            user.name=[xxxxx]
 *          user.region=[null]
 *        user.timezone=[Europe/Paris]
 *      java.class.path=[/Applications/Java_ME_SDK_3.0.app/Contents/Resources/lib/jsr082_1.1.jar:
 *                       /Applications/Java_ME_SDK_3.0.app/Contents/Resources/lib/jsr120_1.1.jar:
 *                       /Applications/Java_ME_SDK_3.0.app/Contents/Resources/lib/jsr135_1.2.jar]
 *    java.library.path=[.:/Library/Java/Extensions:/System/Library/Java/Extensions:/usr/lib/java]
 *       defaultCharset=[UTF-8]
 *  supported.encodings=[Big5               Big5-HKSCS         EUC-JP             EUC-KR             GB18030            ;
 *                       GB2312             GBK                IBM-Thai           IBM00858           IBM01140           ;
 *                       IBM01141           IBM01142           IBM01143           IBM01144           IBM01145           ;
 *                       IBM01146           IBM01147           IBM01148           IBM01149           IBM037             ;
 *                       IBM1026            IBM1047            IBM273             IBM277             IBM278             ;
 *                       IBM280             IBM284             IBM285             IBM297             IBM420             ;
 *                       IBM424             IBM437             IBM500             IBM775             IBM850             ;
 *                       IBM852             IBM855             IBM857             IBM860             IBM861             ;
 *                       IBM862             IBM863             IBM864             IBM865             IBM866             ;
 *                       IBM868             IBM869             IBM870             IBM871             IBM918             ;
 *                       ISO-2022-CN        ISO-2022-JP        ISO-2022-JP-2      ISO-2022-KR        ISO-8859-1         ;
 *                       ISO-8859-13        ISO-8859-15        ISO-8859-2         ISO-8859-3         ISO-8859-4         ;
 *                       ISO-8859-5         ISO-8859-6         ISO-8859-7         ISO-8859-8         ISO-8859-9         ;
 *                       JIS_X0201          JIS_X0212-1990     KOI8-R             KOI8-U             MacRoman           ;
 *                       Shift_JIS          TIS-620            US-ASCII           UTF-16             UTF-16BE           ;
 *                       UTF-16LE           UTF-32             UTF-32BE           UTF-32LE           UTF-8              ;
 *                       windows-1250       windows-1251       windows-1252       windows-1253       windows-1254       ;
 *                       windows-1255       windows-1256       windows-1257       windows-1258       windows-31j        ;
 *                       x-Big5-Solaris     x-euc-jp-linux     x-EUC-TW           x-eucJP-Open       x-IBM1006          ;
 *                       x-IBM1025          x-IBM1046          x-IBM1097          x-IBM1098          x-IBM1112          ;
 *                       x-IBM1122          x-IBM1123          x-IBM1124          x-IBM1381          x-IBM1383          ;
 *                       x-IBM33722         x-IBM737           x-IBM834           x-IBM856           x-IBM874           ;
 *                       x-IBM875           x-IBM921           x-IBM922           x-IBM930           x-IBM933           ;
 *                       x-IBM935           x-IBM937           x-IBM939           x-IBM942           x-IBM942C          ;
 *                       x-IBM943           x-IBM943C          x-IBM948           x-IBM949           x-IBM949C          ;
 *                       x-IBM950           x-IBM964           x-IBM970           x-ISCII91          x-ISO-2022-CN-CNS  ;
 *                       x-ISO-2022-CN-GB   x-iso-8859-11      x-JIS0208          x-JISAutoDetect    x-Johab            ;
 *                       x-MacArabic        x-MacCentralEurope x-MacCroatian      x-MacCyrillic      x-MacDingbat       ;
 *                       x-MacGreek         x-MacHebrew        x-MacIceland       x-MacRomania       x-MacSymbol        ;
 *                       x-MacThai          x-MacTurkish       x-MacUkraine       x-MS932_0213       x-MS950-HKSCS      ;
 *                       x-mswin-936        x-PCK              x-SJIS_0213        x-UTF-16LE-BOM     X-UTF-32BE-BOM     ;
 *                       X-UTF-32LE-BOM     x-windows-50220    x-windows-50221    x-windows-874      x-windows-949      ;
 *                       x-windows-950      x-windows-iso2022jp]
 * 
 * </pre>
 * 
 * The Hunspell java bindings are licensed under LGPL, see the file COPYING.txt
 * in the root of the distribution for the exact terms.
 * 
 * @author L—r‡nd Somogyi < lorand dot somogyi at gmail dot com >
 *         http://lorands.com
 * @author Olivier Gattaz < olivier dot gattaz at isandlatech dot com >
 * @date 28/04/2011 (dd/mm/yy)
 * 
 */
public class CPlatform {

	private final static String ARCH_AMD64 = "amd64";
	private final static String ARCH_IA64N = "ia64n";
	private final static String ARCH_X386 = "i386";
	private final static String ARCH_X686 = "i686";
	private final static String ARCH_X86 = "x86";
	private final static String ARCH_X8664 = "x86_64";

	private final static String OS_LINUX = "linux";
	private final static String OS_MACOSX = "mac os x";
	private final static String OS_SOLARIS = "sunos";
	private final static String OS_WINDOWS = "windows";

	private final static String PROP_OS_NAME = "os.name";
	private final static String PROP_OSARCH = "os.arch";
	private final static String PROP_USER_HOME = "user.home";
	private final static String PROP_USER_LANGUAGE = "user.language";

	/**
	 * @return
	 */
	public static String getOsArch() {
		return System.getProperty(PROP_OSARCH);
	}

	/**
	 * @return
	 */
	public static String getOsName() {
		return System.getProperty(PROP_OS_NAME);
	}

	/**
	 * @return
	 */
	public static String getUserHome() {
		return System.getProperty(PROP_USER_HOME);
	}

	/**
	 * @return
	 */
	public static String getUserLanguage() {
		return System.getProperty(PROP_USER_LANGUAGE);
	}

	/**
	 * Annoying that Java doesn't have consistent names for the arch types:
	 * 
	 * @return
	 */
	public static boolean is32Bits() {
		String wArch = getOsArch();
		if (wArch == null)
			return false;
		wArch = wArch.toLowerCase();
		return ARCH_X86.equals(wArch) || ARCH_X386.equals(wArch)
				|| ARCH_X686.equals(wArch);
	}

	/**
	 * Annoying that Java doesn't have consistent names for the arch types:
	 * 
	 * @return
	 */
	public static boolean is64Bits() {
		String wArch = getOsArch();
		if (wArch == null)
			return false;
		wArch = wArch.toLowerCase();
		return ARCH_X8664.equals(wArch) || ARCH_AMD64.equals(wArch)
				|| ARCH_IA64N.equals(wArch);
	}

	/**
	 * @return
	 */
	public static boolean isLinux() {
		return isOs(OS_LINUX);
	}

	/**
	 * @return
	 */
	public static boolean isMacOs() {
		return isOs(OS_MACOSX);
	}

	/**
	 * @return
	 */
	public static boolean isMacPowerPc() {
		String wArch = getOsArch();
		return (wArch != null) ? "ppc".equals(wArch.toLowerCase()) : false;

	}

	/**
	 * @param aOs
	 * @return
	 */
	public static boolean isOs(String aOs) {
		String wOs = getOsName();
		return (wOs != null) ? wOs.toLowerCase().startsWith(aOs) : false;
	}

	/**
	 * @return
	 */
	public static boolean isSolaris() {
		return isOs(OS_SOLARIS);
	}

	/**
	 * @return
	 */
	public static boolean isWindows() {
		return isOs(OS_WINDOWS);
	}

}
