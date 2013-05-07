package net.archwill.MiniWebServer;

import static org.junit.Assert.*;
import static org.hamcrest.CoreMatchers.*;
import org.junit.Test;

public class HeaderTest {
  @Test
  public void testHeaderKeyAndValue() {
    Header header = new Header("Host", "localhost");
    assertThat(header.getKey(), is("Host"));
    assertThat(header.getValue(), is("localhost"));
  }

  @Test
  public void testHeaderKeyOnly() {
    Header header = new Header("Host");
    assertThat(header.getKey(), is("Host"));
    assertThat(header.getValue(), is(""));
  }

  @Test(expected=InvalidHeaderException.class)
  public void testHeaderEmptyKey() {
    Header header = new Header("");
    fail("Expected an InvalidHeaderException to be thrown");
  }

  @Test(expected=InvalidHeaderException.class)
  public void testHeaderBlankKey() {
    Header header = new Header("  ");
    fail("Expected an InvalidHeaderException to be thrown");
  }

  @Test
  public void testHeaderEmptyValue() {
    Header header = new Header("Host", "");
    assertThat(header.getValue(), is(""));
  }

  @Test
  public void testHeaderBlankValue() {
    Header header = new Header("Host", "  ");
    assertThat(header.getValue(), is(""));
  }

  @Test
  public void testHeaderSpacePaddedKey() {
    Header header = new Header("  Host  ");
    assertThat(header.getKey(), is("Host"));
  }

  @Test
  public void testHeaderSpacePaddedValue() {
    Header header = new Header("Host", "  localhost  ");
    assertThat(header.getValue(), is("localhost"));
  }

  @Test
  public void testHeaderLowercase() {
    Header header = new Header("host");
    assertThat(header.getKey(), is("Host"));
  }

  @Test
  public void testHeaderLowercaseHyphen() {
    Header header = new Header("content-length");
    assertThat(header.getKey(), is("Content-Length"));
  }

  @Test
  public void testHeaderUppercase() {
    Header header = new Header("HOST");
    assertThat(header.getKey(), is("Host"));
  }

  @Test
  public void testHeaderUppercaseHyphen() {
    Header header = new Header("CONTENT-LENGTH");
    assertThat(header.getKey(), is("Content-Length"));
  }

  @Test
  public void testHeaderToStringKeyAndValue() {
    Header header = new Header("Host", "localhost");
    assertThat(header.toString(), is("Host: localhost"));
  }

  @Test
  public void testHeaderToStringKeyOnly() {
    Header header = new Header("Host");
    assertThat(header.toString(), is("Host: "));
  }

  @Test
  public void testHeaderParseStandard() {
    Header header = Header.parseHeader("Host: localhost");
    assertThat(header.getKey(), is("Host"));
    assertThat(header.getValue(), is("localhost"));
  }

  @Test
  public void testHeaderParseMultipleColon() {
    Header header = Header.parseHeader("Host: localhost:80");
    assertThat(header.getKey(), is("Host"));
    assertThat(header.getValue(), is("localhost:80"));
  }

  @Test
  public void testHeaderParseInvertedSpace() {
    Header header = Header.parseHeader("Host :localhost");
    assertThat(header.getKey(), is("Host"));
    assertThat(header.getValue(), is("localhost"));
  }

  @Test(expected=InvalidHeaderException.class)
  public void testHeaderParseEmptyKey() {
    Header header = Header.parseHeader("  : localhost");
    fail("Expected an InvalidHeaderException to be thrown");
  }

  @Test(expected=InvalidHeaderException.class)
  public void testHeaderParseBlankKey() {
    Header header = Header.parseHeader(": localhost");
    fail("Expected an InvalidHeaderException to be thrown");
  }

  @Test(expected=InvalidHeaderException.class)
  public void testHeaderParseEmptyValue() {
    Header header = Header.parseHeader("Host:");
    fail("Expected an InvalidHeaderException to be thrown");
  }

  @Test(expected=InvalidHeaderException.class)
  public void testHeaderParseBlankValue() {
    Header header = Header.parseHeader("Host:  ");
    fail("Expected an InvalidHeaderException to be thrown");
  }

  @Test(expected=InvalidHeaderException.class)
  public void testHeaderParseNoColon() {
    Header header = Header.parseHeader("Host");
    fail("Expected an InvalidHeaderException to be thrown");
  }
}
