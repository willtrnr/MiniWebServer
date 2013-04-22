package com.willisite.MiniWebServer;

import static org.junit.Assert.*;
import static org.hamcrest.CoreMatchers.*;
import org.junit.Test;

public class HeaderTest {
  @Test
  public void testHeader() throws InvalidHeaderException {
    Header header = new Header("Host");
    assertThat(header.getKey(), is("Host"));
    assertThat(header.getValue(), is(""));
  }

  @Test
  public void testHeader2() throws InvalidHeaderException {
    Header header = new Header("Host", "localhost");
    assertThat(header.getKey(), is("Host"));
    assertThat(header.getValue(), is("localhost"));
  }

  @Test(expected=InvalidHeaderException.class)
  public void testHeaderEmptyKey() throws InvalidHeaderException {
    Header header = new Header("");
    fail("Expected an InvalidHeaderException to be thrown");
  }

  @Test(expected=InvalidHeaderException.class)
  public void testHeaderEmptyKey2() throws InvalidHeaderException {
    Header header = new Header("  ");
    fail("Expected an InvalidHeaderException to be thrown");
  }

  @Test
  public void testHeaderEmptyValue() throws InvalidHeaderException {
    Header header = new Header("Host", "");
    assertThat(header.getValue(), is(""));
  }

  @Test
  public void testHeaderEmptyValue2() throws InvalidHeaderException {
    Header header = new Header("Host", "  ");
    assertThat(header.getValue(), is(""));
  }

  @Test
  public void testHeaderSpacePaddedKey() throws InvalidHeaderException {
    Header header = new Header("  Host  ");
    assertThat(header.getKey(), is("Host"));
  }

  @Test
  public void testHeaderSpacePaddedValue() throws InvalidHeaderException {
    Header header = new Header("Host", "  localhost  ");
    assertThat(header.getValue(), is("localhost"));
  }

  @Test
  public void testHeaderLowercase() throws InvalidHeaderException {
    Header header = new Header("host");
    assertThat(header.getKey(), is("Host"));
  }

  @Test
  public void testHeaderLowercase2() throws InvalidHeaderException {
    Header header = new Header("content-length");
    assertThat(header.getKey(), is("Content-Length"));
  }

  @Test
  public void testHeaderUppercase() throws InvalidHeaderException {
    Header header = new Header("HOST");
    assertThat(header.getKey(), is("Host"));
  }

  @Test
  public void testHeaderUppercase2() throws InvalidHeaderException {
    Header header = new Header("CONTENT-LENGTH");
    assertThat(header.getKey(), is("Content-Length"));
  }

  @Test
  public void testHeaderToString() throws InvalidHeaderException {
    Header header = new Header("Host", "localhost");
    assertThat(header.toString(), is("Host: localhost"));
  }

  @Test
  public void testHeaderToString2() throws InvalidHeaderException {
    Header header = new Header("host", "localhost");
    assertThat(header.toString(), is("Host: localhost"));
  }

  @Test
  public void testHeaderToString3() throws InvalidHeaderException {
    Header header = new Header("Host");
    assertThat(header.toString(), is("Host: "));
  }
}
