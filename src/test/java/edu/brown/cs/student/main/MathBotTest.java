package edu.brown.cs.student.main;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class MathBotTest {

  @Test
  public void testAddition() {
    MathBot matherator9000 = new MathBot();
    double output = matherator9000.add(10.5, 3);
    assertEquals(13.5, output, 0.01);
  }

  @Test
  public void testLargerNumbers() {
    MathBot matherator9001 = new MathBot();
    double output = matherator9001.add(100000, 200303);
    assertEquals(300303, output, 0.01);
  }

  @Test
  public void testSubtraction() {
    MathBot matherator9002 = new MathBot();
    double output = matherator9002.subtract(18, 17);
    assertEquals(1, output, 0.01);
  }

  @Test
  public void testSubtractionNegativeResult() {
    MathBot m = new MathBot();
    double output = m.subtract(5,17);
    assertEquals(-12, output, 0.01);
  }

  @Test
  public void negativeInputAddition() {
    MathBot m = new MathBot();
    double output = m.add(-4, 3.5);
    assertEquals(-0.5, output, 0.01);
  }

  @Test
  public void negativeInputSubtraction() {
    MathBot m = new MathBot();
    double output = m.subtract(-5, -3);
    assertEquals(-2, output, 0.01);
  }

  @Test
  public void subtractDoubles() {
    MathBot m = new MathBot();
    double output = m.subtract(8.9, 2.031);
    assertEquals(6.869, output, 0.01);
  }
}
