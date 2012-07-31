package co.gargoyle.supercab.android.utilities;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Date;

import android.text.Html;
import android.text.Spanned;
import android.text.format.DateUtils;

public class StringUtils {

  public static String convertStreamToString(InputStream is) throws Exception {
    BufferedReader reader = new BufferedReader(new InputStreamReader(is));
    StringBuilder sb = new StringBuilder();
    String line = null;

    while ((line = reader.readLine()) != null) {
      sb.append(line);
    }

    is.close();

    return sb.toString();
  }

  public static boolean stringIsEmpty(String str) {
    if(str == null || str.length() == 0) {
      return true;
    } else {
      return false;
    }
  }

  public static CharSequence getNiceTime(Date time) {
    CharSequence timeString = DateUtils.getRelativeTimeSpanString(
        time.getTime(),
        System.currentTimeMillis(),
        DateUtils.MINUTE_IN_MILLIS,
        DateUtils.FORMAT_ABBREV_RELATIVE
        );
    return timeString;
  }

  public static Spanned makeWebLinkFromUrl(String url, String displayText) {
    return Html.fromHtml(String.format("<a href=\"%s\">%s</a>",
                         url,
                         displayText));
  }

}
