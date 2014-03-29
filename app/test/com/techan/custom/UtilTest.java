package test.com.techan.custom;

import com.techan.custom.Util;
import com.xtremelabs.robolectric.RobolectricTestRunner;

import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.Calendar;
import java.util.Date;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.assertThat;

@RunWith(RobolectricTestRunner.class)
public class UtilTest {

    @Test
    public void testIsDateSameWhenSame() {
        Calendar curCal = Calendar.getInstance();
        Calendar cal1 = (Calendar)curCal.clone();
        Calendar cal2 = (Calendar)curCal.clone();

        Date date1 = cal1.getTime();
        String dateStr1 = Util.FORMATER.format(date1);

        assertThat(Util.isDateSame(dateStr1, cal2), equalTo(true));
    }

    @Test
    public void testIsDateSameWhenDiff() {
        Calendar curCal = Calendar.getInstance();
        Calendar cal1 = (Calendar)curCal.clone();
        Calendar cal2 = (Calendar)curCal.clone();

        cal1.add(Calendar.DAY_OF_MONTH,1);
        Date date1 = cal1.getTime();
        String dateStr1 = Util.FORMATER.format(date1);

        assertThat(!Util.isDateSame(dateStr1, cal2), equalTo(true));
    }

    @Test
    public void testDateDiffWhenSame() {
        Calendar curCal = Calendar.getInstance();
        Calendar cal1 = (Calendar)curCal.clone();
        Calendar cal2 = (Calendar)curCal.clone();

        assertThat(Util.dateDiff(cal1, cal2), equalTo(0));
    }

    @Test
    public void testDateDiffWhenLess() {
        Calendar curCal = Calendar.getInstance();
        Calendar cal1 = (Calendar)curCal.clone();
        Calendar cal2 = (Calendar)curCal.clone();

        cal1.add(Calendar.DAY_OF_MONTH, -10);
        assertThat(Util.dateDiff(cal1, cal2), equalTo(10));
    }

    @Test
    public void testDateDiffWhenGreater() {
        Calendar curCal = Calendar.getInstance();
        Calendar cal1 = (Calendar)curCal.clone();
        Calendar cal2 = (Calendar)curCal.clone();

        cal1.add(Calendar.DAY_OF_MONTH, 20);
        assertThat(Util.dateDiff(cal1, cal2), equalTo(0));
    }
}
