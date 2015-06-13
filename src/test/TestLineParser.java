package test;

import java.util.List;

import io.miti.beetle.console.LineParser;

public final class TestLineParser
{
  public TestLineParser() {
    super();
  }
  
  
  private void parse(final String cmd, final int expectedNum) {
    List<String> cmds = new LineParser().parseIntoPhrases(cmd);
    
    System.out.println("Input: " + cmd);
    final int size = cmds.size();
    System.out.println("  Size: " + size + " commands");
    for (int i = 0; i < size; ++i) {
      System.out.println(String.format("  #%d: %s", i + 1, cmds.get(i)));
    }
    System.out.flush();
    System.out.flush();
    System.out.flush();
    
    if (size != expectedNum) {
      System.err.println("Error: Expected " + expectedNum + " but found " + size);
      System.err.flush();
    }
  }
  
  
  public static void main(final String[] args) {
    TestLineParser tlp = new TestLineParser();
    tlp.parse("fake spec ID@id", 3);
    tlp.parse("fake spec ID@id,NAME@fullname", 3);
    tlp.parse("fake spec \"ID@id,NAME@fullname\"", 3);
    tlp.parse("fake spec \"ID@id NAME@fullname\"", 3);
    tlp.parse("fake spec ID@id NAME@fullname", 4);
  }
}
