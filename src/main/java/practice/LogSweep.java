package practice;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.ResourceBundle;

/**
 * 基準差分日数より古いファイルをバックアップディレクトリに移動するクラス。
 *
 * @author user yosuke
 */
public class LogSweep {

	/** 検索ディレクトリ**/
	public static final String DIR = "C:\\test2\\";

	/** 入力日付 **/
	public static Date inputDate;

	/** 更新日付 **/
	public static Date updatedDate;

	/** 日付書式 **/
	public static SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");

	public static void main(String[] args) {

		// 実行日取得
		BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
		try {
			System.out.println("半角文字8桁で日付を入力して下さい");
			String inputDateStr = reader.readLine();
			inputDate = parseDate(inputDateStr);
		} catch (IOException e) {
			e.printStackTrace();
		} catch (ParseException e) {
			e.printStackTrace();
		}

		// 基準差分日数取得
		ResourceBundle rb = ResourceBundle.getBundle("config");
		long baseDiff = Long.parseLong(rb.getString("TIMES"));
		System.out.println("基準日数："+baseDiff);

		// ファイル検索処理
		File files[] = new File(DIR).listFiles();
		for (File file: files) {
			String updatedDateStr = sdf.format(file.lastModified());
			try {
				updatedDate = sdf.parse(updatedDateStr);
			} catch (ParseException e) {
				e.printStackTrace();
			}
			// 実行日と更新日の差分取得
			long dayDiff = calculateDateDiff(inputDate, updatedDate);

		// 差分が基準差分以上であれば、所定のバックアップディレクトリに移動
			if (dayDiff >= baseDiff) {
				sweepLogs(file);
			}
		}
	}

	/**
	 * Date型変換処理
	 * @param date 文字列
	 * @return 変換されたDate値
	 * @throws ParseException
	 */
	private static Date parseDate(String date) throws ParseException {
		return sdf.parse(date);
	}

	/**
	 * ログファイルを移動する。
	 * @param file 移動するファイル
	 */
	private static void sweepLogs(File file) {
		// ファイル名の年月日を抽出する
		String fileName     = file.getName();
		String fileNameY   = fileName.substring(0,4);
		String fileNameYM  = fileName.substring(0,6);
		String fileNameYMD = fileName.substring(0,8);

		// バックアップディレクトリを生成する
		String bkupDirectory = DIR + fileNameY + "\\" +fileNameYM + "\\" + fileNameYMD;
		File newFile = new File(bkupDirectory);
		if (newFile.mkdirs()) {
			System.out.println("ディレクトリの生成が完了しました");
			file.renameTo(new File(bkupDirectory + "\\" + fileName));
		} else {
			System.out.println("ディレクトリの生成に失敗しました");
		}
	}

	/**
	 * 実行日と更新日の差分日数を計算し返却する。
	 * @param sdf 日付書式
	 * @param inputDate 実行日
	 * @param updatedDate 更新日
	 * @return 実行日と更新日の差分日数
	 */
	private static long calculateDateDiff(Date date1, Date date2) {
		long dayDiff = 0;
		long inputDateLong = date1.getTime();
		long updatedDateLong = date2.getTime();
		dayDiff = (inputDateLong - updatedDateLong) / (1000 * 60 * 60 * 24);
		return dayDiff;
	}
}
