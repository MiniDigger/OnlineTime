package me.MiniDigger.OnlineTime;

import me.MiniDigger.OnlineTime.OnlineTime.Reward;
import org.junit.Test;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

/**
 * Created by Martin on 04.07.2016.
 */
public class DateParseTest {

    @Test
    public void parse1D() {
        assertThat( Reward.parseTime( "1D" ), is( 1728000L ) );
    }

    @Test
    public void parse1H() {
        assertThat( Reward.parseTime( "1H" ), is( 72000L ) );
    }

    @Test
    public void parse1M() {
        assertThat( Reward.parseTime( "1M" ), is( 1200L ) );
    }

    @Test
    public void parse1S() {
        assertThat( Reward.parseTime( "1S" ), is( 20L ) );
    }

    @Test
    public void parse1D1H1M1S() {
        assertThat( Reward.parseTime( "1D1H1M1S" ), is( 1801220L ) );
    }

    @Test
    public void parse1H1M1S() {
        assertThat( Reward.parseTime( "1H1M1S" ), is( 73220L ) );
    }
}
