
package basic.driver;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class MainTest {
    @Test
    public void f()
    {
        try {
            Main.compile("cases/case00.bas");
            Main.compile("cases/case01.bas");
            Main.compile("cases/case02.bas");
            Main.compile("cases/case03.bas");
            //Main.compile("cases/case04.bas");
            Main.compile("cases/case05.bas");
            Main.compile("cases/case06.bas");
            Main.compile("cases/case07.bas");
            //Main.compile("cases/case08.bas");
            Main.compile("cases/case09.bas");
            Main.compile("cases/case10.bas");
            Main.compile("cases/case11.bas");
        }
        catch( Exception e ) {
            assertEquals(1, 2);
        }
    }
}
