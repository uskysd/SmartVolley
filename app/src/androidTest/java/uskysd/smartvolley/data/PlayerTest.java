package uskysd.smartvolley.data;

import android.test.AndroidTestCase;

import org.joda.time.DateTime;

import java.util.Calendar;
import java.util.Date;
import java.util.IllegalFormatCodePointException;


/**
 * Created by uskysd on 11/1/2015.
 */
public class PlayerTest extends AndroidTestCase {

    public void testGettingFullname() {
        //Setup
        Player sut = new Player("Firstname", "Lastname");
        String expected = "Firstname Lastname";

        //Exercise
        String actual = sut.getFullName();

        //Verify
        assertEquals(expected, actual);

    }

    public void testSettingAsMale() {
        //Setup
        Player sut = new Player();

        //Exercise
        sut.setAsMale();

        //Verify
        assertEquals(true, sut.isMale());
        assertEquals(false, sut.isFemale());

    }

    public void testSettingAsFemale() {
        //Setup
        Player sut = new Player();

        //Exercise
        sut.setAsFemale();

        //Verify
        assertEquals(true, sut.isFemale());
        assertEquals(false, sut.isMale());
    }

    public void testCannotSetNegativeValueForHeight() {
        //Setup
        Player sut = new Player();
        float expected = (float)190.0;
        sut.setHeight(expected);

        //Exercise
        try {
            sut.setHeight((float)-10.0);
        } catch (IllegalArgumentException e) {
            //do nothing
        }
        float actual = sut.getHeight();

        //Verify

        assertEquals(expected, actual);
    }

    public void testCannotSetNegativeValueForWeight(){
        //Setup
        Player sut = new Player();
        float expected = (float)80.0;
        sut.setWeight(expected);

        //Exercise
        try {
            sut.setWeight((float)-10.0);
        } catch (IllegalArgumentException e) {
            // do nothing
        }
        float actual = sut.getWeight();

        //Verify
        assertEquals(expected, actual);

    }











}
